package cn.turing.firecontrol.datahandler.feign;

import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.datahandler.entity.DeviceAbnormal;
import cn.turing.firecontrol.datahandler.entity.DeviceFacilitiesAbnormal;
import cn.turing.firecontrol.device.entity.DeviceBuilding;
import cn.turing.firecontrol.device.entity.DeviceSensorType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(value = "turing-device")
public interface IDeviceFeign {
      //查询建筑屋信息
    @RequestMapping(value="/deviceBuilding/selectById",method = RequestMethod.GET)
    public JSONObject getBuilding(@RequestParam("id") Integer id);

    //根据ID修改传感器状态
    @RequestMapping(value = "/deviceSensor/updateStatus",method = RequestMethod.POST)
    public void updateStatus(@RequestParam("sensorNo") String sensorNo,@RequestParam("statusTime") String statusTime,@RequestParam("status") String status);

    //查询设备信息
    @RequestMapping(value = "/deviceSensor/selectBySensorNo",method = RequestMethod.GET)
    public JSONObject selectBySensorNo(@RequestParam("sensorNo") String sensorNo);

    @RequestMapping(value = "/deviceAlarmLevel/selectAlarmLevel",method = RequestMethod.GET)
    public JSONObject selectAlarmLevel(@RequestParam("sensorNo") String sensorNo,@RequestParam("alemData") Double alemData,@RequestParam("codeName") String codeName,@RequestParam("tenantId") String tenantId);

    //查询测点信息
    @RequestMapping(value="/deviceSensor/getMeasuringPoints",method = RequestMethod.GET)
    public JSONArray getMeasuringPoints(@RequestParam("id") Integer id);

    //查询传感器类型
    @RequestMapping(value="/deviceSensorType/selectById",method = RequestMethod.GET)
    public JSONObject deviceSensorType(@RequestParam("id") Integer id);

    //增加异常信息表
    @RequestMapping(value="/deviceAbnormal/batchInsert",method = RequestMethod.POST)
    public JSONObject batchInsert(List<DeviceAbnormal> list);

    //查询硬件设施信息
    @RequestMapping(value="/deviceHardwareFacilities/getByHydrantId",method = RequestMethod.GET)
    public JSONObject getHardware(@RequestParam("id") Integer id);

    //增加异常信息表
    @RequestMapping(value="/deviceFacilitiesAbnormal/batchInsert",method = RequestMethod.POST)
    public JSONObject insert(List<DeviceFacilitiesAbnormal> list);

    //1. 预警，报警，故障通知方式，与接收人的接口
    @RequestMapping(value="/deviceMessageNotice/messageNotice",method = RequestMethod.GET)
    JSONObject messageNotice(@RequestParam("noticeType")String noticeType, @RequestParam("levelId")String levelId,@RequestParam("tenantId")String tenantId);

    //查询建筑列表(包含已删除)
    @RequestMapping(value="/deviceBuilding/getBuildingList",method = RequestMethod.GET)
    public JSONObject getBuildingList();

    //查询建筑列表(不包含已删除)
    @RequestMapping(value="/deviceBuilding/getList",method = RequestMethod.GET)
    public ObjectRestResponse<List<DeviceBuilding>> getBuildings();

    //查询联网单位
    @RequestMapping(value="/deviceNetworkingUnit/getById",method = RequestMethod.GET)
    public JSONObject getById(@RequestParam(value = "id") Integer id);

    //查询硬件设施列表
    @RequestMapping(value="/deviceHardwareFacilities/getHardwareFacilitiesList",method = RequestMethod.GET)
    public JSONObject getHardwareFacilitiesList();

    //查询传感器
    @RequestMapping(value="/deviceSensor/getById",method = RequestMethod.GET)
    public JSONObject deviceSensor(@RequestParam("id") Long id);

    //查询消防主机传感器
    @RequestMapping(value="/deviceFireMainSensor/selectIgnoreTenantIpAndPortAndSensor",method = RequestMethod.GET)
    public JSONObject deviceFireMainSensor(@RequestParam("serverIp") String serverIp,@RequestParam("port") String port,@RequestParam("sensorLoop") String sensorLoop,@RequestParam("address") String address);

    //根据ID修改消防主机传感器状态
    @RequestMapping(value = "/deviceFireMainSensor/updateSensorStatus",method = RequestMethod.GET)
    public void updataFireMainSensor(@RequestParam("serverIp") String serverIp,@RequestParam("port") String port,@RequestParam("status") String status,@RequestParam("statusTime") String statusTime,@RequestParam("sensorLoop")String sensorLoop,@RequestParam("address")String address);

    //查询消防主机
    @RequestMapping(value="/deviceFireMain/selectById",method = RequestMethod.GET)
    public JSONObject getFireMain(@RequestParam("id") Integer id);


    //查询传感器列表
    @RequestMapping(value="/deviceSensor/queryByIds",method = RequestMethod.POST)
    public TableResultResponse querySensors(@RequestParam("pageNo") Integer pageNo,
                                            @RequestParam("limit") Integer limit,
                                            @RequestParam("queryStr") String queryStr
                                           );

    //查询传感器列表
    @RequestMapping(value="/deviceFireMainSensor/listFireMainSensorByIds",method=RequestMethod.POST)
    public TableResultResponse listFireMainSensorByIds(@RequestParam("pageNo") Integer pageNo,
                                            @RequestParam("limit") Integer limit,
                                            @RequestParam("queryStr") String queryStr);

//    Long[] ids,
//    @RequestParam("manufacturer") String manufacturer,
//    @RequestParam("equipmentType") String equipmentType,
//    @RequestParam("model") String model,
//    @RequestParam("sensorNo") String sensorNo,
//    @RequestParam("buildingId") Long buildingId,
//    @RequestParam("excludeIds") Long[] excludeIds,
//    @RequestParam("channelId") Integer channelId

    //查询室外传感器列表
    @RequestMapping(value="/deviceSensor/queryOutdoorSensor",method = RequestMethod.POST)
    public TableResultResponse queryOutdoorSensor(@RequestParam("pageNo") Integer pageNo,
                                                  @RequestParam("limit") Integer limit,
                                                  @RequestParam("queryStr") String queryStr);

    //查询设备组传感器列表
    @RequestMapping(value="/deviceSensor/queryNestedSensor",method = RequestMethod.POST)
    public TableResultResponse queryNestedSensor(@RequestParam("pageNo") Integer pageNo,
                                                 @RequestParam("limit") Integer limit,
                                                 @RequestParam("queryStr") String queryStr);

    @RequestMapping(value = "/deviceSensorType/selectByTenant",method = RequestMethod.GET)
    public ObjectRestResponse<List<DeviceSensorType>> queryTypeByTenant();
}
