package cn.turing.firecontrol.common.util;//package cn.turing.firecontrol.device.util.callUtil.main;

import com.huawei.main.Auth;
import com.huawei.main.CallNotify;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class HuaweiVmsUtil {

    // 请向管理员获取RTC业务平台语音通话能力API的调用地址，并替换BASE_URL的取值。
    public static final String BASE_URL = "https://117.78.29.66:10443";
    // 请向管理员获取app_key替换APPID的取值，app_secret替换SECRET的取值。
    public static final String APPID = "moZxLJaCg7RvUTSFdTy1h7sx84WG";
    public static final String SECRET = "4pWILkRO56pBVpcQ14yT7q32x025";


    //*************************** 下列常量无需修改  *********************************//

    /*
     * 请求头域
     */
    public static final String HEADER_APP_KEY = "app_key";
    public static final String HEADER_APP_AUTH = "Authorization";

    /*
     * 鉴权接口请求URL
     */
    public static final String APP_AUTH = BASE_URL + "/rest/fastlogin/v1.0";
    public static final String REFRESH_TOKEN = BASE_URL + "/omp/oauth/refresh";
    public static final String DELETE_AUTH = BASE_URL + "/rest/logout/v1.0";
    public static final String REFRESH_OCEANSTOR = BASE_URL + "/rest/refreshkey/v2.0";


    //测试地址
//    public static final String CALL_NOTIFY_TEST = BASE_URL + "/sandbox/rest/httpsessions/callnotify/";
    //商用地址
    public static final String CALL_NOTIFY_COMERCIAL = BASE_URL + "/rest/httpsessions/callnotify/";



    private static String templateId = "ZhongKeTuLing_MD_02";
    private static String bindNbr = "+8678880008032";
    private static String displayNbr = "+862759768121";
    private static String username = "ZhongKeTuLing";
    private static String password = "ZhongKeTuLing@12";

    @Autowired
    private RedisTemplate redisTemplate ;


    /**
     * 报警短信通知
     * @param phones 电话号码
     * @param param  参数
     */
/*    public void sendAlarm(String[] phones, AlarmParam param){
        String[] paramStr = param.toParam();
        if(phones!=null&&phones.length>0&&paramStr.length==3){
            for(int i=0;i<phones.length;i++){
                call(paramStr,"+86"+phones[i]);
            }
        }
    }*/

    /**
     * 报警短信通知
     * @param phone 电话号码
     * @param param  参数
     */
    public Map sendAlarm(String phone, AlarmParam param){
        String[] paramStr = param.toParam();
        Map map = new HashMap();
        if(phone!=null&&phone.length()>0&&paramStr.length==3){
            map = call(paramStr,"+86"+phone);
        }
        return map;
    }



    public Map callPhone(List<String> params,String calleeNbr){
        CallNotify callTest = new CallNotify(CALL_NOTIFY_COMERCIAL);
        String  access_token = "";
        if(redisTemplate.opsForValue().get("access_token")==null){
            Auth auth = new Auth(APPID,SECRET,APP_AUTH,REFRESH_TOKEN,DELETE_AUTH,REFRESH_OCEANSTOR);
            try {
                auth.fastlogin(username,password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Map<String,String> authResponsebody= auth.getResponsebody();
            access_token = authResponsebody.get("access_token");
            redisTemplate.opsForValue().set("access_token",access_token);
            redisTemplate.expire("access_token",11, TimeUnit.HOURS);
        }else {
            access_token = redisTemplate.opsForValue().get("access_token").toString();
        }

        // 接口响应的消息体
        Map<String,Object> map = callTest.getplayInfo(templateId,params);
        List playInfoList = new ArrayList();
        playInfoList.add(map);
        try {
            callTest.callNotifyAPI(access_token,APPID,displayNbr,bindNbr,calleeNbr,playInfoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String,String> responsebody = callTest.getResponsebody();
        return responsebody;
    }

    public Map call(String [] params,String calleeNbr){
        List<String> list1 = paraOne(params[0]);
        List<String> list2 = paraTwo(params[1]);
        list1.addAll(list2);
        list1.add(params[2]);
        return callPhone(list1,calleeNbr);
    }

    /**
     * 处理第一个参数
     * @param str1
     * @return
     */
    public List<String> paraOne(String str1){
        int count1 = str1.length();
        int xcount1 = count1%10;
        List<String> para1 = new ArrayList<>(11);
        for(int i=0;i<11;i++){
            if(count1>10*(i)){
                if(count1>=10*(i+1)){
                    para1.add( str1.substring(i*10,i*10+10));
                }else {
                    para1.add(str1.substring(i*10,i*10+xcount1));
                }
            }else {
                para1.add("");
            }
        }
        return para1;
    }

    /**
     * 处理第二个参数
     * @param str2
     * @return
     */
    public List<String> paraTwo(String str2){
        int count2 = str2.length();
        int xcount2 = count2%10;
        List<String> para2 = new ArrayList<>(5);
        for(int i=0;i<5;i++){
            if(count2>10*(i)){
                if(count2>=10*(i+1)){
                    para2.add( str2.substring(i*10,i*10+10));
                }else {
                    para2.add( str2.substring(i*10,i*10+xcount2));
                }
            }else {
                para2.add("");
            }

        }
        return para2;
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
        public String[] toParam(){
            return new String[]{buildingName,deviceSeriesName,status};
        }
    }




}
