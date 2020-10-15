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
public class PowerMonitoringDeviceInfoV1 extends DeviceInfo {

    /**
     * 220v开关1字符   0关  1开
     */
    private String eleStatus;
    /**
     * 设备电池电压V
     */
    private String deviceEleValue;
    /**
     * 供电状态1字符   0电池供电    1市电供电
     */
    private String switchStatus;

    public PowerMonitoringDeviceInfoV1(String data){

        Date date=new Date();
        this.device_id=data.substring(0,15);
        this.recieve_time=date;
        this.upload_time=date;
        eleStatus=data.substring(15,16);
        switchStatus=data.substring(16,17);
        deviceEleValue=data.substring(17);
        StringBuffer stringBuffer=new StringBuffer(deviceEleValue);
        stringBuffer.insert(1,".");
        deviceEleValue=stringBuffer.toString();
        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(eleStatus());
        sensorDetails.add(switchStatus());
        sensorDetails.add(deviceEleValue());


    }

    private SensorDetail switchStatus() {
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.DHS);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(switchStatus);

        return  sensorDetail;
    }

    private SensorDetail deviceEleValue(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOL);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(deviceEleValue);
        return  sensorDetail;

    }

    private SensorDetail eleStatus(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.SWITCH);
        if (eleStatus.equals("1")){
            sensorDetail.setAlarmType("开");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }

        if (eleStatus.equals("0")){
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
