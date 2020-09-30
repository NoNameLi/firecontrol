package cn.turing.firecontrol.device.task;

import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.device.biz.DeviceSensorBiz;
import cn.turing.firecontrol.device.biz.DeviceSensorTypeBiz;
import cn.turing.firecontrol.device.biz.DeviceVideoAbnormalDataBiz;
import cn.turing.firecontrol.device.biz.DeviceVideoExtBiz;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import cn.turing.firecontrol.device.entity.DeviceSensorType;
import cn.turing.firecontrol.device.entity.DeviceVideoAbnormalData;
import cn.turing.firecontrol.device.entity.DeviceVideoExt;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.ESTransportUtil;
import cn.turing.firecontrol.device.util.YingShiUtil;
import cn.turing.firecontrol.device.vo.DeviceSensorVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/05/05 10:40
 *
 * @Description 判断视频设备是否离线定时任务
 * @Version V1.7
 */
@Component
@Slf4j
public class DeviceVideoOfflineTask {

    @Autowired
    private YingShiUtil yingShiUtil;
    @Autowired
    private DeviceSensorBiz deviceSensorBiz;
    @Autowired
    private DeviceSensorTypeBiz deviceSensorTypeBiz;
    @Autowired
    private DeviceVideoExtBiz deviceVideoExtBiz;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private DeviceVideoAbnormalDataBiz deviceVideoAbnormalDataBiz;
    @Autowired
    private ESTransportUtil esTransportUtil;

    /**
     * 定时截图并进行火焰识别
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    @SchedulerLock(name = "deviceVideoOfflineTask", lockAtMostFor = 599000, lockAtLeastFor = 599000) //ShedLock分布式任务锁
    public void getDeviceInfo(){
        log.info("开始视频设备离线判断任务");
        Date time = new Date();
        List<String> offlines = Lists.newArrayList();
        List<String> onlines = Lists.newArrayList();
        //1，调用萤石接口"获取摄像头列表"
        String accessToken = yingShiUtil.getaccessToken(redisTemplate);
        List<JSONObject> list =  yingShiUtil.getCameraList(accessToken);
        String status = "", sensorNo = "";
        for(JSONObject json : list){
            status = json.getString("status");
            sensorNo = json.getString("deviceSerial") + ":" + json.getString("channelNo");
            //1在线，0不在线
            if("1".equals(status)){
                onlines.add(sensorNo);
            }else {
                offlines.add(sensorNo);
            }
        }
        //2，修改设备离线状态
        if(!offlines.isEmpty()){
            deviceSensorBiz.updateOfflineStatus(offlines,true);
        }
        if(!onlines.isEmpty()){
            deviceSensorBiz.updateOfflineStatus(onlines,false);
        }
        //3，恢复离线异常
        String alarmType = "离线";
        Constants.AnalysisSolution solution = Constants.AnalysisSolution.FIRE;
        List<JSONObject> abnormalDataList = null;
        DeviceVideoAbnormalData abnormal = null;
        DeviceVideoExt ext = null;
        String alarmMsg = null;
        for(String s : onlines){
            DeviceSensor sensor = deviceSensorBiz.selectBySensorNo(s);
            if(sensor!=null && !sensor.getStatus().equals("1")){
                deviceVideoAbnormalDataBiz.restoreAbnormal(s, Constants.AnalysisSolution.FIRE,alarmType);
                alarmMsg = "[{\"title\":\"告警状态\",\"value\":\"正常\",\"status\": 2}]"; //status:0故障，1报警，2正常
                ext = new DeviceVideoExt();
                ext.setSensorNo(s);
                ext.setAlarmMsg(alarmMsg);
                deviceVideoExtBiz.updateOnlyExtSelectiveBySensorNo(ext);
            }
        }
        //4，添加离线异常记录并推送报警通知
        DeviceVideoAbnormalData abnormalData = null;
        DeviceSensorVo sensorVo = null;
        DeviceSensor sensor = null;
        for(String s : offlines){
            sensor = deviceSensorBiz.selectBySensorNo(s);
            if(sensor == null){
                continue;
            }
            sensorVo =  deviceVideoExtBiz.getById(sensor.getId());
            abnormalData = new DeviceVideoAbnormalData();
            abnormalData.setAlarmTime(time);
            abnormalData.setAlarmType("离线");
            abnormalData.setDeviceName(sensorVo.getDeviceName());
            abnormalData.setSensorNo(s);
            abnormalData.setPictures(Lists.newArrayList());
            abnormalData.setAnalysisDataIds(Lists.newArrayList());
            abnormalData.setTenantId(sensor.getTenantId());
            abnormalData.setUpdateTime(time);
            abnormalData.setAlarmCategory(0);
            abnormalData.setDeviceSerial(sensorVo.getEquipmentType());
            deviceVideoAbnormalDataBiz.saveAbnormal(Constants.AnalysisSolution.FIRE,abnormalData);
            alarmMsg = "[{\"title\":\"告警状态\",\"value\":\"离线\",\"status\": 0}]"; //status:0故障，1报警，2正常
            ext = new DeviceVideoExt();
            ext.setSensorNo(s);
            ext.setAlarmMsg(alarmMsg);
            deviceVideoExtBiz.updateOnlyExtSelectiveBySensorNo(ext);
        }
        log.info("结束视频设备离线判断任务");

    }






}
