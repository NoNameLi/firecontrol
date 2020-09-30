package cn.turing.firecontrol.server.entity.rabbitmq;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 井盖     电压 单位 mv
 */
@Slf4j
@Data
public class WellCoverDeviceInfoV1 extends RabbitDeviceInfo {
    double vole;

    public WellCoverDeviceInfoV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        this.sensorDetails= Lists.newArrayList();
        log.info("开始解析井盖传感器数据");
        if ((this.getData().substring(2,4)+this.getData().substring(0,2)).equals("0000")){
            SensorDetail sensorDetail=new SensorDetail();
            sensorDetail.setAlarmCode(Constant.WC);
            sensorDetail.setAlarmType("井盖被打开");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            sensorDetails.add(sensorDetail);
        }else{
            SensorDetail sensorDetail=new SensorDetail();
            sensorDetail.setAlarmCode(Constant.WC);
            sensorDetail.setAlarmType("井盖闭合");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
            has_alarm=true;
            sensorDetails.add(sensorDetail);

        }
        this.vole=Integer.valueOf(this.getData().substring(6)+this.getData().substring(4,6),16);
        SensorDetail sensorDetail=new SensorDetail();
            sensorDetail.setAlarmCode(Constant.VOL);
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(vole);
        sensorDetails.add(sensorDetail);
    }
}
