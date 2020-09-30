package cn.turing.firecontrol.server.controller;

import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.server.base.Constant;
import cn.turing.firecontrol.server.business.BusinessI;
import cn.turing.firecontrol.server.entity.SensorDetail;
import cn.turing.firecontrol.server.feign.IDeviceFeign;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class MQTTService {

    /**
     *
     * @param message
     * @param businessI
     * @param iDeviceFeign
     */
    public void mqttAccept(byte [] message, BusinessI businessI, IDeviceFeign iDeviceFeign) {
        try {
            String string = new String(message);
            JSONObject jsonObject = JSONObject.parseObject(string);
            XshController xshController = new XshController();
            dealMessage(string,jsonObject,iDeviceFeign,xshController,businessI);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void dealMessage(String string,JSONObject jsonObject,IDeviceFeign iDeviceFeign,XshController xshController,BusinessI businessI){
        if (string.contains("type")) {
            nbType(jsonObject,iDeviceFeign,xshController,businessI);
        } else {
            String data = jsonObject.getString("data");
            String id = jsonObject.getString("id");
            try {
                xshController.turing(data, jsonObject, id, iDeviceFeign, businessI);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void nbType(JSONObject jsonObject,IDeviceFeign iDeviceFeign,XshController xshController,BusinessI businessI){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (jsonObject.getString("type").equals("ONLINE_OFFLINE")){
            JSONObject jsonObject1=jsonObject.getJSONObject("data");
            Date date=jsonObject.getDate("publishTime");
            SensorDetail sensorDetail=new SensorDetail();
            sensorDetail.setAlarmCode(Constant.YG);
            if (jsonObject1.getString("status").equals("ONLINE")){
                iDeviceFeign.updateStatus(jsonObject1.getString("physicalNumber"),sdf.format(date),"2");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmType("正常");
                sensorDetail.setAlarmValue(Constant.ST_NORM);
            }else if (jsonObject1.getString("status").equals("OFFLINE")){
                iDeviceFeign.updateStatus(jsonObject1.getString("physicalNumber"),sdf.format(date),"4");
                iDeviceFeign.updateStatus(jsonObject1.getString("physicalNumber"),sdf.format(date),"2");
                sensorDetail.setAlarmStatus(Constant.ST_OFFLINE);
                sensorDetail.setAlarmType("离线");
                sensorDetail.setAlarmValue(Constant.ST_OFFLINE);
            }
            String uuid = UUIDUtils.generateUuid();
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("deviceid",jsonObject1.getString("physicalNumber").toLowerCase());
            jsonObject2.put("sensorCode", "xsh");
            jsonObject2.put("type", jsonObject.getString("type"));
            jsonObject2.put("uploadtime",date);
            jsonObject2.put("recievetime",new Date());
            JSONObject val = new JSONObject();
            val.put("alarmValue",sensorDetail.getAlarmValue());
            val.put("alarmType",sensorDetail.getAlarmType());
            val.put("alarmStatus",sensorDetail.getAlarmStatus());
            jsonObject2.put(sensorDetail.getAlarmCode(),val);
            log.info("写设备日志:" + jsonObject2.toJSONString());
            businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, jsonObject2);

        }else{
            xshController.nbYangan(jsonObject, businessI);
        }
    }
}
