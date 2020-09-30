package cn.turing.firecontrol.server.handler.device;


import cn.turing.common.entity.turing.SOSNBDeviceInfoV1;
import cn.turing.common.entity.turing.SmartPowerDevice;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 子木源盛，智慧用电
 */

@Component
@Slf4j
@Order(85)
public class SmartPowerZiMuHandler extends AbstractDeviceEventHandler{

    //离线时间24个小时
    public static Integer normal_timeout=86400;
    //消防用水
    public static String deviceType="dqhz";
    //厂商子木源盛
    public static String channel="zimuyuansheng";


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if ("smart_zimu".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        String data=deviceSensorMessage.getData();
        if (data.startsWith("010320")){
            return true;
        }
        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        if ("smart_zimu".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        String data=deviceSensorMessage.getData();
        if (data.startsWith("010320")){
            return true;
        }
        return  false;
    }

    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        //电表设备数据解析
        SmartPowerDevice deviceInfo=new SmartPowerDevice(jsonObject);
        parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);



    }
}
