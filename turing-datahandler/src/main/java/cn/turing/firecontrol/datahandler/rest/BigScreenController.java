package cn.turing.firecontrol.datahandler.rest;

import cn.turing.firecontrol.common.exception.base.BusinessException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.datahandler.biz.DeviceAbnormalBiz;
import cn.turing.firecontrol.datahandler.biz.DeviceFacilitiesAbnormalBiz;
import cn.turing.firecontrol.datahandler.biz.DeviceFireMainAbnormalBiz;
import cn.turing.firecontrol.datahandler.entity.DeviceAbnormal;
import cn.turing.firecontrol.datahandler.entity.DeviceFacilitiesAbnormal;
import cn.turing.firecontrol.datahandler.entity.DeviceFireMainAbnormal;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import cn.turing.firecontrol.datahandler.feign.IUserFeign;
import cn.turing.firecontrol.datahandler.util.ValidatorUtils;
import cn.turing.firecontrol.datahandler.vo.AbnormalDateVo;
import cn.turing.firecontrol.datahandler.vo.AbnormalNumVO;
import cn.turing.firecontrol.datahandler.vo.DeviceAbnormalVo;
import cn.turing.firecontrol.datahandler.vo.TypeNumVO;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description 可视化大屏接口
 * @Author hanyong
 * @Date 2019/07/20 15:38
 * @Version V1.0
 */
@Api(tags = "大屏接口")
@RestController
@RequestMapping("/visual")
public class BigScreenController {

    @Autowired
    private IUserFeign userFeign;
    @Autowired
    private IDeviceFeign deviceFeign;
    @Autowired
    private DeviceAbnormalBiz deviceAbnormalBiz;
    @Autowired
    private DeviceFacilitiesAbnormalBiz deviceFacilitiesAbnormalBiz;
    @Autowired
    private DeviceFireMainAbnormalBiz deviceFireMainAbnormalBiz;

    @ModelAttribute
    public void setContextHandler(@RequestParam String username){
        if(StringUtils.isBlank(username)){
            throw new BusinessException("用户名不能为空");
        }
        JSONObject res = userFeign.getUser(username);
        if(200  != res.getInteger("status")){
            throw new BusinessException("无法获取用户信息");
        }
        JSONObject user = res.getJSONObject("data");
        BaseContextHandler.setTenantID(user.getString("tenantId"));
        BaseContextHandler.setIsTenantAdmin("1");
        BaseContextHandler.setIsSuperAdmin("0");
        BaseContextHandler.setUserID(user.getString("id"));
        BaseContextHandler.setUsername(username);
        BaseContextHandler.setName(user.getString("name"));
    }

    @Data
    public static class DatavizResponse<T>{
        private T value;
        private DatavizResponse data(T value){
            this.value = value;
            return this;
        }
    }

    @ApiOperation("获取设备的最后一条异常记录")
    @GetMapping("lastAbnormal")
    @CrossOrigin
    public DatavizResponse getLastAbnormal(String username,Integer channelId,Long deviceId){
        if(ValidatorUtils.hasAnyBlank(username,channelId,deviceId)){
            throw new BusinessException("参数不能为空");
        }
        Map<String,Object> resMap = new HashMap<>();

        if(channelId == 5){
            PageHelper.startPage(1,1);
            List<DeviceFacilitiesAbnormal> abnormals = deviceFacilitiesAbnormalBiz.selectByEquId(deviceId,null,null,null);
            if(abnormals == null || abnormals.isEmpty()){
                return new DatavizResponse();
            }
            resMap.put("position",abnormals.get(0).getPositionDescription());
            resMap.put("date",abnormals.get(0).getAlrmDate());
            resMap.put("alarmType",abnormals.get(0).getAlrmType());
           // resMap.put("bname",abnormals.get(0));
        }if(channelId == 11){
            PageHelper.startPage(1,1);
            List<DeviceFireMainAbnormal> deviceFireMainAbnormals = deviceFireMainAbnormalBiz.selectByEquId(deviceId, null, null);
            if(deviceFireMainAbnormals == null || deviceFireMainAbnormals.isEmpty()){
                return new DatavizResponse();
            }
            resMap.put("position",deviceFireMainAbnormals.get(0).getPositionDescription());
            resMap.put("date",deviceFireMainAbnormals.get(0).getAlrmDate());
            resMap.put("alarmType",deviceFireMainAbnormals.get(0).getAlrmType());
            resMap.put("bname",deviceFireMainAbnormals.get(0).getbName());
        }else{
            DeviceAbnormal abnormal = deviceAbnormalBiz.selectLast(deviceId);
            if(abnormal == null){
                return new DatavizResponse();
            }
            resMap.put("position",abnormal.getPositionDescription());
            resMap.put("date",abnormal.getAlrmDate());
            resMap.put("alarmType",abnormal.getAlrmType());
            resMap.put("bname",abnormal.getbName());
            JSONObject building = deviceFeign.getBuilding(abnormal.getBuildId()).getJSONObject("data");
            if(building != null){
                resMap.put("linkman",building.getString("linkman"));
                resMap.put("linkphone",building.getString("linkphone"));
            }
        }
        return new DatavizResponse().data(resMap);
    }

