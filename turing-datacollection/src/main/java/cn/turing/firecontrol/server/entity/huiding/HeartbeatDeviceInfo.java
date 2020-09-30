package cn.turing.firecontrol.server.entity.huiding;

import cn.turing.firecontrol.server.base.Constant;
import cn.turing.firecontrol.server.entity.DeviceInfo;
import cn.turing.firecontrol.server.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class HeartbeatDeviceInfo extends DeviceInfo {

    private Integer signalStrength;//信号强度
    private Float vol;//电池电量

    public HeartbeatDeviceInfo(String s) {
        device_id=s.substring(12,20);
        signalStrength=Integer.parseInt(s.substring(20,22),16);
        vol=Float.intBitsToFloat(Integer.parseInt(s.substring(22,26),16));
        upload_time=new Date();
        recieve_time=new Date();
        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(signal(signalStrength));
        sensorDetails.add(voler(vol));
    }

    @Override
    public JSONObject toDeviceJSON() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceid", this.getDevice_id().toLowerCase());
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
    public SensorDetail signal(Integer signalStrength){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.SIGN);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(signalStrength);

        return sensorDetail;
    }
    public SensorDetail voler(Float vol){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XELE);
        if (vol<5){
            sensorDetail.setAlarmType("电量低");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(signalStrength);
            has_guzhang=true;
        }else{
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(signalStrength);
        }


        return sensorDetail;
    }

}
