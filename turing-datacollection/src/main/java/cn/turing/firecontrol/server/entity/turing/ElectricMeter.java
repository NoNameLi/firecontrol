package cn.turing.firecontrol.server.entity.turing;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 电表
 */
@Slf4j
@Data
public class ElectricMeter extends RabbitDeviceInfo {
    private Integer address;//地址

    public ElectricMeter(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        log.info("开始解析16进制数据");
        String data=jsonObject.getString("data");
        //60604898765432AE0D
        address=Integer.parseInt(data.substring(4,6),16);
        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(parseElectric(data.substring(6,14)));
    }

    private SensorDetail parseElectric(String substring) {
        SensorDetail sensorDetail=new SensorDetail();
        Float value = Float.intBitsToFloat(Integer.valueOf(substring.trim(), 16));
        sensorDetail.setAlarmCode(Constant.WATT);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(value*50);

        return sensorDetail;
    }
}
