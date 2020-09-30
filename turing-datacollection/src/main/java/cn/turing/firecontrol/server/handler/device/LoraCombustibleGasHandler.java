package cn.turing.firecontrol.server.handler.device;


import cn.turing.common.entity.turing.CombustibleGasDeviceInfoV2;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * lora-可燃气体
 */
@Component
@Order(85)
public class LoraCombustibleGasHandler extends AbstractDeviceEventHandler {
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
        if (LoraDeviceTypeConst.LORA_GAS.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        if (deviceSensorMessage.getData().startsWith("1111")){
            return true;
        }
        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        //加了iccid卡
        if (deviceSensorMessage.getData().startsWith("1111")){
            return true;
        }

        return false;
    }

    @Override
    public void handle(DeviceSensorMessage warningMessage, String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(warningMessage);
        //喷淋压力设备数据解析
        CombustibleGasDeviceInfoV2 deviceInfo=new CombustibleGasDeviceInfoV2(jsonObject);
        parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);
    }
}
