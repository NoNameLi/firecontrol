package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.SplitUtil;
import cn.turing.firecontrol.device.util.TrimUtil;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.ContentHandler;
import java.util.*;


@RestController
@RequestMapping("deviceInspectionRoute")
@CheckClientToken
@CheckUserToken
public class DeviceInspectionRouteController extends BaseController<DeviceInspectionRouteBiz,DeviceInspectionRoute,Integer> {


    @Autowired
    private DeviceRouteLabelBiz drlBiz;
    @Autowired
    private DeviceIndoorLabelBiz dilBiz;
    @Autowired
    private DeviceOutdoorLabelBiz dolBiz;
    @Autowired
    private DeviceInspectionSchemeBiz disBiz;
    @Autowired
    private DeviceBuildingBiz dbBiz;

    @ApiOperation("室内室外分页获取数据,按条件搜索数据")
    @RequestMapping(value = "/pageIndoorList",method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "当前显示条数",paramType = "query"),
            @ApiImplicitParam(name = "routeName",value = "路线名",paramType = "query"),
    })
    public TableResultResponse<Map> pageIndoorList(@RequestParam String page, @RequestParam String limit, String routeName ){
        String routeFlag = "0";
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return baseBiz.selectPageList(query,routeName,routeFlag);
    }

    @ApiOperation("室内室外分页获取数据,按条件搜索数据")
    @RequestMapping(value = "/pageOutdoorList",method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "当前显示条数",paramType = "query"),
            @ApiImplicitParam(name = "routeName",value = "路线名",paramType = "query"),
    })
    public TableResultResponse<Map> pageOutdoorList(@RequestParam String page, @RequestParam String limit, String routeName ){
        String routeFlag = "1";
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return baseBiz.selectPageList(query,routeName,routeFlag);
    }

    @RequestMapping(value = "/addIndoor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加室内路线")
    public ObjectRestResponse<DeviceInspectionRoute> addIndoor(@RequestBody DeviceInspectionRoute entity){
        String labelFlag = "0";
        ObjectRestResponse<DeviceInspectionRoute> responseResult =  new ObjectRestResponse<DeviceInspectionRoute>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getRouteName());
        String routeName = entity.getRouteName();
        if(routeName.length()>50){
            throw new RuntimeException("巡检路线长度不能超过50");
        }
        if(baseBiz.selectByCount(routeName,null)>0){
            throw new RuntimeException("巡检路线名已存在，不可重复添加");
        }
        entity.setId(null);
        entity.setRouteFlag(labelFlag);
        entity.setLabelCount("0");
        baseBiz.insertSelective(entity);
        return responseResult.data(entity);
    }

    @RequestMapping(value = "/addOutdoor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加室外路线")
    public ObjectRestResponse<DeviceInspectionRoute> addOutdoor(@RequestBody DeviceInspectionRoute entity){
        String labelFlag = "1";
        ObjectRestResponse<DeviceInspectionRoute> responseResult =  new ObjectRestResponse<DeviceInspectionRoute>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getRouteName());
        String routeName = entity.getRouteName();
        if(routeName.length()>50){
            throw new RuntimeException("巡检路线长度不能超过50");
        }
        if(baseBiz.selectByCount(routeName,null)>0){
            throw new RuntimeException("巡检路线名已存在，不可重复添加");
        }
        entity.setId(null);
        entity.setRouteFlag(labelFlag);
        entity.setLabelCount("0");
        baseBiz.insertSelective(entity);
        return responseResult.data(entity);
    }


    @RequestMapping(value = "/updateName",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改室内路线名称")
    public ObjectRestResponse<DeviceInspectionRoute> updateName(@RequestBody Map<String,Object> param){
        String id = (String) param.get("id");
        String routeName = (String) param.get("routeName");
        DeviceInspectionRoute entity = new DeviceInspectionRoute();
        entity.setId(Integer.parseInt(id));
        entity.setRouteName(routeName);
        ObjectRestResponse<DeviceInspectionRoute> responseResult =  new ObjectRestResponse<DeviceInspectionRoute>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getRouteName());
        routeName = entity.getRouteName();
        DeviceInspectionRoute deviceInspectionRoute = baseBiz.selectById(entity.getId());
        if(routeName.length()>50){
            throw new RuntimeException("巡检路线长度不能超过50");
        }
        if(!routeName.equalsIgnoreCase(deviceInspectionRoute.getRouteName())&&baseBiz.selectByCount(routeName,null)>0){
            throw new RuntimeException("巡检路线名已存在");
        }
        baseBiz.updateSelectiveById(entity);
        return responseResult;
    }




