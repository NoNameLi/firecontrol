package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.RandomColor;
import cn.turing.firecontrol.device.util.TrimUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;

import java.util.*;

@RestController
@RequestMapping("deviceSensorType")
@CheckClientToken
@CheckUserToken
@Api(tags = "传感器类型模块")
public class DeviceSensorTypeController extends BaseController<DeviceSensorTypeBiz,DeviceSensorType,Integer> {

    @Autowired
    protected DeviceSensorTypeBiz dstBiz;
    @Autowired
    protected DeviceSensorBiz dsBiz;
    @Autowired
    private DeviceSensorSeriesBiz dssBiz;
    @Autowired
    private DeviceSensorMpRelationBiz dsmsBiz;
    @Autowired
    private IUserFeign iUserFeign;
    @Autowired
    private DeviceBuildingBiz dbBiz;
    @Autowired
    private DeviceSensorManufacturerBiz dsmfBiz;
    @Autowired
    private DeviceHardwareFacilitiesBiz dhfBiz;
    @Autowired
    private DeviceMeasuringPointBiz dmpBiz;


    @ApiOperation("分页获取数据1.5")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Map<String,Object>> list(@RequestParam String page,@RequestParam String limit,Integer channelId,String manufacturer,String equipmentType,String model){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return dssBiz.selectPageList( query,channelId, manufacturer,equipmentType,model);
    }

