package cn.turing.common.entity.lyy;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.DeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.Date;

/**
 * 充电桩状态
 */
@Data
public class DeviceStatus extends DeviceInfo {
    /**
     * 设备编号
     */
    private String deviceNo;
    /**
     * 时间
     */
    private String time;
    /**
     * 状态
     */
    private String status;

    public DeviceStatus(JSONObject jsonObject){
        deviceNo=jsonObject.getString("deviceNo");
        time=jsonObject.getString("time");
        status=jsonObject.getString("status");

//        this.sensorDetails= Lists.newArrayList();
//        sensorDetails.add(get());

    }

//    public SensorDetail get(){
//        SensorDetail sensorDetail=new SensorDetail();
//        if (status.equals("LOGIN")){
//            sensorDetail.setAlarmStatus(Constant.ST_NORM);
//            sensorDetail.setAlarmType("在线");
//        }
//        if (status.equals("LOGOUT")){
//            sensorDetail.setAlarmStatus(Constant.ST_OFFLINE);
//            sensorDetail.setAlarmType("离线");
//            this.has_guzhang=true;
//        }
//
//        return sensorDetail;
//
//    }



    public JSONObject toDeviceJSON() {
        return null;
    }

    public JSONObject toDeviceMessage() {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("deviceCode",deviceNo);
        jsonObject.put("uploadTime",new Date());
        jsonObject.put("receiveTime",new Date());
//        if(this.getSensorDetails()!=null) {
//            for (SensorDetail sensorDetail:this.getSensorDetails()) {
//                JSONObject val = new JSONObject();
//                val.put("alarmValue",sensorDetail.getAlarmValue());
//                val.put("alarmType",sensorDetail.getAlarmType());
//                val.put("alarmStatus",sensorDetail.getAlarmStatus());
//                jsonObject.put(sensorDetail.getAlarmCode(),val);
//            }
//        }
        if (this.has_guzhang){
            jsonObject.put("status", Constant.ST_OFFLINE);
        }else{
            jsonObject.put("status",Constant.ST_NORM);
        }
        return jsonObject;
    }
}
