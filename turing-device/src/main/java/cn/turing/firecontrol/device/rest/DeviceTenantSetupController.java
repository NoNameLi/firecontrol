package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.DeviceTenantSetupBiz;
import cn.turing.firecontrol.device.entity.DeviceTenantSetup;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("deviceTenantSetup")
@CheckClientToken
@CheckUserToken
public class DeviceTenantSetupController extends BaseController<DeviceTenantSetupBiz,DeviceTenantSetup,Integer> {

    @RequestMapping(value = "/addOrUpdate",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("新增单个对象")
    public ObjectRestResponse<DeviceTenantSetup> addOrUpdate(@RequestBody DeviceTenantSetup entity){
        String tenantId = BaseContextHandler.getTenantID();
        entity.setTenantId(tenantId);
        List<DeviceTenantSetup> list =  baseBiz.getAll();
        if(list==null||list.size()==0){
            baseBiz.insertSelective(entity);
        }else {
            DeviceTenantSetup temp = list.get(0);
            temp.setPlanSetup(entity.getPlanSetup());
            baseBiz.updateSelectiveById(temp);
        }
        return new ObjectRestResponse();
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取平面图的显示开关")
    public ObjectRestResponse getAll(){
        ObjectRestResponse restResponse = new ObjectRestResponse();
        List<DeviceTenantSetup> list =  baseBiz.getAll();
        //默认不开启
        String planSetup="0";
        if(list!=null&&list.size()>0){
            planSetup  = list.get(0).getPlanSetup();
        }
        restResponse.setData(planSetup);
        return restResponse;
    }





}
