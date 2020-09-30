package cn.turing.firecontrol.server.entity.aide;


import cn.turing.firecontrol.server.base.Constant;
import cn.turing.firecontrol.server.entity.DeviceInfo;
import cn.turing.firecontrol.server.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import java.util.Date;
@Log4j
@Data
public class AiDeDeviceInfoV1 extends DeviceInfo {


    private String data;//传感器原始数据

    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    //解析16进制数据
    public AiDeDeviceInfoV1(String hexString) {

       this.setUpload_time(new Date());
       this.setData(hexString);

        log.info("08开头数据做处理");
        String eventHex = hexString.substring(18, 20);//事件16进制

        Integer loopHex = Integer.parseInt(hexString.substring(21, 23), 16);
        String loopHexString = loopHex.toString();//回路10进制

        Integer locationHex = Integer.parseInt(hexString.substring(24, 26), 16);
        String locationHexString = locationHex.toString();//地址10进制
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode("AiDe");
        if ("80".equals(eventHex)) { //火警
            Integer loopHexAdd = loopHex + 1;
            String loopHexAddString = loopHexAdd.toString();

            this.setDevice_id("jxw_" + loopHexAddString + "_" + locationHexString);
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("火警");
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            this.has_alarm=true;
            //给你设备号，返回 specificlocation,floor,

        } else if ("81".equals(eventHex)) {//故障
            this.setDevice_id("jxw_" + loopHexString + "_" + locationHexString);
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("故障");
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            //给你设备号，返回 specificlocation,floor,
            this.has_guzhang=true;
        } else if ("70".equals(eventHex)) {//正常
            //其他状态   没有
            this.setDevice_id("jxw_" + loopHexString + "_" + locationHexString);
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Constant.ST_NORM);

        } else {
            log.error("其他状态不做处理");
        }
        this.setRecieve_time(new Date());
    }



    @Override
    public JSONObject toDeviceJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceid", this.getDevice_id());
        jsonObject.put("uploadtime",this.getUpload_time());
        jsonObject.put("recievetime",this.getRecieve_time());
        jsonObject.put("data",this.getData());
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
