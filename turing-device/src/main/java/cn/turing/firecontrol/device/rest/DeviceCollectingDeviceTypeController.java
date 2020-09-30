package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.DeviceCollectingDevice;
import cn.turing.firecontrol.device.entity.DeviceCollectingDeviceType;
import cn.turing.firecontrol.device.entity.DeviceCollectingManufacturer;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.ResponseCode;
import cn.turing.firecontrol.device.util.TrimUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;

import java.util.*;

@RestController
@RequestMapping("deviceCollectingDeviceType")
@CheckClientToken
@CheckUserToken
public class DeviceCollectingDeviceTypeController extends BaseController<DeviceCollectingDeviceTypeBiz,DeviceCollectingDeviceType,Integer> {

    @Autowired
    private DeviceCollectingDeviceTypeBiz dcdtBiz;
    @Autowired
    private DeviceCollectingDeviceBiz dcdBiz;
    @Autowired
    private DeviceCollectingDeviceSeriesBiz dcdsBiz;
    @Autowired
    private DeviceCollectingManufacturerBiz dcnfBiz;

    @ApiOperation("分页获取数据")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Map<String,Object>> list(@RequestParam String page, @RequestParam String limit, String manufacturer, String equipmentType, String model){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return dcdtBiz.selectPageList(query,manufacturer,equipmentType,model);
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象")
    public ObjectRestResponse get(@RequestParam Integer id){
        ObjectRestResponse entityObjectRestResponse = new ObjectRestResponse<>();
        DeviceCollectingDeviceType entity = baseBiz.selectById(id);
        entityObjectRestResponse.data(entity);
        return entityObjectRestResponse;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除采集设备类型,判断是否绑定采集设备,假删除")
    public ObjectRestResponse deleteById(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List<String> list = dcdBiz.deleteCollectingQuery(id);
        //判断是否绑定采集设备
        if(list.size()>0){
            throw new RuntimeException(Constants.API_MESSAGE_DELETE_ERROR);
        }else{
            DeviceCollectingDeviceType deviceCollectingDeviceType = new DeviceCollectingDeviceType();
            deviceCollectingDeviceType.setId(id);
            deviceCollectingDeviceType.setDelFlag("1");
            dcdtBiz.updateSelectiveById(deviceCollectingDeviceType);
        }
        return responseResult;
    }

    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除采集设备前查询有多少子站使用了该采集的系列1.5")
    public ObjectRestResponse<List<Integer>> removeQuery(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(id!=null){
            List<String> tenantIds = dcdBiz.deleteCollectingQuery(id);
            responseResult.setData(tenantIds);
        }
        return responseResult;
    }


    @RequestMapping(value = "/selectType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据厂商选择对应的类型和相对应的型号")
    public ObjectRestResponse  selectByType(Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map result = new HashMap();
        List<Map<String,Object>> lists = new ArrayList<>();
        List<String> manufacturer =  dcdtBiz.getManufacturer();
        for(int i=0;i<manufacturer.size();i++){
            String manufacturerValue = manufacturer.get(i);
            Map<String,Object> map = new HashMap<>();
            map.put("value",manufacturerValue);
            map.put("label",manufacturerValue);
            List<Map<String,Object>> lists_2 = new ArrayList<>();
            List<String> equipmentType = dcdtBiz.getEquipmentTypeByMF(manufacturerValue);
            for(int j=0;j<equipmentType.size();j++){
                String equipmentTypeValue = equipmentType.get(j);
                Map<String,Object> map_2 = new HashMap<>();
                map_2.put("value",equipmentTypeValue);
                map_2.put("label",equipmentTypeValue);
                List<Map<String,Object>> lists_3 = new ArrayList<>();
                List<String> model = dcdtBiz.getModel(manufacturerValue,equipmentTypeValue);
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
            DeviceCollectingDevice entity = dcdBiz.selectById(id);
            if(entity!=null){
                DeviceCollectingDeviceType deviceCollectingDeviceType = baseBiz.selectById(entity.getCollectingDevicTypeId());
                String manufacturerValue = deviceCollectingDeviceType.getManufacturer();
                String equipmentTypeValue = deviceCollectingDeviceType.getEquipmentType();
                String modelValue = deviceCollectingDeviceType.getModel();
                List<String> selectedOptions = new ArrayList<>();
                selectedOptions.add(manufacturerValue);
                selectedOptions.add(equipmentTypeValue);
                selectedOptions.add(modelValue);
                result.put("selectedOptions",selectedOptions);
                result.put("deviceSensor",entity);
            }
        }
        responseResult.setData(result);
        return responseResult;
    }

    @RequestMapping(value = "/selectedType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("显示所有的可选择的采集设备")
    public ObjectRestResponse  selectedType(){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<String> lists = dcdsBiz.selectedType();
        responseResult.setData(lists);
        return responseResult;
    }

    @RequestMapping(value = "/getSelected",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有的厂商，系列")
    public ObjectRestResponse  getSelected(){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //获取所有的厂商
//        List<String> series = dcdsBiz.selectedType();
        List<String> selectManufacturer = dcnfBiz.selectedType();
        List<String> equipmentType = dcdtBiz.getEquipmentType();
        List<String> manufacturer = dcdtBiz.getManufacturer();
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
        //超级管理员添加的系列
/*        if(series!=null&&series.size()>0){
            map.put("series",series);
        }*/
        //超级管理员添加的厂商
        if(selectManufacturer!=null&&selectManufacturer.size()>0){
            map.put("selectManufacturer",selectManufacturer);
        }
        responseResult.setData(map);
        return responseResult;
    }


    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加采集设备类型，判断是否重复")
    public ObjectRestResponse insert(@RequestBody DeviceCollectingDeviceType entity){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getEquipmentType(),entity.getModel());
        if(entity==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            //查询类型列表
            List<DeviceCollectingDeviceType> list = dcdtBiz.selectByType(entity.getManufacturer(),entity.getEquipmentType(),entity.getModel());
            if(list.size()>0){
                throw new RuntimeException(Constants.COLLECTION_REPEAT);
            }
            //新增
            entity.setId(null);
            entity.setManufacturerId(dcnfBiz.selectByName(entity.getManufacturer()));
            dcdtBiz.insertSelective(entity);
        }
        return responseResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改采集设备类型，判断是否重复")
    public ObjectRestResponse update(@RequestBody DeviceCollectingDeviceType entity){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getEquipmentType(),entity.getModel());
        if(entity==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            //查询原设备
            DeviceCollectingDeviceType deviceCollectingDeviceType  = baseBiz.selectById(entity.getId());
            if(deviceCollectingDeviceType==null){
                throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
            }
            //查询类型列表
            List<DeviceCollectingDeviceType> list = dcdtBiz.selectByType(entity.getManufacturer(),entity.getEquipmentType(),entity.getModel());
            //判断厂商类型是否强制修改
            if(!(deviceCollectingDeviceType.getEquipmentType().equals(entity.getEquipmentType())&&
                    deviceCollectingDeviceType.getManufacturer().equals(entity.getManufacturer())&&
                    deviceCollectingDeviceType.getModel().equals(entity.getModel()))&&list.size()>0){
                throw new RuntimeException(Constants.COLLECTION_REPEAT);
            }
            entity.setManufacturerId(dcnfBiz.selectByName(entity.getManufacturer()));
            dcdtBiz.updateSelectiveById(entity);
        }
        return responseResult;
    }

}