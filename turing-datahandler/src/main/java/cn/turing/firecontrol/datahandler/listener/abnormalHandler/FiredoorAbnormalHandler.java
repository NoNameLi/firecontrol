package cn.turing.firecontrol.datahandler.listener.abnormalHandler;

import cn.turing.firecontrol.common.util.AliSmsUtil;
import cn.turing.firecontrol.common.util.AliVmsSent;
import cn.turing.firecontrol.common.util.FeiGeSmsUtil;
import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.datahandler.biz.*;
import cn.turing.firecontrol.datahandler.business.BusinessImpl;
import cn.turing.firecontrol.datahandler.business.JpushServiceImpl;
import cn.turing.firecontrol.datahandler.entity.*;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import cn.turing.firecontrol.datahandler.util.ESTransportUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created on 2019/03/07 16:32
 *
 * @Description TODO
 * @Version V1.0
 */
@Component
@Slf4j
@Data
public class FiredoorAbnormalHandler extends AbstractAbnormalHandler {

    @Autowired
    private BusinessImpl business;
    @Autowired
    private ESTransportUtil esTransportUtil;
    @Value("${tmc.config.elasicSearch.abnormal.index}")
    private String ABNORMAL_INDEX;
    @Value("${tmc.config.elasicSearch.abnormal.type.firedoor}")
    private String ABNORMAL_TYPE_FIREDOOR;
    @Autowired
    private IDeviceFeign iDeviceFeign;

    @Value("${tmc.config.rabbitmq.abnormal.websocket.exchange}")
    private String wsExchange;
    @Value("${tmc.config.rabbitmq.abnormal.websocket.routingKey}")
    private String wsRoutingKey;
    @Value("${sms.feige.alarmTemplateId}")
    private String feiGeTemplateId;
    @Value("${vms.aliyun.templateNo}")
    private String aliyunTemplateNo;
    private String firedoorOfflineRedisKey = "offline:firedoor";

    @Value("${sms.ailiyun.fireTemplateNo}")
    private String aliyunSmsTemplateNo;


    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private JpushServiceImpl jpushService;
    @Autowired
    private AliVmsSent aliVmsSent;
    @Autowired
    private FeiGeSmsUtil feiGeSmsUtil;
    @Autowired
    private AliSmsUtil aliSmsUtil;
    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private SensorAbnormalBiz sensorAbnormalBiz;

    @Autowired
    private NoticeLogBiz noticeLogBiz;

    @Autowired
    private NoticeRuleUserBiz noticeRuleUserBiz;

    @Autowired
    private NoticeRuleBiz noticeRuleBiz;

    @Autowired
    private NoticeRuleSensorBiz noticeRuleSensorBiz;


