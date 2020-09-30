package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.biz.DeviceVideoGroupBiz;
import cn.turing.firecontrol.device.entity.DeviceVideoExt;
import cn.turing.firecontrol.device.entity.DeviceVideoGroup;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/02/20 11:36
 *
 * @Description
 * @Version V1.0
 */
@Api(tags = "视频设备组")
@RestController
@RequestMapping("deviceVideoGroup")
@CheckUserToken
@CheckClientToken
@Slf4j
public class DeviceVideoGroupController extends BaseController<DeviceVideoGroupBiz, DeviceVideoGroup,Integer> {

    @Autowired
    private DeviceVideoGroupBiz deviceVideoGroupBiz;


    /**
     * 重写分页查询
     * @param params
     * @return
     */
    @Override
    public TableResultResponse<DeviceVideoGroup> list(@RequestParam Map<String, Object> params) {
        try {
            Query query = new Query(params);
            return baseBiz.queryByPage(query.getPage(),query.getLimit());
        }catch (Exception e){
            log.error("分页查询设备组失败");
            throw new ParamErrorException(e.getMessage());
        }
    }

    @GetMapping("deviceTree")
    @ApiOperation("查询树形结果设备组、设备列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceNoOrName", value = "设备编号或名称（模糊查询）", paramType = "query"),
            @ApiImplicitParam(name = "groupId", value = "设备组ID（空则全部）", paramType = "query"),
            @ApiImplicitParam(name = "hasSolution", value = "是否已配置分析方案", paramType = "query"),
            @ApiImplicitParam(name = "sensorNo", value = "设备标号（模糊查询）", paramType = "query"),
            @ApiImplicitParam(name = "deviceName", value = "设备名称（模糊查询）", paramType = "query")
    })
    public ObjectRestResponse getDeviceTree(String deviceNoOrName,String groupId, Boolean hasSolution,String sensorNo,String deviceName){
        try {
            List<Map<String,Object>> groupList = deviceVideoGroupBiz.queryDeviceTree(deviceNoOrName,groupId,hasSolution,sensorNo,deviceName);
            return new ObjectRestResponse().data(groupList);
        }catch (Exception e){
            log.error("获取设备列表失败");
            throw new ParamErrorException(e.getMessage());
        }
    }

}
