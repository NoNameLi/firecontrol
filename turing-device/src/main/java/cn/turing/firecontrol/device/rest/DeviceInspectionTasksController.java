package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.exception.BaseException;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.SplitUtil;
import cn.turing.firecontrol.device.vo.LabelCountVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("deviceInspectionTasks")
@CheckClientToken
@CheckUserToken
public class DeviceInspectionTasksController extends BaseController<DeviceInspectionTasksBiz,DeviceInspectionTasks,Integer> {

    @Autowired
    private DeviceInspectionTasksBiz ditBiz;
    @Autowired
    private DeviceInspectionRouteBiz dirBiz;
    @Autowired
    private DeviceRouteLabelBiz drlBiz;
    @Autowired
    private DeviceBuildingBiz dbBiz;
    @Autowired
    private DeviceIndoorLabelBiz dilBiz;
    @Autowired
    private DeviceIndoorRecordInspectionResultsBiz dirirBiz;
    @Autowired
    private DeviceOutdoorRecordInspectionResultsBiz dorirBiz;

    @RequestMapping(value = "/selectMyTasksList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("我的巡检任务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页条数",paramType = "query"),
            @ApiImplicitParam(name = "userId",value = "巡检人员id",paramType = "query")
    })
    public TableResultResponse<DeviceInspectionTasks> selectMyTasksList(String page, String limit, @RequestParam String userId){
        Map<String ,Object> param = new LinkedHashMap<>();
        if(StringUtils.isBlank(page)){
            page="1";
        }
        if(StringUtils.isBlank(limit)){
            limit="10";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return ditBiz.selectMyTasksList(query,userId);
    }

    @RequestMapping(value = "/selectTasksList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("可接取巡检任务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页条数",paramType = "query")
    })
    public TableResultResponse<DeviceInspectionTasks> selectTasksList(String page, String limit){
        Map<String ,Object> param = new LinkedHashMap<>();
        if(StringUtils.isBlank(page)){
            page="1";
        }
        if(StringUtils.isBlank(limit)){
            limit="10";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return ditBiz.selectTasksList(query);
    }

    @RequestMapping(value = "/setUser",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("指派巡检人员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "巡检任务id",paramType = "query"),
            @ApiImplicitParam(name = "userId",value = "巡检人员id",paramType = "query")
    })
    public ObjectRestResponse<DeviceInspectionTasks> setUser(@RequestParam Integer id,@RequestParam String userId){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        DeviceInspectionTasks deviceInspectionTasks = ditBiz.selectById(id);
        if(deviceInspectionTasks==null){
            throw new BaseException("此巡检任务不存在!");
        }
        if("0".equals(deviceInspectionTasks.getStatus())){
            //关联巡检人员
            deviceInspectionTasks.setUserId(userId);
            //更改巡检任务状态为已接取
            deviceInspectionTasks.setStatus("1");
            ditBiz.updateSelectiveById(deviceInspectionTasks);
        }else{
            responseResult.setData("任务已被接取");
        }
        return responseResult;
    }

    @RequestMapping(value = "/giveTask",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("放弃任务")
    @ApiImplicitParam(name = "id",value = "巡检任务id",paramType = "query")
    public ObjectRestResponse giveTask(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        DeviceInspectionTasks deviceInspectionTasks = ditBiz.selectById(id);
        if(deviceInspectionTasks==null){
            throw new BaseException("此巡检任务不存在!");
        }
        if("2".equals(deviceInspectionTasks.getStatus())){
            responseResult.setData(false);
            return responseResult;
        }
        deviceInspectionTasks.setUserId(null);
        //更改巡检任务状态为未接取
        deviceInspectionTasks.setStatus("0");
        //ditBiz.updateSelectiveById(deviceInspectionTasks);
        ditBiz.updateById(deviceInspectionTasks);
        responseResult.setData(true);
        return responseResult;
    }

    @RequestMapping(value = "/selectBuildingList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询巡检任务建筑列表")
    @ApiImplicitParam(name = "routeId",value = "路线id",paramType = "query")
    public ObjectRestResponse<DeviceInspectionTasks> selectBuildingList(@RequestParam Integer routeId){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        DeviceInspectionRoute deviceInspectionRoute = dirBiz.selectById(routeId);
        List<Integer> list = drlBiz.selectByRouteId(deviceInspectionRoute.getId(),"0");
        List<Integer> buildingList = dilBiz.getBuildingId(SplitUtil.merge(list),"1","0","0","0");
        Set<Integer> set =new HashSet<>();
        set.addAll(buildingList);
        List resultList = new ArrayList();
        for(Integer buildingId:set){
            DeviceBuilding deviceBuilding = dbBiz.selectById(buildingId);
            Map map = new HashMap();
            map.put("id",deviceBuilding.getId());
            map.put("name",deviceBuilding.getBName());
            resultList.add(map);
        }
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/selectCountByRouteId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询任务状态数量")
    @ApiImplicitParam(name = "routeId",value = "路线id",paramType = "query")
    public ObjectRestResponse<DeviceInspectionTasks> selectCountByRouteId(@RequestParam Integer routeId,Integer taskId){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        DeviceInspectionRoute deviceInspectionRoute = dirBiz.selectById(routeId);
        Map map = new HashMap();
        if("0".equals(deviceInspectionRoute.getRouteFlag())){//室内路线
            List<Integer> idList = drlBiz.selectByRouteId(deviceInspectionRoute.getId(),"0");
            if(idList.size()>0){
                Integer jumpCount = dirirBiz.selectByTaskIdAndlabalId(taskId, SplitUtil.merge(idList),"1",null);
                Integer checkedCount = dirirBiz.selectByTaskIdAndlabalId(taskId, SplitUtil.merge(idList),"0",null);
                Integer uncheckedCount = idList.size()-jumpCount-checkedCount;
                if(uncheckedCount<0){
                    map.put("uncheckedCount","0");
                }else{
                    map.put("uncheckedCount",uncheckedCount);
                }
                map.put("jumpCount",jumpCount);
                map.put("checkedCount",checkedCount);
            }else{
                map.put("uncheckedCount","0");
                map.put("jumpCount","0");
                map.put("checkedCount","0");
            }
        }
        if("1".equals(deviceInspectionRoute.getRouteFlag())){//室外路线
            List<Integer> idList = drlBiz.selectByRouteId(deviceInspectionRoute.getId(),"0");
            if(idList.size()>0){
                Integer jumpCount = dorirBiz.selectByTaskIdAndlabalId(taskId, SplitUtil.merge(idList),"1",null);
                Integer checkedCount = dorirBiz.selectByTaskIdAndlabalId(taskId, SplitUtil.merge(idList),"0",null);
                Integer uncheckedCount = idList.size()-jumpCount-checkedCount;
                if(uncheckedCount<0){
                    map.put("uncheckedCount","0");
                }else{
                    map.put("uncheckedCount",uncheckedCount);
                }
                map.put("jumpCount",jumpCount);
                map.put("checkedCount",checkedCount);
            }else{
                map.put("uncheckedCount","0");
                map.put("jumpCount","0");
                map.put("checkedCount","0");
            }
        }
        responseResult.setData(map);
        return responseResult;
    }

    @Autowired
    private DeviceTasks deviceTasks;

    @RequestMapping(value = "/generator/{password}",method = RequestMethod.GET)
    @ResponseBody
    @IgnoreClientToken
    @IgnoreUserToken
    public ObjectRestResponse generator(@PathVariable("password")String password){
        if(!"Turing2018".equals(password)){
            throw new ParamErrorException("无权限");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                deviceTasks.delectTask();
            }
        }).start();
        return new ObjectRestResponse();
    }



}