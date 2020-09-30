package cn.turing.firecontrol.device.listener;

import cn.turing.firecontrol.device.biz.DeviceSensorBiz;
import cn.turing.firecontrol.device.biz.DeviceVideoAbnormalDataBiz;
import cn.turing.firecontrol.device.biz.DeviceVideoExtBiz;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import cn.turing.firecontrol.device.entity.DeviceVideoExt;
import cn.turing.firecontrol.device.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.nio.charset.StandardCharsets;
import java.util.Date;
public class KeyExpiredListener extends KeyExpirationEventMessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyExpiredListener.class);

    @Autowired
    private DeviceSensorBiz deviceSensorBiz;
    @Autowired
    private DeviceVideoAbnormalDataBiz deviceVideoAbnormalDataBiz;
    @Autowired
    private DeviceVideoExtBiz deviceVideoExtBiz;

    public KeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        //过期的key
        String key = new String(message.getBody(), StandardCharsets.UTF_8);
        LOGGER.info("监听中，key:{}",key);
        if(key.contains("device:abnormal:fire")){
            String sensorNo = new StringBuilder(key.substring(key.lastIndexOf("_")+1,key.length())).toString();
            DeviceSensor sensor = deviceSensorBiz.selectBySensorNo(sensorNo);
            sensor.setStatusTime(new Date());
            sensor.setStatus("2");
            sensor.setUpdTime(new Date());
            deviceSensorBiz.updateById(sensor);
            String alarmMsg = "[{\"title\":\"告警状态\",\"value\":\"正常\",\"status\": 2}]"; //status:0故障，1报警，2正常
            DeviceVideoExt ext = new DeviceVideoExt();
            ext.setId(sensor.getId());
            ext.setSensorNo(sensorNo);
            ext.setAlarmMsg(alarmMsg);
            deviceVideoExtBiz.updateOnlyExt(ext);
            //恢复所有异常
            deviceVideoAbnormalDataBiz.restoreAbnormal(sensorNo.toString(), Constants.AnalysisSolution.FIRE,null);
            LOGGER.info("======火警恢复正常===");
            LOGGER.info("redis key 过期：pattern={},channel={},key={}", new String(pattern), channel, key);

        }

    }


}

