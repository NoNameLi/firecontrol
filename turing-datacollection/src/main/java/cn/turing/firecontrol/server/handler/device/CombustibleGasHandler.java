package cn.turing.firecontrol.server.handler.device;

import cn.turing.common.entity.turing.CombustibleGasDeviceInfoV1;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * NB可燃气体
 */
@Component
@Order(88)
public class CombustibleGasHandler extends AbstractDeviceEventHandler {
    /**
     * 1个小时上传一次数据
     */
    //离线时间24个小时
    public static Integer normal_timeout=86400;
    //可燃气体
    public static String deviceType="KRQT";
    //厂商中科图灵
    public static String channel="TUR";


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.NB_GAS.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        if ("1010".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        //加了iccid卡
        String data=deviceSensorMessage.getData().toUpperCase();
        if (data.contains("1010") && data.startsWith("89")){
            return true;
        }

        return false;
    }

    @Override
    public void handle(DeviceSensorMessage warningMessage, String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(warningMessage);
        //喷淋压力设备数据解析
        CombustibleGasDeviceInfoV1 deviceInfo=new CombustibleGasDeviceInfoV1(jsonObject);
        parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);
    }
}
