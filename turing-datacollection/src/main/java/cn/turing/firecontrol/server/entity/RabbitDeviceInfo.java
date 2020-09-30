package cn.turing.firecontrol.server.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 躬远设备的特性，抽取到这个类
 */
@Data
@Slf4j
public class RabbitDeviceInfo extends DeviceInfo {
    private String data;
    private String flag;
    private String version;
    public RabbitDeviceInfo(JSONObject jsonObject) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//20180820162829
        log.info(jsonObject.toString());
        this.setData(jsonObject.getString("data"));
        this.setDevice_id(jsonObject.getString("id").toLowerCase());
        this.setVersion(jsonObject.getString("version"));
        this.setFlag(jsonObject.getString("flag"));
        String time=jsonObject.getString("time");
        if (time.length()>14){
            time=time.substring(0,19);
            this.setUpload_time(sdf.parse(time));
        }else{
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
            this.setUpload_time(sdf1.parse(time));
        }
        log.info("setUpload_time"+this.getUpload_time());
        this.setRecieve_time(new Date());

    }


    @Override
    public JSONObject toDeviceJSON() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceid", this.getDevice_id().toLowerCase());
        jsonObject.put("flag", this.getFlag());
        jsonObject.put("version", this.getVersion());
        jsonObject.put("uploadtime",this.getUpload_time());
        jsonObject.put("recievetime",this.getRecieve_time());
        if(this.getSensorDetails()!=null) {
            for (SensorDetail sensorDetail:this.getSensorDetails()) {
                JSONObject val = new JSONObject();
                val.put("alarmValue",sensorDetail.getAlarmValue());
                val.put("alarmType",sensorDetail.getAlarmType());
                val.put("alarmStatus",sensorDetail.getAlarmStatus());
                jsonObject.put(sensorDetail.getAlarmCode(),val);
            }
        }
        return jsonObject;
    }
}
