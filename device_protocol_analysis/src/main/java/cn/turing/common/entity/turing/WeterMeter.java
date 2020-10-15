package cn.turing.common.entity.turing;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * 水表
 */
@Data
public class WeterMeter extends RabbitDeviceInfo {
    private Integer address;//地址

    public WeterMeter(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        String data=jsonObject.getString("data");
        //60604898765432AE0D
        address=Integer.parseInt(data.substring(4,6),16);
        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(parseWater(data.substring(6,14)));

    }

    private SensorDetail parseWater(String substring) {
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.WBTT);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(Integer.valueOf(substring.trim()));

        return sensorDetail;
    }
}
