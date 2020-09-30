package cn.turing.firecontrol.datahandler.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.datahandler.biz.SensorAbnormalBiz;
import cn.turing.firecontrol.datahandler.util.DateUtil;
import cn.turing.firecontrol.datahandler.util.ValidatorUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/03/11 10:05
 *
 * @Description TODO
 * @Version V1.0
 */
@RestController
@RequestMapping("sensorAbnormal")
@CheckUserToken
@CheckClientToken
@Api(tags = "传感器异常记录")
@Slf4j
public class SensorAbnormalController {

    @Autowired
    private SensorAbnormalBiz sensorAbnormalBiz;

    //所有报警类型列表查询
    @GetMapping("alarmTypes")
    @ApiOperation("所有报警类型列表查询")
    public ObjectRestResponse alarmTypes(){
        try{
            List<String> types = sensorAbnormalBiz.queryAlarmTypes(BaseContextHandler.getTenantID());
            return new ObjectRestResponse().data(types);
        }catch (Exception e){
            log.error("报警类型查询异常",e);
            throw new ParamErrorException(e.getMessage());
        }
    }

    //分页查询该租户的所有未处理报警或故障
    @ApiOperation("查询异常列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "abnormalType",value = "异常类型：0故障，1报警，2正常",paramType = "query"),
            @ApiImplicitParam(name = "isHandle",value = "是否已处理",paramType = "query"),
            @ApiImplicitParam(name = "alarmType",value = "报警类型",paramType = "query"),
            @ApiImplicitParam(name = "startAlarmTime",value = "报警时间-开始",paramType = "query"),
            @ApiImplicitParam(name = "endAlarmTime",value = "报警时间-结束",paramType = "query"),
            @ApiImplicitParam(name = "buildingName",value = "建筑物名称",paramType = "query"),
            @ApiImplicitParam(name = "sensorNo",value = "传感器编号",paramType = "query"),
            @ApiImplicitParam(name = "floor",value = "楼层数",paramType = "query"),
            @ApiImplicitParam(name = "manufacturer",value = "厂商名称",paramType = "query"),
            @ApiImplicitParam(name = "deviceSeries",value = "设备系列",paramType = "query"),
            @ApiImplicitParam(name = "model",value = "设备型号",paramType = "query"),
            @ApiImplicitParam(name = "handlePersonName",value = "处理人真实姓名",paramType = "query"),
            @ApiImplicitParam(name = "startRestoreTime",value = "恢复时间-开始",paramType = "query"),
            @ApiImplicitParam(name = "endRestoreTime",value = "恢复时间-结束",paramType = "query")
    })
    @GetMapping("pageList")
    public TableResultResponse pageList(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "15")Integer limit, Integer abnormalType, Boolean isHandle,
                                             String alarmType, Date startAlarmTime, Date endAlarmTime, String buildingName, String sensorNo,
                                             Integer floor, String manufacturer, String deviceSeries, String model,
                                             String handlePersonName, Date startRestoreTime, Date endRestoreTime){
        try {
            return sensorAbnormalBiz.queryPage(page,limit,abnormalType,isHandle,alarmType,startAlarmTime,endAlarmTime,buildingName,sensorNo,floor,manufacturer,deviceSeries,
                    model,handlePersonName,startRestoreTime,endRestoreTime);
        }catch (Exception e){
            log.error("查询异常列表",e);
            throw new ParamErrorException(e.getMessage());
        }

    }


    @ApiOperation("处理异常")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "异常ID",paramType = "query"),
            @ApiImplicitParam(name = "handleResult",value = "处理结果:0故障，1真实火警，2火警测试，3误报",paramType = "query")
    })
    @PostMapping("handleAbnormal")
    public ObjectRestResponse handleAbnormal(String id, Integer handleResult){
        if(ValidatorUtils.hasAnyBlank(id,handleResult)){
            throw new ParamErrorException("参数不能为空");
        }
        String name = BaseContextHandler.getName();
        try{
            sensorAbnormalBiz.handleAbnormal(id,handleResult,name);
        }catch (Exception e){
            log.error("处理异常",e);
            throw new ParamErrorException(e.getMessage());
        }
        return new ObjectRestResponse();
    }

    private Map<String,Date> getStartAndEndTime(Integer countDownDay,Integer lastDays){
        Date time = new Date(),startTime = null,endTime = null;
        if(countDownDay == null && lastDays == null){
            startTime = DateUtil.getStartTime(time);
            endTime = DateUtil.getEndTime(time);
        } else if(countDownDay != null){
            time = new Date(time.getTime() - (countDownDay -1)*24*60*60*1000L);
            startTime = DateUtil.getStartTime(time);
            endTime = DateUtil.getEndTime(time);
        } else if(lastDays != null){
            endTime = DateUtil.getEndTime(time);
            time = new Date(time.getTime() - (lastDays -1)*24*60*60*1000L);
            startTime = DateUtil.getStartTime(time);
        }
        Map<String,Date> map = Maps.newHashMap();
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        return map;
    }


    @ApiOperation("一段时间内的隐患处理情况统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "countDownDay",value = "倒数第N天（今天为倒数第一天）",paramType = "query"),
            @ApiImplicitParam(name = "lastDays",value = "最近N天（包括今天）",paramType = "query")
    })
    @GetMapping("handleStatus")
    public ObjectRestResponse handleStatus(Integer countDownDay,Integer lastDays){
        Map<String,Date> times = getStartAndEndTime(countDownDay,lastDays);
        Date startTime = times.get("startTime");
        Date endTime = times.get("endTime");
        try{
            Map<String, Object> data = sensorAbnormalBiz.getHandleStatus(BaseContextHandler.getTenantID(),startTime,endTime);
            return new ObjectRestResponse().data(data);
        }catch (Exception e){
            log.error("隐患处理情况统计失败",e);
            throw new ParamErrorException(e.getMessage());
        }
    }

    @ApiOperation("一段时间内的异常类型情况统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "countDownDay",value = "倒数第N天（今天为倒数第一天）",paramType = "query"),
            @ApiImplicitParam(name = "lastDays",value = "最近N天（包括今天）",paramType = "query")
    })
    @GetMapping("alarmTypeStatus")
    public ObjectRestResponse alarmTypeStatus(Integer countDownDay,Integer lastDays){
        Map<String,Date> times = getStartAndEndTime(countDownDay,lastDays);
        Date startTime = times.get("startTime");
        Date endTime = times.get("endTime");
        try{
            Map<String, Object> data = sensorAbnormalBiz.getAlarmTypeStatus(BaseContextHandler.getTenantID(),startTime,endTime);
            return new ObjectRestResponse().data(data);
        }catch (Exception e){
            log.error("隐患处理情况统计失败",e);
            throw new ParamErrorException(e.getMessage());
        }
    }



}