    /*
    数据示例：
    status: 0故障，1报警，2正常
    {
        "last":{
            "deviceid": "123",
            "uploadtime": "yyyy-MM-dd HH:mm:ss",
            "recievetime": "yyyy-MM-dd HH:mm:ss",
            "logId":"",
            "alarms": [
                {
                    "alarmCode": "DM",
                    "alarmType": "正常",
                    "alarmStatus": 2,
                    "alarmValue": 2
                },
                {
                    "alarmCode": "VAT",
                    "alarmType": "正常",
                    "alarmStatus": 2,
                    "alarmValue": 2
                }
             ],
            "status": "2"
        },
        "current": {
            "deviceid": "123",
            "uploadtime": "yyyy-MM-dd HH:mm:ss",
            "recievetime": "yyyy-MM-dd HH:mm:ss",
            "logId":"",
            "alarms": [
                {
                    "alarmCode": "DM",
                    "alarmType": "正常",
                    "alarmStatus": 2,
                    "alarmValue": 2
                },
                {
                    "alarmCode": "VAT",
                    "alarmType": "正常",
                    "alarmStatus": 2,
                    "alarmValue": 2
                }
             ],
            "status": "2"
        }
    }
    */
    /**
     *
     * 1、判断数据异常情况
     *    本次正常：
     *          上次异常：修改该设备最后一条异常记录的恢复时间
     *          上次正常：不做任何处理
     *    本次异常：
     *          上次异常：不做任务处理
     *          上次正常：增加异常记录，并发送异常通知
     */
    @Override
    public void handleAbnormal(String data) throws ParseException {
        log.info("接收到防火文异常数据:{}",data);
        JSONObject jsonData = null;
        try{
            jsonData = JSONObject.parseObject(data);
        }catch (Exception e){
            log.error("所接收的数据不是JSON格式",e);
            return;
        }
        JSONObject current = jsonData.getJSONObject("current");
        List<Alarm> currentAlarms = current.getJSONArray("alarms").toJavaList(Alarm.class);
        int currentStatus = current.getInteger("status");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = dateFormat.parse(current.getString("uploadtime"));

        String sensorNo = current.getString("deviceid");
        JSONObject sensorObject = business.getSensor(sensorNo).getJSONObject("data");
        if(sensorObject == null){
            log.error("设备编号{}不存在",sensorNo);
            throw new RuntimeException(String.format("设备编号%s不存在",sensorNo));
        }
        DeviceSensor sensor = sensorObject.toJavaObject(DeviceSensor.class);
        DeviceSensorType sensorType = business.getSensorType(sensor.getSensorTypeId()).getJSONObject("data").toJavaObject(DeviceSensorType.class);
        DeviceBuilding building = iDeviceFeign.getBuilding(sensor.getBuildingId()).getJSONObject("data").toJavaObject(DeviceBuilding.class);


        JSONObject last = jsonData.getJSONObject("last");
        if(last == null){
            Boolean isOffline = redisTemplate.opsForSet().isMember(firedoorOfflineRedisKey,sensorNo);
            if(isOffline){
                redisTemplate.opsForSet().remove(firedoorOfflineRedisKey,sensorNo);
                last = SensorAbnormal.createOfflineData(sensorNo);
                List<SensorAbnormal> offLines = sensorAbnormalBiz.getNotRestores(sensorNo,"OFFLINE");
                for(SensorAbnormal s : offLines){
                    s.setRestoreTime(new Date());
                    esTransportUtil.updateDocument(ABNORMAL_INDEX,ABNORMAL_TYPE_FIREDOOR,s.getId(),s);
                }
            }
        }
        //上次没有数据，或者上次数据没有异常
        if(last == null || last.getInteger("status") == 2){
            //本次也无异常
            if (currentStatus == 2){
                return;
            }
            business.updataSensor(sensorNo,dateFormat.format(currentTime),currentStatus + "");
            //遍历异常的测点，保存异常记录
            for(Alarm alarm : currentAlarms){
                saveSensorAbnormal(current.getString("logId"),sensor,sensorType,building.getBName(),currentTime,alarm);
            }
            return;
        }
        int lastStatus = last.getInteger("status");
        if(lastStatus != currentStatus){
            //修改设备状态
            business.updataSensor(sensorNo,dateFormat.format(currentTime),currentStatus + "");
        }
        List<SensorAbnormal> abnormals = sensorAbnormalBiz.getNotRestores(sensorNo);
        if(currentStatus == 2) {
            //上次的异常全部设置恢复时间
            for(SensorAbnormal s : abnormals){
                s.setRestoreTime(currentTime);
                esTransportUtil.updateDocument(ABNORMAL_INDEX,ABNORMAL_TYPE_FIREDOOR,s.getId(),s);
            }
            return;
        }
        List<Alarm> lastPointList = last.getJSONArray("alarms").toJavaList(Alarm.class);
        //对新增的异常测点增加异常记录
        currentAlarms.removeAll(lastPointList);
        if(!currentAlarms.isEmpty()){
            for(Alarm alarm: currentAlarms){
                if(!alarm.isNormal()){
                    saveSensorAbnormal(current.getString("logId"),sensor,sensorType,building.getBName(),currentTime,alarm);
                }
            }
        }
        //对减少的异常测点修改恢复时间
        List<Alarm> currentPointList2 = current.getJSONArray("alarms").toJavaList(Alarm.class);
        lastPointList.removeAll(currentPointList2);
        if(!lastPointList.isEmpty()){
            for(Alarm a: lastPointList){
                if(a.isNormal()){
                    for(SensorAbnormal s : abnormals){
                        if(s.getMeasuringPoint().equals(a.getAlarmCode())){
                            s.setRestoreTime(currentTime);
                            esTransportUtil.updateDocument(ABNORMAL_INDEX,ABNORMAL_TYPE_FIREDOOR,s.getId(),s);
                        }
                    }
                }
            }
        }
        //对持续的异常，更新异常logId
        List<Alarm> lastPointList2 = last.getJSONArray("alarms").toJavaList(Alarm.class);
        lastPointList2.retainAll(currentPointList2);
        if(!lastPointList2.isEmpty()){
            for(Alarm a: lastPointList2){
                if(!a.isNormal()){
                    for(SensorAbnormal s : abnormals){
                        String alarmCode = a.getAlarmCode();
                        if(s.getMeasuringPoint().equals(alarmCode)){
                            s.getLogIds().add(current.getString("logId"));
                            esTransportUtil.updateDocument(ABNORMAL_INDEX,ABNORMAL_TYPE_FIREDOOR,s.getId(),s);
                        }
                    }
                }
            }
        }
        //发送异常通知
        List<Alarm> currentPointList3 = current.getJSONArray("alarms").toJavaList(Alarm.class);
        sendAlarmNotice(sensor,sensorType,building.getBName(),currentPointList3,currentTime);
    }

