package cn.turing.firecontrol.device.util;

import com.baidu.aip.speech.AipSpeech;
import org.apache.poi.util.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

public class VoiceToTextUtil {
    //设置APPID/AK/SK
    public static final String APP_ID = "15107571";
    public static final String API_KEY = "f0HjMmNZBPuXjf4UQono5BGW";
    public static final String SECRET_KEY = "VVNL0SP0182fYiEPvrmFndsUW8HWs8Zs";

    public static String voiceToText(File file) throws Exception{
        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
//        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
//
//        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
//        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
        FileInputStream fis = new FileInputStream(file);
        byte[] date =IOUtils.toByteArray(fis);
        // 调用接口
        JSONObject res = client.asr(date, "pcm", 16000, null);
        System.out.println(res.toString());
        return res.get("result").toString();
    }

    public static void main(String[] args) throws Exception{
/*        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

//        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
//
//        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
//        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        JSONObject res = client.asr("C:\\Users\\Administrator\\Desktop\\16k.pcm", "pcm", 16000, null);
        System.out.println(res.toString());*/
        File file = new File("C:\\Users\\Administrator\\Desktop\\a.pcm");
        String test = VoiceToTextUtil.voiceToText(file);
        System.out.println(test);
    }
}
