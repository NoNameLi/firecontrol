package cn.turing.firecontrol.datahandler.listener;/**
 * Created by hanyong on 2018/09/12 14:56
 */

import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.datahandler.base.Constant;
import cn.turing.firecontrol.datahandler.business.BusinessI;
import cn.turing.firecontrol.datahandler.entity.SensorAbnormal;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by hanyong on 2018/09/12 14:56
 *
 * @Description TODO
 * @Version V1.0
 */
@Slf4j
@Component
public class ExpireMessageListener extends KeyExpirationEventMessageListener {
    @Autowired
    private BusinessI businessI;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;


    @Value("${tmc.config.rabbitmq.abnormal.msg.exchange}")
    private String abnormalExchange;
    @Value("${tmc.config.rabbitmq.abnormal.msg.routingKey}")
    private String abnormalRoutingKey;

    @Value("${spring.cloud.client.ipAddress}")
    private String ipAddress;
    private final String REDIS_LOCK_EXPIRE_PREFIX = "lock:expire:sensor:";
    private final static String SENSOR_FIREDOOR_PREFIX = "sensor:firedoor:";

    public ExpireMessageListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        //抢任务
        String key = new String(message.getBody());
        //不处理
        if(!key.startsWith("sensor:")){
            return;
        }
        log.info("REDIS超期KEY:{}",key);
        String redisKey = REDIS_LOCK_EXPIRE_PREFIX + key;
        String redisValue1 = ipAddress + "_" + System.currentTimeMillis();
        log.info("争抢资源:{}:{}",redisKey,redisValue1);
        redisTemplate.opsForValue().set(redisKey,redisValue1,3,TimeUnit.MINUTES);
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String redisValue2 = redisTemplate.opsForValue().get(redisKey);
        log.info("value1:{},value2:{}",redisValue1,redisValue2);
        //如果没有抢到任务
        if(!redisValue1.equals(redisValue2)){
            return;
        }
        if(key.startsWith(SENSOR_FIREDOOR_PREFIX)){
            handleFiredoor(key);
        }else{
            handleOld(key);
        }

    }


    /**
     * 处理防火门离线消息
     * @param key
     */
    private void handleFiredoor(String key){
        String sensorNo = key.replace(SENSOR_FIREDOOR_PREFIX,"");
        JSONObject current = SensorAbnormal.createOfflineData(sensorNo);
        JSONObject data = new JSONObject();
        data.put("current",current);
        JSONObject msg = new JSONObject();
        msg.put("type","firedoor");
        msg.put("data",data);
        amqpTemplate.convertAndSend(abnormalExchange,abnormalRoutingKey,msg.toJSONString());
    }

    //原始离线处理程序
    private void handleOld(String key){
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String time = sdf.format(new Date());
            String deviceid = key.substring(7);
            jsonObject.put("deviceid",deviceid);
            jsonObject.put("uploadtime",time);
            jsonObject.put("recievetime",time);
            jsonObject.put("status", Constant.ST_OFFLINE);
            jsonObject.put("logid","N_"+ UUIDUtils.generateUuid());
            JSONArray alarms = new JSONArray();
            JSONObject alarmJson = new JSONObject();
            alarmJson.put("alarmType","离线");
            alarmJson.put("alarmStatus",Constant.ST_OFFLINE);
            alarmJson.put("alarmValue",0);
            alarmJson.put("alarmCode", Constant.OFFLINE);
            alarms.add(alarmJson);
            jsonObject.put("alarms",alarms);
            log.info("发送离线报警：{}",deviceid);
            JSONObject msg = new JSONObject();
            msg.put("type","default");
            msg.put("data",jsonObject);
            amqpTemplate.convertAndSend(abnormalExchange,abnormalRoutingKey,msg.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("处理离线失败："+jsonObject.toJSONString(),e);
        }
    }


}
