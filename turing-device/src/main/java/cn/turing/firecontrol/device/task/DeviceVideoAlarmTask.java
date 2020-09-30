package cn.turing.firecontrol.device.task;

import cn.turing.firecontrol.common.util.UploadUtil;
import cn.turing.firecontrol.device.biz.DeviceSensorBiz;
import cn.turing.firecontrol.device.biz.DeviceSensorTypeBiz;
import cn.turing.firecontrol.device.biz.DeviceVideoAbnormalDataBiz;
import cn.turing.firecontrol.device.biz.DeviceVideoExtBiz;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import cn.turing.firecontrol.device.entity.DeviceSensorType;
import cn.turing.firecontrol.device.entity.DeviceVideoAbnormalData;
import cn.turing.firecontrol.device.entity.DeviceVideoExt;
import cn.turing.firecontrol.device.util.BaseUtil;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.YingShiUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2019/04/16 15:03
 *
 * @Description 获取设备报警信息任务
 * @Version V1.0
 */
//@Component
public class DeviceVideoAlarmTask {

    @Autowired
    private YingShiUtil yingShiUtil;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private DeviceSensorBiz deviceSensorBiz;
    @Autowired
    private DeviceVideoExtBiz deviceVideoExtBiz;
    @Autowired
    private DeviceSensorTypeBiz deviceSensorTypeBiz;
    @Autowired
    private DeviceVideoAbnormalDataBiz deviceVideoAbnormalDataBiz;
    //开始时间
    private final static String REDIS_START_TIME = "device:deviceVideoAlarmTask:startTime";
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public void getStartTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = redisTemplate.opsForValue().get(REDIS_START_TIME);
        if(StringUtils.isBlank(start)){
            Date startTime = new Date(System.currentTimeMillis() - 60*1000L);
            redisTemplate.opsForValue().set(REDIS_START_TIME,dateFormat.format(startTime),2, TimeUnit.MINUTES);
        }
    }

    /**
     * 定时获取
     */
    @Scheduled(cron = "0/1 * * * * ?")
    @SchedulerLock(name = "deviceVideoAlarmTask", lockAtMostFor = 59000, lockAtLeastFor = 59000) //ShedLock分布式任务锁
    public void getAlarmMsg() throws ParseException {
        String accessToken = yingShiUtil.getaccessToken(redisTemplate);
        Date startTime = dateFormat.parse(redisTemplate.opsForValue().get(REDIS_START_TIME));
        Date endTime = new Date();
        //查询近期异常信息
        List<JSONObject> jsonList = yingShiUtil.getAllAlarmList(accessToken,startTime,endTime,null,null);
        JSONObject jo = null;
        DeviceVideoAbnormalData abnormalData = null;
        String sensorNo = null;
        DeviceSensor sensor = null;
        DeviceVideoExt deviceVideoExt = null;
        List<DeviceVideoExt> deviceVideoExtList = null;
        DeviceSensorType sensorType = null;
        List<DeviceVideoAbnormalData> abnormalDataList = new ArrayList<>();
        Date time = new Date();
        for(int i=0; i< jsonList.size(); i++){
            jo = jsonList.get(i);
            sensorNo = jo.getString("deviceSerial") + ":" + jo.getString("channelNo");
            sensor = deviceSensorBiz.selectBySensorNo(sensorNo);
            if(sensor == null){
                throw new RuntimeException("设备不存在：" + sensorNo);
            }
            abnormalData = new DeviceVideoAbnormalData();
            abnormalData.setUpdateTime(time);
            abnormalData.setAlarmCategory(0);
            abnormalData.setSensorNo(sensorNo);
            abnormalData.setTenantId(sensor.getTenantId());
            abnormalData.setAnalysisDataIds(Lists.newArrayList(jo.getString("alarmId")));
            deviceVideoExt = new DeviceVideoExt();
            deviceVideoExt.setId(sensor.getId());
            deviceVideoExtList = deviceVideoExtBiz.queryOnlyExt(deviceVideoExt);
            abnormalData.setDeviceName(deviceVideoExtList.get(0).getDeviceVideoName());
            String picUrl = jo.getString("alarmPicUrl");
            //上传图片到七牛云
            String fileName = "video_" + deviceVideoExt.getSensorNo() + "_" + time.getTime() + ".jpg";
            Map<String,String> uploadRes =  UploadUtil.simpleupload(BaseUtil.urlToBytes(picUrl),fileName);
            String uploadImageUrl = null;
            if("success".equals(uploadRes.get("result"))){
                uploadImageUrl = uploadRes.get("url");
            }
            abnormalData.setPictures(Lists.newArrayList(uploadImageUrl));
            abnormalData.setAlarmTime(time);
            sensorType = deviceSensorTypeBiz.getById(sensor.getSensorTypeId());
            abnormalData.setDeviceSerial(sensorType.getEquipmentType());
            abnormalData.setAlarmType(jo.getString("alarmType"));
            deviceVideoAbnormalDataBiz.saveAbnormal(Constants.AnalysisSolution.FIRE,abnormalData);
            //修改设备状态
            sensor.setStatusTime(time);
            sensor.setStatus("0");
            sensor.setUpdTime(time);
            deviceSensorBiz.updateById(sensor);
            //设置设备告警信息
            String alarmMsg = "[{\"title\":\"告警状态\",\"value\":\"" + abnormalData.getAlarmType() +"\",\"status\":0}]"; //status:0故障，1报警，2正常
            deviceVideoExt.setAlarmMsg(alarmMsg);
            deviceVideoExtBiz.updateOnlyExt(deviceVideoExt);
        }

        //更新开始时间
        redisTemplate.opsForValue().set(REDIS_START_TIME,dateFormat.format(endTime),2, TimeUnit.MINUTES);


    }

}
