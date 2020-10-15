package cn.turing.common.entity.gongyuan;



import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * 躬远设备。门磁、水浸、红外、感烟、
 */

@Data
public class GongYuanDeviceinfoV1 extends RabbitDeviceInfo {
    private String deviceType;



    public GongYuanDeviceinfoV1(JSONObject jsonObject, String codeName, Integer doorType) throws Exception {
        super(jsonObject);
        this.sensorDetails= Lists.newArrayList();
       try{
           String str1=this.getData().substring(4,6);
            sensorDetails.add(parsePolice(str1,codeName,doorType));//报警状态
            String reg= ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(this.getData().substring(6,8),16)));
            sensorDetails.add(parseDianChi(reg));//电池状态
            sensorDetails.add(parseFangChai(reg));//防拆
           if (!codeName.equals("DM")){
               if (str1.equals("00")){
                   sensorDetails.add(parseDevice(reg,codeName));//传感器状态
               }
               sensorDetails.add(parseAnJian(reg));//按键
           }

        }catch (Exception e){
            e.printStackTrace();

        }
    }
    private SensorDetail parseAnJian(String reg) {
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.AJZT);//按键状态 是不是只有烟感
        if (reg.substring(5).equals("000")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else if (reg.substring(5).equals("001")){
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("测试");
            sensorDetail.setAlarmValue(Constant.ST_WARN);
        }else if (reg.substring(5).equals("010")){
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("消音");
            sensorDetail.setAlarmValue(Constant.ST_WARN);
        }

        return sensorDetail;
    }

    private SensorDetail parseFangChai(String reg) {
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XFC); //防拆
        if (reg.substring(4,5).equals("0")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else if (reg.substring(4,5).equals("1")){
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("被拆");
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang=true;
        }

        return sensorDetail;
    }

    private SensorDetail parseDianChi(String reg) {
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.DL);//电量
        if (reg.substring(2,4).equals("00")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else if (reg.substring(2,4).equals("01")){
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("低电");
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang=true;
        }
        return  sensorDetail;
    }

    private SensorDetail parseDevice(String reg,String code) {
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(code);
        if (reg.substring(0,2).equals("00")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else if (reg.substring(0,2).equals("01")){
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("故障");
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang=true;
        }
         return sensorDetail;
    }

    private SensorDetail parsePolice(String input, String code,Integer doorType) {
        SensorDetail sensorDetail=new SensorDetail();
        if (input.equals("00")){
            if (code.equals("DM")){
                if (doorType==1){//长开门
                    has_alarm=true;
                    sensorDetail.setAlarmCode(code);
                    sensorDetail.setAlarmType("门磁报警");
                    sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                    sensorDetail.setAlarmValue(Constant.ST_ALARM);
                }else if (doorType==2){//常闭门
                    sensorDetail.setAlarmCode(code);
                    sensorDetail.setAlarmType("正常");
                    sensorDetail.setAlarmStatus(Constant.ST_NORM);
                    sensorDetail.setAlarmValue(Constant.ST_NORM);
                }else{//0表示未找到防火门
                    System.out.println("不是门磁");
                }
            }else{
                sensorDetail.setAlarmCode(code);
                sensorDetail.setAlarmType("正常");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);
            }

        }else{
            if (input.equals("01")){
                sensorDetail.setAlarmCode("INFR");//测点
                sensorDetail.setAlarmType("红外报警");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                sensorDetail.setAlarmValue(Constant.ST_ALARM);
                has_alarm=true;
            }else if (input.equals("03")){
                if (doorType==1){//长开门
                    sensorDetail.setAlarmCode(Constant.DM);
                    sensorDetail.setAlarmType("正常");
                    sensorDetail.setAlarmStatus(Constant.ST_NORM);
                    sensorDetail.setAlarmValue(Constant.ST_NORM);
                }else if (doorType==2){//常闭门
                    has_alarm=true;
                    sensorDetail.setAlarmCode(Constant.DM);//测点
                    sensorDetail.setAlarmType("门磁报警");
                    sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                    sensorDetail.setAlarmValue(Constant.ST_ALARM);
                }else{//0表示未找到防火门
                    System.out.println("不是门磁");
                }
            }else if (input.equals("04")){
                has_alarm=true;
                sensorDetail.setAlarmCode(Constant.YG);//测点
                sensorDetail.setAlarmType("烟感报警");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                sensorDetail.setAlarmValue(Constant.ST_ALARM);

            }else if (input.equals("06")){
                has_alarm=true;
                sensorDetail.setAlarmCode(Constant.WF);//测点
                sensorDetail.setAlarmType("漏水");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                sensorDetail.setAlarmValue(Constant.ST_ALARM);
            }

        }
        return sensorDetail;
    }

}
