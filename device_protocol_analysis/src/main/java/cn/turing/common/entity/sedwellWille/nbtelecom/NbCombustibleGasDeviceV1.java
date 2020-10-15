package cn.turing.common.entity.sedwellWille.nbtelecom;

import cn.turing.common.base.Constant;
import cn.turing.common.constant.NbTelecomConstant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

/**
 * 电信-AEP物联网平台-赛特威尔-可燃气体-文档都在AEP平台上
 *
 */
public class NbCombustibleGasDeviceV1 extends RabbitDeviceInfo {

	public NbCombustibleGasDeviceV1(JSONObject jsonObject) throws Exception {
		super(jsonObject);
		//电信原始数据   转成TMC平台格式
		JSONObject data=JSONObject.parseObject(this.getData());
		//消息类型
		String messageType=data.getString("messageType");

		this.sensorDetails= Lists.newArrayList();

		//可燃气体有四种数据上报
		if (NbTelecomConstant.dataReport.equals(messageType)){
			JSONObject payload=data.getJSONObject("payload");
			//1.心跳周期 只要电池电量这个测点
			if (data.toJSONString().contains("heartbeat_time")){
				sensorDetails.add(getVol(payload));
			}

			//2.TY-业务数据上报（智慧社区）温度 燃气检测状态 电池电量
			if (data.toJSONString().contains("medium")){
				sensorDetails.add(getVol(payload));
				sensorDetails.add(getCOMG(payload));
				sensorDetails.add(getTemp(payload));
			}
			//3.业务数据上报--燃气检测状态
			if (data.toJSONString().contains("gas_sensor_state") & !
					data.toJSONString().contains("medium") ){
				sensorDetails.add(getCOMG(payload));
			}

		}
		//可燃气体3种事件上报
		if (NbTelecomConstant.eventReport.equals(messageType)){
			JSONObject payload=data.getJSONObject("eventContent");
			//1.燃气告警状态
			if (data.toJSONString().contains("gas_sensor_state") ){
				sensorDetails.add(getCOMGEvent(payload));
			}
			//2.故障上报
			if (data.toJSONString().contains("error_code") ){
				sensorDetails.add(getFalut(payload));
			}
			//3.电池低压告警
			if (data.toJSONString().contains("battery_voltage")){
				sensorDetails.add(getvolD(payload));
			}
		}


	}

	public SensorDetail getVol(JSONObject payload){

		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.XELE);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(payload.getInteger("battery_value"));

		return sensorDetail;
	}

	public SensorDetail getCOMG(JSONObject payload){

		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.COMG);

		if (payload.getInteger("gas_sensor_state")==NbTelecomConstant.gas_sensor_state_normal){
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmType("正常");

		}else{
			sensorDetail.setAlarmStatus(Constant.ST_ALARM);
			sensorDetail.setAlarmType("报警");
			has_alarm=true;
		}

		sensorDetail.setAlarmValue(payload.getInteger("gas_value"));


		return sensorDetail;
	}

	public SensorDetail getCOMGEvent(JSONObject payload){

		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.COMG);

		if (payload.getInteger("gas_sensor_state")==NbTelecomConstant.gas_sensor_state_normal){
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmType("正常");
			sensorDetail.setAlarmValue(Constant.ST_NORM);

		}else{
			sensorDetail.setAlarmStatus(Constant.ST_ALARM);
			sensorDetail.setAlarmType("报警");
			sensorDetail.setAlarmValue(Constant.ST_ALARM);
			has_alarm=true;
		}




		return sensorDetail;
	}

	public SensorDetail getTemp(JSONObject payload){

		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.XTEMP);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(payload.getInteger("temperature"));

		return sensorDetail;
	}

	public SensorDetail getFalut(JSONObject payload){

		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.COMG);
		//正常
		if (payload.getInteger("error_code")==0){
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmType("正常");
			sensorDetail.setAlarmValue(Constant.ST_NORM);
		}
		//传感器故障
		if (payload.getInteger("error_code")==1){
			sensorDetail.setAlarmStatus(Constant.ST_WARN);
			sensorDetail.setAlarmType("传感器故障");
			sensorDetail.setAlarmValue(Constant.ST_WARN);
			has_guzhang=true;
		}
		//电池故障
		if (payload.getInteger("error_code")==2){
			sensorDetail.setAlarmStatus(Constant.ST_WARN);
			sensorDetail.setAlarmType("传感器故障");
			sensorDetail.setAlarmValue(Constant.ST_WARN);
			has_guzhang=true;
		}

		//开机
		if (payload.getInteger("error_code")==3){
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmType("开机");
			sensorDetail.setAlarmValue(Constant.ST_NORM);
		}

		//关机
		if (payload.getInteger("error_code")==4){
			sensorDetail.setAlarmStatus(Constant.ST_WARN);
			sensorDetail.setAlarmType("关机");
			sensorDetail.setAlarmValue(Constant.ST_WARN);
			has_guzhang=true;
		}
		//预热
		if (payload.getInteger("error_code")==5){
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmType("预热");
			sensorDetail.setAlarmValue(Constant.ST_NORM);
		}

		//自检
		if (payload.getInteger("error_code")==6){
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmType("自检");
			sensorDetail.setAlarmValue(Constant.ST_NORM);
		}
		//失效
		if (payload.getInteger("error_code")==7){
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmType("自检");
			sensorDetail.setAlarmValue(Constant.ST_NORM);
		}
		//离线
		if (payload.getInteger("error_code")==8){
			sensorDetail.setAlarmStatus(Constant.ST_OFFLINE);
			sensorDetail.setAlarmType("离线");
			sensorDetail.setAlarmValue(Constant.ST_OFFLINE);
			has_offline=true;
		}

		return sensorDetail;
	}

	public SensorDetail getvolD(JSONObject payload){

		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.XELE);
		sensorDetail.setAlarmStatus(Constant.ST_WARN);
		sensorDetail.setAlarmType("电池低压");
		sensorDetail.setAlarmValue(Constant.ST_WARN);

		return sensorDetail;
	}
}
