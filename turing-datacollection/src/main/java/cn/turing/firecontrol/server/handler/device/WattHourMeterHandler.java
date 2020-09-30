package cn.turing.firecontrol.server.handler.device;

import cn.turing.common.entity.turing.ElectricMeter;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 中科图灵电表
 */

@Component
@Slf4j
@Order(96)
public class WattHourMeterHandler extends AbstractDeviceEventHandler{

    //离线时间24个小时
    public static Integer normal_timeout=86400;
    //消防用水
    public static String deviceType="XFYS";
    //厂商中科图灵
    public static String channel="TUR";


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.LORA_ELE.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }

        if ("6060".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }

        String data=deviceSensorMessage.getData();
        if (data.startsWith("6060") ){
            return true;
        }
        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        String data=deviceSensorMessage.getData();
        if (data.startsWith("6060") ){
            return true;
        }

        return false;
    }

    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        //电表设备数据解析
        ElectricMeter deviceInfo=new ElectricMeter(jsonObject);
        parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);



    }
}
