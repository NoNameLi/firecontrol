package cn.turing.firecontrol.device.rest;


import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.business.BusinessI;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.Constant;
import cn.turing.firecontrol.device.util.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.rngom.parse.host.Base;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/turingBusiness")
@CheckClientToken
@CheckUserToken
public class BusinessController {
    private  static final Logger log = LoggerFactory.getLogger(BusinessController.class);
    @Autowired
    private BusinessI businessI;
    @Autowired
    private DeviceAbnormalBiz deviceAbnormalBiz;
    @Autowired
    private DeviceSensorBiz deviceSensorBiz;
    @Autowired
    private DeviceCollectingDeviceBiz deviceCollectingDeviceBiz;
    @Autowired
    private DeviceBuildingBiz deviceBuildingBiz;
    @Autowired
    private DeviceNetworkingUnitBiz deviceNetworkingUnitBiz;
    @Autowired
    private DeviceFloorLayoutBiz deviceFloorLayoutBiz;
    @Autowired
    private DeviceAlarmThresholdBiz deviceAlarmThresholdBiz;
    @Autowired
    private DeviceAlDnRelationBiz deviceAlDnRelationBiz;
    @Autowired
    private DeviceFireMainSensorBiz deviceFireMainSensorBiz;
    @Autowired
    private DeviceFireMainBiz deviceFireMainBiz;
    @Autowired
    private DeviceHardwareFacilitiesBiz deviceHardwareFacilitiesBiz;




  //查询传感器实时数据
/*    @ResponseBody
    @RequestMapping(value = "/querySensorRealTime",method ={RequestMethod.GET,RequestMethod.POST})
    public List<Map<String,Object>> querySensorRealTime(@RequestBody String str){
        log.info("query start!");
        List<Map<String,Object>> list=null;
        try{
            if(StringUtils.isNotBlank(str)){
                JSONObject jsonObject=JSONObject.parseObject(str);
                list= businessI.querySensorData(jsonObject);
                log.info("query result: "+list.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("query error!");
        }

        log.info("query ok");
        return list;
    }*/

/*    @ResponseBody
    @RequestMapping(value = "/querySensorRealTimeAPP",method ={RequestMethod.POST})
    public List<Map<String,Object>> querySensorRealTimeAPP(@RequestBody Map<String,String> param){
        log.info("query start!");
        List<Map<String,Object>> list=null;
        try{
            if(!ValidatorUtils.hasAnyBlank(param.get("mac"),param.get("sensorType"),param.get("startTime"),param.get("endTime"))){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("mac",param.get("mac"));
                jsonObject.put("sensorType",param.get("sensorType"));
                jsonObject.put("startTime",param.get("startTime"));
                jsonObject.put("endTime",param.get("endTime"));
                list= businessI.querySensorData(jsonObject);
                log.info("query result: "+list.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("query error!");
        }

        log.info("query ok");
        return list;
    }*/

