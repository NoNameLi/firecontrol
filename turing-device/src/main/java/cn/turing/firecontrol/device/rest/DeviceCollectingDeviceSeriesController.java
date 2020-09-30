package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.biz.DeviceCollectingDeviceSeriesBiz;
import cn.turing.firecontrol.device.biz.DeviceCollectingDeviceTypeBiz;
import cn.turing.firecontrol.device.entity.DeviceCollectingDeviceSeries;
import cn.turing.firecontrol.device.entity.DeviceMeasuringPoint;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.ResponseCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("deviceCollectingDeviceSeries")
@CheckClientToken
@CheckUserToken
public class DeviceCollectingDeviceSeriesController extends BaseController<DeviceCollectingDeviceSeriesBiz,DeviceCollectingDeviceSeries,Integer> {

    @Autowired
    private DeviceCollectingDeviceSeriesBiz cdsBiz;
    @Autowired
    private DeviceCollectingDeviceTypeBiz dcdtBiz;

    @ApiOperation("分页获取数据,按条件搜索数据")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<DeviceCollectingDeviceSeries> list(@RequestParam String page, @RequestParam String limit, String type){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return baseBiz.selectPageList(query,type);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加采集设备系列")
    public ObjectRestResponse<DeviceCollectingDeviceSeries> add(@RequestBody DeviceCollectingDeviceSeries entity){
        ObjectRestResponse<DeviceCollectingDeviceSeries> responseResult =  new ObjectRestResponse<DeviceCollectingDeviceSeries>();
        if(entity!=null){
            String type = entity.getType();
            if(type!=null&&!"".equals(type)){
                //判断采集系列是否重复
                if(cdsBiz.selectByCount(type)>0){
                    throw  new RuntimeException( Constants.COLLECTION_SERIES_REPEAT);
                }else {
                    entity.setId(null);
                    baseBiz.insertSelective(entity);
                }
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改采集设备系列")
    public ObjectRestResponse<DeviceCollectingDeviceSeries> update(@RequestBody DeviceCollectingDeviceSeries entity){
        ObjectRestResponse<DeviceCollectingDeviceSeries> responseResult =  new ObjectRestResponse<DeviceCollectingDeviceSeries>();
        if(entity!=null){
            String type = entity.getType();
            DeviceCollectingDeviceSeries deviceCollectingDeviceSeries=cdsBiz.selectById(entity.getId());
            //对象不存在
            if(deviceCollectingDeviceSeries==null){
                throw  new RuntimeException( Constants.API_MESSAGE_OBJECT_NOT_FOUND);
            }
            if(type!=null&&!"".equals(type)){
                //判断采集系列是否存在
                if(!type.equals(deviceCollectingDeviceSeries.getType())&&cdsBiz.selectByCount(type)>0){
                    throw  new RuntimeException(  Constants.COLLECTION_SERIES_REPEAT);
                }else {
                    baseBiz.updateSelectiveById(entity);
                }
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除采集设备前查询有多少子站使用了该采集的系列")
    public ObjectRestResponse<List<Integer>> removeQuery(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(id!=null){
            List<String> tenantIds = dcdtBiz.selectByCollectingDeviceSeriesIdResultTenantId(id);
            responseResult.setData(tenantIds);
        }
        return responseResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除采集设备系列，假删除")
    public ObjectRestResponse remove(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(id!=null){
            //查询传感器系列绑定了多少传感器
            List<String> tenantIds = dcdtBiz.selectByCollectingDeviceSeriesIdResultTenantId(id);
            if(tenantIds!=null&&tenantIds.size()>0){
                throw  new RuntimeException( Constants.DELETE_ERROR);
            }else {
                //删除传感器系列表
                DeviceCollectingDeviceSeries deviceCollectingDeviceSeries = new DeviceCollectingDeviceSeries();
                deviceCollectingDeviceSeries.setId(id);
                deviceCollectingDeviceSeries.setDelFlag("1");
                baseBiz.updateSelectiveById(deviceCollectingDeviceSeries);
            }
        }
        return responseResult;
    }



}