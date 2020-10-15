package cn.turing.common.entity.zhgd.waterMeter;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * 水表-nb-湖北骏翔
 */

@Data
public class NbJunXiangWaterMeter extends RabbitDeviceInfo {
	//{
	//    "upPacketSN": -1,
	//    "upDataSN": -1,
	//    "topic": "v1/up/ad19prof",
	//    "timestamp": 1600400064538,
	//    "tenantId": "10508969",
	//    "serviceId": "",
	//    "protocol": "lwm2m",
	//    "productId": "10094053",
	//    "payload": {
	//        "serviceId": "SystemCmdRawData",
	//        "serviceData": {
	//            "MeterData": {
	//                "voltage": 3.6,
	//                "valveStatus": 1,
	//                "temperature": 22,
	//                "signal": 19,
	//                "reverseQuantity": 0,
	//                "remainNum": 0,
	//                "readTime": "2020-09-18 11:34:24",
	//                "qzStatus": null,
	//                "pressure": 0,
	//                "monthAmount": null,
	//                "mode": 2,
	//                "meterCode": "10012190005493",
	//                "magneticStatus": 0,
	//                "m": 0,
	//                "items": [
	//                    {
	//                        "time": "2020-09-17 11:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-17 12:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-17 13:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-17 14:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-17 15:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-17 16:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-17 17:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-17 18:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-17 19:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-17 20:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-17 21:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-17 22:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-17 23:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-18 00:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-18 01:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-18 02:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-18 03:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-18 04:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-18 05:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-18 06:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-18 07:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-18 08:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-18 09:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    },
	//                    {
	//                        "time": "2020-09-18 10:00:00",
	//                        "pressure": 0,
	//                        "amount": 0
	//                    }
	//                ],
	//                "h": 0,
	//                "faultStatus": 0,
	//                "d": 0,
	//                "batteryStatus": 0,
	//                "amount": 0
	//            }
	//        }
	//    },
	//    "messageType": "dataReport",
	//    "deviceType": "",
	//    "deviceId": "1f9471c63f6947359722f34471e8c7ff",
	//    "assocAssetId": "",
	//    "IMSI": "undefined",
	//    "IMEI": "867726036580035"
	//}

	private Double cumulativeFlow;

	private JSONArray items;

	private String readTime;


	public NbJunXiangWaterMeter(JSONObject jsonObject) throws Exception {
		super(jsonObject);

		JSONObject object=jsonObject.getJSONObject("data").getJSONObject("payload").
				getJSONObject("serviceData").getJSONObject("MeterData");

		cumulativeFlow=object.getDouble("amount");
		items=object.getJSONArray("items");
		readTime=object.getString("readTime");

		this.sensorDetails= Lists.newArrayList();
		sensorDetails.add(cumulativeFlow(object));
		sensorDetails.add(valveStatus(object));
		sensorDetails.add(waterMater(object));
		sensorDetails.add(magneticStatus(object));
		sensorDetails.add(vol(object));
		sensorDetails.add(temp(object));
		sensorDetails.add(sign(object));

	}

	public SensorDetail cumulativeFlow(JSONObject object){
		SensorDetail sensorDetail=new SensorDetail();

		sensorDetail.setAlarmCode(Constant.WCF);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(object.getDouble("amount"));

		return sensorDetail;
	}

	public SensorDetail valveStatus(JSONObject object){
		SensorDetail sensorDetail=new SensorDetail();

		sensorDetail.setAlarmCode(Constant.SWITCH);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		if (object.getInteger("valveStatus")==1){
			sensorDetail.setAlarmType("开阀");
		}else {
			sensorDetail.setAlarmType("关阀");
		}
		sensorDetail.setAlarmValue(object.getInteger("valveStatus"));

		return sensorDetail;
	}

	public SensorDetail waterMater(JSONObject object){
		SensorDetail sensorDetail=new SensorDetail();
		//0正常，1故障
		sensorDetail.setAlarmCode(Constant.WW);

		if (object.getInteger("faultStatus")==0){
			sensorDetail.setAlarmType("正常");
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmValue(Constant.ST_NORM);
		}else {
			sensorDetail.setAlarmType("故障");
			sensorDetail.setAlarmStatus(Constant.ST_WARN);
			sensorDetail.setAlarmValue(Constant.ST_WARN);
			has_guzhang=true;
		}


		return sensorDetail;
	}

	public SensorDetail magneticStatus(JSONObject object){
		SensorDetail sensorDetail=new SensorDetail();
		//0正常，1故障
		sensorDetail.setAlarmCode(Constant.QC);

		if (object.getInteger("magneticStatus")==0){
			sensorDetail.setAlarmType("正常");
			sensorDetail.setAlarmStatus(Constant.ST_NORM);
			sensorDetail.setAlarmValue(Constant.ST_NORM);
		}else {
			sensorDetail.setAlarmType("故障");
			sensorDetail.setAlarmStatus(Constant.ST_WARN);
			sensorDetail.setAlarmValue(Constant.ST_WARN);
			has_guzhang=true;
		}
		return sensorDetail;
	}

	public SensorDetail vol(JSONObject object){
		SensorDetail sensorDetail=new SensorDetail();

		sensorDetail.setAlarmCode(Constant.VOL);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(object.getInteger("batteryStatus"));

		return sensorDetail;
	}

	public SensorDetail temp(JSONObject object){
		SensorDetail sensorDetail=new SensorDetail();

		sensorDetail.setAlarmCode(Constant.XTEMP);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(object.getDouble("temperature"));

		return sensorDetail;
	}
	public SensorDetail sign(JSONObject object){
		SensorDetail sensorDetail=new SensorDetail();

		sensorDetail.setAlarmCode(Constant.SIGN);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);

		if (object.getDouble("signal")<5){
			sensorDetail.setAlarmType("无信号");
		}
		if (object.getDouble("signal")>=5 && object.getDouble("signal")<11){
			sensorDetail.setAlarmType("1格信号");
		}
		if (object.getDouble("signal")>=11 && object.getDouble("signal")<24){
			sensorDetail.setAlarmType("2格信号");
		}
		if (object.getDouble("signal")>=24){
			sensorDetail.setAlarmType("3格信号");
		}

		sensorDetail.setAlarmValue(object.getDouble("temperature"));

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
