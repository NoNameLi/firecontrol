package cn.turing.common.entity.nb;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 所有NB -upd设备
 */
@Data
public class NBDeviceInfoByUDP {
    /**
     * 将数据封装json
     */
    private JSONObject jsonObject;

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    public NBDeviceInfoByUDP(String data){

        String id=data.substring(0,15);
        String version="1.0.1";
        String time=sdf.format(new Date());
        int flag=2;
        //sim 标识
        if (data.substring(15).contains("8986")){
            String req=data.substring(15);
            String deviceType=data.substring(37,41);
            String iccid=data.substring(15,35);
            jsonObject=new JSONObject();
            jsonObject.put("id",id);
            jsonObject.put("version",version);
            jsonObject.put("time",time);
            jsonObject.put("flag",flag);
            jsonObject.put("data",req);
            jsonObject.put("deviceType",deviceType);
            jsonObject.put("iccid",iccid);
        }else{
            String req=data.substring(17);
            String deviceType=data.substring(17,21);
            jsonObject=new JSONObject();
            jsonObject.put("id",id);
            jsonObject.put("version",version);
            jsonObject.put("time",time);
            jsonObject.put("flag",flag);
            jsonObject.put("data",req);
            jsonObject.put("deviceType",deviceType);
        }



    }
}
