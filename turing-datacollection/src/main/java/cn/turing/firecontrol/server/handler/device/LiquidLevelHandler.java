package cn.turing.firecontrol.server.handler.device;


import cn.turing.common.entity.turing.LiquidLevelDeviceInfoV1;
import cn.turing.common.entity.turing.LiquidLevelDeviceInfoV2;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 中科图灵液位
 */

@Component
@Slf4j
@Order(94)
public class LiquidLevelHandler extends AbstractDeviceEventHandler{

    //离线时间24个小时
    public static Integer normal_timeout=86400;
    //物联设备
    public static String deviceType="WLSB";
    //厂商中科图灵
    public static String channel="TUR";


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.LORA_Liquid_Level.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        String data=deviceSensorMessage.getData();
        if (data.startsWith("9090")){
            return true;
        }

        if ("9191".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        String data=deviceSensorMessage.getData();
        if (data.contains("9090")){
            return true;
        }

        return false;
    }

    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        //液位设备数据解析
        if (deviceSensorMessage.getIccid().equals("111111")){
            LiquidLevelDeviceInfoV1 deviceInfo=new LiquidLevelDeviceInfoV1(jsonObject);
            parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);
        }else{
            LiquidLevelDeviceInfoV2 deviceInfo=new LiquidLevelDeviceInfoV2(jsonObject);
            parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);
        }




    }

}
