package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.biz.DeviceCollectingDeviceBiz;
import cn.turing.firecontrol.device.biz.DeviceCollectingDeviceTypeBiz;
import cn.turing.firecontrol.device.entity.DeviceAlarmThreshold;
import cn.turing.firecontrol.device.entity.DeviceCollectingDevice;
import cn.turing.firecontrol.device.entity.DeviceCollectingDeviceType;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.ResponseCode;
import cn.turing.firecontrol.device.util.TrimUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;

import java.util.*;

@RestController
@RequestMapping("deviceCollectingDevice")
@CheckClientToken
@CheckUserToken
public class DeviceCollectingDeviceController extends BaseController<DeviceCollectingDeviceBiz,DeviceCollectingDevice,Integer> {

    @Autowired
    protected DeviceCollectingDeviceBiz dcdBiz;

    @Autowired
    protected DeviceCollectingDeviceTypeBiz dcdtBiz;

    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    public TableResultResponse<Map> list(@RequestParam String page, @RequestParam String limit, DeviceCollectingDevice entity ) {
       //修改bug
        Map<String ,Object> param = new LinkedHashMap<>();
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dcdBiz.selectQuery(query,entity);
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("移除单个对象")
    public ObjectRestResponse<DeviceCollectingDevice> remove(@RequestParam Integer id){
        DeviceCollectingDevice deviceCollectingDevice = new DeviceCollectingDevice();
        deviceCollectingDevice.setId(id);
        deviceCollectingDevice.setDelFlag("1");
        baseBiz.updateSelectiveById(deviceCollectingDevice);
        return new ObjectRestResponse<DeviceCollectingDevice>();
    }


/*    @RequestMapping(value = "/getSelectType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据厂商查询类型，查询型号")
    public ObjectRestResponse  getSelectType(@RequestParam String flag,String str){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //获取所有的厂商，系列
        List<String> equipmentType = dcdBiz.getEquipmentType();
        List<String> manufacturer = dcdBiz.getManufacturer();
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
        responseResult.setData(map);
        return responseResult;
    }*/


    @RequestMapping(value = "/getSelected",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有的厂商，系列,供下拉框选择")
    public ObjectRestResponse  getSelected(){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //获取所有的厂商，系列
        List<String> equipmentType = dcdBiz.getEquipmentType();
        List<String> manufacturer = dcdBiz.getManufacturer();
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
        responseResult.setData(map);
        return responseResult;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加采集设备，绑定对应的采集设备类型")
    public ObjectRestResponse add(@RequestBody  Map<String,Object> params) {
        DeviceCollectingDevice deviceCollectingDevice =new DeviceCollectingDevice();
        deviceCollectingDevice.setNo((String) params.get("no"));
        deviceCollectingDevice.setPositionDescription((String) params.get("positionDescription"));
        deviceCollectingDevice.setGeographicalPositionSign((String) params.get("geographicalPositionSign"));
        List<String> entity =JSON.parseArray((String) params.get("entity"),String.class);
        TrimUtil.trimObject(deviceCollectingDevice);
        TrimUtil.trimNull(deviceCollectingDevice.getNo(),deviceCollectingDevice.getPositionDescription());
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(deviceCollectingDevice==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            String code = deviceCollectingDevice.getNo();
            if(code!=null&&!"".equals(code)){
                //判断代号
                if(dcdBiz.selectByCount(code)>0){
                    throw  new RuntimeException(Constants.DEVICE_CODE_REPEAT);
                }
                //根据厂商、类型、型号查询ID
                List<DeviceCollectingDeviceType> list = dcdtBiz.selectByType(entity.get(0),entity.get(1),entity.get(2));
                if(list.size()==1){
                    for (DeviceCollectingDeviceType deviceCollectingDeviceType:list){
                        deviceCollectingDevice.setCollectingDevicTypeId(deviceCollectingDeviceType.getId());
                    }
                    deviceCollectingDevice.setId(null);
                    //初始状态正常
                    deviceCollectingDevice.setStatus("1");
                    baseBiz.insertSelective(deviceCollectingDevice);
                }else {
                    throw new RuntimeException(Constants.COLLECTINGDEVICE_TYPE_REPEAT);
                }
            }
        }
        return responseResult;
    }


    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("编辑采集设备，绑定对应的采集设备类型")
    public ObjectRestResponse update(@RequestBody  Map<String,Object> params ) {
        DeviceCollectingDevice deviceCollectingDevice =new DeviceCollectingDevice();
        deviceCollectingDevice.setId((Integer) params.get("id"));
        deviceCollectingDevice.setNo((String) params.get("no"));
        deviceCollectingDevice.setPositionDescription((String) params.get("positionDescription"));
        deviceCollectingDevice.setGeographicalPositionSign((String) params.get("geographicalPositionSign"));
        List<String> entity =JSON.parseArray((String) params.get("entity"),String.class);
        TrimUtil.trimObject(deviceCollectingDevice);
        TrimUtil.trimNull(deviceCollectingDevice.getNo(),deviceCollectingDevice.getPositionDescription());
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(deviceCollectingDevice==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            DeviceCollectingDevice temp = baseBiz.selectById(deviceCollectingDevice.getId());
            if(temp==null){
                throw  new RuntimeException(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
            }
            String code = deviceCollectingDevice.getNo();
            //判断代号是否修改，修改判重
            if(code!=null&&!"".equals(code)){
                //判断代号
                if(!code.equalsIgnoreCase(temp.getNo())&&dcdBiz.selectByCount(code)>0){
                    throw  new RuntimeException(Constants.DEVICE_CODE_REPEAT);
                }
                //根据厂商、类型、型号查询ID
                List<DeviceCollectingDeviceType> list = dcdtBiz.selectByType(entity.get(0),entity.get(1),entity.get(2));
                if(list.size()==1){
                    for (DeviceCollectingDeviceType deviceCollectingDeviceType:list){
                        deviceCollectingDevice.setCollectingDevicTypeId(deviceCollectingDeviceType.getId());
                    }
                    baseBiz.updateSelectiveById(deviceCollectingDevice);
                }else {
                    throw new RuntimeException(Constants.COLLECTINGDEVICE_TYPE_REPEAT);
                }
            }
        }
        return responseResult;
    }




}