    @ApiOperation("分页获取数据1.5")
    @RequestMapping(value = "/pageLists",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Map<String,Object>> lists(@RequestParam String page,@RequestParam String limit,String manufacturer,String equipmentType,String model){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return dstBiz.selectPageList(query,manufacturer,equipmentType,model);
    }


    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除传感器前查询有多少子站使用了该传感器的系列1.5")
    public ObjectRestResponse removeQuery(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(id!=null){
            List<String> tenantIds = dsBiz.deleteSensorTypeQuery(id);
            responseResult.setData(tenantIds);
        }
        return responseResult;
    }


    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除传感器系列假删除1.5")
    public ObjectRestResponse remove(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(id!=null){
            //查询传感器系列绑定了多少传感器
            List<String> tenantIds = dsBiz.deleteSensorTypeQuery(id);
            if(tenantIds!=null&&tenantIds.size()>0){
                responseResult.setData(tenantIds);
                throw  new RuntimeException( Constants.DELETE_ERROR);
            }else {
                //删除传感器类型表
                DeviceSensorType deviceSensorType = new DeviceSensorType();
                deviceSensorType.setId(id);
                deviceSensorType.setDelFlag("1");
                dstBiz.updateSelectiveById(deviceSensorType);
                //根据传感器类型id获取传感器系列id
                DeviceSensorSeries deviceSensorSeries = dssBiz.selectBySensorTypeId(id);
                deviceSensorSeries.setDelFlag("1");
                dssBiz.updateSelectiveById(deviceSensorSeries);
                //删除传感器关联测点表的数据
                dsmsBiz.deleteBySSIds(deviceSensorSeries.getId());
            }
        }
        return responseResult;
    }




    @RequestMapping(value = "/selectType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据厂商选择对应的类型和相对应的型号1.5")
    public ObjectRestResponse  selectByType1(Long id,Integer buildingId,@RequestParam Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map result = new HashMap();
        List<Map<String,Object>> lists = new ArrayList<>();
        List<String> manufacturer =  dstBiz.getManufacturerChannelId(channelId);
        for(int i=0;i<manufacturer.size();i++){
            String manufacturerValue = manufacturer.get(i);
            Map<String,Object> map = new HashMap<>();
            map.put("value",manufacturerValue);
            map.put("label",manufacturerValue);
            List<Map<String,Object>> lists_2 = new ArrayList<>();
            List<String> equipmentType = dstBiz.getEquipmentTypeByManufacturerChannelId(manufacturerValue,channelId);
            for(int j=0;j<equipmentType.size();j++){
                String equipmentTypeValue = equipmentType.get(j);
                Map<String,Object> map_2 = new HashMap<>();
                map_2.put("value",equipmentTypeValue);
                map_2.put("label",equipmentTypeValue);
                List<Map<String,Object>> lists_3 = new ArrayList<>();
                List<String> model = dstBiz.getModelByManufacturerAndTypeChannelId(manufacturerValue,equipmentTypeValue,channelId);
                for(int k=0;k<model.size();k++){
                    String modeValue = model.get(k);
                    Map<String,Object> map_3 = new HashMap<>();
                    map_3.put("value",modeValue);
                    map_3.put("label",modeValue);
                    lists_3.add(map_3);
                }
                map_2.put("children",lists_3);
                lists_2.add(map_2);
            }
            map.put("children",lists_2);
            lists.add(map);
        }
        result.put("options",lists);
        if(id!=null){
            DeviceSensor entity = dsBiz.selectById(id);
            if(entity!=null){
                DeviceSensorType deviceSensorType = baseBiz.selectById(entity.getSensorTypeId());
                String manufacturerValue = deviceSensorType.getManufacturer();
                String equipmentTypeValue = deviceSensorType.getEquipmentType();
                String modelValue = deviceSensorType.getModel();
                buildingId = entity.getBuildingId();
                List<String> selectedOptions = new ArrayList<>();
                selectedOptions.add(manufacturerValue);
                selectedOptions.add(equipmentTypeValue);
                selectedOptions.add(modelValue);
                result.put("selectedOptions",selectedOptions);
                result.put("deviceSensor",entity);
            }
        }
        //获取楼层的下拉框
        if(buildingId != null){
            DeviceBuilding deviceBuilding = dbBiz.selectById(buildingId);
            if(deviceBuilding!=null){
                int max = deviceBuilding.getUpFloor();
                int min = deviceBuilding.getUnderFloor();
                List<Integer> count = new ArrayList<>();
                for(int i=max;i>=-min;i--){
                    if(i!=0){
                        count.add(i);
                    }
                }
                result.put("floor",count);
            }
        }
        responseResult.setData(result);
        return responseResult;
    }


    @RequestMapping(value = "/selectTypeApp",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据厂商选择对应的类型和相对应的型号1.5")
    public ObjectRestResponse  selectByTypeApp(Long id,@RequestParam Integer buildingId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map result = new HashMap();
        List<Map<String,Object>> lists = new ArrayList<>();
        List<String> manufacturer =  dstBiz.getManufacturer();
        for(int i=0;i<manufacturer.size();i++){
            String manufacturerValue = manufacturer.get(i);
            Map<String,Object> map = new HashMap<>();
            map.put("value",manufacturerValue);
            map.put("label",manufacturerValue);
            List<Map<String,Object>> lists_2 = new ArrayList<>();
            List<String> equipmentType = dstBiz.getEquipmentTypeByManufacturer(manufacturerValue);
            for(int j=0;j<equipmentType.size();j++){
                String equipmentTypeValue = equipmentType.get(j);
                Map<String,Object> map_2 = new HashMap<>();
                map_2.put("value",equipmentTypeValue);
                map_2.put("label",equipmentTypeValue);
                List<Map<String,Object>> lists_3 = new ArrayList<>();
                List<String> model = dstBiz.getModelByManufacturerAndType(manufacturerValue,equipmentTypeValue);
                for(int k=0;k<model.size();k++){
                    String modeValue = model.get(k);
                    Map<String,Object> map_3 = new HashMap<>();
                    map_3.put("value",modeValue);
                    map_3.put("label",modeValue);
                    lists_3.add(map_3);
                }
                map_2.put("children",lists_3);
                lists_2.add(map_2);
            }
            map.put("children",lists_2);
            lists.add(map);
        }
        result.put("options",lists);
        if(id!=null){
            DeviceSensor entity = dsBiz.selectById(id);
            if(entity!=null){
                DeviceSensorType deviceSensorType = baseBiz.selectById(entity.getSensorTypeId());
                String manufacturerValue = deviceSensorType.getManufacturer();
                String equipmentTypeValue = deviceSensorType.getEquipmentType();
                String modelValue = deviceSensorType.getModel();
                List<String> selectedOptions = new ArrayList<>();
                selectedOptions.add(manufacturerValue);
                selectedOptions.add(equipmentTypeValue);
                selectedOptions.add(modelValue);
                result.put("selectedOptions",selectedOptions);
                result.put("deviceSensor",entity);
            }
        }
        //获取楼层的下拉框
        DeviceBuilding deviceBuilding = dbBiz.selectById(buildingId);
        if(deviceBuilding!=null){
            int max = deviceBuilding.getUpFloor();
            int min = deviceBuilding.getUnderFloor();
            List<Integer> count = new ArrayList<>();
            for(int i=max;i>=-min;i--){
                if(i!=0){
                    count.add(i);
                }
            }
            result.put("floor",count);
        }
        responseResult.setData(result);
        return responseResult;
    }


    @RequestMapping(value = "/selectTypeOutdoor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("室外传感器的添加编辑前查询1.5")
    public ObjectRestResponse  selectByTypeOutdoor(Long id,@RequestParam Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map result = new HashMap();
        List<Map<String,Object>> lists = new ArrayList<>();
        List<String> manufacturer = dstBiz.getManufacturerChannelId(channelId);
        for(int i=0;i<manufacturer.size();i++){
            String manufacturerValue = manufacturer.get(i);
            Map<String,Object> map = new HashMap<>();
            map.put("value",manufacturerValue);
            map.put("label",manufacturerValue);
            List<Map<String,Object>> lists_2 = new ArrayList<>();
            List<String> equipmentType = dstBiz.getEquipmentTypeByManufacturerChannelId(manufacturerValue,channelId);
            for(int j=0;j<equipmentType.size();j++){
                String equipmentTypeValue = equipmentType.get(j);
                Map<String,Object> map_2 = new HashMap<>();
                map_2.put("value",equipmentTypeValue);
                map_2.put("label",equipmentTypeValue);
                List<Map<String,Object>> lists_3 = new ArrayList<>();
                List<String> model = dstBiz.getModelByManufacturerAndTypeChannelId(manufacturerValue,equipmentTypeValue,channelId);
                for(int k=0;k<model.size();k++){
                    String modeValue = model.get(k);
                    Map<String,Object> map_3 = new HashMap<>();
                    map_3.put("value",modeValue);
                    map_3.put("label",modeValue);
                    lists_3.add(map_3);
                }
                map_2.put("children",lists_3);
                lists_2.add(map_2);
            }
            map.put("children",lists_2);
            lists.add(map);
        }
        result.put("options",lists);
        if(id!=null){
            DeviceSensor entity = dsBiz.selectById(id);
            if(entity!=null){
                DeviceSensorType deviceSensorType = baseBiz.selectById(entity.getSensorTypeId());
                DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.getById(entity.getHydrantId());
                String manufacturerValue = deviceSensorType.getManufacturer();
                String equipmentTypeValue = deviceSensorType.getEquipmentType();
                String modelValue = deviceSensorType.getModel();
                List<String> selectedOptions = new ArrayList<>();
                selectedOptions.add(manufacturerValue);
                selectedOptions.add(equipmentTypeValue);
                selectedOptions.add(modelValue);
                result.put("selectedOptions",selectedOptions);
                result.put("deviceSensor",entity);
                result.put("hydrantType",deviceHardwareFacilities.getFacilityType());
            }
        }
        //获取所有设施类型  目前只要 0=室外消火栓
        JSONArray jsonArray = new JSONArray();
        JSONObject temp = new JSONObject();
        temp.put("name","室外消火栓");
        temp.put("facilityType","0");
        jsonArray.add(temp);

        result.put("facility",jsonArray);
        responseResult.setData(result);
        return responseResult;
    }


    @RequestMapping(value = "/getSelected",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("前端下拉框需要展示的数据")
    public ObjectRestResponse  getSelected(){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //获取所有的厂商，系列
        List<String> equipmentType = dstBiz.getEquipmentType();
        List<String> manufacturer = dstBiz.getManufacturer();
        //测点
        List<Map> measuringPoints = dmpBiz.getAll();

        List<Map<String,Object>> selectChannel = iUserFeign.getAll();
        List<String> selectManufacturer = dsmfBiz.selectedType();

        Map<String,Object> map = new HashMap<>();
        LinkedList<String> tempEquipmentType = new LinkedList<>();
        LinkedList<String> tempManufacturer = new LinkedList<>();
        //用户自己添加的系列
        if(equipmentType!=null&&equipmentType.size()>0){
            if(!"".equals(equipmentType.get(0))){
                tempEquipmentType.addAll(equipmentType);
            }
        }
        tempEquipmentType.addFirst("全部");
        map.put("equipmentType",tempEquipmentType);
        //用户自己添加的厂商
        if(manufacturer!=null&&manufacturer.size()>0){
            if(!"".equals(manufacturer.get(0))){
                tempManufacturer.addAll(manufacturer);
            }
        }
        tempManufacturer.addFirst("全部");
        map.put("manufacturer",tempManufacturer);
        //超级管理员添加的厂商
        if(selectManufacturer!=null&&selectManufacturer.size()>0){
            map.put("selectManufacturer",selectManufacturer);
        }
        //所有的栏目
        map.put("selectChannel",selectChannel);
        map.put("measuringPoints",measuringPoints);

        //已经添加的栏目
        List<Integer> addChannelIds = dstBiz.getAddChannelId();
        List<Map> channel = new ArrayList<>();
        for(int i=0;i<selectChannel.size();i++){
            if(addChannelIds.contains((Integer)selectChannel.get(i).get("id"))){
                channel.add(selectChannel.get(i));
            }
        }
        map.put("channel",channel);
        responseResult.setData(map);
        return responseResult;
    }


    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加传感器类型，关联添加测点1.5")
    public ObjectRestResponse<DeviceSensorSeries> add(@RequestBody Map<String,Object> params){
        //传感器类型
        String measuringPointIds = (String) params.get("measuringPointIds");
        DeviceSensorManufacturer deviceSensorManufacturer = dsmfBiz.selecteByName((String) params.get("manufacturer"));
        if(deviceSensorManufacturer == null){
            throw new RuntimeException("厂商不存在");
        }
        DeviceSensorType deviceSensorType = new DeviceSensorType();
        deviceSensorType.setManufacturerId(deviceSensorManufacturer.getId());
        deviceSensorType.setManufacturer(deviceSensorManufacturer.getSensorManufacturer());
        deviceSensorType.setEquipmentType((String) params.get("sensorType"));
        deviceSensorType.setModel((String) params.get("model"));
        deviceSensorType.setChannelId((Integer) params.get("channelId"));
        TrimUtil.trimObject(deviceSensorType);
        TrimUtil.trimNull(deviceSensorType.getEquipmentType(),deviceSensorType.getModel());
        //查询类型列表
        List<DeviceSensorType> list = dstBiz.selectByType(deviceSensorType.getManufacturer(),deviceSensorType.getModel(),deviceSensorType.getEquipmentType());
        //重复
        if(list.size()>0){
            throw new RuntimeException(Constants.SENSOR_REPEAT);
        }
        ValidatorUtils.validateEntity(deviceSensorType);
        dstBiz.insertSelective(deviceSensorType);
        //传感器系列
        DeviceSensorSeries entity =new DeviceSensorSeries();
        entity.setSenesorTypeId(deviceSensorType.getId());
        entity.setSensorType((String) params.get("sensorType"));
        //随机获取颜色
        entity.setColor(RandomColor.getColor());
        entity.setId(null);
        TrimUtil.trimObject(entity);
        dssBiz.insertSelective(entity);
        //添加传感器与测点关联表
        dsmsBiz.insertBatch(entity.getId(),measuringPointIds);
        return new ObjectRestResponse<>();
    }


      @RequestMapping(value = "/update",method = RequestMethod.POST)
      @ResponseBody
      @ApiOperation("修改传感器系列，关联修改添加测点1.5")
      public ObjectRestResponse<DeviceSensorSeries> update(@RequestBody Map<String,Object> params){
          //修改传感器类型
          DeviceSensorManufacturer deviceSensorManufacturer = dsmfBiz.selecteByName((String) params.get("manufacturer"));
          if(deviceSensorManufacturer == null){
              throw new RuntimeException("厂商不存在");
          }
          DeviceSensorType deviceSensorType = new DeviceSensorType();
          deviceSensorType.setId((Integer) params.get("id"));
          deviceSensorType.setManufacturerId(deviceSensorManufacturer.getId());
          deviceSensorType.setManufacturer(deviceSensorManufacturer.getSensorManufacturer());
          deviceSensorType.setEquipmentType((String) params.get("sensorType"));
          deviceSensorType.setModel((String) params.get("model"));
          // TODO: 2018/11/12  当没有绑定传感器是才能修改所属系统，当已经绑定传感器不允许修改所属系统
          deviceSensorType.setChannelId((Integer) params.get("channelId"));
          TrimUtil.trimObject(deviceSensorType);
          TrimUtil.trimNull(deviceSensorType.getEquipmentType(),deviceSensorType.getModel());
          DeviceSensorType temp = baseBiz.selectById(deviceSensorType.getId());
          //查询类型列表
          List<DeviceSensorType> list = dstBiz.selectByType(deviceSensorType.getManufacturer(),deviceSensorType.getModel(),deviceSensorType.getEquipmentType());
          if(!(temp.getManufacturer().equalsIgnoreCase(deviceSensorType.getManufacturer())&&temp.getEquipmentType().equalsIgnoreCase(deviceSensorType.getEquipmentType())&&temp.getModel().equalsIgnoreCase(deviceSensorType.getModel()))&&list.size()>0){
              throw new RuntimeException(Constants.SENSOR_REPEAT);
          }
          DeviceSensorSeries deviceSensorSeries = dssBiz.selectBySensorTypeId(deviceSensorType.getId());
          ValidatorUtils.validateEntity(deviceSensorType);
          dstBiz.updateSelectiveById(deviceSensorType);

          DeviceSensorSeries entity =new DeviceSensorSeries();
          entity.setId(deviceSensorSeries.getId());
          entity.setSensorType((String) params.get("sensorType"));
          String measuringPointIds = (String)params.get("measuringPointIds");
          //修改传感器系列表
          TrimUtil.trimObject(entity);
          dssBiz.updateSelectiveById(entity);
          String[] mpIds=measuringPointIds.split(",");
          List<Integer> mpIdLists_1=new ArrayList<>();
          for(int i=0;i<mpIds.length;i++) {
              mpIdLists_1.add(Integer.parseInt(mpIds[i]));
          }
          List<Integer> mpIdLists_2= dsmsBiz.selectBySensorSeriesId(entity.getId());
          List<Integer> add = new ArrayList<Integer>(mpIdLists_1);//构建list1的副本
          add.removeAll(mpIdLists_2);// 去除相同元素
          List<Integer> delete = new ArrayList<Integer>(mpIdLists_2);//构建list2的副本
          delete.removeAll(mpIdLists_1);// 去除相同元素
          //批量添加，删除传感器关联
          dsmsBiz.updateBatch(entity.getId(),add,delete);
          return  new ObjectRestResponse<DeviceSensorSeries>();
      }



    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/selectById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据id查询传感器类型")
    public ObjectRestResponse  selectById(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        DeviceSensorType deviceSensorType = dstBiz.getById(id);
        responseResult.setData(deviceSensorType);
        return responseResult;
    }

    /*@ApiOperation("APP厂商系列型号 三级连动")
    @RequestMapping(value = "/levelAppPname",method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse levelAppPname(Integer channelId){
        //获取所有的厂商
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<String> manufacturerList = dstBiz.getManufacturerChannelId(channelId);
        List<Map<String,Object>> lists = new ArrayList<>();
        int count = -1;
        for(int i = 0;i<manufacturerList.size();i++){
            count = count + 1;
            Map<String,Object> map1 = new HashMap<>();
            String manufacturer = manufacturerList.get(i);
            map1.put("level",1);
            map1.put("name",manufacturer);
            map1.put("pname","");
            map1.put("key",count);
            map1.put("pkey",-1);
            lists.add(map1);
            //根据厂商获取类型
            List<String> equipmentTypeList = dstBiz.getEquipmentTypeByManufacturerChannelId(manufacturer,channelId);
            for(int j=0;j<equipmentTypeList.size();j++){
                Map<String,Object> map2 = new HashMap<>();
                String equipmentType = equipmentTypeList.get(j);
                map2.put("level",2);
                map2.put("name",equipmentType);
                map2.put("pname",manufacturer);
                map2.put("pkey",map1.get("key"));
                count = count + 1;
                map2.put("key",count);
                lists.add(map2);
                //根据厂商类型获取所有的型号
                List<String> modelList = dstBiz.getModelByManufacturerAndTypeChannelId(manufacturer,equipmentType,channelId);
                for(int k=0;k<modelList.size();k++){
                    Map<String,Object> map3 = new HashMap<>();
                    String model = modelList.get(k);
                    map3.put("level",3);
                    map3.put("name",model);
                    map3.put("pname",equipmentType);
                    map3.put("pkey",map2.get("key"));
                    count = count + 1;
                    map3.put("key",count);
                    lists.add(map3);
                }
            }
        }
        responseResult.setData(lists);
        return responseResult;
    }*/



    @RequestMapping(value = "/levelAppPname",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP厂商系列型号 三级连动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channelId",value = "所属系统ID",paramType = "query"),
            @ApiImplicitParam(name = "isReverse",value = "是否反查，默认为否",paramType = "query")
    })
    public ObjectRestResponse levelAppPname(Integer[] channelId,Integer[] notInChannelId){
        List<Map<String,Object>> list = baseBiz.selectTreeByChannelId(channelId,notInChannelId);
        List<Map<String,Object>> res = new ArrayList<>();
        int count = 0,manufacturerKey,equipmentTypeKey;
        Map<String,Object> manufacturer = null,equipmentType = null,model = null,child = null;
        for(int i = 0 ; i < list.size(); i++){
            child = new HashMap<String,Object>();
            manufacturer = list.get(i);
            child.put("level",1);
            child.put("name",manufacturer.get("manufacturer"));
            child.put("pname","");
            manufacturerKey = count++;
            child.put("key",manufacturerKey);
            child.put("pkey",-1);
            res.add(child);
            List<Map<String,Object>> equipmentTypes = (List<Map<String,Object>>)manufacturer.get("equipmentTypes");
            if(equipmentTypes == null || equipmentTypes.isEmpty()){
                continue;
            }
            for(int j=0; j<equipmentTypes.size(); j++){
                child = new HashMap<String,Object>();
                equipmentType = equipmentTypes.get(j);
                child.put("level",2);
                child.put("name",equipmentType.get("equipmentType"));
                child.put("pname",manufacturer.get("manufacturer"));
                equipmentTypeKey = count++;
                child.put("key",equipmentTypeKey);
                child.put("pkey",manufacturerKey);
                res.add(child);
                List<Map<String,Object>> models = (List<Map<String,Object>>)equipmentType.get("models");
                if(models == null || models.isEmpty()){
                    continue;
                }
                for(int k=0; k < models.size() ; k++){
                    child = new HashMap<String,Object>();
                    model = models.get(k);
                    child.put("level",3);
                    child.put("name",model.get("model"));
                    child.put("pname",equipmentType.get("equipmentType"));
                    child.put("key",count++);
                    child.put("pkey",equipmentTypeKey);
                    res.add(child);
                }
            }
        }

        return new ObjectRestResponse().data(res);
    }

    @RequestMapping(value = "/getByType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据设备类型、厂商、型号查询传感器类型")
    public ObjectRestResponse  getByType(@RequestParam String manufacturer,@RequestParam String model,@RequestParam String equipmentType){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<DeviceSensorType> list = dstBiz.selectByType(manufacturer,model,equipmentType);
        if(list.size()==0){
            throw new RuntimeException("传感器类型不存在");
        }
        if(list.size()>1){
            throw new RuntimeException("传感器类型重复");
        }
        if(list.size()==1){
            if("0".equals(list.get(0).getDataAcquisitionCycleUnit())){
                list.get(0).setDataAcquisitionCycleUnit("秒");
            }
            if("1".equals(list.get(0).getDataAcquisitionCycleUnit())){
                list.get(0).setDataAcquisitionCycleUnit("分钟");
            }
            if("2".equals(list.get(0).getDataAcquisitionCycleUnit())){
                list.get(0).setDataAcquisitionCycleUnit("小时");
            }
            if("3".equals(list.get(0).getDataAcquisitionCycleUnit())){
                list.get(0).setDataAcquisitionCycleUnit("天");
            }
            if("4".equals(list.get(0).getDataAcquisitionCycleUnit())){
                list.get(0).setDataAcquisitionCycleUnit("月");
            }
            if("0".equals(list.get(0).getAcquisitionDelayTimeUnit())){
                list.get(0).setAcquisitionDelayTimeUnit("秒");
            }
            if("1".equals(list.get(0).getAcquisitionDelayTimeUnit())){
                list.get(0).setAcquisitionDelayTimeUnit("分钟");
            }
            if("2".equals(list.get(0).getAcquisitionDelayTimeUnit())){
                list.get(0).setAcquisitionDelayTimeUnit("小时");
            }
            if("3".equals(list.get(0).getAcquisitionDelayTimeUnit())){
                list.get(0).setAcquisitionDelayTimeUnit("天");
            }
            if("4".equals(list.get(0).getAcquisitionDelayTimeUnit())){
                list.get(0).setAcquisitionDelayTimeUnit("月");
            }
            if("0".equals(list.get(0).getMaintenanceCycleUnit())){
                list.get(0).setMaintenanceCycleUnit("小时");
            }
            if("1".equals(list.get(0).getMaintenanceCycleUnit())){
                list.get(0).setMaintenanceCycleUnit("天");
            }
            if("2".equals(list.get(0).getMaintenanceCycleUnit())){
                list.get(0).setMaintenanceCycleUnit("年");
            }
            responseResult.setData(list.get(0));
        }
        return responseResult;
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象")
    public ObjectRestResponse get(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        DeviceSensorType deviceSensorType = dstBiz.selectById(id);
        DeviceSensorSeries deviceSensorSeries = dssBiz.selectBySensorTypeId(deviceSensorType.getId());
        List<Integer> deviceMeasuringPoints = dmpBiz.selectBySensorSeriesIdResultId(deviceSensorSeries.getId());

        List<Map> allIds  =dmpBiz.getAll();
        for(int i=0;i<allIds.size();i++){
            Map map=allIds.get(i);
            if(deviceMeasuringPoints.contains(map.get("id"))){
                map.put("checked",true);
            }else {
                map.put("checked",false);
            }
        }
        //判断改传感器类型是否有子站使用
        Boolean channelFlag = false;
        List<String> tenantIds = dsBiz.deleteSensorTypeQuery(id);
        if(tenantIds!=null&&tenantIds.size()>0){
            channelFlag = true;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("sensorType",deviceSensorType);
        map.put("measuringPoint",allIds);
        map.put("channelFlag",channelFlag);
        responseResult.data(map);
        return responseResult;
    }

    @RequestMapping(value = "/selectResultChannel",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询channel")
    public Map<String, Object> selectResultChannel(@RequestParam String manufacturer, @RequestParam String equipmentType, @RequestParam String model) {
        Boolean flag = false;
        Integer channel = baseBiz.selectResultChannel(manufacturer, equipmentType, model);
        JSONObject a = iUserFeign.selectById(channel);
        JSONObject data = a.getJSONObject("data");
        if (data != null) {
            if (StringUtils.isNotBlank(data.getString("id")) && ("5").equals(data.getString("id"))) {
                //true表示消火栓，其它为false
                flag = true;
            }
        }
        Map<String,Object> res = new HashMap<>();
        res.put("status",200);
        res.put("data",flag);
        res.put("channelId",data.getInteger("id"));
        return res;
    }

    @RequestMapping(value = "/selectByTenant",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询租户所属设备的所有类型")
    public ObjectRestResponse<List<DeviceSensorType>> selectByTenant(){
        String tenantId = BaseContextHandler.getTenantID();
        List<DeviceSensorType> list = baseBiz.selectByTenant(tenantId);
        return new ObjectRestResponse().data(list);
    }




}