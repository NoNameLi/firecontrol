package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.DeviceAlarmLevel;
import cn.turing.firecontrol.device.entity.DeviceAlarmThreshold;
import cn.turing.firecontrol.device.entity.DeviceMeasuringPoint;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.TrimUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("deviceMeasuringPoint")
@CheckClientToken
@CheckUserToken
public class DeviceMeasuringPointController extends BaseController<DeviceMeasuringPointBiz,DeviceMeasuringPoint,Integer> {

    @Autowired
    private DeviceMeasuringPointBiz dmpBiz;
    @Autowired
    private DeviceSensorTypeBiz dstBiz;
    @Autowired
    private DeviceSensorMpRelationBiz dsmrBiz;
    @Autowired
    private DeviceAlarmLevelBiz dalBiz;
    @Autowired
    private DeviceAlarmThresholdBiz datBiz;
    @Autowired
    private DeviceSensorBiz dsBiz;


    @ApiOperation("分页获取数据,按条件搜索数据")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Map> list(@RequestParam String page, @RequestParam String limit, String measuringPoint,String measuringPointType){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return baseBiz.selectPageList(query,measuringPoint,measuringPointType);
    }

    @RequestMapping(value = "/all",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取所有数据")
    public List all(){
        return baseBiz.getAll();
    }

 /*   @ApiOperation("添加或者编辑前的查询")
    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse get(Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map<String,Object> result = new HashMap();
        List<Map> levels = null;
        if(id != null){
            DeviceMeasuringPoint deviceMeasuringPoint = baseBiz.selectById(id);
            result.put("deviceMeasuringPoint",deviceMeasuringPoint);
            for(int i=0;i<levels.size();i++){
                 levels = datBiz.selectByMeasuringPointId(id);
            }
        }else {
            levels = dalBiz.getAll();
        }
        result.put("level",levels);
        responseResult.setData(result);
        return responseResult;
    }
*/

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加测点")
    public ObjectRestResponse<DeviceMeasuringPoint> add(@RequestBody DeviceMeasuringPoint entity){
        ObjectRestResponse<DeviceMeasuringPoint> responseResult =  new ObjectRestResponse<DeviceMeasuringPoint>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getMeasuringPoint(),entity.getCodeName());
        if(entity!=null){
            String measuringPoint = entity.getMeasuringPoint();
            String codeName = entity.getCodeName();
            String dataUnit = entity.getDataUnit();
            if(StringUtils.isNotBlank(measuringPoint)&&StringUtils.isNotBlank(codeName)){
                //判断字段长度是否过长
                if(measuringPoint.length()>100||codeName.length()>100){
                    throw new RuntimeException(Constants.FIELD_LENGTH_ERROR);
                }
                //判断测点，代号是否重复
                if(dmpBiz.selectByCount(measuringPoint,null)>0){
                    throw  new RuntimeException(Constants.MEASURING_POINT_REPEAT);
                }
                if(dmpBiz.selectByCount(null,codeName)>0){
                    throw  new RuntimeException(Constants.CODE_NAME_REPEAT);
                }
                //添加测点的信息
                entity.setId(null);
                if(StringUtils.isBlank(dataUnit)){
                    entity.setDataUnit("");
                }
                baseBiz.insertSelective(entity);
                //当为火警测点时级联添加预警等级
/*                if("0".equals(entity.getMeasuringPointType())&& StringUtils.isNotBlank(entity.getParam())){
                    //级联预警等级
                    List<DeviceAlarmThreshold> levels = JSON.parseArray(entity.getParam(),DeviceAlarmThreshold.class);
                    for(int i=0;i<levels.size();i++){
                        levels.get(i).setMpId(entity.getId());
                        datBiz.insertSelective(levels.get(i));
                    }
                }*/
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改测点")
    public ObjectRestResponse<DeviceMeasuringPoint> update(@RequestBody DeviceMeasuringPoint entity){
        ObjectRestResponse<DeviceMeasuringPoint> responseResult =  new ObjectRestResponse<DeviceMeasuringPoint>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getMeasuringPoint(),entity.getCodeName());
        if(entity!=null){
            String measuringPoint = entity.getMeasuringPoint();
            String codeName = entity.getCodeName();
            String dataUnit = entity.getDataUnit();
            DeviceMeasuringPoint deviceMeasuringPoint=dmpBiz.selectById(entity.getId());
            //对象不存在
            if(deviceMeasuringPoint==null){
                throw  new RuntimeException(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
            }
            if(StringUtils.isNotBlank(measuringPoint)&&StringUtils.isNotBlank(codeName)){
                //判断字段长度是否过长
                if(measuringPoint.length()>100||codeName.length()>100){
                    throw new RuntimeException(Constants.FIELD_LENGTH_ERROR);
                }
                //判断测点是否修改
                if(!measuringPoint.equalsIgnoreCase(deviceMeasuringPoint.getMeasuringPoint())&&dmpBiz.selectByCount(measuringPoint,null)>0){
                    throw  new RuntimeException(Constants.MEASURING_POINT_REPEAT);
                }
                //代号不许修改
                if(!codeName.equalsIgnoreCase(deviceMeasuringPoint.getCodeName())){
                    throw  new RuntimeException(Constants.CODE_NAME_NOT_MODIFY);
                }
                if(StringUtils.isBlank(dataUnit)){
                    entity.setDataUnit("");
                }
                baseBiz.updateSelectiveById(entity);
/*
                List<DeviceAlarmThreshold> levels = null;
                //当为火警测点时级联修改预警等级
                if("0".equals(entity.getMeasuringPointType())&& StringUtils.isNotBlank(entity.getParam())){
                    levels = JSON.parseArray(entity.getParam(),DeviceAlarmThreshold.class);
                    for(int i=0;i<levels.size();i++){
                        levels.get(i).setMpId(entity.getId());
                        datBiz.updateSelectiveById(levels.get(i));
                    }
                }else {
                    //当为监测测点时删除预警等级
                    DeviceAlarmThreshold temp = new DeviceAlarmThreshold();
                    temp.setMpId(entity.getId());
                    datBiz.delete(temp);
                    //将正常值设置为空
                    entity.setNormalSymbolMax(null);
                    entity.setNormalValueMax(null);
                }*/
            }
        }
        return responseResult;
    }

/*    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除测点")
    public ObjectRestResponse remove(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(id!=null){
            //查询测点绑定的传感器，查询有多少子站使用了该传感器的系列，有用户绑定不允许删除
            List<String> tenantIds = dstBiz.selectByMeasuringPointIdResultTenantID(id);
            if(tenantIds!=null&&tenantIds.size()>0){
                throw  new RuntimeException( Constants.DELETE_ERROR);
            }else {
                //删除测点
                DeviceMeasuringPoint deviceMeasuringPoint = new DeviceMeasuringPoint();
                deviceMeasuringPoint.setId(id);
                deviceMeasuringPoint.setDelFlag("1");
                baseBiz.updateSelectiveById(deviceMeasuringPoint);
                //删除测点传感器关联表
                dsmrBiz.deleteByMPIds(id);
            }
        }
        return responseResult;
    }*/

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除测点")
    public ObjectRestResponse remove(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(id!=null){
            //查询测点绑定的传感器，查询有多少子站使用了该传感器的系列，有用户绑定不允许删除
            List<String> tenantIds = dsBiz.deleteMPQuery(id);
            if(tenantIds!=null&&tenantIds.size()>0){
                throw  new RuntimeException( Constants.DELETE_ERROR);
            }else {
                //删除测点
                DeviceMeasuringPoint deviceMeasuringPoint = new DeviceMeasuringPoint();
                deviceMeasuringPoint.setId(id);
                deviceMeasuringPoint.setDelFlag("1");
                baseBiz.updateSelectiveById(deviceMeasuringPoint);
                //删除测点传感器关联表
                dsmrBiz.deleteByMPIds(id);
            }
        }
        return responseResult;
    }


    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除测点前查询测点绑定的传感器，查询是否有传感器绑定测点")
    public ObjectRestResponse removeQuery(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(id!=null){
            List<String> tenantIds = dstBiz.selectByMeasuringPointIdResultTenantID(id);
            responseResult.setData(tenantIds);
        }
        return responseResult;
    }

 /*   @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除测点前查询测点绑定的传感器，查询有多少子站使用了该传感器的系列1.5")
    public ObjectRestResponse removeQuery(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        if(id!=null){
            List<String> tenantIds = dsBiz.deleteMPQuery(id);
            responseResult.setData(tenantIds);
        }
        return responseResult;
    }*/


}