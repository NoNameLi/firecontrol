package cn.turing.firecontrol.server.controller;

import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.server.base.Constant;
import cn.turing.firecontrol.server.business.BusinessI;
import cn.turing.firecontrol.server.entity.SensorBoss;
import cn.turing.firecontrol.server.entity.xinhaosi.yangan.NbYanGanDeviceInfoV1;
import cn.turing.firecontrol.server.feign.IDeviceFeign;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * 消息处理公共类
 */
@Slf4j
@Component
public  class XshController {
    private static String  channelCode = "Xsh";
    private static String  channelCode1="SedwellWille";
    private static String  channelCode_turing="turing";


    /**
     * NB 烟感
     * @param jsonObject json原始数据
     */
    public void nbYangan(JSONObject jsonObject,BusinessI businessI) {
        NbYanGanDeviceInfoV1 deviceInfoV1=new NbYanGanDeviceInfoV1(jsonObject);
        log.error("将数据转为日志对象:");
        String uuid = UUIDUtils.generateUuid();
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV1.getDevice_id());
        map.put("uploadtime", deviceInfoV1.getUpload_time());
        map.put("recievetime", deviceInfoV1.getRecieve_time());
        map.put("rawdata", jsonObject.toJSONString());

        log.error("将中科turing液位数据转为对象,并检查和更新状态:");
//        businessI.updateInRedis(deviceInfoV1, 259200);
        if (deviceInfoV1.isHas_alarm() || deviceInfoV1.isHas_guzhang()) {
            log.error("报警发送报警:");
            String type="wlsb";//报警的模块类型
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV1,type);
        } else {
            log.error("更新为正常状态:" + deviceInfoV1.getDevice_id());
            businessI.updateStatus(deviceInfoV1.getDevice_id(), deviceInfoV1, "2");
            businessI.abnormalRestore(deviceInfoV1.getDevice_id());
        }
        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV1.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);

    }

    /**
     *
     * @param data
     * @param jsonObject
     * @param id
     * @throws Exception
     */
    public void turing(String data,JSONObject jsonObject,String id,IDeviceFeign iDeviceFeign,BusinessI businessI) throws Exception {
        SensorBoss sensorBoss=new SensorBoss();
        if (data.startsWith("22")&& data.length()==12){ //赛特威尔产品
            sensorBoss.sedwellWille(data,channelCode1, data,jsonObject,businessI);
        } else if (data.startsWith("04") && 12<=data.length() && data.length()<=16){ //04 表示温湿度终端
            sensorBoss.temperatureAndHumidity(data,channelCode, data,jsonObject,businessI);
        }else if (data.startsWith("04") && data.length()==20){ //04 表示温湿度终端
            sensorBoss.temperatureAndHumidityV2(data,channelCode, data,jsonObject,businessI);
        } else if (data.startsWith("00000000")){ //液位传感器
            sensorBoss.liquidLevel(data,channelCode, data,jsonObject,businessI);
        }else if(id.equals("393833365B378211") ||id.equals("3739343585367013")){ //v2 液位
            sensorBoss.liquidLevelV2(data,channelCode, data,jsonObject,businessI);
        } else if (data.startsWith("A5")){//智能消防头盔
            sensorBoss.FireHelmet(data,channelCode, data,jsonObject,businessI);
        }else if (data.startsWith("03")){ //SOS
            sensorBoss.SOSPushButton(data,channelCode, data,jsonObject,businessI);
        }else if (data.startsWith("040013") && data.length()==50){ //电测
            sensorBoss.electricalMeasurement(data,channelCode, data,jsonObject,businessI);
        } else if (data.startsWith("FA")&& data.endsWith("F5")){
            sensorBoss.gongyuanAll(iDeviceFeign,id,data,jsonObject,channelCode,data,businessI);
        }else if (data.startsWith("1D")){//井盖v2
            sensorBoss.wellCoverDeviceInfoV1(data,channelCode,data,jsonObject,businessI);
        }else if (data.startsWith("6060")){//电表
            sensorBoss.electricMeter(data, channelCode,data, jsonObject,businessI);
        }else if (data.startsWith("7070")){//电表
            sensorBoss.waterMeter(data,channelCode,data, jsonObject,businessI);
        }else if (data.startsWith("1422")){//液位V3
            sensorBoss.liquidLevelV3(data,channelCode, data,jsonObject,businessI);
        }else if (data.startsWith("5050") && data.length() == 28) {
            sensorBoss.turingXiaohuoshuang(data, channelCode,data, jsonObject,businessI);
        }else if (data.startsWith("4040") && data.length() == 60){
            sensorBoss.easyLiak(data, channelCode,data, jsonObject,businessI);
        }else if (data.startsWith("4040") && data.length() == 64){
            sensorBoss.easyLiakv2(data, channelCode,data, jsonObject,businessI);
        }else if (data.startsWith("8080") && data.endsWith("5A5A")){
            sensorBoss.turingAir(channelCode_turing,data,jsonObject,businessI);
        }else if (data.startsWith("9090") && data.endsWith("5A5A")){
            sensorBoss.turingLiquidLevel(channelCode_turing,data,jsonObject,businessI);
        }else if (data.startsWith("02")) {
            sensorBoss.easyLinkInCombustibleGas(channelCode_turing, data, jsonObject, businessI,iDeviceFeign);
        }else if (data.startsWith("A0A0")){
            sensorBoss.turingInfrared(channelCode_turing, data, jsonObject, businessI);
        }else if (data.startsWith("B0B0")){
            sensorBoss.turingElectricalFire(channelCode_turing,data,jsonObject,businessI);
        }else if (data.startsWith("C0C0")){
            sensorBoss.turingSmoking(channelCode_turing,data,jsonObject,businessI);
        }
        //C0C001010001F40DE00364
        else{
            log.error("接收到无法识别的数据"+data);
        }
    }
}
