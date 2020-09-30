package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.*;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.core.exception.BaseException;
import cn.turing.firecontrol.device.biz.DeviceBuildingBiz;
import cn.turing.firecontrol.device.biz.DeviceFireMainSensorBiz;
import cn.turing.firecontrol.device.biz.DeviceNetworkingUnitBiz;
import cn.turing.firecontrol.device.biz.DeviceSensorBiz;
import cn.turing.firecontrol.device.config.ApplicationStartListener;
import cn.turing.firecontrol.device.entity.DeviceBuilding;
import cn.turing.firecontrol.device.entity.DeviceFireMainSensor;
import cn.turing.firecontrol.device.entity.DeviceNetworkingUnit;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.mapper.DeviceBuildingMapper;
import cn.turing.firecontrol.device.util.ResponseCode;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.TrimUtil;
import cn.turing.firecontrol.device.vo.DeviceBuildingVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.*;


@RestController
@Api(tags = "建筑物")
@RequestMapping("deviceBuilding")
@CheckClientToken
@CheckUserToken
//@TestUserToken(token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJkZW1vIiwidXNlcl9uYW1lIjoiZGVtbyIsImlzU3VwZXJBZG1pbiI6IjAiLCJ1c2VyTmFtZSI6IuWbvueBtea1i-ivlSIsInVzZXJJZCI6ImNMejlqdFJhIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImNsaWVudF9pZCI6InZ1ZSIsImlzVGVuYW50QWRtaW4iOiIxIiwic2NvcGUiOlsicmVhZCJdLCJleHBpcmUiOjE1NjgxOTA1NzE2OTMsImV4cCI6MTU3MDc3ODk3MSwiZGVwYXJ0IjoiZDU4M2U3ZGU2ZDJkNDhiNzhmYjNjN2RjYjE4MGNiMWYiLCJqdGkiOiIxZWVhYTkwMC0xYWE3LTRlYzctOGM0My0xOTE4MWVjZDU4YzIiLCJ0ZW5hbnQiOiJhMTY0SkF5OSJ9.qsXQEYyfUo3q0QGKvsjaMHZchv5LrRTK2g5wbTnKrfyyMXrr4tVZVHQg3m9xRsty4iJuMUalRDDzxZsMKbVAtVXtTvrgn_ZX62rGy-GGNrbBUMMqmtyly3xJtnH4YUXVZlhoc9zgy0iFZOnlJcc7YSvltGOeRkanuV_ccIqVAhk")
@Slf4j
public class DeviceBuildingController extends BaseController<DeviceBuildingBiz,DeviceBuilding,Integer> {

    @Autowired
    protected  DeviceBuildingBiz dbBiz;
    @Autowired
    protected DeviceNetworkingUnitBiz dnuBiz;
    @Autowired
    protected DeviceSensorBiz dsBiz;
    @Autowired
    private DeviceBuildingMapper deviceBuildingMapper;
    @Autowired
    private DeviceFireMainSensorBiz dfmsBiz;
    @Autowired
    private IUserFeign iUserFeign;


