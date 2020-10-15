package cn.turing.common.entity.turing;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.Hex2Float;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

import java.math.BigDecimal;

/**
 * NB空气
 */
@Data
public class AirMonitoringDeviceInfoV2 extends RabbitDeviceInfo {

    private Integer pm1_0;

    private Integer pm2_5;

    private Integer pm10;

    private Double voc;

    private Double temp;

    private Double hum;

    private String cmd;

    private String sign;

    public AirMonitoringDeviceInfoV2(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        this.sensorDetails = Lists.newArrayList();
        pm1_0=Integer.parseInt(this.getData().substring(26,30),16);
        pm2_5=Integer.parseInt(this.getData().substring(30,34),16);
        pm10=Integer.parseInt(this.getData().substring(34,38),16);
        voc=Integer.parseInt(this.getData().substring(38,42),16)*0.01;
        temp=Integer.parseInt(this.getData().substring(42,46),16)*0.01-100;
        hum=Integer.parseInt(this.getData().substring(46,50),16)*0.01;
        cmd=this.getData().substring(50,52);
        sign=this.getData().substring(52);
        sensorDetails.add(pm1_0());//pm1.0浓度
        sensorDetails.add(pm2_5());//PM2.5浓度
        sensorDetails.add(pm10());//PM10浓度
        sensorDetails.add(voc());//VOC浓度>异味
        sensorDetails.add(temp1());//温度
        sensorDetails.add(humidity());//湿度
        sensorDetails.add(paringSign());


    }

    public SensorDetail paringSign(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.SIGN);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(Integer.parseInt(sign,16)-110);
        return  sensorDetail;
    }

    public SensorDetail pm1_0() {
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.PM1_0);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(pm1_0);
        return sensorDetail;
    }

    public SensorDetail pm2_5() {
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.PM2_5);
        if (cmd.equals("01")){
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            has_alarm=true;
        }
        else{
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
        sensorDetail.setAlarmValue(pm2_5);
      //  System.out.println("pm2.5:"+Integer.parseInt(data, 16));
        return sensorDetail;
    }

    public SensorDetail pm10() {
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.PM10);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(pm10);
     //   System.out.println("pm10:"+Integer.parseInt(data, 16));
        return sensorDetail;
    }

    public SensorDetail voc() {
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOC);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(voc);
        return sensorDetail;
    }

    public SensorDetail temp1() {
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XTEMP);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        BigDecimal bg = new BigDecimal(temp).setScale(2, BigDecimal.ROUND_HALF_UP);
        sensorDetail.setAlarmValue(bg.doubleValue());
   //     System.out.println("temp1:"+getFloat(data));
        return sensorDetail;
    }

    public SensorDetail humidity() {
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.HUM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(hum);
    //    System.out.println("humidity:"+getFloat(data));
        return sensorDetail;
    }


}
