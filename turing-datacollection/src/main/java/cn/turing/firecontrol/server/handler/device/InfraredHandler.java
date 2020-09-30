package cn.turing.firecontrol.server.handler.device;

import cn.turing.common.entity.turing.InfraredDeviceInfoV1;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 中科图灵红外
 */

@Component
@Slf4j
@Order(94)
public class InfraredHandler extends AbstractDeviceEventHandler{

    //离线时间24个小时
    public static Integer normal_timeout=86400;
    //物联设备
    public static String deviceType="WLSB";
    //厂商中科图灵
    public static String channel="TUR";


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.LORA_INFRARED.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        if ("A0A0".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }

        String data=deviceSensorMessage.getData().toUpperCase();
        if (data.startsWith("A0A0") ){
            return true;
        }
        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        String data=deviceSensorMessage.getData();
        if (data.contains("A0A0") ){
            return true;
        }

        return false;
    }

    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        //液位设备数据解析
        InfraredDeviceInfoV1 deviceInfo=new InfraredDeviceInfoV1(jsonObject);
        parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);



    }

}
