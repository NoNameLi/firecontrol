package cn.turing.firecontrol.server.business.impl;


import cn.turing.common.entity.DeviceInfo;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.server.business.BusinessI;
import cn.turing.firecontrol.server.entity.SensorDetail;
import cn.turing.firecontrol.server.entity.acrel.AcrelDeviceInfo;
import cn.turing.firecontrol.server.feign.IDevcieFeigndataHandler;
import cn.turing.firecontrol.server.feign.IDeviceFeign;
import cn.turing.firecontrol.server.feign.IUserFeign;
import cn.turing.firecontrol.server.util.SocketBaseUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Configuration
public class BusinessImpl implements BusinessI {
    private static final Logger log = LoggerFactory.getLogger(BusinessImpl.class);
    @Value("${socketIP}")
    private String socketIP;
    @Value("${socketPort}")
    private Integer socketPort;
    @Value("${socketPort1}")
    private Integer socketPort1;
    @Value("${timeout.common}")
    private long common;
    @Value("${timeout.yangan}")
    private long yanganX;
    @Value("${timeout.xiaofangshuan}")
    private long xiaofangshuan;
    @Value("${timeout.topSail}")
    private long topSail;
    @Value("${rabbit.fireDoor.exchange}")
    private String fireDoorExchange;
    @Value("${rabbit.fireDoor.routingKey}")
    private String fireDoorRoutingKey;
    @Value("${rabbit.hengYang.exchange}")
    private String hengYangExchange;
    @Value("${rabbit.hengYang.routingKey}")
    private String hengYangroutingKey;

    @Autowired
//    private TransportClient client;
    private RestHighLevelClient client;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    private IUserFeign iUserFeign;

    @Autowired
    private IDevcieFeigndataHandler iDevcieFeigndataHandler;
    @Autowired
    private SocketBaseUtil socketBaseUtil;

    @Autowired
    private IDeviceFeign iDeviceFeign;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public void sendAlarm(String uuid, DeviceInfo deviceInfo) {
        //if(deviceInfo!=null && deviceInfo.isHas_alarm() ){
        JSONObject jsonObject = deviceInfo.toAlarmJSON();
        if (jsonObject != null) {
            jsonObject.put("logid", uuid);
            this.sendSocketMessage(jsonObject.toJSONString());
        } else {
            log.info("无状态更新，不发送报警");
        }
        //}
    }

    @Override
    public void sendAlarm1(String uuid, JSONObject jsonObject) {
        //if(deviceInfo!=null && deviceInfo.isHas_alarm() ){
        if (jsonObject != null) {
            jsonObject.put("logid", uuid);
            this.sendSocketMessage1(jsonObject.toJSONString());
        } else {
            log.info("无状态更新，不发送报警");
        }
        //}
    }

