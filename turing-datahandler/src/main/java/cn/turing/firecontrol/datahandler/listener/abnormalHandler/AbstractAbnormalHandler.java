package cn.turing.firecontrol.datahandler.listener.abnormalHandler;

import cn.turing.firecontrol.common.util.AliSmsUtil;
import cn.turing.firecontrol.common.util.AliVmsSent;
import cn.turing.firecontrol.common.util.BeanUtils;
import cn.turing.firecontrol.common.util.FeiGeSmsUtil;
import cn.turing.firecontrol.datahandler.business.JpushServiceImpl;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created on 2019/03/07 16:14
 *
 * @Description TODO
 * @Version V1.0
 */
@Slf4j
@Data
public abstract class AbstractAbnormalHandler {

    private static final String VIDEO_TYPE = "video";
    private static final String FIREDOOR_TYPE = "firedoor";
    private static final String FIREMAIN_TYPE="firemain";




    private String wsExchange;
    private String wsRoutingKey;
    private String feiGeTemplateId;
    private String aliyunTemplateNo;
    private String aliyunSmsTemplateNo;
    private AmqpTemplate amqpTemplate;
    private IDeviceFeign iDeviceFeign;
    private JpushServiceImpl jpushService;
    private AliVmsSent aliVmsSent;
    private FeiGeSmsUtil feiGeSmsUtil;
    private AliSmsUtil aliSmsUtil;


    //执行异常处理程序
    public abstract void handleAbnormal(String data) throws Exception;

    //获取处理器实体
    public static AbstractAbnormalHandler getInstance(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }
        switch (type){
            case VIDEO_TYPE://视频异常处理类
                return BeanUtils.getBean(VideoAbnormalHandler.class);
            case FIREDOOR_TYPE://防火门异常处理类
                return BeanUtils.getBean(FiredoorAbnormalHandler.class);
            case FIREMAIN_TYPE://消防主机异常处理类
                return BeanUtils.getBean(FireMainAbnormalHandler.class);
            default: //其他
                return BeanUtils.getBean(DefaultAbnormalHandler.class);
        }
    }

    /**
     * 发送WebSocket消息
     * @param userIds 接收消息的用户ID数组
     * @param wsData 消息内容
     */
    public void sendWs(String[] userIds,String wsData){
        JSONObject wsMsg = new JSONObject();
        wsMsg.put("userIds",userIds);
        wsMsg.put("msg",wsData);
        getAmqpTemplate().convertAndSend(getWsExchange(),getWsRoutingKey(),wsMsg.toJSONString());
        log.info("WebSocket消息发送成功");
    }


    /**
     * 发送极光推送消息
     * @param userIds 接收消息的用户ID数组
     * @param jpushData 消息内容
     */
    public void sendJPush(String[] userIds,String jpushData){
        getJpushService().sendPush(jpushData,null,userIds);
        log.info("极光消息发送成功");
    }

    /**
     * 发送语音电话通知
     * @param phones 接收通知的手机号
     * @param vmsParams 语音接口参数
     */
    public void sendVms(String[] phones,Map<String,String> vmsParams){
        for(int i = 0 ;i < phones.length ; i++){
            for(int k=0;k<3;k++){//循环3次,避免调用失败
                SingleCallByTtsResponse response= null;
                log.info("第{}次拨打电话",k);
                try {
                    response = getAliVmsSent().sendAlarm(getAliyunTemplateNo(),phones[i],vmsParams);
                } catch (ClientException e) {
                    log.error("号码:"+ phones[i] +"第"+ k +"次语音电话失败",e);
                    throw new RuntimeException("号码:"+ phones[i] +"第"+ k +"次语音电话失败",e);
                }
                if("OK".equals(response.getCode())){//如果成功,结束循环
                    log.info("拨打电话成功:{}",phones[i]);
                    break;
                }else {
                    log.error("第{}次拨打电话失败:{}",k,response.getMessage());
                }
            }
        }
        log.info("拨打电话");
    }

    /**
     * 发送短信通知
     * @param phones 接收通知的手机号
     * @param smsParams 短信接口参数
     */

    public void sendSms(String[] phones, String[] smsParams){
        getFeiGeSmsUtil().sendMsg(getFeiGeTemplateId(),phones,smsParams);
        log.info("短信消息发送成功");
    }
    public void sendAliSms(String[] phones, Map<String,String> params){
        String phoneNumbers = StringUtils.join(phones, ",");
        try {
            log.info("阿里云发送消息:{}",getAliSmsUtil().toString());
            getAliSmsUtil().sendSms(phoneNumbers,getAliyunSmsTemplateNo(),params,null);
            log.info("阿里云短信消息发送成功,{}",phones);
        } catch (ClientException e) {
            log.error("短信消息发送失败");
        }

    }


    //发送报警通知
    public void sendAlarmNotice(String tenantId, String wsData,String jpushData, String[] smsParams,Map<String,String> vmsParams){
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
        String[] phones = new String[recipientArray.size()];
        String[] userIds = new String[recipientArray.size()];
        for(int i=0;i< recipientArray.size();i++){
            JSONObject jsonObject = recipientArray.getJSONObject(i);
            phones[i] = jsonObject.getString("mobilePhone");
            userIds[i] = jsonObject.getString("id");
        }
        //发送报警WebSocket消息
        if(wsData != null){
            sendWs(userIds,wsData);
        }
        //发送报警极光推送
        if(notices.contains("0") && jpushData != null){
            sendJPush(userIds,jpushData);
        }
        //发送报警短信
        if(notices.contains("1") && smsParams != null){
            //sendSms(phones,smsParams);
        }
        //发送报警电话
        if(notices.contains("2") && vmsParams != null){
            sendVms(phones,vmsParams);
        }
    }


    @Data
    public static class Alarm{
        private String alarmType;
        private String alarmStatus;
        private Number alarmValue;
        private String alarmCode;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AbstractAbnormalHandler.Alarm alarm = (AbstractAbnormalHandler.Alarm) o;
            return Objects.equals(alarmType, alarm.alarmType) &&
                    Objects.equals(alarmStatus, alarm.alarmStatus) &&
                    Objects.equals(alarmCode, alarm.alarmCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(alarmType, alarmStatus, alarmCode);
        }

        //状态是否正常（2位正常状态）
        public boolean isNormal(){
            return "2".equals(alarmStatus);
        }
    }

}
