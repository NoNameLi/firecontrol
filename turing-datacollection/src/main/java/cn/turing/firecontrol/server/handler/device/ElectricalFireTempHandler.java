package cn.turing.firecontrol.server.handler.device;

import cn.turing.common.entity.turing.ElectricalFireDeviceInfoV1;
import cn.turing.firecontrol.server.business.BusinessI;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@Order(98)
public class ElectricalFireTempHandler extends AbstractDeviceEventHandler{

    //离线时间2个小时
    public static Integer normal_timeout=86400;
    //电气火灾
    public static String deviceType="DQHZ";
    //厂商中科图灵
    public static String channel="TUR";

    @Autowired
    BusinessI businessI;

    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.LORA_CEF_TEMP.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        if ("B0B0".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }

        String data=deviceSensorMessage.getData().toUpperCase();
        if (data.startsWith("B0B0")){
            return true;
        }
        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        String data=deviceSensorMessage.getData();
        if (data.startsWith("B0B0")){
            return true;
        }
        return false;
    }

    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        ElectricalFireDeviceInfoV1 deviceInfo=new ElectricalFireDeviceInfoV1(jsonObject,
                businessI.selectMqttSensoralarmValue(jsonObject.getString("deviceCode")));
        parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);


    }


}