/*    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加室内外路线labelFlag[0=室内路线,1=室外路线]")
    public ObjectRestResponse<DeviceInspectionRoute> add(@RequestBody Map<String,Object> params){
        String labelFlag = (String) params.get("labelFlag");
        ObjectRestResponse<DeviceInspectionRoute> responseResult =  new ObjectRestResponse<DeviceInspectionRoute>();
        String ids = (String) params.get("ids");
        DeviceInspectionRoute entity = new DeviceInspectionRoute();
        entity.setRouteName((String) params.get("routeName"));
        TrimUtil.trimObject(entity);
        String routeName = entity.getRouteName();
        if(ValidatorUtils.hasAnyBlank(ids,routeName)){
            return responseResult;
        }
        if(baseBiz.selectByCount(routeName,null)>0){
            throw new RuntimeException("巡检路线名已存在，不可重复添加");
        }
        //获取路线标签id
        Integer[] labels = SplitUtil.splitInt(ids);
        entity.setId(null);
        entity.setRouteFlag(labelFlag);
        entity.setLabelCount(labels.length+"");
        baseBiz.insertSelective(entity);
        //连接关联表
        Integer routeId = entity.getId();
        //当室内路线
        if("0".equalsIgnoreCase(labelFlag)){
            DeviceRouteLabel deviceRouteLabel = null;
            DeviceIndoorLabel deviceIndoorLabel = null;
            for(int i=0;i<labels.length;i++){
                deviceRouteLabel = new DeviceRouteLabel();
                deviceRouteLabel.setRouteId(routeId);
                deviceRouteLabel.setLabelId(labels[i]);
                deviceRouteLabel.setLabelFlag(labelFlag);
                //标记为巡检路线
                deviceRouteLabel.setRouteFlag("0");
                drlBiz.insertSelective(deviceRouteLabel);
            }
            //修改室外标签状态为已经绑定路线
            dilBiz.updateByIds("1",ids);
        }else if("1".equalsIgnoreCase(labelFlag)){
            DeviceRouteLabel deviceRouteLabel = null;
            DeviceOutdoorLabel deviceOutdoorLabel = null;
            for(int i=0;i<labels.length;i++){
                deviceRouteLabel = new DeviceRouteLabel();
                deviceRouteLabel.setRouteId(routeId);
                deviceRouteLabel.setLabelId(labels[i]);
                deviceRouteLabel.setLabelFlag(labelFlag);
                //标记为巡检路线
                deviceRouteLabel.setRouteFlag("0");
                drlBiz.insertSelective(deviceRouteLabel);
            }
            //修改室外标签状态为已经绑定路线
            dolBiz.updateByIds("1",ids);
        }
        return responseResult;
    }*/


    @RequestMapping(value = "/addIndoorLabel",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("给路线添加室内标签")
    public ObjectRestResponse<DeviceInspectionRoute> addIndoorLabel(@RequestBody Map<String,Object> params){
        //      默认巡检
        String routeFlag = "0";
        //默认室内
        String labelFlag = "0";
        ObjectRestResponse<DeviceInspectionRoute> responseResult =  new ObjectRestResponse<DeviceInspectionRoute>();
        String id = (String) params.get("id");
        String ids = (String) params.get("ids");
        if(ValidatorUtils.hasAnyBlank(id,ids)){
            throw new RuntimeException("参数错误");
        }
        Integer[] labels = SplitUtil.splitInt(ids);

        DeviceRouteLabel deviceRouteLabel = null;
        for(int i=0;i<labels.length;i++){
            deviceRouteLabel = new DeviceRouteLabel();
            deviceRouteLabel.setRouteId(Integer.parseInt(id));
            deviceRouteLabel.setLabelId(labels[i]);
            deviceRouteLabel.setLabelFlag(labelFlag);
            //标记为巡检路线
            deviceRouteLabel.setRouteFlag(routeFlag);
            //判断改标签是否绑定别的路线
            if(drlBiz.selectByLabelIdCount(labelFlag,routeFlag,labels[i])==0)
            drlBiz.insertSelective(deviceRouteLabel);
        }
        DeviceInspectionRoute temp = new DeviceInspectionRoute();
        temp.setId(Integer.parseInt(id));
        temp.setLabelCount(drlBiz.selectByRouteId(Integer.parseInt(id),routeFlag).size()+"");
        baseBiz.updateSelectiveById(temp);
        //修改室内标签状态为已经绑定路线
        dilBiz.updateByIds("1",ids);
        return responseResult;
    }




    @RequestMapping(value = "/deleteIndoorLabel",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("路线删除室内标签")
    public ObjectRestResponse<DeviceInspectionRoute> deleteIndoorLabel(@RequestParam Integer id,String ids,Integer buildingId){
//      默认巡检
        String routeFlag = "0";
        ObjectRestResponse<DeviceInspectionRoute> responseResult =  new ObjectRestResponse<DeviceInspectionRoute>();
        if(ValidatorUtils.hasAnyBlank(id)){
            return responseResult;
        }
        if(buildingId!=null&&buildingId>0){
            String labelIds = SplitUtil.merge(drlBiz.selectByRouteId(id,routeFlag));
            ids = SplitUtil.merge(dilBiz.selectByBuildingIdResultId(buildingId,labelIds,"1",null));
        }
        if(StringUtils.isBlank(ids)){
            return responseResult;
        }

        //删除关联表
        drlBiz.deleteByLabel(id,routeFlag,ids);

        DeviceInspectionRoute entity = new DeviceInspectionRoute();
        entity.setId(id);
        entity.setLabelCount(drlBiz.selectByRouteId(id,routeFlag).size()+"");
        baseBiz.updateSelectiveById(entity);

        //修改室内标签状态为未绑定路线
        dilBiz.updateByIds("0",ids);
        return responseResult;
    }

    @RequestMapping(value = "/getRouteIndoor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取室内已经绑定路线的标签")
    public ObjectRestResponse getRouteIndoor(@RequestParam Integer id) {
        //      默认巡检
        String routeFlag = "0";
        //默认室内
        String labelFlag = "0";
        List<Object> lists = new ArrayList<>();
        String labelIds = SplitUtil.merge(drlBiz.selectByRouteId(id,routeFlag));
        if(StringUtils.isNotBlank(labelIds)){
            List<Integer> buildings = dilBiz.getBuildingId(labelIds,"1",null,routeFlag,labelFlag);
            Set<Integer> set = new HashSet<>();
            List<Integer> deviceBuildings = new ArrayList<>();
            for(int i=0;i<buildings.size();i++){
                if(set.add(buildings.get(i))){
                    deviceBuildings.add(buildings.get(i));
                }
            }
            DeviceBuilding deviceBuilding = null;
            Map<String,Object> map = null;
            for(int i=0;i<deviceBuildings.size();i++){
                map = Maps.newHashMap();
                deviceBuilding = dbBiz.selectById(deviceBuildings.get(i));
                if(deviceBuilding!=null){
                    map.put("buildingId",deviceBuilding.getId());
                    map.put("bName",deviceBuilding.getBName());
                    map.put("label",dilBiz.selectByBuildingId(deviceBuilding.getId(),labelIds,"1",null));
                    lists.add(map);
                }
            }
        }
        return new ObjectRestResponse().data(lists);
    }

    @RequestMapping(value = "/getRouteOutdoor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取室外已经绑定路线的标签")
    public ObjectRestResponse getRouteOutdoor(@RequestParam Integer id) {
        //      默认巡检
        String routeFlag = "0";
        List<Map> lists = new ArrayList<>();
        String labelIds = SplitUtil.merge(drlBiz.selectByRouteId(id,routeFlag));
        if(StringUtils.isNotBlank(labelIds)){
            lists = dolBiz.selectByIdsAndUseFlag(labelIds,"1",null,"0");
        }
        return new ObjectRestResponse().data(lists);
    }

    @RequestMapping(value = "/getAllRouteOutdoor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取室外没有绑定路线的标签,已经打点")
    public ObjectRestResponse getAllRouteOutdoor() {
        return new ObjectRestResponse().data(dolBiz.selectByIdsAndUseFlag(null,"0",null,"0"));
    }

    @RequestMapping(value = "/getAllOutdoorSing",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取室外没有绑定路线的标签,没有打点")
    public ObjectRestResponse getAllOutdoorSing() {
        List<Map> list = dolBiz.selectByIdsAndUseFlag(null,"0",null,"1");
        Map<String,Object> map = new HashMap<>();
        map.put("count",list.size());
        return new ObjectRestResponse().data(map);
    }




    @RequestMapping(value = "/updateOutdoor",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改室外路线")
    public ObjectRestResponse<DeviceInspectionRoute> updateOutdoor(@RequestBody Map<String,Object> params){
        //      默认巡检
        String routeFlag = "0";
        //      默认室外
        String labelFlag = "1";
        ObjectRestResponse<DeviceInspectionRoute> responseResult =  new ObjectRestResponse<DeviceInspectionRoute>();
        String idTemp = (String) params.get("id");
        Integer id = Integer.parseInt(idTemp);
        String ids = (String) params.get("ids");
        DeviceInspectionRoute entity = new DeviceInspectionRoute();
        entity.setId(id);
        entity.setRouteName((String) params.get("routeName"));
        entity.setRouteFlag(labelFlag);
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getRouteName());
        String routeName = entity.getRouteName();
        if(routeName.length()>50){
            throw new RuntimeException("巡检路线长度不能超过50");
        }
        DeviceInspectionRoute deviceInspectionRoute = baseBiz.selectById(id);
        if(!routeName.equalsIgnoreCase(deviceInspectionRoute.getRouteName())&&baseBiz.selectByCount(routeName,null)>0){
            throw new RuntimeException("巡检路线名已存在");
        }
        //获取路线标签id
        Integer[] labels = SplitUtil.splitInt(ids);

        Integer routeId = entity.getId();
        String labelIds = SplitUtil.merge(drlBiz.selectByRouteId(routeId,routeFlag));
        //删除关联表
        DeviceRouteLabel temp = new DeviceRouteLabel();
        temp.setRouteId(routeId);
        temp.setRouteFlag(routeFlag);
        temp.setTenantId(BaseContextHandler.getTenantID());
        drlBiz.delete(temp);
        //连接关联表
        //当室内路线
        if("0".equalsIgnoreCase(labelFlag)){
            //修改之前修改室内标签状态为未绑定路线
            if(StringUtils.isNotBlank(labelIds)){
                dilBiz.updateByIds("0",labelIds);
            }
            DeviceRouteLabel deviceRouteLabel = null;
            for(int i=0;i<labels.length;i++){
                deviceRouteLabel = new DeviceRouteLabel();
                deviceRouteLabel.setRouteId(routeId);
                deviceRouteLabel.setLabelId(labels[i]);
                deviceRouteLabel.setLabelFlag(labelFlag);
                //标记为巡检路线
                deviceRouteLabel.setRouteFlag(routeFlag);
                //判断标签是否绑定路线
                if(drlBiz.selectByLabelIdCount(labelFlag,routeFlag,labels[i])==0)
                drlBiz.insertSelective(deviceRouteLabel);
            }
            //修改室内标签状态为已经绑定路线
            if(StringUtils.isNotBlank(ids)){
                dilBiz.updateByIds("1",ids);
            }
        }else if("1".equalsIgnoreCase(labelFlag)){
            //修改之前室外标签状态为未绑定路线
            if(StringUtils.isNotBlank(labelIds)){
                dolBiz.updateByIds("0",labelIds);
            }
            DeviceRouteLabel deviceRouteLabel = null;
            for(int i=0;i<labels.length;i++){
                deviceRouteLabel = new DeviceRouteLabel();
                deviceRouteLabel.setRouteId(routeId);
                deviceRouteLabel.setLabelId(labels[i]);
                deviceRouteLabel.setLabelFlag(labelFlag);
                //标记为巡检路线
                deviceRouteLabel.setRouteFlag(routeFlag);
                //判断标签是否绑定路线
                if(drlBiz.selectByLabelIdCount(labelFlag,routeFlag,labels[i])==0)
                drlBiz.insertSelective(deviceRouteLabel);
            }
            //修改室外标签状态为已经绑定路线
            if(StringUtils.isNotBlank(ids)){
                dolBiz.updateByIds("1",ids);
            }
        }
        entity.setLabelCount(drlBiz.selectByRouteId(id,routeFlag).size()+"");
        baseBiz.updateSelectiveById(entity);
        return responseResult;
    }


    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除巡检路线前查询")
    public ObjectRestResponse<Object> deleteQuery(@RequestParam String ids){
        //查询路线是否与计划绑定
        List<String> list = new ArrayList<>();
        if(StringUtils.isNotBlank(ids)){
            Integer[] routeIds = SplitUtil.splitInt(ids);
            DeviceInspectionRoute deviceInspectionRoute = null;
            for(int i=0;i<routeIds.length;i++){
                if((disBiz.selectByInspectionRouteId(routeIds[i])).size()>0){
                    //已经与计划绑定
                    deviceInspectionRoute = baseBiz.selectById(routeIds[i]);
                    if(deviceInspectionRoute != null){
                        list.add(deviceInspectionRoute.getRouteName());
                    }
                }
            }
        }
        return new ObjectRestResponse<>().data(list);
    }

    @RequestMapping(value = "/deleteIndoor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("室内删除巡检路线")
    public ObjectRestResponse<Object> deleteIndoor(@RequestParam String ids){
        //      默认巡检
        String routeFlag = "0";
        if(StringUtils.isNotBlank(ids)){
            Integer[] routeIds = SplitUtil.splitInt(ids);
            DeviceInspectionRoute deviceInspectionRoute = null;
            for(int i=0;i<routeIds.length;i++){
                deviceInspectionRoute = new DeviceInspectionRoute();
                deviceInspectionRoute.setId(routeIds[i]);
                deviceInspectionRoute.setDelFlag("1");
                baseBiz.updateSelectiveById(deviceInspectionRoute);
                String labelIds = SplitUtil.merge(drlBiz.selectByRouteId(routeIds[i],routeFlag));
                //删除路线关联标签表
                DeviceRouteLabel deviceRouteLabel = new DeviceRouteLabel();
                deviceRouteLabel.setRouteId(routeIds[i]);
                deviceRouteLabel.setRouteFlag(routeFlag);
                deviceRouteLabel.setTenantId(BaseContextHandler.getTenantID());
                drlBiz.delete(deviceRouteLabel);
                //将室外或者室外标签的是否生成路线标记为未生成
                if(StringUtils.isNotBlank(labelIds)){
                    dilBiz.updateByIds("0",labelIds);
                }
            }

        }
        return new ObjectRestResponse<>();
    }

    @RequestMapping(value = "/deleteOutdoor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("室外删除巡检路线")
    public ObjectRestResponse<Object> deleteOutdoor(@RequestParam String ids){
        //      默认巡检
        String routeFlag = "0";
        if(StringUtils.isNotBlank(ids)){
            Integer[] routeIds = SplitUtil.splitInt(ids);
            DeviceInspectionRoute deviceInspectionRoute = null;
            for(int i=0;i<routeIds.length;i++){
                deviceInspectionRoute = new DeviceInspectionRoute();
                deviceInspectionRoute.setId(routeIds[i]);
                deviceInspectionRoute.setDelFlag("1");
                baseBiz.updateSelectiveById(deviceInspectionRoute);
                String labelIds = SplitUtil.merge(drlBiz.selectByRouteId(routeIds[i],routeFlag));
                //删除路线关联标签表
                DeviceRouteLabel deviceRouteLabel = new DeviceRouteLabel();
                deviceRouteLabel.setRouteId(routeIds[i]);
                deviceRouteLabel.setRouteFlag(routeFlag);
                deviceRouteLabel.setTenantId(BaseContextHandler.getTenantID());
                drlBiz.delete(deviceRouteLabel);
                //将室外或者室外标签的是否生成路线标记为未生成
                if(StringUtils.isNotBlank(labelIds)){
                    dolBiz.updateByIds("0",labelIds);
                }
            }

        }
        return new ObjectRestResponse<>();
    }










}