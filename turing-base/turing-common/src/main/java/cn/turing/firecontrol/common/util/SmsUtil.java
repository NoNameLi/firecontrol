package cn.turing.firecontrol.common.util;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.util.HashMap;
import java.util.Map;

public class SmsUtil {

    //ACCESS_KEY_ID
    private static final String ACCESS_KEY_ID = "";
    //ACCESS_KEY_SECRET
    private static final String ACCESS_KEY_SECRET = "";
    //验证码短信模板
    private static final String VALICODE_TEMPLATE = "";
    //预警短信模板
    private static final String EARLY_WARNING_TEMPLATE = "";
    //报警短信模板
    private static final String ALARM_TEMPLATE = "";//"SMS_145596759";
    //短信签名
    private static final String SIGN = "智慧消防";


    /**
     * 发送验证码
     * @param phoneNumbers 多个用逗号隔开
     * @param code
     * @param bussinessId
     */
    public static void sendValicode(String phoneNumbers,String code,String bussinessId) throws ClientException {
        Map<String,String> params = new HashMap<String,String>();
        params.put("code", code);
        sendSms(phoneNumbers,VALICODE_TEMPLATE,params,bussinessId);
    }


    /**
     * 发送预警信息
     * @param phoneNumbers 手机号，多个用逗号隔开
     * @param building 建筑物名称
     * @param deviceSeries 设备系列号
     * @param pointName 测点名称
     * @param alarmGrade 预警等级
     * @param pointValue 测点数据及单位
     * @param bussinessId 业务ID
     * @throws ClientException
     */
    public static void sendEarlyWarning(String phoneNumbers,String building,String deviceSeries,String pointName,String alarmGrade,String pointValue,String bussinessId) throws ClientException {
        Map<String,String> params = new HashMap<String,String>();
        params.put("building",building);
        params.put("deviceSeries",deviceSeries);
        params.put("pointName",pointName);
        params.put("alarmGrade",alarmGrade);
        params.put("pointValue",pointValue);
        sendSms(phoneNumbers,EARLY_WARNING_TEMPLATE,params,bussinessId);
    }

    /**
     * 发送报警消息
     * @param phoneNumbers 手机号，多个用逗号隔开
     * @param building 建筑物名称
     * @param deviceSeries 设备系列号
//     * @param pointName 测点名称
//     * @param pointValue 测点数据及单位
     * @param bussinessId 业务ID
     * @throws ClientException
     */
    /*public static void sendAlarm(String phoneNumbers,String building,String deviceSeries,String pointName,String pointValue,String bussinessId) throws ClientException {
        Map<String,String> params = new HashMap<String,String>();
        params.put("building",building);
        params.put("deviceSeries",deviceSeries);
        params.put("pointName",pointName);
        params.put("pointValue",pointValue);
        sendSms(phoneNumbers,ALARM_TEMPLATE,params,bussinessId);
    }*/

    public static void sendAlarm(String phoneNumbers,String building,String deviceSeries,String status,String bussinessId) throws ClientException {
        Map<String,String> params = new HashMap<String,String>();
        params.put("building",building);
        params.put("deviceSeries",deviceSeries);
//        params.put("pointName",pointName);
//        params.put("pointValue",pointValue);
        params.put("status",status);
        sendSms(phoneNumbers,ALARM_TEMPLATE,params,bussinessId);
    }

    /**
     * 发送短信
     * @param phoneNumbers 手机号，多个用逗号隔开
     * @param templateId 模板ID
     * @param params 参数
     * @param bussinessId 业务ID
     * @throws ClientException
     */
    private static void sendSms(String phoneNumbers,String templateId, Map<String,String> params, String bussinessId) throws ClientException {
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
//初始化ascClient需要的几个参数
        final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
        final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY_ID,
                ACCESS_KEY_SECRET);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为00+国际区号+号码，如“0085200000000”
        request.setPhoneNumbers(phoneNumbers);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(SIGN);
        //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
        request.setTemplateCode(templateId);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam(JSON.toJSONString(params));
        //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId(bussinessId);
//请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
//请求成功
        }
    }

    public static void main(String[] args) throws ClientException {
//        SmsUtil.sendValicode("13476008959","123456","123");
//        SmsUtil.sendAlarm("15392906405","未来之光7栋","电气火灾","温度","78度","123");
        SmsUtil.sendAlarm("18696119087","未来之光7栋","电气火灾","报警","123");
//        SmsUtil.sendEarlyWarning("15392906405","未来之光7栋","电气火灾","温度","一级","78度","123");
    }



}
