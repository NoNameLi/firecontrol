package cn.turing.common.entity.jadebirdfire;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;


/**
 * 北大青鸟-NB烟感
 */
@Data
public class JaDeBiRdFireSmokeDevice extends RabbitDeviceInfo {


	/**
	 * 是否在线
	 */
	private Integer  online;

	/**
	 * 状态- 详见北大青鸟的推送平台的数据字典
	 */
	private Integer event;
	/**
	 * 电量-百分比
	 */
	private String voltage;

	//火警
	//{
	//  "data": {
	//    "devType": 332,
	//    "event": 1,
	//    "online": 1,
	//    "psn": "863808040144638",
	//    "stat": [
	//      1
	//    ],
	//    "time": 1599013824,
	//    "voltage": 100
	//  },
	//  "notifyType": "wirelessDeviceUpload"
	//}
	public JaDeBiRdFireSmokeDevice(JSONObject jsonObject) throws Exception {
		super(jsonObject);
		JSONObject object=JSONObject.parseObject(this.getData());
		JSONObject data=object.getJSONObject("data");
		event=data.getInteger("event");
		if (event==null){
			event=0;
		}
		online=data.getInteger("online");
		voltage=data.getString("voltage");

		this.sensorDetails= Lists.newArrayList();
		sensorDetails.add(yg());
		sensorDetails.add(vol());
	}


	public SensorDetail yg(){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.YG);

		if (event==1){
			sensorDetail.setAlarmType("火警");
			sensorDetail.setAlarmStatus(Constant.ST_ALARM);
			sensorDetail.setAlarmValue(Constant.ST_ALARM);
			has_alarm=true;
		}else

		if (event==7){
			sensorDetail.setAlarmType("故障");
			sensorDetail.setAlarmStatus(Constant.ST_WARN);
			sensorDetail.setAlarmValue(Constant.ST_WARN);
			has_guzhang=true;
		}else

		if (event==9){
			sensorDetail.setAlarmType("故障恢复");
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmValue(Constant.ST_NORM);
		}else
		if (event==66){
			sensorDetail.setAlarmType("分离故障");
			sensorDetail.setAlarmStatus(Constant.ST_WARN);
			sensorDetail.setAlarmValue(Constant.ST_WARN);
			has_guzhang=true;
		}else
			if (event==71){
				sensorDetail.setAlarmType("分离故障恢复");
				sensorDetail.setAlarmStatus(Constant.ST_NORM);
				sensorDetail.setAlarmValue(Constant.ST_NORM);
			}
		else if (event==72){
				sensorDetail.setAlarmType("火警撤销");
				sensorDetail.setAlarmStatus(Constant.ST_NORM);
				sensorDetail.setAlarmValue(Constant.ST_NORM);
		}else{
			if (online==1){
				sensorDetail.setAlarmType("在线");
				sensorDetail.setAlarmStatus(Constant.ST_NORM);
				sensorDetail.setAlarmValue(Constant.ST_NORM);
			}else{
				sensorDetail.setAlarmType("离线");
				sensorDetail.setAlarmStatus(Constant.ST_OFFLINE);
				sensorDetail.setAlarmValue(Constant.ST_OFFLINE);
				has_offline=true;
				has_guzhang=true;
			}
		}
		return sensorDetail;
	}

	public SensorDetail vol(){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.XELE);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(voltage);
		return  sensorDetail;
	}
}
