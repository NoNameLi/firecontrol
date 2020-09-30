package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;

import cn.turing.firecontrol.device.biz.DeviceInspectionRouteBiz;
import cn.turing.firecontrol.device.biz.DeviceInspectionSchemeBiz;
import cn.turing.firecontrol.device.biz.DeviceInspectionTimeBiz;
import cn.turing.firecontrol.device.biz.DeviceNoticeBiz;
import cn.turing.firecontrol.device.entity.DeviceInspectionRoute;
import cn.turing.firecontrol.device.entity.DeviceInspectionScheme;
import cn.turing.firecontrol.device.entity.DeviceInspectionTime;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.util.MapUtils;
import cn.turing.firecontrol.device.util.SplitUtil;
import cn.turing.firecontrol.device.util.TrimUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("deviceInspectionScheme")
@CheckClientToken
@CheckUserToken
@Api(tags = "巡检计划")
public class DeviceInspectionSchemeController extends BaseController<DeviceInspectionSchemeBiz,DeviceInspectionScheme,Integer> {
    private  static final Logger log = LoggerFactory.getLogger(BusinessController.class);
    @Autowired
    private DeviceInspectionTimeBiz ditBiz;
    @Autowired
    private DeviceInspectionRouteBiz dirBiz;
    @Autowired
    private IUserFeign iUserFeign;


