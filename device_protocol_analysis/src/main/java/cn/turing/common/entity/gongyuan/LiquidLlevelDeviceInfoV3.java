package cn.turing.common.entity.gongyuan;



import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * 液位传感器 一次上传一条一样的数据 液位传感器是6小时心跳的，10分钟发一次是因为变化超过了10%
 *version：1.1
 * 测点：YW VOL
 *
 * ，
 */

@Data
public class LiquidLlevelDeviceInfoV3 extends RabbitDeviceInfo {
    double  yw; //液位
    double  dy; //电压
    Integer length;
    public LiquidLlevelDeviceInfoV3(JSONObject jsonObject) throws Exception {
        super(jsonObject); //142205 00 0000 0DEE

        length= Integer.parseInt(this.getData().substring(4,6),16);
        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(yewei(this.getData().substring(8,12)));
        sensorDetails.add(dianya(this.getData().substring(12)));


    }
    private SensorDetail dianya(String string) {
        SensorDetail sensorDetail=new SensorDetail();
        this.dy=Integer.valueOf(string,16)*0.001;
        sensorDetail.setAlarmCode(Constant.VOL);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(dy);
        return sensorDetail;
    }

    private SensorDetail yewei(String string) {
        SensorDetail sensorDetail=new SensorDetail();
        this.yw= Integer.valueOf(Integer.parseInt(string,16))*0.001;
        if (this.getData().substring(6,8).equals(01)){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("报警");
            has_alarm=true;
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
        }
        sensorDetail.setAlarmCode(Constant.YW);
        sensorDetail.setAlarmValue(yw);
        return sensorDetail;
    }
}
