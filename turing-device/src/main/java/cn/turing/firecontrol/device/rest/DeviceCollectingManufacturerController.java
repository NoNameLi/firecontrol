package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.biz.DeviceCollectingDeviceTypeBiz;
import cn.turing.firecontrol.device.biz.DeviceCollectingManufacturerBiz;
import cn.turing.firecontrol.device.biz.DeviceSensorManufacturerBiz;
import cn.turing.firecontrol.device.biz.DeviceSensorTypeBiz;
import cn.turing.firecontrol.device.entity.DeviceCollectingDeviceType;
import cn.turing.firecontrol.device.entity.DeviceCollectingManufacturer;
import cn.turing.firecontrol.device.entity.DeviceSensorManufacturer;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.TrimUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("deviceCollectingManufacturer")
@CheckClientToken
@CheckUserToken
public class DeviceCollectingManufacturerController extends BaseController<DeviceCollectingManufacturerBiz,DeviceCollectingManufacturer,Integer> {


    @Autowired
    private DeviceCollectingDeviceTypeBiz dcdtBiz;

    @ApiOperation("分页获取数据")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<DeviceCollectingManufacturer> list(String page,String limit, String collectingManufacturer){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        if(StringUtils.isBlank(page)){
            page = "1";
        }
        if(StringUtils.isBlank(limit)){
            limit = "10";
        }
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return baseBiz.selectPageList(query,collectingManufacturer);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添网关厂商")
    public ObjectRestResponse<DeviceCollectingManufacturer> add(@RequestBody DeviceCollectingManufacturer entity){
        ObjectRestResponse<DeviceCollectingManufacturer> responseResult =  new ObjectRestResponse<DeviceCollectingManufacturer>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getCollectingManufacturer());
        if(entity!=null){
            String manufacturer = entity.getCollectingManufacturer();
            if(StringUtils.isNotBlank(manufacturer)){
                if(baseBiz.selectByCount(manufacturer)>0){
                    throw new RuntimeException("厂商已存在");
                }
                entity.setId(null);
                baseBiz.insertSelective(entity);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加传感器厂商")
    public ObjectRestResponse<DeviceCollectingManufacturer> update(@RequestBody DeviceCollectingManufacturer entity){
        ObjectRestResponse<DeviceCollectingManufacturer> responseResult =  new ObjectRestResponse<DeviceCollectingManufacturer>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getCollectingManufacturer());
        if(entity!=null){
            String manufacturer = entity.getCollectingManufacturer();
            DeviceCollectingManufacturer deviceCollectingManufacturer = baseBiz.selectById(entity.getId());
            //对象不存在
            if(deviceCollectingManufacturer==null){
                throw  new RuntimeException(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
            }
            if(StringUtils.isNotBlank(manufacturer)){
                manufacturer = manufacturer.trim();
                if(!manufacturer.equalsIgnoreCase(deviceCollectingManufacturer.getCollectingManufacturer())&&baseBiz.selectByCount(manufacturer)>0){
                    throw new RuntimeException("厂商已存在");
                }
                baseBiz.updateSelectiveById(entity);
                DeviceCollectingDeviceType collectingDeviceType = new DeviceCollectingDeviceType();
                collectingDeviceType.setManufacturerId(entity.getId());
                collectingDeviceType.setManufacturer(entity.getCollectingManufacturer());
                dcdtBiz.updateCollecting(collectingDeviceType);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象")
    public ObjectRestResponse get(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        DeviceCollectingManufacturer deviceSensorManufacturer = baseBiz.selectById(id);
        responseResult.data(deviceSensorManufacturer);
        return responseResult;
    }


    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除传感器厂商")
    public ObjectRestResponse<DeviceCollectingManufacturer> deleteQuery(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        List<String> tenantIds = dcdtBiz.selectByCollectingManufacturerIdResultTenantID(id);
        responseResult.setData(tenantIds);
        return responseResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除传感器厂商")
    public ObjectRestResponse<DeviceCollectingManufacturer> delete(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        List<String> tenantIds =  dcdtBiz.selectByCollectingManufacturerIdResultTenantID(id);
        //当有子站使用时不能删除
        if(tenantIds!=null&&tenantIds.size()>0) {
            throw new RuntimeException(Constants.DELETE_ERROR);
        }
        //假删除
        DeviceCollectingManufacturer entity = new DeviceCollectingManufacturer();
        entity.setId(id);
        entity.setDelFlag("1");
        baseBiz.updateSelectiveById(entity);
        return responseResult;
    }

}