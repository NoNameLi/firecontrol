package cn.turing.firecontrol.datahandler.listener.abnormalHandler;

import cn.turing.firecontrol.common.util.AliSmsUtil;
import cn.turing.firecontrol.common.util.AliVmsSent;
import cn.turing.firecontrol.common.util.FeiGeSmsUtil;
import cn.turing.firecontrol.datahandler.biz.NoticeLogBiz;
import cn.turing.firecontrol.datahandler.biz.NoticeRuleBiz;
import cn.turing.firecontrol.datahandler.biz.NoticeRuleSensorBiz;
import cn.turing.firecontrol.datahandler.biz.NoticeRuleUserBiz;
import cn.turing.firecontrol.datahandler.business.JpushServiceImpl;
import cn.turing.firecontrol.datahandler.entity.NoticeLog;
import cn.turing.firecontrol.datahandler.entity.NoticeRule;
import cn.turing.firecontrol.datahandler.entity.NoticeRuleSensor;
import cn.turing.firecontrol.datahandler.entity.NoticeRuleUser;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created on 2019/03/07 16:05
 *
 * @Description TODO
 * @Version V1.0
 */
@Component
@Slf4j
@Data
public class VideoAbnormalHandler extends AbstractAbnormalHandler {

    @Value("${tmc.config.rabbitmq.abnormal.websocket.exchange}")
    private String wsExchange;
    @Value("${tmc.config.rabbitmq.abnormal.websocket.routingKey}")
    private String wsRoutingKey;
    @Value("${sms.feige.fireTemplateId}")
    private String feiGeTemplateId;
    @Value("${vms.aliyun.fireTemplateNo}")
    private String aliyunTemplateNo;
    @Value("${sms.ailiyun.videoTemplateNo}")
    private String aliyunSmsTemplateNo;

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private IDeviceFeign iDeviceFeign;
    @Autowired
    private JpushServiceImpl jpushService;
    @Autowired
    private AliVmsSent aliVmsSent;
    @Autowired
    private FeiGeSmsUtil feiGeSmsUtil;
    @Autowired
    private AliSmsUtil aliSmsUtil;

    @Autowired
    private NoticeLogBiz noticeLogBiz;

    @Autowired
    private NoticeRuleUserBiz noticeRuleUserBiz;

    @Autowired
    private NoticeRuleBiz noticeRuleBiz;

    @Autowired
    private NoticeRuleSensorBiz noticeRuleSensorBiz;

    //{"deviceSerial":"视频识别","analysisDataIds":["d007a2e18aa6475a93009e0be8eeeaa3"],"alarmType":"火警","alarmTime":"2019-02-25 18:27:39","tenantId":"a164JAy9","deviceNo":"202480707","deviceName":"测试摄像头","pictures":["http://file.tmc.turing.ac.cn/video_202480707_1551090459015"]}
    @Override
    public void handleAbnormal(String json) {
        JSONObject mqMsg = JSONObject.parseObject(json);
        String tenantId = mqMsg.getString("tenantId");
        String sensorNo = mqMsg.getString("sensorNo");
        if(StringUtils.isBlank(sensorNo)){
            sensorNo = mqMsg.getString("deviceNo");
        }
        String alarmTime = mqMsg.getString("alarmTime");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date  alarmDate = null;
        try{
            alarmDate = sdf.parse(alarmTime);
        }catch (Exception e){
            alarmDate = new Date();
        }
        //定义WebSocket消息
        String wsData = json;
        //定义极光推送消息
        String deviceName = mqMsg.getString("deviceName");
        String alarmType = mqMsg.getString("alarmType");
        //您好！{设备名}摄像头处于｛报警类型｝状态，请立即前往现场处理！
        String[] jpushData = new String[]{deviceName,alarmType};
        //定义报警短信的参数
        String[] smsParams = new String[]{deviceName,alarmType};
        //定义语音消息参数
        Map<String,String> vmsParams = Maps.newHashMap();
        vmsParams.put("deviceName",deviceName);
        vmsParams.put("alarmType",alarmType);
        //发送通知;
        sendAlarmNotice(tenantId,wsData,jpushData,smsParams,vmsParams,sensorNo,alarmDate);
    }
   /* public void sendSms(){
         //sendSms(null,null);
        String[] phones={"13164616349"};
        Map<String,String> vmsParams=new HashMap<>();
        vmsParams.put("deviceName","测试");
        vmsParams.put("alarmType","报警");
        sendAliSms(phones,vmsParams);
    }*/

    //发送报警通知
    public void sendAlarmNotice(String tenantId, String wsData,String[] jpushData, String[] smsParams,Map<String,String> vmsParams,String sensorNo,Date alarmTime){
        //查询通知方案和通知人
        String noticeType= "1"; //1=报警通知/2=故障通知
        JSONObject noticeResponse = getIDeviceFeign().messageNotice(noticeType,null,tenantId);
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
        log.info("设备编号:{}",sensorNo);
        JSONObject json = this.iDeviceFeign.selectBySensorNo(sensorNo);
        Integer sensorId = null;
        Integer channelId = null;
        if (json != null && json != null && json.getJSONObject("data") != null) {
            sensorId = json.getJSONObject("data").getInteger("id");
            channelId = json.getJSONObject("data").getInteger("channelId");
        }
        checkSendRule(userIdList,sensorId,channelId,phoneMap,noticeType);
        if(userIdList.size() == 0){
            return;
        }
        String[] phones = phoneMap.values().toArray(new String[0]);
        String[] userIds = userIdList.toArray(new String[0]);
        //发送报警WebSocket消息
        if(wsData != null){
            sendWs(userIds,wsData);
        }
        //发送报警极光推送
        if(notices.contains("0") && jpushData != null){
            String content = "您好！%s摄像机处于%s状态，请立即前往现场处理！";
            sendJPush(userIds,String.format(content,jpushData));
            saveNoticeSendLog(userIdList,channelId,alarmTime,sensorNo,
                    "0","极光推送", jpushData[0] + jpushData[1], phoneMap,nameMap,sensorId.longValue());
        }
        //发送报警短信
        if(notices.contains("1") && smsParams != null){
            //sendSms(phones,smsParams);
            sendAliSms(phones,vmsParams);
            saveNoticeSendLog(userIdList,channelId,alarmTime,sensorNo,
                    "1","阿里云短信", smsParams[0] + smsParams[1], phoneMap,nameMap,sensorId.longValue());
        }
        //发送报警电话
        if(notices.contains("2") && vmsParams != null){
            sendVms(phones,vmsParams);
            saveNoticeSendLog(userIdList,channelId,alarmTime,sensorNo,
                    "2","阿里云语音电话", vmsParams.get("deviceName") + vmsParams.get("alarmType"), phoneMap,nameMap,sensorId.longValue());
        }
    }

    private void checkSendRule(List<String> userIdList,Integer sensorId,Integer channelId,Map<String,String> phoneMap,String noticeType){
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
            if(log != null && (System.currentTimeMillis() - log.getNoticeTime().getTime()) < (rule.getIntervalTimeMinutes() - 1) * 60 * 1000){
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
                noticeLog.setServiceSupplyName(serviceSupplier);
                noticeLog.setUserId(userId);
                noticeLog.setTenantId(sensor.getTenantId());
                noticeLog.setUsername(userMap.get(userId));
                noticeLog.setMobilePhone(phoneMap.get(userId));
                this.noticeLogBiz.insertSelective(noticeLog);
            }
        }catch (Exception e){
            log.error("消息日志保存失败，" + e.getMessage());
        }
    }
}