    @ApiOperation("当前告警")
    @GetMapping("lastNAbnormal")
    @CrossOrigin
    public DatavizResponse<List<DeviceAbnormalVo>> getLastAbnormal(String username,Integer channelId, Long deviceId,@RequestParam(defaultValue = "1") Integer limit,Integer buildId,Integer floor)   {
        PageHelper.startPage(1,limit);
        List<DeviceAbnormalVo> list =new ArrayList<>();
        if(channelId==null){
            List<DeviceAbnormalVo> deviceAbnormalVos = deviceAbnormalBiz.selectList(channelId, deviceId, buildId, floor);
            if(CollectionUtils.isNotEmpty(deviceAbnormalVos)){
                list.addAll(deviceAbnormalVos);
            }
            List<DeviceAbnormalVo> deviceAbnormalVos1 = deviceFireMainAbnormalBiz.selectList(channelId, deviceId, buildId, floor);
            if(CollectionUtils.isNotEmpty(deviceAbnormalVos1)){
                list.addAll(deviceAbnormalVos1);
            }
            List<DeviceAbnormalVo> deviceAbnormalVos2 = deviceFacilitiesAbnormalBiz.selectList(channelId, deviceId);
            if(CollectionUtils.isNotEmpty(deviceAbnormalVos2)){
                list.addAll(deviceAbnormalVos2);
            }
        }else if(channelId==5){
            list=deviceFacilitiesAbnormalBiz.selectList(channelId, deviceId);

        }else if(channelId==11){
            list=deviceFireMainAbnormalBiz.selectList(channelId, deviceId, buildId, floor);

        }else {
            list=deviceAbnormalBiz.selectList(channelId, deviceId, buildId, floor);
        }

        list.parallelStream().forEach(deviceAbnormal -> {
            if("1".equals(deviceAbnormal.getAlrmCategory())){
                deviceAbnormal.setAlrmCategory("报警");
            }else{
                deviceAbnormal.setAlrmCategory("故障");
            }
        });
        if(list.isEmpty()){
            list.add(new DeviceAbnormalVo());
        }
        return new DatavizResponse().data(list);
    }


