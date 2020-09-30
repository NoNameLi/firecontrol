package cn.turing.firecontrol.server.entity;

import cn.turing.firecontrol.server.base.Constant;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
public abstract class DeviceInfo {
    protected String device_id;  //设备自己的唯一编号
    protected Date upload_time; //设备信息数据的时间
    protected Date recieve_time;//我方程序接受时间

    //以下为空间换时间
    protected boolean has_alarm =false; //设备是否报警
    protected boolean has_guzhang =false;//设备是否故障

    protected List<SensorDetail> sensorDetails; //设备外接传感器信息列表，一个设备可能外接n个传感器

    /**
     * 设备信息的JSON格式,每台设备都有自己的特殊点，所以只能抽象
     * @return
     */
    public abstract JSONObject toDeviceJSON();

    /**
     * 基本 行数据
     * @return
     */
    public JSONObject toBaseJSON() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isalarm",this.isHas_alarm());
        jsonObject.put("isguzhang",this.isHas_guzhang());
        jsonObject.put("uploadtime",sdf.format(this.getUpload_time()));
        jsonObject.put("recievetime",sdf.format(this.getRecieve_time()));
        return jsonObject;
    }
    /**
     * 报警信息的JSON格式
     * @return
     */
    public JSONObject toAlarmJSON() {
        //{"deviceid":"设备的ID","uploaddate":"上传时间","recievedate":"接受时间","alarms":[{"alarmCode":"约定的缩写","alarmType":"约定的缩写","alarmValue":"约定的值"}]}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceid",this.getDevice_id());
        jsonObject.put("uploadtime",sdf.format(this.getUpload_time()));
        jsonObject.put("recievetime",sdf.format(this.getRecieve_time()));
        JSONArray jsonArray = new JSONArray();
        for(SensorDetail sensorDetail:getSensorDetails()){
            if(sensorDetail.getAlarmStatus()<2){
                JSONObject jo = new JSONObject();
                jo.put("alarmCode",sensorDetail.getAlarmCode());
                jo.put("alarmType",sensorDetail.getAlarmType());
                jo.put("alarmValue",sensorDetail.getAlarmValue());
                jo.put("alarmStatus",sensorDetail.getAlarmStatus());
                jsonArray.add(jo);
            }
        }
        if(isHas_alarm()){
            jsonObject.put("status",Integer.toString(Constant.ST_ALARM));
        }else if(isHas_guzhang()){
            jsonObject.put("status",Integer.toString(Constant.ST_WARN));
        }else{
            jsonObject.put("status",Integer.toString(Constant.ST_NORM));
        }
        jsonObject.put("alarms",jsonArray);
        return jsonObject;
    }

    /**
     * 报警信息的JSON格式(防火门)
     * @return
     */
    public JSONObject toAlarmJSON(String uuid) {
        //{"deviceid":"设备的ID","uploaddate":"上传时间","recievedate":"接受时间","alarms":[{"alarmCode":"约定的缩写","alarmType":"约定的缩写","alarmValue":"约定的值"}]}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("logId", uuid);
        jsonObject.put("deviceid",this.getDevice_id().toLowerCase());
        jsonObject.put("uploadtime",sdf.format(this.getUpload_time()));
        jsonObject.put("recievetime",sdf.format(this.getRecieve_time()));
        JSONArray jsonArray = new JSONArray();
        for(SensorDetail sensorDetail:getSensorDetails()){

                JSONObject jo = new JSONObject();
                jo.put("alarmCode",sensorDetail.getAlarmCode());
                jo.put("alarmType",sensorDetail.getAlarmType());
                jo.put("alarmValue",sensorDetail.getAlarmValue());
                jo.put("alarmStatus",sensorDetail.getAlarmStatus());
                jsonArray.add(jo);

        }
        if(isHas_alarm()){
            jsonObject.put("status",Integer.toString(Constant.ST_ALARM));
        }else if(isHas_guzhang()){
            jsonObject.put("status",Integer.toString(Constant.ST_WARN));
        }else{
            jsonObject.put("status",Integer.toString(Constant.ST_NORM));
        }
        jsonObject.put("alarms",jsonArray);
        return jsonObject;
    }
}
