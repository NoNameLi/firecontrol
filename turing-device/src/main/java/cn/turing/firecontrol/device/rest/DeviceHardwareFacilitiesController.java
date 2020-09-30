package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.core.exception.BaseException;
import cn.turing.firecontrol.device.biz.DeviceHardwareFacilitiesBiz;
import cn.turing.firecontrol.device.biz.DeviceNetworkingUnitBiz;
import cn.turing.firecontrol.device.biz.DeviceNoticeBiz;
import cn.turing.firecontrol.device.biz.DeviceSensorBiz;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.util.*;
import io.swagger.annotations.*;
import cn.turing.firecontrol.device.util.Constants;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("deviceHardwareFacilities")
@CheckClientToken
@CheckUserToken
@Api(tags = "硬件设施模块")
public class DeviceHardwareFacilitiesController extends BaseController<DeviceHardwareFacilitiesBiz,DeviceHardwareFacilities,Integer> {

    @Autowired
    private  DeviceHardwareFacilitiesBiz dhfBiz;
    @Autowired
    private DeviceNetworkingUnitBiz dnkuBiz;
    @Autowired
    private DeviceSensorBiz dsBiz;
    @Autowired
    private IUserFeign iUserFeign;

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据6位行政区编码查询省市区")
    public ObjectRestResponse selectAll(){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List list = dhfBiz.selectAll("0");
        responseResult.setData(list);
        return responseResult;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "code",value = "地区的编码",paramType = "query")})
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    public TableResultResponse<Map> list(String page, String limit, String code, DeviceHardwareFacilities entity) {
        String ids = "";
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "15";
        }
        //选择地区时传地区编码
        if(StringUtils.isNotBlank(code)){
            if(code.length()!=6){
                ids = "-1";
            }else {
                if("00".equals(code.substring(2,4))){
                    code = code.substring(0,2)+"____";
                }else if("00".equals(code.substring(4))){
                    code = code.substring(0,4)+"__";
                }
                if("00".equals(code.substring(2,4))){
                    code = code.substring(0,2)+"____";
                }else if("00".equals(code.substring(4))){
                    code = code.substring(0,4)+"__";
                }
                List<Integer> lists = baseBiz.selectByZxqzResultIds(code,"0");
                ids = SplitUtil.merge(lists);
            }
        }
        if(StringUtils.isBlank(ids)){
            return new TableResultResponse<Map>();
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return baseBiz.selectQuery(query,ids,entity);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "硬件设施id",paramType = "query")})
    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("假删除")
    public ObjectRestResponse<DeviceHardwareFacilities> delete(@RequestParam Integer id){
        if(dsBiz.deleteQueryByHydrantId(id)>0){
            throw new RuntimeException("有传感器绑定改硬件设施");
        }
        DeviceHardwareFacilities entity = new DeviceHardwareFacilities();
        entity.setId(id);
        entity.setDelFlag("1");
        baseBiz.updateById(entity);
        return new ObjectRestResponse();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "硬件设施id",paramType = "query")})
    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("假删除，前查看多少室外消防栓绑定的室外传感器")
    public ObjectRestResponse<DeviceHardwareFacilities> deleteQuery(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        responseResult.setData(dsBiz.deleteQueryByHydrantId(id));
        return responseResult;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "硬件设施id",paramType = "query")})
    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("编辑查询")
    public ObjectRestResponse<DeviceHardwareFacilities> get(Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        Map<String,Object> map = new HashMap<>();
        if(id!=null){
            DeviceHardwareFacilities entity=baseBiz.selectById(id);
            //将区代号，转换为省市区代号 返回前端
            if(StringUtils.isNotBlank(entity.getZxqy())){
                StringBuffer stringBuffer = new StringBuffer("");
                List<String> list = dhfBiz.getProvince(entity.getZxqy());
                for(int i=0;i<list.size();i++){
                    if(i==list.size()-1){
                        stringBuffer.append(list.get(i));
                    }else {
                        stringBuffer.append(list.get(i)+",");
                    }
                }
                entity.setZxqy(stringBuffer.toString());
            }
            map.put("deviceHardwareFacilities",entity);
        }
        map.put("oName",dnkuBiz.getAll(null));
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加硬件设施")
    public ObjectRestResponse<DeviceHardwareFacilities> add(@RequestBody DeviceHardwareFacilities entity){
        ValidatorUtils.validateEntity(entity);
        ObjectRestResponse<DeviceHardwareFacilities> responseResult =  new ObjectRestResponse<DeviceHardwareFacilities>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getHydrantName());
        if(entity!=null){
            String hydrantName = entity.getHydrantName();
            String outlet = entity.getOutlet();
//            entity.setZxqy(entity.getCode());

            //严重出水口校验
            if(!validatorOutlet(outlet,entity,responseResult)){
                throw new RuntimeException(responseResult.getMessage());
            }

            if(StringUtils.isNotBlank(hydrantName)){
                //判断消火栓名称是否重复
                if(baseBiz.selectByCount(hydrantName,"0")>0){
                    throw  new RuntimeException("消火栓已存在");
                }
                entity.setId(null);
                //将编码转换为区
                if(StringUtils.isNotBlank(entity.getZxqy())&&entity.getZxqy().length()==6){
                    entity.setArea(dhfBiz.getDistrictByZxqy(entity.getZxqy()));
                }else {
                    throw new RuntimeException("地区编码错误");
                }
                //默认类型为
                entity.setFacilityType("0");
                baseBiz.insertSelective(entity);
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加硬件设施")
    public ObjectRestResponse<DeviceHardwareFacilities> update(@RequestBody DeviceHardwareFacilities entity){
        ValidatorUtils.validateEntity(entity);
        ObjectRestResponse<DeviceHardwareFacilities> responseResult =  new ObjectRestResponse<DeviceHardwareFacilities>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getHydrantName());
        if(entity!=null){
            String hydrantName = entity.getHydrantName();
            String outlet = entity.getOutlet();
            DeviceHardwareFacilities deviceHardwareFacilities = baseBiz.selectById(entity.getId());
            //严重出水口校验
            if(!validatorOutlet(outlet,entity,responseResult)){
                throw new RuntimeException(responseResult.getMessage());
            }
            //对象不存在
            if(deviceHardwareFacilities==null){
                throw  new RuntimeException(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
            }
            if(StringUtils.isNotBlank(hydrantName)){
                //判断消火栓名称是否重复
                if(!hydrantName.equalsIgnoreCase(deviceHardwareFacilities.getHydrantName())&&baseBiz.selectByCount(hydrantName,"0")>0){
                    throw  new RuntimeException("消火栓已存在");
                }
            }
            //将编码转换为区
            if(StringUtils.isNotBlank(entity.getZxqy())&&entity.getZxqy().length()==6){
                entity.setArea(dhfBiz.getDistrictByZxqy(entity.getZxqy()));
            }else {
                //地区编码不修改，区域名不修改
                entity.setZxqy(null);
            }
            baseBiz.updateSelectiveById(entity);
        }
        return responseResult;
    }



    @RequestMapping(value = "/getDistrict",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据6位行政区编码查询所属区名称")
    @ApiImplicitParam(name="zxqy",value = "6位行政区编码",paramType = "query")
    public ObjectRestResponse getDistrict(@RequestParam String zxqy){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        String district = dhfBiz.getDistrictByZxqy(zxqy);
        responseResult.setData(district);
        return responseResult;
    }

    @RequestMapping(value = "/getProvince",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据6位行政区编码查询省市区编码")
    @ApiImplicitParam(name="zxqy",value = "6位行政区编码",paramType = "query")
    public ObjectRestResponse getProvince(@RequestParam String zxqy){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List<String> list = dhfBiz.getProvince(zxqy);
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/selectFirst",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("返回第一个6位行政区编码")
    public ObjectRestResponse selectFirst(){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        Map map = dhfBiz.selectFirst("0");
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getHardwareFacilities",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据地区编码查询消火栓、设备信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "zxqy",value = "6位地区编码",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "当前显示条数",paramType = "query")
    })
    public TableResultResponse getHardwareFacilities(String zxqy,Integer channelId,String page, String limit) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "10";
        }
        if(StringUtils.isNotBlank(zxqy) && zxqy.trim().length()==6){
            //判断 省 市  的时候用__代替查询
            if("00".equals(zxqy.substring(2,4))){
                zxqy = zxqy.substring(0,2)+"____";
            }else if("00".equals(zxqy.substring(4))){
                zxqy = zxqy.substring(0,4)+"__";
            }
            if("00".equals(zxqy.substring(2,4))){
                zxqy = zxqy.substring(0,2)+"____";
            }else if("00".equals(zxqy.substring(4))){
                zxqy = zxqy.substring(0,4)+"__";
            }
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dhfBiz.getHardwareFacilities(query,zxqy,channelId);
    }


    @RequestMapping(value = "/selectByNameAndType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据设施类型与设施名称")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "facilityType",value = "设施类型[0=室外传感器]",paramType = "query"),
            @ApiImplicitParam(name = "hydrantName",value = "设施名称",paramType = "query"),
            @ApiImplicitParam(name = "code",value = "六位编码",paramType = "query")
    })
    public List<DeviceHardwareFacilities> selectByNameAndType(String facilityType,String hydrantName,String code) {
        if(StringUtils.isBlank(facilityType)){
            facilityType = "0";
        }
        //选择地区时传地区编码
        if(code!=null){
            if(code.length()==6){
                if("00".equals(code.substring(2,4))){
                    code = code.substring(0,2)+"____";
                }else if("00".equals(code.substring(4))){
                    code = code.substring(0,4)+"__";
                }
                if("00".equals(code.substring(2,4))){
                    code = code.substring(0,2)+"____";
                }else if("00".equals(code.substring(4))){
                    code = code.substring(0,4)+"__";
                }
            }
        }
        return dhfBiz.selectByNameAndTypeAndCode(facilityType,hydrantName,code);
    }

    @RequestMapping(value = "/getById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据id查询出水口信息")
    @ApiImplicitParam(name="id",value = "消火栓id",paramType = "query")
    public ObjectRestResponse getById(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.getById(id);
        if(deviceHardwareFacilities==null){
            throw new BaseException("硬件设施不存在!");
        }
        List result = new ArrayList();
        String outletTypeOne = null;
        if(StringUtils.isBlank(deviceHardwareFacilities.getOutletTypeOne())){
            responseResult.setData(result);
            return responseResult;
        }
        if("0".equals(deviceHardwareFacilities.getOutletTypeOne())){
            outletTypeOne ="外螺旋式";
        }
        if("1".equals(deviceHardwareFacilities.getOutletTypeOne())){
            outletTypeOne ="内扣式";
        }
        Map map1 = new HashMap();
        map1.put("name","第一出水口");
        map1.put("outletType",outletTypeOne);
        map1.put("outletValue",deviceHardwareFacilities.getOutletValueOne());
        if("0".equals(deviceHardwareFacilities.getOutlet())){
            result.add(map1);
            responseResult.setData(result);
            return responseResult;
        }
        String outletTypeTwo = null;
        if("0".equals(deviceHardwareFacilities.getOutletTypeTwo())){
            outletTypeTwo ="外螺旋式";
        }
        if("1".equals(deviceHardwareFacilities.getOutletTypeTwo())){
            outletTypeTwo ="内扣式";
        }
        Map map2 = new HashMap();
        map2.put("name","第二出水口");
        map2.put("outletType",outletTypeTwo);
        map2.put("outletValue",deviceHardwareFacilities.getOutletValueTwo());
        if("1".equals(deviceHardwareFacilities.getOutlet())){
            result.add(map1);
            result.add(map2);
            responseResult.setData(result);
            return responseResult;
        }
        String outletTypeThree = null;
        if("0".equals(deviceHardwareFacilities.getOutletTypeThree())){
            outletTypeThree ="外螺旋式";
        }
        if("1".equals(deviceHardwareFacilities.getOutletTypeThree())){
            outletTypeThree ="内扣式";
        }
        Map map3 = new HashMap();
        map3.put("name","第三出水口");
        map3.put("outletType",outletTypeThree);
        map3.put("outletValue",deviceHardwareFacilities.getOutletValueThree());
        result.add(map1);
        result.add(map2);
        result.add(map3);
        responseResult.setData(result);
        return responseResult;
    }

    @RequestMapping(value = "/selectByHydrantNameLike",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询消火栓和位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name="hydrantName",value = "消火栓名称",paramType = "query"),
            @ApiImplicitParam(name="tenantId",value = "租户id",paramType = "query")
    })
    public ObjectRestResponse selectByHydrantNameLike(String hydrantName,String tenantId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        List<DeviceHardwareFacilities> hardwareFacilitiesList = new ArrayList<>();
//        //查询消火栓
//        if(iUserFeign.isSuperAdmin(BaseContextHandler.getUserID())){//判断是否是管理员
//            hardwareFacilitiesList = dhfBiz.selectByHydrantNameLike(hydrantName,tenantId);
//        }else {
//            throw new RuntimeException("无此权限!");
//        }
        //查询消火栓
        List<DeviceHardwareFacilities> hardwareFacilitiesList = dhfBiz.selectByHydrantNameLike(hydrantName,null);
        List resultlist = new ArrayList();
        for(DeviceHardwareFacilities deviceHardwareFacilities:hardwareFacilitiesList){
            //List<DeviceSensor> list = dsBiz.getByHydrantId(deviceHardwareFacilities.getId(),channelId);
            Map<String ,Object> map = new HashMap<>();
            map.put("id",deviceHardwareFacilities.getId());//消火栓id
            map.put("name",deviceHardwareFacilities.getHydrantName());//消火栓名称
            //map.put("count",list.size());
            map.put("protectionRadius",deviceHardwareFacilities.getProtectionRadius());//保护半径
            if(StringUtils.isNotBlank(deviceHardwareFacilities.getGis())){
                String [] gis =deviceHardwareFacilities.getGis().split(",");
                map.put("gisx",gis[0]);
                map.put("gisy",gis[1]);
            }
            String status = dsBiz.HardwareFacilities(deviceHardwareFacilities.getHydrantName());
            map.put("status",status);
            resultlist.add(map);
        }
        responseResult.setData(resultlist);
        return responseResult;
    }

    //后台验证参数出水口
    public boolean validatorOutlet(String outlet,DeviceHardwareFacilities entity,ObjectRestResponse responseResult){
        boolean flag = true;
        if("0".equals(outlet)){
            if(ValidatorUtils.hasAnyBlank(entity.getOutletTypeOne(),entity.getOutletValueOne())){
                responseResult.setStatus(500);
                responseResult.setMessage("第一出水口缺少参数");
                flag = false;
                return flag;
            }
            //多余的提交重置
            entity.setOutletValueTwo(null);
            entity.setOutletTypeTwo(null);
            entity.setOutletTypeThree(null);
            entity.setOutletValueThree(null);
        }else if("1".equals(outlet)){
            if(ValidatorUtils.hasAnyBlank(entity.getOutletTypeOne(),entity.getOutletValueOne())){
                responseResult.setStatus(500);
                responseResult.setMessage("第一出水口缺少参数");
                flag = false;
                return flag;
            }
            if(ValidatorUtils.hasAnyBlank(entity.getOutletTypeTwo(),entity.getOutletValueTwo())){
                responseResult.setStatus(500);
                responseResult.setMessage("第二出水口缺少参数");
                flag = false;
                return flag;
            }
            entity.setOutletTypeThree(null);
            entity.setOutletValueThree(null);
        }else if("2".equals(outlet)){
            if(ValidatorUtils.hasAnyBlank(entity.getOutletTypeOne(),entity.getOutletValueOne())){
                responseResult.setStatus(500);
                responseResult.setMessage("第一出水口缺少参数");
                flag = false;
                return flag;
            }
            if(ValidatorUtils.hasAnyBlank(entity.getOutletTypeTwo(),entity.getOutletValueTwo())){
                responseResult.setStatus(500);
                responseResult.setMessage("第二出水口缺少参数");
                flag = false;
                return flag;
            }
            if(ValidatorUtils.hasAnyBlank(entity.getOutletTypeThree(),entity.getOutletValueThree())){
                responseResult.setStatus(500);
                responseResult.setMessage("第三出水口缺少参数");
                flag = false;
                return flag;
            }
        }
        return flag;
    }

    @RequestMapping(value = "/selectById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据id查询硬件设施")
    @ApiImplicitParam(name="id",value = "消火栓id",paramType = "query")
    public ObjectRestResponse selectById(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.selectById(id);
        responseResult.setData(deviceHardwareFacilities);
        return responseResult;
    }

    @RequestMapping(value = "/getStatusById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据id查询硬件设施位置和状态")
    @ApiImplicitParam(name="id",value = "消火栓id",paramType = "query")
    public ObjectRestResponse getStatusById(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.selectById(id);
        if(deviceHardwareFacilities==null){
            throw new BaseException("硬件设施不存在!");
        }
        Map map = new HashMap();
        map.put("hydrantName",deviceHardwareFacilities.getHydrantName());
        map.put("gix",deviceHardwareFacilities.getGis());
        map.put("protectionRadius",deviceHardwareFacilities.getProtectionRadius());
        String status = dsBiz.HardwareFacilities(deviceHardwareFacilities.getHydrantName());//状态
        map.put("status",status);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/selectUnitById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据id查询硬件设施地址和联网单位")
    @ApiImplicitParam(name="id",value = "消火栓id",paramType = "query")
    public ObjectRestResponse selectUnitById(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.getById(id);
        if(deviceHardwareFacilities==null){
            throw new BaseException("硬件设施不存在!");
        }
        Map map = new HashMap();
        if(StringUtils.isNotBlank(deviceHardwareFacilities.getPositionDescription())){
            map.put("address",deviceHardwareFacilities.getPositionDescription());
        }
        DeviceNetworkingUnit deviceNetworkingUnit = dnkuBiz.selectById(deviceHardwareFacilities.getOid());
        if(deviceNetworkingUnit==null){
            responseResult.setData(map);
            return responseResult;
        }
        map.put("oName",deviceNetworkingUnit.getOName());
        map.put("oPhone",deviceNetworkingUnit.getOPhone());
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getAllHardwareFacilitiesList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("实时监测下列表模式，查询所有消火栓信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "当前显示条数",paramType = "query")
    })
    public TableResultResponse getAllHardwareFacilitiesList(String page, String limit,Integer channelId) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "15";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dhfBiz.getAllHardwareFacilitiesList(query,channelId);
    }

    @IgnoreClientToken
    @RequestMapping(value = "/getHardwareFacilitiesList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询硬件设施列表")
    public ObjectRestResponse getHardwareFacilitiesList(){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        //List<DeviceHardwareFacilities> list= dhfBiz.getAll("0");
        List<DeviceHardwareFacilities> list= dhfBiz.getAllAndDelflag("0");
        responseResult.setData(list);
        return responseResult;
    }

    @IgnoreClientToken
    @IgnoreUserToken
    @RequestMapping(value = "/getByHydrantId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据id查询硬件设施")
    public ObjectRestResponse getByHydrantId(@RequestParam Integer id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.getById(id);
        responseResult.setData(deviceHardwareFacilities);
        return responseResult;
    }

    @RequestMapping(value = "/getSelected",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有的硬件设施名称,供下拉框选择")
    public ObjectRestResponse  getSelected(){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //获取所有的硬件设施名称
        List<String> hydrantName = dhfBiz.getHydrantName();
        Map<String,Object> map = new HashMap<>();
        LinkedList<String> tempbname = new LinkedList<>();
        if(hydrantName!=null&&hydrantName.size()>0){
            if(!"".equals(hydrantName.get(0))){
                tempbname.addAll(hydrantName);
            }
        }
        tempbname.addFirst("全部");
        map.put("hydrantrName",tempbname);
        responseResult.setData(map);
        return responseResult;
    }
}