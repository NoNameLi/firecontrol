package cn.turing.firecontrol.server.handler.device;


import cn.turing.common.entity.turing.SprayDeviceInfoV1;
import cn.turing.common.entity.turing.SprayDeviceInfoV2;
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
public class SprayHandler extends AbstractDeviceEventHandler{

    //离线时间24个小时
    public static Integer normal_timeout=86400;
    //消防用水
    public static String deviceType="XFYS";
    //厂商中科图灵
    public static String channel="TUR";


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.LORA_SPRAY.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }

        String data=deviceSensorMessage.getData().toUpperCase();
        if (data.startsWith("D0D0")){
            return true;
        }

        if ("9292".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        String data=deviceSensorMessage.getData().toUpperCase();
        if (data.contains("D0D0") ){
            return true;
        }

        return false;
    }

    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        //喷淋压力设备数据解析
        if (deviceSensorMessage.getIccid().equals("111111")){
            SprayDeviceInfoV1 deviceInfo=new SprayDeviceInfoV1(jsonObject);
            parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);
        }else{
            SprayDeviceInfoV2 deviceInfo=new SprayDeviceInfoV2(jsonObject);
            parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);
        }




    }

}
