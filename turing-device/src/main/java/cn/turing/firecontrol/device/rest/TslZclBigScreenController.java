package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.exception.base.BusinessException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.business.BusinessI;
import cn.turing.firecontrol.device.dto.SensorTypeStatus;
import cn.turing.firecontrol.device.entity.DeviceBuilding;
import cn.turing.firecontrol.device.entity.DeviceIndoorLabel;
import cn.turing.firecontrol.device.entity.DeviceMeasuringPoint;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import cn.turing.firecontrol.device.dto.TypeNumDto;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.feign.AdminFeign;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.vo.DateNumVO;
import cn.turing.firecontrol.device.vo.InspectionResultsVO;
import cn.turing.firecontrol.device.vo.LabelnspectionVO;
import cn.turing.firecontrol.device.vo.SensorStatusVO;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private DeviceBuildingBiz deviceBuildingBiz;
    @Autowired
    private DeviceSensorBiz deviceSensorBiz;
    @Autowired
    private DeviceHardwareFacilitiesBiz deviceHardwareFacilitiesBiz;
    @Autowired
    private IUserFeign userFeign;
    @Autowired
    private DeviceIndoorLabelBiz deviceIndoorLabelBiz;
    @Autowired
    private DeviceInspectionRouteBiz  deviceInspectionRouteBiz;
    @Autowired
    private DeviceIndoorRecordInspectionResultsBiz indoorRecordInspectionResultsBiz;
    @Autowired
    private DeviceOutdoorRecordInspectionResultsBiz outdoorRecordInspectionResultsBiz;
    @Autowired
    private DeviceInspectionTasksBiz deviceInspectionTasksBiz;
    @Autowired
    private BusinessI business;
    @Autowired
    private DeviceOutdoorLabelBiz outdoorLabelBiz;
    @Autowired
    private AdminFeign adminFeign;


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

    @ApiOperation("首页：建筑物及设备数量")
    @GetMapping("count/buildingAndDevice")
    public DatavizResponse<Map<String, Object>> buildingAndDeviceCount(String username){
        //查询建筑物数量和面积
        Map<String,Object> map = deviceBuildingBiz.getTotalCountAndArea();
        //查询设备数量
        DeviceSensor sensor = new DeviceSensor();
        sensor.setDelFlag("0");
        Long sensorCount = deviceSensorBiz.selectCount(sensor);
        sensor.setStatus("3");
        sensorCount = sensorCount - deviceSensorBiz.selectCount(sensor);
        map.put("sensorCount",sensorCount);
        //查询设施数量
        DeviceIndoorLabel indoorLabel = new DeviceIndoorLabel();
        indoorLabel.setDelFlag("0");
        Long facilityCount = deviceIndoorLabelBiz.selectCount(indoorLabel);
        map.put("facilityCount",facilityCount);
        return new DatavizResponse<>().data(new Map[]{map});
    }

    @ApiOperation("所有设备及设施的ID和状态")
    @GetMapping("devices/status")
    @CrossOrigin
    public DatavizResponse<List<Map<String,Object>>> queryAllStatus(String username,Integer channelId){
        List<Map<String,Object>> devices = deviceSensorBiz.getAllStatus(channelId);
        List<Map<String,Object>> indoorLabels = new ArrayList<>();
        if(channelId == null || channelId == 10){
            indoorLabels = deviceIndoorLabelBiz.getAllStatus();
            indoorLabels.parallelStream().forEach(label -> {
                if("0".equals(label.get("status").toString())){
                    label.put("status","2");
                }else{
                    label.put("status","0");
                }
                label.put("channelId",10);
            });
        }
        List<Map<String,Object>> statusList = new ArrayList<>();
        statusList.addAll(devices);
        statusList.addAll(indoorLabels);
        return new DatavizResponse<>().data(statusList);
    }

    @ApiOperation("获取设备详情")
    @GetMapping("deviceInfo")
    @CrossOrigin
    public DatavizResponse getDeviceInfo(String username, Integer channelId,Long deviceId){
        if(ValidatorUtils.hasAnyBlank(username,channelId,deviceId)){
            throw new BusinessException("参数不能为空");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("id",deviceId);
        if(channelId == 10){
            DeviceIndoorLabel label = deviceIndoorLabelBiz.getById(Integer.valueOf(deviceId.toString()));
            if(label == null){
                throw new BusinessException("设备不存在");
            }
            map.put("position",label.getPositionDescription());
            map.put("status",label.getStatus());
            map.put("buildingId",label.getBuildingId());
            map.put("sensorNo",label.getFacilitiesNo());
        }else{
            DeviceSensor sensor = deviceSensorBiz.selectById(deviceId);
            if(sensor == null){
                throw new BusinessException("设备不存在");
            }
            map.put("position",sensor.getPositionDescription());
            map.put("status",sensor.getStatus());
            map.put("buildingId",sensor.getBuildingId());
            map.put("sensorNo",sensor.getSensorNo());
        }
        DeviceBuilding building = deviceBuildingBiz.selectById(map.get("buildingId"));
        if(building!=null){
            map.put("bname",building.getBName());
        }

        JSONObject data = business.queryLastData(map.get("sensorNo").toString());
        if(data != null){
            map.put("time",data.getDate("uploadtime"));
            List<DeviceMeasuringPoint> mps = deviceSensorBiz.selectMeasuringPointById(deviceId);
            Map<String,String> values = new TreeMap<>();
            mps.parallelStream().forEach(p -> {
                JSONObject point  = data.getJSONObject(p.getCodeName());
                if(point == null){
                    return;
                }
                String value  = point.getString("alarmValue");
                if(StringUtils.isNotBlank(value)){
                    values.put(p.getMeasuringPoint(),value +p.getDataUnit());
                }
            });
            map.put("values",values);
        }
        return new DatavizResponse().data(map);
    }

    @ApiOperation("巡更巡检：巡检结果分布饼状图")
    @GetMapping("count/inspectionResultPie")
    public DatavizResponse<TypeNumDto> inspectionResultPie(String username){
        //查询累计巡检次数，包括室内和室外
        Long outTotal = outdoorRecordInspectionResultsBiz.selectCount(null);
        //室外漏检（跳过）
        DeviceOutdoorRecordInspectionResults p1=new DeviceOutdoorRecordInspectionResults();
        p1.setLeakFlag("1");
        Long outLeak = outdoorRecordInspectionResultsBiz.selectCount(p1);
        Long outCheck=outTotal-outLeak;
        Long inTotal = indoorRecordInspectionResultsBiz.selectCount(null);
        //室外漏检（跳过）
        DeviceIndoorRecordInspectionResults p2=new DeviceIndoorRecordInspectionResults();
        p2.setLeakFlag("1");
        Long inLeak = indoorRecordInspectionResultsBiz.selectCount(p2);
        Long inCheck=inTotal-inLeak;
        //巡检任务总数
        Long taskTotal = deviceInspectionTasksBiz.selectTaskCount();
        List<TypeNumDto> list=new ArrayList<>();
        if(taskTotal!=0){
            double check = new BigDecimal(1.0 * (outCheck + inCheck) / taskTotal).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
            double leak = new BigDecimal(1.0 * (outLeak + inLeak) / taskTotal).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
            list.add(new TypeNumDto("已巡检",check));
            list.add(new TypeNumDto("跳过",leak));
            list.add(new TypeNumDto("未巡检",1.0-leak-check));
        }else {
            list.add(new TypeNumDto("已巡检",0.0));
            list.add(new TypeNumDto("跳过",0.0));
            list.add(new TypeNumDto("未巡检",0.0));
        }
        return new DatavizResponse<>().data(list);
    }

    @ApiOperation("巡更巡检：巡检结果分布")
    @GetMapping("count/resultStatusCount")
    public DatavizResponse<TypeNumDto> selectStatusResultCount(String username){
        List<TypeNumDto> list = indoorRecordInspectionResultsBiz.selectResultCount();
        return new DatavizResponse<>().data(list);
    }
    @ApiOperation("巡更巡检：近两周巡检结果变化")
    @GetMapping("count/selectResultTrend")
    public DatavizResponse<DateNumVO> selectResultTrend(String username){
        List<DateNumVO> list = indoorRecordInspectionResultsBiz.selectResultTrend();
        return new DatavizResponse<>().data(list);
    }
    @ApiOperation("巡更巡检：近两周巡检异常数据列表")
    @GetMapping("count/selectAbnormalList")
    public DatavizResponse<InspectionResultsVO> selectAbnormalList(String username){
        List<InspectionResultsVO> list = indoorRecordInspectionResultsBiz.selectAbnormalList();
        return new DatavizResponse<>().data(list);
    }
    @ApiOperation("巡更巡检：通过labelId查询设施详情")
    @GetMapping("getInspectionInfoById")
    @CrossOrigin
    public DatavizResponse<LabelnspectionVO> getInspectionInfoById(String username,Integer labelId){
        LabelnspectionVO vo = deviceIndoorLabelBiz.getInspectionInfoById(labelId);
        /*List<LabelnspectionVO> list=new ArrayList<>();
        list.add(vo);*/
        return new DatavizResponse<>().data(vo);
    }
    @ApiOperation("巡更巡检：通过labelId查询历史记录")
    @GetMapping("getInspectionRecords")
    @CrossOrigin
    public DatavizResponse<DeviceIndoorRecordInspectionResults> getInspectionRecord(String username,Integer labelId, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "1") Integer page){
        DeviceIndoorRecordInspectionResults params=new DeviceIndoorRecordInspectionResults();
        params.setLabelId(labelId);
        params.setLeakFlag("0");
        PageHelper.startPage(page,limit);
        List<DeviceIndoorRecordInspectionResults> list = indoorRecordInspectionResultsBiz.selectList(params);
        return new DatavizResponse<>().data(list);
    }

    @ApiOperation("巡更巡检：路线，累计次数")
    @GetMapping("count/inspectionCount")
    public DatavizResponse<TypeNumDto> routeAndRecordCount(String username){

        List<TypeNumDto> list =new ArrayList<>();

        //查询巡检路线个数
        DeviceInspectionRoute param=new DeviceInspectionRoute();
        param.setDelFlag("0");
        Long routeCount = deviceInspectionRouteBiz.selectCount(param);
        list.add(new TypeNumDto("routeCount",routeCount));
        //查询累计巡检次数，包括室内和室外
        Long outTotal = outdoorRecordInspectionResultsBiz.selectCount(null);
        //室外
        DeviceOutdoorRecordInspectionResults p1=new DeviceOutdoorRecordInspectionResults();
        p1.setLeakFlag("0");
        Long outCheck = outdoorRecordInspectionResultsBiz.selectCount(p1);
        //室内
        DeviceIndoorRecordInspectionResults p2=new DeviceIndoorRecordInspectionResults();
        p2.setLeakFlag("0");
        Long inCheck = indoorRecordInspectionResultsBiz.selectCount(p2);
        list.add(new TypeNumDto("totalInspectionCount",outCheck + inCheck));
        return new DatavizResponse<>().data(list);
    }


    @ApiOperation("电气火灾：按照设备系列状态统计")
    @GetMapping("getSensorTypeStatus")
    @CrossOrigin
    public DatavizResponse<SensorStatusVO> getSensorTypeStatus(String username,Integer channelId){
        List<SensorStatusVO> sensorTypeStatus = deviceSensorBiz.getSensorTypeStatus(channelId);
        return new DatavizResponse<>().data(sensorTypeStatus);
    }
    @ApiOperation("首页：设备状态统计")
    @GetMapping("getSensorStatusNum")
    @CrossOrigin
    public DatavizResponse<SensorTypeStatus> getSensorStatusNum(String username){
        List<SensorTypeStatus> sensorTypeStatus = deviceSensorBiz.getSensorStatusNum();
        return new DatavizResponse<>().data(sensorTypeStatus);
    }

    @ApiOperation("电气火灾：监测设备，消防设施")
    @GetMapping("getSensorAndLabelNum")
    @CrossOrigin
    public DatavizResponse<SensorStatusVO> getSensorAndLabelNum(String username,Integer channelId){
        DeviceSensor param=new DeviceSensor();
        param.setDelFlag("0");
        param.setChannelId(channelId);
        Long aLong = deviceSensorBiz.selectCount(param);

        param.setStatus("3");
        Long temp = deviceSensorBiz.selectCount(param);
        List<TypeNumDto> rs=new ArrayList<>();
        rs.add(new TypeNumDto("sensorNum",aLong-temp));
        DeviceIndoorLabel p1=new DeviceIndoorLabel();
        p1.setDelFlag("0");
        Long aLong1 = deviceIndoorLabelBiz.selectCount(p1);
        DeviceOutdoorLabel p2=new DeviceOutdoorLabel();
        p2.setDelFlag("0");
        Long aLong2 = outdoorLabelBiz.selectCount(p2);
        rs.add(new TypeNumDto("labelNum",aLong1+aLong2));
        return new DatavizResponse<>().data(rs);
    }
    @ApiOperation("首页：系统运行情况")
    @GetMapping("getChannelSensorStatusNum")
    @CrossOrigin
    public DatavizResponse<SensorStatusVO> getChannelSensorStatusNum(String username){
        List<SensorStatusVO> list = deviceSensorBiz.getChannelSensorStatusNum();
        return new DatavizResponse<>().data(list);
    }
    @ApiOperation("测试")
    @GetMapping("test")
    @CrossOrigin
    public DatavizResponse<SensorStatusVO> getChannelSensorStatusNum(String username,String channelId){
        ObjectRestResponse feign = adminFeign.getByIds(channelId);
        return new DatavizResponse<>().data(feign);
    }
}
