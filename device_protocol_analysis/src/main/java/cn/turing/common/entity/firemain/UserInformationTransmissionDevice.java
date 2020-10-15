package cn.turing.common.entity.firemain;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 思迪-用户信息传输装置-适用于消防主机
 */
@Data
public class UserInformationTransmissionDevice extends RabbitDeviceInfo {
	/**
	 * 类型标志
	 */
	private String typeStat;
	/**
	 * 信息对象数目
	 */
	private Integer messageNumber;
	/**
	 * 信息体
	 */
	private List<UserMessage> userMessageList;

	public UserInformationTransmissionDevice(JSONObject jsonObject) throws Exception {
		super(jsonObject);
		typeStat=this.getData().substring(0,2);
		messageNumber=Integer.parseInt(this.getData().substring(2,4),16);

		String data=this.getData().substring(4);
		//上传建筑部件设施运行状态
		if (typeStat.equals("02")){
			userMessageList=new ArrayList<UserMessage>();
			for (int i=1;i<=messageNumber;i++){
				String message=data.substring(92*i-92,92*i);
				userMessageList.add(userMessages(message));
			}

		}

	}

	public UserMessage userMessages(String message){
		UserMessage userMessage=new UserMessage();
		userMessage.setSystemType(message.substring(0,2));
		userMessage.setFireMainCode(message.substring(2,4));
		userMessage.setPartType(message.substring(4,6));
		userMessage.setAddressNo(String.valueOf(Integer.parseInt(message.substring(8,10)+
				message.substring(6,8),16)));

		userMessage.setLoopNo(String.valueOf(Integer.parseInt(message.substring(12,14)+
				message.substring(10,12),16)));

		userMessage.setPartStat(ByteUtil.buqi(Integer.toBinaryString(Integer.parseInt(message.substring(14,18),16))));

		userMessage.setPartExplain(ByteUtil.hexStr2Str(message.substring(18,80)));

		String time=message.substring(80);
		String ms=String.valueOf(Integer.parseInt(time.substring(0,2),16));
		String min=String.valueOf(Integer.parseInt(time.substring(2,4),16));
		String hour=String.valueOf(Integer.parseInt(time.substring(4,6),16));
		String day=String.valueOf(Integer.parseInt(time.substring(6,8),16));
		String month=String.valueOf(Integer.parseInt(time.substring(8,10),16));
		String year=String.valueOf(Integer.parseInt(time.substring(10),16));


		userMessage.setUpTime("20"+year+"-"+month+"-"+day+" "+hour+":"+min+":"+ms);

		this.sensorDetails=Lists.newArrayList();
		sensorDetails.add(fireMain(userMessage.getPartStat()));

		userMessage.setList(sensorDetails);
		return userMessage;
	}

	public SensorDetail fireMain(String partStat){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.FIREMAIN);
		if (partStat.substring(1,2).equals("1")){//火警
			sensorDetail.setAlarmType("火警");
			sensorDetail.setAlarmStatus(Constant.ST_ALARM);
			sensorDetail.setAlarmValue(Constant.ST_ALARM);
			has_alarm=true;
			return sensorDetail;
		}
		if (partStat.substring(2,3).equals("1")){
			sensorDetail.setAlarmType("故障");
			sensorDetail.setAlarmStatus(Constant.ST_ALARM);
			sensorDetail.setAlarmValue(Constant.ST_ALARM);
			has_guzhang=true;
			return sensorDetail;
		}
		if (partStat.substring(8,9).equals("1")){
			sensorDetail.setAlarmType("电源故障");
			sensorDetail.setAlarmStatus(Constant.ST_ALARM);
			sensorDetail.setAlarmValue(Constant.ST_ALARM);
			has_guzhang=true;
			return sensorDetail;
		}

		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(Constant.ST_NORM);
		return sensorDetail;

	}

	public JSONObject toDeviceMessage(List<SensorDetail> sensorDetails) {
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("deviceCode",this.getDevice_id().toLowerCase());
		jsonObject.put("uploadTime",this.getUpload_time());
		jsonObject.put("receiveTime",this.getRecieve_time());
		if(sensorDetails!=null) {
			for (SensorDetail sensorDetail:sensorDetails) {
				JSONObject val = new JSONObject();
				val.put("alarmValue",sensorDetail.getAlarmValue());
				val.put("alarmType",sensorDetail.getAlarmType());
				val.put("alarmStatus",sensorDetail.getAlarmStatus());
				jsonObject.put(sensorDetail.getAlarmCode(),val);
			}
		}
		if (this.has_guzhang){
			jsonObject.put("status", Constant.ST_WARN);
		}else if (this.has_alarm){
			jsonObject.put("status",Constant.ST_ALARM);
		}else if (this.has_offline){
			jsonObject.put("status",Constant.ST_OFFLINE);
		}
		else{
			jsonObject.put("status",Constant.ST_NORM);
		}
		return jsonObject;
	}


}
