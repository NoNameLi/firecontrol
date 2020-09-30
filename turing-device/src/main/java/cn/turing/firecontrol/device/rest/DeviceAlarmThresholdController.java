package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.DeviceAlarmLevelBiz;
import cn.turing.firecontrol.device.biz.DeviceAlarmThresholdBiz;
import cn.turing.firecontrol.device.biz.DeviceMeasuringPointBiz;
import cn.turing.firecontrol.device.entity.DeviceAlarmThreshold;
import cn.turing.firecontrol.device.entity.DeviceMeasuringPoint;
import cn.turing.firecontrol.device.util.SplitUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;

import java.util.*;

@RestController
@RequestMapping("deviceAlarmThreshold")
@CheckClientToken
@CheckUserToken
public class DeviceAlarmThresholdController extends BaseController<DeviceAlarmThresholdBiz,DeviceAlarmThreshold,Integer> {

    @Autowired
    private DeviceAlarmThresholdBiz datBiz;
    @Autowired
    private DeviceMeasuringPointBiz dmpBiz;
    @Autowired
    private DeviceAlarmLevelBiz dalBiz;

    @ApiOperation("分页获取数据,按条件搜索数据")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Map<String,Object>> list(@RequestParam String page, @RequestParam String limit, String measuringPoint){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return baseBiz.selectPageList(query,measuringPoint);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加测点告警")
    public ObjectRestResponse<DeviceAlarmThreshold> add(@RequestBody  Map<String,Object> params){
        List<DeviceAlarmThreshold> level = JSON.parseArray((String) params.get("level"),DeviceAlarmThreshold.class);
        ObjectRestResponse<DeviceAlarmThreshold> responseResult =  new ObjectRestResponse<>();
        if(level!=null){
            if(level.size()>0){
                for(int i=0;i<level.size();i++){
                    DeviceAlarmThreshold entity=level.get(i);
                    if(i==0){
                        entity.setAlarmMin(null);
                    }
                    if(i==level.size()-1){
                        entity.setAlarmMax(null);
                    }
                    entity.setId(null);
                    entity.setMpId((Integer)params.get("mpId"));
                    baseBiz.insertSelective(entity);
                }
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改测点告警")
    public ObjectRestResponse<DeviceAlarmThreshold> update(@RequestBody  Map<String,Object> params){
        List<DeviceAlarmThreshold> level = JSON.parseArray((String) params.get("level"),DeviceAlarmThreshold.class);
        ObjectRestResponse<DeviceAlarmThreshold> responseResult =  new ObjectRestResponse<>();
        if(level!=null){
            if(level.size()>0){
                for(int i=0;i<level.size();i++){
                    DeviceAlarmThreshold entity=level.get(i);
                    if(i==0){
                        entity.setAlarmMin(null);
                    }
                    if(i==level.size()-1){
                        entity.setAlarmMax(null);
                    }
                    entity.setMpId((Integer)params.get("mpId"));
                    baseBiz.updateSelectiveById(entity);
                }
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据测点id删除")
    public ObjectRestResponse<DeviceAlarmThreshold> remove(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        DeviceAlarmThreshold entity=new DeviceAlarmThreshold();
        entity.setMpId(id);
        entity.setTenantId(BaseContextHandler.getTenantID());
        baseBiz.delete(entity);
        return responseResult;
    }

    /**
     * id为空，添加查询，不为空编辑查询
     * @param id
     * @return
     */
    @RequestMapping(value = "/getAdd",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取所有需要测点告警跟等级")
    public Map<String, Object> getAdd(Integer id){
        //获取已经添加的测点的id
        List<Integer> lists = datBiz.getMeasuringPointIds();
        String ids ="";
        HashMap<String,Object> map=new HashMap<>();
        List<Map> level = dalBiz.getAll();
        int num = 0;
        for(int i=0;i<level.size();i++){
            if(i==0){
                //负无穷
                level.get(i).put("alarmMin",-999999999);
            }else {
                level.get(i).put("alarmMin",num);
            }
            num = num + 1;
            if(i==level.size()-1){
                //正无穷
                level.get(i).put("alarmMax",999999999);
            }else {
                level.get(i).put("alarmMax",num);
            }
            num = num + 1;
        }
        map.put("level",level);
        if(lists!=null&&lists.size()>0){
            ids = SplitUtil.merge(lists);
        }
        List<DeviceMeasuringPoint> measuringPoint = dmpBiz.selectByNotIds(ids);
        map.put("measuringPoint",measuringPoint);
        return map;
    }


    /**
     * id为空，添加查询，不为空编辑查询
     * @param id
     * @return
     */
    @RequestMapping(value = "/getUpdate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取所有需要测点告警跟等级")
    public Map<String, Object> getUpdate(@RequestParam Integer id){
        //获取已经添加的测点的id
        List<Integer> lists = datBiz.getMeasuringPointIds();
        String ids ="";
        HashMap<String,Object> map=new HashMap<>();
        List<Map> deviceAlarmThresholds = datBiz.selectByMeasuringPointId(id);
        for(int i=0;i<deviceAlarmThresholds.size();i++){
            Map map1 = deviceAlarmThresholds.get(i);
            if(i==0){
                map1.put("alarmMin",-999999999);
            }else if(i==deviceAlarmThresholds.size()-1){
                map1.put("alarmMax",999999999);
            }
        }
        map.put("level",deviceAlarmThresholds);
        if(lists!=null&&lists.size()>0){
            ids = SplitUtil.merge(lists);
        }
        List<DeviceMeasuringPoint> measuringPoint = dmpBiz.selectByNotIds(ids);
        //添加本身测点
        LinkedList<DeviceMeasuringPoint> temp = new LinkedList<>();
        temp.addAll(measuringPoint);
        temp.addFirst(dmpBiz.get(id));
        map.put("measuringPoint",temp);
        return map;
    }




}