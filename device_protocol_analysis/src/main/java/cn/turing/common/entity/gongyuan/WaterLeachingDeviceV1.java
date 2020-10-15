package cn.turing.common.entity.gongyuan;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;


/**
 * 嘉德-LoraWAN水浸传感器
 */
@Data
public class WaterLeachingDeviceV1 extends RabbitDeviceInfo {
    /**
     * 报警状态 00:正常 06;报警
     */
    private String call_police;
    /**
     * 传感器状态 00:正常 01：故障
     */
    private String sensor_status;
    /**
     * 电池状态00:正常 01：故障
     */
    private String vol_status;
    /**
     * 防拆状态 00:正常 01：故障
     */
    private String tamper_status;
    /**
     * 按钮状态 000:正常 001：测试按键
     */
    private String button_status;

    /**
     * FA 02 00 50 3C D0 F5
     * @param jsonObject
     * @throws Exception
     */
    public WaterLeachingDeviceV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        this.sensorDetails= Lists.newArrayList();
        call_police=this.getData().substring(4,6);
        String message= ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(this.getData().substring(6,8),16)));
        sensor_status=message.substring(0,2);
        vol_status=message.substring(2,4);
        tamper_status=message.substring(4,5);
        button_status=message.substring(5);
        sensorDetails.add(police());
        sensorDetails.add(vol());
        sensorDetails.add(tamper());
        sensorDetails.add(button());
    }

    public SensorDetail police(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.WF);
        if (call_police.equals("06")){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("水浸报警");
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            has_alarm=true;
        }else{
            if (sensor_status.equals("01")){
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                sensorDetail.setAlarmType("故障");
                sensorDetail.setAlarmValue(Constant.ST_WARN);
                has_guzhang=true;
            }else{
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmType("正常");
                sensorDetail.setAlarmValue(Constant.ST_NORM);
            }

        }
        return  sensorDetail;
    }

    public SensorDetail vol(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XELE);
        if (vol_status.equals("01")){
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("电池电量低");
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang=true;
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return sensorDetail;
    }

    public SensorDetail tamper(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XFC);
        if (tamper_status.equals("01")){
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("被拆");
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang=true;
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return sensorDetail;
    }

    public SensorDetail button(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.AJZT);
        if (button_status.equals("01")){
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("测试按键");
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang=true;
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return sensorDetail;
    }

}
