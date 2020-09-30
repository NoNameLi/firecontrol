package cn.turing.firecontrol.server.feign;

import cn.turing.firecontrol.auth.client.config.FeignApplyConfiguration;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



@FeignClient(value = "turing-device",configuration = FeignApplyConfiguration.class)//
public interface IDeviceFeign {
      //查询建筑屋信息
    @RequestMapping(value="/deviceBuilding/selectById",method = RequestMethod.GET,headers = "service=data-handler")
    public JSONObject getBuilding(@RequestParam("id") Integer id);
    //根据ID修改传感器状态
    @RequestMapping(value = "/deviceSensor/updateStatus",method = RequestMethod.POST,headers = "service=data-handler")
    public void updateStatus(@RequestParam("sensorNo") String sensorNo, @RequestParam("statusTime") String statusTime, @RequestParam("status") String status);

    //查询设备信息
    @RequestMapping(value = "/deviceSensor/selectBySensorNo",method = RequestMethod.GET,headers = "service=data-handler")
    public JSONObject selectBySensorNo(@RequestParam("sensorNo") String sensorNo);

    @RequestMapping(value = "/deviceAlarmLevel/selectAlarmLevel",method = RequestMethod.GET,headers = "service=data-handler")
    public JSONObject selectAlarmLevel(@RequestParam("sensorNo") String sensorNo, @RequestParam("alemData") Double alemData, @RequestParam("codeName") String codeName);

    //查询测点信息
    @RequestMapping(value="/deviceSensor/getMeasuringPoints",method = RequestMethod.GET)
    public JSONArray getMeasuringPoints(@RequestParam("id") Integer id);

    //查询传感器类型
    @RequestMapping(value="/deviceSensorType/selectById",method = RequestMethod.GET,headers = "service=data-handler")
    public JSONObject deviceSensorType(@RequestParam("id") Integer id);
    //更改设备状态

    @RequestMapping(value="deviceSensor/updateAllStatus",method = RequestMethod.GET,headers ="service=data-handler")
    public  JSONObject updateAllStatusByAD8081(@RequestParam("sensorNo") String sensorNo);

    //查询传感器信息
    @RequestMapping(value="deviceSensor/selectBuilding",method = RequestMethod.GET,headers = "service=data-handler")
    public  JSONObject getSensorMessage(@RequestParam("sensorNo") String sensorNo);

    //查询设备信息
    @RequestMapping(value = "/deviceFireMainSensor/selectIgnoreTenantIpAndPortAndSensor",method = RequestMethod.GET,headers = "service=data-handler")
    public JSONObject selectByIp(@RequestParam("serverIp") String ip, @RequestParam("port") String port, @RequestParam("sensorLoop") String sensorLoop, @RequestParam("address") String localtionNo);

    //查询设备信息
    @RequestMapping(value = "/deviceFireMainSensor/updateSensorStatus",method = RequestMethod.GET,headers = "service=data-handler")
    public JSONObject updateSensorStatus(@RequestParam("serverIp") String ip, @RequestParam("port") String port, @RequestParam("sensorLoop") String sensorLoop, @RequestParam("address") String localtionNo, @RequestParam("status") String status, @RequestParam("statusTime") String statusTime);

    // 查询防火门的的类型（常闭门，长开门）
    @RequestMapping(value = "/deviceFireDoor/getFireDoorNormalStatus",method = RequestMethod.GET)
    public  JSONObject getFireDoorNormalStatusBySensorId(@RequestParam("sensorNo") String sensorNo);

    //修改防火门的状态
    @RequestMapping(value = "/deviceFireDoor/updateSensorDataStatus",method = RequestMethod.POST)
    public void updateFireDoor(@RequestParam("sensorNo") String sensorNo, @RequestParam("status") Integer status);

}
