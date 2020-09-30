package cn.turing.firecontrol.server.entity.sedwellWille;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.firecontrol.server.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 烟感 4%低压  心跳24小时（YG,XELE）... 可燃气体(COMG)
 */
@Slf4j
@Data
public class YanGanDeviceV1 extends RabbitDeviceInfo {
    public String police;
    public String onlineType;
    public YanGanDeviceV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        //22115A300043
        log.info("开始解析16进制数据");
        String data=jsonObject.getString("data");
        police=data.substring(2,4);
        String type= this.getPolice().substring(0,1);
        onlineType=this.getPolice().substring(1);
        if (ByteUtil.HexadecimalSumCheck(data)){
            this.sensorDetails= Lists.newArrayList();
            if (type.equals("1")){//烟感
                sensorDetails.add(parsePolice(data));
                sensorDetails.add(parseDianliang(data));
            }
            if (type.equals("4")){//可燃气体
                sensorDetails.add(parsePoliceGas(data));
            }

        }else{
            log.error("数据校验失败...");
        }

    }

    private SensorDetail parseDianliang(String data) {
        SensorDetail sensorDetail=new SensorDetail();
        if (onlineType.equals("7")){//上电、正常
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
        }else if (onlineType.equals("4")){//低压
            has_guzhang=true;
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("低压");
        }
            sensorDetail.setAlarmCode(Constant.XELE);
            sensorDetail.setAlarmValue(Integer.parseInt(data.substring(4,6),16));
        return  sensorDetail;
    }

    public SensorDetail parsePolice(String data){
        SensorDetail sensorDetail=new SensorDetail();

        if (this.getPolice().equals("11")){//报警
            has_alarm=true;
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("报警");
        }else if (this.getPolice().equals("17")){//上电、正常
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
        }else if (this.getPolice().equals("12")){//静音
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("静音");
        }else if (this.getPolice().equals("13")){//故障
            has_guzhang=true;
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("故障");
        }
        sensorDetail.setAlarmCode(Constant.YG);
        sensorDetail.setAlarmValue(Integer.parseInt(data.substring(6,8),16));

        return sensorDetail;
    }
    public SensorDetail parsePoliceGas(String data){
            SensorDetail sensorDetail=new SensorDetail();

        if (this.getPolice().equals("41")){//报警
            has_alarm=true;
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("报警");
        }else if (this.getPolice().equals("47")){//上电、正常
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
        }else if (this.getPolice().equals("42")){//静音
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("静音");
        }else if (this.getPolice().equals("43")){//故障
            has_guzhang=true;
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("故障");
        }
        sensorDetail.setAlarmCode(Constant.COMG);
        sensorDetail.setAlarmValue(Integer.parseInt(data.substring(6,8),16));

            return sensorDetail;
    }

}
