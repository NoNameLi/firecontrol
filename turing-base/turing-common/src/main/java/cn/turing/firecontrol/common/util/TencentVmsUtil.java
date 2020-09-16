package cn.turing.firecontrol.common.util;

import com.github.qcloudsms.TtsVoiceSender;
import com.github.qcloudsms.TtsVoiceSenderResult;
import com.github.qcloudsms.httpclient.PoolingHTTPClient;
import lombok.AllArgsConstructor;
import lombok.Data;

import com.github.qcloudsms.httpclient.HTTPException;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONException;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by hanyong on 2018/09/19 11:23
 *
 * @Description 腾讯语音短信工具
 * @Version V1.0
 */
@Slf4j
@Component
public class TencentVmsUtil {

    //预警语音模板
    private static final Integer EARLY_WARNING_TEMPLATE = 197207;
    //报警语音模板
    private static final Integer ALARM_TEMPLATE = 202881;//.197209;
    //APP_ID
    private static final Integer APP_ID = 1400143529;
    //APP_KEY
    private static final String APP_KEY = "95ed9f0035aee473d4db8070186bed74";


    /**
     * 发送语音短信预警通知
     * @param phones
     * @param param
     */
    public void sendEarlyWarning(String[] phones, AlarmParam param){
        sendVms(EARLY_WARNING_TEMPLATE,phones,param);
    }

    /**
     * 发送语音短信报警通知
     * @param phones
     * @param param
     */
    public void sendAlarm(String[] phones, AlarmParam param){
        sendVms(ALARM_TEMPLATE,phones,param);
    }


    /**
     * 发送语音短信通知
     * @param templateId 语音模板编号
     * @param phones 需要语音通知的电话号码
     * @param param 通知的参数
     */
    private void sendVms(Integer templateId,String[] phones, AlarmParam param) {
        String[] params = param.toParam();
        // 创建一个连接池httpclient, 并设置最大连接量为10
        PoolingHTTPClient httpclient = new PoolingHTTPClient(10);
        TtsVoiceSender tvsender = new TtsVoiceSender(APP_ID, APP_KEY,httpclient);
        // 创建线程
        VmsThread[] threads = new VmsThread[phones.length];
        for (int i = 0; i < phones.length; i++) {
            threads[i] = new VmsThread(tvsender, "86", phones[i], templateId,params);
        }
        // 运行线程
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
    }

    //发送语音通知的线程类
    class VmsThread extends Thread{
        private final TtsVoiceSender sender;
        private final String nationCode;
        private final String phoneNumber;
        private final Integer templateId;
        private final String[] params;

        public VmsThread(TtsVoiceSender sender, String nationCode, String phoneNumber, Integer templateId,String[] params ) {
            this.sender = sender;
            this.nationCode = nationCode;
            this.phoneNumber = phoneNumber;
            this.templateId = templateId;
            this.params = params;
        }

        @Override
        public void run() {
            try {
                TtsVoiceSenderResult result = sender.send(nationCode, phoneNumber,
                        templateId, params, 3, "");
                System.out.println(result);
            } catch (HTTPException e) {
                // HTTP响应码错误
                e.printStackTrace();
                log.error("发送语音通知失败:" + phoneNumber, e);
            } catch (JSONException e) {
                // json解析错误
                e.printStackTrace();
                log.error("发送语音通知失败:" + phoneNumber, e);
            } catch (IOException e) {
                // 网络IO错误
                e.printStackTrace();
                log.error("发送语音通知失败:" + phoneNumber, e);
            }
        }
    }

    /**
     * 报警通知模板所需要的参数
     */
    @Data
    @AllArgsConstructor
    public class AlarmParam{
        //建筑名
        private String buildingName;
        //设备系列名
        private String deviceSeriesName;
        //设备状态
        private String status;
        /*//预警测点名称
        private String deviceMeasuringPointName;
        //预警等级
        private String alarmGrade;
        //测点数值
        private String deviceMeasuringPointValue;
        //测点单位
        private String deviceMeasuringPointUnit;*/

        /*public String[] toParam(){
            String[] params = null;
            String value = deviceMeasuringPointValue.concat(deviceMeasuringPointUnit);
            if(alarmGrade == null){
                params = new String[]{buildingName,deviceSeriesName,deviceMeasuringPointName,value};//用于报警通知的参数
            }else {
                params = new String[]{buildingName,deviceSeriesName,deviceMeasuringPointName,alarmGrade,value};//用于预警通知的参数
            }
            return params;
        }*/
        public String[] toParam(){
            return new String[]{buildingName,deviceSeriesName,status};
        }
    }


    public static void main(String[] args){
        String[] phones = new String[]{"13476008959"};
        TencentVmsUtil tencentVmsUtil = new TencentVmsUtil();
        AlarmParam alarmParam = tencentVmsUtil.new AlarmParam("未来之光7栋","鑫豪斯三相","报警");
        tencentVmsUtil.sendAlarm(phones,alarmParam);
    }



}
