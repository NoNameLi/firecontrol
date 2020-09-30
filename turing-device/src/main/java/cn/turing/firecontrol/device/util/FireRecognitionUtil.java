package cn.turing.firecontrol.device.util;


import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 2019/02/13 17:12
 *
 * @Description TODO
 * @Version V1.0
 */
@Slf4j
@AllArgsConstructor
@Data
public class FireRecognitionUtil {

    private final static String APP_URL = "https://fire.xiaohuaerai.com/fire";

    private String appCode;
    private Double accuracyThreshold;


    //判断是否报警
    public Boolean isAlarm(double accuracy){
        return accuracy >= accuracyThreshold;
    }

    //图片分析
    public double analysisImage(byte[] bytes) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appCode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        String imageStr = Base64.getMimeEncoder().encodeToString(bytes);
        querys.put("src", imageStr);
        String res = BaseUtil.unicodeToString(HttpUtils.doPost(APP_URL, headers, querys));
        JSONObject jsonObject = JSONObject.parseObject(res);
        if(200 != jsonObject.getInteger("status")){
            log.error("火焰识别失败：{}",jsonObject.getString("msg"));
            throw new RuntimeException("火焰识别失败：" + jsonObject.getString("msg"));
        }
        return Double.valueOf(jsonObject.getString("accuracy"));
    }

    //识别本地图片
    public double analysisFromFile(File file) {
        try {
            byte[] bytes = FileUtils.readFileToByteArray(file);
            return analysisImage(bytes);
        } catch (IOException e) {
            log.info("火焰识别失败",e);
            throw new RuntimeException("火焰识别失败",e);
        }
    }


    //识别网络图片
    public double analysisFromURL(String url){
        byte[] bytes = BaseUtil.urlToBytes(url);
        return analysisImage(bytes);
    }

    public static void main(String[] args) {
//        String url = "http://file.tmc.turing.ac.cn/video_202480707_1550809913006";
        String file = "C://Users//Administrator//Desktop//tetws.jpg";
        FireRecognitionUtil util = new FireRecognitionUtil("0f0ff3b33ac34539a625606be4a64cc7",0.8);
//        double accuracy = util.analysisFromURL(url);
        double accuracy = util.analysisFromFile(new File(file));
        log.info("结果：{}",accuracy);
    }


}
