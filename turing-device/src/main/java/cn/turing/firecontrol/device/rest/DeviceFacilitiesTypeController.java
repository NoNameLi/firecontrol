package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.device.biz.DeviceCheckTestItemBiz;
import cn.turing.firecontrol.device.biz.DeviceFacilitiesTypeBiz;
import cn.turing.firecontrol.device.biz.DeviceIndoorLabelBiz;
import cn.turing.firecontrol.device.biz.DeviceOutdoorLabelBiz;
import cn.turing.firecontrol.device.entity.DeviceFacilitiesType;
import cn.turing.firecontrol.device.entity.DeviceOutdoorLabel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.SplitUtil;
import cn.turing.firecontrol.device.util.TrimUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import java.util.List;

@RestController
@RequestMapping("deviceFacilitiesType")
@CheckClientToken
@CheckUserToken
public class DeviceFacilitiesTypeController extends BaseController<DeviceFacilitiesTypeBiz,DeviceFacilitiesType,Integer> {

    @Autowired
    protected DeviceFacilitiesTypeBiz dftBiz;
    @Autowired
    private DeviceCheckTestItemBiz dctiBiz;
    @Autowired
    private DeviceOutdoorLabelBiz dolBiz;
    @Autowired
    private DeviceIndoorLabelBiz dilBiz;

    @RequestMapping(value = "/selectByType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有设施类型")
    public List<DeviceFacilitiesType> selectByType(String equipmentType){
        return dftBiz.selectByType(equipmentType);
    }

