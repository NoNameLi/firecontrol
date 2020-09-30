package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.DeviceBuildingBiz;
import cn.turing.firecontrol.device.biz.DeviceNetworkingUnitBiz;
import cn.turing.firecontrol.device.entity.DeviceBuilding;
import cn.turing.firecontrol.device.entity.DeviceNetworkingUnit;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.TrimUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("deviceNetworkingUnit")
@CheckClientToken
@CheckUserToken
public class DeviceNetworkingUnitController extends BaseController<DeviceNetworkingUnitBiz,DeviceNetworkingUnit,Integer> {

    @Autowired
    protected DeviceNetworkingUnitBiz dnuBiz;

    @Autowired
    protected DeviceBuildingBiz dbBiz;

    @Autowired
    private IUserFeign iUserFeign;



    @RequestMapping(value = "/getAll",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取所有数据")
    public List<DeviceNetworkingUnit> all(String oName){
        return dnuBiz.getAll(oName);
    }

    @RequestMapping(value = "/getAllNetworkingUnit",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取所有数据")
    public ObjectRestResponse getAllNetworkingUnit(String oName){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        responseResult.setData(dnuBiz.getAll(oName));
        return responseResult;
    }



    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除联网单位,判断是否绑定建筑物")
    public ObjectRestResponse deleteById(@RequestParam Integer id){
        ObjectRestResponse<DeviceNetworkingUnit> responseResult =  new ObjectRestResponse();
        if(id==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else {
            List<DeviceBuilding> list = dbBiz.selectByOid(id);
            //判断是否绑定建筑物
            if(list.size() ==0){
                //假删除
                DeviceNetworkingUnit deviceNetworkingUnit = new DeviceNetworkingUnit();
                deviceNetworkingUnit.setId(id);
                deviceNetworkingUnit.setDelFlag("1");
                dnuBiz.updateSelectiveById(deviceNetworkingUnit);
            }else {
                throw new RuntimeException(Constants.API_MESSAGE_DELETE_ERROR);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加联网单位")
    public ObjectRestResponse<DeviceNetworkingUnit> add(@RequestBody DeviceNetworkingUnit entity){
        TrimUtil.trimObject(entity);
        if(entity.getKeyunitTimeLong()!=null){
            entity.setKeyunitTime(new Date(entity.getKeyunitTimeLong()));
        }
        if(entity.getoLicenseTimeLong()!=null){
            entity.setOLicenseTime(new Date(entity.getoLicenseTimeLong()));
        }
        ObjectRestResponse<DeviceNetworkingUnit> responseResult =  new ObjectRestResponse<DeviceNetworkingUnit>();
        if(entity!=null){
            String oName = entity.getOName();
            if(oName!=null&&!"".equals(oName)){
                //判单位名称是否重复
                if(dnuBiz.selectByCount(oName)>0){
                    throw  new RuntimeException(Constants.UNIT_NAME_REPEAT);
                }
                entity.setId(null);
                //获取创建用户，修改用户
                String username  = BaseContextHandler.getUsername();
                JSONObject jsonObject  = iUserFeign.getUser(username);
                JSONObject data = jsonObject.getJSONObject("data");
                String name  = (String) data.get("name");
                entity.setCreateacc(username+"("+name+")");
                entity.setCreatetime(new Date());
                baseBiz.insertSelective(entity);
            }
        }
        return responseResult;
    }


    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象")
    public ObjectRestResponse<DeviceNetworkingUnit> get(@RequestParam Integer id){
        ObjectRestResponse<DeviceNetworkingUnit> entityObjectRestResponse = new ObjectRestResponse<>();
        DeviceNetworkingUnit entity = baseBiz.selectById(id);
        //返回时间戳
        if(entity.getKeyunitTime()!=null){
            entity.setKeyunitTimeLong(entity.getKeyunitTime().getTime());
        }
        if(entity.getOLicenseTime()!=null){
            entity.setoLicenseTimeLong(entity.getOLicenseTime().getTime());
        }
        entityObjectRestResponse.data(entity);
        return entityObjectRestResponse;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改联网单位")
    public ObjectRestResponse<DeviceNetworkingUnit> update(@RequestBody DeviceNetworkingUnit entity){
        TrimUtil.trimObject(entity);
        if(entity.getKeyunitTimeLong()!=null){
            entity.setKeyunitTime(new Date(entity.getKeyunitTimeLong()));
        }
        if(entity.getoLicenseTimeLong()!=null){
            entity.setOLicenseTime(new Date(entity.getoLicenseTimeLong()));
        }
        ObjectRestResponse<DeviceNetworkingUnit> responseResult =  new ObjectRestResponse<DeviceNetworkingUnit>();
        if(entity!=null){
            DeviceNetworkingUnit deviceNetworkingUnit = baseBiz.selectById(entity.getId());
            String oName = entity.getOName();
            String street = entity.getStreet();
            if(deviceNetworkingUnit==null){
                throw  new RuntimeException(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
            }
            if(street!=null&&street.length()>3){
                throw  new RuntimeException("街道编码错误！");
            }
            if(oName!=null&&!"".equals(oName)){
                //判断建筑管理，代号是否重复
                if(!oName.equalsIgnoreCase(deviceNetworkingUnit.getOName())&&dnuBiz.selectByCount(oName)>0){
                    throw  new RuntimeException(Constants.UNIT_NAME_REPEAT);
                }
            }
            //获取创建用户，修改用户
            String username  = BaseContextHandler.getUsername();
            JSONObject jsonObject  = iUserFeign.getUser(username);
            JSONObject data = jsonObject.getJSONObject("data");
            String name  = (String) data.get("name");
            entity.setChangeacc(username+"("+name+")");
            entity.setChangetime(new Date());
            baseBiz.updateSelectiveById(entity);
        }
        return responseResult;
    }

    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除联网单位,查看绑定多少个建筑物id")
    public ObjectRestResponse deleteQuery(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(id==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else {
            List<Integer> list = dbBiz.selectByOidResultIds(id);
            responseResult.setData(list);
        }
        return responseResult;
    }



    @RequestMapping(value = "/getBuildingList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查看绑定的建筑")
    public TableResultResponse getBuilding(@RequestParam String ids,@RequestParam Integer page,@RequestParam Integer limit){
        if(ids==null&&!ids.equals("")){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<DeviceBuilding> lists = dbBiz.selectByOids(ids);
        return new TableResultResponse(result.getTotal(),lists);
    }

    @IgnoreClientToken
    @IgnoreUserToken
    @RequestMapping(value = "/getById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据id查询联网单位")
    public ObjectRestResponse getById(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        DeviceNetworkingUnit deviceNetworkingUnit = dnuBiz.getById(id);
        responseResult.setData(deviceNetworkingUnit);
        return responseResult;
    }

}