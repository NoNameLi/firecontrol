package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.RandomColor;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;

import java.util.*;

@RestController
@RequestMapping("deviceSensorSeries")
@CheckClientToken
@CheckUserToken
public class DeviceSensorSeriesController extends BaseController<DeviceSensorSeriesBiz,DeviceSensorSeries,Integer> {

    @Autowired
    private DeviceSensorSeriesBiz dssBiz;
    @Autowired
    private DeviceSensorMpRelationBiz dsmsBiz;
    @Autowired
    private DeviceSensorBiz dsBiz;
    @Autowired
    private DeviceSensorTypeBiz dstBiz;
    @Autowired
    private DeviceMeasuringPointBiz dmpBiz;
    @Autowired
    private DeviceSensorManufacturerBiz dsmfBiz;

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加传感器系列，关联添加测点1.4")
    public ObjectRestResponse<DeviceSensorSeries> add(@RequestBody Map<String,String> params){
        DeviceSensorSeries entity =new DeviceSensorSeries();
        entity.setSensorType(params.get("sensorType"));
        String measuringPointIds = params.get("measuringPointIds");
        ObjectRestResponse<DeviceSensorSeries> responseResult =  new ObjectRestResponse<DeviceSensorSeries>();
        if(entity!=null){
            String sensorType = entity.getSensorType();
            if(sensorType!=null&&!"".equals(sensorType)&&measuringPointIds!=null&&!"".equals(measuringPointIds)){
                //传感器系列重复
                if(dssBiz.selectByCount(sensorType)>0){
                    throw  new RuntimeException(Constants.SENSOR_TYPE_REPEAT);
                }else {
                    //添加传感器系列表
                    //随机获取颜色
                    entity.setColor(RandomColor.getColor());
                    entity.setId(null);
                    baseBiz.insertSelective(entity);
                    //添加传感器与测点关联表
                    dsmsBiz.insertBatch(entity.getId(),measuringPointIds);
                }
            }
        }
        return responseResult;
    }

