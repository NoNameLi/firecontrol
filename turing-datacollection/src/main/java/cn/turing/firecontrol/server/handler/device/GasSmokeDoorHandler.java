package cn.turing.firecontrol.server.handler.device;


import cn.turing.firecontrol.server.base.Constant;
import cn.turing.firecontrol.server.constant.LoraDeviceTypeConst;
import cn.turing.firecontrol.server.feign.IDeviceFeign;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(87)
@Slf4j
public class GasSmokeDoorHandler extends AbstractDeviceEventHandler {
    /**
     * 1个小时上传一次数据
     */
    //离线时间24个小时
    public static Integer normal_timeout=86400;
    //可燃气体
    public static String deviceType="KRQT";
    //厂商中科图灵
    public static String channel="TUR";

    @Autowired
    IDeviceFeign iDeviceFeign;

    public String codeName;

    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        if (LoraDeviceTypeConst.NB_GAS.equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        if ("02".equals(deviceSensorMessage.getDeviceType())){
            return true;
        }
        String data=deviceSensorMessage.getData();
        if (data.startsWith("02") ){
            return true;
        }
        return  false;
    }

    @Override
    public boolean preHandle(DeviceSensorMessage deviceSensorMessage) {
        //加了iccid卡
        String data=deviceSensorMessage.getData();
        if (data.startsWith("02") ){
            return true;
        }

        return false;
    }

    @Override
    public void handle(DeviceSensorMessage warningMessage, String uuid) throws Exception {
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON(warningMessage);
        String code = "";
        List list = new ArrayList();
        JSONObject jsonObject1 = null;
        JSONArray array = null;
        try {
            jsonObject1 = iDeviceFeign.selectBySensorNo(jsonObject.getString("id"));
            array = iDeviceFeign.getMeasuringPoints(jsonObject1.getJSONObject("data").getInteger("id"));//查询设备是否已插入
        } catch (NullPointerException e) {
            log.warn(e.toString()+"未查找到该设备");
        }
        if (array != null && jsonObject1 != null) {
            for (int i = 0; i < array.size(); i++) {

                String conent = array.getJSONObject(i).getString("codeName");
                list.add(conent);
            }
            for (int i = 0; i < list.size(); i++) {
                code = (String) list.get(i);
                if (code.equals(Constant.YG) || code.equals(Constant.DM) || code.equals(Constant.COMG)
                ) {//|| code.equals(Constant.DL)
                    codeName = code;
                    break;
                }
            }

        }
        cn.turing.common.entity.easylinkin.CombustibleGasDeviceInfoV1 deviceInfo=new cn.turing.common.entity.easylinkin.CombustibleGasDeviceInfoV1(jsonObject,code);
        parsingHandle(deviceInfo,normal_timeout,uuid, deviceType,channel);
    }
}
