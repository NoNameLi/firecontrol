package cn.turing.common.base;

import com.google.common.collect.Maps;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class MessageMap {

    private static String channelCode = "AiDe";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Map defMap(String hexText, Date date, String str){
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("device_id",str);//约定为所有的设备
        map.put("uploadtime",sdf.format(date));
        map.put("recievetime",sdf.format(new Date()));
        map.put("rawdata",hexText);
        return  map;
    }
}
