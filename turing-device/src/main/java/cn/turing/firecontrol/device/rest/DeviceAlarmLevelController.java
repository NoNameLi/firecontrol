package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.ResponseCode;
import cn.turing.firecontrol.device.util.SplitUtil;
import cn.turing.firecontrol.device.util.TrimUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;

import java.util.*;

@RestController
@RequestMapping("deviceAlarmLevel")
@CheckClientToken
@CheckUserToken
public class DeviceAlarmLevelController extends BaseController<DeviceAlarmLevelBiz,DeviceAlarmLevel,Integer> {

    @Autowired
    private DeviceAlarmLevelBiz dalBiz;
    @Autowired
    private DeviceAlarmThresholdBiz datBiz;
    @Autowired
    private DeviceSensorBiz dsBiz;
    @Autowired
    private DeviceSensorTypeBiz dstBiz;
    @Autowired
    private DeviceSensorSeriesBiz dssBiz;
    @Autowired
    private DeviceSensorMpRelationBiz dsmrBiz;
    @Autowired
    private DeviceAlarmThresholdBiz alarmThresholdBiz;
    @Autowired
    private DeviceMeasuringPointBiz dmpBiz;

    /**
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation("分页获取数据,按条件搜索数据")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse list(@RequestParam String page, @RequestParam String limit){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return dalBiz.selectPageList(query,true);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加等级")
    public ObjectRestResponse<DeviceAlarmLevel> add(@RequestBody DeviceAlarmLevel entity){
        ObjectRestResponse<DeviceAlarmLevel> responseResult =  new ObjectRestResponse<DeviceAlarmLevel>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getLevel(),entity.getLevelInstructions());
        if(entity!=null){
            String level = entity.getLevel();
            Integer sort = entity.getSort();
            String  levelInstructions = entity.getLevelInstructions();
            String color = entity.getColor();
            if(level!=null&&!"".equals(level)&&sort!=null&&sort>=0&&levelInstructions!=null&&!"".equals(levelInstructions)){
                //判断字段长度是否过长
                if(level.length()>10||levelInstructions.length()>50||sort>99999){
                    throw new RuntimeException(Constants.FIELD_LENGTH_ERROR);
                }
                //判断等级是否重复
                if(dalBiz.selectByCount(level,null,null)>0){
                    throw  new RuntimeException( Constants.ALARM_LEVEL_REPEAT);
                }
                //判断排序是否重复
                if(dalBiz.selectByCount(null,sort,null)>0){
                    throw  new RuntimeException( Constants.SORT_LEVEL_REPEAT);
                }
                //判断颜色是否重复
                if(dalBiz.selectByCount(null,null,color)>0){
                    throw  new RuntimeException( Constants.COLOR_REPEAT);
                }
                entity.setId(null);
                baseBiz.insertSelective(entity);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改等级")
    public ObjectRestResponse<DeviceAlarmLevel> update(@RequestBody DeviceAlarmLevel entity){
        ObjectRestResponse<DeviceAlarmLevel> responseResult =  new ObjectRestResponse<DeviceAlarmLevel>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getLevel(),entity.getLevelInstructions());
        if(entity!=null){
            String level = entity.getLevel();
            Integer sort = entity.getSort();
            String color  = entity.getColor();
            String  levelInstructions = entity.getLevelInstructions();
            DeviceAlarmLevel deviceAlarmLevel=dalBiz.selectById(entity.getId());
            //对象不存在
            if(deviceAlarmLevel==null){
                throw  new RuntimeException( Constants.API_MESSAGE_OBJECT_NOT_FOUND);
            }
            if(level!=null&&!"".equals(level)&&sort!=null&&sort>=0&&color!=null&&!"".equals(color)&&levelInstructions!=null&&!"".equals(levelInstructions)){
                //判断字段长度是否过长
                if(level.length()>10||levelInstructions.length()>50||sort>99999){
                    throw new RuntimeException(Constants.FIELD_LENGTH_ERROR);
                }
                //判断等级是否重复
                if(!level.equalsIgnoreCase(deviceAlarmLevel.getLevel())&&dalBiz.selectByCount(level,null,null)>0){
                    throw  new RuntimeException(  Constants.ALARM_LEVEL_REPEAT);
                }
                //判断排序是否重复
                if(!sort.equals(deviceAlarmLevel.getSort())&&dalBiz.selectByCount(null,sort,null)>0){
                    throw  new RuntimeException( Constants.SORT_LEVEL_REPEAT);
                }
                //判断颜色是否重复
                if(!color.equalsIgnoreCase(deviceAlarmLevel.getColor())&&dalBiz.selectByCount(null,null,color)>0){
                    throw  new RuntimeException( Constants.COLOR_REPEAT);
                }
                baseBiz.updateSelectiveById(entity);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除等级，级联删除报警阀值得数据,假删除")
    public ObjectRestResponse remove(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse<DeviceAlarmLevel>();
        if(id!=null){
            //删除报警等级
            DeviceAlarmLevel deviceAlarmLevel = new DeviceAlarmLevel();
            deviceAlarmLevel.setId(id);
            deviceAlarmLevel.setDelFlag("1");
            baseBiz.updateSelectiveById(deviceAlarmLevel);
            //删除报警阀值里面得数据
            datBiz.deleteByAlId(id);
        }
        return responseResult;
    }
    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/selectAlarmLevel",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询报警等级")
    public ObjectRestResponse selectAlarmLevel(@RequestParam String sensorNo,@RequestParam Double alemData,@RequestParam String codeName,@RequestParam String tenantId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<DeviceAlarmLevel>();
        //查询传感器
        DeviceSensor deviceSensor = dsBiz.selectBySensorNo(sensorNo);
        //查询传感器类型
        DeviceSensorType deviceSensorType = dstBiz.getById(deviceSensor.getSensorTypeId());
        //查询传感器系列
        DeviceSensorSeries deviceSensorSeries = dssBiz.selectBySensorTypeId(deviceSensorType.getId());
        //查询测点ids
        List<Integer> ids = dsmrBiz.selectBySensorSeriesId(deviceSensorSeries.getId());
        for(Integer id:ids){
            DeviceMeasuringPoint deviceMeasuringPoint = dmpBiz.selectById(id);
            if(deviceMeasuringPoint.getCodeName().equals(codeName)){
                DeviceAlarmThreshold deviceAlarmThreshold = alarmThresholdBiz.selectByAlrmData(id,alemData,tenantId);
                if(deviceAlarmThreshold!=null){
                    DeviceAlarmLevel deviceAlarmLevel =dalBiz.selectById(deviceAlarmThreshold.getAlId());
                    responseResult.setData(deviceAlarmLevel);
                }
            }
        }
        return responseResult;
    }





}