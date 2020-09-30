package cn.turing.firecontrol.server.entity;

import cn.turing.firecontrol.server.base.Constant;
import cn.turing.firecontrol.server.util.ByteUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.log4j.Log4j;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 设备信息
 */
@Data
public abstract class EasyLinkInDeviceInfo extends DeviceInfo {
    //以下为慧莲无线的lora模块信息
    private String appeui;                         //应用服务电子识别号
    private String data;                           //具体传输的数据
    private String reserver;                      //保留字段
    private String data_type;                     //数据类型
    private List<Gateways> gateways;              //多条网关信息


    public EasyLinkInDeviceInfo(JSONObject jsonObject) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");//20180820162829
        List<Gateways> listGateways = (List<Gateways>) jsonObject.get("gateways");
        this.setData(jsonObject.getString("data").toString());
        this.setDevice_id(jsonObject.getString("mac").toLowerCase());
        this.setUpload_time(sdf.parse(jsonObject.getString("last_update_time")));
        this.setRecieve_time(new Date());
        this.setGateways(jsonObject.getJSONArray("gateways").toJavaList(Gateways.class));
        this.setReserver(jsonObject.getString("reserver"));
        this.setAppeui(jsonObject.getString("appeui"));
        this.setData_type(jsonObject.getString("data_type"));
    }

    @Override
    public JSONObject toDeviceJSON() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceid", this.getDevice_id());
        jsonObject.put("appeui", this.getAppeui());
        jsonObject.put("datatype", this.getData_type());
        jsonObject.put("reserver", this.getReserver());
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