    @ApiOperation("当前告警(所有设备)")
    @GetMapping("getLastAbnormalAll")
    @CrossOrigin
    public DatavizResponse<List<DeviceAbnormalVo>> getLastAbnormalAll(String username,Integer channelId,Integer buildId,Integer floor)  {
        //PageHelper.startPage(1,10);
        List<DeviceAbnormalVo> list =  deviceAbnormalBiz.selectList(channelId,null,buildId,floor);
        List<DeviceAbnormalVo> rs=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            Map<Integer, List<DeviceAbnormalVo>> collect = list.stream().collect(Collectors.groupingBy(DeviceAbnormalVo::getSensorId));
            collect.forEach((k,v)->{
                rs.addAll(v.stream().limit(10).collect(Collectors.toList()));
            });
           /* rs.parallelStream().forEach(deviceAbnormal -> {
                if("1".equals(deviceAbnormal.getAlrmCategory())){
                    deviceAbnormal.setAlrmCategory("报警");
                }else{
                    deviceAbnormal.setAlrmCategory("故障");
                }
            });*/
        }
        return new DatavizResponse().data(rs);
    }


    @GetMapping("selectCountNearlyDate")
    @ApiOperation("近7日报警趋势")
    public DatavizResponse selectCountNearlyDate(@RequestParam(defaultValue = "5") Integer count, String username,@RequestParam(required = false) Integer channelId,@RequestParam(required = false)Integer buildId){
        Map<String,Object[]> map = deviceAbnormalBiz.selectCountNearlyDate(count,channelId,buildId);
        Map[] arr = new Map[count];
        map.forEach((key,value) -> {
            for(int i=0;i<value.length;i++){
                if(arr[i] == null){
                    arr[i] = new HashMap();
                }
                arr[i].put(key,value[i]);
            }
        });
        return new DatavizResponse().data(arr);
    }

    @ApiOperation("报警类型统计，首页不需要传channelId")
    @GetMapping("getSensorTypeStatus")
    public DatavizResponse <TypeNumVO> selectFireFlagCount(String username, Integer channelId){
        List<TypeNumVO> list = deviceAbnormalBiz.selectFireFlagCount(channelId);
        if(list.isEmpty()){
            list.add(new TypeNumVO());
        }
        return new DatavizResponse<>().data(list);
    }
    @ApiOperation("首页告警总量只需传username，电气火灾待处理报警传handleFlag=0，和系统id  channelId")
    @GetMapping("getCountByCond")
    public DatavizResponse <TypeNumVO> getCountByCond(String username, String handleFlag,Integer channelId){
        Integer num= deviceAbnormalBiz.getCountByCond(handleFlag,channelId);
        List<TypeNumVO> list=new ArrayList<>();
        list.add(new TypeNumVO("count",num));
        return new DatavizResponse<>().data(list);
    }


    @ApiOperation("首页：警情时段分析")
    @GetMapping("selectCountByWeekAndHour")
    public DatavizResponse<Map<String,Object>> selectCountByWeekAndHour(String username,@RequestParam(defaultValue = "7") Integer count){
        List<Map<String,Object>> list = deviceAbnormalBiz.selectCountByWeekAndHour(count);
        return new DatavizResponse<>().data(list);
    }

    @GetMapping(value = "/getLatestList")
    @ApiOperation("查询最近30s内异常")
    @CrossOrigin
    public ObjectRestResponse<List<DeviceAbnormalVo>> getLatestList(@RequestParam(required = false) Integer channelId){
        List<DeviceAbnormalVo> list = deviceAbnormalBiz.getLatestList(channelId);
        if(CollectionUtils.isNotEmpty(list)){
            for (DeviceAbnormalVo deviceAbnormalVo : list) {
                JSONObject building = deviceFeign.getBuilding(deviceAbnormalVo.getBuildId()).getJSONObject("data");
                if(building != null){
                    deviceAbnormalVo.setLinkman(building.getString("linkman"));
                    deviceAbnormalVo.setLinkphone(building.getString("linkphone"));

                }
            }
        }
        return new ObjectRestResponse().data(list);
    }

    @ApiOperation("四川农信/省级备份：今日累计报警次数，故障次数，处理次数")
    @GetMapping("getAlrmNumByDate")
    public DatavizResponse <List<TypeNumVO>> getAlrmNumByDate(String username){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateTime now = DateTime.now();
        Date next = now.plusDays(1).toDate();
        List<TypeNumVO> list = deviceAbnormalBiz.getAlrmNumByDate(format.format(now.toDate()), format.format(next));
        return new DatavizResponse<>().data(list);
    }

    @ApiOperation("四川农信/建筑备份：报警待处理，故障待处理，故障处理情况，报警处理情况")
    @GetMapping("countNumByCategoryAndHandle")
    public DatavizResponse <List<TypeNumVO>> countNumByCategoryAndHandle(String username,@RequestParam(required = false) Integer channelId,@RequestParam(required = false) Integer buildId){
        List<TypeNumVO> list = deviceAbnormalBiz.countNumByCategoryAndHandle(buildId, channelId);
        return new DatavizResponse<>().data(list);
    }

    @ApiOperation("四川农信：近一个月系统概况、近一个月报警曲线")
    @GetMapping("countNumMonth")
    public DatavizResponse <List<AbnormalNumVO>> countNumMonth(String username,@RequestParam(required = false) Integer equId,@RequestParam(required = false) Integer buildId){
        List<AbnormalNumVO> list = deviceAbnormalBiz.countNumMonth(equId, buildId);
        return new DatavizResponse<>().data(list);
    }
    @ApiOperation("四川农信：近一个月系统概况、近一个月报警曲线")
    @CrossOrigin
    @GetMapping("countNumMonthNew")
    public DatavizResponse<List<AbnormalDateVo>> countNumMonthNew(String username,@RequestParam(required = false) Integer equId,@RequestParam(required = false) Integer buildId){
        //此接口是为了适应配合前端的数据结构
        List<AbnormalNumVO> list = deviceAbnormalBiz.countNumMonth(equId, buildId);
        List<AbnormalDateVo> rs=new ArrayList<>();
        for (AbnormalNumVO v : list) {
            AbnormalDateVo m=new AbnormalDateVo();
            List<TypeNumVO> typeNumVOS=new ArrayList<>();
            typeNumVOS.add(new TypeNumVO("故障",v.getFaultNum()));
            typeNumVOS.add(new TypeNumVO("报警",v.getFireNum()));
            m.setAlrmDate(v.getAlrmDate());
            m.setList(typeNumVOS);
            rs.add(m);
        }

        return new DatavizResponse<>().data(rs);
    }

    @ApiOperation("四川农信：近一个月系统概况、近一个月报警曲线（所有）")
    @GetMapping("countNumMonthAll")
    public DatavizResponse <List<TypeNumVO>> countNumMonthAll(String username,@RequestParam(required = false) Integer equId,@RequestParam(required = false) Integer buildId){
        List<Integer> ids = deviceAbnormalBiz.getEquId();
        List<Map<String,Object>> rs=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(ids)){
            ids.parallelStream().forEach(id->{
                Map<String,Object> m=new HashMap<>();
                m.put("id",id);
                m.put("list",deviceAbnormalBiz.countNumMonth(equId, buildId));
                rs.add(m);
            });
        }
        //List<AbnormalNumVO> list = deviceAbnormalBiz.countNumMonth(equId, buildId);
        return new DatavizResponse<>().data(rs);
    }














}
