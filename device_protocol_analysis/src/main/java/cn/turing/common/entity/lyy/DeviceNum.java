package cn.turing.common.entity.lyy;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.DeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.Date;

/**
 * 充电桩 通道的信息
 */
@Data
public class DeviceNum extends DeviceInfo {
    /**
     * 设备编号
     */
    private String deviceNo;
    /**
     * 电压
     */
    private String vol;
    /**
     * 电流
     */
    private String ele;
    /**
     * 功率
     */
    private String pw;
    /**
     * 通道号
     */
    private String num;


    public DeviceNum(JSONObject jsonObject){
        deviceNo=jsonObject.getString("deviceNo");
        Float v=jsonObject.getFloat("voltage");
        Float e=jsonObject.getFloat("current");
        if (v==null){
            vol=String.valueOf("0.0");
        }else{
            vol=String.valueOf(v);
        }
        if (e==null){
            ele=String.valueOf("0.0");
        }else{
            ele=String.valueOf(e);
        }

        pw=String.valueOf(jsonObject.getFloat("power"));
        num=String.valueOf(jsonObject.getInteger("num"));

        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(volMessage());
        sensorDetails.add(eleMessage());
        sensorDetails.add(pwMessage());


    }


    public SensorDetail volMessage(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOL);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(vol);

        return sensorDetail;
    }

    public SensorDetail pwMessage(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.POWER);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(pw);

        return sensorDetail;
    }

    public SensorDetail eleMessage(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECE);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(ele);

        return sensorDetail;
    }


    public JSONObject toDeviceJSON() {
        return null;
    }

    public JSONObject toDeviceMessage() {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("deviceCode",deviceNo);
        jsonObject.put("uploadTime",new Date());
        jsonObject.put("receiveTime",new Date());
        jsonObject.put("num",num);
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
        }else{
            jsonObject.put("status",Constant.ST_NORM);
        }
        return jsonObject;
    }
}
