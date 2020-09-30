package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.DeviceVideoAbnormalDataBiz;
import cn.turing.firecontrol.device.util.DateUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/02/28 14:30
 *
 * @Description
 * @Version V1.0
 */
@RestController
@RequestMapping("videoAbnormal")
@Api(tags = "视频异常记录")
@CheckClientToken
@CheckUserToken
@Slf4j
public class DeviceVideoAbnormalDataController {

    @Autowired
    private DeviceVideoAbnormalDataBiz deviceVideoAbnormalDataBiz;


    //所有报警类型列表查询
    @GetMapping("alarmTypes")
    @ApiOperation("所有报警类型列表查询")
    public ObjectRestResponse alarmTypes(){
        try{
            List<String> types = deviceVideoAbnormalDataBiz.queryAlarmTypes(BaseContextHandler.getTenantID());
            return new ObjectRestResponse().data(types);
        }catch (Exception e){
            log.error("报警类型查询异常",e);
            throw new ParamErrorException(e.getMessage());
        }
    }


    //所有设备系列列表查询
    @GetMapping("deviceSerials")
    @ApiOperation("所有设备系列列表查询")
    public ObjectRestResponse deviceSerials(){
        try{
            List<String> types = deviceVideoAbnormalDataBiz.queryDeviceSerials(BaseContextHandler.getTenantID());
            return new ObjectRestResponse().data(types);
        }catch (Exception e){
            log.error("报警类型查询异常",e);
            throw new ParamErrorException(e.getMessage());
        }
    }


    private Map<String,Date> getTime(String dateStr){
        Date startTime = null,endTime = null,now = new Date();
        if(StringUtils.isBlank(dateStr)){
            startTime = null;
            endTime = null;
        }else {
            String[] timeStrs = dateStr.split(",");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                startTime = dateFormat.parse(timeStrs[0]);
                endTime = dateFormat.parse(timeStrs[1]);
            } catch (ParseException e) {
                log.error("日期格式错误");
                throw new ParamErrorException("日期格式错误");
            }
        }
        Map<String,Date> map = new HashMap<>();

        map.put("startTime",startTime == null ? null : DateUtil.getStartTime(startTime));
        map.put("endTime", endTime == null ? null : DateUtil.getEndTime(endTime));
        return map;
    }

    @GetMapping("queryList")
    @ApiOperation("查询异常记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码（默认1）", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页显示条数（默认10）", paramType = "query"),
            @ApiImplicitParam(name = "hasRestore", value = "是否已恢复", paramType = "query"),
            @ApiImplicitParam(name = "alarmType", value = "报警类型", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "报警时间启始值", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "报警时间终点值", paramType = "query"),
            @ApiImplicitParam(name = "deviceName", value = "设备名称（模糊查询）", paramType = "query"),
            @ApiImplicitParam(name = "sensorNo", value = "设备编号（模糊查询）", paramType = "query"),
            @ApiImplicitParam(name = "deviceSerial", value = "设备系列", paramType = "query")
    })
    public TableResultResponse<JSONObject> queryList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "15") Integer limit,
                                                     @RequestParam(defaultValue = "false") Boolean hasRestore, String alarmType, String dateStrs,
                                                     String deviceName, String sensorNo, String deviceSerial){
        Map<String,Date> dates = getTime(dateStrs);
        Date startTime = dates.get("startTime");
        Date endTime = dates.get("endTime");
        String tenantId = BaseContextHandler.getTenantID();
        return deviceVideoAbnormalDataBiz.queryByPage(tenantId,page,limit,hasRestore,alarmType,startTime,endTime,deviceName,sensorNo,deviceSerial);
    }

    @GetMapping("listHistoryEvent")
    @ApiOperation("设备历史事件列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码（默认1）", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页显示条数（默认15）", paramType = "query"),
            @ApiImplicitParam(name = "sensorNo", value = "设备编号", paramType = "query")
    })
    public TableResultResponse<JSONObject> listHistoryEvent(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "15") Integer limit,
                                                            @RequestParam String sensorNo){
        String tenantId = BaseContextHandler.getTenantID();
        return deviceVideoAbnormalDataBiz.listHistoryEvent(tenantId,page,limit,sensorNo);
    }

    @GetMapping("getAlarmCount")
    @ApiOperation("查询视频设备的报警数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sensorNo", value = "设备编号", paramType = "query")
    })
    public ObjectRestResponse getAlarmCount(@RequestParam String sensorNo){
        ObjectRestResponse response = new ObjectRestResponse();
        String tenantId = BaseContextHandler.getTenantID();
        response.data(deviceVideoAbnormalDataBiz.getAlarmCount(tenantId,sensorNo));
        return response;
    }

}
