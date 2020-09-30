package cn.turing.firecontrol.server.entity.turing;
import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.firecontrol.server.util.Hex2Float;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 上传周期：60秒钟。
 * 数据格式：
 * 包头：0x80 0x80
 * 版本信息：0x00
 * 长度：0x18，包括包头、版本信息、包尾等所有字段的长度
 * 两个字节：PM1.0浓度，高位在前
 * 两个字节：PM2.5浓度，高位在前
 * 两个字节：PM10浓度，高位在前
 * 四个字节：VOC浓度，单精度浮点型float类型
 * 四个字节：温度，单精度浮点型float类型
 * 四个字节：湿度，单精度浮点型float类型
 * 包尾：0x5A 0x5A
 */
@Data
@Slf4j
public class AirMonitoringDeviceInfoV1 extends RabbitDeviceInfo {
    private Integer length;
    public AirMonitoringDeviceInfoV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        //80 80 00 18 0A 4F 4B 2B 53 45 B7 A4 9E 3E B1 5D AE 41 2B 37 80 42 5A 5A
        // 80 80 00 18 0086 009A 00C7 DD645D3F BA0EAD41 C0C08242 5A5A
        this.sensorDetails = Lists.newArrayList();
        length = Integer.parseInt(this.getData().substring(6, 8), 16);//数据长度字段
        sensorDetails.add(pm1_0(this.getData().substring(8, 12)));//pm1.0浓度
        sensorDetails.add(pm2_5(this.getData().substring(12, 16)));//PM2.5浓度
        sensorDetails.add(pm10(this.getData().substring(16, 20)));//PM10浓度
        sensorDetails.add(voc(this.getData().substring(20, 28)));//VOC浓度>异味
        sensorDetails.add(temp1(this.getData().substring(28, 36)));//温度
        sensorDetails.add(humidity(this.getData().substring(36, 44)));//湿度


    }

    public SensorDetail pm1_0(String data) {
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.PM1_0);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(Integer.parseInt(data, 16));
      //  System.out.println("pm1.0:"+Integer.parseInt(data, 16));
        return sensorDetail;
    }

    public SensorDetail pm2_5(String data) {
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.PM2_5);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(Integer.parseInt(data, 16));
      //  System.out.println("pm2.5:"+Integer.parseInt(data, 16));
        return sensorDetail;
    }

    public SensorDetail pm10(String data) {
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.PM10);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(Integer.parseInt(data, 16));
     //   System.out.println("pm10:"+Integer.parseInt(data, 16));
        return sensorDetail;
    }

    public SensorDetail voc(String data) {
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOC);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(getFloat(data));
     //   System.out.println("voc:"+getFloat(data));
        return sensorDetail;
    }

    public SensorDetail temp1(String data) {
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XTEMP);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(getFloat(data));
   //     System.out.println("temp1:"+getFloat(data));
        return sensorDetail;
    }

    public SensorDetail humidity(String data) {
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.HUM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(getFloat(data));
    //    System.out.println("humidity:"+getFloat(data));
        return sensorDetail;
    }

    public Float getFloat(String data) {
        Long l = Hex2Float.parseLong(data.substring(6) + data.substring(4, 6) + data.substring(2, 4) + data.substring(0, 2), 16);
        Float f = Float.intBitsToFloat(l.intValue());
        return f;
    }
}
