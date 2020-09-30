package cn.turing.firecontrol.server.handler.device;

import cn.turing.common.entity.turing.SmokingDeviceInfoV2;
import cn.turing.common.entity.turing.SmokingDeviceInfoV3;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;

import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * lora烟感
 */
@Component
@Slf4j
@Order(98)
public class TuringSmokeHandler extends AbstractDeviceEventHandler{
    //离线时间24小时
    private static Integer normal_timeout=86400;
    //模块类型
    private static String deviceType="yg";
    //厂商：中科图灵
    private static String channel="TUR";


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.LORA_SMOKE.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }

        if ("C0C0".equalsIgnoreCase(deviceSensorMessage.getDeviceType())){
            return true;
        }
        String data=deviceSensorMessage.getData().toUpperCase();
        if (data.startsWith("C0C0")  ){
            return true;
        }

        if (data.contains("C1C1")  ){
            return true;
        }
        return  false;
    }


    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        String data=deviceSensorMessage.getData().toUpperCase();
        if (data.startsWith("C0C0") ){
            return true;
        }
        if (data.contains("C1C1") ){
            return true;
        }
        return false;
    }

    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        if (jsonObject.getString("iccid").equals("111111")){
            SmokingDeviceInfoV2 deviceInfo=new SmokingDeviceInfoV2(jsonObject);
            if (deviceInfo.getCMD().equals("01")) {
                //处理解析的数据
                parsingHandle(deviceInfo, normal_timeout, uuid, deviceType, channel);
            }
        }else{
            SmokingDeviceInfoV3 deviceInfo=new SmokingDeviceInfoV3(jsonObject);
            if (deviceInfo.getCMD().equals("01")) {
                //处理解析的数据
                parsingHandle(deviceInfo, normal_timeout, uuid, deviceType, channel);
            }
        }

    }
}
