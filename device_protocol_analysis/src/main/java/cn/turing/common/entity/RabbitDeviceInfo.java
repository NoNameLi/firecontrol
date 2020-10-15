package cn.turing.common.entity;

import cn.turing.common.base.Constant;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 躬远设备的特性，抽取到这个类
 */
@Data
public class RabbitDeviceInfo extends DeviceInfo {
    private String data;
    private String flag;
    private String version;
    public RabbitDeviceInfo(JSONObject jsonObject) throws Exception{
        //20180820162829
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        this.setRecieve_time(new Date());

    }

	public RabbitDeviceInfo(String message){

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

    public JSONObject toDeviceMessage() {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("deviceCode",this.getDevice_id().toLowerCase());
        jsonObject.put("uploadTime",this.getUpload_time());
        jsonObject.put("receiveTime",this.getRecieve_time());
        if(this.getSensorDetails()!=null) {
            for (SensorDetail sensorDetail:this.getSensorDetails()) {
                JSONObject val = new JSONObject();
                val.put("alarmValue",sensorDetail.getAlarmValue());
                val.put("alarmType",sensorDetail.getAlarmType());
                val.put("alarmStatus",sensorDetail.getAlarmStatus());
                jsonObject.put(sensorDetail.getAlarmCode(),val);
            }
        }
        if (this.has_guzhang){
            jsonObject.put("status", Constant.ST_WARN);
        }else if (this.has_alarm){
            jsonObject.put("status",Constant.ST_ALARM);
        }else if (this.has_offline){
            jsonObject.put("status",Constant.ST_OFFLINE);
        }
        else{
            jsonObject.put("status",Constant.ST_NORM);
        }
        return jsonObject;
    }

    public Map<String,Object> toDeviceLogJSON(String channelCode) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", this.getDevice_id().toLowerCase());
        map.put("uploadtime", this.getUpload_time());
        map.put("recievetime", this.getRecieve_time());
        map.put("rawdata", data);

        return map;
    }

}
