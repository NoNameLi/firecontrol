package cn.turing.firecontrol.server.entity.rabbitmq;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.firecontrol.server.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 消防头盔
 * 位置数据：3min左右
 * 传感器数据：2min左右
 * SOS：1min左右
 *    经度，纬度暂时没有用（EAST，WEST，SOUTH，NORTH）原因：高层，室内gps信号不好，有偏差。室内相差小。大约10米左右。
 * //30+30.4751/60
 * 114+25.0203/60
 * 然后转成百度的坐标
 * 测点：XTEMP HUM  AIRP VOL FFH
 */

@Slf4j
@Data
public class XiaoFangTouKuiDeviceInfoV1 extends RabbitDeviceInfo {
    String cmmd;

    public XiaoFangTouKuiDeviceInfoV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        log.info("解析消防头盔");
        this.sensorDetails= Lists.newArrayList();
        this.cmmd=this.getData().substring(2,4);
        if (cmmd.equals("06")){ //传感器数据上报

            sensorDetails.add(wendu(Constant.XTEMP));
            sensorDetails.add(shidu(Constant.HUM));
            sensorDetails.add(qiya(Constant.AIRP));
            //导电率不做处理
            sensorDetails.add(dianya(Constant.VOL));
        }else if (cmmd.equals("04")){
            sensorDetails.add(jingbao(Constant.FFH));
        }else if (cmmd.equals("05")){
            String s= this.getData().substring(4,6);
            if (s.equals("00")){
                log.info("定位数据无效");
            }else if (s.equals("01")){
                log.info("定位数据有效");
                if (this.getData().substring(6,8).equals("01")){
                    sensorDetails.add(beiweidu(Constant.NORTH));
                }else if (this.getData().substring(6,8).equals("02")){
                    sensorDetails.add(nanweidu(Constant.SOUTH));
                }
                if (this.getData().substring(16,18).equals("01")){
                    sensorDetails.add(dongjingdu(Constant.EAST));
                }else if (this.getData().substring(16,18).equals("02")){
                    sensorDetails.add(xijingdu(Constant.WEST));
                }


            }
        }else{
            log.error("其他状态不作处理");
        }

    }

    private SensorDetail xijingdu(String str) {
        SensorDetail sensorDetail=new SensorDetail();
        String s=  Integer.valueOf(this.getData().substring(18,22),16).toString();
        String d=  Integer.valueOf(this.getData().substring(22,26),16).toString();
        String weidu=s+"."+d;
        Double aDouble=0.0;
        if (s.length()>=5){
            aDouble=Double.parseDouble(weidu.substring(0,3))+Double.parseDouble(weidu.substring(2))/60;
        }else{
            aDouble=Double.parseDouble(weidu.substring(0,2))+Double.parseDouble(weidu.substring(2))/60;
        }
        sensorDetail.setAlarmCode(str);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(aDouble);

        return sensorDetail;
    }

    private SensorDetail dongjingdu(String str) {
        SensorDetail sensorDetail=new SensorDetail();
        String s=  Integer.valueOf(this.getData().substring(18,22),16).toString();
        String d=  Integer.valueOf(this.getData().substring(22,26),16).toString();
        String weidu=s+"."+d;
        Double aDouble=0.0;
        if (s.length()>=5){
           aDouble=Double.parseDouble(weidu.substring(0,3))+Double.parseDouble(weidu.substring(2))/60;
        }else{
            aDouble=Double.parseDouble(weidu.substring(0,2))+Double.parseDouble(weidu.substring(2))/60;
        }

        sensorDetail.setAlarmCode(str);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(aDouble);

        return sensorDetail;
    }

    private SensorDetail nanweidu(String str) {
        SensorDetail sensorDetail=new SensorDetail();
        String s=  Integer.valueOf(this.getData().substring(8,12),16).toString();
        String d=  Integer.valueOf(this.getData().substring(12,16),16).toString();
        String weidu=s+"."+d;
        Double aDouble=Double.parseDouble(weidu.substring(0,2))+Double.parseDouble(weidu.substring(2))/60;
        sensorDetail.setAlarmCode(str);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(aDouble);

        return sensorDetail;
    }

    private SensorDetail beiweidu(String str) {
        SensorDetail sensorDetail=new SensorDetail();
        //A5 05 01 01 0B D6 10 97 01 2C A1 05 65
        //3030.4214,11425.1406  30.507023333  114.41901
   String s=  Integer.valueOf(this.getData().substring(8,12),16).toString();
   String d=  Integer.valueOf(this.getData().substring(12,16),16).toString();
   String weidu=s+"."+d;
  Double aDouble=Double.parseDouble(weidu.substring(0,2))+Double.parseDouble(weidu.substring(2))/60;

        sensorDetail.setAlarmCode(str);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(aDouble);

        return sensorDetail;
    }

    private SensorDetail jingbao(String ffh) {
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(ffh);
        sensorDetail.setAlarmType("警报");
        sensorDetail.setAlarmStatus(Constant.ST_ALARM);
        sensorDetail.setAlarmValue(Constant.ST_ALARM);
        has_alarm=true;
        return  sensorDetail;
    }

    private SensorDetail dianya(String vol) {
        //A50617291E0400018D0E20C80900002E0900000DB6
        SensorDetail sensorDetail=new SensorDetail();
        Double dy=((Integer.valueOf(this.getData().substring(38),16))*0.001);
        BigDecimal b = new BigDecimal(dy);
        dy = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        sensorDetail.setAlarmCode(vol);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(dy);  //mv
        return sensorDetail;
    }

    private SensorDetail qiya(String airp) {
        SensorDetail sensorDetail=new SensorDetail();
        Integer sd=    Integer.valueOf(this.getData().substring(12,13),16)+Integer.valueOf(this.getData().substring(14,16),16)+
                    Integer.valueOf(this.getData().substring(16,18),16)+Integer.valueOf(this.getData().substring(19,20),16);

        String string= sd.toString()+"."+
                Integer.valueOf(this.getData().substring(20,22),16).toString();
        sensorDetail.setAlarmCode(airp);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(ByteUtil.quling(string));
        return sensorDetail;

    }

    private SensorDetail shidu(String hum) {
        String string= Integer.valueOf(this.getData().substring(8,10),16).toString()+ "."+
                Integer.valueOf(this.getData().substring(10,12),16).toString();
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(hum);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(string);
        return sensorDetail;
    }

    private SensorDetail wendu(String xtemp) {
        //A506 1729 1E04 00018D0E20 C80900002E090000 0DB6
      Integer integer= Integer.valueOf(this.getData().substring(4,6),16);
        String string= integer.toString()+"."+
                    Integer.valueOf(this.getData().substring(6,8),16).toString();
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(xtemp);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(string);
        return sensorDetail;
    }



}