    @RequestMapping(value = "/select",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据6位行政区编码查询省市区")
    public ObjectRestResponse selectAll(Integer channelId){
        //开发新功能
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List list = dbBiz.selectAll(channelId);
        responseResult.setStatus(ResponseCode.API_CODE_CALL_SUCCESS);
        responseResult.setMessage(Constants.API_MESSAGE_SUCCESS);
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/selectFirst",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("返回第一个建筑id")
    public ObjectRestResponse selectFirst(){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        Map map = dbBiz.selectFirst();
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除建筑物，传感器关联删除")
    public ObjectRestResponse<DeviceBuilding> delete(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(id == null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            List<DeviceSensor> list = dsBiz.selectByBuildingId(id,null);
            if(list!=null&&list.size()>0){
                throw new RuntimeException("有传感器绑了此建筑，请先解绑！");
            }
            DeviceBuilding deviceBuilding = new DeviceBuilding();
            //假删除
            deviceBuilding.setId(id);
            deviceBuilding.setDelFlag("1");
            dbBiz.updateSelectiveById(deviceBuilding);
        }
        return responseResult;
    }

    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除建筑物，查看绑定的传感器")
    public ObjectRestResponse<DeviceBuilding> deleteQuery(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(id == null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            //传感器假删除
            List<DeviceSensor> list = dsBiz.selectByBuildingId(id,null);
            //消防主机传感器1.6
/*            List<DeviceFireMainSensor> list1 = dfmsBiz.selectByBuildingIdQuery(id);
            responseResult.setData(list.size()+list1.size());*/
            responseResult.setData(list.size());
        }
        return responseResult;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加建筑物")
    public ObjectRestResponse<DeviceBuilding> add(@RequestBody DeviceBuilding deviceBuilding){
        TrimUtil.trimObject(deviceBuilding);
        if(deviceBuilding.getbTimeLong()!=null){
            deviceBuilding.setBTime(new Date(deviceBuilding.getbTimeLong()));
        }
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(deviceBuilding == null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            DeviceBuilding building = dbBiz.selectByBname(deviceBuilding.getBName());
            if(building==null){
                //获取修改用户，修改用户
                String username  = BaseContextHandler.getUsername();
                JSONObject jsonObject  = iUserFeign.getUser(username);
                JSONObject data = jsonObject.getJSONObject("data");
                String name  = (String) data.get("name");
                deviceBuilding.setCrtUserName(username+"("+name+")");
                deviceBuilding.setCrtTime(new Date());
                deviceBuilding.setCrtUserId((String) data.get("id"));
                deviceBuilding.setTenantId((String) data.get("tenantId"));
                deviceBuilding.setDepartId((String) data.get("departId"));
                //不使用框架的默认赋值
                if(deviceBuilding.getZxqy()!=null&&deviceBuilding.getZxqy().length()==6){
                    if(deviceBuilding.getBAddress()!=null&&deviceBuilding.getBAddress().length()>200){
                        throw new RuntimeException("建筑地址长度不能超过200！");
                    }
                    deviceBuildingMapper.insertSelective(deviceBuilding);
                }else {
                    throw new RuntimeException("地区编码不识别！");
                }
            }else{
                throw new RuntimeException(Constants.BUILDING_NAME_REPEAT);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象")
    public ObjectRestResponse<DeviceBuilding> get(@RequestParam Integer id){
        ObjectRestResponse<DeviceBuilding> entityObjectRestResponse = new ObjectRestResponse<>();
        DeviceBuilding entity = baseBiz.selectById(id);
        if(entity==null){
            return entityObjectRestResponse;
        }
        //返回时间戳
        if(entity.getBTime()!=null){
            entity.setbTimeLong(entity.getBTime().getTime());
        }
        //当修改用户名没有时不传修改用户的时间
        if(entity.getUpdUserName()==null||"".equals(entity.getUpdUserName())){
            entity.setUpdTime(null);
        }
        entityObjectRestResponse.data(entity);
        return entityObjectRestResponse;
    }


    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("更新建筑物")
    public ObjectRestResponse<DeviceBuilding> update(@RequestBody DeviceBuilding deviceBuilding){
        TrimUtil.trimObject(deviceBuilding);
        if(deviceBuilding.getbTimeLong()!=null){
            deviceBuilding.setBTime(new Date(deviceBuilding.getbTimeLong()));
        }
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(deviceBuilding == null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            String street = deviceBuilding.getStreet();
            String bname = deviceBuilding.getBName();
            DeviceBuilding building = dbBiz.selectById(deviceBuilding.getId());
            //判断
            if(!building.getBName().equalsIgnoreCase(bname)&&dbBiz.selectByCount(bname)>0){
                throw new RuntimeException(Constants.BUILDING_NAME_REPEAT);
            }
            if(street!=null&&street.length()>3){
                throw  new RuntimeException("街道编码错误！");
            }
            //获取修改用户，修改用户
            String username  = BaseContextHandler.getUsername();
            JSONObject jsonObject  = iUserFeign.getUser(username);
            JSONObject data = jsonObject.getJSONObject("data");
            String name  = (String) data.get("name");
            deviceBuilding.setUpdUserName(username+"("+name+")");
            deviceBuilding.setUpdTime(new Date());
            deviceBuilding.setUpdUserId((String) data.get("id"));
            //不使用框架的默认赋值
            //不允许修改地区编码
//            deviceBuilding.setZxqy(null);
            if(deviceBuilding.getBAddress()!=null&&deviceBuilding.getBAddress().length()>200){
                throw new RuntimeException("建筑地址长度不能超过200！");
            }
            deviceBuildingMapper.updateByPrimaryKeySelective(deviceBuilding);
        }
        return responseResult;
    }

    @RequestMapping(value = "/selectAllBuilding",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有建筑物，对应绑定的传感器状态")
    public ObjectRestResponse<DeviceBuilding> selectAllBuilding(){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List<DeviceBuilding> buildinglist = dbBiz.getAll(null);//所有建筑
        Map<String,Object> map = new HashMap();
        for(DeviceBuilding deviceBuilding:buildinglist){
            map.put("deviceBuilding",deviceBuilding);
            List<DeviceSensor> sensorlist = dsBiz.selectByBuildingId(deviceBuilding.getId(),null);//建筑所有绑定的传感器
            for(DeviceSensor deviceSensor:sensorlist){
                if(deviceSensor.getStatus().equals("0")){//故障
                    map.put("status","0");
                }
                if(deviceSensor.getStatus().equals("1")){//报警
                    map.put("status","1");
                }
                if(deviceSensor.getStatus().equals("2")){//正常
                    map.put("status","2");
                }
            }
        }
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按建筑名称查询")
    public TableResultResponse<DeviceBuilding> list(@RequestParam String page, @RequestParam String limit, String buildingName ) {
        Map<String ,Object> param = new LinkedHashMap<>();
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dbBiz.selectQuery(query,buildingName);
    }

    @RequestMapping(value = "/selectByBuildId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑物id查询联网单位")
    public ObjectRestResponse<DeviceBuilding> selectByBuildId(@RequestParam Integer buildId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(buildId == null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            DeviceBuilding deviceBuilding = dbBiz.selectById(buildId);
            if(deviceBuilding==null){
                return responseResult;
            }
            DeviceNetworkingUnit deviceNetworkingUnit = dnuBiz.selectById(deviceBuilding.getOid());
            Map map = new HashMap();
            map.put("bName",deviceBuilding.getBName());
            map.put("bAddress",deviceBuilding.getBAddress());
            map.put("safeDutyName",deviceNetworkingUnit.getSafeDutyName());//单位消防安全责任人姓名
            map.put("safeDutyPhone",deviceNetworkingUnit.getSafeDutyPhone());//单位消防安全责任人电话
            map.put("legalName",deviceNetworkingUnit.getLegalName());//企业法人姓名
            map.put("legalPhone",deviceNetworkingUnit.getLegalPhone());//企业法人电话
            map.put("safeManagerName",deviceNetworkingUnit.getSafeManagerName());//单位消防安全管理人员姓名
            map.put("safeManagerPhone",deviceNetworkingUnit.getSafeManagerPhone());//单位消防安全管理人员电话
            responseResult.setData(map);
        }
        return responseResult;
    }

    @RequestMapping(value = "/getFloor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获得建筑物对应的楼层")
    public ObjectRestResponse getFloor(@RequestParam Integer buildId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        DeviceBuilding deviceBuilding = dbBiz.selectById(buildId);
        if(deviceBuilding==null){
            return responseResult;
        }
        Integer upFloor = deviceBuilding.getUpFloor();//地上层数
        Integer underFloor =  deviceBuilding.getUnderFloor();//地下层数
        Integer upfloor = upFloor+1;
        List<Integer> list = new ArrayList();
        for (int i=0;i<upFloor;i++){
            upfloor--;
            list.add(upfloor);
        }
        Integer underfloor = 0;
        for (int i=0;i<underFloor;i++){
            underfloor--;
            list.add(underfloor);
        }
        List resultlist = new ArrayList();
        for(Integer floor:list){
            Map map = new HashMap();
            map.put("floor",floor);
            resultlist.add(map);
        }
        responseResult.setData(resultlist);
        return responseResult;
    }

    @RequestMapping(value = "/getFloorList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获得建筑物对应的楼层")
    public ObjectRestResponse getFloorList(@RequestParam Integer buildId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        DeviceBuilding deviceBuilding = dbBiz.selectById(buildId);
        Integer upFloor = deviceBuilding.getUpFloor();//地上层数
        Integer underFloor =  deviceBuilding.getUnderFloor();//地下层数
        Integer upfloor = 0;
        List<Integer> list = new ArrayList();
        for (int i=0;i<upFloor;i++){
            upfloor++;
            list.add(upfloor);
        }
        Integer underfloor = 0;
        for (int i=0;i<underFloor;i++){
            underfloor--;
            list.add(underfloor);
        }
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/getSelected",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有的建筑物,供下拉框选择")
    public ObjectRestResponse  getSelected(){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //获取所有的建筑物名称
        List<String> bname = dbBiz.getBname();
        Map<String,Object> map = new HashMap<>();
        LinkedList<String> tempbname = new LinkedList<>();
        if(bname!=null&&bname.size()>0){
            if(!"".equals(bname.get(0))){
                tempbname.addAll(bname);
            }
        }
        tempbname.addFirst("全部");
        map.put("bname",tempbname);
        responseResult.setData(map);
        return responseResult;
    }

    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/selectById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据id查询建筑物")
    public ObjectRestResponse  getSelectById(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        DeviceBuilding deviceBuilding = dbBiz.getById(id);
        responseResult.setData(deviceBuilding);
        return responseResult;
    }

    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/selectByBName",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑名称查询建筑物")
    public ObjectRestResponse selectByBName(@RequestParam String bName){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(bName==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else{
            DeviceBuilding deviceBuilding = dbBiz.selectByBname(bName);
            if(deviceBuilding==null){
                log.info("building no exist :"+bName);
            }else{
                responseResult.setData(deviceBuilding);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/getBuildingByBNameAndFloor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP二级联动建筑物名称和楼层")
    public ObjectRestResponse getBuildingByBNameAndFloor(Integer buildingId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List<DeviceBuilding> list = dbBiz.getAll(buildingId);
        List<Map<String,Object>> resultlist = new ArrayList<>();
        for(DeviceBuilding deviceBuilding:list){
            Map map = new HashMap();
            map.put("level",1);
            map.put("name",deviceBuilding.getBName());
            map.put("pname","");
            map.put("buildingId",deviceBuilding.getId());
            resultlist.add(map);
            ObjectRestResponse objectRestResponse = this.getFloor(deviceBuilding.getId());
            List<Map> floorlist = (List<Map>)objectRestResponse.getData();
            for(Map floormap : floorlist){
                Integer floor = (Integer)floormap.get("floor");
                Map map1 = new HashMap();
                map1.put("level",2);
                map1.put("name",floor);
                map1.put("pname",deviceBuilding.getBName());
                map1.put("buildingId",deviceBuilding.getId());
                resultlist.add(map1);
            }
        }
        responseResult.setData(resultlist);
        return responseResult;
    }

    @IgnoreClientToken
    @RequestMapping(value = "/getBuildingList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询建筑列表(包含已删除)")
    public ObjectRestResponse getBuildingList(){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        //List<DeviceBuilding> list = dbBiz.getAll();
        List<DeviceBuilding> list = dbBiz.getAllAndDelflag();
        responseResult.setData(list);
        return responseResult;
    }

    @IgnoreClientToken
    @RequestMapping(value = "/getList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询建筑列表（不包含已删除）")
    public ObjectRestResponse getList(){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List<DeviceBuilding> list = dbBiz.getAll(null);
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/getAreaCode",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取地区编码json对象")
    public JSONArray getAreaCode(){
        JSONArray jsonArray = null;
        try{
            String filePath = "AreaCode.json";
            Resource resource = new ClassPathResource(filePath);
            InputStream is = ApplicationStartListener.class.getClassLoader().getResourceAsStream(filePath);
            String content = IOUtils.toString(is, "utf-8");
            jsonArray = JSONArray.parseArray(content);
        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }
        return jsonArray;
    }
    @RequestMapping(value = "/getMaxAlarmBuilding",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取报警最多的建筑物")
    public ObjectRestResponse<DeviceBuildingVo> getMaxAlarmBuilding(@RequestParam Integer channelId){
        DeviceBuildingVo building = baseBiz.getMaxAlarmBuilding(channelId);
        return new ObjectRestResponse().data(building);
    }
    @GetMapping(value = "/getAllBuildingStatus")
    @ResponseBody
    @ApiOperation("获取所有建筑物及消火栓状态")
    public ObjectRestResponse<List<DeviceBuildingVo>> getAllBuildingStatus(){
        List<DeviceBuildingVo> buildingStatus = baseBiz.getAllBuildingStatus();
        return new ObjectRestResponse().data(buildingStatus);
    }
}