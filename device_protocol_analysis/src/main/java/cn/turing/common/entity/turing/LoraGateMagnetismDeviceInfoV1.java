package cn.turing.common.entity.turing;


import cn.turing.common.base.Constant;
import cn.turing.common.base.SensorExpirationTime;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * LORA-门磁
 */

@Data
public class LoraGateMagnetismDeviceInfoV1 extends RabbitDeviceInfo {

    private  String CMD;//指令码01表示上传
    private  String left_status;//1字节左门状态(01表打开。00表示关闭)
	private  String right_status;//1字节右门状态(01表打开。00表示关闭)
    private  Double vol_value;//2字节电池电压
	private  Integer sign_value;//信号强度
    public LoraGateMagnetismDeviceInfoV1(JSONObject jsonObject) throws Exception {
    	super(jsonObject);
        try {
            CMD = this.getData().substring(4, 6);
            if ("01".equalsIgnoreCase(CMD)){
				left_status = this.getData().substring(6, 8);
				right_status=this.getData().substring(8, 10);
                vol_value = Integer.parseInt(this.getData().substring(10, 14), 16) * 0.001;
                sign_value=Integer.parseInt(this.getData().substring(14,16),16)-110;
                this.sensorDetails = Lists.newArrayList();
                sensorDetails.add(parsingmenci());
                sensorDetails.add(parsingSign());
                sensorDetails.add(parsingVol());
            }else{

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public SensorDetail parsingmenci(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.DM);
        if (left_status.equals("00") && right_status.equals("00")){
			sensorDetail.setAlarmType("关闭");
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else {
			sensorDetail.setAlarmType("门开");
			sensorDetail.setAlarmStatus(Constant.ST_ALARM);
			sensorDetail.setAlarmValue(Constant.ST_ALARM);
			this.has_alarm=true;
        }
        return  sensorDetail;
    }

    public SensorDetail parsingSign(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.SIGN);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(SensorExpirationTime.decimaFormat(sign_value.doubleValue()));

        return sensorDetail;
    }


    public SensorDetail parsingVol(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOL);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(SensorExpirationTime.decimaFormat(vol_value));

        return sensorDetail;
    }

}