    @ApiOperation("分页获取数据,按条件搜索数据")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "当前显示条数",paramType = "query"),
            @ApiImplicitParam(name = "equipmentType",value = "设施类型",paramType = "query")
    })
    public TableResultResponse<Map> list(@RequestParam String page, @RequestParam String limit, String equipmentType){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return baseBiz.selectPageList(query,equipmentType);
    }


    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加设施")
    public ObjectRestResponse<DeviceFacilitiesType> add(@RequestBody Map<String,Object> params){
        ObjectRestResponse<DeviceFacilitiesType> responseResult =  new ObjectRestResponse<DeviceFacilitiesType>();
        String equipmentType = (String) params.get("equipmentType");
        //检查项  单选
        List<DeviceCheckTestItem> checkItems1 = JSON.parseArray((String) params.get("checkItems1"),DeviceCheckTestItem.class);
        //检查项  输入
        List<DeviceCheckTestItem> checkItems2 = JSON.parseArray((String) params.get("checkItems2"),DeviceCheckTestItem.class);
        //检测项  单选
        List<DeviceCheckTestItem> testItems1 = JSON.parseArray((String) params.get("testItems1"),DeviceCheckTestItem.class);
        //检测项  输入
        List<DeviceCheckTestItem> testItems2 = JSON.parseArray((String) params.get("testItems2"),DeviceCheckTestItem.class);
        checkItems1 = check(checkItems1);
        checkItems2 = check(checkItems2);
        testItems1 = check(testItems1);
        testItems2 = check(testItems2);
        DeviceFacilitiesType deviceFacilitiesType = new DeviceFacilitiesType();
        deviceFacilitiesType.setEquipmentType(equipmentType);
        deviceFacilitiesType.setCheckItemCount((checkItems1.size()+checkItems2.size())+"");
        deviceFacilitiesType.setTestItemCount((testItems1.size()+testItems2.size())+"");
        TrimUtil.trimObject(deviceFacilitiesType);
        if(ValidatorUtils.hasAnyBlank(equipmentType)){
            return responseResult;
        }
        if(baseBiz.selectByCount(equipmentType)>0){
            throw new RuntimeException("设施类型已存在，不可重复添加");
        }
        baseBiz.insertSelective(deviceFacilitiesType);
        //添加检查项
        DeviceCheckTestItem deviceCheckTestItem = null;
        for(int i=0;i<checkItems1.size();i++){
            deviceCheckTestItem = checkItems1.get(i);
            deviceCheckTestItem.setFacilitiesTypeId(deviceFacilitiesType.getId());
            deviceCheckTestItem.setFlag("0");
            deviceCheckTestItem.setItemFlag("0");
            deviceCheckTestItem.setId(null);
            dctiBiz.insertSelective(deviceCheckTestItem);
        }
        for(int i=0;i<checkItems2.size();i++){
            deviceCheckTestItem = checkItems2.get(i);
            deviceCheckTestItem.setFacilitiesTypeId(deviceFacilitiesType.getId());
            deviceCheckTestItem.setFlag("1");
            deviceCheckTestItem.setItemFlag("0");
            deviceCheckTestItem.setId(null);
            dctiBiz.insertSelective(deviceCheckTestItem);
        }
        for(int i=0;i<testItems1.size();i++){
            deviceCheckTestItem = testItems1.get(i);
            deviceCheckTestItem.setFacilitiesTypeId(deviceFacilitiesType.getId());
            deviceCheckTestItem.setFlag("0");
            deviceCheckTestItem.setItemFlag("1");
            deviceCheckTestItem.setId(null);
            dctiBiz.insertSelective(deviceCheckTestItem);
        }
        for(int i=0;i<testItems2.size();i++){
            deviceCheckTestItem = testItems2.get(i);
            deviceCheckTestItem.setFacilitiesTypeId(deviceFacilitiesType.getId());
            deviceCheckTestItem.setFlag("1");
            deviceCheckTestItem.setItemFlag("1");
            deviceCheckTestItem.setId(null);
            dctiBiz.insertSelective(deviceCheckTestItem);
        }
        return responseResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改设施")
    public ObjectRestResponse<DeviceFacilitiesType> update(@RequestBody Map<String,Object> params){
        ObjectRestResponse<DeviceFacilitiesType> responseResult =  new ObjectRestResponse<DeviceFacilitiesType>();
        String equipmentType = (String) params.get("equipmentType");
        Integer id = (Integer) params.get("id");
        //检查项  单选
        List<DeviceCheckTestItem> checkItems1 = JSON.parseArray((String) params.get("checkItems1"),DeviceCheckTestItem.class);
        //检查项  输入
        List<DeviceCheckTestItem> checkItems2 = JSON.parseArray((String) params.get("checkItems2"),DeviceCheckTestItem.class);
        //检测项  单选
        List<DeviceCheckTestItem> testItems1 = JSON.parseArray((String) params.get("testItems1"),DeviceCheckTestItem.class);
        //检测项  输入
        List<DeviceCheckTestItem> testItems2 = JSON.parseArray((String) params.get("testItems2"),DeviceCheckTestItem.class);
        checkItems1 = check(checkItems1);
        checkItems2 = check(checkItems2);
        testItems1 = check(testItems1);
        testItems2 = check(testItems2);
        DeviceFacilitiesType deviceFacilitiesType = new DeviceFacilitiesType();
        deviceFacilitiesType.setId(id);
        deviceFacilitiesType.setEquipmentType(equipmentType);
        deviceFacilitiesType.setCheckItemCount((checkItems1.size()+checkItems2.size())+"");
        deviceFacilitiesType.setTestItemCount((testItems1.size()+testItems2.size())+"");
        TrimUtil.trimObject(deviceFacilitiesType);
        DeviceFacilitiesType entity = baseBiz.selectById(id);
        if(entity==null){
            throw  new RuntimeException(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
        }
        if(ValidatorUtils.hasAnyBlank(deviceFacilitiesType.getEquipmentType())){
            return responseResult;
        }
        if(!entity.getEquipmentType().equalsIgnoreCase(deviceFacilitiesType.getEquipmentType())&&baseBiz.selectByCount(equipmentType)>0){
            throw new RuntimeException("设施类型已存在");
        }
        baseBiz.updateSelectiveById(deviceFacilitiesType);

        //删除原有的检查项检测项
        DeviceCheckTestItem deleteItem = new DeviceCheckTestItem();
        deleteItem.setFacilitiesTypeId(deviceFacilitiesType.getId());
        dctiBiz.delete(deleteItem);

        //添加检查检测项
        DeviceCheckTestItem deviceCheckTestItem = null;
        for(int i=0;i<checkItems1.size();i++){
            deviceCheckTestItem = checkItems1.get(i);
            deviceCheckTestItem.setFacilitiesTypeId(deviceFacilitiesType.getId());
            deviceCheckTestItem.setFlag("0");
            deviceCheckTestItem.setItemFlag("0");
            deviceCheckTestItem.setId(null);
            dctiBiz.insertSelective(deviceCheckTestItem);
        }
        for(int i=0;i<checkItems2.size();i++){
            deviceCheckTestItem = checkItems2.get(i);
            deviceCheckTestItem.setFacilitiesTypeId(deviceFacilitiesType.getId());
            deviceCheckTestItem.setFlag("1");
            deviceCheckTestItem.setItemFlag("0");
            deviceCheckTestItem.setId(null);
            dctiBiz.insertSelective(deviceCheckTestItem);
        }
        for(int i=0;i<testItems1.size();i++){
            deviceCheckTestItem = testItems1.get(i);
            deviceCheckTestItem.setFacilitiesTypeId(deviceFacilitiesType.getId());
            deviceCheckTestItem.setFlag("0");
            deviceCheckTestItem.setItemFlag("1");
            deviceCheckTestItem.setId(null);
            dctiBiz.insertSelective(deviceCheckTestItem);
        }
        for(int i=0;i<testItems2.size();i++){
            deviceCheckTestItem = testItems2.get(i);
            deviceCheckTestItem.setFacilitiesTypeId(deviceFacilitiesType.getId());
            deviceCheckTestItem.setFlag("1");
            deviceCheckTestItem.setItemFlag("1");
            deviceCheckTestItem.setId(null);
            dctiBiz.insertSelective(deviceCheckTestItem);
        }
        return responseResult;
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除设施前的查询")
    public ObjectRestResponse get(@RequestParam Integer id){
        ObjectRestResponse<Map> responseResult =  new ObjectRestResponse<Map>();
        Map<String,Object> map = new HashMap<>();
        DeviceFacilitiesType deviceFacilitiesType = baseBiz.selectById(id);
        map.put("equipmentType",deviceFacilitiesType.getEquipmentType());
        map.put("checkItemCount",deviceFacilitiesType.getCheckItemCount());
        map.put("testItemCount",deviceFacilitiesType.getTestItemCount());
        map.put("checkItems1",dctiBiz.selectByQuery(id,"0","0"));
        map.put("checkItems2",dctiBiz.selectByQuery(id,"0","1"));
        map.put("testItems1",dctiBiz.selectByQuery(id,"1","0"));
        map.put("testItems2",dctiBiz.selectByQuery(id,"1","1"));
        return responseResult.data(map);
    }


    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除设施前的查询")
    public ObjectRestResponse<Object> deleteQuery(@RequestParam Integer id){
        ObjectRestResponse<Object> responseResult =  new ObjectRestResponse<Object>();
        List<String> dol = dolBiz.deleteFacilitiesTypeQuery(id);
        List<String> dil = dilBiz.deleteFacilitiesTypeQuery(id);
        Set<String> set = new HashSet<>();
        set.addAll(dil);
        set.addAll(dol);
        return responseResult.data(set);
    }




    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除设施")
    public ObjectRestResponse<Object> delete(@RequestParam Integer id){
        ObjectRestResponse<Object> responseResult =  new ObjectRestResponse<Object>();
        DeviceFacilitiesType deviceFacilitiesType = new DeviceFacilitiesType();
        deviceFacilitiesType.setId(id);
        deviceFacilitiesType.setDelFlag("1");
        baseBiz.updateSelectiveById(deviceFacilitiesType);
        return responseResult;
    }


    public List<DeviceCheckTestItem> check(List<DeviceCheckTestItem> checkItems1){
        List<DeviceCheckTestItem> checkItems_1 = new ArrayList<>();
        for(int i=0;i<checkItems1.size();i++){
            if(StringUtils.isNotBlank(checkItems1.get(i).getCheckTestItem())){
                checkItems_1.add(checkItems1.get(i));
            }
        }
        return checkItems_1;
    }

    @RequestMapping(value = "/selectType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有设施类型")
    public ObjectRestResponse selectType(){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List<String> list = dftBiz.getByType();
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/selectTypeByName",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有设施类型")
    public ObjectRestResponse selectTypeByName(String equipmentType){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List<DeviceFacilitiesType> list =dftBiz.selectByType(equipmentType);
        responseResult.setData(list);
        return responseResult;
    }

}