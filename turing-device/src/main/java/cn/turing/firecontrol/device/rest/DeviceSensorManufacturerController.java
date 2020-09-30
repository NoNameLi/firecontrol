package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.DeviceSensorManufacturer;
import cn.turing.firecontrol.device.entity.DeviceSensorType;
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
@RequestMapping("deviceSensorManufacturer")
@CheckClientToken
@CheckUserToken
public class DeviceSensorManufacturerController extends BaseController<DeviceSensorManufacturerBiz,DeviceSensorManufacturer,Integer> {

    @Autowired
    private DeviceSensorTypeBiz dstBiz;

    @ApiOperation("分页获取数据")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<DeviceSensorManufacturer> list(String page,String limit, String sensorManufacturer){
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
        return baseBiz.selectPageList(query,sensorManufacturer);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加传感器厂商")
    public ObjectRestResponse<DeviceSensorManufacturer> add(@RequestBody DeviceSensorManufacturer entity){
        ObjectRestResponse<DeviceSensorManufacturer> responseResult =  new ObjectRestResponse<DeviceSensorManufacturer>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getSensorManufacturer());
        if(entity!=null){
            String sensorManufacturer = entity.getSensorManufacturer();
            if(StringUtils.isNotBlank(sensorManufacturer)){
                if(baseBiz.selectByCount(sensorManufacturer)>0){
                    throw new RuntimeException("厂商已存在");
                }
                entity.setId(null);
                baseBiz.insertSelective(entity);
            }else {
                throw new RuntimeException(Constants.SAVE_ERROR);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加传感器厂商")
    public ObjectRestResponse<DeviceSensorManufacturer> update(@RequestBody DeviceSensorManufacturer entity){
        ObjectRestResponse<DeviceSensorManufacturer> responseResult =  new ObjectRestResponse<DeviceSensorManufacturer>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getSensorManufacturer());
        if(entity!=null){
            String sensorManufacturer = entity.getSensorManufacturer();
            DeviceSensorManufacturer deviceSensorManufacturer = baseBiz.selectById(entity.getId());
            //对象不存在
            if(deviceSensorManufacturer==null){
                throw  new RuntimeException(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
            }
            if(StringUtils.isNotBlank(sensorManufacturer)){
                if(!sensorManufacturer.equalsIgnoreCase(deviceSensorManufacturer.getSensorManufacturer())&&baseBiz.selectByCount(sensorManufacturer)>0){
                    throw new RuntimeException("厂商已存在");
                }
                baseBiz.updateSelectiveById(entity);
                DeviceSensorType deviceSensorType = new DeviceSensorType();
                deviceSensorType.setManufacturer(entity.getSensorManufacturer());
                deviceSensorType.setManufacturerId(entity.getId());
                dstBiz.updateSensor(deviceSensorType);
            }
        }
        return responseResult;
    }


    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象")
    public ObjectRestResponse get(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        DeviceSensorManufacturer deviceSensorManufacturer = baseBiz.selectById(id);
        responseResult.data(deviceSensorManufacturer);
        return responseResult;
    }

    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除传感器厂商")
    public ObjectRestResponse<DeviceSensorManufacturer> deleteQuery(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        List<String> tenantIds = dstBiz.selectBySensorManufacturerIdResultTenantID(id);
        responseResult.setData(tenantIds);
        return responseResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除传感器厂商")
    public ObjectRestResponse<DeviceSensorManufacturer> delete(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        List<String> tenantIds = dstBiz.selectBySensorManufacturerIdResultTenantID(id);
        //当有子站使用时不能删除
        if(tenantIds!=null&&tenantIds.size()>0) {
            throw new RuntimeException(Constants.DELETE_ERROR);
        }
        //假删除
        DeviceSensorManufacturer entity = new DeviceSensorManufacturer();
        entity.setId(id);
        entity.setDelFlag("1");
        baseBiz.updateSelectiveById(entity);
        return responseResult;
    }
}