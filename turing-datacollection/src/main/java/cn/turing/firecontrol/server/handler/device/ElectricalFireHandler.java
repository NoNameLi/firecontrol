package cn.turing.firecontrol.server.handler.device;

import cn.turing.common.entity.xinhaosi.XinHaoSiDeviceInfoMQV1;
import cn.turing.common.entity.xinhaosi.XinHaoSiDeviceInfoMQV2;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;



@Component
@Slf4j
@Order(99)
public class ElectricalFireHandler extends AbstractDeviceEventHandler{
    //默认选则版本1
    public Integer version=1;
    //离线时间2个小时
    public static Integer normal_timeout=86400;
    //电气火灾
    public static String deviceType="DQHZ";
    //厂商鑫豪斯
    public static String channel="XSH";


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.LORA_CEF.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }

        if ("4040".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        String data=deviceSensorMessage.getData();
        if (data.startsWith("4040") && data.length()==60 ){
            version=1;
            return true;
        }

        if (data.startsWith("4040") && data.length()==64){
            version=2;
            return true;
        }
        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        String data=deviceSensorMessage.getData();
        if (data.startsWith("4040") && data.length()==60 ){
            version=1;
            return true;
        }

        if (data.startsWith("4040") && data.length()==64){
            version=2;
            return true;
        }

        return false;
    }

    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        if (version==1){
            XinHaoSiDeviceInfoMQV1 deviceInfo=new XinHaoSiDeviceInfoMQV1(jsonObject);
            parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);
        }
        if (version==2){
            XinHaoSiDeviceInfoMQV2 deviceInfo=new XinHaoSiDeviceInfoMQV2(jsonObject);
            parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);
        }

    }


}
