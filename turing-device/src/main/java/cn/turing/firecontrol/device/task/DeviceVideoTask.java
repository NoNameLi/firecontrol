package cn.turing.firecontrol.device.task;

import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.common.util.UploadUtil;
import cn.turing.firecontrol.device.biz.DeviceSensorBiz;
import cn.turing.firecontrol.device.biz.DeviceSensorTypeBiz;
import cn.turing.firecontrol.device.biz.DeviceVideoAbnormalDataBiz;
import cn.turing.firecontrol.device.biz.DeviceVideoExtBiz;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/02/20 15:24
 *
 * @Description 图片识别任务类
 * @Version V1.0
 */
@Component
@Slf4j
public class DeviceVideoTask {

    @Autowired
    private DeviceVideoExtBiz deviceVideoExtBiz;
    @Autowired
    private YingShiUtil yingShiUtil;
    @Autowired
    private FireRecognitionUtil fireRecognitionUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ESTransportUtil esTransportUtil;
    @Autowired
    private DeviceSensorBiz deviceSensorBiz;
    @Autowired
    private DeviceSensorTypeBiz deviceSensorTypeBiz;
    @Autowired
    private DeviceVideoAbnormalDataBiz deviceVideoAbnormalDataBiz;

    /**
     * 定时截图并进行火焰识别
     */
    @Scheduled(cron = "0/1 * * * * ?")
    @SchedulerLock(name = "deviceVideoFireRecognition", lockAtMostFor = 59000, lockAtLeastFor = 59000) //ShedLock分布式任务锁
    public void fireRecognition() {
        log.info("执行火焰识别任务");
        Constants.AnalysisSolution solution = Constants.AnalysisSolution.FIRE;
        //1、查询所有需要进行火焰识别的视频设备
        List<DeviceVideoExt> devices = deviceVideoExtBiz.getAllToAnalysisDevices(solution.getCode(), null);
        //2、遍历设备：截图、火焰识别，保存图片以及识别的结果，处理异常
        String alarmType = "火源";
        devices.forEach(deviceVideoExt -> {
            try {
                analisisFire(deviceVideoExt, solution, alarmType);
            } catch (Exception e) {
                log.error("设备:" + deviceVideoExt.getSensorNo() + "分析失败", e);
            }
        });
        log.info("完成火焰识别任务");
    }


