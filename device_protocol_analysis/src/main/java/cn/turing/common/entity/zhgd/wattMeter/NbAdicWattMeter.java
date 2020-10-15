package cn.turing.common.entity.zhgd.wattMeter;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

/**
 * NB电表--阿迪克 (一个小时周期)
 */
public class NbAdicWattMeter extends RabbitDeviceInfo {

	//单相电
	//{
	//    "dataMarker": "04601001",
	//    "loopType": "0",
	//    "data": {
	//        "rssi": "17",
	//        "PF": "1",
	//        "isOff": "0",
	//        "Ia": "0",
	//        "reverseActiveE": "0",
	//        "activeP": "0.0023",
	//        "positiveActiveE": "0",
	//        "Ua": "233.2",
	//        "elecFrequency": "50",
	//        "elecVoltageRating": "null",
	//        "totalActiveE": "0"
	//    },
	//    "equipNumber": "000000365042",
	//    "equipType": "110000",
	//    "commType": "304",
	//    "dataType": "221",
	//    "appKey": "APP_6c0c402628",
	//    "lastSaveTime": "1600328019718"
	//}




	public NbAdicWattMeter(JSONObject jsonObject) throws Exception {
		super(jsonObject);

		JSONObject object=jsonObject.getJSONObject("data");
		JSONObject data=object.getJSONObject("data");
		//单相电表
		this.sensorDetails= Lists.newArrayList();
		if (object.getString("dataMarker").equals("04601001")){

			this.sensorDetails.add(PF(data));
			this.sensorDetails.add(isoff(data));
			this.sensorDetails.add(ia(data));
			this.sensorDetails.add(activeP(data));
			this.sensorDetails.add(ua(data));
			this.sensorDetails.add(totalActiveE(data));
		}

		//三相电表
		if (object.getString("dataMarker").equals("04601206")){
			this.sensorDetails.add(ua(data));
			this.sensorDetails.add(ub(data));
			this.sensorDetails.add(uc(data));
			this.sensorDetails.add(ia(data));
			this.sensorDetails.add(ib(data));
			this.sensorDetails.add(ic(data));
			this.sensorDetails.add(activeP(data));
			this.sensorDetails.add(activePA(data));
			this.sensorDetails.add(activePB(data));
			this.sensorDetails.add(activePC(data));
			this.sensorDetails.add(PF(data));
			this.sensorDetails.add(PFa(data));
			this.sensorDetails.add(PFb(data));
			this.sensorDetails.add(PFc(data));
			this.sensorDetails.add(totalActiveE(data));
			this.sensorDetails.add(isoff(data));
		}

	}

	/**
	 * A相电压
	 * @param data
	 * @return
	 */
	public SensorDetail ua(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.VLA);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("Ua"));

		return sensorDetail;
	}
	/**
	 * b相电压
	 * @param data
	 * @return
	 */
	public SensorDetail ub(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.VLB);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("Ub"));

		return sensorDetail;
	}
	/**
	 * c相电压
	 * @param data
	 * @return
	 */
	public SensorDetail uc(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.VLC);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("Uc"));

		return sensorDetail;
	}

	/**
	 * A相电流
	 * @param data
	 * @return
	 */
	public SensorDetail ia(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.ECA);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("Ia"));

		return sensorDetail;
	}

	/**
	 * b相电流
	 * @param data
	 * @return
	 */
	public SensorDetail ib(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.ECB);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("Ib"));

		return sensorDetail;
	}
	/**
	 * c相电流
	 * @param data
	 * @return
	 */
	public SensorDetail ic(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.ECC);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("Ic"));

		return sensorDetail;
	}

	/**
	 * 瞬时总有功
	 * @param data
	 * @return
	 */
	public SensorDetail activeP(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.PW);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("activeP"));

		return sensorDetail;
	}
	/**
	 * 瞬时总有功c
	 * @param data
	 * @return
	 */
	public SensorDetail activePC(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.PWC);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("activePc"));

		return sensorDetail;
	}
	/**
	 * 瞬时总有功a
	 * @param data
	 * @return
	 */
	public SensorDetail activePA(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.PWA);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("activePa"));

		return sensorDetail;
	}
	/**
	 * 瞬时总有功b
	 * @param data
	 * @return
	 */
	public SensorDetail activePB(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.PWB);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("activePb"));

		return sensorDetail;
	}


	/**
	 * 总功率因数
	 * @param data
	 * @return
	 */
	public SensorDetail PF(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.PF);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("PF"));

		return sensorDetail;
	}
	/**
	 * 总功率因数
	 * @param data
	 * @return
	 */
	public SensorDetail PFa(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.PFA);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("PFa"));

		return sensorDetail;
	}
	/**
	 * b功率因数
	 * @param data
	 * @return
	 */
	public SensorDetail PFb(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.PFB);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("PFb"));

		return sensorDetail;
	}
	/**
	 * c功率因数
	 * @param data
	 * @return
	 */
	public SensorDetail PFc(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.PFC);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("PFc"));

		return sensorDetail;
	}

	/**
	 * 组合有功总电能
	 * @param data
	 * @return
	 */
	public SensorDetail totalActiveE(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.EEG);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmValue(data.getString("totalActiveE"));

		return sensorDetail;
	}

	public SensorDetail isoff(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.SWITCH);

		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		if (data.getString("isOff").equals("0")){
			sensorDetail.setAlarmType("开");
		}else{
			sensorDetail.setAlarmType("关");
		}
		sensorDetail.setAlarmValue(data.getString("isOff"));

		return sensorDetail;
	}
	@Override
	public JSONObject toDeviceJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("deviceId", this.getDevice_id().toLowerCase());
		jsonObject.put("uploadTime",this.getUpload_time());
		jsonObject.put("receiveTime",this.getRecieve_time());
		JSONArray jsonArray=new JSONArray();
		if(this.getSensorDetails()!=null) {
			for (SensorDetail sensorDetail:this.getSensorDetails()) {
				JSONObject val = new JSONObject();
				val.put("alarmValue",sensorDetail.getAlarmValue());
				val.put("alarmType",sensorDetail.getAlarmType());
				val.put("alarmStatus",sensorDetail.getAlarmStatus());
				val.put("alarmCode",sensorDetail.getAlarmCode());
				jsonArray.add(val);
			}
		}
		jsonObject.put("point",jsonArray);
		return jsonObject;
	}
}
