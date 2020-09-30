package cn.turing.firecontrol.datahandler.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.datahandler.biz.DeviceAbnormalBiz;
import cn.turing.firecontrol.datahandler.biz.DeviceFacilitiesAbnormalBiz;
import cn.turing.firecontrol.datahandler.biz.DeviceFireMainAbnormalBiz;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import cn.turing.firecontrol.datahandler.util.Constants;
import cn.turing.firecontrol.datahandler.util.DateUtil;
import cn.turing.firecontrol.datahandler.util.SortUtil;
import cn.turing.firecontrol.datahandler.vo.ResultVo;
import cn.turing.firecontrol.datahandler.entity.DeviceFacilitiesAbnormal;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("deviceFacilitiesAbnormal")
@CheckClientToken
@CheckUserToken
@Api(tags = "硬件设施异常记录模块")
public class DeviceFacilitiesAbnormalController extends BaseController<DeviceFacilitiesAbnormalBiz,DeviceFacilitiesAbnormal,Integer> {

    @Autowired
    private  DeviceFacilitiesAbnormalBiz dfaBiz;
    @Autowired
    private DeviceAbnormalBiz daBiz;
    @Autowired
    private IDeviceFeign iDeviceFeign;
    @Autowired
    private DeviceFireMainAbnormalBiz dfmaBiz;

