package cn.turing.common.entity.turing;

/**
 * 可燃气体-LORA
 *
 * TR-18GD01L
 */

import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

@Data
public class CombustibleGasDeviceInfoV2 extends RabbitDeviceInfo {
    /**
     * 信号位 --00 表示开机，04 表示按键自检，03 表示周期数据，01 报警，02 解除报 警
     */
    private String cmd;
    //序号
    private String serialNumber;
    //后台分析不做展示
    private String vol;

    public CombustibleGasDeviceInfoV2(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        vol=String.valueOf(Integer.parseInt(this.getData().substring(6,10),16)*0.001);
        serialNumber=this.getData().substring(10,12);
        cmd=this.getData().substring(12);
        this.sensorDetails= Lists.newArrayList();
        this.sensorDetails.add(parsingCmd());
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
