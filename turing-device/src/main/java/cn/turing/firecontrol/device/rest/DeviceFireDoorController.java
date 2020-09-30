package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.biz.DeviceAlarmLevelBiz;
import cn.turing.firecontrol.device.biz.DeviceFireDoorBiz;
import cn.turing.firecontrol.device.dto.FireDoorSensorDto;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.vo.FdSensorVo;
import cn.turing.firecontrol.device.vo.FireDoorVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("deviceFireDoor")
@CheckClientToken
@CheckUserToken
//@IgnoreUserToken
//@IgnoreClientToken
public class DeviceFireDoorController extends BaseController<DeviceFireDoorBiz,DeviceFireDoor,Integer>{

    @Autowired
    private DeviceFireDoorBiz deviceFireDoorBiz;
    /**
     * 修改防火门
     * @param door
     * @return
     */
    @RequestMapping(value = "/updateFireDoor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改防火门")
    public ObjectRestResponse updateFireDoor(DeviceFireDoor door){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        deviceFireDoorBiz.updateFireDoor(door);
        return responseResult;
    }

    /**
     * 删除防火门
     * @param doorIds
     * @return
     */
    @RequestMapping(value = "/deleteFireDoor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("删除防火门")
    public ObjectRestResponse deleteFireDoor(@RequestParam String doorIds){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        deviceFireDoorBiz.deleteFireDoor(doorIds);
        return responseResult;
    }

    /**
     * 防火门列表分页查询
     * @param door
     * @return
     */
    @RequestMapping(value = "/listFireDoor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("防火门列表分页查询")
    public TableResultResponse listFireDoor(Integer page, Integer limit,
                                             DeviceFireDoor door){
        if(page == null || page.intValue() <= 0){
            page = 1;
        }
        if(limit == null || limit <= 0 ){
            limit = 20;
        }
        return deviceFireDoorBiz.listFireDoor(page,limit,door);
    }


    /**
     * 增加防火门传感器
     * @param fireDoorId  防火门ID
     * @param sensorNo 设备编号
     * @param manufacturer　厂商
     * @param model 型号
     * @param equipmentType　设备类型（序列）
     */
    @RequestMapping(value = "/addSensor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("增加防火门传感器")
    public ObjectRestResponse addSensor(@RequestParam Integer channelId,
                                        @RequestParam Long fireDoorId,
                                        @RequestParam String sensorNo,
                                        @RequestParam String manufacturer,
                                        @RequestParam String model,
                                        @RequestParam String equipmentType){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        deviceFireDoorBiz.addSensor(channelId,fireDoorId,sensorNo,manufacturer,model,equipmentType);
        return responseResult;
    }

    /**
     * 获取防火门所在楼层的平面图
     * @param fireDoorId
     * @return
     */
    @RequestMapping(value = "/getFloorLayout",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取防火门所在楼层的平面图")
    public ObjectRestResponse getFloorLayout(@RequestParam Long fireDoorId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        return responseResult.data(deviceFireDoorBiz.getFloorLayout(fireDoorId));
    }

    /**
     * 标记防火墙位置
     * @param doorId 防火门ID
     * @param position 防火门位置
     * @return
     */
    @RequestMapping(value = "/signFireDoor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("标记防火墙位置")
    public ObjectRestResponse signFireDoor(@RequestParam Long doorId,@RequestParam String position){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        deviceFireDoorBiz.signFireDoor(doorId,position);
        return responseResult;
    }

    /**
     * 防火门传感器列表
     * @param page
     * @param limit
     * @param sensor
     * @return
     */
    @RequestMapping(value = "/listSensor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("防火门传感器列表")
    public TableResultResponse listSensor(Integer page, Integer limit,FireDoorSensorDto sensor){
        if(page == null || page.intValue() <= 0){
            page = 1;
        }
        if(limit == null || limit <= 0 ){
            limit = 20;
        }
        return deviceFireDoorBiz.listSensor(page,limit,sensor);
    }

    /**
     * 更新传感器
     * @param sensor
     * @return
     */
    @RequestMapping(value = "/updateFdSensor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("更新传感器")
    public ObjectRestResponse updateFdSensor(FireDoorSensorDto sensor){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        deviceFireDoorBiz.updateFdSensor(sensor);
        return responseResult;
    }

    /**
     * 删除传感器
     * @param sensorIds
     * @return
     */
    @RequestMapping(value = "/deleteFdSensor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("删除传感器")
    public ObjectRestResponse deleteFdSensor(@RequestParam String sensorIds){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        deviceFireDoorBiz.deleteFdSensor(sensorIds);
        return responseResult;
    }

    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/getFireDoorNormalStatus",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获传感器关联的防火门类型")
    public ObjectRestResponse getFireDoorNormalStatus(@RequestParam String sensorNo){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        responseResult.data(deviceFireDoorBiz.getFireDoorNormalStatus(sensorNo));
        return responseResult;
    }

    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/updateSensorDataStatus",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("更新防火门传感器状态")
    public ObjectRestResponse updateSensorDataStatus(@RequestParam String sensorNo,String status){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        deviceFireDoorBiz.updateSensorDataStatus(sensorNo,status);
        return responseResult;
    }

    @RequestMapping(value = "/addFireDoor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("新增防火门")
    public ObjectRestResponse addFireDoor(DeviceFireDoor door){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        deviceFireDoorBiz.addFireDoor(door);
        return responseResult;
    }


}
