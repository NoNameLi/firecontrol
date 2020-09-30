package cn.turing.firecontrol.server.entity;

import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.server.base.Constant;
import cn.turing.firecontrol.server.business.BusinessI;
import cn.turing.firecontrol.server.entity.easylinkin.CombustibleGasDeviceInfoV1;
import cn.turing.firecontrol.server.entity.rabbitmq.*;
import cn.turing.firecontrol.server.entity.sedwellWille.YanGanDeviceV1;
import cn.turing.firecontrol.server.entity.turing.*;
import cn.turing.firecontrol.server.entity.xinhaosi.XinHaoSiDeviceInfoMQV1;
import cn.turing.firecontrol.server.entity.xinhaosi.XinHaoSiDeviceInfoMQV2;
import cn.turing.firecontrol.server.feign.IDeviceFeign;
import cn.turing.firecontrol.server.util.ByteUtil;
import cn.turing.firecontrol.server.util.CRC16Modbus;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SensorBoss {
    private Integer rabbit_timeout=75600;//7小时21600*3
    private Integer rabbit_timeout_yw=75600; //液位 7小时
    private Integer rabbit_timeout_xftk=9000; //消防头盔 5min
    private Integer rabbit_timeout_dc=180000;
    private Integer rabbit_timeout_SedW=259200;
    private Integer xiaofangshuan_timeout=4800;
    private Integer normal_timeout=3960;
    private Integer turing_air_timeout=180;//周期一分钟*3 空气
    private Integer TURING_LIQUID_TIMEOUT=21600;//周期2小时*3 液位
    private Integer EASYLINKIN_GAS_TIMEOUT=21600;// 可燃气体
    public String codeName;

    /**
     *温湿度
     * @param string
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public void temperatureAndHumidity (String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.error("将数据转为设备对象:" + string);
        TempHumidityGatewayServerDeviceInfoV2 deviceInfoV1=new TempHumidityGatewayServerDeviceInfoV2( jsonObject);

        String uuid= UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV1.getDevice_id().toLowerCase());
        map.put("uploadtime", deviceInfoV1.getUpload_time());
        map.put("recievetime", deviceInfoV1.getRecieve_time());
        map.put("rawdata", data);
        log.error("将数据转为对象,并检查和更新状态:");
        //报警
        if (deviceInfoV1.isHas_alarm() || deviceInfoV1.isHas_guzhang()) {
            log.error("报警发送报警:");
//            businessI.sendAlarm(uuid, deviceInfoV1);
            String type="wlsb";//物联设备
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV1,type);
        } else {
            log.error("更新为正常状态 :" + deviceInfoV1.getDevice_id());
            businessI.updateStatus(deviceInfoV1.getDevice_id(), deviceInfoV1, "2");
            businessI.abnormalRestore(deviceInfoV1.getDevice_id());
        }

        businessI.updateInRedis(deviceInfoV1, rabbit_timeout);

        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV1.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }

    /**
     * V2
     * @param string
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public void temperatureAndHumidityV2 (String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.error("将温湿度v2转为设备对象:" + string);
        TempHumidityGatewayServerDeviceInfoV1 deviceInfoV1=new TempHumidityGatewayServerDeviceInfoV1( jsonObject);
        String uuid= UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV1.getDevice_id().toLowerCase());
        map.put("uploadtime", deviceInfoV1.getUpload_time());
        map.put("recievetime", deviceInfoV1.getRecieve_time());
        map.put("rawdata", data);
        log.error("将数据转为对象,并检查和更新状态:");
        //报警
        if (deviceInfoV1.isHas_alarm() || deviceInfoV1.isHas_guzhang()) {
            log.error("报警发送报警:");
//            businessI.sendAlarm(uuid, deviceInfoV1);

            String type="wlsb";//物联设备
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV1,type);

        } else {
            log.error("更新为正常状态 :" + deviceInfoV1.getDevice_id());
            businessI.updateStatus(deviceInfoV1.getDevice_id(), deviceInfoV1, "2");
            businessI.abnormalRestore(deviceInfoV1.getDevice_id());
        }

        businessI.updateInRedis(deviceInfoV1, rabbit_timeout);

        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV1.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }
    /**
     * 液位
     * @param string
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public void liquidLevel(String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.error("将数据转为设备对象:" + string);
        LiquidLlevelDeviceInfoV1 deviceInfoV1=new LiquidLlevelDeviceInfoV1(jsonObject);

        String uuid= UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV1.getDevice_id().toLowerCase());
        map.put("uploadtime", deviceInfoV1.getUpload_time());
        map.put("recievetime", deviceInfoV1.getRecieve_time());
        map.put("rawdata", data);
        log.error("将数据转为对象,并检查和更新状态:");
        //报警
        if (deviceInfoV1.isHas_alarm() || deviceInfoV1.isHas_guzhang()) {
            log.error("报警发送报警:");
//            businessI.sendAlarm(uuid, deviceInfoV1);
            String type="wlsb";//物联设备
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV1,type);
        } else {
            log.error("更新为正常状态 :" + deviceInfoV1.getDevice_id());
            businessI.updateStatus(deviceInfoV1.getDevice_id(), deviceInfoV1, "2");
            businessI.abnormalRestore(deviceInfoV1.getDevice_id());
        }

        businessI.updateInRedis(deviceInfoV1, rabbit_timeout_yw);

        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV1.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }

    /**
     * 液位v2
     * @param string
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public void liquidLevelV2(String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        LiquidLlevelDeviceInfoV2 deviceInfoV2=new LiquidLlevelDeviceInfoV2(jsonObject);
        String uuid= UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV2.getDevice_id().toLowerCase());
        map.put("uploadtime", deviceInfoV2.getUpload_time());
        map.put("recievetime", deviceInfoV2.getRecieve_time());
        map.put("rawdata", data);
        log.error("将数据转为对象,并检查和更新状态:");
        //报警
        if (deviceInfoV2.isHas_alarm() || deviceInfoV2.isHas_guzhang()) {
            log.error("报警发送报警:");
//            businessI.sendAlarm(uuid, deviceInfoV2);
            String type="wlsb";//物联设备
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV2,type);
        } else {
            log.error("更新为正常状态 :" + deviceInfoV2.getDevice_id());
            businessI.updateStatus(deviceInfoV2.getDevice_id(), deviceInfoV2, "2");
            businessI.abnormalRestore(deviceInfoV2.getDevice_id());
        }

        businessI.updateInRedis(deviceInfoV2, rabbit_timeout_yw);
        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV2.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }

    /**
     * 消防头盔
     * @param string
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public void FireHelmet(String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.error("将数据转为设备对象:" + string);
        XiaoFangTouKuiDeviceInfoV1 deviceInfoV1=new XiaoFangTouKuiDeviceInfoV1(jsonObject);
        String uuid= UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV1.getDevice_id().toLowerCase());
        map.put("uploadtime", deviceInfoV1.getUpload_time());
        map.put("recievetime", deviceInfoV1.getRecieve_time());
        map.put("rawdata", data);
        log.error("将数据转为对象,并检查和更新状态:");
        //报警
        if (deviceInfoV1.isHas_alarm() || deviceInfoV1.isHas_guzhang()) {
            log.error("报警发送报警:");
//            businessI.sendAlarm(uuid, deviceInfoV1);
            String type="wlsb";//物联设备
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV1,type);

        } else {
            log.error("更新为正常状态 :" + deviceInfoV1.getDevice_id());
            businessI.updateStatus(deviceInfoV1.getDevice_id(), deviceInfoV1, "2");
            businessI.abnormalRestore(deviceInfoV1.getDevice_id());
        }

        businessI.updateInRedis(deviceInfoV1, rabbit_timeout_xftk);
        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV1.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }

    /**
     * SOS按钮.没有心跳周期就没有存入Radis中
     * @param string
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public void SOSPushButton(String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.error("将数据转为设备对象:" + string);
        if (string.length()==8){
            SOSDeviceInfoV2 deviceInfoV2=new SOSDeviceInfoV2(jsonObject);
            String uuid= UUIDUtils.generateUuid();
            log.error("将数据转为日志对象:");
            Map<String, Object> map = Maps.newHashMap();
            map.put("channel", channelCode);
            map.put("deviceid", deviceInfoV2.getDevice_id().toLowerCase());
            map.put("uploadtime", deviceInfoV2.getUpload_time());
            map.put("recievetime", deviceInfoV2.getRecieve_time());
            map.put("rawdata", data);
            log.error("将数据转为对象,并检查和更新状态:");
            //报警
            if (deviceInfoV2.isHas_alarm() || deviceInfoV2.isHas_guzhang()) {
                log.error("报警发送报警:");
                // businessI.sendAlarm(uuid, deviceInfoV1);
                String type="wlsb";//物联设备
                businessI.sendDeviceAlarmMQ(uuid,deviceInfoV2,type);
            } else {
                log.error("更新为正常状态1:" + deviceInfoV2.getDevice_id());
                businessI.updateStatus(deviceInfoV2.getDevice_id(), deviceInfoV2, "2");
                businessI.abnormalRestore(deviceInfoV2.getDevice_id());
            }

            log.info("写raw日志:");
            businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
            //数据存储到ES索引中
            JSONObject deviceJSON = deviceInfoV2.toDeviceJSON();
            log.info("写设备日志:" + deviceJSON.toJSONString());
            businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
        }else{
            SOSDeviceInfoV1 deviceInfoV1=new SOSDeviceInfoV1(jsonObject);
            String uuid= UUIDUtils.generateUuid();
            log.error("将数据转为日志对象:");
            Map<String, Object> map = Maps.newHashMap();
            map.put("channel", channelCode);
            map.put("deviceid", deviceInfoV1.getDevice_id().toLowerCase());
            map.put("uploadtime", deviceInfoV1.getUpload_time());
            map.put("recievetime", deviceInfoV1.getRecieve_time());
            map.put("rawdata", data);
            log.error("将数据转为对象,并检查和更新状态:");
            //报警
            if (deviceInfoV1.isHas_alarm() || deviceInfoV1.isHas_guzhang()) {
                log.error("报警发送报警:");
                // businessI.sendAlarm(uuid, deviceInfoV1);
                String type="wlsb";//物联设备
                businessI.sendDeviceAlarmMQ(uuid,deviceInfoV1,type);
            } else {
                log.error("更新为正常状态1:" + deviceInfoV1.getDevice_id());
                businessI.updateStatus(deviceInfoV1.getDevice_id(), deviceInfoV1, "2");
                businessI.abnormalRestore(deviceInfoV1.getDevice_id());
            }

            log.info("写raw日志:");
            businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
            //数据存储到ES索引中
            JSONObject deviceJSON = deviceInfoV1.toDeviceJSON();
            log.info("写设备日志:" + deviceJSON.toJSONString());
            businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
        }


    }

    /**
     * 井盖
     * @param string
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public void wellCover (String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.error("将数据转为设备对象:" + string);
        WellCoverDeviceInfoV1 deviceInfoV1=new WellCoverDeviceInfoV1(jsonObject);
        String uuid= UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV1.getDevice_id().toLowerCase());
        map.put("uploadtime", deviceInfoV1.getUpload_time());
        map.put("recievetime", deviceInfoV1.getRecieve_time());
        map.put("rawdata", data);
        log.error("将数据转为对象,并检查和更新状态:");
        //报警
        if (deviceInfoV1.isHas_alarm() || deviceInfoV1.isHas_guzhang()) {
            log.error("报警发送报警:");
//            businessI.sendAlarm(uuid, deviceInfoV1);
            String type="wlsb";
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV1,type);
        } else {
            log.error("更新为正常状态  :" + deviceInfoV1.getDevice_id());
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
     * 电测
     * @param string
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public void electricalMeasurement(String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.error("将数据转为设备对象:" + string);
        ElectricalMeasurementDeviceInfoV1 deviceInfoV1=new ElectricalMeasurementDeviceInfoV1(jsonObject);
        String uuid= UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV1.getDevice_id().toLowerCase());
        map.put("uploadtime", deviceInfoV1.getUpload_time());
        map.put("recievetime", deviceInfoV1.getRecieve_time());
        map.put("rawdata", data);
        log.error("将数据转为对象,并检查和更新状态:");
        //报警
        if (deviceInfoV1.isHas_alarm() || deviceInfoV1.isHas_guzhang()) {
            log.error("报警发送报警:");
//            businessI.sendAlarm(uuid, deviceInfoV1);
            String type="wlsb";
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV1,type);
        } else {
            log.error("更新为正常状态1 :" + deviceInfoV1.getDevice_id());
            businessI.updateStatus(deviceInfoV1.getDevice_id(), deviceInfoV1, "2");
            businessI.abnormalRestore(deviceInfoV1.getDevice_id());
        }

        businessI.updateInRedis(deviceInfoV1, rabbit_timeout_dc);
        String gongyuandiance = "gongyuandiance";
        businessI.updateInRedisTwo(gongyuandiance);
        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV1.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }
    public void sedwellWille(String string, String channelCode1, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        YanGanDeviceV1 deviceV1=new YanGanDeviceV1(jsonObject);
        String uuid= UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode1);
        map.put("deviceid", deviceV1.getDevice_id().toLowerCase());
        map.put("uploadtime", deviceV1.getUpload_time());
        map.put("recievetime", deviceV1.getRecieve_time());
        map.put("rawdata", data);
        log.error("将数据转为对象,并检查和更新状态:");
        //报警
        if (deviceV1.isHas_alarm() || deviceV1.isHas_guzhang()) {
            log.error("报警发送烟感报警:");
//            businessI.sendAlarm(uuid, deviceV1);
            String type="yg";
            businessI.sendDeviceAlarmMQ(uuid,deviceV1,type);
        } else {
            log.error("更新为正常状态1 :" + deviceV1.getDevice_id());
            businessI.updateStatus(deviceV1.getDevice_id(), deviceV1, "2");
            businessI.abnormalRestore(deviceV1.getDevice_id());
        }
        businessI.updateInRedis(deviceV1,rabbit_timeout_SedW);
        String sedw = "SedwellWille";
        businessI.updateInRedisTwo(sedw);
        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceV1.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }

    /**
     * 躬远设备。门磁、水浸、红外、感烟、
     * @param iDeviceFeign
     * @param id
     * @param string
     * @param jsonObject
     * @param channelCode
     * @param data
     * @param businessI
     * @throws Exception
     */
    public void gongyuanAll(IDeviceFeign iDeviceFeign, String id, String string, JSONObject jsonObject, String channelCode, String data, BusinessI businessI) {
        String code = "";
        List list = new ArrayList();
        JSONObject jsonObject1 = null;
        JSONArray array = null;
        try {
            jsonObject1 = iDeviceFeign.selectBySensorNo(id);
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
                if (code.equals(Constant.YG) || code.equals(Constant.INFR) || code.equals(Constant.WF) || code.equals(Constant.DM)
                        || code.equals(Constant.XTEMP)
                ) {//|| code.equals(Constant.DL)
                    codeName = code;
                    break;
                }
            }
            log.error("将数据转为设备对象:" + string);
            Integer doorType=0;
            try {
                JSONObject object = iDeviceFeign.getFireDoorNormalStatusBySensorId(id);
                doorType = object.getInteger("data");//0表示未找到防火门　1表示常开门　２表示　常闭门
            }catch (Exception e){
                e.printStackTrace();
            }
            GongYuanDeviceinfoV1 deviceinfo = null;
            try {
                deviceinfo = new GongYuanDeviceinfoV1(jsonObject, codeName, doorType);//doorType
            } catch (Exception e) {
                e.printStackTrace();
            }

            String uuid = UUIDUtils.generateUuid();
            log.error("将数据转为日志对象:");
            Map<String, Object> map = Maps.newHashMap();
            map.put("channel", channelCode);
            map.put("deviceid", deviceinfo.getDevice_id().toLowerCase());
            map.put("uploadtime", deviceinfo.getUpload_time());
            map.put("recievetime", deviceinfo.getRecieve_time());
            map.put("rawdata", data);
            log.error("将数据转为对象,并检查和更新状态:");
            if (data.substring(8, 12).equalsIgnoreCase(CRC16Modbus.sendMessageHexByte(data.substring(2, 8)))) {
                if (Constant.DM.equals(code)) {

                    log.info("推送门磁信息:");
                    businessI.sendFireDoorMessage(uuid, deviceinfo);
                    // TODO: 2019/3/13  两个条件deviceId，1 开 2关 0；
                    if (deviceinfo.isHas_alarm()){//1表示常开门　２表示　常闭门
                        if (doorType==1){
                            businessI.updateFireDoorStatus(deviceinfo.getDevice_id().toLowerCase(),2);
                        }else if (doorType==2){
                            businessI.updateFireDoorStatus(deviceinfo.getDevice_id().toLowerCase(),1);
                        }
                    }else if (deviceinfo.isHas_guzhang()){
                        businessI.updateFireDoorStatus(deviceinfo.getDevice_id().toLowerCase(),0);
                    }else{
                        if (doorType==1){
                            businessI.updateFireDoorStatus(deviceinfo.getDevice_id().toLowerCase(),1);
                        }else if (doorType==2){
                            businessI.updateFireDoorStatus(deviceinfo.getDevice_id().toLowerCase(),2);
                        }
                    }
                    businessI.updateInRedisByFireDoor(deviceinfo, rabbit_timeout, uuid);
                } else {
                    //报警
                    if (deviceinfo.isHas_alarm() || deviceinfo.isHas_guzhang()) {
                        log.error("报警发送报警:");
//                        businessI.sendAlarm(uuid, deviceinfo);
                        String type="wlsb";
                        businessI.sendDeviceAlarmMQ(uuid,deviceinfo,type);
                    } else {
                        businessI.abnormalRestore(deviceinfo.getDevice_id());
                        log.error("更新为正常状态:" + deviceinfo.getDevice_id());
                        businessI.updateStatus(deviceinfo.getDevice_id(), deviceinfo, "2");
                    }
                    businessI.updateInRedis(deviceinfo, rabbit_timeout);
                }

                String gongyuan = "gongyuan";
                businessI.updateInRedisTwo(gongyuan);

                //存储基本日志 到ES数据库
                log.info("写raw日志:");
                businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
                //数据存储到ES索引中
                JSONObject deviceJSON = deviceinfo.toDeviceJSON();
                log.info("写设备日志:" + deviceJSON.toJSONString());
                businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);

            } else {
                //存储基本日志 到ES数据库
                log.error("crc 验证失败，只写raw日志:");
                businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
            }
        } else {
            log.info("没有" + id + "这个传感器");
        }

    }

    /**
     *
     * @param string
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public  void wellCoverDeviceInfoV1(String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.info("将井盖数据转为设备对象"+string);
        WellCoverDeviceInfoV2 deviceInfoV2=new WellCoverDeviceInfoV2(jsonObject);
        String uuid= UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV2.getDevice_id().toLowerCase());
        map.put("uploadtime", deviceInfoV2.getUpload_time());
        map.put("recievetime", deviceInfoV2.getRecieve_time());
        map.put("rawdata", data);
        log.error("将数据转为对象,并检查和更新状态:");
        //报警
        if (deviceInfoV2.isHas_alarm() || deviceInfoV2.isHas_guzhang()) {
            log.error("报警发送报警:");
//            businessI.sendAlarm(uuid, deviceInfoV2);
            String type="wlsb";
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV2,type);
        } else {
            businessI.abnormalRestore(deviceInfoV2.getDevice_id());
            log.error("更新为正常状态  :" + deviceInfoV2.getDevice_id());
            businessI.updateStatus(deviceInfoV2.getDevice_id(), deviceInfoV2, "2");
        }

        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV2.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }

    /**
     *
     * @param string
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public void electricMeter(String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.info("将电表数据转为设备对象"+string);
        ElectricMeter deviceInfoV2=new ElectricMeter(jsonObject);
        String uuid= UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV2.getDevice_id().toLowerCase());
        map.put("uploadtime", deviceInfoV2.getUpload_time());
        map.put("recievetime", deviceInfoV2.getRecieve_time());
        map.put("rawdata", data);
        log.error("将数据转为对象,并检查和更新状态:");
        //报警
        if (deviceInfoV2.isHas_alarm() || deviceInfoV2.isHas_guzhang()) {
            log.error("报警发送报警:");
//            businessI.sendAlarm(uuid, deviceInfoV2);
            String type="wlsb";
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV2,type);
        } else {
            log.error("更新为正常状态  :" + deviceInfoV2.getDevice_id());
            businessI.updateStatus(deviceInfoV2.getDevice_id(), deviceInfoV2, "2");
            businessI.abnormalRestore(deviceInfoV2.getDevice_id());
        }

        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV2.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }
    public void waterMeter(String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception{
        log.info("将水表数据转为设备对象"+string);
        WeterMeter deviceInfo=new WeterMeter(jsonObject);
        String uuid= UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfo.getDevice_id().toLowerCase());
        map.put("uploadtime", deviceInfo.getUpload_time());
        map.put("recievetime", deviceInfo.getRecieve_time());
        map.put("rawdata", data);
        log.error("将数据转为对象,并检查和更新状态:");
        //报警
        if (deviceInfo.isHas_alarm() || deviceInfo.isHas_guzhang()) {
            log.error("报警发送报警:");
//            businessI.sendAlarm(uuid, deviceInfo);

            String type="wlsb";
            businessI.sendDeviceAlarmMQ(uuid,deviceInfo,type);
        } else {
            log.error("更新为正常状态  :" + deviceInfo.getDevice_id());
            businessI.updateStatus(deviceInfo.getDevice_id(), deviceInfo, "2");
            businessI.abnormalRestore(deviceInfo.getDevice_id());
        }

        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfo.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }

    public void liquidLevelV3(String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.info("将液位设备v3转为:"+string);
        LiquidLlevelDeviceInfoV3 deviceInfoV2=new LiquidLlevelDeviceInfoV3(jsonObject);
        String uuid= UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV2.getDevice_id().toLowerCase());
        map.put("uploadtime", deviceInfoV2.getUpload_time());
        map.put("recievetime", deviceInfoV2.getRecieve_time());
        map.put("rawdata", data);
        log.error("将数据转为对象,并检查和更新状态:");
        //报警
        if (deviceInfoV2.isHas_alarm() || deviceInfoV2.isHas_guzhang()) {
            log.error("报警发送报警:");
//            businessI.sendAlarm(uuid, deviceInfoV2);
            String type="wlsb";
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV2,type);
        } else {
            log.error("更新为正常状态 :" + deviceInfoV2.getDevice_id());
            businessI.updateStatus(deviceInfoV2.getDevice_id(), deviceInfoV2, "2");
            businessI.abnormalRestore(deviceInfoV2.getDevice_id());
        }

        businessI.updateInRedis(deviceInfoV2, rabbit_timeout_yw);
        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV2.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }

    /**
     * 中科turing消火栓
     * @param string
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public void turingXiaohuoshuang(String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        //暂定为5050 turing
        log.error("将数据转为设备对象:" + string);
        XiaoFangShuanDeviceInfoV1 deviceInfo = new XiaoFangShuanDeviceInfoV1(jsonObject);

        String uuid = UUIDUtils.generateUuid();
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfo.getDevice_id());
        map.put("uploadtime", deviceInfo.getUpload_time());
        map.put("recievetime", deviceInfo.getRecieve_time());
        map.put("rawdata", data);

        //判断会否是available 的数据。
        if (ByteUtil.checkXHSDeviceCRC2(deviceInfo.getData())) {
            log.error("将数据转为对象,并检查和更新状态:");
            businessI.updateInRedis(deviceInfo, xiaofangshuan_timeout);
            //报警
            if (deviceInfo.isHas_alarm() || deviceInfo.isHas_guzhang()) {
                log.error("报警发送报警:");
//                businessI.sendAlarm(uuid, deviceInfo);
                String type="wlsb";
                businessI.sendDeviceAlarmMQ(uuid,deviceInfo,type);
            } else {
                log.error("更新为正常状态:" + deviceInfo.getDevice_id());
                businessI.updateStatus(deviceInfo.getDevice_id(), deviceInfo, "2");
                businessI.abnormalRestore(deviceInfo.getDevice_id());
            }
            String turingXiaoFangShuan = "turingXiaoFangShuan";
            businessI.updateInRedisTwo(turingXiaoFangShuan);
            //存储基本日志 到ES数据库
            log.info("写raw日志:");
            businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
            //数据存储到ES索引中
            JSONObject deviceJSON = deviceInfo.toDeviceJSON();
            log.info("写设备日志:" + deviceJSON.toJSONString());
            businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
        } else {
            //存储基本日志 到ES数据库
            log.error("crc 验证失败，只写raw日志:");
            businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        }
    }
    public void easyLiak(String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {

            log.error("将数据转为设备对象:" + string);
            XinHaoSiDeviceInfoMQV1 deviceInfo = new XinHaoSiDeviceInfoMQV1(jsonObject);

            String uuid = UUIDUtils.generateUuid();

            log.error("将数据转为日志对象:");
            Map<String, Object> map = Maps.newHashMap();
            map.put("channel", channelCode);
            map.put("deviceid", deviceInfo.getDevice_id());
            map.put("uploadtime", deviceInfo.getUpload_time());
            map.put("recievetime", deviceInfo.getRecieve_time());
            map.put("rawdata", data);

            //判断会否是available 的数据。
            if (ByteUtil.checkXHSDeviceCRC(deviceInfo.getData())) {
                log.error("将数据转为对象,并检查和更新状态:");
                log.info("设备信息存入REDIS");
                businessI.updateInRedis(deviceInfo, normal_timeout);
                //报警
                if (deviceInfo.isHas_alarm() || deviceInfo.isHas_guzhang()) {
                    log.error("报警发送报警:");
//                    businessI.sendAlarm(uuid, deviceInfo);
                    String type="wlsb";
                    businessI.sendDeviceAlarmMQ(uuid,deviceInfo,type);
                } else {
                    log.error("更新为正常状态:" + deviceInfo.getDevice_id());
                    businessI.updateStatus(deviceInfo.getDevice_id(), deviceInfo, "2");
                    businessI.abnormalRestore(deviceInfo.getDevice_id());
                    log.info("调更新设备状态成功");
                }
                String dianqihuozai = "dianqihuozai";
                businessI.updateInRedisTwo(dianqihuozai);
                //存储基本日志 到ES数据库
                log.info("写raw日志:");
                businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
                //数据存储到ES索引中
                JSONObject deviceJSON = deviceInfo.toDeviceJSON();
                deviceJSON.put("parttype", deviceInfo.getPartType());
                deviceJSON.put("parttypetext", deviceInfo.getPartTypeText());
                log.info("写设备日志:" + deviceJSON.toJSONString());
                businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
                log.info("sensorId:"+deviceInfo.getDevice_id());


            } else {
                //存储基本日志 到ES数据库
                log.error("crc 验证失败，只写raw日志:");
                businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
            }

    }
    public void easyLiakv2(String string, String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
            log.error("将数据转为设备对象:" + string);
            XinHaoSiDeviceInfoMQV2 deviceInfo = new XinHaoSiDeviceInfoMQV2(jsonObject);
            String uuid = UUIDUtils.generateUuid();
            log.error("将数据转为日志对象:");
            Map<String, Object> map = Maps.newHashMap();
            map.put("channel", channelCode);
            map.put("deviceid", deviceInfo.getDevice_id());
            map.put("uploadtime", deviceInfo.getUpload_time());
            map.put("recievetime", deviceInfo.getRecieve_time());
            map.put("rawdata", data);

            //判断会否是available 的数据。
            if (ByteUtil.checkXHSDeviceCRC(deviceInfo.getData())) {
                log.error("将数据转为对象,并检查和更新状态:");
                businessI.updateInRedis(deviceInfo, normal_timeout);
                        //报警
                if (deviceInfo.isHas_alarm() || deviceInfo.isHas_guzhang()) {
                    log.error("报警发送报警:");
//                    businessI.sendAlarm(uuid, deviceInfo);
                    String type="dqhz";
                    businessI.sendDeviceAlarmMQ(uuid,deviceInfo,type);
                } else {
                    log.error("更新为正常状态:" + deviceInfo.getDevice_id());
                    businessI.updateStatus(deviceInfo.getDevice_id(), deviceInfo, "2");
                    businessI.abnormalRestore(deviceInfo.getDevice_id());
                }
                String dianqihuozai = "dianqihuozai";
                businessI.updateInRedisTwo(dianqihuozai);
                //存储基本日志 到ES数据库
                log.info("写raw日志:");
                businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
                //数据存储到ES索引中
                JSONObject deviceJSON = deviceInfo.toDeviceJSON();
                deviceJSON.put("parttype", deviceInfo.getPartType());
                deviceJSON.put("parttypetext", deviceInfo.getPartTypeText());
                log.info("写设备日志:" + deviceJSON.toJSONString());
                businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
            } else {
                //存储基本日志 到ES数据库
                log.error("crc 验证失败，只写raw日志:");
                businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
            }
        }

    /**
     * 中科turing空气检测
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     */
    public void turingAir(String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.error("将数据转为设备对象："+data);
        AirMonitoringDeviceInfoV1 deviceInfoV1=new AirMonitoringDeviceInfoV1(jsonObject);
        log.error("将数据转为日志对象:");
        String uuid = UUIDUtils.generateUuid();
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV1.getDevice_id());
        map.put("uploadtime", deviceInfoV1.getUpload_time());
        map.put("recievetime", deviceInfoV1.getRecieve_time());
        map.put("rawdata", data);
        if (deviceInfoV1.getLength()==data.length()/2){
            log.error("将将中科turing空气数据转为对象,并检查和更新状态:");
            businessI.updateInRedis(deviceInfoV1, turing_air_timeout);
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
        }else{
            //存储基本日志 到ES数据库
            log.error("crc 验证失败，只写raw日志:");
            businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        }
    }

    /**
     * 中科图灵-液位传感器
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     */
    public void turingLiquidLevel(String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.error("将数据转为设备对象："+data);
        LiquidLevelDeviceInfoV1 deviceInfoV1=new LiquidLevelDeviceInfoV1(jsonObject);
        log.error("将数据转为日志对象:");
        String uuid = UUIDUtils.generateUuid();
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV1.getDevice_id());
        map.put("uploadtime", deviceInfoV1.getUpload_time());
        map.put("recievetime", deviceInfoV1.getRecieve_time());
        map.put("rawdata", data);
        if (deviceInfoV1.getLength()==data.length()/2){
            log.error("将中科turing液位数据转为对象,并检查和更新状态:");
            businessI.updateInRedis(deviceInfoV1, TURING_LIQUID_TIMEOUT);
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
        }else{
            //存储基本日志 到ES数据库
            log.error("crc 验证失败，只写raw日志:");
            businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        }
    }

    /**
     * 惠联无线-可燃气体-烟感-门磁
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public void easyLinkInCombustibleGas(String channelCode, String data, JSONObject jsonObject, BusinessI businessI,IDeviceFeign iDeviceFeign)throws Exception{
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
            log.error("将可燃气体数据转为设备对象：" + data);
            CombustibleGasDeviceInfoV1 deviceInfoV1 = new CombustibleGasDeviceInfoV1(jsonObject,code);
            String uuid = UUIDUtils.generateUuid();

            log.error("将数据转为日志对象:");
            Map<String, Object> map = Maps.newHashMap();
            map.put("channel", channelCode);
            map.put("deviceid", deviceInfoV1.getDevice_id());
            map.put("uploadtime", deviceInfoV1.getUpload_time());
            map.put("recievetime", deviceInfoV1.getRecieve_time());
            map.put("rawdata", data);

            //判断会否是available 的数据。
            log.error("将数据转为对象,并检查和更新状态:");
            businessI.updateInRedis(deviceInfoV1, EASYLINKIN_GAS_TIMEOUT);
            if (deviceInfoV1.isHas_alarm() || deviceInfoV1.isHas_guzhang()) {
                log.error("发送报警:");
                String type = "yg";
                businessI.sendDeviceAlarmMQ(uuid, deviceInfoV1, type);
            } else {
                log.error("更新为正常状态:" + deviceInfoV1.getDevice_id());
                businessI.updateStatus(deviceInfoV1.getDevice_id(), deviceInfoV1, "2");
                businessI.abnormalRestore(deviceInfoV1.getDevice_id());
            }

            String yangan = "yangan";
            businessI.updateInRedisTwo(yangan);
            //存储基本日志 到ES数据库
            log.info("写raw日志:");
            businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
            //数据存储到ES索引中
            JSONObject deviceJSON = deviceInfoV1.toDeviceJSON();
            log.info("写设备日志:" + deviceJSON.toJSONString());
            businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
        }else{
            log.error("没添加这个设备");
        }
    }

    /**
     * 中科图灵-红外设备
     * @param channelCode
     * @param data
     * @param jsonObject
     * @param businessI
     * @throws Exception
     */
    public void turingInfrared(String channelCode, String data, JSONObject jsonObject, BusinessI businessI) throws Exception {
        log.error("将红外人体感应数据转为设备对象："+data);
        InfraredDeviceInfoV1 deviceInfoV1=new InfraredDeviceInfoV1(jsonObject);
        String uuid = UUIDUtils.generateUuid();

        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode);
        map.put("deviceid", deviceInfoV1.getDevice_id());
        map.put("uploadtime", deviceInfoV1.getUpload_time());
        map.put("recievetime", deviceInfoV1.getRecieve_time());
        map.put("rawdata", data);

        //判断会否是available 的数据。
        log.error("将数据转为对象,并检查和更新状态:");
        businessI.updateInRedis(deviceInfoV1, TURING_LIQUID_TIMEOUT);
        if (deviceInfoV1.isHas_alarm() || deviceInfoV1.isHas_guzhang()) {
            log.error("发送报警:");
            String type="wlsb";
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV1,type);
        } else {
            log.error("更新为正常状态:" + deviceInfoV1.getDevice_id());
            businessI.updateStatus(deviceInfoV1.getDevice_id(), deviceInfoV1, "2");
            businessI.abnormalRestore(deviceInfoV1.getDevice_id());
        }

        String yangan = "hongwai";
        businessI.updateInRedisTwo(yangan);
        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV1.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }

    /**
     * 中科图灵-测温电气火灾探测器
     * @param channelCode_turing
     * @param data
     * @param businessI
     */
    public void turingElectricalFire(String channelCode_turing, String data, JSONObject jsonObject,BusinessI businessI) throws Exception{
        log.error("将测温电气火灾数据转为设备对象"+data);
        String uuid = UUIDUtils.generateUuid();
        JSONObject jsonObject1=businessI.selectMqttSensoralarmValue(jsonObject.getString("id"));
        ElectricalFireDeviceInfoV1 deviceInfoV1=new ElectricalFireDeviceInfoV1(jsonObject,jsonObject1);
        log.error("将数据转为日志对象:");
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode_turing);
        map.put("deviceid", deviceInfoV1.getDevice_id());
        map.put("uploadtime", deviceInfoV1.getUpload_time());
        map.put("recievetime", deviceInfoV1.getRecieve_time());
        map.put("rawdata", data);

        //判断会否是available 的数据。
        log.error("将数据转为对象,并检查和更新状态:");
        businessI.updateInRedis(deviceInfoV1, TURING_LIQUID_TIMEOUT);
        if (deviceInfoV1.isHas_alarm() || deviceInfoV1.isHas_guzhang()) {
            log.error("发送报警:");
            String type="dqhz";
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV1,type);
        } else {
            log.error("更新为正常状态:" + deviceInfoV1.getDevice_id());
            businessI.updateStatus(deviceInfoV1.getDevice_id(), deviceInfoV1, "2");
            businessI.abnormalRestore(deviceInfoV1.getDevice_id());
        }

        String yangan = "dqhz";
        businessI.updateInRedisTwo(yangan);
        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV1.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }

    /**
     * 中科图灵烟感
     * @param channelCode_turing
     * @param data
     * @param jsonObject
     * @param businessI
     */
    public void turingSmoking(String channelCode_turing, String data, JSONObject jsonObject,BusinessI businessI) throws Exception {
        SmokingDeviceInfoV1 deviceInfoV1=new SmokingDeviceInfoV1(jsonObject);
        log.error("将数据转为日志对象:");
        String uuid = UUIDUtils.generateUuid();
        Map<String, Object> map = Maps.newHashMap();
        map.put("channel", channelCode_turing);
        map.put("deviceid", deviceInfoV1.getDevice_id());
        map.put("uploadtime", deviceInfoV1.getUpload_time());
        map.put("recievetime", deviceInfoV1.getRecieve_time());
        map.put("rawdata", data);

        //判断会否是available 的数据。
        log.error("将数据转为对象,并检查和更新状态:");
        businessI.updateInRedis(deviceInfoV1, TURING_LIQUID_TIMEOUT);
        if (deviceInfoV1.isHas_alarm() || deviceInfoV1.isHas_guzhang()) {
            log.error("发送报警:");
            String type="dqhz";
            businessI.sendDeviceAlarmMQ(uuid,deviceInfoV1,type);
        } else {
            log.error("更新为正常状态:" + deviceInfoV1.getDevice_id());
            businessI.updateStatus(deviceInfoV1.getDevice_id(), deviceInfoV1, "2");
            businessI.abnormalRestore(deviceInfoV1.getDevice_id());
        }

        String yangan = "hzbj";
        businessI.updateInRedisTwo(yangan);
        //存储基本日志 到ES数据库
        log.info("写raw日志:");
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, map);
        //数据存储到ES索引中
        JSONObject deviceJSON = deviceInfoV1.toDeviceJSON();
        log.info("写设备日志:" + deviceJSON.toJSONString());
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceJSON);
    }
}