    @RequestMapping(value = "/selectAlrmByHydrantId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP分页查询当前消火栓故障信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hydrantId",value = "消火栓id",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "当前显示条数",paramType = "query")
    })
    public TableResultResponse selectAlrmByHydrantId(Integer hydrantId, Integer channelId, String page, String limit) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "10";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dfaBiz.selectAlrmByHydrantId(query,hydrantId,channelId);
    }

    @RequestMapping(value = "/selectCountByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据时间统计消火栓每天的异常数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "切换标识，0=本周 1=本月",paramType = "query"),
            @ApiImplicitParam(name = "hydrantId",value = "消火栓id",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceFacilitiesAbnormal> selectCountByDate(@RequestParam String tag, @RequestParam Integer hydrantId, Integer channelId) throws Exception {
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        SimpleDateFormat slf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = null;
        String endDate = null;
        if(tag !=null){
            if(tag.equals("0")){//本周
                startDate = simpleDateFormat.format(DateUtil.getBeginDayOfWeek())+" 00:00:00";
                endDate = slf.format(DateUtil.getEndTime());
            }
            if(tag.equals("1")){//本月
                Date date = new Date();
                startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date))+" 00:00:00";
                endDate = slf.format(DateUtil.getEndTime());
            }
        }
        List<ResultVo> resultList = dfaBiz.selectCountByDate(slf.parse(startDate),slf.parse(endDate),hydrantId,channelId,null);
        for(ResultVo resultVo:resultList){
            String date = resultVo.getDate().substring(5,10);
            if(date.startsWith("0")){
                date =date.substring(1,5);
            }
            resultVo.setDate(date);
        }
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/selectCountByHydranNameAndDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据时间查询消火栓对应的故障数量TOP10")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "identity",value = "时间切换标识，0=本日 1=本周 2=本月 3=本年",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse selectCountByHydranNameAndDate(@RequestParam String identity, Integer channelId) throws Exception{
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate=null;
        String endDate=null;
        if(identity.equals("0")){//今天
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("1")){//昨天
            startDate = DateUtil.getYesterdayStartDay();
            endDate = DateUtil.getYesterdayEedDay();
        }
        if(identity.equals("2")){//最近7天
            startDate = DateUtil.getRecentlySevenStartDay();
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("3")){//最近30天
            startDate = DateUtil.getRecentlyStartDay();
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        Integer allcount = dfaBiz.selectCountByHydranNameAndDate(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),null,null,channelId,null);//所有的次数
        DecimalFormat df = new DecimalFormat("0.00");
        JSONObject jsonObject = iDeviceFeign.getHardwareFacilitiesList();//查询所有消火栓
        JSONArray hardwareFacilitiesData = jsonObject.getJSONArray("data");
        List resultlist = new ArrayList();
        for(int i = 0; i<hardwareFacilitiesData.size();i++){
            Map<String ,Object> map = new HashMap<>();
            String hydrantName = hardwareFacilitiesData.getJSONObject(i).getString("hydrantName");
            Integer count = dfaBiz.selectCountByHydranNameAndDate(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),hydrantName,null,channelId,null);
            if(count!=0){
                map.put("name",hydrantName);
                map.put("count",count);
                map.put("percentage",df.format(((float)count/allcount)*100));//计算百分比
                resultlist.add(map);
            }
        }
        if(resultlist.size()>10){//如果list大于10取前10条
            responseResult.setData(SortUtil.sort(resultlist).subList(0,10));
        }else{
            responseResult.setData(SortUtil.sort(resultlist));
        }
        return responseResult;
    }

    @RequestMapping(value = "/selectCallCountByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP统计故障报警数量次数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "identity",value = "时间切换标识，0=本日 1=本周 2=本月 3=本年",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceFacilitiesAbnormal> selectCallCountByDate(@RequestParam String identity, Integer channelId) throws Exception {
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        String startDate=null;
        String endDate=null;
        if(identity.equals("0")){//本日
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("1")){//本周
            startDate = simpleDateFormat.format(DateUtil.getBeginDayOfWeek())+" 00:00:00";
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("2")){//本月
            Date date = new Date();
            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("3")){//本年
            List<ResultVo> countlist = dfaBiz.selectByYear(DateUtil.getNowYear(),channelId);
            List resultlist = new ArrayList();
            for(ResultVo resultVo:countlist){
                Map map = new HashMap();//返回数据集合
                map.put("datelist",resultVo.getMonth());
                map.put("countlist",resultVo.getCount());
                resultlist.add(map);
            }
            responseResult.setData(resultlist);
            return responseResult;
        }
        List<Date> list = DateUtil.getBetweenDates(simpleDateFormat.parse(startDate),simpleDateFormat.parse(endDate));
        List datelist = new LinkedList();
        List countlist = new LinkedList<>();
        for (Date date:list){
            Integer count = dfaBiz.selectCountByHydranNameAndDate(simpleDateFormat.parse(sdf.format(date)+" 00:00:00"),simpleDateFormat.parse(sdf.format(date)+" 23:59:59"),null,null,channelId,null);
            datelist.add(simpleDateFormat.format(date).substring(5,10).replace("-","."));
            countlist.add(count);
        }
        Map map = new HashMap();//返回数据集合
        map.put("datelist",datelist);
        map.put("countlist",countlist);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/selectAlrm",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP分页查询报警信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "handleFlag",value = "是否处理[1=是/0=否]",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "当前显示条数",paramType = "query")
    })
    public TableResultResponse selectAlrm(String handleFlag, Integer channelId, String page, String limit) {
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "10";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dfaBiz.selectAlrm(query,handleFlag,channelId);
    }

    @RequestMapping(value ="/selectAlrmByDetails",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据报警记录id查询报警详情")
    @ApiImplicitParam(name = "alarmId",value = "报警记录id",paramType = "query")
    public ObjectRestResponse selectAlrmByDetails(Integer alarmId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map map = new HashMap();
        Object object = dfaBiz.selectById(alarmId);//报警信息
        DeviceFacilitiesAbnormal deviceFacilitiesAbnormal = new DeviceFacilitiesAbnormal();
        BeanUtils.copyProperties(object,deviceFacilitiesAbnormal);
        JSONObject jsonObject = iDeviceFeign.getHardware(deviceFacilitiesAbnormal.getFireCockId());//硬件设施信息
        Integer id = jsonObject.getJSONObject("data").getInteger("id");//硬件设施id
        map.put("id",id);
        map.put("measuringPoint",deviceFacilitiesAbnormal.getMeasuringPoint());
        map.put("equipmentType",deviceFacilitiesAbnormal.getEquipmentType());
        map.put("sensorNo",deviceFacilitiesAbnormal.getSensorNo());
        map.put("bName",deviceFacilitiesAbnormal.getHydrantName());
        map.put("positionDescription",deviceFacilitiesAbnormal.getPositionDescription());
        map.put("alrmType1",deviceFacilitiesAbnormal.getAlrmType());
        //map.put("alrmType2",deviceFacilitiesAbnormal.getAlrmData()+deviceFacilitiesAbnormal.getDataUnit());
        map.put("alrmDate",deviceFacilitiesAbnormal.getAlrmDate());
        map.put("handleFlag",deviceFacilitiesAbnormal.getHandleFlag());
        map.put("alrmCategory","0");//给前端做判断
        map.put("gis",jsonObject.getJSONObject("data").getString("gis"));
        if(jsonObject.getJSONObject("data").getInteger("oid")!=null){
            JSONObject jsonObject1 = iDeviceFeign.getById(jsonObject.getJSONObject("data").getInteger("oid"));//联网单位
            //DeviceNetworkingUnit deviceNetworkingUnit =dnuBiz.selectById(deviceHardwareFacilities.getOid());
            map.put("oName",jsonObject1.getJSONObject("data").getString("oname"));
            map.put("oPhone",jsonObject1.getJSONObject("data").getString("ophone"));
        }
        if("1".equals(deviceFacilitiesAbnormal.getHandleFlag())){
                map.put("handleDate",deviceFacilitiesAbnormal.getHandleDate());
                map.put("handlePerson",deviceFacilitiesAbnormal.getHandlePerson());
                map.put("time",DateUtil.getHandletime(deviceFacilitiesAbnormal.getHandleDate(),deviceFacilitiesAbnormal.getAlrmDate()));
        }
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getAlrmBySensorId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("室外传感器根据传感器id分页查询异常记录list,云平台")
    public TableResultResponse getAlrmBySensorId(@RequestParam(value = "sensorId") String sensorId, String limit, String page){
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "10";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map<String,Object>>  resultList = new ArrayList();
        if(sensorId==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else{
            DeviceFacilitiesAbnormal temp = new DeviceFacilitiesAbnormal();
            temp.setEquId(Integer.parseInt(sensorId));
            temp.setHandleFlag("1");
            temp.setTenantId(null);
            resultList = baseBiz.selectByEquIdResultMP(temp);
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    @RequestMapping(value = "/getRatioByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("消防给水下根据时间查询隐患处理比例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceFacilitiesAbnormal> getRatioByDate(String tag, String dateStrs, Integer channelId) throws Exception {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate=null;
        String endDate=null;
        if(dateStrs!=null){
            String[] date = dateStrs.split(",");
            startDate = date[0]+" 00:00:00";
            endDate = date[1]+" 23:59:59";
        }
        if(tag!=null){
            if(tag.equals("0")){//今天
                startDate = simpleDateFormat.format(DateUtil.getStartTime());
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
            if(tag.equals("1")){//昨天
                startDate = DateUtil.getYesterdayStartDay();
                endDate = DateUtil.getYesterdayEedDay();
            }
            if(tag.equals("2")){//最近7天
                startDate = DateUtil.getRecentlySevenStartDay();
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
            if(tag.equals("3")){//最近30天
                startDate = DateUtil.getRecentlyStartDay();
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
        }
        //查询当前时间已处理数量
        Integer handleCount = dfaBiz.getCountByHandleFlag(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),"1",channelId,null);
        //查询当前时间故障报警数量
        Integer count = dfaBiz.getCountByToday(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),channelId,null);
        Integer  handlecount = dfaBiz.selectCountByDateAndHandle(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),"0",channelId,null);
        DecimalFormat df = new DecimalFormat("0.0000");
        Map map = new HashMap();
        if(count!=0){
            Float handle_percentage = Float.parseFloat(df.format((float)handleCount/count))*100;
            Float notHandle_percentage = 100-handle_percentage;
            map.put("handle_percentage",handle_percentage);
            map.put("notHandle_percentage",notHandle_percentage);
            map.put("handleCount",handleCount);
            map.put("count",handlecount);
        }else{
            map.put("handle_percentage","100");
            map.put("notHandle_percentage","0");
            map.put("handleCount",handleCount);
            map.put("count",handlecount);
        }
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getRatio",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("概览下查询近7天隐患处理比例")
    public ObjectRestResponse getRatio() throws Exception {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = DateUtil.getRecentlySevenStartDay();
        String endDate = simpleDateFormat.format(DateUtil.getEndTime());
        //查询当前时间已处理数量
        Integer handleCount = dfaBiz.getCountByHandleFlag(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),"1",null,null);
        //查询当前时间故障报警数量
//        Integer count = dfaBiz.getCountByToday(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),null,null);
        Integer  handlecount = dfaBiz.selectCountByDateAndHandle(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),"0",null,null);

        //消防给水
        Integer handleCoun1 = daBiz.getCountByHandleFlag(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),"1",null,null);
//        Integer count1 = daBiz.getCountByToday(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),null,null,null);
        Integer  handlecount1 = daBiz.selectCountByDateAndHandle(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),"0",null);

        //消防主机
        Integer handleCount2 = dfmaBiz.getCountByHandleFlag(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),"1",null,null);
        //查询当前时间故障报警数量
//        Integer count2 = dfmaBiz.getCountByToday(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),null,null,null);
        Integer  handlecount2 = dfmaBiz.selectCountByDateAndHandle(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),"0",null);

//        DecimalFormat df = new DecimalFormat("0.0000");
        Map map = new HashMap();
        Integer handleNum = handleCount+handleCoun1+handleCount2;
//        Integer countNum = count+count1+count2;
        Integer notHandleNum = handlecount+handlecount1+handlecount2;
//        if(count!=0){
//            Float handle_percentage = Float.parseFloat(df.format((float)handleNum/countNum))*100;
//            Float notHandle_percentage = 100-handle_percentage;
//            map.put("handle_percentage",handle_percentage);
//            map.put("notHandle_percentage",notHandle_percentage);
            map.put("handleCount",handleNum);
            map.put("count",notHandleNum);
//        }else{
//            map.put("handle_percentage","100");
//            map.put("notHandle_percentage","0");
//            map.put("handleCount",handleNum);
//            map.put("count",notHandleNum);
//        }
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getCallCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("概览下查询近7天报警数量")
    public ObjectRestResponse getCallCount() throws Exception {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List countList = new ArrayList();
        List dateList = new ArrayList();
        String startDate = DateUtil.getRecentlySevenStartDay();//开始时间
        String endDate = simpleDateFormat.format(DateUtil.getEndTime());//结束时间
        List<Date> list = DateUtil.getBetweenDates(sdf.parse(startDate),sdf.parse(endDate));
        Map map = new HashMap();
        for(Date date:list){
            //报警数量
            Integer count = daBiz.getCountByToday(simpleDateFormat.parse(sdf.format(date)+" 00:00:00"), simpleDateFormat.parse(sdf.format(date)+" 23:59:59"),"1",null,null);
            //消防主机
            Integer count1 = dfmaBiz.getCountByToday(simpleDateFormat.parse(sdf.format(date)+" 00:00:00"),simpleDateFormat.parse(sdf.format(date)+" 23:59:59"),"1",null,null);
            countList.add(count+count1);
            dateList.add(sdf.format(date));
        }
        map.put("countList",countList);
        map.put("dateList",dateList);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getFaultCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("概览下查询近7天故障数量")
    public ObjectRestResponse getFaultCount() throws Exception {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List countList = new ArrayList();
        List dateList = new ArrayList();
        String startDate = DateUtil.getRecentlySevenStartDay();//开始时间
        String endDate = simpleDateFormat.format(DateUtil.getEndTime());//结束时间
        List<Date> list = DateUtil.getBetweenDates(sdf.parse(startDate),sdf.parse(endDate));
        Map map = new HashMap();
        for(Date date:list){
            //故障数量
            Integer count1 = daBiz.getCountByToday(simpleDateFormat.parse(sdf.format(date)+" 00:00:00"), simpleDateFormat.parse(sdf.format(date)+" 23:59:59"),"0",null,null);
            //消防给水
            Integer count2 = dfaBiz.getCountByToday(simpleDateFormat.parse(sdf.format(date)+" 00:00:00"), simpleDateFormat.parse(sdf.format(date)+" 23:59:59"),null,null);
            //消防主机
            Integer count3 = dfmaBiz.getCountByToday(simpleDateFormat.parse(sdf.format(date)+" 00:00:00"), simpleDateFormat.parse(sdf.format(date)+" 23:59:59"),"0",null,null);
            //总数
            Integer count = count1+count2+count3;
            countList.add(count);
            dateList.add(sdf.format(date));
        }
        map.put("countList",countList);
        map.put("dateList",dateList);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "tag",value = "前端切换标识",paramType = "query"),
            @ApiImplicitParam(name = "hydrantName",value = "消火栓名称，条件搜索参数",paramType = "query"),
            @ApiImplicitParam(name = "equipmentType",value = "设备系列，条件搜索参数",paramType = "query"),
            @ApiImplicitParam(name = "sensorNo",value = "设备编号，条件搜索参数",paramType = "query"),
            @ApiImplicitParam(name = "measuringPoint",value = "测点，条件搜索参数",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public TableResultResponse<DeviceFacilitiesAbnormal> pageList(@RequestParam String page, @RequestParam String limit, DeviceFacilitiesAbnormal deviceFacilitiesAbnormal,
                                                                  String dateStrs, @RequestParam String tag, String hydrantName, String equipmentType, String sensorNo, String measuringPoint, Integer channelId){
        Map<String ,Object> param = new LinkedHashMap<>();
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        String startDate = null;
        String endDate = null;
        if(dateStrs !=null){
            String[] date = dateStrs.split(",");
            startDate = date[0];
            endDate = date[1];
        }
        return dfaBiz.selectQuery(query,deviceFacilitiesAbnormal,startDate,endDate,tag,hydrantName,equipmentType,sensorNo,measuringPoint,channelId);
    }

    @RequestMapping(value = "/selectAlrmType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询异常记录下所有报警类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "handleFlag",value = "是否处理",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceFacilitiesAbnormal> selectAlrmType(String handleFlag, Integer channelId) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<String> list = dfaBiz.selectAlrmType(handleFlag,channelId,null);//查询报警类型列表
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/selectMeasuringPoint",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询异常记录下所有测点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "handleFlag",value = "是否处理",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceFacilitiesAbnormal> selectMeasuringPoint(String handleFlag, Integer channelId) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<String> list = dfaBiz.selectMeasuringPoint(handleFlag,channelId,null);//查询报警类型列表
        list.add(0,"全部");
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/affirmHandle",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("确认处理")
    @ApiImplicitParam(name = "id",value = "异常记录id",paramType = "query")
    public ObjectRestResponse<DeviceFacilitiesAbnormal> affirmHandle(@RequestParam Integer id) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        if(id==null ){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            Object object = dfaBiz.selectById(id);
            DeviceFacilitiesAbnormal deviceFacilitiesAbnormal = new DeviceFacilitiesAbnormal();
            BeanUtils.copyProperties(object,deviceFacilitiesAbnormal);
            deviceFacilitiesAbnormal.setHandleFlag("1");//是否处理[1=是/0=否]'
            deviceFacilitiesAbnormal.setHandlePerson(BaseContextHandler.getUsername());//处理人
            deviceFacilitiesAbnormal.setHandleDate(new Date());//处理时间
            dfaBiz.updateSelectiveById(deviceFacilitiesAbnormal);
        }
        return responseResult;
    }

    @RequestMapping(value = "/selectCountByType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据报警类型查询对应的异常数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，用','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceFacilitiesAbnormal> selectCountByType(String tag, String dateStrs, Integer channelId) throws Exception{
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = null;
        String endDate = null;
        if(dateStrs !=null){
            String[] date = dateStrs.split(",");
            startDate = date[0];
            endDate = date[1];
        }
        if(tag !=null){
            if(tag.equals("0")){//今天
                startDate = simpleDateFormat.format(DateUtil.getStartTime());
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
            if(tag.equals("1")){//昨天
                startDate = DateUtil.getYesterdayStartDay();
                endDate = DateUtil.getYesterdayEedDay();
            }
            if(tag.equals("2")){//最近7天
                startDate = DateUtil.getRecentlySevenStartDay();
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
            if(tag.equals("3")){//最近30天
                startDate = DateUtil.getRecentlyStartDay();
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
        }
        if(startDate==null && endDate==null){//最近7天
            startDate = DateUtil.getRecentlySevenStartDay();
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        List<String> typelist = dfaBiz.selectAlrmType(null,channelId,null);//报警类型list
        Integer allCount = dfaBiz.selectCountByType(simpleDateFormat.parse(startDate+ " 00:00:00"),
                simpleDateFormat.parse(endDate + " 23:59:59"),null,channelId,null);
        DecimalFormat df = new DecimalFormat("0.00");
        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
        for (String s:typelist){
            Map<String ,Object> map = new HashMap<>();
            Integer count = dfaBiz.selectCountByType(simpleDateFormat.parse(startDate+ " 00:00:00"),
                    simpleDateFormat.parse(endDate + " 23:59:59"),s,channelId,null);
            if(count!=0){
                map.put("alrmType",s);
                map.put("count",count);
                map.put("percentage",df.format(((float)count/allCount)*100));//比例
                resultList.add(map);
            }
        }
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/selectFaultCountByMonth",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("每天的故障数量统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，用','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceFacilitiesAbnormal> selectFaultCountByMonth(String tag, String dateStrs, Integer channelId) throws Exception {
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        SimpleDateFormat  slf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = null;
        String endDate = null;
        if(dateStrs !=null){
            String[] date = dateStrs.split(",");
            startDate = date[0]+" 00:00:00";
            endDate = date[1]+" 23:59:59";
        }
        if(tag !=null){
            if(tag.equals("0")){//今天
                startDate = slf.format(DateUtil.getStartTime());
                endDate = slf.format(DateUtil.getEndTime());
            }
            if(tag.equals("1")){//昨天
                startDate = DateUtil.getYesterdayStartDay();
                endDate = DateUtil.getYesterdayEedDay();
            }
            if(tag.equals("2")){//最近7天
                startDate = DateUtil.getRecentlySevenStartDay();
                endDate = slf.format(DateUtil.getEndTime());
            }
            if(tag.equals("3")){//最近30天
                startDate = DateUtil.getRecentlyStartDay();
                endDate = slf.format(DateUtil.getEndTime());
            }
        }
        if(startDate==null && endDate==null){//最近7天
            startDate = DateUtil.getRecentlySevenStartDay();
            endDate = slf.format(DateUtil.getEndTime());
        }
        List<ResultVo> resultList = dfaBiz.selectCountByDate(slf.parse(startDate),slf.parse(endDate),null,channelId,null);
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/getAllAlrmAndFault",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("消防给水实时监测，故障处理列表，未处理的记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public TableResultResponse getAllAlrmAndFault(Integer channelId, String limit, String page){
        if(StringUtils.isBlank(limit)){
            limit="20";
        }
        if(StringUtils.isBlank(page)){
            page="1";
        }
        Map<String,Object> param=new HashMap<>();
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map> list = dfaBiz.selectByHandelFlagAndAlrm("0",channelId,null);
        for(Map map:list){
            map.put("date",DateUtil.getFriendlytime((Date) map.get("alrmDate")));
        }
        return new TableResultResponse(result.getTotal(),list);
    }

    @RequestMapping(value = "/selectByHydrantId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据消火栓id查询硬件设施报警记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "消火栓id",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public TableResultResponse selectByHydrantId(@RequestParam Integer id, String limit, String page, Integer channelId){
        if(StringUtils.isBlank(limit)){
            limit="10";
        }
        if(StringUtils.isBlank(page)){
            page="1";
        }
        Map<String,Object> param=new HashMap<>();
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map> list = dfaBiz.selectByHydrantId(id,channelId,null);
        return new TableResultResponse(result.getTotal(),list);
    }

//    @RequestMapping(value = "/selectAllAlrm",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("查询所有未处理异常记录")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "page",value = "当前页码",paramType = "query"),
//            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query")
//    })
//    public TableResultResponse selectAllAlrm(String limit, String page,String flag){
//        if(StringUtils.isBlank(limit)){
//            limit="15";
//        }
//        if(StringUtils.isBlank(page)){
//            page="1";
//        }
//        Map<String,Object> param=new HashMap<>();
//        param.put("page",page);
//        param.put("limit",limit);
//        Query query = new Query(param);
//        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
//        List<AlrmVo> list = dfaBiz.selectAllAlrm("0");
//        if(StringUtils.isNotBlank(flag)){
//            return new TableResultResponse(result.getTotal(),null);
//        }
//        for (AlrmVo alrmVo:list){
//            if(alrmVo.getId()!=null){
//                JSONObject jsonObject = iDeviceFeign.getHardware(alrmVo.getId());//硬件设施信息
//                //DeviceHardwareFacilities deviceHardwareFacilities = dhfBiz.getById(alrmVo.getId());
//                if("0".equals(jsonObject.getJSONObject("data").getString("facilityType"))){
//                    alrmVo.setFacilityType("室外消火栓");
//                }
//            }
//        }
//        return new TableResultResponse(result.getTotal(),list);
//    }

//    @RequestMapping(value = "/selectNotHandleFlag",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("查询未处理异常记录")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "page",value = "当前页码",paramType = "query"),
//            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
//            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
//    })
//    public TableResultResponse selectNotHandleFlag(String limit, String page,Integer channelId){
//        if(StringUtils.isBlank(limit)){
//            limit="15";
//        }
//        if(StringUtils.isBlank(page)){
//            page="1";
//        }
//        Map<String,Object> param=new HashMap<>();
//        param.put("page",page);
//        param.put("limit",limit);
//        Query query = new Query(param);
//        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
//        List<DeviceFacilitiesAbnormal> list = dfaBiz.selectAbnormal("0",channelId);
//        List resultList = new ArrayList();
//        for(DeviceFacilitiesAbnormal abnormal:list){
//            Map map = new HashMap();
//            JSONObject jsonObject = iDeviceFeign.getHardware(abnormal.getFireCockId());//硬件设施信息
//            if("0".equals(jsonObject.getJSONObject("data").getString("facilityType"))){
//                map.put("name","室外消火栓"+abnormal.getHydrantName());
//            }
//            map.put("id",abnormal.getId());
//            map.put("type",abnormal.getEquipmentType()+"设备"+abnormal.getMeasuringPoint()+abnormal.getAlrmType());
//            map.put("date",abnormal.getAlrmDate());
//            map.put("logId",abnormal.getLogId());
//            resultList.add(map);
//        }
//        return new TableResultResponse(result.getTotal(),resultList);
//    }

    @RequestMapping(value = "/getCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("概览下查询近7天地图上异常数量")
    public ObjectRestResponse getCount() throws Exception {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JSONObject building = iDeviceFeign.getBuildingList();
        JSONArray buildingArray= building.getJSONArray("data");//建筑列表

        JSONObject HardwareFacilities = iDeviceFeign.getHardwareFacilitiesList();
        JSONArray hardwareFacilitiesArray= HardwareFacilities.getJSONArray("data");//硬件设施列表

        String startDate = DateUtil.getRecentlySevenStartDay();//开始时间
        String endDate = simpleDateFormat.format(DateUtil.getEndTime());//结束时间
        List resultList = new ArrayList();
        Set<String> set = new HashSet();
        List<String> bNameLsit = daBiz.getBNameByDate(startDate,endDate);
        set.addAll(bNameLsit);
        List<String> bNameLsit1 = dfmaBiz.getBNameByDate(startDate,endDate);
        set.addAll(bNameLsit1);
        List<String> hardwareFacilitiesLsit = dfaBiz.getHardwareFacilitiesByDate(startDate,endDate);
        for (int i =0;i<buildingArray.size();i++){
            Map map = new HashMap();
            String bName = buildingArray.getJSONObject(i).getString("bname");//建筑名称
            if(set.contains(bName)){
                //电气火灾  火灾报警
                Integer count0 = daBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate),simpleDateFormat.parse(endDate),bName,null,null,null);
                //消防主机
                Integer count1 = dfmaBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate),simpleDateFormat.parse(endDate),bName,null,null,null);
                Integer count = count0+count1;
                if(count!=0){
                    map.put("gis",buildingArray.getJSONObject(i).getString("gis"));
                    map.put("count",count+count1);
                    resultList.add(map);
                }
            }
        }
        for (int i =0;i<hardwareFacilitiesArray.size();i++){
            Map map = new HashMap();
            String hydrantName = hardwareFacilitiesArray.getJSONObject(i).getString("hydrantName");//硬件设施名称
            if(hardwareFacilitiesLsit.contains(hydrantName)){
                //消防给水
                Integer count = dfaBiz.selectCountByHydranNameAndDate(simpleDateFormat.parse(startDate),simpleDateFormat.parse(endDate),hydrantName,null,null,null);
                if(count!=0){
                    map.put("gis",hardwareFacilitiesArray.getJSONObject(i).getString("gis"));
                    map.put("count",count);
                    resultList.add(map);
                }
            }
        }
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/getNotHandlerCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取消火栓待处理个数")
    @ApiImplicitParam(name = "ids",value = "消火栓ids",paramType = "query")
    public ObjectRestResponse getNotHandlerCount(@RequestParam String ids) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        if(StringUtils.isBlank(ids)){
            return responseResult;
        }
        String [] hydrantIds = ids.split(",");
        List resultList = new ArrayList();
        for(int i = 0;i<hydrantIds.length;i++){
            JSONObject jsonObject = iDeviceFeign.getHardware(Integer.valueOf(hydrantIds[i]));
            String hydrantName = jsonObject.getJSONObject("data").getString("hydrantName");
            Integer count = dfaBiz.selectCountByHydranNameAndDate(null,null,hydrantName,"0",null,null);
            resultList.add(count);
        }
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/getAlrmListBySensorId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器id分页查询异常记录listAPP")
    public TableResultResponse getAlrmListBySensorId(@RequestParam(value = "sensorId") String sensorId, String limit, String page){
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "10";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List resultList = new ArrayList();
        if(sensorId==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else{
            List<DeviceFacilitiesAbnormal> list = dfaBiz.selectByEquId(Long.valueOf(sensorId),"1",null,null);
            for(DeviceFacilitiesAbnormal deviceFacilitiesAbnormal:list){
                Map map = new HashMap();
                map.put("id",deviceFacilitiesAbnormal.getId());
                map.put("type",deviceFacilitiesAbnormal.getAlrmType());
                map.put("alrmDate",deviceFacilitiesAbnormal.getAlrmDate());
                map.put("alrmCategory","0");
                map.put("handlePerson",deviceFacilitiesAbnormal.getHandlePerson());
                map.put("handleDate",deviceFacilitiesAbnormal.getHandleDate());
                if(deviceFacilitiesAbnormal.getDataUnit()!=null){
                    map.put("alarmData",deviceFacilitiesAbnormal.getAlrmData()+deviceFacilitiesAbnormal.getDataUnit());
                }else{
                    map.put("alarmData",deviceFacilitiesAbnormal.getAlrmData());
                }
                map.put("measuringPoint",deviceFacilitiesAbnormal.getMeasuringPoint());
                resultList.add(map);
            }
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }
}