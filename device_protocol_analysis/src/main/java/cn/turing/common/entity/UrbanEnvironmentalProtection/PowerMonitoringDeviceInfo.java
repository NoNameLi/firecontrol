package cn.turing.common.entity.UrbanEnvironmentalProtection;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.DeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.Date;

/**
 * 都是环保-电源检测
 */
@Data
public class PowerMonitoringDeviceInfo extends DeviceInfo {
    /**
     * 消息头
     */
    private String heart;
    /**
     * 01表示有电，00表示没电（220V市电）
     */
    private Integer eleStatus;
    /**
     * 设备电池电压V
     */
    private Integer deviceEleValue;
    /**
     * 开关状态 设备的开启状态 01表示开启 00表示关闭
     */
    private Integer switchStatus;

    public PowerMonitoringDeviceInfo(String data){
        String id=data.substring(0,30);
        StringBuffer stringBuffer=new StringBuffer();
        for (int i=1;i<=id.length()/2;i++){
            stringBuffer.append(data.substring(2*i-1,2*i));
        }
        Date date=new Date();
        this.device_id=stringBuffer.toString();
        this.recieve_time=date;
        this.upload_time=date;
        heart=data.substring(30,34);
        eleStatus=Integer.parseInt(data.substring(34,36),16);
        deviceEleValue=Integer.parseInt(data.substring(36,40),16);
        switchStatus=Integer.parseInt(data.substring(40),16);

        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(eleStatus());
        sensorDetails.add(deviceEleValue());
        sensorDetails.add(switchStatus());

    }

    private SensorDetail switchStatus() {
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.SWITCH);
        if (switchStatus==1){
            sensorDetail.setAlarmType("开");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }

        if (switchStatus==0){
            sensorDetail.setAlarmType("关");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            has_alarm=true;
        }

        return  sensorDetail;
    }

    private SensorDetail deviceEleValue(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOL);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(deviceEleValue*0.001);
        return  sensorDetail;

    }

    private SensorDetail eleStatus(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.PM);
        if (eleStatus==1){
            sensorDetail.setAlarmType("有电正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }

        if (eleStatus==0){
            sensorDetail.setAlarmType("断电");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            has_alarm=true;
        }

        return  sensorDetail;

    }



    public JSONObject toDeviceJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId", this.getDevice_id().toLowerCase());
        jsonObject.put("uploadTime",this.getUpload_time());
        jsonObject.put("receiveTime",this.getRecieve_time());
        JSONArray jsonArray=new JSONArray();
        if(this.getSensorDetails()!=null) {
            for (SensorDetail sensorDetail:this.getSensorDetails()) {
                JSONObject val = new JSONObject();
                val.put("alarmValue",sensorDetail.getAlarmValue());
                val.put("alarmType",sensorDetail.getAlarmType());
                val.put("alarmStatus",sensorDetail.getAlarmStatus());
                val.put("alarmCode",sensorDetail.getAlarmCode());
                jsonArray.add(val);
            }
        }
        jsonObject.put("point",jsonArray);
        return jsonObject;
    }

    public JSONObject toDeviceMessage() {
        return null;
    }
}
