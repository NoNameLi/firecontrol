package cn.turing.firecontrol.server.handler.device;


import cn.turing.common.entity.turing.LoraXiaoFangShuanDeviceInfoV2;
import cn.turing.common.entity.turing.XiaoFangShuanDeviceInfoV1;
import cn.turing.common.entity.turing.XiaoFangShuanNBDeviceInfoV2;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 中科图灵消火栓
 */

@Component
@Slf4j
@Order(97)
public class HydrantHandler extends AbstractDeviceEventHandler{

    //离线时间24个小时
    public static Integer normal_timeout=86400;
    //消防用水
    public static String deviceType="XFYS";
    //厂商中科图灵
    public static String channel="TUR";


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.LORA_XHS.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }

        if ("5050".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }

        if ("5151".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        String data=deviceSensorMessage.getData();
        if (data.startsWith("5050") | data.contains("5151")  |data.startsWith("5252") ){
            return true;
        }

        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        String data=deviceSensorMessage.getData();
        if (data.startsWith("5050")  | data.contains("5151")|data.startsWith("5252") ){
            return true;
        }

        return false;
    }

    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        //lora 没有iccid
        if (deviceSensorMessage.getData().startsWith("5050")){
            //消火栓设备数据解析
            XiaoFangShuanDeviceInfoV1 deviceInfo=new XiaoFangShuanDeviceInfoV1(jsonObject);

                //处理消防栓
            parsingHandle(deviceInfo, normal_timeout, uuid, deviceType, channel);

        }

        if (deviceSensorMessage.getData().startsWith("5252")){
            LoraXiaoFangShuanDeviceInfoV2 deviceInfoV2=new LoraXiaoFangShuanDeviceInfoV2(jsonObject);
            //处理消防栓
            parsingHandle(deviceInfoV2, normal_timeout, uuid, deviceType, channel);
        }

        if (deviceSensorMessage.getData().contains("5151")){
            XiaoFangShuanNBDeviceInfoV2 deviceInfo=new XiaoFangShuanNBDeviceInfoV2(jsonObject);
            parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);
        }




    }



}
