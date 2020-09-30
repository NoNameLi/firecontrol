package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.*;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.util.*;
import cn.turing.firecontrol.device.vo.CountVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
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
import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


@RestController
@RequestMapping("deviceSensor")
@CheckClientToken
@CheckUserToken
@Api(tags = "传感器模块")
public class DeviceSensorController extends BaseController<DeviceSensorBiz,DeviceSensor,Integer> {

    @Autowired
    protected DeviceSensorBiz dsBiz;
    @Autowired
    protected DeviceSensorTypeBiz dstBiz;
    @Autowired
    protected DeviceBuildingBiz dbBiz;
    @Autowired
    protected DeviceAbnormalBiz daBiz;
    @Autowired
    private DeviceSensorSeriesBiz dssBiz;
    @Autowired
    private DeviceMeasuringPointBiz dmpBiz;
    @Autowired
    private IUserFeign iUserFeign;
    @Autowired
    private DeviceNetworkingUnitBiz dnuBiz;
    @Autowired
    private DeviceSensorMpRelationBiz dsmrBiz;
    @Autowired
    private DeviceAlarmThresholdBiz datBiz;
    @Autowired
    private DeviceAlarmLevelBiz dalBiz;
    @Autowired
    private DeviceFloorLayoutBiz dflBiz;
    @Autowired
    private TransportClient client;
    @Autowired
    private DeviceHardwareFacilitiesBiz dhfBiz;
    @Autowired
    private DeviceFireMainSensorBiz dfmsBiz;

    private  static final Logger log = LoggerFactory.getLogger(DeviceSensorController.class);

    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    public TableResultResponse<DeviceSensor> list( String page, String limit,String ids,String code, DeviceSensor deviceSensor,String floorId) {
        //当楼层乱输入时直接返回空，查不到
        //开发接口
        if(deviceSensor.getFloor()==null&&StringUtils.isNotBlank(floorId)){
            try {
                deviceSensor.setFloor(Integer.parseInt(floorId));
            }catch (Exception e){
                return new TableResultResponse<DeviceSensor>();
            }
        }
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "15";
        }
        //选择地区时传地区编码
        if(ids==null&&code!=null){
            if(code.length()!=6){
               throw new RuntimeException("错误的地区编码");
            }
            if("00".equals(code.substring(2,4))){
                code = code.substring(0,2)+"____";
            }else if("00".equals(code.substring(4))){
                code = code.substring(0,4)+"__";
            }
            if("00".equals(code.substring(2,4))){
                code = code.substring(0,2)+"____";
            }else if("00".equals(code.substring(4))){
                code = code.substring(0,4)+"__";
            }
            List<Integer> lists = dbBiz.selectByZxqzResultIds(code);
            ids = SplitUtil.merge(lists);
        }
        if(StringUtils.isBlank(ids)){
            return new TableResultResponse<DeviceSensor>();
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dsBiz.selectQuery(query,ids,deviceSensor);
    }

