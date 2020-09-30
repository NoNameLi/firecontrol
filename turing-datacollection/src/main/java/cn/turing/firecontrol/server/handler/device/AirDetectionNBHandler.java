package cn.turing.firecontrol.server.handler.device;


import cn.turing.common.entity.turing.AirMonitoringDeviceInfoV2;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 中科图灵空气
 */

@Component
@Slf4j
@Order(85)
public class AirDetectionNBHandler extends AbstractDeviceEventHandler{

    //离线时间2个小时
    public static Integer normal_timeout=86400;
    //物联设备
    public static String deviceType="WLSB";
    //厂商中科图灵
    public static String channel="TUR";


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.NB_AIR.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        if ("8181".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }

        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        String data=deviceSensorMessage.getData();
        if (data.contains("8181") && data.startsWith("89")){
            return true;
        }

        return false;
    }

    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        //空气设备数据解析
        AirMonitoringDeviceInfoV2 deviceInfo=new AirMonitoringDeviceInfoV2(jsonObject);
        parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);



    }



}