    //添加新异常记录
    public void saveSensorAbnormal(String logId, DeviceSensor sensor, DeviceSensorType sensorType,String bName, Date currentTime, Alarm alarm){
        if(alarm.isNormal()){
            return;
        }
        SensorAbnormal entity = new SensorAbnormal();
        entity.setTenantId(sensor.getTenantId());
        entity.setAbnormalType(Integer.valueOf(alarm.getAlarmStatus()));
        entity.setAddress(sensor.getPositionDescription());
        entity.setBuildingName(bName);
        entity.setFloor(sensor.getFloor());
        entity.setIsHandle(false);
        entity.setMeasuringPoint(alarm.getAlarmCode());
        entity.setSensorNo(sensor.getSensorNo());
        entity.setDeviceSeries(sensorType.getEquipmentType());
        entity.setManufacturer(sensorType.getManufacturer());
        entity.setModel(sensorType.getModel());
        entity.setAlarmTime(currentTime);
        entity.setAlarmType(alarm.getAlarmType());
        entity.setAlarmValue(alarm.getAlarmValue().toString());
        entity.setLogIds(Lists.newArrayList(logId));
        esTransportUtil.addDocument(ABNORMAL_INDEX,ABNORMAL_TYPE_FIREDOOR, UUIDUtils.generateUuid(),entity);
        log.info("保存异常成功");
        //针对离线数据存Redis
        if("OFFLINE".equals(alarm.getAlarmCode())){
            redisTemplate.opsForSet().add(firedoorOfflineRedisKey,sensor.getSensorNo());
        }
    }


    //发送报警通知
    public void sendAlarmNotice(DeviceSensor sensor,DeviceSensorType sensorType, String bName,List<Alarm> alarms,Date alarmTime){
        //查询通知方案和通知人
        String noticeType= "1"; //1=报警通知/2=故障通知
        JSONObject noticeResponse = getIDeviceFeign().messageNotice(noticeType,null,sensor.getTenantId());
        if(!"200".equals(noticeResponse.getString("status"))){
            log.info("获取通知方式和通知人失败:" + noticeResponse);
            throw new RuntimeException("获取通知方式和通知人失败:" + noticeResponse);
        }
        JSONObject messageNotice = noticeResponse.getJSONObject("data");
        List<String> notices = messageNotice.getJSONArray("notice").toJavaList(String.class);
        JSONArray recipientArray = messageNotice.getJSONArray("recipients");
        List<String> userIdList = new ArrayList<>();
        Map<String,String> phoneMap = new HashMap<>();
        Map<String,String> nameMap = new HashMap<>();
        for(int i=0;i< recipientArray.size();i++){
            JSONObject jsonObject = recipientArray.getJSONObject(i);
            userIdList.add(jsonObject.getString("id"));
            phoneMap.put(jsonObject.getString("id"),jsonObject.getString("mobilePhone"));
            nameMap.put(jsonObject.getString("id"),jsonObject.getString("username"));
        }
        if(userIdList.size() == 0){
            return;
        }
        String[] phones = phoneMap.values().toArray(new String[0]);
        String[] userIds = userIdList.toArray(new String[0]);
        checkSendRule(userIdList,sensor.getId(),sensor.getChannelId(),phoneMap,noticeType);
        //发送报警通知
        String deviceSeries = sensorType.getEquipmentType();
        Map<String,String> vmsParams = Maps.newHashMap();
        vmsParams.put("building",bName);
        vmsParams.put("deviceSeries",deviceSeries);
        String[] smsParams = null;
        String abnormalType = null;
        String wsData = null;
        for(Alarm alarm: alarms){
            abnormalType = alarm.getAlarmStatus() == "0" ? "故障" : "报警";
            vmsParams.put("alrmType",abnormalType);
            vmsParams.put("status",abnormalType);
            smsParams = new String[]{bName,deviceSeries,abnormalType};
            //发送报警WebSocket消息
            wsData = JSONObject.toJSONString(alarm);
            sendWs(userIds,wsData);
            //发送报警极光推送
            if(notices.contains("0")){
                String jpushData = JSONObject.toJSONString(alarm);
                sendJPush(userIds,jpushData);
                saveNoticeSendLog(userIdList,sensor.getChannelId(),alarmTime,sensor.getSensorNo(),
                        "0","APP", jpushData, phoneMap,nameMap,sensor.getId());
            }
            //发送报警短信
            if(notices.contains("1") && smsParams != null){
                //sendSms(phones,smsParams);
                sendAliSms(phones,vmsParams);
                StringBuilder msgContent = new StringBuilder();
                for(String str: smsParams){
                    if(str != null){
                        msgContent.append(str);
                    }
                }
                saveNoticeSendLog(userIdList,sensor.getChannelId(),alarmTime,sensor.getSensorNo(),
                        "1","短信", msgContent.toString(), phoneMap,nameMap,sensor.getId());
            }
            //发送报警电话
            if(notices.contains("2") && vmsParams != null){
                sendVms(phones,vmsParams);
                StringBuilder msgContent = new StringBuilder();
                if(vmsParams.get("building") != null){
                    msgContent.append(vmsParams.get("building"));
                }
                if(vmsParams.get("deviceSeries") != null){
                    msgContent.append(vmsParams.get("deviceSeries"));
                }
                if(vmsParams.get("alrmType") != null){
                    msgContent.append(vmsParams.get("alrmType"));
                }
                saveNoticeSendLog(userIdList,sensor.getChannelId(),alarmTime,sensor.getSensorNo(),
                        "2","语音电话", msgContent.toString(), phoneMap,nameMap,sensor.getId());
            }
        }
        log.info("发送异常通知成功");
    }


