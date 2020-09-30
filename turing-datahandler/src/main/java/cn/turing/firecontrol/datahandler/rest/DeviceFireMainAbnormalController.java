package cn.turing.firecontrol.datahandler.rest;

import cn.turing.firecontrol.auth.client.annotation.*;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.datahandler.biz.DeviceFireMainAbnormalBiz;
import cn.turing.firecontrol.datahandler.entity.DeviceFireMainAbnormal;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import cn.turing.firecontrol.datahandler.util.Constants;
import cn.turing.firecontrol.datahandler.util.DateUtil;
import cn.turing.firecontrol.datahandler.util.SortUtil;
import cn.turing.firecontrol.datahandler.util.ValidatorUtils;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("deviceFireMainAbnormal")
@CheckClientToken
@CheckUserToken
@Api(tags = "消防主机异常记录模块")
@Slf4j
public class DeviceFireMainAbnormalController extends BaseController<DeviceFireMainAbnormalBiz, DeviceFireMainAbnormal,Integer> {

    @Autowired
    private DeviceFireMainAbnormalBiz dfmaBiz;
    @Autowired
    private IDeviceFeign iDeviceFeign;

    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "报警时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "tag",value = "前端切换标识",paramType = "query"),
            @ApiImplicitParam(name = "bName",value = "建筑名称，条件搜索参数",paramType = "query"),
            @ApiImplicitParam(name = "fireMainId",value = "消防主机ID，条件搜索参数",paramType = "query"),
            @ApiImplicitParam(name = "sensorLoop",value = "回路，条件搜索参数",paramType = "query"),
            @ApiImplicitParam(name = "address",value = "地址，条件搜索参数",paramType = "query"),
            @ApiImplicitParam(name = "series",value = "系列，条件搜索参数",paramType = "query"),
            @ApiImplicitParam(name = "floorId",value = "楼层，条件搜索参数",paramType = "query"),
            @ApiImplicitParam(name = "positionDescription",value = "位置描述，条件搜索参数",paramType = "query"),
            @ApiImplicitParam(name = "handlePerson",value = "处理人，条件搜索参数",paramType = "query"),
            @ApiImplicitParam(name = "handleDateStrs",value = "处理时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public TableResultResponse<DeviceFireMainAbnormal> pageList(@RequestParam String page, @RequestParam String limit, String dateStrs, @RequestParam String tag, String bName, String fireMainId,
                                                                String sensorLoop, String address,String series,String alrmType,String floorId,String positionDescription, String handlePerson,String handleDateStrs, Integer channelId){
        Map<String ,Object> param = new LinkedHashMap<>();
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        String startDate = null;
        String endDate = null;
        if(StringUtils.isNotBlank(dateStrs) && !"null".equals(dateStrs)){
            String[] date = dateStrs.split(",");
            startDate = date[0];
            endDate = date[1];
        }
        String handleStartDate = null;
        String handleEndDate = null;
        if(StringUtils.isNotBlank(handleDateStrs)){
            String[] date = handleDateStrs.split(",");
            handleStartDate = date[0];
            handleEndDate = date[1];
        }
        return dfmaBiz.selectQuery(query,startDate,endDate,tag,bName,fireMainId,sensorLoop,address,series,alrmType,floorId,positionDescription,handlePerson,handleStartDate,handleEndDate,channelId);
    }