    @ApiOperation("巡检计划分页获取数据,按条件搜索数据")
    @RequestMapping(value = "/pageList", method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "当前显示条数", paramType = "query"),
            @ApiImplicitParam(name = "routeName", value = "路线名", paramType = "query"),
            @ApiImplicitParam(name = "patrolCycle", value = "周期", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "查询开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "查询结束时间", paramType = "query")
    })
    public TableResultResponse<Map> pageList(@RequestParam String page, @RequestParam String limit, String routeName, String patrolCycle, String startTime, String endTime) {
        //查询列表数据
        String startTimeFirst = null;
        String startTimeLast = null;
        String endTimeFirst = null;
        String endTimeLast = null;
        Map<String, Object> params = new LinkedHashMap();
        Integer temp = null;
        if(StringUtils.isNotBlank(patrolCycle)){
            try {
                temp = Integer.parseInt(patrolCycle);
            }catch (Exception e){
                return new TableResultResponse<>();
            }
        }
        if(StringUtils.isNotBlank(startTime)&&!"null".equalsIgnoreCase(startTime)){
            String [] tempStart =  startTime.split(",");
            if(tempStart.length==2){
                startTimeFirst = tempStart[0];
                startTimeLast = tempStart[1];
            }
        }
        if(StringUtils.isNotBlank(endTime)&&!"null".equalsIgnoreCase(endTime)){
            String [] tempEndTime = endTime.split(",");
            if(tempEndTime.length==2){
                endTimeFirst = tempEndTime[0];
                endTimeLast = tempEndTime[1];
            }
        }
        params.put("page", page);
        params.put("limit", limit);
        Query query = new Query(params);
        return baseBiz.selectPageList(query, routeName, temp, startTimeFirst, startTimeLast, endTimeFirst, endTimeLast);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加巡检计划")
    public ObjectRestResponse<DeviceInspectionScheme> add(@RequestBody Map<String, Object> param) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Integer inspectionRouteId = null;
        String startTime = null;
        String endTime = null;
        Integer patrolCycle = null;
        try{
            inspectionRouteId = (Integer) param.get("inspectionRouteId");
            startTime = (String) param.get("startTime");
            endTime = (String) param.get("endTime");
        }catch (Exception e){
            return new ObjectRestResponse<>();
        }
        //前端传文字
        try {
            patrolCycle = Integer.parseInt((String)param.get("patrolCycle"));
            if(patrolCycle>999){
                throw new RuntimeException("周期只允许输入不超过3位正整数");
            }
        }catch (Exception e){
            throw new RuntimeException("周期只允许输入不超过3位正整数");
        }
        List<List> timePeriods = (List<List>) param.get("timePeriod");
        List<String> timePeriod = new ArrayList<>();
        for(int i=0;i<timePeriods.size();i++){
            if(timePeriods.get(i)!=null&&timePeriods.get(i).size()==2){
                timePeriod.add(timePeriods.get(i).get(0)+"-"+timePeriods.get(i).get(1));
            }
        }
        DeviceInspectionScheme entity = new DeviceInspectionScheme();
        entity.setInspectionRouteId(inspectionRouteId);
        entity.setPatrolCycle(patrolCycle);
        entity.setStartTime(dateFormat.parse(startTime));
        if(StringUtils.isNotBlank(endTime)){
            entity.setEndTime(dateFormat.parse(endTime));
        }
        //获取已经绑定计划的路线ID
        List<Integer> routeIds = baseBiz.getAllInspectionTouteId(null);
        if(routeIds.contains(inspectionRouteId)){
            throw new RuntimeException("该路线已经与计划绑定");
        }
        baseBiz.insertSelective(entity);
        try {
            DeviceInspectionTime deviceInspectionTime = null;
            for (int i = 0; i < timePeriod.size(); i++) {
                deviceInspectionTime = new DeviceInspectionTime();
                deviceInspectionTime.setInspectionSchemeId(entity.getId());
                deviceInspectionTime.setInspectionTime(timePeriod.get(i));
                ditBiz.insertSelective(deviceInspectionTime);
            }
        }catch (Exception e){
            log.info("巡检时段参数错误");
        }
        //设置巡检时段个数
        DeviceInspectionScheme temp = new DeviceInspectionScheme();
        temp.setId(entity.getId());
        temp.setTimeCount(ditBiz.selectBySchemeId(entity.getId(),BaseContextHandler.getTenantID()).size()+"");
        baseBiz.updateSelectiveById(temp);
        return new ObjectRestResponse<>();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改巡检计划")
    public ObjectRestResponse<DeviceInspectionScheme> update(@RequestBody Map<String, Object> param) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Integer id = null;
        Integer patrolCycle = null;
        Integer inspectionRouteId = null;
        String startTime = null;
        String endTime = null;
        try{
            inspectionRouteId = (Integer) param.get("inspectionRouteId");
            startTime = (String) param.get("startTime");
            endTime = (String) param.get("endTime");
            id = (Integer) param.get("id");
        }catch (Exception e){
            return new ObjectRestResponse<>();
        }
        try {
            patrolCycle = Integer.parseInt((String) param.get("patrolCycle"));
            if(patrolCycle>999){
                throw new RuntimeException("周期只允许输入不超过3位整数");
            }
        }catch (Exception e){
            throw new RuntimeException("周期只允许输入不超过3位整数");
        }
        List<List> timePeriods = (List<List>) param.get("timePeriod");
        List<String> timePeriod = new ArrayList<>();
        for(int i=0;i<timePeriods.size();i++){
            if(timePeriods.get(i)!=null&&timePeriods.get(i).size()==2){
                timePeriod.add(timePeriods.get(i).get(0)+"-"+timePeriods.get(i).get(1));
            }
        }
        DeviceInspectionScheme entity = baseBiz.selectById(id);
        entity.setInspectionRouteId(inspectionRouteId);
        entity.setPatrolCycle(patrolCycle);
        entity.setStartTime(dateFormat.parse(startTime));
        if(StringUtils.isNotBlank(endTime)){
            entity.setEndTime(dateFormat.parse(endTime));
        }else {
            entity.setEndTime(null);
        }
        //获取已经绑定计划的路线ID
        List<Integer> routeIds = baseBiz.getAllInspectionTouteId(id);
        if(routeIds.contains(inspectionRouteId)){
            throw new RuntimeException("该路线已经与计划绑定");
        }
        baseBiz.updateById(entity);
        //删除之前绑定的时段
        DeviceInspectionTime temp = new DeviceInspectionTime();
        temp.setInspectionSchemeId(id);
        temp.setTenantId(BaseContextHandler.getTenantID());
        ditBiz.delete(temp);
        try {
            DeviceInspectionTime deviceInspectionTime = null;
            for (int i = 0; i < timePeriod.size(); i++) {
                deviceInspectionTime = new DeviceInspectionTime();
                deviceInspectionTime.setInspectionSchemeId(entity.getId());
                deviceInspectionTime.setInspectionTime(timePeriod.get(i));
                ditBiz.insertSelective(deviceInspectionTime);
            }
        }catch (Exception e){
            log.info("巡检时段参数错误");
        }
        //设置巡检时段个数
        DeviceInspectionScheme temps = new DeviceInspectionScheme();
        temps.setId(id);
        temps.setTimeCount(ditBiz.selectBySchemeId(entity.getId(),BaseContextHandler.getTenantID()).size()+"");
        baseBiz.updateSelectiveById(temps);
        return new ObjectRestResponse<>();
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("添加和修改前查询")
    public ObjectRestResponse get(Integer id) {
        //获取已经绑定计划的路线ID
        List<Integer> routeIds = baseBiz.getAllInspectionTouteId(null);
        DeviceInspectionScheme deviceInspectionScheme = null;
        Map<String,Object> map = Maps.newHashMap();
        LinkedList<Map> lists = new LinkedList<>();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (routeIds != null && routeIds.size() > 0) {
            lists.addAll(dirBiz.getNotIds(SplitUtil.merge(routeIds), null));
        }else {
            lists.addAll(dirBiz.getNotIds(null, null));
        }

//        编辑
        if (id != null) {
            deviceInspectionScheme = baseBiz.selectById(id);
            if(deviceInspectionScheme!=null){
                List<Map> temp = dirBiz.getNotIds(null, deviceInspectionScheme.getInspectionRouteId());
                if (temp != null && temp.size() == 1) {
                    lists.addFirst(temp.get(0));
                }
                deviceInspectionScheme.getStartTime();
                Map tempMap = MapUtils.convertObjToMap(deviceInspectionScheme);
                tempMap.put("patrolCycle",deviceInspectionScheme.getPatrolCycle()+"");
                map.put("inspectionScheme",tempMap);
                //巡检时段
                String tenantId = null;
                if(!iUserFeign.isSuperAdmin(BaseContextHandler.getUserID())){
                    tenantId = BaseContextHandler.getTenantID();
                }
                List<DeviceInspectionTime> times = ditBiz.selectBySchemeId(deviceInspectionScheme.getId(),tenantId);
                List<List> timePeriods = new ArrayList<>();
                for(int i=0;i<times.size();i++){
                    String timeStr = times.get(i).getInspectionTime();
                    List list = new ArrayList();
                    list.add(timeStr.split("-")[0]);
                    list.add(timeStr.split("-")[1]);
                    timePeriods.add(list);
                }
                map.put("times",timePeriods);
            }
        }
        map.put("routes", lists);
        return new ObjectRestResponse<>().data(map);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除")
    public ObjectRestResponse delete(@RequestParam String ids) {
        if(StringUtils.isNotBlank(ids)){
            Integer[] temp = SplitUtil.splitInt(ids);
            for(int i=0;i<temp.length;i++){
                DeviceInspectionScheme entity = new DeviceInspectionScheme();
                entity.setId(temp[i]);
                entity.setDelFlag("1");
                baseBiz.updateSelectiveById(entity);
            }
        }
        return new ObjectRestResponse<>();
    }









}

