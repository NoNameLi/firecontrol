package cn.turing.firecontrol.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class IPUtils {

    private static Logger log = LoggerFactory.getLogger(IPUtils.class);

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值
     *
     * @return ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if( ip.indexOf(",")!=-1 ){
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    /**
     * 通过淘宝IP获取对应地区，如果IP为空或者不合法则返回null
     * @param ip
     * @return
     */
    public static String getRegion(String ip){
        String ipv4 = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
        String ipv6 = "^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$";
        if (ip ==null || ip.trim().isEmpty() || ( !Pattern.matches(ipv4,ip) && !Pattern.matches(ipv6,ip) ) ){
            return "";
        }
        //通过淘宝免费接口查询IP
//        return getRegionByTaoBao(ip);
        //通过高德地图接口查询IP
        return getRegionByGaode(ip);

    }

    /**
     * 通过淘宝免费接口查询IP
     * @param ip
     * @return
     */
    private static String getRegionByTaoBao(String ip){
        String json = "";
        try{
            URL url = new URL("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            json = IOUtils.toString(is,"utf-8");
        }catch (IOException e){
            log.error("查询IP地址失败",e);
            return "未知";
        }
        JSONObject object = JSON.parseObject(json);
        object = object.getJSONObject("data");
        String city = object.getString("region") + object.get("city");
        Object county = object.get("county");
        if(county != null && !"XX".equals(county.toString())){
            city += county;
        }
        if(city.contains("内网")){
            city = "内网";
        }
        return city;
    }


    private static String getRegionByGaode(String ip){
        String uri = "http://iploc.market.alicloudapi.com/v3/ip?ip=" + ip;
        String appcode = "2c98bf83572848379a4629957378d6eb";
        log.info("appcode:{}",appcode);
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        String json = "";
        try{
            URL url = new URL(uri);
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("Authorization","APPCODE " + appcode);
            InputStream is = connection.getInputStream();
            json = IOUtils.toString(is,"utf-8");
            JSONObject obj = JSONObject.parseObject(json);
            String province = obj.getString("province");
            String city = obj.getString("city");
            if("[]".equals(city)){
                city = "";
            }
            return  province + city;
        }catch (IOException e){
            log.error("查询IP地址失败",e);
            System.out.println(e.getMessage());
            return "未知";
        }

    }

    public static void main(String[] args){
        //String ip = "119.98.79.212";
//       String ip = "192.168.0.224";
        String ip = "27.18.85.25";
        String region = IPUtils.getRegion(ip);
        System.out.println(region);
    }

}
