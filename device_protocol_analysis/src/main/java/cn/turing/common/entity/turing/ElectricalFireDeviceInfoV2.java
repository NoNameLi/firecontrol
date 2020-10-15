package cn.turing.common.entity.turing;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * turing自研测温电气火灾传感器
 */
@Data

public class ElectricalFireDeviceInfoV2 extends RabbitDeviceInfo {
    private String stat_a;//A相温度标识（正或者负）
    private String stat_b;//B相温度标识（正或者负）
    private String stat_c;//C相温度标识（正或者负）
    private Integer temp_a;//温度值
    private Integer temp_b;
    private Integer temp_c;
    private Integer vol;
    public ElectricalFireDeviceInfoV2(JSONObject jsonObject) throws Exception {
        //B0B0 01 00 0116 00 00F0 00 00F9 0000 0361
        super(jsonObject);
        String cmd=this.getData().substring(4,6);
        if (cmd.equals("01")){//01表示设备上行数据，02表示下行数据（云端下发指令）
            stat_a=this.getData().substring(6,8);
            temp_a=Integer.parseInt(this.getData().substring(8,12),16);
            stat_b=this.getData().substring(12,14);
            temp_b=Integer.parseInt(this.getData().substring(14,18),16);
            stat_c=this.getData().substring(18,20);
            temp_c=Integer.parseInt(this.getData().substring(20,24),16);
            vol=Integer.parseInt(this.getData().substring(24,28),16);

            this.sensorDetails= Lists.newArrayList();
            sensorDetails.add(parsingTempA());
            sensorDetails.add(parsingTempB());
            sensorDetails.add(parsingTempC());
            sensorDetails.add(parsingVol());
        }
    }
    public SensorDetail parsingTempA(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMP1);
        if (temp_a*0.1>45){
            this.has_alarm=true;
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
        }else{
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
        if (stat_a.equals("00")){
            sensorDetail.setAlarmValue(temp_a*0.1);
        }else{
            sensorDetail.setAlarmValue(-temp_a*0.1);
        }


        return sensorDetail;
    }
    public SensorDetail parsingTempB(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMP2);
        if (temp_b*0.1>45){
            this.has_alarm=true;
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
        }else{
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
        if (stat_b.equals("00")){
            sensorDetail.setAlarmValue(temp_b*0.1);
        }else{
            sensorDetail.setAlarmValue(-temp_b*0.1);
        }

        return sensorDetail;
    }
    public SensorDetail parsingTempC(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMP3);
        if (temp_c*0.1>45){
            this.has_alarm=true;
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
        }else{
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
        if (stat_c.equals("00")){
            sensorDetail.setAlarmValue(temp_c*0.1);
        }else{
            sensorDetail.setAlarmValue(-temp_c*0.1);
        }

        return sensorDetail;
    }
    public SensorDetail parsingVol(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECA);
        if (vol>500){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("报警");
            this.has_alarm=true;
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
        }
        sensorDetail.setAlarmValue(vol*0.1);
        return sensorDetail;
    }
}
