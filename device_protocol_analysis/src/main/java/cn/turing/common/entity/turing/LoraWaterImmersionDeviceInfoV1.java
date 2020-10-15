package cn.turing.common.entity.turing;


import cn.turing.common.base.Constant;
import cn.turing.common.base.SensorExpirationTime;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * LORA-水浸
 */

@Data
public class LoraWaterImmersionDeviceInfoV1 extends RabbitDeviceInfo {

    private  String CMD;//指令码01表示上传
    private  String status;//1字节水状态(01表示无水。00表示有水)
    private  Double vol_value;//2字节电池电压
	private  Integer sign_value;//信号强度
    public LoraWaterImmersionDeviceInfoV1(JSONObject jsonObject) throws Exception {
    	super(jsonObject);
        try {
            CMD = this.getData().substring(4, 6);
            if ("01".equalsIgnoreCase(CMD)){
				status = this.getData().substring(6, 8);
                vol_value = Integer.parseInt(this.getData().substring(8, 12), 16) * 0.001;
                sign_value=Integer.parseInt(this.getData().substring(12,14),16)-110;
                this.sensorDetails = Lists.newArrayList();
                sensorDetails.add(parsingshuijin());
                sensorDetails.add(parsingSign());
                sensorDetails.add(parsingVol());
            }else{

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public SensorDetail parsingshuijin(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.WF);
        if (status.equals("00")){
            sensorDetail.setAlarmType("有水");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            this.has_alarm=true;
        }else if (status.equals("01")){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
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
