package cn.turing.common.entity.turing;


import cn.turing.common.base.Constant;
import cn.turing.common.base.SensorExpirationTime;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * LORA-井盖
 */

@Data
public class LoraWellCoverDeviceInfoV1 extends RabbitDeviceInfo {

    private  String CMD;//指令码01表示上传
    private  String status;//1字节报警标致(01表示报警。00表示不报警)
	private  Integer angle;//角度
    private  Double vol_value;//2字节电池电压
	private  Integer sign_value;//信号强度
    public LoraWellCoverDeviceInfoV1(JSONObject jsonObject) throws Exception {
    	super(jsonObject);
        try {
            CMD = this.getData().substring(4, 6);
            if ("01".equalsIgnoreCase(CMD)){
				status = this.getData().substring(6, 8);
				angle=Integer.parseInt(this.getData().substring(8,10),16);
                vol_value = Integer.parseInt(this.getData().substring(10, 14), 16) * 0.001;
                sign_value=Integer.parseInt(this.getData().substring(14,16),16)-110;
                this.sensorDetails = Lists.newArrayList();
                sensorDetails.add(parsingjingai());
                sensorDetails.add(parsingjiaodu());
                sensorDetails.add(parsingSign());
                sensorDetails.add(parsingVol());
            }else{

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public SensorDetail parsingjingai(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.WC);
        if (status.equals("00") ){
			sensorDetail.setAlarmType("正常");
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else {
			sensorDetail.setAlarmType("报警");
			sensorDetail.setAlarmStatus(Constant.ST_ALARM);
			sensorDetail.setAlarmValue(Constant.ST_ALARM);
			this.has_alarm=true;
        }
        return  sensorDetail;
    }

	public SensorDetail parsingjiaodu(){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.ANGLE);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(SensorExpirationTime.df.format(angle));

		return sensorDetail;
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
