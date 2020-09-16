package cn.turing.firecontrol.common.util;

import com.baidu.aip.speech.AipSpeech;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created on 2018/12/14 15:02
 *
 * @Description TODO
 * @Version V1.0
 */
public class BaiDuAsrUtil {

    public static final Logger log = Logger.getLogger(BaiDuAsrUtil.class);

    //调用FFMPEG进行转码
    private static void mp3Convertpcm(String ffmpegPath,String mp3filepath,String pcmfilepath){
        String command = ffmpegPath + " -y  -i "+ mp3filepath +"  -acodec pcm_s16le -f s16le -ac 1 -ar 16000 " + pcmfilepath;
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(command);
            IOUtils.closeQuietly(process.getOutputStream());
            IOUtils.closeQuietly(process.getInputStream());
            IOUtils.closeQuietly(process.getErrorStream());
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("音频转码失败",e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("音频转码失败",e);
        } finally {
            //run调用lame解码器最后释放内存
            runtime.freeMemory();
        }
    }

    public static String asr(String appId,String apiKey,String secretKey,String pcmFile){
        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(appId, apiKey, secretKey);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        JSONObject res = client.asr(pcmFile, "pcm", 16000, null);
        log.info("语音接口调用结果:" + res.toString(2));
        if(!"success.".equalsIgnoreCase(res.getString("err_msg"))){
            log.error("语音识别失败");
            return null;
        }
        JSONArray ress = res.getJSONArray("result");
        if(ress == null || ress.length() == 0){
            return "";
        }else {
            return ress.get(0).toString();
        }

    }

    /**
     * 本方法只负责语音识别，MP3文件请自行处理
     * @param appId 百度接口参数
     * @param apiKey 百度接口参数
     * @param secretKey 百度接口参数
     * @param ffmpegPath ffmpeg路径
     * @param mp3FilePath mp3文件路径
     * @return
     */
    public static String baiDuAsr(String appId,String apiKey,String secretKey,String ffmpegPath, String mp3FilePath){
        String pcmfilepath = mp3FilePath.substring(0,mp3FilePath.lastIndexOf(".mp3")).concat(".pcm");
        mp3Convertpcm(ffmpegPath,mp3FilePath,pcmfilepath);
        String result = asr(appId,apiKey,secretKey,pcmfilepath);
        try {
            FileUtils.forceDelete(new File(pcmfilepath));
        } catch (IOException e) {
            e.printStackTrace();
            log.info("语音识别失败",e);
        }
        return result;

    }

    public static void main(String[] args) throws Exception {
        //设置APPID/AK/SK
        String appId = "15163465";
        String apiKey = "W3vVyVwBotPQ9v7NwcRrKgNB";
        String secretKey = "NpkFr823bchbOYNWdMj4P3CmG6QAnhif";
        String ffmpegPath = "D:\\ffmpeg\\bin\\ffmpeg.exe";
        String mp3filepath = "E:\\harry\\firecontrol\\turing-base\\turing-common\\src\\main\\resources\\test.mp3";
        String result = baiDuAsr(appId,apiKey,secretKey,ffmpegPath,mp3filepath);
        log.info("语音识别结果:" + result);
    }

}