    @RequestMapping(value = "/selectAlrmType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有报警类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query"),
            @ApiImplicitParam(name = "alrmCategory",value = "报警类型：0：故障，1：火警",paramType = "query"),
            @ApiImplicitParam(name = "handleFlag",value = "是否处理[1=是/0=否]",paramType = "query")
    })
    public ObjectRestResponse<DeviceFireMainAbnormal> selectAlrmType(Integer channelId, String alrmCategory, String handleFlag) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<String> list = dfmaBiz.selectAlrmType(channelId,alrmCategory,handleFlag,null);//查询报警类型列表
        //list.add(0,"全部");
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/selectCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有真实火警、误报火警、故障报警数量")
    @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    public ObjectRestResponse<DeviceFireMainAbnormal> selectCount(Integer channelId) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Integer falseFire = dfmaBiz.selectCountByAlrmCategoryAndFireFlag("0","1","1",channelId,null);//误报
        Integer realFire = dfmaBiz.selectCountByAlrmCategoryAndFireFlag("1","1","1",channelId,null);//真实火警
        Integer faultAlarm = dfmaBiz.selectCountByAlrmCategoryAndFireFlag(null,"0",null,channelId,null);//故障报警
        Map<String,Object> map = new HashMap<>();//返回map
        map.put("realFire",realFire);
        map.put("falseFire",falseFire);
        map.put("faultAlarm",faultAlarm);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/affirmFire",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("确认是否是真实火警,并处理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "异常记录id",paramType = "query"),
            @ApiImplicitParam(name = "flag",value = "是否为真实火警 2=火警测试/1=是/0=否",paramType = "query")
    })
    public ObjectRestResponse<DeviceFireMainAbnormal> affirmFire(@RequestParam Integer id,String flag) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        if(id==null || flag==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            Object object = dfmaBiz.selectById(id);
            DeviceFireMainAbnormal deviceFireMainAbnormal = new DeviceFireMainAbnormal();
            BeanUtils.copyProperties(object,deviceFireMainAbnormal);
            if(StringUtils.isNotBlank(flag)){
                deviceFireMainAbnormal.setFireFlag(flag);//是否为真实火警[1=是/0=否/2=火警测试]
            }
            deviceFireMainAbnormal.setHandleFlag("1");
            deviceFireMainAbnormal.setHandlePerson(BaseContextHandler.getUsername());//处理人
            deviceFireMainAbnormal.setHandleDate(new Date()); //处理时间
            dfmaBiz.updateSelectiveById(deviceFireMainAbnormal);
        }
        return responseResult;
    }

    @RequestMapping(value = "/getRatioByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据时间查询隐患处理比例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceFireMainAbnormal> getRatioByDate(String tag, String dateStrs, Integer channelId) throws Exception {
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
        if(startDate==null && endDate==null){//今天
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        //查询当前时间已处理数量
        Integer handleCount = dfmaBiz.getCountByHandleFlag(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),"1",channelId,null);
        //查询当前时间报警数量
        Integer count = dfmaBiz.getCountByToday(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),null,channelId,null);
        Integer notHandleCount = dfmaBiz.getCount(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),null,"0",channelId,null);
        DecimalFormat df = new DecimalFormat("0.0000");
        Map map = new HashMap();
        if(count!=0){
            Float handle_percentage = Float.parseFloat(df.format((float)handleCount/count))*100;
            Float notHandle_percentage = 100-handle_percentage;
            map.put("handle_percentage",handle_percentage);
            map.put("notHandle_percentage",notHandle_percentage);
            map.put("handleCount",handleCount);
            map.put("callCount",notHandleCount);
        }else{
            map.put("handle_percentage","100");
            map.put("notHandle_percentage","0");
            map.put("handleCount",handleCount);
            map.put("callCount",notHandleCount);
        }
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/selectCountByType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据报警类型查询对应的异常数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceFireMainAbnormal> selectCountByType(String tag, String dateStrs,Integer channelId) throws Exception{
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
        if(startDate==null && endDate==null){//今天
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        List<String> typelist = dfmaBiz.selectAlrmType(channelId,null,null,null);//报警类型list
        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
        Integer allCount = dfmaBiz.selectCountByType(simpleDateFormat.parse(startDate+ " 00:00:00"),
                simpleDateFormat.parse(endDate + " 23:59:59"),null,channelId,null);//总共个数
        DecimalFormat df = new DecimalFormat("0.00");
        for (String s:typelist){
            Map<String ,Object> map = new HashMap<>();
            Integer count = dfmaBiz.selectCountByType(simpleDateFormat.parse(startDate+ " 00:00:00"),
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

    @RequestMapping(value = "/selectCountByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("每天的故障,报警数量统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceFireMainAbnormal> selectCountByDate(String tag, String dateStrs, Integer channelId) throws Exception {
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = null;
        String endDate = null;
        if(dateStrs !=null){
            String[] date = dateStrs.split(",");
            startDate = date[0]+" 00:00:00";
            endDate = date[1]+" 23:59:59";
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
        if(startDate==null && endDate==null){//今天
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        Map map = new HashMap();
        List faultList = new ArrayList();
        List callList = new ArrayList();
        List dateList = new ArrayList();
        List<Date> datelist = DateUtil.getBetweenDates(sdf.parse(startDate),sdf.parse(endDate));
        for(Date date:datelist){
            Date beginDate = simpleDateFormat.parse(sdf.format(date)+" 00:00:00");
            Date lastDate = simpleDateFormat.parse(sdf.format(date)+" 23:59:59");
            Integer faultCount = dfmaBiz.getCountByToday(beginDate,lastDate,"0",channelId,null);
            Integer callCount = dfmaBiz.getCountByToday(beginDate,lastDate,"1",channelId,null);
            if(faultCount ==0 && callCount==0){
                continue;
            }
            faultList.add(faultCount);
            callList.add(callCount);
            dateList.add(sdf.format(date));
        }
        map.put("faultList",faultList);
        map.put("callList",callList);
        map.put("dateList",dateList);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/selectCountByBuildId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑物查询对应的异常数量并排名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query"),
            @ApiImplicitParam(name = "alrmCategory",value = "报警类型：0：故障，1：火警",paramType = "query")
    })
    public ObjectRestResponse<DeviceFireMainAbnormal> selectCountByBuildId(String tag, String dateStrs,Integer channelId,@RequestParam String alrmCategory) throws Exception{
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
        if(startDate==null && endDate==null){//今天
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        //查询建筑列表
        JSONObject jsonObject = iDeviceFeign.getBuildingList();
        JSONArray buildingData=jsonObject.getJSONArray("data");
        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
        DecimalFormat df = new DecimalFormat("0.00");
        Integer allCount = dfmaBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate+ " 00:00:00"),simpleDateFormat.parse(endDate + " 23:59:59"),null,alrmCategory,channelId,null);
        for(int i = 0; i<buildingData.size(); i++){
            Map<String ,Object> map = new HashMap<>();
            String bName = buildingData.getJSONObject(i).getString("bname");
            Integer count = dfmaBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate+ " 00:00:00"),simpleDateFormat.parse(endDate + " 23:59:59"),bName,alrmCategory,channelId,null);
            if(count!=0){
                map.put("name",bName);
                map.put("count",count);
                map.put("percentage",df.format(((float)count/allCount)*100));
                resultList.add(map);
            }
        }
        if(resultList.size()>1){
            SortUtil.sort(resultList);//按count排序
        }
        int i =1;
        for (Map map:resultList) {//生成排名id
            map.put("id",i);
            i =i+1;
        }
        if(resultList.size()>10){//如果list大于10取前10条
            responseResult.setData(resultList.subList(0,10));
        }else{
            responseResult.setData(resultList);
        }
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/getAllAlrmAndFault",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("实时监测，火警处理列表-故障处理列表，未处理的记录")
    public TableResultResponse getAllAlrmAndFault(Integer channelId, @RequestParam String alrmCategory, String limit, String page){
        if(StringUtils.isBlank(limit)){
            limit="20";
        }
        if(StringUtils.isBlank(page)){
            page="1";
        }
        Map<String,Object> param=new HashMap<>();
        List<Map> lists = new ArrayList<>();
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map> list = dfmaBiz.selectByHandelFlagAndAlrm(alrmCategory,"0",channelId,null);
        for (Map deviceFireMainAbnormal:list){
            Map<String,Object> map=new HashMap<>();
            map.put("buildId",deviceFireMainAbnormal.get("buildId"));
            map.put("id",deviceFireMainAbnormal.get("id"));
            map.put("bName",deviceFireMainAbnormal.get("bName"));
            map.put("series",deviceFireMainAbnormal.get("series"));
            map.put("position",deviceFireMainAbnormal.get("floor")+"F-"+deviceFireMainAbnormal.get("positionDescription"));
            //map.put("alarmType",deviceFireMainAbnormal.get("alrmType"));
            map.put("date",DateUtil.getFriendlytime((Date) deviceFireMainAbnormal.get("alrmDate")));
            map.put("logId",deviceFireMainAbnormal.get("logId"));
            lists.add(map);
        }
        return new TableResultResponse(result.getTotal(),lists);
    }

    @RequestMapping(value = "/selectAbnormal",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询建筑物对应楼层传感器报警记录")
    public TableResultResponse selectAbnormal(String page,String limit,@RequestParam Integer buildId,Integer floor,Integer channelId){
        if(StringUtils.isBlank(limit)){
            limit="15";
        }
        if(StringUtils.isBlank(page)){
            page="1";
        }
        Map<String ,Object> param = new LinkedHashMap<>();
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dfmaBiz.selectAbnormal(query,buildId,floor,channelId);
    }

    @RequestMapping(value = "/getAlrmBySensorId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器id分页查询异常记录list,云平台")
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
            DeviceFireMainAbnormal deviceFireMainAbnormal = new DeviceFireMainAbnormal();
            deviceFireMainAbnormal.setEquId(Integer.parseInt(sensorId));
            deviceFireMainAbnormal.setHandleFlag("1");
            deviceFireMainAbnormal.setTenantId(null);
            resultList = dfmaBiz.selectByEquIdResultMP(deviceFireMainAbnormal);
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    @RequestMapping(value = "/getAlrmCountBySensonId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据传感器id查询报警故障次数")
    public ObjectRestResponse getAlrmCountBySensonId(String ids){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List result = new LinkedList();
        if(StringUtils.isBlank(ids)){
            return responseResult;
        }
        String [] sensonIds = ids.split(",");
        for(int i = 0;i<sensonIds.length;i++){
            Map map = new HashMap();
            Long id = Long.valueOf(sensonIds[i]);
            Integer faultCount = dfmaBiz.selectCountBySensorId(id,"0",null);//故障次数
            Integer alrmCount = dfmaBiz.selectCountBySensorId(id,"1",null);//报警次数
            map.put("faultCount",faultCount);
            map.put("alrmCount",alrmCount);
            result.add(map);
        }
        responseResult.setData(result);
        return responseResult;
    }

    @RequestMapping(value = "/selectAlrm",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP分页查询报警信息")
    public TableResultResponse selectAlrm(String handleFlag, String alrmCategory, Integer channelId, String page, String limit) {
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
        return dfmaBiz.selectAlrm(query,handleFlag,alrmCategory,channelId);
    }

    @RequestMapping(value ="/selectAlrmByDetails",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据传感器id和报警记录id查询报警详情")
    public ObjectRestResponse selectAlrmByDetails(Integer alarmId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map map = new HashMap();
        Object object= dfmaBiz.selectById(alarmId);//报警信息
        DeviceFireMainAbnormal deviceFireMainAbnormal= new DeviceFireMainAbnormal();
        BeanUtils.copyProperties(object,deviceFireMainAbnormal);
        JSONObject building = iDeviceFeign.getBuilding(deviceFireMainAbnormal.getBuildId());//查询建筑物信息
        Integer id = building.getJSONObject("data").getInteger("oid");//联网单位id
        JSONObject networkingUnit = iDeviceFeign.getById(id);//查询联网单位
        JSONObject fireMain = iDeviceFeign.getFireMain(deviceFireMainAbnormal.getFireMainId());//查询消防主机
        map.put("fireMainPositionDescription",fireMain.getJSONObject("data").getString("positionDescription"));//消防主机位置
        map.put("equipmentType",deviceFireMainAbnormal.getSeries());
        map.put("sensorLoop",deviceFireMainAbnormal.getSensorLoop());
        map.put("address",deviceFireMainAbnormal.getAddress());
        map.put("fireFlag",deviceFireMainAbnormal.getFireFlag());
        map.put("bName",deviceFireMainAbnormal.getbName());
        map.put("positionDescription",deviceFireMainAbnormal.getFloor()+"F-"+deviceFireMainAbnormal.getPositionDescription());
        map.put("alrmType1",deviceFireMainAbnormal.getAlrmType());
        map.put("alrmDate",deviceFireMainAbnormal.getAlrmDate());
        map.put("handleFlag",deviceFireMainAbnormal.getHandleFlag());
        map.put("safeDutyName",networkingUnit.getJSONObject("data").getString("safeDutyName"));
        map.put("safeDutyPhone",networkingUnit.getJSONObject("data").getString("safeDutyPhone"));
        map.put("alrmCategory",deviceFireMainAbnormal.getAlrmCategory());
        if(deviceFireMainAbnormal.getHandleFlag().equals("1")){
            map.put("handleDate",deviceFireMainAbnormal.getHandleDate());
            map.put("handlePerson",deviceFireMainAbnormal.getHandlePerson());
            map.put("time",DateUtil.getHandletime(deviceFireMainAbnormal.getHandleDate(),deviceFireMainAbnormal.getAlrmDate()));
        }
        responseResult.setData(map);
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
            List<DeviceFireMainAbnormal> list = dfmaBiz.selectByEquId(Long.valueOf(sensorId),"1",null);
            for(DeviceFireMainAbnormal deviceFireMainAbnormal:list){
                Map map = new HashMap();
                map.put("id",deviceFireMainAbnormal.getId());
                map.put("type",deviceFireMainAbnormal.getAlrmType());
                map.put("alrmDate",deviceFireMainAbnormal.getAlrmDate());
                map.put("alrmCategory","0");
                map.put("handlePerson",deviceFireMainAbnormal.getHandlePerson());
                map.put("handleDate",deviceFireMainAbnormal.getHandleDate());
                map.put("alarmData",deviceFireMainAbnormal.getAlrmData());
                resultList.add(map);
            }
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }


    @GetMapping("selectCountNearlyMonth")
    @ApiOperation("查询最近N个月各状态（火警，故障，误报）的数量")
    public ObjectRestResponse selectCountNearlyMonth(@RequestParam(defaultValue = "5") Integer count){
        Map<String,Object[]> map = baseBiz.selectCountNearlyMonth(count);
        return new ObjectRestResponse().data(map);
    }

    @RequestMapping(value = "/restore",method = RequestMethod.GET)
    @ResponseBody
    @IgnoreUserToken
    @IgnoreClientToken
    @ApiOperation("恢复异常")
    public ObjectRestResponse restore(String serverIp,String port,String sensorLoop,String address){
        if(ValidatorUtils.hasAnyBlank(serverIp,port,sensorLoop,address)){
            throw new ParamErrorException("参数不正确");
        }
        try {
            JSONObject jsonObject = iDeviceFeign.deviceFireMainSensor(serverIp, port, sensorLoop, address);
            if(jsonObject != null || jsonObject.getJSONObject("data") != null){
                JSONObject data = jsonObject.getJSONObject("data");
                Long equId = data.getLong("id");
                dfmaBiz.restoreAbnormal(equId);
            }
            return new ObjectRestResponse();
        }catch (Exception e){
            log.error("恢复消防主机异常失败:{}",e.getMessage());
            return  ObjectRestResponse.failure("恢复消防主机异常失败:"+e.getMessage());
        }
    }
}