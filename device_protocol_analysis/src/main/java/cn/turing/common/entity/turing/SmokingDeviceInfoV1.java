package cn.turing.common.entity.turing;


import cn.turing.common.base.Constant;
import cn.turing.common.base.SensorExpirationTime;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;


@Data
public class SmokingDeviceInfoV1 extends RabbitDeviceInfo {

    private  String CMD;//指令码
    private  String smoke_status;//烟雾报警指令码 01：报警 00正常
    private  String temp_status;//正或者负 01:负  00：正
    private  Double temp_value;//温度值
    private  Double vol_value;//电压值 v
    public SmokingDeviceInfoV1(JSONObject jsonObject) throws Exception {
            super(jsonObject);
        try {
            CMD = this.getData().substring(4, 6);
            if ("01".equalsIgnoreCase(CMD)){
                smoke_status = this.getData().substring(6, 8);
                temp_status = this.getData().substring(8, 10);
                temp_value = Integer.parseInt(this.getData().substring(10, 14), 16) * 0.1;
                vol_value = Integer.parseInt(this.getData().substring(14, 18), 16) * 0.001;
                this.sensorDetails = Lists.newArrayList();
                sensorDetails.add(parsingSmoke());
                sensorDetails.add(parsingTemp());
                sensorDetails.add(parsingVol());
            }else{

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public SensorDetail parsingSmoke(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.YG);
        if (smoke_status.equals("01")){
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            this.has_alarm=true;
        }else if (smoke_status.equals("00")){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return  sensorDetail;
    }

    public SensorDetail parsingTemp(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XTEMP);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        if (temp_status.equals("00")){
            sensorDetail.setAlarmValue(SensorExpirationTime.df.format(temp_value));
        }else if (temp_status.equals("01")){
            sensorDetail.setAlarmValue(SensorExpirationTime.df.format(-temp_value));
        }
        return sensorDetail;
    }


    public SensorDetail parsingVol(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOL);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(SensorExpirationTime.df.format(vol_value));

        return sensorDetail;
    }
}