    private void checkSendRule(List<String> userIdList, Long sensorId, Integer channelId, Map<String,String> phoneMap, String noticeType){
        if(sensorId == null || channelId == null){
            return;
        }
        //查询推送规则　
        NoticeRuleSensor  rsEntity = this.noticeRuleSensorBiz.queryBySensorIdAndChannelId(sensorId.longValue(),channelId);
        if(rsEntity == null ){
            return ;
        }
        NoticeRule rule = this.noticeRuleBiz.queryById(rsEntity.getNoticeRuleId());
        if(rule == null || "1".equals(rule.getDelFlag())){
            return ;
        }
        //查询规则关联的用户
        List<NoticeRuleUser> rsList = this.noticeRuleUserBiz.queryByNoticeRuleId(rule.getId(),noticeType);
        if(rsList.size() == 0){
            return ;
        }
        for(NoticeRuleUser  ru : rsList){
            //如果不在发送列表中，就直接跳过
            if(!userIdList.contains(ru.getUserId())){
                continue;
            }
            NoticeLog log = this.noticeLogBiz.queryLastLog(sensorId.longValue(),rule.getChannelId(),ru.getUserId());
            if(log != null && (System.currentTimeMillis() - log.getNoticeTime().getTime()) < (rule.getIntervalTimeMinutes()-1) * 60 * 1000){
                userIdList.remove(ru.getUserId());
                phoneMap.remove(ru.getUserId());
            }
        }

    }

    private void saveNoticeSendLog(List<String> userIdList,Integer channelId,Date alarmTime,String sensorNo,
                                   String noticeType,String serviceSupplier,String content,Map<String,String> phoneMap,Map<String,String> userMap,Long sensorId){
        try {
            JSONObject json = this.iDeviceFeign.deviceSensor(sensorId);
            DeviceSensor sensor = null;
            if (json != null && json != null && json.getJSONObject("data") != null) {
                sensor = json.getJSONObject("data").toJavaObject(DeviceSensor.class);
            }
            for (String userId : userIdList) {
                NoticeLog noticeLog = new NoticeLog();
                noticeLog.setChannelId(channelId);
                noticeLog.setDelFlag("0");
                noticeLog.setAlarmTime(alarmTime);
                noticeLog.setNoticeContent(content);
                noticeLog.setNoticeResult("成功");
                noticeLog.setNoticeTime(new Date());
                noticeLog.setNoticeType(noticeType);
                noticeLog.setSensorId(sensorId);
                noticeLog.setSensorNo(sensorNo);
                noticeLog.setTenantId(sensor.getTenantId());
                noticeLog.setServiceSupplyName(serviceSupplier);
                noticeLog.setUserId(userId);
                noticeLog.setUsername(userMap.get(userId));
                noticeLog.setMobilePhone(phoneMap.get(userId));
                this.noticeLogBiz.insertSelective(noticeLog);
            }
        }catch (Exception e){
            log.error("消息日志保存失败，" + e.getMessage());
        }
    }
}
