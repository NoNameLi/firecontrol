package cn.turing.firecontrol.datahandler.business;

import cn.turing.firecontrol.common.util.AliSmsUtil;
import cn.turing.firecontrol.common.util.AliVmsSent;
import cn.turing.firecontrol.common.util.TencentVmsUtil;
import cn.turing.firecontrol.datahandler.base.Constant;
import cn.turing.firecontrol.datahandler.biz.*;
import cn.turing.firecontrol.datahandler.entity.*;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import cn.turing.firecontrol.datahandler.feign.IUserFeign;
import cn.turing.firecontrol.datahandler.listener.abnormalHandler.AbstractAbnormalHandler;
import cn.turing.firecontrol.datahandler.util.ESTransportUtil;
import cn.turing.firecontrol.datahandler.util.ValidatorUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BusinessImpl implements BusinessI {
    private  static final Logger log = LoggerFactory.getLogger(BusinessImpl.class);

    @Autowired
//    private TransportClient client;
    private ESTransportUtil esTransportUtil;

    @Autowired
    private IUserFeign iUserFeign;
    @Autowired
    private IDeviceFeign iDeviceFeign;
    @Autowired
    JpushServiceImpl jpushService;
    @Autowired
    TencentVmsUtil tencentVmsUtil;
    @Autowired
    private DeviceAbnormalBiz daBiz;
    @Autowired
    private DeviceFacilitiesAbnormalBiz dfaBiz;
    @Autowired
    private DeviceFireMainAbnormalBiz dfmaBiz;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private AliVmsSent aliVmsSent;
   /* @Autowired
    private FeiGeSmsUtil feiGeSmsUtil;*/
   @Value("${sms.ailiyun.fireTemplateNo}")
   private String aliyunSmsTemplateNo;
    @Autowired
    private AliSmsUtil aliSmsUtil;
    @Value("${tmc.config.rabbitmq.abnormal.websocket.exchange}")
    private String EXCHANGE_NAME;
    @Value("${tmc.config.rabbitmq.abnormal.websocket.routingKey}")
    private String ROUTING_KEY;
    @Value(("${sms.feige.alarmTemplateId}"))
    private String feiGeAlarmTemplateId;
    @Value(("${sms.feige.earlyWarningTemplateId}"))
    private String feiGeEarlyWarningTemplateId;
    @Value("${vms.aliyun.templateNo}")
    private String aLiTemplateNo;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private NoticeRuleSensorBiz noticeRuleSensorBiz;

    @Autowired
    private NoticeRuleBiz noticeRuleBiz;

    @Autowired
    private NoticeRuleUserBiz noticeRuleUserBiz;

    @Autowired
    private NoticeLogBiz noticeLogBiz;

    @Autowired
    private DeviceAbnormalBiz deviceAbnormalBiz;

    @Autowired
    private DeviceFacilitiesAbnormalBiz deviceFacilitiesAbnormalBiz;

    @PostConstruct
    public void init(){
        redisTemplate.opsForHash().put(REDIS_ABNORMAL_HASH,"init",Long.toString(System.currentTimeMillis()));
        redisTemplate.persist(REDIS_ABNORMAL_HASH);
    }

    //查询管理员信息
    @Override
    public JSONObject getAdmin(String tenantId) {
        log.info("调用模块:turing-admin 查询管理员信息接口: /tenant/admin开始");
        JSONObject json=null;
        try{
            if(tenantId!=null){
                json=iUserFeign.getTenantAdmin(tenantId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info("调用模块:turing-admin 查询管理员信息接口:/tenant/admin结束");
        return json;
    }
   //查询建筑屋信息
    @Override
    public JSONObject getArchitect(Integer buildingId) {
        log.info("调用模块:turing-device 查询建筑屋信息接口: /deviceBuilding/{id}开始");
        JSONObject json=null;
        try{
            if(buildingId!=null){
                json=iDeviceFeign.getBuilding(buildingId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info("调用模块:turing-device 查询建筑屋信息接口: /deviceBuilding/{id}开始");
        return json;
    }
    //查询硬件设施信息
    @Override
    public JSONObject getHardware(Integer hydrantId) {
        log.info("调用模块:turing-device 查询硬件设施信息接口: /deviceHardwareFacilities/getByHydrantId开始");
        JSONObject json=null;
        try{
            if(hydrantId!=null){
                json=iDeviceFeign.getHardware(hydrantId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info("调用模块:turing-device 查询硬件设施信息接口: /deviceHardwareFacilities/selectById开始");
        return json;
    }
    //查询传感器信息
    @Override
    public JSONObject getSensor(String deviceID) {
        log.info("调用模块:turing-device 查询传感器信息接口: /deviceSensor/selectBySensorNo开始");
        JSONObject json=null;
                try{
                    if(deviceID!=null){
                        json=iDeviceFeign.selectBySensorNo(deviceID);
                    }
                }catch (Exception e){
                   e.printStackTrace();
                }
        log.info("调用模块:turing-device 查询传感器信息接口: /deviceSensor/selectBySensorNo开始");
        return json;
    }

    @Override
    public JSONObject getSensorType(Integer sensorTypeId) {
        log.info("调用模块:turing-device 查询传感器类型接口: /deviceSensorType/id开始");
        JSONObject json=null;
        try{
            if(sensorTypeId!=null){
                json=iDeviceFeign.deviceSensorType(sensorTypeId);
                log.info("获取:"+json.toJSONString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info("调用模块:turing-device 查询传感器类型接口: /deviceSensorType/id开始");
        return json;
    }

    @Override
    public JSONArray getPoint(Integer sensorId) {
        log.info("调用模块:turing-device 查询测点信息接口: /getMeasuringPoint/deviceSensor/getMeasuringPoint开始");
        JSONArray jsonArray=null;
        try{
            if(sensorId!=null){
                jsonArray=iDeviceFeign.getMeasuringPoints(sensorId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info("调用模块:turing-device 查询测点信息接口: /getMeasuringPoint/deviceSensor/getMeasuringPoint开始");
        return jsonArray;
    }

    /**
     * 原始数据存入ES
     *
     * @param map
     */
    @Override
    public void insertData(String index,String type,String id,Map<String, Object> map) {
        log.info("增加原始数据到ES开始");
        try {
            esTransportUtil.addDocument(index,type,id,map);
//            IndexResponse response = client.prepareIndex(index, type, id).setSource(map).get();
//            log.info("返回的状态码：" + response.status().getStatus());
        } catch (Exception e) {
            log.error("增加原始数据到ES出错");
            e.printStackTrace();
        }
        log.info("增加原始数据到ES结束");
    }

    @Override
    public void updataSensor(String sensorNo, String statusTime, String status) {
        log.info("更新状态");
        try {
            iDeviceFeign.updateStatus(sensorNo,statusTime,status);

        } catch (Exception e) {
            log.error("更新状态出错");
            e.printStackTrace();
        }
    }

    @Override
    public List<Integer> insertException(Integer equID, Integer buildId, Integer channelId, Date uploadTime, String tenantId, String logid, JSONObject warningMap) {
       //                   String b_name,String senso_no,String equipment_type,Integer floor,String postion_desc,String measuring_point,String level) {
        //JSONArray ret = new JSONArray();
        String b_name = warningMap.getString("bname");
        String senso_no = warningMap.getString("deviceid");
        String equipment_type = warningMap.getString("equipmentType");
        Integer floor = warningMap.getInteger("floor");
        String postion_desc = warningMap.getString("positionDescription");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DeviceAbnormal> list = Lists.newArrayList();
        for(int i=0;i<warningMap.getJSONArray("alarms").size();i++) {
            DeviceAbnormal deviceAbnormal = new DeviceAbnormal();
            deviceAbnormal.setEquId(equID);
            deviceAbnormal.setBuildId(buildId);
            deviceAbnormal.setChannelId(channelId);
            deviceAbnormal.setAlrmData(warningMap.getJSONArray("alarms").getJSONObject(i).getDoubleValue("alarmValue"));
            if(warningMap.getJSONArray("alarms").getJSONObject(i).getIntValue("alarmStatus")==4)
                deviceAbnormal.setAlrmCategory("0");//OFFLINE是特殊值
            else
                deviceAbnormal.setAlrmCategory(warningMap.getJSONArray("alarms").getJSONObject(i).getInteger("alarmStatus").toString());

            deviceAbnormal.setAlrmType(warningMap.getJSONArray("alarms").getJSONObject(i).getString("alarmType"));
            deviceAbnormal.setAlrmDate(uploadTime);
            deviceAbnormal.setHandleFlag("0");
//            deviceAbnormal.setFireFlag("0");
            deviceAbnormal.setDelFlag("0");
            deviceAbnormal.setTenantId(tenantId);
            deviceAbnormal.setUnit(warningMap.getJSONArray("alarms").getJSONObject(i).getString("alarmCode"));

            deviceAbnormal.setMeasuringPoint(warningMap.getJSONArray("alarms").getJSONObject(i).getString("measuringPoint"));
            deviceAbnormal.setLevel(warningMap.getJSONArray("alarms").getJSONObject(i).getString("alarmlevel"));
            deviceAbnormal.setDataUnit(warningMap.getJSONArray("alarms").getJSONObject(i).getString("dataUnit"));

            deviceAbnormal.setLogId(logid);
            //添加一些字段B_NAME（建筑名）
            //SENSOR_NO（传感器编号）
            //EQUIPMENT_TYPE（传感器类型）
            //FLOOR（传感器楼层）
            //POSITION_DESCRIPTION（传感器位置描述）
            //MEASURING_POINT（测点）
            //LEVEL（报警等级）
            deviceAbnormal.setbName(b_name);
            deviceAbnormal.setSensorNo(senso_no);
            deviceAbnormal.setEquipmentType(equipment_type);
            deviceAbnormal.setFloor(floor);
            deviceAbnormal.setPositionDescription(postion_desc);
            list.add(deviceAbnormal);
        }
        List<Integer> ret = daBiz.add(list);
//        JSONObject object = iDeviceFeign.batchInsert(list);
//        if(object.getIntValue("status")==200 && object.containsKey("data")){
//            ret = object.getJSONArray("data");
//        }
//        log.info("exception 列表是:"+JSON.toJSONString(list));
        return ret;
    }

    @Override
    public List<Integer> insert(Integer equID,Integer hydrantId,Integer channelId,Date uploadTime,String tenantId,String logid,JSONObject warningMap) {
        //                   String b_name,String senso_no,String equipment_type,Integer floor,String postion_desc,String measuring_point,String level) {
        //JSONArray ret = new JSONArray();
        String hydrant_name = warningMap.getString("hydrantName");
        String senso_no = warningMap.getString("deviceid");
        String equipment_type = warningMap.getString("equipmentType");
        //Integer floor = warningMap.getInteger("floor");
        String postion_desc = warningMap.getString("positionDescription");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DeviceFacilitiesAbnormal> list = Lists.newArrayList();
        for(int i=0;i<warningMap.getJSONArray("alarms").size();i++) {
            DeviceFacilitiesAbnormal deviceFacilitiesAbnormal = new DeviceFacilitiesAbnormal();
            deviceFacilitiesAbnormal.setEquId(equID);
            deviceFacilitiesAbnormal.setFireCockId(hydrantId);
            deviceFacilitiesAbnormal.setChannelId(channelId);
            deviceFacilitiesAbnormal.setAlrmData(warningMap.getJSONArray("alarms").getJSONObject(i).getDoubleValue("alarmValue"));
            deviceFacilitiesAbnormal.setAlrmType(warningMap.getJSONArray("alarms").getJSONObject(i).getString("alarmType"));
            deviceFacilitiesAbnormal.setAlrmDate(uploadTime);
            deviceFacilitiesAbnormal.setHandleFlag("0");
            deviceFacilitiesAbnormal.setDelFlag("0");
            deviceFacilitiesAbnormal.setTenantId(tenantId);
            deviceFacilitiesAbnormal.setUnit(warningMap.getJSONArray("alarms").getJSONObject(i).getString("alarmCode"));

            deviceFacilitiesAbnormal.setDataUnit(warningMap.getJSONArray("alarms").getJSONObject(i).getString("dataUnit"));

            deviceFacilitiesAbnormal.setMeasuringPoint(warningMap.getJSONArray("alarms").getJSONObject(i).getString("measuringPoint"));
            deviceFacilitiesAbnormal.setLevel(warningMap.getJSONArray("alarms").getJSONObject(i).getString("alarmlevel"));

            deviceFacilitiesAbnormal.setLogId(logid);
            deviceFacilitiesAbnormal.setHydrantName(hydrant_name);
            deviceFacilitiesAbnormal.setSensorNo(senso_no);
            deviceFacilitiesAbnormal.setEquipmentType(equipment_type);
            deviceFacilitiesAbnormal.setPositionDescription(postion_desc);
            list.add(deviceFacilitiesAbnormal);
        }
        List<Integer> ret = dfaBiz.add(list);
//        JSONObject object = iDeviceFeign.insert(list);
//        if(object.getIntValue("status")==200 && object.containsKey("data")){
//            ret = object.getJSONArray("data");
//        }
//        log.info("exception 列表是:"+JSON.toJSONString(list));
        return ret;
    }

    @Override
    public List<Integer> insertFireMainException(Long equID, Integer buildId, Integer channelId, Date uploadTime, String tenantId, String logid, JSONObject warningMap) {
        String b_name = warningMap.getString("bname");//建筑名称
        Integer fireMainId = warningMap.getInteger("fireMainId");//消防主机id
        String sensorLoop = warningMap.getString("sensorLoop");//传感器回路
        String address = warningMap.getString("address");//传感器地址
        String series = warningMap.getString("series");//传感器系列
        Integer floor = warningMap.getInteger("floor");
        String postion_desc = warningMap.getString("positionDescription");
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DeviceFireMainAbnormal> list = Lists.newArrayList();
        DeviceFireMainAbnormal deviceFireMainAbnormal = new DeviceFireMainAbnormal();
        deviceFireMainAbnormal.setEquId(equID.intValue());
        deviceFireMainAbnormal.setBuildId(buildId);
        deviceFireMainAbnormal.setChannelId(channelId);
        deviceFireMainAbnormal.setAlrmData(warningMap.getJSONObject("alarms").getDoubleValue("alarmValue"));
        if(warningMap.getJSONObject("alarms").getIntValue("alarmStatus")==4) {
            deviceFireMainAbnormal.setAlrmCategory("0");//OFFLINE是特殊值
        }else{
            deviceFireMainAbnormal.setAlrmCategory(warningMap.getJSONObject("alarms").getInteger("alarmStatus").toString());
        }
        deviceFireMainAbnormal.setAlrmType(warningMap.getJSONObject("alarms").getString("alarmType"));
        deviceFireMainAbnormal.setAlrmDate(uploadTime);
        deviceFireMainAbnormal.setHandleFlag("0");
//        deviceFireMainAbnormal.setFireFlag("0");
        deviceFireMainAbnormal.setDelFlag("0");
        deviceFireMainAbnormal.setTenantId(tenantId);
        deviceFireMainAbnormal.setLogId(logid);
        deviceFireMainAbnormal.setbName(b_name);
        deviceFireMainAbnormal.setFireMainId(fireMainId);
        deviceFireMainAbnormal.setSensorLoop(sensorLoop);
        deviceFireMainAbnormal.setAddress(address);
        deviceFireMainAbnormal.setSeries(series);
        deviceFireMainAbnormal.setFloor(floor);
        deviceFireMainAbnormal.setPositionDescription(postion_desc);
        list.add(deviceFireMainAbnormal);
        List<Integer> ret = dfmaBiz.add(list);
        return ret;
    }

    @Override
    public JSONObject selectAlarmLevel(String sensorNo, Double alemData, String codeName,String tenantId) {
        log.info("查询报警等级");
        try {
            JSONObject jsonObject = iDeviceFeign.selectAlarmLevel(sensorNo,alemData,codeName,tenantId);
            log.info("获取的数据：" + jsonObject.toJSONString());
            return  jsonObject;
        } catch (Exception e) {
            log.error("更新状态出错");
            e.printStackTrace();
        }
        return null;
    }

    //查询消防主机传感器信息
    @Override
    public JSONObject getFireMainSensor(String serverIp,String port,String sensorLoop,String address) {
        log.info("调用模块:turing-device 查询消防主机传感器信息接口: /deviceFireMainSensor/selectIgnoreTenantIpAndPortAndSensor开始");
        JSONObject json=null;
        try{
            if(!ValidatorUtils.hasAnyBlank(serverIp,port,sensorLoop,address)){
                json=iDeviceFeign.deviceFireMainSensor(serverIp,port,sensorLoop,address);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info("调用模块:turing-device 查询消防主机传感器信息接口: /deviceFireMainSensor/selectIgnoreTenantIpAndPortAndSensor开始");
        return json;
    }

    //更新消防主机传感器状态
    @Override
    public void updataFireMainSensor(String serverIp,String port,String status,String statusTime,String sensorLoop,String address) {
        log.info("更新状态");
        try {
            iDeviceFeign.updataFireMainSensor(serverIp,port,status,statusTime,sensorLoop,address);
        } catch (Exception e) {
            log.error("更新状态出错");
            e.printStackTrace();
        }
    }

    public void alertMSG(String data) throws Exception{
        //{"logid":"sfasdfsadfdsafsda","deviceid":"设备的ID","uploaddate":"上传时间","recievedate":"接受时间","alarms":[{"alarmCode":"约定的缩写","alarmType":"约定的缩写","alarmValue":"约定的值"}]}
        JSONObject alarmInfo=JSONObject.parseObject(data);
        String deviceID = alarmInfo.getString("deviceid").toLowerCase();
        String logid = alarmInfo.getString("logid");
        String status = alarmInfo.getString("status");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date uploadtime = simpleDateFormat.parse(alarmInfo.getString("uploadtime"));
        //查询传感器信息  通过equipment查询  返回{"status":200,"data":{"id":2,"buildingId":1,"channelId":1,"sensorTypeId":25,"cdId":0,"fieldStatus":"0","status":"0","sensorNo":"111","floor":2,"positionDescription":"string","positionSign":"string","delFlag":"0","crtUserName":"","crtTime":"2018-08-03 09:41:48","updUserName":"","updTime":"2018-08-03 09:41:48","tenantId":"3dceb1854a044192880a08086009679d"}}
        JSONObject sensorDataTemp=getSensor(deviceID);
        if(sensorDataTemp==null || sensorDataTemp.getJSONObject("data") == null){
            log.info("传感器不存在");
        }
        log.info("传感器信息 :"+sensorDataTemp.toString());
        JSONObject sensorData=sensorDataTemp.getJSONObject("data");
        Integer sensorTypeId = sensorData.getInteger("sensorTypeId");//传感器类型id
        Integer buildingId = sensorData.getInteger("buildingId");//建筑物id
        Integer hydrantId = sensorData.getInteger("hydrantId");//室外消火栓id

        String tenantId = sensorData.getString("tenantId");//权限相关ID
        Integer sensorId = sensorData.getInteger("id");
        Integer channelId = sensorData.getInteger("channelId");
        String positionDescription=sensorData.getString("positionDescription");

        //查询设备的管理员   通过tenantId查询   返回{"id":"a436028b2264430084feccf9d527c55c","username":"bys","name":"不要删","attr1":"1"}
        JSONObject jsonAdmin=getAdmin(tenantId);
        log.info("管理员信息："+jsonAdmin.toJSONString());

        //查询传感器类型   通过sensorTypeId查询 返回{"status":200,"data":{"id":24,"equipmentType":"温湿度仪",
        // "manufacturer":"高级传感器有限公司","model":"AK4","dataAcquisitionCycleUnit":"0","dataAcquisitionCycleValue":4,
        // "acquisitionDelayTimeUnit":"0","acquisitionDelayTimeValue":5,"maintenanceCycleUnit":"1","maintenanceCycleValue":3,
        // "delFlag":"0","crtUserName":"消防大数据","crtUserId":"3814f13a0b3841d7a0bb6806708e53d8","crtTime":"2018-08-08 05:11:37","updUserName":"消防大数据",
        // "updUserId":"3814f13a0b3841d7a0bb6806708e53d8","updTime":"2018-08-08 05:11:37","departId":"d583e7de6d2d48b78fb3c7dcb180cb1f","tenantId":"3dceb1854a044192880a08086009679d"}}
        JSONObject SensorTypeTemp=getSensorType(sensorTypeId);
        log.info("传感器类型信息："+SensorTypeTemp.toJSONString());
        JSONObject jsonSensorType=SensorTypeTemp.getJSONObject("data");

        //查询测点信息 通过sensorId查询  measuringPointType:0 火警  1故障
        // [{"measuringPoint":"温度","codeName":"TEM","typeName":"火警测点","id":23,"dataUnit":"°C"},
        // {"measuringPoint":"电压","codeName":"VOL","typeName":"火警测点","id":24,"dataUnit":"V"}]
        JSONArray pointTemp=getPoint(sensorId);
        log.info("测点信息："+pointTemp.toJSONString());
        Map<String,JSONObject> typeMap = Maps.newHashMap();
        for(int i=0;i<pointTemp.size();i++){
            typeMap.put(pointTemp.getJSONObject(i).getString("codeName"),pointTemp.getJSONObject(i));
        }
        // 根据数据修改传感器信息表中的状态
        updataSensor(deviceID,alarmInfo.getString("uploadtime"),status);

        //组装报警数据
        JSONObject warningMap=new JSONObject();

        JSONObject jsonHydrant = null;
        JSONObject jsonArchitect = null;
        boolean flag = false;
        if(buildingId ==null && hydrantId != null){
            flag =true;
            //查询硬件设施信息
            JSONObject hydrantTemp=getHardware(hydrantId);
            log.info("硬件设施信息："+hydrantTemp.toJSONString());
            jsonHydrant = hydrantTemp.getJSONObject("data");

            warningMap.put("username", jsonAdmin.getString("username"));//设备管理员
            warningMap.put("baddress" ,jsonHydrant.getString("area"));//所属区域
            warningMap.put("addressDetail", jsonHydrant.getString("positionDescription"));//详细地址
            warningMap.put("positionDescription",jsonHydrant.getString("positionDescription"));
            warningMap.put("hydrantName", jsonHydrant.getString("hydrantName"));//消火栓名称
            warningMap.put("equipmentType",jsonSensorType.getString("equipmentType"));//设备类型
            warningMap.put("userId",jsonAdmin.getString("id"));//用户id
            warningMap.put("alarmdate",uploadtime);//报警时间
            warningMap.put("hydrantId", hydrantId);//消火栓id
            warningMap.put("tenantId", tenantId);//租户id
            warningMap.put("handelFlag", "0");//是否处理
            warningMap.put("sendStatus", "yes");
            warningMap.put("deviceid",deviceID);
            warningMap.put("sensorid",sensorId);
            List<Map<String, Object>> alarms = Lists.newArrayList();
            //报警是报警的格式，入库是入库的格式
            for(int i=0;i<alarmInfo.getJSONArray("alarms").size();i++) {
                JSONObject jsonObject = alarmInfo.getJSONArray("alarms").getJSONObject(i);
                Double alarmValue = jsonObject.getDoubleValue("alarmValue");
                String alarmCode = jsonObject.getString("alarmCode");
                String alarmType = jsonObject.getString("alarmType");
                Integer alarmStatus = jsonObject.getInteger("alarmStatus");
                JSONObject jsonObject1 = selectAlarmLevel(deviceID,alarmValue,alarmCode,tenantId);
                log.info("alarmCode :" +alarmCode+" "+jsonObject1.toJSONString());
                if(jsonObject1!=null && jsonObject1.getJSONObject("data") != null && jsonObject1.getJSONObject("data").containsKey("level")){
                    //JSONObject levelData=sensorDataTemp.getJSONObject("data");
                    Map<String, Object> detail = Maps.newHashMap();
                    detail.put("alarmType", alarmType);//报警类型
                    detail.put("alarmValue", alarmValue);// 增加报警值
                    detail.put("alarmlevel",jsonObject1.getJSONObject("data").getString("level"));//报警等级
                    detail.put("alarmCode", alarmCode);//报警类型
                    detail.put("alarmStatus", alarmStatus);//报警类型
                    //detail.put("typeName", typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("typeName"):"火警测点");//是否火警
                    detail.put("measuringPoint",typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("measuringPoint"):"");
                    jsonObject.put("unit",typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("unit"):"");
                    jsonObject.put("dataUnit",typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("dataUnit"):"");
                    alarms.add(detail);
                }else {
                    //如果没有设置，设置最基本的
                    Map<String, Object> detail = Maps.newHashMap();
                    detail.put("alarmType", alarmType);//是否火警
                    detail.put("alarmValue", alarmValue);// 增加报警值
                    detail.put("alarmlevel", "一级");//报警等级
                    detail.put("alarmCode", alarmCode);//报警类型
                    detail.put("alarmStatus",alarmStatus);
                    //detail.put("typeName", typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("typeName"):"火警测点");//是否火警
                    detail.put("measuringPoint",typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("measuringPoint"):"");
                    jsonObject.put("unit",typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("unit"):"");
                    alarms.add(detail);
                }
            }
            warningMap.put("alarms",alarms);
            log.info(JSON.toJSONString(warningMap));
        }else{
            //查询建筑屋信息  通过buildingId查询  返回 {"status":200,"data":{"id":1,"bid":1,"oid":1,"zxqy":"210505","street":"0","road":"光谷大道","
            // mnph":"1303","ldz":"7栋","addressDetail":"武汉市洪山区未来之光6栋","upFloor":8,"underFloor":-2,"sysOrganId":-1,"delFlag":"0",
            // "crtTime":"2018-07-31 19:46:40","updTime":"2018-07-31 19:46:40","tenantId":"3dceb1854a044192880a08086009679d",
            // "bname":"未来之光6栋","baddress":"武汉市洪山区","btime":"2018-08-11 15:00:39","bstate":"1"}}
            JSONObject architectTemp=getArchitect(buildingId);
            log.info("地址信息："+architectTemp.toJSONString());
            jsonArchitect=architectTemp.getJSONObject("data");

            warningMap.put("username", jsonAdmin.getString("username"));//设备管理员
            warningMap.put("baddress" ,jsonArchitect.getString("baddress"));//所属区域
            warningMap.put("addressDetail", jsonArchitect.getString("addressDetail"));//详细地址
            warningMap.put("positionDescription",positionDescription);
            warningMap.put("bname", jsonArchitect.getString("bname"));//建筑名称
            warningMap.put("floor",sensorData.getInteger("floor"));//楼层
            warningMap.put("equipmentType",jsonSensorType.getString("equipmentType"));//报警设备
            warningMap.put("userId",jsonAdmin.getString("id"));//用户id
            warningMap.put("alarmdate",uploadtime);//报警时间
            warningMap.put("buildingId", buildingId);//建筑屋id
            warningMap.put("tenantId", tenantId);//租户id
            warningMap.put("handelFlag", "0");//是否处理
            warningMap.put("fireFlag", "1");//是否为真实火警
            warningMap.put("sendStatus", "yes");
            warningMap.put("deviceid",deviceID);
            warningMap.put("sensorid",sensorId);
            List<Map<String, Object>> alarms = Lists.newArrayList();
            //报警是报警的格式，入库是入库的格式
            for(int i=0;i<alarmInfo.getJSONArray("alarms").size();i++) {
                JSONObject jsonObject = alarmInfo.getJSONArray("alarms").getJSONObject(i);
                Double alarmValue = jsonObject.getDoubleValue("alarmValue");
                String alarmCode = jsonObject.getString("alarmCode");
                String alarmType = jsonObject.getString("alarmType");
                Integer alarmStatus = jsonObject.getInteger("alarmStatus");
                JSONObject jsonObject1 = selectAlarmLevel(deviceID,alarmValue,alarmCode,tenantId);
                log.info("alarmCode :" +alarmCode+" "+jsonObject1.toJSONString());
                if(jsonObject1 != null && jsonObject1.getJSONObject("data") != null && jsonObject1.getJSONObject("data").containsKey("level")){
//                if(jsonObject1 != null && jsonObject1.containsKey("data") && jsonObject1.getJSONObject("data").containsKey("level")){
                    JSONObject levelData=sensorDataTemp.getJSONObject("data");
                    Map<String, Object> detail = Maps.newHashMap();
                    detail.put("alarmType", alarmType);//报警类型
                    detail.put("alarmValue", alarmValue);// 增加报警值
                    detail.put("alarmlevel",jsonObject1.getJSONObject("data").getString("level"));//报警等级
                    detail.put("alarmCode", alarmCode);//测点代号
                    detail.put("alarmStatus", alarmStatus);//报警状态
                    detail.put("typeName", typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("typeName"):"火警测点");//是否火警
                    detail.put("measuringPoint",typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("measuringPoint"):"");//测点
                    detail.put("dataUnit",typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("dataUnit"):"");//数据单位
                    jsonObject.put("unit",typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("unit"):"");//测点代号
                    alarms.add(detail);
                }else {
                    //如果没有设置，设置最基本的
                    Map<String, Object> detail = Maps.newHashMap();
                    detail.put("alarmType", alarmType);//报警类型
                    detail.put("alarmValue", alarmValue);// 增加报警值
                    detail.put("alarmlevel", "一级");//报警等级
                    detail.put("alarmCode", alarmCode);//测点代号
                    detail.put("alarmStatus",alarmStatus);
                    detail.put("typeName", typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("typeName"):"火警测点");//是否火警
                    detail.put("measuringPoint",typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("measuringPoint"):"");
                    detail.put("dataUnit",typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("dataUnit"):"");//数据单位
                    jsonObject.put("unit",typeMap.containsKey(alarmCode)?typeMap.get(alarmCode).getString("unit"):"");
                    alarms.add(detail);
                }
            }
            warningMap.put("alarms",alarms);
            log.info(JSON.toJSONString(warningMap));
        }

        //将报警数据存到ES中
        insertData(Constant.ESConstant.ES_INDEX_WARNING,Constant.ESConstant.ES_STRING,logid, warningMap);
        String name = null;
        String address = null;
        String deviceSeries =jsonSensorType.getString("equipmentType");
        if(flag){
            name= jsonHydrant.getString("hydrantName");
            address = name+jsonHydrant.getString("positionDescription");
        }else{
            name = jsonArchitect.getString("bname");
            Integer floor = sensorData.getInteger("floor");
            //String addressDetail = jsonArchitect.getString("addressDetail");
            address = name + floor + "楼" + positionDescription;
        }
        List<Integer> idlist = new ArrayList<>();//返回插入异常记录id集合
        //异常折叠
        String sensorNo = sensorData.getString("sensorNo");
        Object value = redisTemplate.opsForHash().get(REDIS_ABNORMAL_HASH,sensorNo);
        JSONArray alarms = warningMap.getJSONArray("alarms");
        redisTemplate.opsForHash().put(REDIS_ABNORMAL_HASH,sensorNo,alarms.toJSONString());
        String deviceLastStatus = sensorData.getString("status");
        List<AbstractAbnormalHandler.Alarm> newAlarms = alarms.toJavaList(AbstractAbnormalHandler.Alarm.class);
        if(!"2".equals(deviceLastStatus) && value != null) {
            List<AbstractAbnormalHandler.Alarm> oldAlarms = JSONObject.parseArray(value.toString(), AbstractAbnormalHandler.Alarm.class);
            List<AbstractAbnormalHandler.Alarm> newAlarms2 = new ArrayList<>(newAlarms);
            //获取新增的异常，如果新增的异常数大于0，则将新增的异常做进一步处理
            newAlarms.removeAll(oldAlarms);
            //如果本次不是离线则对上次有而本次没有的异常进行恢复
            if(!newAlarms2.contains(Constant.OFFLINE)){
                oldAlarms.removeAll(newAlarms2);
                if(oldAlarms.size() > 0){
                    restoreAbnormal(sensorData.toJavaObject(DeviceSensor.class),oldAlarms);
                }
            }
        }

        //保存异常
        if(newAlarms != null && !newAlarms.isEmpty()){
            JSONObject newWarningMap = JSONObject.parseObject(warningMap.toJSONString());
            List<JSONObject> saveAlarms = newWarningMap.getJSONArray("alarms").toJavaList(JSONObject.class);
            Iterator<JSONObject> iterator = saveAlarms.iterator();
            while (iterator.hasNext()){
                AbstractAbnormalHandler.Alarm a = iterator.next().toJavaObject(AbstractAbnormalHandler.Alarm.class);
                if(!newAlarms.contains(a)){
                    iterator.remove();
                }
            }
            newWarningMap.put("alarms",saveAlarms);
            if(!flag){
                //将报警数据存入异常信息记录表
                idlist = insertException(sensorId,buildingId,channelId,uploadtime,tenantId,logid,newWarningMap);
            }else{
                //将报警数据存入异常信息记录表
                idlist = insert(sensorId,hydrantId,channelId,uploadtime,tenantId,logid,newWarningMap);
            }
        }
        //发送异常通知
        pushMessage(status,warningMap,flag,sensorData,uploadtime,idlist,address,deviceSeries);
    }


    //保存信息到异常信息记录表，并推送消息
    public void pushMessage(String status,JSONObject warningMap,Boolean flag,JSONObject sensorData,Date uploadtime,List<Integer> idlist,String address,String deviceSeries) throws Exception{
        Integer buildingId = sensorData.getInteger("buildingId");//建筑物id
        Integer hydrantId = sensorData.getInteger("hydrantId");//室外消火栓id

        String tenantId = sensorData.getString("tenantId");//权限相关ID
        Integer sensorId = sensorData.getInteger("id");
        Integer channelId = sensorData.getInteger("channelId");
        String noticeType = null;
        if("0".equals(status) || "4".equals(status)){
            noticeType = "2";
        }
        if("1".equals(status)){
            noticeType = "1";
        }

        // {"status":200,"data":{"recipients":[{"id":"yH7WzK1H","username":"123455","name":"撒打算","mobilePhone":"13700000007","groupName":"weiba"},{"id":"cLz9jtRa","username":"demo","name":"图灵测试","mobilePhone":"18571574972","groupName":"高级会员组"}],"notice":["0","1","2"]}}
        JSONObject messageNotice = iDeviceFeign.messageNotice(noticeType,null,tenantId);
        //JSONObject messageNotice = JSONObject.parseObject("{\"status\":200,\"data\":{\"recipients\":[{\"id\":\"yH7WzK1H\",\"username\":\"123455\",\"name\":\"撒打算\",\"mobilePhone\":\"13260501658\",\"groupName\":\"weiba\"},{\"id\":\"cLz9jtRa\",\"username\":\"demo\",\"name\":\"图灵测试\",\"mobilePhone\":\"18186141485\",\"groupName\":\"高级会员组\"}],\"notice\":[\"0\",\"1\",\"2\"]}}");
        JSONObject messageDataTemp = messageNotice.getJSONObject("data");
        log.info("消息通知信息 :"+messageDataTemp.toString());
        StringBuffer phoneNumbers =new StringBuffer();
        List<String> userIdList = new ArrayList();
        Map<String,String> phoneMap = new HashMap<>();
        Map<String,String> userNameMap = new HashMap<>();
        for(int i=0;i< messageDataTemp.getJSONArray("recipients").size();i++){
            JSONObject jsonObject = messageDataTemp.getJSONArray("recipients").getJSONObject(i);
            String mobilePhone = jsonObject.getString("mobilePhone");
            String id = jsonObject.getString("id");
            phoneNumbers.append(mobilePhone);
            userIdList.add(id);
            phoneMap.put(id,mobilePhone);
            userNameMap.put(id,jsonObject.getString("username"));
            if(i != messageDataTemp.getJSONArray("recipients").size()-1){
                phoneNumbers.append(",");
            }
        }

        checkSendRule(userIdList,sensorId,channelId,phoneMap,noticeType);
        if(userIdList.size() == 0){
            return;
        }

        //云平台推送
        String msg = JSONObject.toJSONString(warningMap);
        log.info("msg: "+msg);
        //userID  上线了就发消息
        boolean findUserID = false;
        //发送消息到MQ，用于发送WebSocket消息
        Map<String,Object> mqMap = Maps.newHashMap();
        mqMap.put("userIds",userIdList);
        mqMap.put("msg",msg);
        amqpTemplate.convertAndSend(EXCHANGE_NAME,ROUTING_KEY,new Gson().toJson(mqMap));

        String logContent = address + deviceSeries + ("1".equalsIgnoreCase(status)?"报警":"故障");

        //发送电话和短信、极光推送
        for(int i=0;i< messageDataTemp.getJSONArray("notice").size();i++){//notice:  [0=APP推送/1=短信推送/2=语音电话]
            String notice = messageDataTemp.getJSONArray("notice").get(i).toString();
            if("0".equals(notice)){
                HashMap<String,String> extrasMap =Maps.newHashMap();
                extrasMap.put("sensorid", sensorId.toString());
                if(flag){
                    extrasMap.put("hydrantId", hydrantId.toString());
                }else{
                    extrasMap.put("buildid", buildingId.toString());
                }
                extrasMap.put("alrmids", idlist.toString());
                extrasMap.put("channelId",channelId.toString());
                extrasMap.put("time", uploadtime.toString());
                String content = address + deviceSeries + ("1".equalsIgnoreCase(status)?"报警":"故障");
                log.info("极光推送map:{}",extrasMap.toString());
                jpushService.sendPush(content,extrasMap,userIdList.toArray(new String[userIdList.size()]));
                saveNoticeSendLog(userIdList,channelId,warningMap.getString("alarmdate"),sensorId.longValue(),tenantId,"0","极光推送",logContent,phoneMap,userNameMap);
            }
            String[] phones = phoneMap.values().toArray(new String[0]);
            if("1".equals(notice)){
                log.info("开始发送阿里云短信:{},模板id：{}",phoneNumbers.toString(),aliyunSmsTemplateNo);
                //SmsUtil.sendAlarm(phoneNumbers+"",address,deviceSeries,"1".equalsIgnoreCase(status)?"报警":"故障","123");
                /*FeiGeSmsUtil.AlarmParam alarmParam = feiGeSmsUtil.new AlarmParam(address,deviceSeries,"1".equalsIgnoreCase(status)?"报警":"故障");
                feiGeSmsUtil.sendMsg(feiGeAlarmTemplateId,phones,alarmParam);*/
                Map<String,String> params = new HashMap();
                params.put("building",address);
                params.put("deviceSeries",deviceSeries);
                params.put("status","1".equalsIgnoreCase(status)?"报警":"故障");
                aliSmsUtil.sendSms(phoneNumbers.toString(),aliyunSmsTemplateNo,params,null);
                log.info("发送阿里云短信成功");

                saveNoticeSendLog(userIdList,channelId,warningMap.getString("alarmdate"),sensorId.longValue(),tenantId,"1","阿里云短信",logContent,phoneMap,userNameMap);
            }
            if("2".equals(notice)){
                Map<String,String> params = new HashMap();
                params.put("building",address);
                params.put("deviceSeries",deviceSeries);
                params.put("alrmType","1".equalsIgnoreCase(status)?"报警":"故障");
                for(int j=0;j< phones.length;j++){
//                    for(int k=0;k<3;k++){//循环3次,避免调用失败
//                        HuaweiVmsUtil.AlarmParam alarmParam = huaweiVmsUtil.new AlarmParam(address,deviceSeries,"1".equalsIgnoreCase(status)?"报警":"故障");
//                        Map map= huaweiVmsUtil.sendAlarm(phones[j],alarmParam);
//                        if("0".equals(map.get("resultcode"))){//如果成功,结束循环
//                            break;
//                        }
//                    }
                    for(int k=0;k<3;k++){//循环3次,避免调用失败
                        SingleCallByTtsResponse response= aliVmsSent.sendAlarm(aLiTemplateNo,phones[j],params);
                        if("OK".equals(response.getCode())){//如果成功,结束循环
                            log.info("拨打电话成功-----------------");
                            break;
                        }else{
                            log.info("电话失败",response);
                        }
                    }
//                    //阿里语音电话
//                    SingleCallByTtsResponse response= AliVmsSent.sendAlarm(phones[j],params);
                }
                saveNoticeSendLog(userIdList,channelId,warningMap.getString("alarmdate"),sensorId.longValue(),tenantId,"2","阿里云语音电话",logContent,phoneMap,userNameMap);
            }
        }
    }

    private void saveNoticeSendLog(List<String> userIdList,Integer channelId,String alarmTime,Long sensorId,String tenantId,
                                   String noticeType,String serviceSupplier,String content,Map<String,String> phoneMap,Map<String,String> userMap){
        try {
            log.info("userIdList:{}",userIdList);
            log.info("userMap:{}",userMap);
            log.info("phoneMap:{}",phoneMap);
            JSONObject json = this.iDeviceFeign.deviceSensor(sensorId);
            log.info("json信息：{}",json.toJSONString());
            DeviceSensor sensor = null;
            if (json != null && json != null && json.getJSONObject("data") != null) {
                sensor = json.getJSONObject("data").toJavaObject(DeviceSensor.class);
            }
            for (String userId : userIdList) {
                NoticeLog noticeLog = new NoticeLog();
                noticeLog.setChannelId(channelId);
                noticeLog.setDelFlag("0");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date alarmDate = null;
                try {
                    alarmDate = simpleDateFormat.parse(alarmTime);
                } catch (Exception e) {
                    alarmDate = new Date();
                }
                noticeLog.setAlarmTime(alarmDate);
                noticeLog.setNoticeContent(content);
                noticeLog.setNoticeResult("成功");
                noticeLog.setNoticeTime(new Date());
                noticeLog.setNoticeType(noticeType);
                noticeLog.setSensorId(sensorId.longValue());
                noticeLog.setSensorNo(sensor!=null?sensor.getSensorNo():"");
                noticeLog.setServiceSupplyName(serviceSupplier);
                noticeLog.setUserId(userId);
                noticeLog.setTenantId(tenantId);
                noticeLog.setUsername(userMap.get(userId));
                noticeLog.setMobilePhone(phoneMap.get(userId));
                this.noticeLogBiz.insertSelective(noticeLog);
            }
        }catch (Exception e){
            log.error("消息日志保存失败，" + e);
        }
    }

    private void checkSendRule(List<String> userIdList,Integer sensorId,Integer channelId,Map<String,String> phoneMap,String noticeType){
        if(sensorId == null || channelId == null){
            return;
        }
        //查询推送规则　
        NoticeRuleSensor  rsEntity = this.noticeRuleSensorBiz.queryBySensorIdAndChannelId(sensorId.longValue(),channelId);
        if(rsEntity == null ){
            return ;
        }
        NoticeRule rule = this.noticeRuleBiz.queryById(rsEntity.getNoticeRuleId());
        if(rule == null || "1".equals(rule.getDelFlag())){
            return ;
        }
        //查询规则关联的用户
        List<NoticeRuleUser> rsList = this.noticeRuleUserBiz.queryByNoticeRuleId(rule.getId(),noticeType);
        if(rsList.size() == 0){
            return ;
        }
        for(NoticeRuleUser  ru : rsList){
            //如果不在发送列表中，就直接跳过
            if(!userIdList.contains(ru.getUserId())){
                continue;
            }
            NoticeLog log = this.noticeLogBiz.queryLastLog(sensorId.longValue(),rule.getChannelId(),ru.getUserId());
            //如果上一次推给用户的时间间隔小于规定时间就不推送(误差一分钟)
            if(log != null && (System.currentTimeMillis() - log.getNoticeTime().getTime()) < (rule.getIntervalTimeMinutes() - 1) * 60 * 1000){
                userIdList.remove(ru.getUserId());
                phoneMap.remove(ru.getUserId());
            }
        }

    }

    /**
     * //        data = "{\n" +
     * //                "\"type\":\"firemain\",\n" +
     * //                "\"data\":{\n" +
     * //                "        \"last\":{\n" +
     * //                "            \t\"ip\": \"127.0.0.1\",\n" +
     * //                "\t\"port\":\"9994\",\n" +
     * //                "\t\"loopNo\":\"11\",\n" +
     * //                "\t\"localtionNo\":\"1\",\n" +
     * //                "\t        \"logid\":\"xxx1\",\n" +
     * //                "            \"uploadtime\": \"2019-03-27 18:09:00\",\n" +
     * //                "            \"status\":true,\n" +
     * //                "\t\t\t\"alarm\":{\n" +
     * //                "\t\t\t  \"alarmType\":\"报警\",\n" +
     * //                "\t\t\t  \"alarmValue\":1,\n" +
     * //                "\t\t\t  \"alarmStatus\":1\n" +
     * //                "\t\t\t}\n" +
     * //                "        },\n" +
     * //                "        \"current\": {\n" +
     * //                "            \t\"ip\": \"127.0.0.1\",\n" +
     * //                "\t\"port\":\"9994\",\n" +
     * //                "\t\"loopNo\":\"11\",\n" +
     * //                "\t\"localtionNo\":\"1\",\n" +
     * //                "\t        \"logid\":\"xxx2\",\n" +
     * //                "            \"uploadtime\": \"2019-03-27 18:09:01\",\n" +
     * //                "            \"status\":true,\n" +
     * //                "\t\t\t\"alarm\":{\n" +
     * //                "\t\t\t  \"alarmType\":\"报警\",\n" +
     * //                "\t\t\t  \"alarmValue\":2,\n" +
     * //                "\t\t\t  \"alarmStatus\":2\n" +
     * //                "\t\t\t}\n" +
     * //                "        }\n" +
     * //                "    }\n" +
     * //                "}";
     * @param data
     * @throws Exception
     */
    public void alertFireMainMSG(String data) throws Exception {
        JSONObject alarmInfo=JSONObject.parseObject(data);
        JSONObject last = alarmInfo.getJSONObject("last");
        JSONObject current = alarmInfo.getJSONObject("current");
        if(last == null || current == null){
            return ;
        }
        String lserverIp = last.getString("ip");//消防主机ip
        String lport = last.getString("port");//消防主机端口号
        String lsensorLoop = last.getString("loopNo");//传感器回路
        String laddress = last.getString("localtionNo");//传感器地址
        String llogid = last.getString("logid");
        boolean lstatus = last.getBoolean("status");//传感器状态

        String serverIp = current.getString("ip");//消防主机ip
        String port = current.getString("port");//消防主机端口号
        String sensorLoop = current.getString("loopNo");//传感器回路
        String address = current.getString("localtionNo");//传感器地址
        String logid = current.getString("logid");
        //true表示有异常 false表示没有异常 这里把异常都当作报警处理
        boolean status = current.getBoolean("status");//传感器状态




        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = new Date(Long.parseLong(current.getString("uploadtime")));
        String uploadtime = current.getString("uploadtime");//上传时间
        //查询传感器信息 返回{"status":200,"data":{"id":2,"buildingId":1,"channelId":1,"sensorTypeId":25,"cdId":0,"fieldStatus":"0","status":"0","sensorNo":"111","floor":2,"positionDescription":"string","positionSign":"string","delFlag":"0","crtUserName":"","crtTime":"2018-08-03 09:41:48","updUserName":"","updTime":"2018-08-03 09:41:48","tenantId":"3dceb1854a044192880a08086009679d"}}
        JSONObject sensorDataTemp=getFireMainSensor(serverIp,port,sensorLoop,address);
        log.info("传感器信息 :"+sensorDataTemp.toString());
        JSONObject sensorData=sensorDataTemp.getJSONObject("data");
        String series = sensorData.getString("series");//传感器系列
        Integer buildingId = sensorData.getInteger("buildingId");//建筑物id
        String tenantId = sensorData.getString("tenantId");//权限相关ID
        Long sensorId = sensorData.getLong("id");
        Integer channelId = sensorData.getInteger("channelId");
        String positionDescription=sensorData.getString("positionDescription");

        //查询设备的管理员   通过tenantId查询   返回{"id":"a436028b2264430084feccf9d527c55c","username":"bys","name":"不要删","attr1":"1"}
        JSONObject jsonAdmin=getAdmin(tenantId);
        log.info("管理员信息："+jsonAdmin.toJSONString());

        // 根据数据修改传感器信息表中的状态
        updataFireMainSensor(serverIp,port,status?"1":"2",uploadtime,sensorLoop,address);
        if(!status){
            //将异常信息设置为已处理
            DeviceFireMainAbnormal  dto = new DeviceFireMainAbnormal();
            dto.setFireMainId(sensorData.getInteger("fireMainId"));
            dto.setEquId(sensorId.intValue());
            dto.setSensorLoop(sensorLoop);
            dto.setAddress(address);
            dto.setSeries(series);
            this.dfmaBiz.handleAbnormal(sensorData.getInteger("fireMainId"),
                   sensorId, sensorLoop,address,series);
            return;
        }


        //组装报警数据
        JSONObject warningMap=new JSONObject();

        //查询建筑屋信息  通过buildingId查询  返回 {"status":200,"data":{"id":1,"bid":1,"oid":1,"zxqy":"210505","street":"0","road":"光谷大道","
        // mnph":"1303","ldz":"7栋","addressDetail":"武汉市洪山区未来之光6栋","upFloor":8,"underFloor":-2,"sysOrganId":-1,"delFlag":"0",
        // "crtTime":"2018-07-31 19:46:40","updTime":"2018-07-31 19:46:40","tenantId":"3dceb1854a044192880a08086009679d",
        // "bname":"未来之光6栋","baddress":"武汉市洪山区","btime":"2018-08-11 15:00:39","bstate":"1"}}
        JSONObject architectTemp=getArchitect(buildingId);
        log.info("地址信息："+architectTemp.toJSONString());
        JSONObject jsonArchitect=architectTemp.getJSONObject("data");

        warningMap.put("username", jsonAdmin.getString("username"));//设备管理员
        warningMap.put("baddress" ,jsonArchitect.getString("baddress"));//所属区域
        warningMap.put("addressDetail", jsonArchitect.getString("addressDetail"));//详细地址
        warningMap.put("positionDescription",positionDescription);
        warningMap.put("bname", jsonArchitect.getString("bname"));//建筑名称
        warningMap.put("floor",sensorData.getInteger("floor"));//楼层
        warningMap.put("series",series);//设备系列
        warningMap.put("userId",jsonAdmin.getString("id"));//用户id
        warningMap.put("alarmdate",simpleDateFormat.parse(uploadtime));//报警时间
        warningMap.put("buildingId", buildingId);//建筑屋id
        warningMap.put("tenantId", tenantId);//租户id
        warningMap.put("handelFlag", "0");//是否处理
//        warningMap.put("fireFlag", "1");//是否为真实火警
        warningMap.put("sendStatus", "yes");
        warningMap.put("sensorLoop",sensorLoop);//传感器回路
        warningMap.put("address",address);//传感器地址
        warningMap.put("fireMainId",sensorData.getInteger("fireMainId"));//消防主机id
        warningMap.put("sensorid",sensorId);//传感器id
        //报警是报警的格式，入库是入库的格式
        JSONObject jsonObject = current.getJSONObject("alarm");
        Double alarmValue = jsonObject.getDouble("alarmValue");
        String alarmType = jsonObject.getString("alarmType");
        Integer alarmStatus = jsonObject.getInteger("alarmStatus");

        Map<String, Object> alarm = Maps.newHashMap();
        alarm.put("alarmType", alarmType);//报警类型
        alarm.put("alarmValue", alarmValue);// 增加报警值
        alarm.put("alarmStatus", alarmStatus);//报警状态
        warningMap.put("alarms",alarm);

        log.info(JSON.toJSONString(warningMap));

        //将报警数据存到ES中
        //当两次数据一样时直接结束,避免重复发送
        if(lserverIp.equals(serverIp)
                || lport.equals(port)
                || lsensorLoop.equals(sensorLoop)
                || lstatus == status
                || laddress.equals(address)){
            insertData(Constant.ESConstant.ES_INDEX_WARNING,Constant.ESConstant.ES_STRING,logid, warningMap);
        }

        String name = jsonArchitect.getString("bname");
        Integer floor = sensorData.getInteger("floor");

        //将报警数据存入消防主机异常信息记录表,并返回插入异常记录id集合
        List<Integer> idlist = insertFireMainException(sensorId,buildingId,channelId,simpleDateFormat.parse(uploadtime),tenantId,logid,warningMap);
        log.info("返回的idlist:{}", CollectionUtils.isNotEmpty(idlist)?idlist.toString():"空");
        String addresss = name + floor + "楼" + positionDescription;

        List<Map<String, Object>> alarms = Lists.newArrayList();
        alarms.add(alarm);
        warningMap.put("alarms",alarms);

        pushMessage(status?"1":"3",warningMap,false,sensorData,simpleDateFormat.parse(uploadtime),idlist,addresss,series);
    }

    public void test(){

        //查询传感器信息 返回{"status":200,"data":{"id":2,"buildingId":1,"channelId":1,"sensorTypeId":25,"cdId":0,"fieldStatus":"0","status":"0","sensorNo":"111","floor":2,"positionDescription":"string","positionSign":"string","delFlag":"0","crtUserName":"","crtTime":"2018-08-03 09:41:48","updUserName":"","updTime":"2018-08-03 09:41:48","tenantId":"3dceb1854a044192880a08086009679d"}}
        String sensorInfo = "{\"status\":200," +
                "\"data\":{\"id\":339," +
                "\"buildingId\":1," +
                "\"channelId\":2," +
                "\"sensorTypeId\":25," +
                "\"cdId\":0," +
                "\"fieldStatus\":\"0\"," +
                "\"status\":\"0\"," +
                "\"sensorNo\":\"111\"," +
                "\"floor\":2," +
                "\"positionDescription\":\"string\"," +
                "\"positionSign\":\"string\"," +
                "\"delFlag\":\"0\"," +
                "\"crtUserName\":\"\"," +
                "\"crtTime\":\"2018-08-03 09:41:48\"," +
                "\"updUserName\":\"\"," +
                "\"updTime\":\"2018-08-03 09:41:48\"," +
                "\"tenantId\":\"3dceb1854a044192880a08086009679d\"}}";
        JSONObject sensorDataTemp = JSONObject.parseObject(sensorInfo);
        JSONObject sensorData=sensorDataTemp.getJSONObject("data");
        String series = sensorData.getString("series");//传感器系列
        Integer buildingId = sensorData.getInteger("buildingId");//建筑物id
        String tenantId = sensorData.getString("tenantId");//权限相关ID
        Long sensorId = sensorData.getLong("id");
        Integer channelId = sensorData.getInteger("channelId");
        String positionDescription=sensorData.getString("positionDescription");

        //查询设备的管理员   通过tenantId查询   返回{"id":"a436028b2264430084feccf9d527c55c","username":"bys","name":"不要删","attr1":"1"}
//        JSONObject jsonAdmin=getAdmin(tenantId);
//        log.info("管理员信息："+jsonAdmin.toJSONString());

        //组装报警数据
        JSONObject warningMap=new JSONObject();

        //查询建筑屋信息  通过buildingId查询  返回 {"status":200,"data":{"id":1,"bid":1,"oid":1,"zxqy":"210505","street":"0","road":"光谷大道","
        // mnph":"1303","ldz":"7栋","addressDetail":"武汉市洪山区未来之光6栋","upFloor":8,"underFloor":-2,"sysOrganId":-1,"delFlag":"0",
        // "crtTime":"2018-07-31 19:46:40","updTime":"2018-07-31 19:46:40","tenantId":"3dceb1854a044192880a08086009679d",
        // "bname":"未来之光6栋","baddress":"武汉市洪山区","btime":"2018-08-11 15:00:39","bstate":"1"}}
//        JSONObject architectTemp=getArchitect(buildingId);
//        JSONObject jsonArchitect=architectTemp.getJSONObject("data");

        warningMap.put("username", "管理员名称");//设备管理员
        warningMap.put("baddress" ,"所属区域");//所属区域
        warningMap.put("addressDetail", "详细地址");//详细地址
        warningMap.put("positionDescription",positionDescription);
        warningMap.put("bname", "建筑名称");//建筑名称
        warningMap.put("floor",2);//楼层
        warningMap.put("series",series);//设备系列
        warningMap.put("userId","用户ID");//用户id
        warningMap.put("alarmdate",new Date());//报警时间
        warningMap.put("buildingId", buildingId);//建筑屋id
        warningMap.put("tenantId", tenantId);//租户id
        warningMap.put("handelFlag", "0");//是否处理
//        warningMap.put("fireFlag", "1");//是否为真实火警
        warningMap.put("sendStatus", "yes");
        warningMap.put("sensorLoop","传感器回路");//传感器回路
        warningMap.put("address","传感器地址");//传感器地址
        warningMap.put("fireMainId",sensorData.getInteger("fireMainId"));//消防主机id
        warningMap.put("sensorid",sensorId);//传感器id
        //报警是报警的格式，入库是入库的格式
        Map<String, Object> alarms = Maps.newHashMap();
        alarms.put("alarmType", "报警类型");//报警类型
        alarms.put("alarmValue", 100.00);// 增加报警值
        alarms.put("alarmStatus", "报警状态");//报警状态
        warningMap.put("alarms",alarms);

        String name = "建筑名称";
        Integer floor = 2;

        String addresss = name + floor + "楼" + positionDescription;

        String status = "1";
        //String status = "0";


        String recieverInfo = "{\"status\":200,\"data\":{\"recipients\":[{\"id\":\"yH7WzK1H\",\"username\":\"123455\",\"name\":\"撒打算\",\"mobilePhone\":\"13700000007\",\"groupName\":\"weiba\"},{\"id\":\"cLz9jtRa\",\"username\":\"demo\",\"name\":\"图灵测试\",\"mobilePhone\":\"18571574972\",\"groupName\":\"高级会员组\"}],\"notice\":[\"0\",\"1\",\"2\"]}}";
        sensorId = 339L;
        List<String> userIdList = new ArrayList<>();
        userIdList.add("u1");
        userIdList.add("u2");
        userIdList.add("u3");
        userIdList.add("u4");
        userIdList.add("u5");
        Map<String,String> phoneMap = new HashMap<>();
        phoneMap.put("u1","u1PH");
        phoneMap.put("u2","u2PH");
        phoneMap.put("u3","u3PH");
        phoneMap.put("u4","u4PH");
        phoneMap.put("u5","u5PH");
        channelId = 2;
        checkSendRule(userIdList,sensorId.intValue(), channelId,phoneMap,"1");
        Map<String,String> userMap = new HashMap<>();
        userMap.put("u1","u1NAME");
        userMap.put("u2","u2NAME");
        userMap.put("u3","u3NAME");
        userMap.put("u4","u4NAME");
        userMap.put("u5","u5NAME");
        saveNoticeSendLog(userIdList,channelId,"2019-03-22 10:00:00",sensorId,tenantId,
                "1","APP","content",phoneMap,userMap);

        List<Integer> idlist = new ArrayList<>();
        idlist.add(1);
        idlist.add(2);
        try{
            pushMessage(status,warningMap,false,sensorData,new Date(),idlist,addresss,series);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 自动恢复设备异常记录
     * @param sensor
     * @param alarmCodes
     */
    @Override
    public void restoreAbnormal(DeviceSensor sensor, List<AbstractAbnormalHandler.Alarm> alarmCodes) {
        //如果是消防用水设备
        if(sensor.getHydrantId() != null){
            deviceFacilitiesAbnormalBiz.restore(sensor.getSensorNo(),alarmCodes);
        }else {
            deviceAbnormalBiz.restore(sensor.getSensorNo(),alarmCodes);
        }
    }
}