    //对指定设备进行火焰分析
    private void analisisFire(DeviceVideoExt deviceVideoExt, Constants.AnalysisSolution solution, String alarmType) {
        Date time = new Date();
        //获取当前设备的最后一次分析结果
        String sensorNo = deviceVideoExt.getSensorNo();
        //从摄像头截取图片
        String accessToken = yingShiUtil.getaccessToken(redisTemplate);
        String[] sensorNoAndChannelNo = sensorNo.split(":");
        String realSensorNo = sensorNoAndChannelNo[0];
        String channelNo = sensorNoAndChannelNo.length >= 2 ? sensorNoAndChannelNo[1] : "1";
        String originImageUrl = yingShiUtil.getImageUrl(accessToken, realSensorNo, Integer.valueOf(channelNo));
        //测试异常图片
        /*String[] images = {"http://file.tmc.turing.ac.cn/video_202480707_1551089118009", //正常
                        "http://file.tmc.turing.ac.cn/video_202480707_1551088879009"}; //异常
                String originImageUrl = images[new Random().nextInt(images.length)];*/
        byte[] bytes = BaseUtil.urlToBytes(originImageUrl);
        String fileName = "video_" + deviceVideoExt.getSensorNo() + "_" + time.getTime() + ".jpg";
        Map<String, String> uploadRes = UploadUtil.simpleupload(bytes, fileName);
        String uploadImageUrl = null;
        if ("success".equals(uploadRes.get("result"))) {
            uploadImageUrl = uploadRes.get("url");
        }
        double accuracy = fireRecognitionUtil.analysisImage(bytes);
        Boolean isAlarm = fireRecognitionUtil.isAlarm(accuracy);
        //保存本次识别结果
        DeviceSensor sensor = deviceSensorBiz.getById(deviceVideoExt.getId());
        DeviceVideoAnalysisData analysisData = new DeviceVideoAnalysisData();
        analysisData.setSensorNo(deviceVideoExt.getSensorNo());
        analysisData.setAnalysisValue(accuracy);
        analysisData.setAnalysisPicture(uploadImageUrl);
        analysisData.setAnalysisResult(isAlarm ? 1 : 2);
        analysisData.setAnalysisTime(time);
        analysisData.setTenantId(sensor.getTenantId());
        analysisData.setId(UUIDUtils.generateUuid());
        esTransportUtil.addDocument(solution.getDataEsIndex(), solution.getCode(), analysisData.getId(), analysisData);
        // 如果本次识别异常：
        if (isAlarm) {
            handlerAlarm(sensor, time, alarmType, deviceVideoExt, uploadImageUrl, analysisData.getId(), Constants.AnalysisSolution.FIRE);
        } else {
            log.info("本次分析结果正常");
            sensor.setStatusTime(time);
            sensor.setStatus("2");
            sensor.setUpdTime(time);
            deviceSensorBiz.updateById(sensor);
            //设置设备告警信息
            String alarmMsg = "[{\"title\":\"告警状态\",\"value\":\"正常\",\"status\":2}]"; //status:0故障，1报警，2正常
            deviceVideoExt.setAlarmMsg(alarmMsg);
            deviceVideoExtBiz.updateOnlyExt(deviceVideoExt);
            //恢复所有异常
            deviceVideoAbnormalDataBiz.restoreAbnormal(sensorNo, Constants.AnalysisSolution.FIRE, null);
        }
    }


    /**
     * 处理异常
     *
     * @param sensor
     * @param time
     * @param alarmType
     * @param deviceVideoExt
     * @param uploadImageUrl
     * @param analysisDataId
     * @param solution
     */
    public void handlerAlarm(DeviceSensor sensor, Date time, String alarmType, DeviceVideoExt deviceVideoExt, String uploadImageUrl, String analysisDataId, Constants.AnalysisSolution solution) {
        log.info("本次分析结果异常");
        //增加异常记录
        DeviceSensorType sensorType = deviceSensorTypeBiz.getById(sensor.getSensorTypeId());
        DeviceVideoAbnormalData abnormalData = new DeviceVideoAbnormalData();
        abnormalData.setAlarmTime(time);
        abnormalData.setAlarmType(alarmType);
        abnormalData.setDeviceName(deviceVideoExt.getDeviceVideoName());
        abnormalData.setSensorNo(deviceVideoExt.getSensorNo());
        if (StringUtils.isNotBlank(uploadImageUrl)) {
            abnormalData.setPictures(Lists.newArrayList(uploadImageUrl));
        }
        if (StringUtils.isNotBlank(analysisDataId)) {
            abnormalData.setAnalysisDataIds(Lists.newArrayList(analysisDataId));
        }
        abnormalData.setTenantId(sensor.getTenantId());
        abnormalData.setUpdateTime(time);
        abnormalData.setAlarmCategory(1);
        abnormalData.setDeviceSerial(sensorType.getEquipmentType());
        deviceVideoAbnormalDataBiz.saveAbnormal(solution, abnormalData);
        //设置设备告警信息
        String alarmMsg = "[{\"title\":\"告警状态\",\"value\":\"烟火告警\",\"status\": 1}]"; //status:0故障，1报警，2正常
        deviceVideoExt.setAlarmMsg(alarmMsg);
        deviceVideoExtBiz.updateOnlyExt(deviceVideoExt);
        //修改设备状态
        sensor.setStatus("1");
        sensor.setStatusTime(time);
        sensor.setUpdTime(time);
        deviceSensorBiz.updateSelectiveById(sensor);
        //恢复离线异常
        deviceVideoAbnormalDataBiz.restoreAbnormal(sensor.getSensorNo(), Constants.AnalysisSolution.FIRE, "离线");
    }

}
