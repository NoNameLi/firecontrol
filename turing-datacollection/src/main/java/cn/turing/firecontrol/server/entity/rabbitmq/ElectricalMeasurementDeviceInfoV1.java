package cn.turing.firecontrol.server.entity.rabbitmq;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 电测 15分钟上传一次
 * 测点：VOL ECE POWER POWYZ VOLF POWDN
 */
@Slf4j
@Data
public class ElectricalMeasurementDeviceInfoV1 extends RabbitDeviceInfo {
    double vole;
    double ecaa;
    double powera;
    double yz;
    double hz;
    double pw;
    public ElectricalMeasurementDeviceInfoV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        this.sensorDetails= Lists.newArrayList();
        log.info("开始解析电测数据");
        //0400 13 01 30 00005605 000000FA 000014CA 03C7 1386 0000573D
        if (this.getData().substring(4,6).equals("13")  && this.getData().substring(8,10).equals("30")){
            sensorDetails.add(dianya1(Constant.VOL));
            sensorDetails.add(dianliu(Constant.ECE));
            sensorDetails.add(gonglv(Constant.POWER));
            sensorDetails.add(gonglvyz(Constant.POWYZ));
            sensorDetails.add(dianyapinglv(Constant.VOLF));
            sensorDetails.add(yougongdianneng(Constant.POWDN));

        }else{
            log.info("解析出错");
        }
    }

    public SensorDetail yougongdianneng(String powdn) {
        SensorDetail sensorDetail=new SensorDetail();
        pw =Integer.valueOf(this.getData().substring(42),16)*0.01;
        sensorDetail.setAlarmCode(powdn);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(pw);
        return  sensorDetail;
    }

    public SensorDetail gonglvyz(String powyz) {
        SensorDetail sensorDetail=new SensorDetail();
        yz =Integer.valueOf(this.getData().substring(34,38),16)*0.001;
        sensorDetail.setAlarmCode(powyz);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(yz);
        return  sensorDetail;
    }

    public SensorDetail dianyapinglv(String volf) {
        SensorDetail sensorDetail=new SensorDetail();
        hz =Integer.valueOf(this.getData().substring(38,42),16)*0.01;
        sensorDetail.setAlarmCode(volf);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(hz);
        return  sensorDetail;
    }

    public SensorDetail gonglv(String power) {
        SensorDetail sensorDetail=new SensorDetail();
        powera =Integer.valueOf(this.getData().substring(26,34),16)*0.01;
        sensorDetail.setAlarmCode(power);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(powera);
        return  sensorDetail;
    }

    public SensorDetail dianliu(String eca) {
        SensorDetail sensorDetail=new SensorDetail();
        ecaa =Integer.valueOf(this.getData().substring(18,26),16)*0.001;
        sensorDetail.setAlarmCode(eca);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(ecaa);
        return  sensorDetail;
    }

    public SensorDetail dianya1(String vol) {
        SensorDetail sensorDetail=new SensorDetail();
        vole =Integer.valueOf(this.getData().substring(10,18),16)*0.01;
        sensorDetail.setAlarmCode(vol);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(vole);
        return  sensorDetail;
    }
}