    @Override
    public void sendFireDoorMessage(String uuid, DeviceInfo deviceInfo) {
        // {"type":"firedoor","data":"{}"}
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "firedoor");
        JSONObject object = new JSONObject(); //data
        JSONObject js = deviceInfo.toAlarmJSON(uuid);
        object.put("current", js);
        if (redisTemplate.hasKey(sensorflagfiredoor + deviceInfo.getDevice_id())) {
            JSONObject jsonObject1 = JSONObject.parseObject(redisTemplate.opsForValue().get(sensorflagfiredoor + deviceInfo.getDevice_id()));
            log.info("上一条数据:" + jsonObject1);
            object.put("last", jsonObject1);
        }
        jsonObject.put("data", object);
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(fireDoorExchange, fireDoorRoutingKey, jsonObject.toString().getBytes(), correlationId);
        log.info("推送成功:" + jsonObject.toString());
    }

    @Override
    public void sendHengYangMessage(String string) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.info(hengYangExchange + "+" + hengYangroutingKey);
        rabbitTemplate.convertAndSend(hengYangExchange, hengYangroutingKey, string.getBytes(), correlationId);
        log.info("推送衡阳分公司success...");
    }

    @Override
    public void sendSocketMessage(String json) {
        socketBaseUtil.client(socketIP, socketPort, json);
    }

    @Override
    public void sendSocketMessage1(String msg) {
        socketBaseUtil.client(socketIP, socketPort1, msg);
    }

    @Override
    public void updateFireDoorStatus(String sensorNo, Integer status) {
        iDeviceFeign.updateFireDoor(sensorNo, status);
    }

    @Override
    public void sendDeviceAlarmMQ(String uuid, DeviceInfo deviceInfo, String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        JSONObject V1 = deviceInfo.toAlarmJSON();
        V1.put("logId", uuid);
        jsonObject.put("data", V1);
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        System.out.println("====" + jsonObject.toString());
        rabbitTemplate.convertAndSend(fireDoorExchange, fireDoorRoutingKey, jsonObject.toString().getBytes(), correlationId);
    }

    @Override
    public void sendDeviceAlarmMQToAcrel(String uuid, AcrelDeviceInfo deviceInfo, String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        JSONObject V1 = deviceInfo.toAlarmJSON();
        V1.put("logId", uuid);
        jsonObject.put("data", V1);
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        System.out.println("====" + jsonObject.toString());
        rabbitTemplate.convertAndSend(fireDoorExchange, fireDoorRoutingKey, jsonObject.toString().getBytes(), correlationId);
    }

    @Override
    public void abnormalRestore(String sensorNo) {
        log.info("恢复异常设备：" + sensorNo);
        ObjectRestResponse res = iDevcieFeigndataHandler.abnormalRestore(sensorNo);
        System.out.println("res" + res + "s" + res.toString());
    }


    public static String sensorflagMain = "fireMain";

    @Override
    public JSONObject selectRedisFireMain(String ip, String port, String loopNo, String localtionNo) {
        String deviceId = ip + port + loopNo + localtionNo;
        JSONObject jsonObject1 = null;
        if (redisTemplate.hasKey(sensorflag + deviceId)) {
            jsonObject1 = JSONObject.parseObject(redisTemplate.opsForValue().get(sensorflag + deviceId));

        }
        return jsonObject1;
    }

    @Override
    public boolean sendMessageFireMain(JSONObject js, JSONObject jsonObject1) {
        //{"data":{"current":{"port":"1","ip":"127.0.0.1","alarm":{"alarmType":"报警","alarmStatus":1,"alarmValue":1},"logid":"f10c401951ca4b54a7f59fcd2fcce800","localtionNo":"7101","loopNo":"27","status":true,"uploadtime":"2019-11-29 16:54:01"},"last":{"port":"1","ip":"127.0.0.1","alarm":{"alarmType":"正常","alarmStatus":2,"alarmValue":2},"logid":"b368a3407343457eaaa65c1f18a76880","localtionNo":"7101","loopNo":"27","uploadtime":"2019-11-29 16:53:46","status":false}},"type":"firemain"}
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "firemain");
        JSONObject object = new JSONObject();
        object.put("last", js);
        object.put("current", jsonObject1);
        jsonObject.put("data", object);
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(fireDoorExchange, fireDoorRoutingKey, jsonObject.toString().getBytes(), correlationId);
        log.info("推送成功:" + jsonObject.toString());
        return false;
    }

    @Override
    public boolean updateFireMainInRedis(String ip, String port, String loopNo, String localtionNo, Boolean flag, int timeout, String uuid, SensorDetail sensorDetail) {
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObject.put("ip", ip);
        jsonObject.put("port", port);
        jsonObject.put("loopNo", loopNo);
        jsonObject.put("localtionNo", localtionNo);
        jsonObject.put("uploadtime", sdf.format(new Date()));
        jsonObject.put("status", flag);
        jsonObject.put("logid", uuid);
        JSONObject json = new JSONObject();
        json.put("alarmType", sensorDetail.getAlarmType());
        json.put("alarmStatus", sensorDetail.getAlarmStatus());
        json.put("alarmValue", sensorDetail.getAlarmValue());
        jsonObject.put("alarm", json);
        String deviceId = ip + port + loopNo + localtionNo;
        try {
            if (redisTemplate.hasKey(sensorflag + deviceId)) {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                JSONObject jsonObject1 = JSONObject.parseObject(redisTemplate.opsForValue().get(sensorflag + deviceId));
                System.out.println(sensorflag + deviceId + " " + jsonObject1);
                Date dateinredis = sdf1.parse(jsonObject1.getString("uploadtime"));

                if (dateinredis.getTime() <= new Date().getTime()) {

                    redisTemplate.opsForValue().set(sensorflag + deviceId, jsonObject.toJSONString(), timeout, TimeUnit.SECONDS);
                    log.info("expire:" + sensorflag + deviceId + ":" + timeout);

                }
            } else {
                redisTemplate.opsForValue().set(sensorflag + deviceId, jsonObject.toJSONString(), timeout, TimeUnit.SECONDS);

                log.info("expire:" + sensorflag + deviceId + ":" + timeout);

            }
        } catch (Exception e) {
            log.error("发到redis失败", e);
            return false;
        }
        return true;
    }


    @Override
    public JSONObject selectMqttSensoralarmValue(String deviceId) {
        JSONObject jsonObject = null;//loraMapper.selectMqttSensoralarmValue(deviceId);
        if (jsonObject == null) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("temp_alarm", 45);
            jsonObject1.put("vol_alarm", 500);
            jsonObject1.put("sensorId", deviceId);
            return jsonObject1;
        } else {
            return jsonObject;
        }

    }


    public static String sensorflag = "sensor:";
    public static String sensorflagfiredoor = "sensor:firedoor:";//防火门标识
    public static String sensorflagXs = "monitor:";


    @Override
    public boolean updateInRedis(DeviceInfo deviceInfo, int timeout_second) {
        //可能会有脏数据，但是概率相当低。
        try {
            if (redisTemplate.hasKey(sensorflag + deviceInfo.getDevice_id())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForValue().get(sensorflag + deviceInfo.getDevice_id()));
                System.out.println(sensorflag + deviceInfo.getDevice_id() + " " + jsonObject);
                Date dateinredis = sdf.parse(jsonObject.getString("uploadtime"));

                if (dateinredis.getTime() <= deviceInfo.getUpload_time().getTime()) {

                    redisTemplate.opsForValue().set(sensorflag + deviceInfo.getDevice_id(), deviceInfo.toBaseJSON().toJSONString(), timeout_second, TimeUnit.SECONDS);
                    //jedisCluster.get(sensorflag+deviceInfo.getDevice_id());
                    //jedisCluster.expire(sensorflag+deviceInfo.getDevice_id(), );
                    log.info("expire:" + sensorflag + deviceInfo.getDevice_id() + ":" + timeout_second);
                    //jedisCluster.publish(Constant.RedisConstant.CHANNEL, sensorflag+deviceInfo.getDevice_id());
                }
            } else {
                redisTemplate.opsForValue().set(sensorflag + deviceInfo.getDevice_id(), deviceInfo.toBaseJSON().toJSONString(), timeout_second, TimeUnit.SECONDS);
                //jedisCluster.expire(sensorflag+deviceInfo.getDevice_id(), );
                log.info("expire:" + sensorflag + deviceInfo.getDevice_id() + ":" + timeout_second);
                //jedisCluster.publish(Constant.RedisConstant.CHANNEL, sensorflag+deviceInfo.getDevice_id());
            }
        } catch (Exception e) {
            log.error("发到redis失败", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean updateInRedisToAcrel(AcrelDeviceInfo deviceInfo, int timeout_second) {
        //可能会有脏数据，但是概率相当低。
        try {
            if (redisTemplate.hasKey(sensorflag + deviceInfo.getDevice_id())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForValue().get(sensorflag + deviceInfo.getDevice_id()));
                System.out.println(sensorflag + deviceInfo.getDevice_id() + " " + jsonObject);
                Date dateinredis = sdf.parse(jsonObject.getString("uploadtime"));

                if (dateinredis.getTime() <= deviceInfo.getUpload_time().getTime()) {

                    redisTemplate.opsForValue().set(sensorflag + deviceInfo.getDevice_id(), deviceInfo.toBaseJSON().toJSONString(), timeout_second, TimeUnit.SECONDS);
                    //jedisCluster.get(sensorflag+deviceInfo.getDevice_id());
                    //jedisCluster.expire(sensorflag+deviceInfo.getDevice_id(), );
                    log.info("expire:" + sensorflag + deviceInfo.getDevice_id() + ":" + timeout_second);
                    //jedisCluster.publish(Constant.RedisConstant.CHANNEL, sensorflag+deviceInfo.getDevice_id());
                }
            } else {
                redisTemplate.opsForValue().set(sensorflag + deviceInfo.getDevice_id(), deviceInfo.toBaseJSON().toJSONString(), timeout_second, TimeUnit.SECONDS);
                //jedisCluster.expire(sensorflag+deviceInfo.getDevice_id(), );
                log.info("expire:" + sensorflag + deviceInfo.getDevice_id() + ":" + timeout_second);
                //jedisCluster.publish(Constant.RedisConstant.CHANNEL, sensorflag+deviceInfo.getDevice_id());
            }
        } catch (Exception e) {
            log.error("发到redis失败", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean updateInRedisByFireDoor(DeviceInfo deviceInfo, int timeout_second, String uuid) {
        //可能会有脏数据，但是概率相当低。
        try {
            if (redisTemplate.hasKey(sensorflagfiredoor + deviceInfo.getDevice_id())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForValue().get(sensorflagfiredoor + deviceInfo.getDevice_id()));
                System.out.println(sensorflag + deviceInfo.getDevice_id() + " " + jsonObject);
                Date dateinredis = sdf.parse(jsonObject.getString("uploadtime"));

                if (dateinredis.getTime() <= deviceInfo.getUpload_time().getTime()) {

                    redisTemplate.opsForValue().set(sensorflagfiredoor + deviceInfo.getDevice_id(), deviceInfo.toAlarmJSON(uuid).toJSONString(), timeout_second, TimeUnit.SECONDS);
                    //jedisCluster.get(sensorflag+deviceInfo.getDevice_id());
                    //jedisCluster.expire(sensorflag+deviceInfo.getDevice_id(), );
                    log.info("expire:" + sensorflagfiredoor + deviceInfo.getDevice_id() + ":" + timeout_second);
                    //jedisCluster.publish(Constant.RedisConstant.CHANNEL, sensorflag+deviceInfo.getDevice_id());
                }
            } else {
                redisTemplate.opsForValue().set(sensorflagfiredoor + deviceInfo.getDevice_id(), deviceInfo.toAlarmJSON(uuid).toJSONString(), timeout_second, TimeUnit.SECONDS);
                //jedisCluster.expire(sensorflag+deviceInfo.getDevice_id(), );
                log.info("expire:" + sensorflagfiredoor + deviceInfo.getDevice_id() + ":" + timeout_second);
                //jedisCluster.publish(Constant.RedisConstant.CHANNEL, sensorflag+deviceInfo.getDevice_id());
            }
        } catch (Exception e) {
            log.error("发到redis失败", e);
            return false;
        }
        return true;
    }


    /**
     * 存服务处理设备数据时间到redis中
     *
     * @param string
     * @return
     */
    @Override
    public boolean updateInRedisTwo(String string) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            redisTemplate.opsForValue().set(sensorflagXs + string, sdf.format(new Date()));
        } catch (Exception e) {
            log.error("发到redis失败", e);
            return false;
        }
        return true;
    }


    public boolean updateInRedisDianQiHuoZai() {
        boolean flag = true;
        long date = new Date().getTime();
        System.out.println("date:" + date);
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //电气火灾
            if (redisTemplate.hasKey(sensorflagXs + "dianqihuozai")) {
                String dqhz = redisTemplate.opsForValue().get(sensorflagXs + "dianqihuozai");
                Date dt1 = df.parse(dqhz);
                if (date - dt1.getTime() > common) {
                    flag = false;
                } else {
                    flag = true;
                }
            } else {
                //throw new Exception("电气火灾不存在");
                log.error("电气火灾不存在redis中");
            }
        } catch (Exception e) {
            log.error("获取redis数据失败");
        }

        return flag;
    }

    public boolean updateInRedisYanGan() {
        boolean flag1 = true;
        long date = new Date().getTime();
        System.out.println("date:" + date);
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //烟感
            if (redisTemplate.hasKey(sensorflagXs + "yangan")) {
                String yangan = redisTemplate.opsForValue().get(sensorflagXs + "yangan");
                Date dt2 = df.parse(yangan);
                if (date - dt2.getTime() > yanganX) {
                    flag1 = false;
                } else {
                    flag1 = true;
                }
            } else {
                log.error("烟感不存在redis中");
            }
        } catch (Exception e) {
            log.info("获取redis数据失败");
        }

        return flag1;
    }

    public boolean updateInRedisXiaoHuoShuan() {

        boolean flag2 = true;
        long date = new Date().getTime();
        System.out.println("date:" + date);
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //智能消防栓
            if (redisTemplate.hasKey(sensorflagXs + "turingXiaoFangShuan")) {
                String turingXiaoFangShuan = redisTemplate.opsForValue().get(sensorflagXs + "turingXiaoFangShuan");
                System.out.println("turingXiaoFangShuan:" + turingXiaoFangShuan);
                Date dt3 = df.parse(turingXiaoFangShuan);
                System.out.println("时间：" + dt3.getTime());
                System.out.println("xiaofangshuan规定：" + xiaofangshuan);
                if (date - dt3.getTime() > xiaofangshuan) {
                    flag2 = false;
                } else {
                    flag2 = true;
                }
            } else {
                log.error("智能消防栓不存在redis中");
            }
        } catch (Exception e) {
            log.info("获取redis数据失败");
        }
        return flag2;
    }

    /**
     * tcp心跳检测
     *
     * @return
     */
    @Override
    public boolean updateInRedisTcp() {
        boolean flag3 = false;
        long date = new Date().getTime();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //消火栓压力
            if (redisTemplate.hasKey(sensorflagXs + "YLXiaoFangShuan")) {
                String YLXiaoFangShuan = redisTemplate.opsForValue().get(sensorflagXs + "YLXiaoFangShuan");
                Date dt4 = df.parse(YLXiaoFangShuan);
                if (date - dt4.getTime() > topSail) {
                    flag3 = false;
                } else {
                    flag3 = true;
                }
                log.info("已经比较了");
            } else {
                log.error("消火栓压力不存在redis中");
            }
        } catch (Exception e) {
            log.error("获取redis数据失败");
        }
        return flag3;
    }

    /**
     * 原始数据存入ES
     *
     * @param map
     */
    @Override
    public void insertData(String index, String type, String id, Map<String, Object> map) {
        log.info("增加原始数据到ES开始");
        try {
            IndexRequest indexRequest = new IndexRequest(index);
            indexRequest.id(id)/*.type(type)*/.source(map);
            log.info("数据内容:" + id + ":" + JSON.toJSONString(map));
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            log.info("返回的状态码：" + response.status().getStatus());
        } catch (Exception e) {
            log.error("增加原始数据到ES出错");
            e.printStackTrace();
            log.error(e.toString());
        }
        log.info("增加原始数据到ES结束");
    }

    @Override
    public void updateStatus(String deviceID, DeviceInfo deviceInfo, String status) {
        //     log.info("更新状态");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.info("deviceID:" + deviceID + "deviceInfo:" + deviceInfo.getUpload_time() + "status:" + status);
            iDeviceFeign.updateStatus(deviceID, sdf.format(deviceInfo.getUpload_time()), status);
        } catch (Exception e) {
            log.error("更新状态出错");
            e.printStackTrace();
        }
    }

    @Override
    public void updateStatusToAcrel(String deviceID, AcrelDeviceInfo deviceInfo, String status) {
        //    log.info("更新状态");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.info("deviceID:" + deviceID + "deviceInfo:" + deviceInfo.getUpload_time() + "status:" + status);
            iDeviceFeign.updateStatus(deviceID, sdf.format(deviceInfo.getUpload_time()), status);
        } catch (Exception e) {
            log.error("更新状态出错");
            e.printStackTrace();
        }
    }

    @Override
    public void updateStatus(String ip, String port, String loopNo, String localtionNo, String status) {
        //     log.info("更新状态");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            iDeviceFeign.updateSensorStatus(ip, port, loopNo, localtionNo, status, sdf.format(new Date()));
        } catch (Exception e) {
            log.error("\"更新状态出错:?>>>>\"" + e.getMessage());
        }
    }
}
