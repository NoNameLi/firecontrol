package cn.turing.firecontrol.server.handler.device;

import cn.turing.common.entity.easylinkin.CombustibleGasDeviceInfoV1;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.feign.IDeviceFeign;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 惠联无限的烟感门磁可燃气体
 */
@Component
@Slf4j
@Order(99)
public class EasyLinkInCombustibleGasHandler extends AbstractDeviceEventHandler{

    //离线时间24个小时
    public static Integer normal_timeout=86400;
    //可燃气体
    public static String deviceType="KRQT";
    //厂商 ：惠联无限
    public static String channel="easyLinkIn";

    @Autowired
    IDeviceFeign deviceFeign;


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.LIST.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }

        if ("02".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        String data=deviceSensorMessage.getData();
        if (data.startsWith("02")){
            return true;
        }

        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        String data=deviceSensorMessage.getData();
        if (data.startsWith("02")){
            return true;
        }

        return false;
    }

    @Override
    public void handle(DeviceSensorMessage deviceSensorMessage,String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(deviceSensorMessage);
        JSONObject sensor=deviceFeign.selectBySensorNo(deviceSensorMessage.getId());
        JSONArray jsonArray=deviceFeign.getMeasuringPoints(sensor.getJSONObject("data").getInteger("id"));
        if (jsonArray.size()>0 && !sensor.isEmpty()){
            for (int i=0;i<jsonArray.size();i++){
                String codeName=jsonArray.getJSONObject(i).getString("codeName");
                if (LoraDeviceTypeConst.POINTS.contains(codeName)){
                    CombustibleGasDeviceInfoV1 deviceInfo=new CombustibleGasDeviceInfoV1(jsonObject,codeName);
                    parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);
                    break;
                }
            }

        }

    }


}
