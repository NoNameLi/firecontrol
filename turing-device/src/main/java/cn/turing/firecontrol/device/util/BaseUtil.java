package cn.turing.firecontrol.device.util;

import cn.turing.firecontrol.common.util.UploadUtil;
import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 2019/02/21 11:00
 *
 * @Description TODO
 * @Version V1.0
 */
@Slf4j
public class BaseUtil {

    //图片URL转字节数组
    public static byte[] urlToBytes(String url){
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream());
            out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            byte[] content = out.toByteArray();
            return content;
        } catch (Exception e) {
            log.info("火焰识别失败",e);
            throw new RuntimeException("火焰识别失败",e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //unicode转中文字符串
    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            String group = matcher.group(2);
            ch = (char) Integer.parseInt(group, 16);
            //group1 \u6728
            String group1 = matcher.group(1);
            str = str.replace(group1, ch + "");
        }
        return str;
    }



    public static String GetImageStr(String imgFile)
    {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        System.out.println(encoder.encode(data).length()+"===");
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }


    //对字节数组字符串进行Base64解码并上传图片
    public static String GenerateAndUpload(String base64str,String savepath,String fileName)
    {
        if (base64str == null) //图像数据为空
            return "";
        // System.out.println("开始解码");
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(base64str);
            //  System.out.println("解码完成");
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            fileName=fileName + "_" + new Date().getTime() + ".jpg";
            Map<String,String> uploadRes =  UploadUtil.simpleupload(b,fileName);

            return uploadRes.get("url");
        }
        catch (Exception e)
        {
            log.error("上传报错："+e.getMessage());
            return "";
        }
    }



}
