package cn.turing.common.entity.turing;

/**
 * 可燃气体-NB
 */

import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

@Data
public class CombustibleGasDeviceInfoV1 extends RabbitDeviceInfo {
    /**
     * 信号位 --00 表示开机，04 表示按键自检，03 表示周期数据，01 报警，02 解除报 警
     */
    private String cmd;
    /**
     * 信号强度
     */
    private String sign;

    private String reserve;


    public CombustibleGasDeviceInfoV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        reserve=this.getData().substring(26,30);
        cmd=this.getData().substring(30,32);
        sign=this.getData().substring(32,34);
        this.sensorDetails= Lists.newArrayList();
        this.sensorDetails.add(parsingCmd());
        this.sensorDetails.add(paringSign());
    }

    public SensorDetail paringSign(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.SIGN);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(Integer.parseInt(sign,16)-110);
        return  sensorDetail;
    }

    public SensorDetail parsingCmd(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.COMG);
        if (cmd.equals("00")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("开机");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        if (cmd.equals("04")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("按键自检");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        if (cmd.equals("03")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("周期上传");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        if (cmd.equals("01")){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            this.has_alarm=true;
        }
        if (cmd.equals("02")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("解除报警");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }

        return sensorDetail;
    }
}
