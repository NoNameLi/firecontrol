package cn.turing.firecontrol.datahandler.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.datahandler.biz.NoticeRuleSensorBiz;
import cn.turing.firecontrol.datahandler.entity.NoticeRuleSensor;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created on 2019/03/18 10:29
 *
 * @Description TODO
 * @Version V1.0
 */
@RestController
@RequestMapping("noticeRuleSensor")
@CheckClientToken
@CheckUserToken
@Api(tags = "通知推送规则与传感器关联关系")
public class NoticeRuleSensorController extends BaseController<NoticeRuleSensorBiz, NoticeRuleSensor,Long> {

    @RequestMapping(value = "/listBindedSensor",method = RequestMethod.GET)
    @ApiOperation("查询推送规则已关联的传感器列表(非消防主机)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",paramType = "query"),
            @ApiImplicitParam(name="limit",value = "每页数量",paramType = "query"),
            @ApiImplicitParam(name="manufacturer",value = "厂商",paramType = "query"),
            @ApiImplicitParam(name="equipmentType",value = "序列",paramType = "query"),
            @ApiImplicitParam(name="model",value = "型号",paramType = "query"),
            @ApiImplicitParam(name="sensorNo",value = "传感器编号",paramType = "query"),
            @ApiImplicitParam(name="channelId",value = "传感器编号",paramType = "query"),
    })
    public TableResultResponse listBindedSensor(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "15") Integer limit,
                                                @RequestParam Long noticeRuleId,
                                                String manufacturer, String equipmentType, String model, String sensorNo){

          return this.baseBiz.listBindedSensor(page,limit,noticeRuleId,manufacturer,equipmentType,model,sensorNo);
    }

    @RequestMapping(value = "/listFireMainBindedSensor",method = RequestMethod.GET)
    @ApiOperation("查询推送规则已关联的传感器列表(非消防主机)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",paramType = "query"),
            @ApiImplicitParam(name="limit",value = "每页数量",paramType = "query"),
            @ApiImplicitParam(name="noticeRuleId",value = "推送规则ID",paramType = "query"),
            @ApiImplicitParam(name="buildingId",value = "建筑ID",paramType = "query"),
            @ApiImplicitParam(name="channelId",value = "所属系统ID",paramType = "query"),
            @ApiImplicitParam(name="series",value = "系列",paramType = "query"),
            @ApiImplicitParam(name="serverIp",value = "服务器IP",paramType = "query"),
            @ApiImplicitParam(name="port",value = "端口",paramType = "query"),
            @ApiImplicitParam(name="sensorLoop",value = "回路",paramType = "query"),
            @ApiImplicitParam(name="address",value = "地址",paramType = "query")
    })
    public TableResultResponse listFireMainBindedSensor(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "15") Integer limit,
                                                @RequestParam Long noticeRuleId,
                                                Integer buildingId, Integer channelId,
                                                String series, String serverIp,
                                                String port, String sensorLoop,
                                                String address){

        return this.baseBiz.listFireMainBindedSensor(page,limit,noticeRuleId,buildingId,channelId,series,serverIp,port,sensorLoop,address);
    }

    @RequestMapping(value = "/listFireMainUnBindedSensor",method = RequestMethod.GET)
    @ApiOperation("查询推送规则已关联的传感器列表(消防主机)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",paramType = "query"),
            @ApiImplicitParam(name="limit",value = "每页数量",paramType = "query"),
            @ApiImplicitParam(name="noticeRuleId",value = "推送规则ID",paramType = "query"),
            @ApiImplicitParam(name="buildingId",value = "建筑ID",paramType = "query"),
            @ApiImplicitParam(name="code",value = "所属区域编码",paramType = "query"),
            @ApiImplicitParam(name="channelId",value = "所属系统ID",paramType = "query")
    })
    public TableResultResponse listFireMainUnBindedSensor(@RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "15") Integer limit,
                                                        @RequestParam Long noticeRuleId,
                                                        Integer buildingId, Integer channelId,
                                                        String code){

        return this.baseBiz.listFireMainUnBindedSensor(page,limit,noticeRuleId,buildingId,channelId,code);
    }

    /**
     * 适用于电气火灾、无线感烟、可燃气体、防火门等室内系统
     * @param pageNo
     * @param limit
     * @param buildingId
     * @param channelId
     * @return
     */
    @RequestMapping(value = "/listUnBindedSensorForIndoorSystem",method = RequestMethod.GET)
    @ApiOperation("查询推送规则未关联的传感器列表(非消防主机)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="pageNo",value = "页码",paramType = "query"),
            @ApiImplicitParam(name="limit",value = "每页数量",paramType = "query"),
            @ApiImplicitParam(name="buildingId",value = "建筑ID",paramType = "query"),
            @ApiImplicitParam(name="channelId",value = "栏目ID",paramType = "query")
    })
    public TableResultResponse listUnBindedSensorForIndoorSystem(@RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "15") Integer limit,
                                                Long buildingId, Long channelId, String code){

        return this.baseBiz.listUnBindedSensorForIndoorSystem(pageNo,limit,buildingId,channelId,code);
    }

    /**
     * 适用于危险预警等设备内嵌系统
     * @param page
     * @param limit
     * @param groupId
     * @param channelId
     * @return
     */
    @RequestMapping(value = "/listUnBindedSensorForNestedSystem",method = RequestMethod.GET)
    @ApiOperation("查询推送规则未关联的传感器列表(非消防主机)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",paramType = "query"),
            @ApiImplicitParam(name="limit",value = "每页数量",paramType = "query"),
            @ApiImplicitParam(name="groupId",value = "建筑ID",paramType = "query"),
            @ApiImplicitParam(name="channelId",value = "建筑ID",paramType = "query")
    })
    public TableResultResponse listUnBindedSensorForNestedSystem(@RequestParam(defaultValue = "1") Integer page,
                                                                  @RequestParam(defaultValue = "15") Integer limit,
                                                                  Long groupId,
                                                                  Long channelId){
        return this.baseBiz.listUnBindedSensorForNestedSystem(page,limit,groupId,channelId);
    }

    /**
     * 适用于消防用水等室外系统
     * @param page
     * @param limit
     * @param code
     * @param channelId
     * @return
     */
    @RequestMapping(value = "/listUnBindedSensorForOutdoorSystem",method = RequestMethod.GET)
    @ApiOperation("查询推送规则未关联的传感器列表(非消防主机)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",paramType = "query"),
            @ApiImplicitParam(name="limit",value = "每页数量",paramType = "query"),
            @ApiImplicitParam(name="code",value = "地区编码",paramType = "query"),
            @ApiImplicitParam(name="channelId",value = "建筑ID",paramType = "query")
    })
    public TableResultResponse listUnBindedSensorForOutdoorSystem(@RequestParam(defaultValue = "1") Integer page,
                                                                  @RequestParam(defaultValue = "15") Integer limit,
                                                                  @RequestParam(defaultValue = "") String code,
                                                                  @RequestParam Long channelId){

        return this.baseBiz.listUnBindedSensorForOutdoorSystem(page,limit,code,channelId);
    }



    @RequestMapping(value = "/delBindedSensor",method = RequestMethod.POST)
    @ApiOperation("删除推送规则已关联的传感器(非消防主机)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="noticeRuleId",value = "推送规则ID",paramType = "query"),
            @ApiImplicitParam(name="sensorIds",value = "传感器ID，多个以逗号分隔",paramType = "query")
    })
    public ObjectRestResponse delBindedSensor(@RequestBody JSONObject params){
        ObjectRestResponse response = new ObjectRestResponse();
        Long noticeRuleId = params.getLong("noticeRuleId");
        String sensorIds = params.getString("sensorIds");
        this.baseBiz.delBindedSensor(noticeRuleId,sensorIds);
        return response;
    }

    @RequestMapping(value = "/batchAdd",method = RequestMethod.POST)
    @ApiOperation("添加推送规则与传感器(非消防主机)的关联")
    @ApiImplicitParams({
            @ApiImplicitParam(name="noticeRuleId",value = "推送规则ID",paramType = "query"),
            @ApiImplicitParam(name="sensorIds",value = "传感器ID，多个以逗号分隔",paramType = "query")
    })
    public ObjectRestResponse batchAdd(@RequestBody JSONObject params){
        ObjectRestResponse response = new ObjectRestResponse();
        Long noticeRuleId = params.getLong("noticeRuleId");
        String sensorIds = params.getString("sensorIds");
        this.baseBiz.batchAdd(noticeRuleId,sensorIds);
        return response;
    }
}