    /**
     * 室内传感器实时数据
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySensorRealTime",method ={RequestMethod.POST})
    public com.alibaba.fastjson.JSONArray querySensorRealTime(@RequestBody Map<String,String> param){
        log.info("query start!");
        JSONArray list=null;
        try{
            if(!ValidatorUtils.hasAnyBlank(param.get("mac"),param.get("sensorType"),param.get("startTime"),param.get("endTime"))){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("mac",param.get("mac"));
                jsonObject.put("sensorType",param.get("sensorType"));
                jsonObject.put("startTime",param.get("startTime"));
                jsonObject.put("endTime",param.get("endTime"));
                list= businessI.querySensorDataTest(jsonObject);
                log.info("query result: "+list.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("query error!");
        }

        log.info("query ok");
        return list;
    }

    /**
     * 室外传感器实时数据,返回年月日的格式
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/querySensorRealTimeDay",method ={RequestMethod.POST})
    public com.alibaba.fastjson.JSONArray querySensorRealTimeDay(@RequestBody Map<String,String> param){
        log.info("query start!");
        JSONArray list=null;
        try{
            if(!ValidatorUtils.hasAnyBlank(param.get("mac"),param.get("sensorType"),param.get("startTime"),param.get("endTime"))){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("mac",param.get("mac"));
                jsonObject.put("sensorType",param.get("sensorType"));
                jsonObject.put("startTime",param.get("startTime"));
                jsonObject.put("endTime",param.get("endTime"));
                list= businessI.querySensorDataDay(jsonObject);
                log.info("query result: "+list.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("query error!");
        }

        log.info("query ok");
        return list;
    }

    /**
     * 消防主机传感器实时数据
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryMainSensorRealTime",method ={RequestMethod.POST})
    public com.alibaba.fastjson.JSONArray queryMainSensorRealTime(@RequestBody Map<String,String> param){
        log.info("query start!");
        JSONArray list=null;
        try{
            Long id = Long.parseLong(param.get("id"));
            if(id != null){
                //根据消防主机id查询  服务器ip + 端口 + 回路 + 地址
                DeviceFireMainSensor deviceFireMainSensor = deviceFireMainSensorBiz.selectById(id);
                DeviceFireMain deviceFireMain = deviceFireMainBiz.selectById(deviceFireMainSensor.getFireMainId());
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("serverIp",deviceFireMain.getServerIp());
                jsonObject.put("port",deviceFireMain.getPort());
                jsonObject.put("loopNo",deviceFireMainSensor.getSensorLoop());
                jsonObject.put("address",deviceFireMainSensor.getAddress());
                jsonObject.put("startTime",param.get("startTime"));
                jsonObject.put("endTime", param.get("endTime"));
                list= businessI.queryMianSensorData(jsonObject);
                log.info("query result: "+list.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("query error!");
        }

        log.info("query ok");
        return list;
    }



    @RequestMapping(value = "/deleteTenant",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除租户，级联删除设备管理的信息")
    public ObjectRestResponse deleteTenant(@RequestParam String tenantId){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(StringUtils.isNotBlank(tenantId)){
            //1.删除消火栓
            DeviceHardwareFacilities deviceHardwareFacilities = new DeviceHardwareFacilities();
            deviceHardwareFacilities.setTenantId(tenantId);
            deviceHardwareFacilitiesBiz.delete(deviceHardwareFacilities);
            //2.删除传感器记录表
            DeviceSensor deviceSensor = new DeviceSensor();
            deviceSensor.setTenantId(tenantId);
            deviceSensorBiz.delete(deviceSensor);
            //3.删除消防主机表
            DeviceFireMain deviceFireMain = new DeviceFireMain();
            deviceFireMain.setTenantId(tenantId);
            deviceFireMainBiz.delete(deviceFireMain);
            //4.删除消防主机传感器表
            DeviceFireMainSensor deviceFireMainSensor = new DeviceFireMainSensor();
            deviceFireMainSensor.setTenantId(tenantId);
            deviceFireMainSensorBiz.delete(deviceFireMainSensor);
            //5.删除网关记录表
            DeviceCollectingDevice deviceCollectingDevice = new DeviceCollectingDevice();
            deviceCollectingDevice.setTenantId(tenantId);
            deviceCollectingDeviceBiz.delete(deviceCollectingDevice);
            //6.删除建筑列表
            DeviceBuilding deviceBuilding = new DeviceBuilding();
            deviceBuilding.setTenantId(tenantId);
            deviceBuildingBiz.delete(deviceBuilding);
            //7.删除联网单位表
            DeviceNetworkingUnit deviceNetworkingUnit = new DeviceNetworkingUnit();
            deviceNetworkingUnit.setTenantId(tenantId);
            deviceNetworkingUnitBiz.delete(deviceNetworkingUnit);
            //8.删除平面图附件表
            DeviceFloorLayout deviceFloorLayout = new DeviceFloorLayout();
            deviceFloorLayout.setTenantId(tenantId);
            deviceFloorLayoutBiz.delete(deviceFloorLayout);
            //9.删除测点告警表
            DeviceAlarmThreshold deviceAlarmThreshold = new DeviceAlarmThreshold();
            deviceAlarmThreshold.setTenantId(tenantId);
            deviceAlarmThresholdBiz.delete(deviceAlarmThreshold);
            //10.删除报警通知表
            DeviceAlDnRelation deviceAlDnRelation = new DeviceAlDnRelation();
            deviceAlDnRelation.setTenantId(tenantId);
            deviceAlDnRelationBiz.delete(deviceAlDnRelation);
        }
        return responseResult;
    }



    @RequestMapping(value = "/queryElectricityByBuilding",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询各建筑物用电情况")
    public ObjectRestResponse queryElectricity(){
        String tenantId = BaseContextHandler.getTenantID();
        Date time = new Date();
        Date startTime = DateUtil.getStartTime(time);
        Date endTime = DateUtil.getEndTime(time);
        List<Map<String,Object>> list = businessI.queryElectricityByBuilding(startTime,endTime,tenantId);
        return new ObjectRestResponse().data(list);
    }






}
