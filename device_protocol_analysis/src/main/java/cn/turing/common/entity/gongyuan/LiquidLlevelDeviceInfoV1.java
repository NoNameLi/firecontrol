package cn.turing.common.entity.gongyuan;



import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * 液位传感器 一次上传两条一样的数据 10分钟上传一次
 *
 * 测点：YW VOL
 */

@Data
public class LiquidLlevelDeviceInfoV1 extends RabbitDeviceInfo {
    double  yw; //液位
    double  dy; //电压
    public LiquidLlevelDeviceInfoV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(yewei());
        sensorDetails.add(dianya());


    }

    private SensorDetail dianya() {
        SensorDetail sensorDetail=new SensorDetail();
        this.dy=Integer.valueOf(this.getData().substring(14)+this.getData().substring(12,14),16)*0.001;
        sensorDetail.setAlarmCode(Constant.VOL);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(dy);
        return sensorDetail;
    }

    private SensorDetail yewei() {
        SensorDetail sensorDetail=new SensorDetail();
        this.yw= Integer.valueOf(this.getData().substring(10,12)+this.getData().substring(8,10),16)*0.001;
        sensorDetail.setAlarmCode(Constant.YW);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(yw);
        return sensorDetail;
    }
}