    @RequestMapping(value = "/getOutdoor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象,室外传感器实时数据")
    public ObjectRestResponse getSensorOutdoor(@RequestParam String id){
        ObjectRestResponse restResponse = new ObjectRestResponse<>();
        DeviceSensor deviceSensor = dsBiz.selectById(Long.parseLong(id));
        DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.selectById(deviceSensor.getHydrantId());
        DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());
        Map<String,Object> map = new HashMap<>();
        map.put("status",deviceSensor.getStatus());
        map.put("sensor",deviceSensorType.getEquipmentType());
        map.put("sensorNo",deviceSensor.getSensorNo());
        map.put("hydrantName",deviceHardwareFacilities.getHydrantName());
        return restResponse.data(map);
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象,室内传感器实时数据")
    public ObjectRestResponse getSensor(@RequestParam String id){
        ObjectRestResponse restResponse = new ObjectRestResponse<>();
        DeviceSensor deviceSensor = dsBiz.selectById(Long.parseLong(id));
        DeviceBuilding deviceBuilding = dbBiz.selectById(deviceSensor.getBuildingId());
        DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());
        Map<String,Object> map = new HashMap<>();
        map.put("status",deviceSensor.getStatus());
        map.put("sensor",deviceSensorType.getEquipmentType());
        map.put("sensorNo",deviceSensor.getSensorNo());
        map.put("bName",deviceBuilding.getBName());
        map.put("floor",deviceSensor.getFloor());
        map.put("description",deviceSensor.getPositionDescription());
        return restResponse.data(map);
    }

    @RequestMapping(value = "/batchInsert",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("批量插入传感器,关联对应的建筑物和传感器类型")
    public ObjectRestResponse batchInsert(@RequestBody List<DeviceSensor> list,String buildingName){
        ObjectRestResponse<DeviceSensor> responseResult =  new ObjectRestResponse<>();
        if(list.size()==0){
            throw new RuntimeException(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
        }
        for (DeviceSensor deviceSensor:list) {
            //根据厂商、类型、型号查询对应的传感器id
            List<DeviceSensorType> typeList = dstBiz.selectByType(deviceSensor.getManufacturer(),deviceSensor.getModel(),deviceSensor.getEquipmentType());
            //根据建筑名称查询建筑
            DeviceBuilding deviceBuilding = dbBiz.selectByBname(buildingName);
            deviceSensor.setBuildingId(deviceBuilding.getId());
            if(typeList.size()>1){
                throw new RuntimeException(Constants.SENSOR_TYPE_REPEAT);
            }else {
                for (DeviceSensorType deviceSensorType:typeList){
                    deviceSensor.setSensorTypeId(deviceSensorType.getId());
                    dsBiz.insertSelective(deviceSensor);
                }
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("批量假删除")
    public ObjectRestResponse<DeviceSensor> remove(@RequestParam String id){
        if(StringUtils.isBlank(id)){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }
        dsBiz.updateBatch(id);
        return new ObjectRestResponse<DeviceSensor>();
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("编辑传感器")
    public ObjectRestResponse update(@RequestBody  Map<String,Object> params){
        ObjectRestResponse<DeviceSensor> responseResult = new ObjectRestResponse();
        DeviceSensor deviceSensor = new DeviceSensor();
        try{
            deviceSensor.setId(Long.parseLong((String) params.get("id")));
            deviceSensor.setSensorNo((String) params.get("sensorNo"));
            deviceSensor.setFloor((Integer) params.get("floor"));
            deviceSensor.setPositionSign((String) params.get("positionSign"));
            deviceSensor.setPositionDescription((String) params.get("positionDescription"));
//            deviceSensor.setChannelId((Integer) params.get("channelId"));
        }catch (Exception e){
            throw new RuntimeException("参数错误!");
        }
        //建筑
        try{
            deviceSensor.setBuildingId((Integer) params.get("buildingId"));
        }catch (Exception e){
            deviceSensor.setBuildingId(Integer.parseInt((String) params.get("buildingId")));
        }
        TrimUtil.trimObject(deviceSensor);
        TrimUtil.trimNull(deviceSensor.getSensorNo(),deviceSensor.getPositionDescription());
        deviceSensor.setSensorNo(deviceSensor.getSensorNo().toLowerCase());
        ValidatorUtils.validateEntity(deviceSensor);
        List<String> entity = JSON.parseArray((String) params.get("entity"),String.class);
        if(deviceSensor==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            if(baseBiz.queryIdIsDel(deviceSensor.getId())==0){
                responseResult.setStatus(501);
                responseResult.setMessage("设备在系统不存在");
                return responseResult;
            }else {
                String sensorNo = deviceSensor.getSensorNo();
                if(sensorNo==null||sensorNo.equals("")){
                    throw new RuntimeException("编号不能为空！");
                }
                DeviceSensor sensor = dsBiz.selectById(deviceSensor.getId());
                List<DeviceSensorType> list = dstBiz.selectByType(entity.get(0),entity.get(2),entity.get(1));
                if(list.size()==1){
                    deviceSensor.setSensorTypeId(list.get(0).getId());
                    deviceSensor.setChannelId(list.get(0).getChannelId());
                }else{
                    throw new RuntimeException("没有找到厂商系列型号");
                }
                if((!sensor.getSensorNo().equalsIgnoreCase(deviceSensor.getSensorNo()))&&dsBiz.selectBySensorNo(deviceSensor.getSensorNo())!=null){
                    throw new RuntimeException(Constants.SENSOR_NO_REPEAT);
                }
                //修改楼层，不修改打点,初始化状态为未标记
                if(sensor.getFloor()!=deviceSensor.getFloor()){
                    String ps = deviceSensor.getPositionSign();
                    if(StringUtils.isBlank(ps)){
                        deviceSensor.setPositionSign("");
                    }
                }
                dsBiz.updateSelectiveById(deviceSensor);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/selectCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器类型查询数量")
    public ObjectRestResponse<DeviceSensor> selectCount(Integer buildId,Integer floor,Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        //根据所属系统id查询传感器
        List<DeviceSensor> sensorList = dsBiz.getSensorStatusByBuildAndFloor(buildId,floor,channelId);
        List<DeviceSensorType> sensorTypeList = new ArrayList<>();
        for (DeviceSensor deviceSensor:sensorList){
            if(!deviceSensor.getStatus().equals("3")){
                DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());
                sensorTypeList.add(deviceSensorType);
            }
        }
        List<Map<String,Object>> list = new ArrayList<>();
        for(DeviceSensorType deviceSensorType:removeDuplicateSensor(sensorTypeList)){
            Map<String,Object> map = new HashMap();
            DeviceSensorSeries deviceSensorSeries = dssBiz.selectBySensorTypeId(deviceSensorType.getId());
            map.put("color",deviceSensorSeries.getColor());
            map.put("type",deviceSensorType.getEquipmentType());
            Integer count =dsBiz.selectCountByType(deviceSensorType.getId(),buildId,floor,channelId);
            map.put("count", count);
            list.add(map);
        }
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/selectStatusCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询建筑物对应楼层传感器状态数量")
    public ObjectRestResponse<DeviceSensor> selectStatusCount(Integer buildId,Integer floor,Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
//        Integer faultCount = dsBiz.selectStatusCount(buildId,floor,channelId,"0");//故障
//        Integer faultTempCount = dsBiz.selectStatusCount(buildId,floor,channelId,"4");//离线，当故障
//        Integer callCount = dsBiz.selectStatusCount(buildId,floor,channelId,"1");//报警
//        Integer normalCount = dsBiz.selectStatusCount(buildId,floor,channelId,"2");//正常
        CountVo countVo = dsBiz.getCountByStatus(buildId,floor,channelId);
        //消防主机
        CountVo countVo1 = dfmsBiz.getCountByStatus(buildId,floor,channelId);
        Integer faultCount = countVo.getFaultCount()+countVo.getOffCount()+countVo1.getFaultCount()+countVo1.getOffCount();
        Integer callCount = countVo.getCallCount()+ countVo1.getCallCount();
        Integer normalCount = countVo.getNormalCount()+countVo1.getNormalCount();
        Map<String,Object> map = new HashMap<>();
        map.put("faultCount",faultCount);
        map.put("callCount",callCount);
        map.put("normalCount",normalCount);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/selectAbnormal",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询建筑物对应楼层传感器报警记录")
    public TableResultResponse<DeviceSensor> selectAbnormal(@RequestParam String page, @RequestParam String limit,@RequestParam Integer buildId,Integer floor,Integer channelId){
        Map<String ,Object> param = new LinkedHashMap<>();
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dsBiz.selectAbnormal(query,buildId,floor,channelId);
    }

    @RequestMapping(value = "/getMeasuringPoint",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器列表id查询对应的测点")
    public List<Map> selectMPoint(@RequestParam Integer id){
        List<Map> maps =new LinkedList<>();
        DeviceSensor deviceSensor = baseBiz.selectById(Long.parseLong(id.toString()));
        if(id!=null){
            Integer sensorSeriesId  = dssBiz.selectBySensorId(id);
            if(sensorSeriesId == null||deviceSensor!=null){
                new RuntimeException("系统找不到改传感器！");
            }
            List<DeviceMeasuringPoint> deviceMeasuringPoints = dmpBiz.selectBySensorSeriesIdResult(sensorSeriesId);
            JSONObject jsonObject = getESlastData(deviceSensor.getSensorNo());
            log.info("josn-->"+jsonObject);
            for(int i=0;i<deviceMeasuringPoints.size();i++){
                Map<String,Object> map=new HashMap();
                DeviceMeasuringPoint deviceMeasuringPoint=deviceMeasuringPoints.get(i);
                //不显示offline测点
                if("OFFLINE".equalsIgnoreCase(deviceMeasuringPoint.getMeasuringPoint())){
                    continue;
                }
                map.put("id",deviceMeasuringPoint.getId());
                map.put("measuringPoint",deviceMeasuringPoint.getMeasuringPoint());
                map.put("codeName",deviceMeasuringPoint.getCodeName());
                map.put("dataUnit",deviceMeasuringPoint.getDataUnit());
                //当传感器离线时，测点全部正常 author zwy
                if((!"4".equals(deviceSensor.getStatus()))&&jsonObject!=null&&jsonObject.get(map.get("codeName"))!=null){
                    JSONObject temp = (JSONObject)jsonObject.get(map.get("codeName"));
                    //status  0  故障，1 报警 ，2 正常 ，3 已屏蔽
                    if(temp.get("alarmType")!=null&&"屏蔽".equals(temp.get("alarmType").toString())){
                        map.put("alarmStatus",3);
                    }else {
                        map.put("alarmStatus",temp.get("alarmStatus"));
                    }
                }
                //当es里面没有找到测点的字段时，或者传感器离线默认正常
                if(map.get("alarmStatus")==null){
                    map.put("alarmStatus",2);
                }
                if("0".equals(deviceMeasuringPoint.getMeasuringPointType())){
                    map.put("typeName","火警测点");
                }else if("1".equals(deviceMeasuringPoint.getMeasuringPointType())){
                    map.put("typeName","监测测点");
                }
                map.put("dataUnit",deviceMeasuringPoint.getDataUnit());
                maps.add(map);
                log.info(map.toString());
            }
        }
        return maps;
    }

    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/getMeasuringPoints",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器列表id查询对应的测点")
    public List<Map> selectMPoints(@RequestParam Integer id){
        List<Map> maps =new LinkedList<>();
        if(id!=null){
            Integer sensorSeriesId  = dssBiz.selectBySensorId(id);
            if(sensorSeriesId == null){
                new RuntimeException("系统找不到改传感器！");
            }
            List<DeviceMeasuringPoint> deviceMeasuringPoints = dmpBiz.selectBySensorSeriesIdResult(sensorSeriesId);
            for(int i=0;i<deviceMeasuringPoints.size();i++){
                Map map=new HashMap();
                DeviceMeasuringPoint deviceMeasuringPoint=deviceMeasuringPoints.get(i);
                map.put("id",deviceMeasuringPoint.getId());
                map.put("measuringPoint",deviceMeasuringPoint.getMeasuringPoint());
                map.put("codeName",deviceMeasuringPoint.getCodeName());
                map.put("dataUnit",deviceMeasuringPoint.getDataUnit());
                if("0".equals(deviceMeasuringPoint.getMeasuringPointType())){
                    map.put("typeName","火警测点");
                }else if("1".equals(deviceMeasuringPoint.getMeasuringPointType())){
                    map.put("typeName","监测测点");
                }
                map.put("dataUnit",deviceMeasuringPoint.getDataUnit());
                maps.add(map);
            }
        }
        return maps;
    }

    @RequestMapping(value = "/getSelectModel",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器列表id查询对应的测点")
    public ObjectRestResponse getSelectModel(String equipmentType,String manufacturer){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(manufacturer!=null&&equipmentType==null){
            responseResult.setData(dstBiz.getEquipmentTypeByManufacturer(equipmentType));
        }
        if(manufacturer!=null&&equipmentType!=null){
            responseResult.setData(dstBiz.getModelByManufacturerAndType(manufacturer,equipmentType));
        }
        return responseResult;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加传感器，判断是否与建筑物绑定")
    public ObjectRestResponse<DeviceSensor> add(@RequestBody  Map<String,Object> params){
        DeviceSensor deviceSensor = new DeviceSensor();
        try{
            if( params.get("sensorNo")==null){
                throw new RuntimeException("没有输入传感器!");
            }
            deviceSensor.setSensorNo((String) params.get("sensorNo"));
            deviceSensor.setFloor((Integer) params.get("floor"));
            deviceSensor.setPositionSign((String) params.get("positionSign"));
            deviceSensor.setPositionDescription((String) params.get("positionDescription"));
//            deviceSensor.setChannelId((Integer) params.get("channelId"));
        }catch (Exception e){
            throw new RuntimeException("参数错误!");
        }
        try{
            deviceSensor.setBuildingId((Integer) params.get("buildingId"));
        }catch (Exception e){
            deviceSensor.setBuildingId(Integer.parseInt((String) params.get("buildingId")));
        }
        TrimUtil.trimObject(deviceSensor);
        TrimUtil.trimNull(deviceSensor.getSensorNo(),deviceSensor.getPositionDescription());
        deviceSensor.setSensorNo(deviceSensor.getSensorNo().toLowerCase());
        ValidatorUtils.validateEntity(deviceSensor);
        List<String> entity = JSON.parseArray((String) params.get("entity"),String.class);
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(deviceSensor==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else {
            DeviceSensor sensor = dsBiz.selectBySensorNo(deviceSensor.getSensorNo());
            if(sensor!=null){
               throw  new RuntimeException(Constants.SENSOR_NO_REPEAT);
            }
            List<DeviceSensorType> list = dstBiz.selectByType(entity.get(0),entity.get(2),entity.get(1));
            if(list.size()==1){
                deviceSensor.setSensorTypeId(list.get(0).getId());
                deviceSensor.setChannelId(list.get(0).getChannelId());
            }else{
                throw new RuntimeException("没有找到厂商系列型号");
            }
            DeviceBuilding deviceBuilding = dbBiz.selectById(deviceSensor.getBuildingId());
            if(deviceBuilding == null){
                throw new RuntimeException(Constants.SENSOR_NO);
            }else{
                //默认未启用
                deviceSensor.setStatus("3");
                deviceSensor.setId(null);
                deviceSensor.setHydrantId(null);
                dsBiz.insertSelective(deviceSensor);
                responseResult.setData(deviceSensor.getId());
            }
        }
        return responseResult;
    }



    @IgnoreClientToken
    @IgnoreUserToken
    @RequestMapping(value = "/selectBuilding",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器编号查询建筑物信息")
    public ObjectRestResponse selectBuilding(@RequestParam String sensorNo){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        Map<String,Object> map = new HashMap<>();
        if(sensorNo==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else {
            //查询传感器
            DeviceSensor deviceSensor = dsBiz.selectBySensorNo(sensorNo);
            DeviceSensorType deviceSensorType = dstBiz.getById(deviceSensor.getSensorTypeId());
            //查询建筑物信息
            //DeviceBuilding deviceBuilding = dbBiz.selectById(deviceSensor.getBuildingId());
            DeviceBuilding deviceBuilding = dbBiz.getById(deviceSensor.getBuildingId());
            map.put("equipmentType",deviceSensorType.getEquipmentType());
            map.put("floor",deviceSensor.getFloor());
            if(deviceBuilding != null){
                map.put("bName",deviceBuilding.getBName());
                map.put("bAddress",deviceBuilding.getBAddress());
            }
            map.put("positionDescription",deviceSensor.getPositionDescription());
        }
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getSelected",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有的厂商，系列,供下拉框选择")
    public ObjectRestResponse  getSelected(Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //获取所有的厂商，系列
        List<Map<String,Object>> channel = iUserFeign.getAll();
        List<String> equipmentType = dsBiz.getEquipmentTypeChannelId(channelId);
        List<String> manufacturer = dsBiz.getManufacturerChannelId(channelId);
        Map<String,Object> map = new HashMap<>();
        LinkedList<String> tempEquipmentType = new LinkedList<>();
        LinkedList<String> tempManufacturer = new LinkedList<>();
        if(equipmentType!=null&&equipmentType.size()>0){
            if(!"".equals(equipmentType.get(0))){
                tempEquipmentType.addAll(equipmentType);
            }
        }
        tempEquipmentType.addFirst("全部");
        map.put("equipmentType",tempEquipmentType);
        if(manufacturer!=null&&manufacturer.size()>0){
            if(!"".equals(manufacturer.get(0))){
                tempManufacturer.addAll(manufacturer);
            }
        }
        tempManufacturer.addFirst("全部");
        map.put("manufacturer",tempManufacturer);
        map.put("channel",channel);
        responseResult.setData(map);
        return responseResult;
    }


    @RequestMapping(value = "/getOutdoorSelected",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有室外传感器的厂商，系列,供下拉框选择")
    public ObjectRestResponse  getOutdoorSelected(){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //获取所有室外传感器的厂商，系列
        List<Map<String,Object>> channel = iUserFeign.getAll();
        List<String> equipmentType = dsBiz.getOutdoorEquipmentType();
        List<String> manufacturer = dsBiz.getOutdoorManufacturer();
        Map<String,Object> map = new HashMap<>();
        LinkedList<String> tempEquipmentType = new LinkedList<>();
        LinkedList<String> tempManufacturer = new LinkedList<>();
        if(equipmentType!=null&&equipmentType.size()>0){
            if(!"".equals(equipmentType.get(0))){
                tempEquipmentType.addAll(equipmentType);
            }
        }
        tempEquipmentType.addFirst("全部");
        map.put("equipmentType",tempEquipmentType);
        if(manufacturer!=null&&manufacturer.size()>0){
            if(!"".equals(manufacturer.get(0))){
                tempManufacturer.addAll(manufacturer);
            }
        }
        tempManufacturer.addFirst("全部");
        map.put("manufacturer",tempManufacturer);
        map.put("channel",channel);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getStatusByBuild",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询建筑物传感器状态和位置")
    public ObjectRestResponse getStatusByBuild(String bName,Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //查询建筑物
        List<DeviceBuilding> buildList = dbBiz.selectByBnameLike(bName,null);
        List resultlist = new ArrayList();
        for(DeviceBuilding deviceBuilding:buildList){
            Map<String ,Object> map = new HashMap<>();
            map.put("id",deviceBuilding.getId());
            map.put("name",deviceBuilding.getBName());
            if(StringUtils.isNotBlank(deviceBuilding.getGis())){
                String [] gis =deviceBuilding.getGis().split(",");
                map.put("gisx",gis[0]);
                map.put("gisy",gis[1]);
            }
            if(deviceBuilding.getImageX() != null){
                map.put("imageX",deviceBuilding.getImageX());
            }
            if(deviceBuilding.getImageY() != null){
                map.put("imageY",deviceBuilding.getImageY());
            }
            Integer[] counts = dsBiz.getBuildingStatusAndCount(deviceBuilding.getId(),channelId);
            if(counts[0] > 0){
                map.put("status","1");
                map.put("count",counts[0]);
            }else if(counts[1] > 0){
                map.put("status","0");
                map.put("count",counts[1]);
            }else {
                map.put("status","2");
                map.put("count",0);
            }
            resultlist.add(map);
        }
        responseResult.setData(resultlist);
        return responseResult;
    }

    @RequestMapping(value = "/getStatusByBuildAndTenantId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据租户id查询建筑物传感器状态和位置")
    public ObjectRestResponse getStatusByBuildAndTenantId(String bName,Integer channelId,String tenantId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<DeviceBuilding> buildList = new ArrayList<>();
        //查询建筑物
        if(iUserFeign.isSuperAdmin(BaseContextHandler.getUserID())){//判断是否是管理员
            buildList = dbBiz.selectByBnameLike(bName,tenantId);
        }else {
            throw new RuntimeException("无此权限!");
        }
        List resultlist = new ArrayList();
        for(DeviceBuilding deviceBuilding:buildList){
            Map<String ,Object> map = new HashMap<>();
            map.put("id",deviceBuilding.getId());
            map.put("name",deviceBuilding.getBName());
            if(StringUtils.isNotBlank(deviceBuilding.getGis())){
                String [] gis =deviceBuilding.getGis().split(",");
                map.put("gisx",gis[0]);
                map.put("gisy",gis[1]);
            }
            String status = dsBiz.getBuildingStatus(deviceBuilding.getId(),channelId);
            map.put("status",status);
            resultlist.add(map);
        }
        responseResult.setData(resultlist);
        return responseResult;
    }

    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/selectBySensorNo",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器编号查询传感器")
    public ObjectRestResponse selectBySensorNo(@RequestParam String sensorNo){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(sensorNo==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else{
            DeviceSensor deviceSensor = dsBiz.selectBySensorNo(sensorNo);
            if(deviceSensor==null){
                log.info("sensor no exist :"+sensorNo);
            }else{
                responseResult.setData(deviceSensor);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/selectByArea",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据地区编码、所属系统查询建筑、设备信息")
    public TableResultResponse<DeviceSensor> selectByArea(String zxqy,Integer channelId,String page, String limit) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "10";
        }
        if(StringUtils.isNotBlank(zxqy)&&zxqy.trim().length()==6){
            //判断 省 市  的时候用__代替查询
            if("00".equals(zxqy.substring(2,4))){
                zxqy = zxqy.substring(0,2)+"____";
            }else if("00".equals(zxqy.substring(4))){
                zxqy = zxqy.substring(0,4)+"__";
            }
            if("00".equals(zxqy.substring(2,4))){
                zxqy = zxqy.substring(0,2)+"____";
            }else if("00".equals(zxqy.substring(4))){
                zxqy = zxqy.substring(0,4)+"__";
            }
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dsBiz.selectByArea(query,zxqy,channelId);
    }

    @RequestMapping(value = "/selectBybuildId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据建筑id查询建筑、设备、单位信息接口")
    public ObjectRestResponse selectBybuildId(@RequestParam Integer buildId ,Integer channelId) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<Map<String ,Object>> resultlist = new ArrayList();
        Map map = new HashMap();
        //根据建筑id查询建筑信息
        DeviceBuilding deviceBuilding = dbBiz.selectById(buildId);
        map.put("name",deviceBuilding.getBName());
        map.put("address",deviceBuilding.getAddressDetail());
        //根据单位id查询单位
        DeviceNetworkingUnit deviceNetworkingUnit = dnuBiz.selectById(deviceBuilding.getOid());
        map.put("principal",deviceNetworkingUnit.getOLinkman());
        map.put("tel",deviceNetworkingUnit.getOLinkphone());
        Integer faultCount = dsBiz.selectStatusCount(buildId,null,channelId,"0");//故障
        Integer callCount = dsBiz.selectStatusCount(buildId,null,channelId,"1");//故障
        map.put("faultCount",faultCount);
        map.put("callCount",callCount);
        resultlist.add(map);
        responseResult.setData(resultlist);
        return responseResult;
    }

//    @RequestMapping(value = "/getSensorStatusByBuildAndFloor",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("根据建筑id和楼层查询传感器信息")
//    public ObjectRestResponse getSensorStatusByBuildAndFloor(Integer buildId,Integer floor,Integer channelId){
//        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        List resultlist = new ArrayList();
//        List<DeviceSensor> sensorlist =dsBiz.getSensorStatusByBuildAndFloor(buildId,floor,channelId);
//        for(DeviceSensor deviceSensor:sensorlist){
//            Map map =new HashMap();
//            DeviceSensorType deviceSensorType =dstBiz.selectById(deviceSensor.getSensorTypeId());
//            List<DeviceAbnormal> abnormalslist = daBiz.selectByEquId(deviceSensor.getId(),null,null);
//            //查询传感器系列
//            DeviceSensorSeries deviceSensorSeries = dssBiz.selectBySensorType(deviceSensorType.getEquipmentType());
//            //查询测点ids
//            List<Integer> ids = dsmrBiz.selectBySensorSeriesId(deviceSensorSeries.getId());
//            for(DeviceAbnormal deviceAbnormal:abnormalslist){
//                map.put("equipmentType",deviceSensorType.getEquipmentType());
//                map.put("sensorNo",deviceSensor.getSensorNo());
//                map.put("positionDescription",deviceSensor.getPositionDescription());
//                map.put("positionSign",deviceSensor.getPositionSign());
//                if(deviceAbnormal.getAlrmCategory().equals("0")){//故障
//                    map.put("status",0);
//                    map.put("status",0);
//                }
//                if(deviceAbnormal.getAlrmCategory().equals("1")){//火警
//                    for(Integer id:ids){
//                        DeviceMeasuringPoint deviceMeasuringPoint =dmpBiz.selectById(id);
//                        if(deviceMeasuringPoint.getCodeName().equals(deviceAbnormal.getUnit())){
//                            DeviceAlarmThreshold deviceAlarmThreshold = datBiz.selectByAlrmData(id,deviceAbnormal.getAlrmData(),deviceSensor.getTenantId());
//                            if(deviceAlarmThreshold!=null){
//                                DeviceAlarmLevel deviceAlarmLevel =dalBiz.selectById(deviceAlarmThreshold.getAlId());
//                                map.put("alarmlevel",deviceAlarmLevel.getLevel());
//                                map.put("alrmData",deviceAbnormal.getAlrmData());
//                                map.put("status",1);
//                            }
//                        }
//                    }
//                }
//            }
//            resultlist.add(map);
//        }
//        responseResult.setData(resultlist);
//        return responseResult;
//    }

    @RequestMapping(value = "/getSensorAndFloor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑id和楼层查询传感器位置，状态和平面图")
    public ObjectRestResponse getSensorAndFloor(Integer buildId,Integer floor,Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List sensorList = new ArrayList();
        Map map = new HashMap();
        //传感器列表
        List<DeviceSensor> sensorlist =dsBiz.getSensorStatusByBuildAndFloor(buildId,floor,channelId);
        //平面图
        List<DeviceFloorLayout> floorLayoutlist = dflBiz.selectFloorLayout(buildId,floor);
        if(floorLayoutlist.size()==1){
            DeviceFloorLayout deviceFloorLayout = floorLayoutlist.get(0);
            map.put("url",deviceFloorLayout.getFilePath());
        }else {
            map.put("url","http://file.tmc.turing.ac.cn/not_img.png");
        }
        for(DeviceSensor deviceSensor:sensorlist){
            if(!deviceSensor.getStatus().equals("3")){
                Map sensormap = new HashMap();
                sensormap.put("id",deviceSensor.getId());
                if(StringUtils.isNotBlank(deviceSensor.getPositionSign())){
                    String[] positionSign =deviceSensor.getPositionSign().split(",");
                    sensormap.put("positionSignX",positionSign[0]);
                    sensormap.put("positionSignY",positionSign[1]);
                }
                sensormap.put("status",deviceSensor.getStatus());
                DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());
                DeviceSensorSeries deviceSensorSeries =dssBiz.selectBySensorTypeId(deviceSensorType.getId());
                sensormap.put("color",deviceSensorSeries.getColor());
                sensorList.add(sensormap);
            }
        }
        map.put("sensor",sensorList);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getSensor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器id查询")
    public ObjectRestResponse getSensor(@RequestParam Long sensorId) throws ParseException {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map map = new HashMap();
        if(sensorId==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else{
            //查询
            DeviceSensor deviceSensor = dsBiz.selectById(sensorId);
            DeviceSensorType deviceSensorType =dstBiz.selectById(deviceSensor.getSensorTypeId());
            //"recievetime" -> "2019-08-13T03:16:48.490Z"
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
            if(!deviceSensor.getStatus().equals("4")){//传感器状态不为已下线
                //查询传感器系列
                DeviceSensorSeries deviceSensorSeries = dssBiz.selectBySensorTypeId(deviceSensorType.getId());
                //查询测点ids
                List<Integer> ids = dsmrBiz.selectBySensorSeriesId(deviceSensorSeries.getId());
                map.put("equipmentType",deviceSensorType.getEquipmentType());
                map.put("sensorNo",deviceSensor.getSensorNo());
                map.put("positionDescription",deviceSensor.getPositionDescription());
                List list = new LinkedList();
                for(Integer id:ids){
                    DeviceMeasuringPoint deviceMeasuringPoint =dmpBiz.selectById(id);
                    Map<String,Object> resultmap = new LinkedHashMap<>();
                    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                    //mac地址
                    if (deviceSensor.getSensorNo() != null) {
                        queryBuilder.must(QueryBuilders.matchQuery("deviceid", deviceSensor.getSensorNo()));
                    }
                    SortBuilder sortBuilder = SortBuilders.fieldSort("uploadtime").unmappedType("date").order(SortOrder.DESC);
                    SearchResponse searchResponse = client.prepareSearch(Constant.ESConstant.ES_INDEX_SENSOR).setQuery(queryBuilder).setSize(1).addSort(sortBuilder).execute().actionGet();
                    SearchHits searchHits = searchResponse.getHits();
                    for(SearchHit hit:searchHits){
                        JSONObject jsonObject = JSONObject.parseObject(hit.getSourceAsString());
                        map.put("date",simpleDateFormat1.parse(jsonObject.get("uploadtime").toString()));
                        if(jsonObject.containsKey(deviceMeasuringPoint.getCodeName())){
                            JSONObject jsonObject1 = JSONObject.parseObject(jsonObject.get(deviceMeasuringPoint.getCodeName()).toString());
                            resultmap.put("measuringPoint",deviceMeasuringPoint.getMeasuringPoint());
                            resultmap.put("data",jsonObject1.get("alarmValue")+deviceMeasuringPoint.getDataUnit());
                            resultmap.put("status",jsonObject1.get("alarmStatus"));
                            resultmap.put("type",jsonObject1.get("alarmType"));
                            if(jsonObject1.get("alarmValue")!=null){
                                DeviceAlarmThreshold deviceAlarmThreshold = datBiz.selectByAlrmData(id,Double.valueOf(jsonObject1.get("alarmValue").toString()),deviceSensor.getTenantId());
                                if(deviceAlarmThreshold!=null){
                                    DeviceAlarmLevel deviceAlarmLevel = dalBiz.selectById(deviceAlarmThreshold.getAlId());
                                    resultmap.put("color", deviceAlarmLevel.getColor());
                                }
                            }
                            list.add(resultmap);
                        }
                    }
                }
                map.put("alrmData",list);
            }else{
                map.put("equipmentType",deviceSensorType.getEquipmentType());
                map.put("sensorNo",deviceSensor.getSensorNo());
                map.put("positionDescription",deviceSensor.getPositionDescription());
                map.put("status",deviceSensor.getStatus());
//                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//                //mac地址
//                if (deviceSensor.getSensorNo() != null) {
//                    queryBuilder.must(QueryBuilders.matchQuery("deviceid", deviceSensor.getSensorNo()));
//                }
//                SortBuilder sortBuilder = SortBuilders.fieldSort("uploadtime").unmappedType("date").order(SortOrder.DESC);
//                SearchResponse searchResponse = client.prepareSearch(Constant.ESConstant.ES_INDEX_SENSOR).setQuery(queryBuilder).setSize(1).addSort(sortBuilder).execute().actionGet();
//                SearchHits searchHits = searchResponse.getHits();
//                for(SearchHit hit:searchHits){
//                    JSONObject jasonObject = JSONObject.parseObject(hit.getSourceAsString());
//                    map.put("date",simpleDateFormat1.parse(jasonObject.get("uploadtime").toString()));
//                    map.put("time",DateUtil.getHandletime(new Date(),simpleDateFormat1.parse(jasonObject.get("uploadtime").toString())));
//                }
                map.put("date",deviceSensor.getStatusTime());
                map.put("time",DateUtil.getHandletime(new Date(),deviceSensor.getStatusTime()));
            }
        }
        responseResult.setData(map);
        return responseResult;
    }


//    @IgnoreUserToken
//    @IgnoreClientToken
//    @RequestMapping(value = "/updateById",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("更新传感器")
//    public ObjectRestResponse  getSelectById(@RequestBody DeviceAbnormal deviceAbnormal){
//        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        daBiz.updateSelectiveById(deviceAbnormal);
//        return responseResult;
//    }

    @RequestMapping(value = "/getSensorById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据传感器id查询设备详细信息")
    public ObjectRestResponse  getSelectById(@RequestParam long id){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map map = new HashMap();
        //传感器
        DeviceSensor deviceSensor = dsBiz.selectById(id);
        if(deviceSensor==null){
            throw new RuntimeException("传感器不存在!");
        }
        //传感器类型
        DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());
        if(deviceSensorType==null){
            throw new RuntimeException("传感器类型不存在!");
        }
        //建筑物
        DeviceBuilding deviceBuilding = dbBiz.selectById(deviceSensor.getBuildingId());
        if(deviceBuilding==null){
            throw new RuntimeException("建筑物不存在!");
        }
        map.put("type",deviceSensorType.getEquipmentType());//设备类型
        map.put("buildingId",deviceBuilding.getId());//建筑id
        map.put("channelId",deviceSensor.getChannelId());//所属系统id
        map.put("sensorNo",deviceSensor.getSensorNo());
        map.put("manufacturer",deviceSensorType.getManufacturer());
        map.put("model",deviceSensorType.getModel());
        String dataAcquisitionCycleUnit = null;
        if("0".equals(deviceSensorType.getDataAcquisitionCycleUnit())){
           dataAcquisitionCycleUnit = "秒";
        }
        if("1".equals(deviceSensorType.getDataAcquisitionCycleUnit())){
            dataAcquisitionCycleUnit = "分钟";
        }
        if("2".equals(deviceSensorType.getDataAcquisitionCycleUnit())){
            dataAcquisitionCycleUnit = "小时";
        }
        if("3".equals(deviceSensorType.getDataAcquisitionCycleUnit())){
            dataAcquisitionCycleUnit = "天";
        }
        if("4".equals(deviceSensorType.getDataAcquisitionCycleUnit())){
            dataAcquisitionCycleUnit = "月";
        }
        String acquisitionDelayTimeUnit = null;
        if("0".equals(deviceSensorType.getAcquisitionDelayTimeUnit())){
            acquisitionDelayTimeUnit = "秒";
        }
        if("1".equals(deviceSensorType.getAcquisitionDelayTimeUnit())){
            acquisitionDelayTimeUnit = "分钟";
        }
        if("2".equals(deviceSensorType.getAcquisitionDelayTimeUnit())){
            acquisitionDelayTimeUnit = "小时";
        }
        if("3".equals(deviceSensorType.getAcquisitionDelayTimeUnit())){
            acquisitionDelayTimeUnit = "天";
        }
        if("4".equals(deviceSensorType.getAcquisitionDelayTimeUnit())){
            acquisitionDelayTimeUnit = "月";
        }
        String maintenanceCycleUnit = null;
        if("0".equals(deviceSensorType.getMaintenanceCycleUnit())){
            maintenanceCycleUnit = "小时";
        }
        if("1".equals(deviceSensorType.getMaintenanceCycleUnit())){
            maintenanceCycleUnit = "天";
        }
        if("2".equals(deviceSensorType.getMaintenanceCycleUnit())){
            maintenanceCycleUnit = "年";
        }
        map.put("dataAcquisitionCycleValue",deviceSensorType.getDataAcquisitionCycleValue()+dataAcquisitionCycleUnit);
        map.put("acquisitionDelayTimeValue",deviceSensorType.getAcquisitionDelayTimeValue()+acquisitionDelayTimeUnit);
        map.put("maintenanceCycleValue",deviceSensorType.getMaintenanceCycleValue()+maintenanceCycleUnit);
        map.put("bName",deviceBuilding.getBName());
        map.put("floor",deviceSensor.getFloor());
        map.put("type",deviceSensor.getPositionDescription());
        map.put("status",deviceSensor.getStatus());
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getOutdoorSensorById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据室外传感器id查询设备详细信息")
    public ObjectRestResponse  getOutdoorSensorById(@RequestParam long id){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map map = new HashMap();
        //传感器
        DeviceSensor deviceSensor = dsBiz.selectById(id);
        if(deviceSensor==null){
            throw new RuntimeException("传感器不存在!");
        }
        //传感器类型
        DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());
        if(deviceSensorType==null){
            throw new RuntimeException("传感器类型不存在!");
        }
        //硬件设施
        DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.selectById(deviceSensor.getHydrantId());
        if(deviceHardwareFacilities==null){
            throw new RuntimeException("硬件设施不存在!");
        }
        map.put("name",deviceHardwareFacilities.getHydrantName());//消火栓名称
        map.put("hydrantId",deviceHardwareFacilities.getId());//消火栓名称
        map.put("type",deviceSensorType.getEquipmentType());//设备类型
        map.put("channelId",deviceSensor.getChannelId());//所属系统id
        map.put("sensorNo",deviceSensor.getSensorNo());
        map.put("manufacturer",deviceSensorType.getManufacturer());
        map.put("model",deviceSensorType.getModel());
        String dataAcquisitionCycleUnit = null;
        if("0".equals(deviceSensorType.getDataAcquisitionCycleUnit())){
            dataAcquisitionCycleUnit = "秒";
        }
        if("1".equals(deviceSensorType.getDataAcquisitionCycleUnit())){
            dataAcquisitionCycleUnit = "分钟";
        }
        if("2".equals(deviceSensorType.getDataAcquisitionCycleUnit())){
            dataAcquisitionCycleUnit = "小时";
        }
        if("3".equals(deviceSensorType.getDataAcquisitionCycleUnit())){
            dataAcquisitionCycleUnit = "天";
        }
        if("4".equals(deviceSensorType.getDataAcquisitionCycleUnit())){
            dataAcquisitionCycleUnit = "月";
        }
        String acquisitionDelayTimeUnit = null;
        if("0".equals(deviceSensorType.getAcquisitionDelayTimeUnit())){
            acquisitionDelayTimeUnit = "秒";
        }
        if("1".equals(deviceSensorType.getAcquisitionDelayTimeUnit())){
            acquisitionDelayTimeUnit = "分钟";
        }
        if("2".equals(deviceSensorType.getAcquisitionDelayTimeUnit())){
            acquisitionDelayTimeUnit = "小时";
        }
        if("3".equals(deviceSensorType.getAcquisitionDelayTimeUnit())){
            acquisitionDelayTimeUnit = "天";
        }
        if("4".equals(deviceSensorType.getAcquisitionDelayTimeUnit())){
            acquisitionDelayTimeUnit = "月";
        }
        String maintenanceCycleUnit = null;
        if("0".equals(deviceSensorType.getMaintenanceCycleUnit())){
            maintenanceCycleUnit = "小时";
        }
        if("1".equals(deviceSensorType.getMaintenanceCycleUnit())){
            maintenanceCycleUnit = "天";
        }
        if("2".equals(deviceSensorType.getMaintenanceCycleUnit())){
            maintenanceCycleUnit = "年";
        }
        map.put("dataAcquisitionCycleValue",deviceSensorType.getDataAcquisitionCycleValue()+dataAcquisitionCycleUnit);
        map.put("acquisitionDelayTimeValue",deviceSensorType.getAcquisitionDelayTimeValue()+acquisitionDelayTimeUnit);
        map.put("maintenanceCycleValue",deviceSensorType.getMaintenanceCycleValue()+maintenanceCycleUnit);
        map.put("type",deviceSensor.getPositionDescription());
        map.put("status",deviceSensor.getStatus());
        responseResult.setData(map);
        return responseResult;
    }

    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/updateStatus",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("更新传感器状态")
    public ObjectRestResponse  updateStatus(@RequestParam String sensorNo,@RequestParam String statusTime,@RequestParam String status) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        if(ValidatorUtils.hasAnyBlank(sensorNo,statusTime,status)){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DeviceSensor deviceSensor = dsBiz.selectBySensorNo(sensorNo);
        if(deviceSensor!=null){
            try {
                if(deviceSensor.getStatusTime()==null || deviceSensor.getStatusTime().getTime()<simpleDateFormat.parse(statusTime).getTime()){
                    deviceSensor.setStatusTime(simpleDateFormat.parse(statusTime));
                    deviceSensor.setStatus(status);
                    dsBiz.updateSelectiveById(deviceSensor);//更新状态
                }else {
                    throw new RuntimeException("传感器已经是最新状态!");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return responseResult;
    }

//    @RequestMapping(value = "/getAllStatusCountAPP",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("查询APP所有传感器状态的数量")
//    public ObjectRestResponse getAllStatusCountAPP(Integer channelId){
//        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        Integer faultCount = dsBiz.selectByChannelIdAndStatusAndBuilding(channelId,"0",null,null); //故障
//        Integer faultTempCount =dsBiz.selectByChannelIdAndStatusAndBuilding(channelId,"4",null,null); //离线,算故障
//        Integer callCount = dsBiz.selectByChannelIdAndStatusAndBuilding(channelId,"1",null,null); //报警
//        Integer normalCount =dsBiz.selectByChannelIdAndStatusAndBuilding(channelId,"2",null,null); //正常
//        Map map = new HashMap();
//        map.put("faultCount",faultCount+faultTempCount);
//        map.put("callCount",callCount);
//        map.put("normalCount",normalCount);
////        //分数
////        Double marks = new BigDecimal((float)normalCount / (faultCount+callCount+normalCount)).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
////        map.put("marks",Math.round(marks*100));
//        responseResult.setData(map);
//        return responseResult;
//    }

    @RequestMapping(value = "/getAllStatusCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有传感器状态的数量")
    @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    public ObjectRestResponse getAllStatusCount(Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        Integer faultCount = dsBiz.selectByChannelIdAndStatusAndBuilding(channelId,"0",null,null); //故障
//        Integer faultTempCount =dsBiz.selectByChannelIdAndStatusAndBuilding(channelId,"4",null,null); //离线,算故障
//        Integer callCount = dsBiz.selectByChannelIdAndStatusAndBuilding(chaselectStatusCountnnelId,"1",null,null); //报警
//        Integer normalCount =dsBiz.selectByChannelIdAndStatusAndBuilding(channelId,"2",null,null); //正常
        CountVo countVo = dsBiz.getCountByStatus(null,null,channelId);
        //消防主机
        CountVo countVo1 = dfmsBiz.getCountByStatus(null,null,channelId);
        Integer faultCount = countVo.getFaultCount()+countVo.getOffCount()+countVo1.getOffCount()+countVo1.getFaultCount();
        Integer callCount = countVo.getCallCount()+countVo1.getCallCount();
        Integer normalCount = countVo.getNormalCount()+countVo1.getNormalCount();
        Map map = new HashMap();//返回map
        map.put("faultCount",faultCount);
        map.put("callCount",callCount);
        map.put("normalCount",normalCount);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getHydrantAllStatusCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询消防给水下实时监测所有传感器状态的数量")
    @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    public ObjectRestResponse getHydrantAllStatusCount(Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Integer faultCount = dsBiz.selectByChannelIdAndStatusAndHydrant(channelId,"0",null); //故障
        Integer faultTempCount =dsBiz.selectByChannelIdAndStatusAndHydrant(channelId,"4",null); //离线,算故障
        Integer normalCount =dsBiz.selectByChannelIdAndStatusAndHydrant(channelId,"2",null); //正常
        Map map = new HashMap();
        map.put("faultCount",faultCount+faultTempCount);
        map.put("normalCount",normalCount);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getAllCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询概览下所有传感器状态的数量")
    public ObjectRestResponse getAllCount(){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        CountVo countVo = dsBiz.getCountByStatus(null,null,null);
        //消防主机
        CountVo countVo1 = dfmsBiz.getCountByStatus(null,null,null);
        Map map = new HashMap();
        map.put("faultCount",countVo.getFaultCount()+countVo1.getFaultCount());
        map.put("normalCount",countVo.getNormalCount()+countVo1.getNormalCount());
        map.put("callCount",countVo.getCallCount()+countVo1.getCallCount());
        map.put("offCount",countVo.getOffCount()+countVo1.getOffCount());
        responseResult.setData(map);
        return responseResult;
    }



    @RequestMapping(value = "/sensorImport",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("导入传感器1.5")
    public ObjectRestResponse sensorImport(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam Integer id,@RequestParam Integer channelId ) {
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if (file==null||id==null) {
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else if(file.getSize()>10*1024*1024){
            throw new RuntimeException("文件大小不能超过10M!");
        }else{
            String fileName = file.getOriginalFilename();
            InputStream inputStream= null;
            List<String[]> excel  = null;
            POIUtil poiUtil=new POIUtil();
            try {
                inputStream = file.getInputStream();
                excel = poiUtil.readExcel(fileName,inputStream);
                inputStream.close();
            } catch (Exception e) {
                throw new RuntimeException("导入出错!",e);
            }
            //模板的字段
            boolean sensorNoFlag=false;
            boolean typeNoFlag=false;
            boolean floorFlag=false;
            boolean positionDescriptionFlag=false;
            //模板字段的位置标记
            int sensorNoCount=0;
            int typeNoCount=0;
            int floorCount=0;
            int positionDescriptionCount=0;
            if(excel.size()>1){
                String[] s =  excel.get(0);
                for(int i=0;i<s.length;i++){
                    if("设备编号".equals(s[i])||"设备编号(必填)".equals(s[i])||"设备编号（必填）".equals(s[i])){
                        sensorNoFlag=true;
                        sensorNoCount=i;
                    }
                    if("类型编号".equals(s[i])||"类型编号(必填)".equals(s[i])||"类型编号（必填）".equals(s[i])){
                        typeNoFlag=true;
                        typeNoCount=i;
                    }
                    if("楼层".equals(s[i])||"楼层(必填)".equals(s[i])||"楼层（必填）".equals(s[i])){
                        floorFlag=true;
                        floorCount=i;
                    }
                    if("位置描述".equals(s[i])||"位置描述(必填)".equals(s[i])||"位置描述（必填）".equals(s[i])){
                        positionDescriptionFlag=true;
                        positionDescriptionCount=i;
                    }
                }
                //判断Excel表模板是否正确
                if(sensorNoFlag&&floorFlag&&positionDescriptionFlag&&typeNoFlag){

                    //获取所属系统
                    JSONObject a= iUserFeign.selectById(channelId);
                    JSONObject data = a.getJSONObject("data");


                    //判断Excel里面编号是否重复
                    Set<String> setNo = new HashSet<>();
                    //获取所有的编号
                    Set<String> setAllNo = dsBiz.getAllIgnoreTenantSensorNo();
                    //获取所有的厂商，系列，型号  用空格隔开

                    List<Integer> typeIds = dstBiz.selectByChannelId(channelId);
                    Set<Integer> setSensor =new HashSet<>(typeIds);
                    //获取楼层判断楼层是否为非法楼层
                    DeviceBuilding buildings=dbBiz.selectById(id);
                    //地下层数
                    int minFloor = buildings.getUnderFloor();
                    //地上层数
                    int maxFloor = buildings.getUpFloor();
                    //数据检验
                    for (int i=1;i<excel.size();i++){
                        String[] exs = new String[s.length];
                        String[] str = excel.get(i);
                        if(str.length<s.length) {
                            System.arraycopy(str,0,exs,0,str.length);
                        }else {
                            exs = str;
                        }
                        for(int j=1;j<s .length;j++){
                            if(StringUtils.isNotBlank(exs[j])){
                                exs[j] = exs[j].trim();
                            }
                            if(j==sensorNoCount){
                                //判断编号是否为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的设备编号为空，无法导入!");
                                }
                                //判断设备编号长度
                                if(exs[j].length()>16){
                                    throw new RuntimeException("第"+exs[0]+"行的设备编号长度超过16");
                                }
                                if(!Pattern.matches("^[a-z0-9]+$", exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的设备编号只允许小写字母与数字");
                                }
                                //判断编号在Excel里面是否重复
                                if(!setNo.add(exs[j])){
                                    throw new RuntimeException("文件中设备编号"+exs[j]+"存在重复");
                                }
                                //判读编号在数据库是否重复
                                if(!setAllNo.add(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的设备编号已绑定建筑");
                                }
                            }
                            else if(j==typeNoCount){
                                //判断类型编号是否为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的类型编号为空，无法导入!");
                                }
                                try{
                                    if(setSensor.add(Integer.parseInt(exs[j]))){
                                        throw new RuntimeException("第"+exs[0]+"行的类型编号与"+data.get("channelName")+"系统不符，无法导入");
                                    }
                                }catch (Exception e){
                                    throw new RuntimeException("第"+exs[0]+"行的类型编号与"+data.get("channelName")+"系统不符，无法导入");
                                }
                            }else if(j==floorCount){
                                //判断楼层是否为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的楼层为空，无法导入");
                                }
                                //判断楼层是否合法
                                try {
                                    if(!(Integer.parseInt(exs[j])>=-minFloor&&Integer.parseInt(exs[j])<=maxFloor&&Integer.parseInt(exs[j])!=0)){
                                        throw new RuntimeException("第"+exs[0]+"行的楼层数在建筑中不存在");
                                    }
                                }catch (Exception e){
                                    throw new RuntimeException("第"+exs[0]+"行的楼层数在建筑中不存在");
                                }
                            }else if(j==positionDescriptionCount){
                                //判断位置描述是否为空  excel最后一行数据为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的位置描述为空，无法导入");
                                }
                                if(exs[j].length()>100){
                                    throw new RuntimeException("第"+exs[0]+"行的位置描述字符长度超过100");
                                }
                            }
                        }
                    }
                    //去除第一条数据
                    excel.remove(0);
                    int insertCount = 0;
                    DeviceSensorType deviceSensorType = null;
                    DeviceSensor deviceSensor = null;
                    for(int i=0;i<excel.size();i++){
                        String [] temp = excel.get(i);
                        deviceSensorType = dstBiz.selectById(Integer.parseInt(temp[typeNoCount]));
                        deviceSensor=new DeviceSensor();
                        deviceSensor.setSensorNo(temp[sensorNoCount].trim());
                        deviceSensor.setBuildingId(id);
                        //默认未启用
                        deviceSensor.setStatus("3");
                        deviceSensor.setFloor(Integer.parseInt(temp[floorCount]));
                        deviceSensor.setPositionDescription(temp[positionDescriptionCount]);
                        deviceSensor.setPositionSign("");
                        deviceSensor.setSensorTypeId( Integer.parseInt(temp[typeNoCount]));
                        deviceSensor.setChannelId(deviceSensorType.getChannelId());
                        try {
                            TrimUtil.trimObject(deviceSensor);
                            baseBiz.insertSelective(deviceSensor);
                            insertCount = insertCount+1;
                        }catch (Exception e){
                            throw new RuntimeException("插入数据异常！");
                        }
                    }
                    responseResult.setData(insertCount);
                    return responseResult;
                }else{
                    throw new RuntimeException("Excel模板错误!");
                }
            }else{
                throw new RuntimeException("文件内容为空!");
            }
        }
    }


    @RequestMapping(value = "/getAllBuildingList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("列表模式，查询所有的楼层信息，传感器的信息")
    public TableResultResponse getAllBuildingList(Integer channelId, String page, String limit) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "15";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dsBiz.getAllBuildingList(query,channelId);
    }

    @RequestMapping(value = "/selectByFloorGetSensor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("设备列表，根据建筑id，楼层id获得传感器")
    public TableResultResponse selectByFloorGetSensor(Integer channelId,@RequestParam Integer buildingId,@RequestParam Integer floor, String page, String limit,String status) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "15";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dsBiz.selectByFloorGetSensor(query,channelId,buildingId,floor,status);
    }

    @RequestMapping(value = "/selectByHydrantIdGetSensor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("设备列表，根据消火栓id获得传感器")
    public TableResultResponse selectByHydrantIdGetSensor(Integer channelId,@RequestParam Integer hydrantId, String page, String limit,String status) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "15";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dsBiz.selectByHydrantIdGetSensor(query,channelId,hydrantId,status);
    }

    @RequestMapping(value = "/selectBySensorId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据传感器id查询测点实时数据")
    public ObjectRestResponse  selectBySensorId(@RequestParam String sensorId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List resultList = new ArrayList();
        //传感器
        DeviceSensor deviceSensor = dsBiz.selectById(Long.valueOf(sensorId));
        if("3".equals(deviceSensor.getStatus())){
            responseResult.setData(resultList);
            return responseResult;
        }
        if("4".equals(deviceSensor.getStatus())){
            Map<String,Object> resultmap = new HashMap();
            resultmap.put("date",DateUtil.getHandletime(new Date(),deviceSensor.getStatusTime()));
            responseResult.setData(resultmap);
            return responseResult;
        }
        DeviceSensorType deviceSensorType =dstBiz.selectById(deviceSensor.getSensorTypeId());
        //查询传感器系列
        DeviceSensorSeries deviceSensorSeries = dssBiz.selectBySensorTypeId(deviceSensorType.getId());
        //查询测点ids
        List<Integer> ids = dsmrBiz.selectBySensorSeriesId(deviceSensorSeries.getId());
        for(Integer id:ids){
            DeviceMeasuringPoint deviceMeasuringPoint =dmpBiz.selectById(id);
            Map<String,Object> resultmap = new HashMap();
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            //mac地址
            if (deviceSensor.getSensorNo() != null) {
                queryBuilder.must(QueryBuilders.matchQuery("deviceid", deviceSensor.getSensorNo()));
            }
            SortBuilder sortBuilder = SortBuilders.fieldSort("uploadtime").unmappedType("date").order(SortOrder.DESC);
            SearchResponse searchResponse = client.prepareSearch(Constant.ESConstant.ES_INDEX_SENSOR).setQuery(queryBuilder).setSize(1).addSort(sortBuilder).execute().actionGet();
            SearchHits searchHits = searchResponse.getHits();
            for(SearchHit hit:searchHits){
                //System.out.println(hit.getSourceAsString());
                //Map map = hit.getFields();
                JSONObject jasonObject = JSONObject.parseObject(hit.getSourceAsString());
                //System.out.println(jasonObject.get(deviceMeasuringPoint.getCodeName()));
                if(jasonObject.containsKey(deviceMeasuringPoint.getCodeName())){
                    JSONObject jasonObject1 = JSONObject.parseObject(jasonObject.get(deviceMeasuringPoint.getCodeName()).toString());
                        resultmap.put("measuringPoint",deviceMeasuringPoint.getMeasuringPoint());
                        resultmap.put("data",jasonObject1.get("alarmValue")+deviceMeasuringPoint.getDataUnit());
                        resultmap.put("status",jasonObject1.get("alarmStatus"));
                        resultmap.put("type",jasonObject1.get("alarmType"));
                }
            }
            if(resultmap!=null && resultmap.size()!=0){
                resultList.add(resultmap);
            }
        }
        responseResult.setData(resultList);
        return responseResult;
    }


    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/updateAllStatus",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("更新特定所有传感器状态为正常")
    public ObjectRestResponse  updateAllStatus(String prefix) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<DeviceSensor> list = dsBiz.selectByBuildingId(null,null);
        for(DeviceSensor deviceSensor:list){
            if(deviceSensor.getSensorNo().startsWith(prefix)){
                deviceSensor.setStatus("2");
                dsBiz.updateSelectiveById(deviceSensor);
            }
        }
        return responseResult;
    }


    @RequestMapping(value = "/getSensorId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器编号获取id")
    public ObjectRestResponse  getSensorId(@RequestParam String sensor) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //将大写全部转换为小写
        sensor = sensor.toLowerCase();
        DeviceSensor temp = new DeviceSensor();
        temp.setSensorNo(sensor);
        ValidatorUtils.validateEntity(temp);
        DeviceSensor deviceSensor = dsBiz.selectBySensorNo(sensor);
        if(deviceSensor==null){
            responseResult.setStatus(401);
            responseResult.setMessage("传感器不存在！");
            return responseResult;
        }
        DeviceSensorType deviceSensorType =  dstBiz.selectById(deviceSensor.getSensorTypeId());
        JSONObject a= iUserFeign.selectById(deviceSensorType.getChannelId());
        JSONObject data = a.getJSONObject("data");
        //[1=消防给水/0=其他]
        String channelFlag = "0";
        if(data!=null&&data.getString("id")!=null){
            if("5".equals(data.getString("id"))){
                channelFlag = "1";
            }
        }
        Map map = new HashMap();
        map.put("id",deviceSensor.getId());
        map.put("equipmentType",deviceSensorType.getEquipmentType());
        map.put("channelFlag",channelFlag);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/selectByHydrantId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询消火栓下传感器的实时数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hydrantId",value = "消火栓id",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query"),
    })
    public ObjectRestResponse  selectByHydrantId(@RequestParam Integer hydrantId,Integer channelId) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<DeviceSensor> list = dsBiz.getByHydrantId(hydrantId,channelId);
        List resultlist = new ArrayList();
        if(list.size()==0){
            responseResult.setData(resultlist);
            return responseResult;
        }
        for(DeviceSensor deviceSensor:list){
            if("3".equals(deviceSensor.getStatus())){
                continue;
            }
            DeviceSensorType deviceSensorType =dstBiz.selectById(deviceSensor.getSensorTypeId());
            //查询传感器系列
            DeviceSensorSeries deviceSensorSeries = dssBiz.selectBySensorTypeId(deviceSensorType.getId());
            //查询测点ids
            List<Integer> ids = dsmrBiz.selectBySensorSeriesId(deviceSensorSeries.getId());
            JSONObject jasonObject = getESlastData(deviceSensor.getSensorNo());
            for(Integer id:ids){
                Map<String,Object> resultmap = new HashMap();
                DeviceMeasuringPoint deviceMeasuringPoint =dmpBiz.selectById(id);
                if(deviceMeasuringPoint==null){
                    throw new RuntimeException("测点不存在!");
                }
                if(jasonObject.containsKey(deviceMeasuringPoint.getCodeName())){
                    JSONObject jasonObject1 = JSONObject.parseObject(jasonObject.get(deviceMeasuringPoint.getCodeName()).toString());
                    resultmap.put("measuringPoint",deviceMeasuringPoint.getMeasuringPoint());
                    if(jasonObject1.get("alarmValue")!=null){
                        resultmap.put("data",jasonObject1.get("alarmValue")+deviceMeasuringPoint.getDataUnit());
                    }
                    resultlist.add(resultmap);
                }
            }
        }
        responseResult.setData(resultlist);
        return responseResult;
    }

    public static ArrayList<DeviceSensorType> removeDuplicateSensor(List<DeviceSensorType> deviceSensorType) {
         Set<DeviceSensorType> set = new TreeSet<DeviceSensorType>(new Comparator<DeviceSensorType>() {
             @Override
             public int compare(DeviceSensorType o1, DeviceSensorType o2) {
                 //字符串,则按照asicc码升序排列
                 return o1.getEquipmentType().compareTo(o2.getEquipmentType());
             }
         });
         set.addAll(deviceSensorType);
         return new ArrayList<DeviceSensorType>(set);
    }

    //获取es  传感器的最后一条数据
    public JSONObject getESlastData(String deviceid){
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        JSONObject jsonObject = new JSONObject();
        //mac地址
        if (org.apache.commons.lang.StringUtils.isNotBlank(deviceid)) {
            queryBuilder.must(QueryBuilders.matchQuery("deviceid", deviceid));
            SortBuilder sortBuilder = SortBuilders.fieldSort("uploadtime").unmappedType("date").order(SortOrder.DESC);
            SearchResponse searchResponse = client.prepareSearch(Constant.ESConstant.ES_INDEX_SENSOR).setQuery(queryBuilder).setSize(1).addSort(sortBuilder).execute().actionGet();
            SearchHits searchHits = searchResponse.getHits();
            if(searchHits.getTotalHits()>0){
                 jsonObject = JSONObject.parseObject(searchHits.getAt(0).getSourceAsString());
            }
        }
        return jsonObject;
    }

    @RequestMapping(value = "/pageOutdoorList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("室外传感器列表")
    public TableResultResponse<DeviceSensor> pageOutdoorList( String page, String limit,String code,String facilityType,String hydrantName, DeviceSensor deviceSensor) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "15";
        }
        String ids = "";
        //选择地区时传地区编码
        if(StringUtils.isNotBlank(code)){
            if(code.length()!=6){
              ids = "-1";
            }else {
                if("00".equals(code.substring(2,4))){
                    code = code.substring(0,2)+"____";
                }else if("00".equals(code.substring(4))){
                    code = code.substring(0,4)+"__";
                }
                if("00".equals(code.substring(2,4))){
                    code = code.substring(0,2)+"____";
                }else if("00".equals(code.substring(4))){
                    code = code.substring(0,4)+"__";
                }
                List<Integer> lists = dhfBiz.selectByZxqzResultIds(code,null);
                ids = SplitUtil.merge(lists);
            }
        }
        if(StringUtils.isBlank(ids)){
            return new TableResultResponse<DeviceSensor>();
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dsBiz.selectOutdoorQuery(query,ids,facilityType,hydrantName,deviceSensor);
    }

    @RequestMapping(value = "/sensorOutdoorImport",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("导入室外传感器")
    public ObjectRestResponse sensorOutdoorImport(@RequestParam(value = "file", required = false) MultipartFile file ,@RequestParam Integer channelId ) throws FileNotFoundException {
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if (file == null) {
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else if(file.getSize()>10*1024*1024){
            throw new RuntimeException("文件大小不能超过10M!");
        }else{
            String fileName = file.getOriginalFilename();
            InputStream inputStream = null;
            List<String[]> excel  = null;
            POIUtil poiUtil=new POIUtil();
            try {
                inputStream = file.getInputStream();
                excel = poiUtil.readExcel(fileName,inputStream);
                inputStream.close();
            } catch (Exception e) {
                throw new RuntimeException("导入出错!");
            }
            int formatLenth = 5;
            boolean sensorNoFlag=false;
            boolean typeNoFlag=false;
            boolean facilityTypeFlag=false;
            boolean hydrantNameFlag=false;

            //模板字段的位置标记
            int sensorNoCount=1;
            int typeNoCount = 2;
            int facilityTypeCount=3;
            int hydrantNameCount=4;

            String[] s =  excel.get(0);
            for(int i=0;i<s.length;i++){
                if("*设备编号".equals(s[i])){
                    sensorNoFlag=true;
//                    sensorNoCount=i;
                }
                if("*类型编号".equals(s[i])){
                    typeNoFlag=true;
//                    typeNoCount=i;
                }
                if("*设施类型".equals(s[i])){
                    facilityTypeFlag=true;
//                    facilityTypeCount=i;
                }
                if("*所属设施".equals(s[i])){
                    hydrantNameFlag=true;
//                    hydrantNameCount=i;
                }
            }


            if(excel.size()>2){
                //判断Excel表模板是否正确
                if(sensorNoFlag&&typeNoFlag&&facilityTypeFlag&&hydrantNameFlag){
                    //获取所属系统
                    JSONObject a= iUserFeign.selectById(channelId);
                    JSONObject data = a.getJSONObject("data");

                    //判断Excel里面编号是否重复
                    Set<String> setNo = new HashSet<>();
                    //获取所有的编号
                    Set<String> setAllNo = dsBiz.getAllIgnoreTenantSensorNo();
                    //获取所有的厂商，系列，型号  用空格隔开
                    List<Integer> typeIds = dstBiz.selectByChannelId(channelId);
                    Set<Integer> setSensor =new HashSet<>(typeIds);
                    //数据检验
                    for (int i=2;i<excel.size();i++){
                        String[] exs = new String[formatLenth];
                        String[] str = excel.get(i);
                        if(str.length<formatLenth) {
                            System.arraycopy(str,0,exs,0,str.length);
                        }else {
                            exs = str;
                        }
                        for(int j=1;j<formatLenth;j++){
                            if(StringUtils.isNotBlank(exs[j])){
                                exs[j] = exs[j].trim();
                            }
                            if(j==sensorNoCount){
                                //判断编号是否为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的编号为空，无法导入");
                                }
                                //判断设备编号长度
                                if(exs[j].length()>16){
                                    throw new RuntimeException("第"+exs[0]+"行的设备编号长度超过16");
                                }
                                if(!Pattern.matches("^[a-z0-9]+$", exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的设备编号只允许小写字母与数字");
                                }
                                //判断编号在Excel里面是否重复
                                if(!setNo.add(exs[j])){
                                    throw new RuntimeException("文件中设备编号"+exs[j]+"存在重复");
                                }
                                //判读编号在数据库是否重复
                                if(!setAllNo.add(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的设备编号已绑定建筑");
                                }
                            }else if(j==typeNoCount){
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的类型编号为空，无法导入");
                                }
                                try{
                                    if(setSensor.add(Integer.parseInt(exs[j]))){
                                        throw new RuntimeException("第"+exs[0]+"行的类型编号与"+data.get("channelName")+"系统不符，无法导入");
                                    }
                                }catch (Exception e){
                                    throw new RuntimeException("第"+exs[0]+"行的类型编号与"+data.get("channelName")+"系统不符，无法导入");
                                }
                            }else if(j==facilityTypeCount){
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的设施类型为空，无法导入");
                                }
                                //判断设施类型是否合法
                                if("室外消火栓".equals(exs[j])){
                                    exs[j] = "0";
                                }else {
                                    throw new RuntimeException("第"+exs[0]+"行的设施类型在系统中找不到，无法导入");
                                }
                            }else if(j==hydrantNameCount){
                                //判断所属设施是否为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的所属设施为空，无法导入");
                                }
                                List<DeviceHardwareFacilities> temp = dhfBiz.selectByNameAndType(exs[facilityTypeCount],exs[hydrantNameCount]);
                                if(temp.size()>0){
                                    exs[j] = temp.get(0).getId()+"";
                                }else {
                                    throw new RuntimeException("第"+exs[0]+"行的所属设施在系统中找不到，无法导入");
                                }
                            }
                        }
                    }
                    //去除第一，二条数据
                    excel.remove(0);
                    excel.remove(0);
                    int insertCount = 0;
                    DeviceSensorType deviceSensorType = null;
                    DeviceSensor deviceSensor = null;
                    for(int i=0;i<excel.size();i++){
                        String [] temp = excel.get(i);
                        deviceSensor=new DeviceSensor();
                        deviceSensor.setSensorNo(temp[sensorNoCount].trim());
                        //默认未启用
                        deviceSensor.setStatus("3");
                        deviceSensorType = dstBiz.selectById(Integer.parseInt(temp[typeNoCount].trim()));
                        deviceSensor.setSensorTypeId( deviceSensorType.getId());
                        deviceSensor.setChannelId(deviceSensorType.getChannelId());
                        deviceSensor.setHydrantId(Integer.parseInt(temp[hydrantNameCount].trim()));
                        try {
                            TrimUtil.trimObject(deviceSensor);
                            baseBiz.insertSelective(deviceSensor);
                            insertCount = insertCount+1;
                        }catch (Exception e){
                            throw new RuntimeException("插入数据异常");
                        }
                    }
                    responseResult.setData(insertCount);
                    return responseResult;
                }else{
                    throw new RuntimeException("Excel模板错误");
                }
            }else{
                throw new RuntimeException("文件内容为空");
            }
        }
    }

    @RequestMapping(value = "/addOutdoor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加室外传感器")
    public ObjectRestResponse<DeviceSensor> addOutdoor(@RequestBody  Map<String,Object> params){
        DeviceSensor deviceSensor = new DeviceSensor();
        try{
            if( params.get("sensorNo")==null){
                throw new RuntimeException("没有输入传感器");
            }
            deviceSensor.setSensorNo((String) params.get("sensorNo"));
            //deviceSensor.setFloor((Integer) params.get("floor"));
            //deviceSensor.setPositionSign((String) params.get("positionSign"));
            //deviceSensor.setPositionDescription((String) params.get("positionDescription"));
//            deviceSensor.setChannelId((Integer) params.get("channelId"));
        }catch (Exception e){
            throw new RuntimeException("参数错误!");
        }
        try{
            deviceSensor.setHydrantId((Integer) params.get("hydrantId"));
        }catch (Exception e){
            deviceSensor.setHydrantId(Integer.parseInt((String) params.get("hydrantId")));
        }
        TrimUtil.trimObject(deviceSensor);
        TrimUtil.trimNull(deviceSensor.getSensorNo());
        deviceSensor.setSensorNo(deviceSensor.getSensorNo().toLowerCase());
        ValidatorUtils.validateEntity(deviceSensor);
        List<String> entity = JSON.parseArray((String) params.get("entity"),String.class);
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(deviceSensor==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else {
            DeviceSensor sensor = dsBiz.selectBySensorNo(deviceSensor.getSensorNo());
            if(sensor!=null){
                throw  new RuntimeException(Constants.SENSOR_NO_REPEAT);
            }
            List<DeviceSensorType> list = dstBiz.selectByType(entity.get(0),entity.get(2),entity.get(1));
            if(list.size()==1){
                deviceSensor.setSensorTypeId(list.get(0).getId());
                deviceSensor.setChannelId(list.get(0).getChannelId());
            }else{
                throw new RuntimeException("没有找到厂商系列型号");
            }
            //默认未启用
            deviceSensor.setStatus("3");
            deviceSensor.setId(null);
            deviceSensor.setBuildingId(null);
            dsBiz.insertSelective(deviceSensor);
            responseResult.setData(deviceSensor.getId());
        }
        return responseResult;
    }

    @RequestMapping(value = "/updateOutdoor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("编辑室外传感器")
    public ObjectRestResponse updateOutdoor(@RequestBody  Map<String,Object> params){
        ObjectRestResponse<DeviceSensor> responseResult = new ObjectRestResponse();
        DeviceSensor deviceSensor = new DeviceSensor();
        try{
            deviceSensor.setId(Long.parseLong((String) params.get("id")));
            deviceSensor.setSensorNo((String) params.get("sensorNo"));
            //deviceSensor.setFloor((Integer) params.get("floor"));
//            deviceSensor.setPositionSign((String) params.get("positionSign"));
//            deviceSensor.setPositionDescription((String) params.get("positionDescription"));
            deviceSensor.setChannelId((Integer) params.get("channelId"));
        }catch (Exception e){
            throw new RuntimeException("参数错误!");
        }
        try{
            deviceSensor.setHydrantId((Integer) params.get("hydrantId"));
        }catch (Exception e){
            deviceSensor.setHydrantId(Integer.parseInt((String) params.get("hydrantId")));
        }
        TrimUtil.trimObject(deviceSensor);
        TrimUtil.trimNull(deviceSensor.getSensorNo());
        deviceSensor.setSensorNo(deviceSensor.getSensorNo().toLowerCase());
        ValidatorUtils.validateEntity(deviceSensor);
        List<String> entity = JSON.parseArray((String) params.get("entity"),String.class);
        if(deviceSensor==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            if(baseBiz.queryIdIsDel(deviceSensor.getId())==0){
                responseResult.setStatus(501);
                responseResult.setMessage("设备在系统不存在");
                return responseResult;
            }else {
                String sensorNo = deviceSensor.getSensorNo();
                if(sensorNo==null||sensorNo.equals("")){
                    throw new RuntimeException("编号不能为空！");
                }
                DeviceSensor sensor = dsBiz.selectById(deviceSensor.getId());
                List<DeviceSensorType> list = dstBiz.selectByType(entity.get(0),entity.get(2),entity.get(1));
                if(list.size()==1){
                    deviceSensor.setSensorTypeId(list.get(0).getId());
                    deviceSensor.setChannelId(list.get(0).getChannelId());
                }else{
                    throw new RuntimeException("没有找到厂商系列型号");
                }
                if((!sensor.getSensorNo().equalsIgnoreCase(deviceSensor.getSensorNo()))&&dsBiz.selectBySensorNo(deviceSensor.getSensorNo())!=null){
                    throw new RuntimeException(Constants.SENSOR_NO_REPEAT);
                }
                deviceSensor.setBuildingId(null);
                dsBiz.updateSelectiveById(deviceSensor);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/selectListByBuildId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据建筑id分页查询传感器报警信息列表")
    public TableResultResponse  selectListByBuildId(Integer buildId,String status,String page,String limit,Integer channelId){
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "10";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dsBiz.selectListByBuildId(query,buildId,status,channelId);
    }

    @RequestMapping(value = "/getAlrmFloor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑id获得报警楼层")
    public ObjectRestResponse getAlrmFloor(@RequestParam Integer buildId,Integer channelId) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        Map map = new HashMap();
        List<Map> result = new ArrayList<>();
        List<DeviceSensor> list = dsBiz.selectByBuildingId(buildId,channelId);
        //获取所有的楼层
        Set<Integer> floor = new HashSet<>();
        for(int i=0;i<list.size();i++ ){
            floor.add(list.get(i).getFloor());
        }
        List<Integer> floorList = new ArrayList<>(floor);
        //根据楼层查看异常的传感器
        for(int i=0;i<floorList.size();i++){
            Map map = new HashMap();
            String status ="2";
            map.put("floor",floorList.get(i));
            for(int j=0;j<list.size();j++){
                DeviceSensor deviceSensor=list.get(j);
                //判断当前楼层报一个火警就是报警，没有报警，有故障就是故障，都没有是正常
                if(floorList.get(i)==deviceSensor.getFloor()){
                    if(deviceSensor.getStatus().equals("1")){//火警
                        status ="1";
                        map.put("status",status);
                        break;
                    }else if(deviceSensor.getStatus().equals("0")||deviceSensor.getStatus().equals("4")) {
                        status ="0";
                        map.put("status",status);
                    }
                }
            }
            //正常不返回
            if(!status.equals("2")){
                result.add(map);
            }
        }
        responseResult.setData(result);
        return responseResult;
    }

    @RequestMapping(value = "/getAllHardwareFacilitiesList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("列表模式，查询所有的消火栓信息，传感器的信息")
    public TableResultResponse getAllHardwareFacilitiesList(Integer channelId, String page, String limit) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "15";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dsBiz.getAllHardwareFacilitiesList(query,channelId);
    }

    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/getById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器id查询传感器")
    public ObjectRestResponse getById(@RequestParam Long id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(id==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else{
            DeviceSensor deviceSensor = dsBiz.getById(id);
            if(deviceSensor==null){
                log.info("id no exist :"+id);
            }else{
                responseResult.setData(deviceSensor);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/getStatusCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询建筑物和楼层传感器未启用,未标记数量")
    public ObjectRestResponse<DeviceSensor> getStatusCount(Integer buildId,Integer floor,Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        Integer notEnabledCount = dsBiz.selectStatusCount(buildId,floor,channelId,"3");//未启用
        Integer unlabeledCount = dsBiz.selectNotsignCount(buildId,floor,channelId,null);//未标记
        Integer allNotEnabledCount = dsBiz.selectStatusCount(buildId,null,channelId,"3");//未启用
        Map<String,Object> map = new HashMap<>();
        map.put("notEnabledCount",notEnabledCount);
        map.put("unlabeledCount",unlabeledCount);
        map.put("allNotEnabledCount",allNotEnabledCount);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getNotEnabledSensorList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑id，查询未启用传感器列表")
    public TableResultResponse getNotEnabledSensorList(Integer channelId,@RequestParam Integer buildingId,Integer floor,String page, String limit) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "15";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dsBiz.getNotEnabledSensorList(query,channelId,buildingId,floor);
    }

    @RequestMapping(value = "/getNotEnabledSensorListByHydrantId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据消火栓id，查询未启用传感器列表")
    public TableResultResponse getNotEnabledSensorListByHydrantId(Integer channelId,@RequestParam Integer hydrantId,String page, String limit) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "15";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dsBiz.getNotEnabledSensorListByHydrantId(query,channelId,hydrantId);
    }

    @RequestMapping(value = "/getChannelName",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所属系统下传感器")
    public ObjectRestResponse getChannelName(Integer buildId,String status){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List<Map<String,Object>> list = iUserFeign.getAll();
        List resultList = new ArrayList();
        for(Map channelMap:list){
            Map map = new HashMap();
            Integer channelId = Integer.valueOf(channelMap.get("id").toString());
            Integer count = null;
            if("0".equals(status)){
                Integer faultCount =null;
                Integer offCount =null;
                //消防主机
                if("11".equals(channelMap.get("id").toString())){
                    faultCount = dfmsBiz.getStatusCount(buildId,null,channelId,status);
                    offCount = dfmsBiz.getStatusCount(buildId,null,channelId,"4");
                }else{
                    faultCount = dsBiz.getStatusCount(buildId,null,channelId,status);
                    offCount = dsBiz.getStatusCount(buildId,null,channelId,"4");
                }
                count = faultCount+offCount;
            }else{
                //消防主机
                if("11".equals(channelMap.get("id").toString())){
                    count = dfmsBiz.getStatusCount(buildId,null,channelId,status);
                }else{
                    count = dsBiz.getStatusCount(buildId,null,channelId,status);
                }
            }
            if(count!=0){
                map.put("channelName",channelMap.get("channelName"));
                map.put("channelId",channelId);
                map.put("count",count);
                resultList.add(map);
            }
        }
        responseResult.setData(resultList);
        return responseResult;
    }

/*    @RequestMapping(value = "/getUnlabeledSensorList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑id,楼层，查询未标记传感器列表")
    public TableResultResponse getUnlabeledSensorList(Integer channelId,@RequestParam Integer buildingId,@RequestParam Integer floor,String page, String limit) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "15";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dsBiz.getUnlabeledSensorList(query,channelId,buildingId,floor);
    }*/

    @RequestMapping(value = "/getByHydrantIdGetSensor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据消火栓id查询传感器")
    public ObjectRestResponse getByHydrantIdGetSensor(Integer channelId,@RequestParam Integer hydrantId) {
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List resultList = new ArrayList();
        List<DeviceSensor> list = dsBiz.getByHydrantId(hydrantId,channelId);
        for(DeviceSensor deviceSensor:list){
           if(!"3".equals(deviceSensor.getStatus())){
               Map map = new HashMap();
               DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());
               map.put("equipmentType",deviceSensorType.getEquipmentType());
               map.put("sensorNo",deviceSensor.getSensorNo());
               if("4".equals(deviceSensor.getStatus())){
                   map.put("time","设备离线，"+DateUtil.getHandletime(new Date(),deviceSensor.getStatusTime())+"无反馈");
               }else{
                   map.put("time",deviceSensor.getStatusTime());
               }
               resultList.add(map);
           }
        }
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/queryByIds",method = RequestMethod.POST)
    @ApiOperation("根据传感器ID批量查询传感器信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="pageNo",value = "页码",paramType = "query"),
            @ApiImplicitParam(name="limit",value = "每页数量",paramType = "query"),
            @ApiImplicitParam(name="ids",value = "传感器ID",paramType = "query"),
            @ApiImplicitParam(name="manufacturer",value = "厂商",paramType = "query"),
            @ApiImplicitParam(name="equipmentType",value = "序列",paramType = "query"),
            @ApiImplicitParam(name="model",value = "型号",paramType = "query"),
            @ApiImplicitParam(name="sensorNo",value = "传感器编号",paramType = "query"),
    })
    public TableResultResponse queryByIds(@RequestParam(defaultValue = "1") Integer pageNo,
                                          @RequestParam(defaultValue = "15") Integer limit,
                                          @RequestParam String queryStr
                                          ){
        JSONObject json = JSONObject.parseObject(queryStr);
        String ids = json.getString("ids");
        String manufacturer = json.getString("manufacturer");
        String equipmentType = json.getString("equipmentType");
        String model = json.getString("model");
        String sensorNo = json.getString("sensorNo");
        Long buildingId = json.getLong("buildingId");
        String excludeIds = json.getString("excludeIds");
        Integer channelId = json.getInteger("channelId");
        String code = json.getString("code");
        if(StringUtils.isNotBlank(code)){
            if(code.length()!=6){
                code = null;
            }else {
                if("00".equals(code.substring(2,4))){
                    code = code.substring(0,2)+"____";
                }else if("00".equals(code.substring(4))){
                    code = code.substring(0,4)+"__";
                }
            }
        }

        Page page =  PageHelper.startPage(pageNo,limit);
        List<Map<String,Object>> list = dsBiz.queryByIds(ids,manufacturer,equipmentType,model,sensorNo,buildingId,excludeIds,channelId,code);
        return new TableResultResponse(page.getTotal(),list);
    }

    @RequestMapping(value = "/queryOutdoorSensor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("室外传感器列表")
    public TableResultResponse<DeviceSensor> queryOutdoorSensor(@RequestParam(defaultValue = "1") Integer pageNo,
                                                         @RequestParam(defaultValue = "15") Integer limit,@RequestParam String queryStr) {

        JSONObject json = JSONObject.parseObject(queryStr);
        String ids = json.getString("ids");
        String manufacturer = json.getString("manufacturer");
        String equipmentType = json.getString("equipmentType");
        String model = json.getString("model");
        String sensorNo = json.getString("sensorNo");
        String code = json.getString("code");
        String excludeIds = json.getString("excludeIds");
        Integer channelId = json.getInteger("channelId");

        List<Integer> hids = null;
        //选择地区时传地区编码
        if(StringUtils.isNotBlank(code)){
            if(code.length()!=6){
                hids = Lists.newArrayList(-1);
            }else {
                if("00".equals(code.substring(2,4))){
                    code = code.substring(0,2)+"____";
                }else if("00".equals(code.substring(4))){
                    code = code.substring(0,4)+"__";
                }
                hids = dhfBiz.selectByZxqzResultIds(code,null);
            }
        }
        if(hids != null && hids.isEmpty()){
            return new TableResultResponse<DeviceSensor>();
        }
        Page page =  PageHelper.startPage(pageNo,limit);
        List<Map<String,Object>> list = dsBiz.queryOutdoorSensorByIds(ids,manufacturer,equipmentType,model,sensorNo,excludeIds,channelId,hids);
        return new TableResultResponse(page.getTotal(),list);
    }

    @RequestMapping(value = "/queryNestedSensor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("设备组传感器列表")
    public TableResultResponse<DeviceSensor> queryNestedSensor(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                @RequestParam(defaultValue = "15") Integer limit,@RequestParam String queryStr) {

        JSONObject json = JSONObject.parseObject(queryStr);
        String ids = json.getString("ids");
        String manufacturer = json.getString("manufacturer");
        String equipmentType = json.getString("equipmentType");
        String model = json.getString("model");
        String sensorNo = json.getString("sensorNo");
        Long groupId = json.getLong("groupId");
        String excludeIds = json.getString("excludeIds");
        Integer channelId = json.getInteger("channelId");
        Page page =  PageHelper.startPage(pageNo,limit);
        List<Map<String,Object>> list = dsBiz.queryNestedSensorByIds(ids,manufacturer,equipmentType,model,sensorNo,excludeIds,channelId,groupId);
        return new TableResultResponse(page.getTotal(),list);
    }




}