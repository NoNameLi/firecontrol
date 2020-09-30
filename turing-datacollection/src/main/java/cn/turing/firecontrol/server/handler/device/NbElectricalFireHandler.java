package cn.turing.firecontrol.server.handler.device;



import cn.turing.common.entity.turing.NbElectricalFireV1;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author yangqi
 * 中科图灵电气火灾 -NB
 */

@Component
@Slf4j
@Order(80)
public class NbElectricalFireHandler extends AbstractDeviceEventHandler{

    //离线时间2个小时
    public static Integer normal_timeout=86400;

    //物联设备
    public static String deviceType="dqhz";
    //厂商中科图灵
    public static String channel="TUR";

    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){

        if ("B1B1".equalsIgnoreCase(deviceSensorMessage.getDeviceType())){
            return true;
        }

        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        String data=deviceSensorMessage.getData().toUpperCase();
        if (data.contains("B1B1") ){
            return true;
        }
        return  false;
    }


    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        //电气火灾-NB
        NbElectricalFireV1 deviceInfo=new NbElectricalFireV1(jsonObject);

        //处理数据
        parsingHandle(deviceInfo,normal_timeout,uuid,deviceType,channel);

    }



}