 /*   @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加传感器系列，关联添加测点1.5")
    public ObjectRestResponse<DeviceSensorSeries> add(@RequestBody Map<String,Object> params){
        //传感器类型
        String measuringPointIds = (String) params.get("measuringPointIds");
        DeviceSensorManufacturer deviceSensorManufacturer = dsmfBiz.selectById((Integer) params.get("manufacturerId"));
        if(deviceSensorManufacturer == null){
            throw new RuntimeException("厂商不存在");
        }
        DeviceSensorType deviceSensorType = new DeviceSensorType();
        deviceSensorType.setManufacturerId(deviceSensorManufacturer.getId());
        deviceSensorType.setManufacturer(deviceSensorManufacturer.getSensorManufacturer());
        deviceSensorType.setEquipmentType((String) params.get("sensorType"));
        deviceSensorType.setModel((String) params.get("model"));
        deviceSensorType.setChannelId((Integer) params.get("channelId"));
        dstBiz.insertSelective(deviceSensorType);
        //传感器系列
        DeviceSensorSeries entity =new DeviceSensorSeries();
        entity.setSenesorTypeId(deviceSensorType.getId());
        entity.setSensorType((String) params.get("sensorType"));
        //随机获取颜色
        entity.setColor(RandomColor.getColor());
        entity.setId(null);
        baseBiz.insertSelective(entity);
        //添加传感器与测点关联表
        dsmsBiz.insertBatch(entity.getId(),measuringPointIds);
        return new ObjectRestResponse<>();
    }
*/
  /*  @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改传感器系列，关联修改添加测点1.5")
    public ObjectRestResponse<DeviceSensorSeries> update(@RequestBody Map<String,Object> params){
        //修改传感器类型
        DeviceSensorManufacturer deviceSensorManufacturer = dsmfBiz.selectById((Integer) params.get("manufacturerId"));
        if(deviceSensorManufacturer == null){
            throw new RuntimeException("厂商不存在");
        }
        DeviceSensorType deviceSensorType = new DeviceSensorType();
        deviceSensorType.setId((Integer) params.get("id"));
        deviceSensorType.setManufacturerId(deviceSensorManufacturer.getId());
        deviceSensorType.setManufacturer(deviceSensorManufacturer.getSensorManufacturer());
        deviceSensorType.setEquipmentType((String) params.get("sensorType"));
        deviceSensorType.setModel((String) params.get("model"));
        deviceSensorType.setChannelId((Integer) params.get("channelId"));
        dstBiz.updateSelectiveById(deviceSensorType);

        DeviceSensorSeries entity =new DeviceSensorSeries();
        entity.setId((Integer) params.get("seriesId"));
        entity.setSensorType((String) params.get("sensorType"));
        String measuringPointIds = (String)params.get("measuringPointIds");
        //修改传感器系列表

        baseBiz.updateSelectiveById(entity);
        String[] mpIds=measuringPointIds.split(",");
        List<Integer> mpIdLists_1=new ArrayList<>();
        for(int i=0;i<mpIds.length;i++) {
            mpIdLists_1.add(Integer.parseInt(mpIds[i]));
        }
        List<Integer> mpIdLists_2 = dsmsBiz.selectBySensorSeriesId(entity.getId());
        List<Integer> add = new ArrayList<Integer>(mpIdLists_1);//构建list1的副本
        add.removeAll(mpIdLists_2);// 去除相同元素
        List<Integer> delete = new ArrayList<Integer>(mpIdLists_2);//构建list2的副本
        delete.removeAll(mpIdLists_1);// 去除相同元素
        //批量添加，删除传感器关联
        dsmsBiz.updateBatch(entity.getId(),add,delete);
        return  new ObjectRestResponse<DeviceSensorSeries>();
    }
*/
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改传感器系列，关联修改添加测点1.4")
    public ObjectRestResponse<DeviceSensorSeries> update(@RequestBody Map<String,Object> params){
        DeviceSensorSeries entity =new DeviceSensorSeries();
        entity.setId((Integer) params.get("id"));
        entity.setSensorType((String) params.get("sensorType"));
        String measuringPointIds = (String)params.get("measuringPointIds");
        ObjectRestResponse<DeviceSensorSeries> responseResult =  new ObjectRestResponse<DeviceSensorSeries>();
        if(entity!=null){
            String sensorType = entity.getSensorType();
            DeviceSensorSeries deviceSensorSeries=dssBiz.selectById(entity.getId());
            //对象不存在
            if(deviceSensorSeries==null){
                throw  new RuntimeException(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
            }
            if(sensorType!=null&&!"".equals(sensorType)&&measuringPointIds!=null&&!"".equals(measuringPointIds)){
                //传感器类型重复
                if(!sensorType.equals(deviceSensorSeries.getSensorType())&&dssBiz.selectByCount(sensorType)>0){
                    throw new RuntimeException(Constants.SENSOR_TYPE_REPEAT);
                }else {
                    //修改传感器系列表
                    baseBiz.updateSelectiveById(entity);
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
                }
            }
        }
        return responseResult;
    }



/*    @ApiOperation("分页获取数据1.5")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Map<String,Object>> list(@RequestParam String page,@RequestParam String limit,Integer channelId,Integer manufacturerId,String equipmentType,String model){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return baseBiz.selectPageList( query,channelId, manufacturer,equipmentType,model);
    }*/



    @ApiOperation("分页获取数据1.4")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Map<String,Object>> list(@RequestParam String page,@RequestParam String limit,String typeName){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return baseBiz.selectPageList(query,typeName);
    }




    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除传感器系列假删除")
    public ObjectRestResponse<DeviceSensorSeries> remove(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(id!=null){
            //查询传感器系列绑定了多少传感器
            List<String> tenantIds = dstBiz.selectBySensorSeriesIdResultTenantId(id);
            if(tenantIds!=null&&tenantIds.size()>0){
                responseResult.setData(tenantIds);
                throw  new RuntimeException( Constants.DELETE_ERROR);
            }else {
                //删除传感器系列表
                DeviceSensorSeries deviceSensorSeries = new DeviceSensorSeries();
                deviceSensorSeries.setId(id);
                deviceSensorSeries.setDelFlag("1");
                baseBiz.updateSelectiveById(deviceSensorSeries);
                //删除传感器关联测点表的数据
                dsmsBiz.deleteBySSIds(id);
            }
        }
        return responseResult;
    }


/*    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除传感器系列假删除1.5")
    public ObjectRestResponse<DeviceSensorSeries> remove(@RequestParam Integer id){
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
                DeviceSensorSeries deviceSensorSeries = baseBiz.selectBySensorTypeId(id);
                deviceSensorSeries.setDelFlag("1");
                baseBiz.updateSelectiveById(deviceSensorSeries);
                //删除传感器关联测点表的数据
                dsmsBiz.deleteBySSIds(deviceSensorSeries.getId());
            }
        }
        return responseResult;
    }*/


    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除传感器前查询有多少子站使用了该传感器的系列1.4")
    public ObjectRestResponse removeQuery(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(id!=null){
           List<String> tenantIds = dstBiz.selectBySensorSeriesIdResultTenantId(id);

            responseResult.setData(tenantIds);
        }
        return responseResult;
    }


  /*  @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除传感器前查询有多少子站使用了该传感器的系列1.5")
    public ObjectRestResponse removeQuery(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(id!=null){
//            List<String> tenantIds = dstBiz.selectBySensorSeriesIdResultTenantId(id);
              List<String> tenantIds = dsBiz.deleteSensorTypeQuery(id);
            responseResult.setData(tenantIds);
        }
        return responseResult;
    }*/

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象")
    public ObjectRestResponse get(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        DeviceSensorSeries deviceSensorSeries = baseBiz.selectById(id);
        List<Integer> deviceMeasuringPoints = dmpBiz.selectBySensorSeriesIdResultId(id);
        List<Map> allIds  =dmpBiz.getAll();
        for(int i=0;i<allIds.size();i++){
            Map map=allIds.get(i);
            if(deviceMeasuringPoints.contains(map.get("id"))){
                map.put("checked",true);
            }else {
                map.put("checked",false);
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("sensorType",deviceSensorSeries);
        map.put("measuringPoint",allIds);
        responseResult.data(map);
        return responseResult;
    }



  /*  @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象1.5")
    public ObjectRestResponse get(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        DeviceSensorType deviceSensorType = dstBiz.selectById(id);
        DeviceSensorSeries deviceSensorSeries = baseBiz.selectBySensorTypeId(deviceSensorType.getId());
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
        Map<String,Object> map = new HashMap<>();
        map.put("sensorType",deviceSensorType);
        map.put("measuringPoint",allIds);
        responseResult.data(map);
        return responseResult;
    }
*/



}