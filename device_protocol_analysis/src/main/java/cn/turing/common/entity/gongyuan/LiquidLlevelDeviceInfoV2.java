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
 * ，这个v2版本只解析中电长江的2个液位传感器（393833365B378211，3739343585367013）
 */

@Data
public class LiquidLlevelDeviceInfoV2 extends RabbitDeviceInfo {
    double  yw; //液位
    double  dy; //电压
    public LiquidLlevelDeviceInfoV2(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(yewei());
        sensorDetails.add(dianya());


    }
    //00720FE4
    private SensorDetail dianya() {
        SensorDetail sensorDetail=new SensorDetail();
        this.dy=Integer.valueOf(this.getData().substring(4),16)*0.001;
        sensorDetail.setAlarmCode(Constant.VOL);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(dy);
        return sensorDetail;
    }

    private SensorDetail yewei() {
        SensorDetail sensorDetail=new SensorDetail();
        this.yw= Integer.valueOf(Integer.parseInt(this.getData().substring(0,4),16))*0.001;
        sensorDetail.setAlarmCode(Constant.YW);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(yw);
        return sensorDetail;
    }
}
