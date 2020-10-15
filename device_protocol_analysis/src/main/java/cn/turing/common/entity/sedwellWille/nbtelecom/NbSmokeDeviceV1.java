package cn.turing.common.entity.sedwellWille.nbtelecom;

import cn.turing.common.base.Constant;
import cn.turing.common.constant.NbTelecomConstant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

/**
 * 电信-AEP物联网平台-赛特威尔-烟感-文档都在AEP平台上
 */
public class NbSmokeDeviceV1 extends RabbitDeviceInfo {



	public NbSmokeDeviceV1(JSONObject jsonObject) throws Exception {
		super(jsonObject);
		//电信原始数据   转成TMC平台格式
		JSONObject data=JSONObject.parseObject(this.getData());
		//消息类型
		String messageType=data.getString("messageType");

		this.sensorDetails= Lists.newArrayList();

		//烟感有2种数据上报
		if (NbTelecomConstant.dataReport.equals(messageType)){
			JSONObject payload=data.getJSONObject("payload");
			//1.烟感报警上报 两个测点  烟感  -电池电量
			if (data.toJSONString().contains("smoke_state") & data.toJSONString().contains("cell_ID")){
				sensorDetails.add(getyg(payload));
				sensorDetails.add(getVol(payload));
			}
			//2.心跳  电量
			if (data.toJSONString().contains("heartbeat_time")){
				sensorDetails.add(getVol(payload));
			}

		}
		//烟感3种事件上报
		if (NbTelecomConstant.eventReport.equals(messageType)){
			JSONObject payload=data.getJSONObject("eventContent");
			//1.防拆
			if (data.toJSONString().contains("tamper_alarm")){
				sensorDetails.add(getVol(payload));
				sensorDetails.add(getFangchai(payload));
			}
			//2.故障
			if (data.toJSONString().contains("error")){
				sensorDetails.add(getygFalut(payload));
			}
			//3.烟雾监测状态
			if (data.toJSONString().contains("smoke_state")){
				sensorDetails.add(getyg(payload));
			}
		}
	}
	public SensorDetail getygFalut(JSONObject payload){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.YG);

		if (payload.getInteger("error")==1){
			sensorDetail.setAlarmStatus(Constant.ST_WARN);
			sensorDetail.setAlarmType("烟感故障");
			sensorDetail.setAlarmValue(Constant.ST_WARN);
			has_guzhang=true;
		}else if(payload.getInteger("error")==2){
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmType("烟感故障解除");
			sensorDetail.setAlarmValue(Constant.ST_NORM);
		}else if(payload.getInteger("error")==3){
			sensorDetail.setAlarmStatus(Constant.ST_WARN);
			sensorDetail.setAlarmType("低压故障");
			sensorDetail.setAlarmValue(Constant.ST_WARN);
			has_guzhang=true;
		}else if(payload.getInteger("error")==4){
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmType("低压故障解除");
			sensorDetail.setAlarmValue(Constant.ST_NORM);
		}

		return sensorDetail;
	}
	public SensorDetail getyg(JSONObject payload){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.YG);

		if (payload.getInteger("smoke_state")==0){
			sensorDetail.setAlarmStatus(Constant.ST_ALARM);
			sensorDetail.setAlarmType("烟感报警");
			has_alarm=true;
		}else{
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmType("烟感报警解除");
		}

		sensorDetail.setAlarmValue(payload.getInteger("smoke_value"));

		return sensorDetail;
	}
	public SensorDetail getVol(JSONObject payload){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.XELE);

		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(payload.getInteger("battery_value"));

		return sensorDetail;
	}

	public SensorDetail getFangchai(JSONObject payload){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.XFC);
		if (payload.getInteger("tamper_alarm")==0){
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmType("设备装上");
			sensorDetail.setAlarmValue(Constant.ST_NORM);
		}else{
			sensorDetail.setAlarmStatus(Constant.ST_ALARM);
			sensorDetail.setAlarmType("拆除报警");
			sensorDetail.setAlarmValue(Constant.ST_ALARM);
			has_alarm=true;
		}

		return sensorDetail;
	}
}
