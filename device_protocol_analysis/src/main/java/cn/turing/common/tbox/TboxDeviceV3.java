package cn.turing.common.tbox;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

/**
 * tbox-接入到消防平台
 */
public class TboxDeviceV3 extends RabbitDeviceInfo {


	public TboxDeviceV3(JSONObject jsonObject) throws Exception {
		super(jsonObject);
		JSONObject data=JSONObject.parseObject(this.getData());

		this.sensorDetails= Lists.newArrayList();

		if (data.getString("redMeter")!=null){

			sensorDetails.add(redMeter(data));
			sensorDetails.add(vol(data));
			sensorDetails.add(vol(data));

		}

		if (data.getString("redSum")!=null){

			sensorDetails.add(redSum(data));
			sensorDetails.add(vol(data));
			sensorDetails.add(vol(data));
		}

		if (data.getString("mpa") !=null){
			sensorDetails.add(mpa(data));
			sensorDetails.add(vol(data));
			sensorDetails.add(vol(data));
		}

		if (data.getString("temp")!=null){
			sensorDetails.add(hum(data));
			sensorDetails.add(hum(data));
			sensorDetails.add(vol(data));
			sensorDetails.add(vol(data));
		}

		if (data.getString("yw")!=null){
			sensorDetails.add(yw(data));
			sensorDetails.add(vol(data));
			sensorDetails.add(vol(data));
		}

	}

	public SensorDetail yw(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.YW);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(data.getString("yw"));

		return sensorDetail;
	}


	public SensorDetail temp(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.XTEMP);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(data.getString("temp"));

		return sensorDetail;
	}

	public SensorDetail hum(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.HUM);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(data.getString("hum"));

		return sensorDetail;
	}





	public SensorDetail redMeter(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.REDMETER);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(data.getString("redMeter"));

		return sensorDetail;
	}

	public SensorDetail redSum(JSONObject data){

		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.REDSUM);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(data.getString("redSum"));

		return sensorDetail;
	}
	public SensorDetail mpa(JSONObject data){

		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.SPR);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(data.getString("mpa"));

		return sensorDetail;
	}



	public SensorDetail vol(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.VOL);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(data.getString("vol"));

		return sensorDetail;
	}

	public SensorDetail sign(JSONObject data){
		SensorDetail sensorDetail=new SensorDetail();
		sensorDetail.setAlarmCode(Constant.SIGN);
		sensorDetail.setAlarmStatus(Constant.ST_NORM);
		sensorDetail.setAlarmType("正常");
		sensorDetail.setAlarmValue(data.getString("sign"));

		return sensorDetail;
	}

}
