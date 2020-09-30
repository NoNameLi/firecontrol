package cn.turing.firecontrol.datahandler.rest;

import cn.turing.firecontrol.common.exception.base.BusinessException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.datahandler.biz.DeviceAbnormalBiz;
import cn.turing.firecontrol.datahandler.biz.DeviceFacilitiesAbnormalBiz;
import cn.turing.firecontrol.datahandler.entity.DeviceAbnormal;
import cn.turing.firecontrol.datahandler.entity.DeviceFacilitiesAbnormal;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import cn.turing.firecontrol.datahandler.feign.IUserFeign;
import cn.turing.firecontrol.datahandler.util.ValidatorUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @Description 特斯联中储粮大屏接口
 * @Author hanyong
 * @Date 2019/07/20 15:38
 * @Version V1.0
 */
@Api(tags = "特斯联中储粮大屏接口")
@RestController
@RequestMapping("tsl/zcl")
public class TslZclBigScreenController {

    @Autowired
    private IUserFeign userFeign;
    @Autowired
    private IDeviceFeign deviceFeign;
    @Autowired
    private DeviceAbnormalBiz deviceAbnormalBiz;
    @Autowired
    private DeviceFacilitiesAbnormalBiz deviceFacilitiesAbnormalBiz;

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

        if(channelId == 10){
            PageHelper.startPage(1,1);
            List<DeviceFacilitiesAbnormal> abnormals = deviceFacilitiesAbnormalBiz.selectByEquId(deviceId,null,null,null);
            if(abnormals == null || abnormals.isEmpty()){
                return new DatavizResponse();
            }
            resMap.put("position",abnormals.get(0).getPositionDescription());
            resMap.put("date",abnormals.get(0).getAlrmDate());
            resMap.put("alarmType",abnormals.get(0).getAlrmType());
            resMap.put("bname",abnormals.get(0));
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

    @ApiOperation("获取设备的最后N条异常记录")
    @GetMapping("lastNAbnormal")
    @CrossOrigin
    public DatavizResponse getLastAbnormal(String username,Integer channelId, Long deviceId,@RequestParam(defaultValue = "1") Integer limit) throws InvocationTargetException, IllegalAccessException {
        PageHelper.startPage(1,limit);
        List<DeviceAbnormalVo> list =  deviceAbnormalBiz.selectList(channelId,deviceId,null,null);
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


    @GetMapping("selectCountNearlyDate")
    @ApiOperation("查询最近N个月各状态（火警，故障，误报）的数量")
    public DatavizResponse selectCountNearlyDate(@RequestParam(defaultValue = "5") Integer count, String username,Integer channelId){
        Map<String,Object[]> map = deviceAbnormalBiz.selectCountNearlyDate(count,channelId,null);
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













}
