package cn.turing.common.entity.lyy;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.DeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.Date;

/**
 * 设备主动上报故障
 */
@Data
public class DeviceFault extends DeviceInfo {
    /**
     * 设备号
     */
    private String deviceNo;

    /**
     * 通道号
     */
    private String num;
    /**
     * 停止代码
     */
    private String code;
    /**
     * 故障原因
     */
    private String reason;

    public DeviceFault (JSONObject jsonObject){
       // {"fn":"BSYS_AK_REPORT_CDZ_END","deviceNo":"0000000092000180","data":{"reason":"购买的充电时或者电量用完了","code":6,"num":2,"returnMoney":0}}
        deviceNo=jsonObject.getString("deviceNo");
        JSONObject data=jsonObject.getJSONObject("data");
        num=String.valueOf(data.getInteger("num"));
        code=String.valueOf(data.getInteger("code"));
        reason=String.valueOf(data.getFloat("reason"));

        this.has_guzhang=true;
        this.sensorDetails= Lists.newArrayList();
        if (code.equals(cn.turing.common.entity.lyy.Constant.CODE_ONE)){
            SensorDetail sensorDetail=new SensorDetail();
            sensorDetail.setAlarmCode(Constant.ECE);
            sensorDetail.setAlarmType(cn.turing.common.entity.lyy.Constant.CODE_REASON_ONE);
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(0);
            sensorDetails.add(sensorDetail);
        }

        if (code.equals(cn.turing.common.entity.lyy.Constant.CODE_TWO)){
            SensorDetail sensorDetail=new SensorDetail();
            sensorDetail.setAlarmCode(Constant.VOL);
            sensorDetail.setAlarmType(cn.turing.common.entity.lyy.Constant.CODE_REASON_TWO);
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(0);
            sensorDetails.add(sensorDetail);
        }

        if (code.equals(cn.turing.common.entity.lyy.Constant.CODE_FOUR)){
            SensorDetail sensorDetail=new SensorDetail();
            sensorDetail.setAlarmCode(Constant.POWER);
            sensorDetail.setAlarmType(cn.turing.common.entity.lyy.Constant.CODE_REASON_FOUR);
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(0);
            sensorDetails.add(sensorDetail);
        }
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
        jsonObject.put("status", Constant.ST_WARN);
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
