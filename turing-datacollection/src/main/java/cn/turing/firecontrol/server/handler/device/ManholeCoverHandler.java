package cn.turing.firecontrol.server.handler.device;


import cn.turing.common.entity.gongyuan.WellCoverDeviceInfoV2;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 中科图灵井盖
 */

@Component
@Slf4j
@Order(94)
public class ManholeCoverHandler extends AbstractDeviceEventHandler{

    //离线时间24个小时
    public static Integer normal_timeout=86400;
    //物联设备
    public static String deviceType="WLSB";
    //厂商中科图灵
    public static String channel="TUR";


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.LORA_MANHOLE_COVER.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }

        if ("1D".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        String data=deviceSensorMessage.getData().toUpperCase();
        if (data.startsWith("1D")){
            return true;
        }
        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        String data=deviceSensorMessage.getData();
        if (data.startsWith("1D")){
            return true;
        }

        return false;
    }

    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        //水浸设备数据解析
        WellCoverDeviceInfoV2 deviceInfo=new WellCoverDeviceInfoV2(jsonObject);
        parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);

    }

    @Override
    public void postHandle(DeviceSensorMessage deviceSensorMessage) {

    }

}
