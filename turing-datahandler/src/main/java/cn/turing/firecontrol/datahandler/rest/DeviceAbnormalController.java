package cn.turing.firecontrol.datahandler.rest;

import cn.turing.firecontrol.auth.client.annotation.*;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.datahandler.biz.DeviceAbnormalBiz;
import cn.turing.firecontrol.datahandler.biz.DeviceFacilitiesAbnormalBiz;
import cn.turing.firecontrol.datahandler.biz.DeviceFireMainAbnormalBiz;
import cn.turing.firecontrol.datahandler.business.BusinessI;
import cn.turing.firecontrol.datahandler.entity.DeviceFacilitiesAbnormal;
import cn.turing.firecontrol.datahandler.entity.DeviceFireMainAbnormal;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import cn.turing.firecontrol.datahandler.feign.IUserFeign;
import cn.turing.firecontrol.datahandler.util.Constants;
import cn.turing.firecontrol.datahandler.util.DateUtil;
import cn.turing.firecontrol.datahandler.entity.DeviceAbnormal;
import cn.turing.firecontrol.datahandler.util.SortUtil;
import cn.turing.firecontrol.datahandler.util.ValidatorUtils;
import cn.turing.firecontrol.device.biz.DeviceSensorBiz;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("deviceAbnormal")
@CheckClientToken
@CheckUserToken
@Api(tags = "传感器异常记录模块")
@Slf4j
public class DeviceAbnormalController extends BaseController<DeviceAbnormalBiz, DeviceAbnormal,Integer> {

    @Autowired
    private DeviceAbnormalBiz daBiz;
    @Autowired
    private IUserFeign IUserFeign;
    @Autowired
    private IDeviceFeign iDeviceFeign;
    @Autowired
    private DeviceFacilitiesAbnormalBiz dafBiz;
    @Autowired
    private DeviceFireMainAbnormalBiz dfmBiz;
    @Autowired
    private IDeviceFeign deviceFeign;

    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "tag",value = "前端切换标识0：当前报警记录，1：火警报警记录，2：故障报警记录",paramType = "query"),
            @ApiImplicitParam(name = "bName",value = "建筑名称",paramType = "query"),
            @ApiImplicitParam(name = "floorId",value = "建筑楼层",paramType = "query"),
            @ApiImplicitParam(name = "sensorNo",value = "传感器编号",paramType = "query"),
            @ApiImplicitParam(name = "positionDescription",value = "位置描述",paramType = "query"),
            @ApiImplicitParam(name = "equipmentType",value = "传感器类型",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public TableResultResponse<DeviceAbnormal> list(String page,String limit, DeviceAbnormal deviceAbnormal,
                                                    String dateStrs,String tag, String bName, String floorId, String sensorNo, String positionDescription, String equipmentType,Integer channelId){
        Map<String ,Object> param = new LinkedHashMap<>();
        if(StringUtils.isBlank(page)){
            page = "1";
        }
        if(StringUtils.isBlank(limit)){
            limit = "15";
        }
        if(StringUtils.isBlank(tag)){
            tag = "0";
        }
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
        return daBiz.selectQuery(query,deviceAbnormal,startDate,endDate,tag,bName,floorId,sensorNo,positionDescription,equipmentType,channelId);
    }

    @RequestMapping(value = "/selectAlrmType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有报警类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query"),
            @ApiImplicitParam(name = "alrmCategory",value = "报警类型：0：故障，1：火警",paramType = "query"),
            @ApiImplicitParam(name = "handleFlag",value = "是否处理[1=是/0=否]",paramType = "query")
    })
    public ObjectRestResponse<DeviceAbnormal> selectAlrmType(Integer channelId, String alrmCategory, String handleFlag) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<String> list = daBiz.selectAlrmType(channelId,alrmCategory,handleFlag,null);//查询报警类型列表
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/selectCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有真实火警、误报火警、故障报警数量")
    @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    public ObjectRestResponse<DeviceAbnormal> selectCount(Integer channelId) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Integer falseFire = daBiz.selectCountByAlrmCategoryAndFireFlag("0","1","1",channelId,null);//误报
        Integer realFire = daBiz.selectCountByAlrmCategoryAndFireFlag("1","1","1",channelId,null);//真实火警
        Integer faultAlarm = daBiz.selectCountByAlrmCategoryAndFireFlag(null,"0",null,channelId,null);//故障报警
        Map<String,Object> map = new HashMap<>();//返回map
        map.put("realFire",realFire);
        map.put("falseFire",falseFire);
        map.put("faultAlarm",faultAlarm);
        responseResult.setData(map);
        return responseResult;
    }

    @GetMapping("selectCountNearlyMonth")
    @ApiOperation("查询最近N个月各状态（火警，故障，误报）的数量")
    public ObjectRestResponse selectCountNearlyMonth(@RequestParam(defaultValue = "5") Integer count,Integer channelId){
        Map<String,Object[]> map = baseBiz.selectCountNearlyMonth(count,channelId);
        return new ObjectRestResponse().data(map);
    }

    @RequestMapping(value = "/affirmFire",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("确认是否是真实火警,并处理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "异常记录id",paramType = "query"),
            @ApiImplicitParam(name = "flag",value = "是否为真实火警 2=火警测试/1=是/0=否",paramType = "query")
    })
    public ObjectRestResponse<DeviceAbnormal> affirmFire(@RequestParam Integer id, @RequestParam String flag) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        if(id==null || flag==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            Object object = daBiz.selectById(id);
            DeviceAbnormal deviceAbnormal = new DeviceAbnormal();
            BeanUtils.copyProperties(object,deviceAbnormal);
            deviceAbnormal.setFireFlag(flag);//是否为真实火警[1=是/0=否/2=火警测试]
            deviceAbnormal.setHandleFlag("1");
            deviceAbnormal.setConfirPerson(BaseContextHandler.getUsername());//确认人
            deviceAbnormal.setConfirDate(new Date());//确认时间
            deviceAbnormal.setHandlePerson(BaseContextHandler.getUsername());//处理人
            deviceAbnormal.setHandleDate(new Date()); //处理时间
            daBiz.updateSelectiveById(deviceAbnormal);
        }
        return responseResult;
    }

    @RequestMapping(value = "/affirmHandle",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("确认处理")
    public ObjectRestResponse<DeviceAbnormal> affirmHandle(@RequestParam Integer id) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        if(id==null ){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            Object object = daBiz.selectById(id);
            DeviceAbnormal deviceAbnormal = new DeviceAbnormal();
            BeanUtils.copyProperties(object,deviceAbnormal);
            deviceAbnormal.setHandleFlag("1");//是否处理[1=是/0=否]'
            deviceAbnormal.setFireFlag("0");//是否为真实火警[1=是/0=否/2=火警测试]
            deviceAbnormal.setHandlePerson(BaseContextHandler.getUsername());//处理人
            deviceAbnormal.setHandleDate(new Date());//处理时间
            deviceAbnormal.setConfirDate(new Date());
            deviceAbnormal.setConfirPerson(BaseContextHandler.getUsername());//确认人
            daBiz.updateSelectiveById(deviceAbnormal);
        }
        return responseResult;
    }

//    @RequestMapping(value = "/selectCountByBuildId",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("根据建筑物查询对应的异常数量并排名")
//    public ObjectRestResponse<DeviceAbnormal> selectCountByBuildId(String tag, String dateStrs, Integer channelId) throws Exception{
//        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd");
//        String startDate = null;
//        String endDate = null;
//        if(dateStrs !=null){
//            String[] date = dateStrs.split(",");
//            startDate = date[0];
//            endDate = date[1];
//        }
//        if(tag !=null){
//            if(tag.equals("0")){//当前月份
//                Date date = new Date();
//                startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
//                endDate = simpleDateFormat.format(DateUtil.getEndTime());
//            }
//            if(tag.equals("1")){//本季度
//                startDate = simpleDateFormat.format(DateUtil.getCurrentQuarterStartTime());
//                //endDate = simpleDateFormat.format(DateUtil.getCurrentQuarterEndTime());
//                endDate = simpleDateFormat.format(DateUtil.getEndTime());
//            }
//            if(tag.equals("2")){//本年度
//                startDate = slf.format(DateUtil.getCurrentYearStartTime())+" 00:00:00";
//                //endDate = simpleDateFormat.format(DateUtil.getCurrentYearEndTime())+" 23:59:59";
//                endDate = simpleDateFormat.format(DateUtil.getEndTime());
//            }
//        }
//        if(startDate==null && endDate==null){//当前月份
//            Date date = new Date();
//            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
//            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
//            endDate = simpleDateFormat.format(DateUtil.getEndTime());
//        }
//        //查询建筑列表
//        JSONObject jsonObject = iDeviceFeign.getBuildingList(channelId);
//        JSONArray buildingData=jsonObject.getJSONArray("data");
//        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
//        for(int i = 0; i<buildingData.size(); i++){
//            Map<String ,Object> map = new HashMap<>();
//            String bName = buildingData.getJSONObject(i).getString("bname");
//            Integer count = daBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate+ " 00:00:00"),simpleDateFormat.parse(endDate + " 23:59:59"),bName,null,channelId);
//            if(count!=0){
//                map.put("name",bName);
//                map.put("count",count);
//                resultList.add(map);
//            }
//        }
//        if(resultList.size()>1){
//            sort(resultList);//按count排序
//        }
//        int i =1;
//        for (Map map:resultList) {//生成排名id
//            map.put("id",i);
//            i =i+1;
//        }
//        responseResult.setData(resultList);
//        return responseResult;
//    }

/*    @RequestMapping(value = "/selectCountByBuildId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑物查询对应的异常数量并排名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query"),
            @ApiImplicitParam(name = "alrmCategory",value = "报警类型：0：故障，1：火警",paramType = "query")
    })
    public ObjectRestResponse<DeviceAbnormal> selectCountByBuildId(String tag, String dateStrs,Integer channelId,@RequestParam String alrmCategory) throws Exception{
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
        Integer allCount = daBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate+ " 00:00:00"),simpleDateFormat.parse(endDate + " 23:59:59"),null,alrmCategory,channelId,null);
        for(int i = 0; i<buildingData.size(); i++){
            Map<String ,Object> map = new HashMap<>();
            String bName = buildingData.getJSONObject(i).getString("bname");
            Integer count = daBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate+ " 00:00:00"),simpleDateFormat.parse(endDate + " 23:59:59"),bName,alrmCategory,channelId,null);
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
        return responseResult;
    }*/
    @RequestMapping(value = "/selectCountByBuildId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑物查询对应的异常数量并排名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query"),
            @ApiImplicitParam(name = "alrmCategory",value = "报警类型：0：故障，1：火警",paramType = "query")
    })
    public ObjectRestResponse<DeviceAbnormal> selectCountByBuildId(String tag, String dateStrs,Integer channelId,@RequestParam String alrmCategory) throws Exception{
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
        Integer allCount = daBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate+ " 00:00:00"),simpleDateFormat.parse(endDate + " 23:59:59"),null,alrmCategory,channelId,null);
        DecimalFormat df = new DecimalFormat("0.00");
        List<Map> resultList = daBiz.selectTopCountByBuildIdAndDate(simpleDateFormat.parse(startDate + " 00:00:00"), simpleDateFormat.parse(endDate + " 23:59:59"), alrmCategory, channelId, null);
        int i=1;
        for (Map map : resultList) {
            map.put("id",i);
            if(allCount.intValue()>0){
                map.put("percentage",df.format(((Long) map.get("count")*1.0/allCount)*100));
            }
            i++;
        }

        responseResult.setData(resultList);
        return responseResult;
    }

//    @RequestMapping(value = "/selectCountByType",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("根据报警类型查询对应的异常数量")
//    public ObjectRestResponse<DeviceAbnormal> selectCountByType(String tag, String dateStrs, Integer channelId) throws Exception{
//        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd");
//        String startDate = null;
//        String endDate = null;
//        if(dateStrs !=null){
//            String[] date = dateStrs.split(",");
//            startDate = date[0];
//            endDate = date[1];
//        }
//        if(tag !=null){
//            if(tag.equals("0")){//当前月份
//                Date date = new Date();
//                startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
//                endDate = simpleDateFormat.format(DateUtil.getEndTime());
//            }
//            if(tag.equals("1")){//本季度
//                startDate = simpleDateFormat.format(DateUtil.getCurrentQuarterStartTime());
//                //endDate = simpleDateFormat.format(DateUtil.getCurrentQuarterEndTime());
//                endDate = simpleDateFormat.format(DateUtil.getEndTime());
//            }
//            if(tag.equals("2")){//本年度
//                startDate = slf.format(DateUtil.getCurrentYearStartTime())+" 00:00:00";
//                //endDate = simpleDateFormat.format(DateUtil.getCurrentYearEndTime())+" 23:59:59";
//                endDate = simpleDateFormat.format(DateUtil.getEndTime());
//            }
//        }
//        if(startDate==null && endDate==null){//当前月份
//            Date date = new Date();
//            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
//            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
//            endDate = simpleDateFormat.format(DateUtil.getEndTime());
//        }
//        List<String> typelist = daBiz.selectAlrmType(channelId,null,null);//报警类型list
//        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
//        for (String s:typelist){
//            Map<String ,Object> map = new HashMap<>();
//            Integer count = daBiz.selectCountByType(simpleDateFormat.parse(startDate+ " 00:00:00"),
//                    simpleDateFormat.parse(endDate + " 23:59:59"),s);
//            map.put("alrmType",s);
//            map.put("count",count);
//            resultList.add(map);
//        }
//        responseResult.setData(resultList);
//        return responseResult;
//    }

    @RequestMapping(value = "/selectCountByType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据报警类型查询对应的异常数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceAbnormal> selectCountByType(String tag, String dateStrs,Integer channelId) throws Exception{
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
            }else if(tag.equals("1")){//昨天
                startDate = DateUtil.getYesterdayStartDay();
                endDate = DateUtil.getYesterdayEedDay();
            } else if(tag.equals("2")){//最近7天
                startDate = DateUtil.getRecentlySevenStartDay();
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            } else if(tag.equals("3")){//最近30天
                startDate = DateUtil.getRecentlyStartDay();
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
        }
        if(startDate==null && endDate==null){//今天
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        List<String> typelist = daBiz.selectAlrmType(channelId,null,null,null);//报警类型list
        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
        Integer allCount = daBiz.selectCountByType(simpleDateFormat.parse(startDate+ " 00:00:00"),
                simpleDateFormat.parse(endDate + " 23:59:59"),null,channelId,null);//总共个数
        DecimalFormat df = new DecimalFormat("0.00");
        for (String s:typelist){
            Map<String ,Object> map = new HashMap<>();
            Integer count = daBiz.selectCountByType(simpleDateFormat.parse(startDate+ " 00:00:00"),
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

//    @RequestMapping(value = "/selectCountByMonth",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("每天的异常数量统计")
//    public ObjectRestResponse<DeviceAbnormal> selectCountByMonth(String tag, String dateStrs, Integer channelId) throws Exception {
//       ObjectRestResponse responseResult = new ObjectRestResponse<>();
//        SimpleDateFormat  slf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String startDate = null;
//        String endDate = null;
//        if(dateStrs !=null){
//            String[] date = dateStrs.split(",");
//            startDate = date[0]+" 00:00:00";
//            endDate = date[1]+" 23:59:59";
//        }
//        if(tag !=null){
//            if(tag.equals("0")){//当前月份
//                Date date = new Date();
//                startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date))+" 00:00:00";
//                endDate = slf.format(DateUtil.getEndTime());
//            }
//            if(tag.equals("1")){//本季度
//                startDate = slf.format(DateUtil.getCurrentQuarterStartTime());
//                //endDate = simpleDateFormat.format(DateUtil.getCurrentQuarterEndTime());
//                endDate = slf.format(DateUtil.getEndTime());
//            }
//            if(tag.equals("2")){//本年度
//                startDate = simpleDateFormat.format(DateUtil.getCurrentYearStartTime())+" 00:00:00";
//                //endDate = simpleDateFormat.format(DateUtil.getCurrentYearEndTime())+" 23:59:59";
//                endDate = slf.format(DateUtil.getEndTime());
//            }
//        }
//        if(startDate==null && endDate==null){//当前月份
//            Date date = new Date();
//            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date))+" 00:00:00";
//            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
//            endDate = slf.format(DateUtil.getEndTime());
//        }
//        List<ResultVo> resultList = daBiz.selectCountByDate(slf.parse(startDate),slf.parse(endDate),channelId,null);
//        responseResult.setData(resultList);
//        return responseResult;
//    }

    @RequestMapping(value = "/selectCountByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("每天的故障,报警数量统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceAbnormal> selectCountByDate(String tag, String dateStrs, Integer channelId) throws Exception {
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
            Integer faultCount = daBiz.getCountByToday(beginDate,lastDate,"0",channelId,null);
            Integer callCount = daBiz.getCountByToday(beginDate,lastDate,"1",channelId,null);
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

//    @RequestMapping(value = "/getCountByToday",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("APP根据时间查询火警、异常数量")
//    public ObjectRestResponse<DeviceAbnormal> getCountByToday(@RequestParam String identity, Integer channelId) throws Exception {
//        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd");
//        String startDate=null;
//        String endDate=null;
//        if(identity.equals("0")){//本日
//            startDate = simpleDateFormat.format(DateUtil.getStartTime());
//            endDate = simpleDateFormat.format(DateUtil.getEndTime());
//        }
//        if(identity.equals("1")){//本周
//            startDate = slf.format(DateUtil.getBeginDayOfWeek())+" 00:00:00";
//            endDate = slf.format(DateUtil.getEndTime())+" 23:59:59";
//        }
//        if(identity.equals("2")){//本月
//            Date date = new Date();
//            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
//            endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
//        }
//        if(identity.equals("3")){//本年
//            startDate = slf.format(DateUtil.getCurrentYearStartTime())+" 00:00:00";
//            endDate = simpleDateFormat.format(DateUtil.getCurrentYearEndTime())+" 23:59:59";
//        }
//        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
//        for(int i = 0; i<2;i++){
//            Map<String ,Object> map = new HashMap<>();
//            Integer count = daBiz.getCountByToday(simpleDateFormat.parse(startDate),
//                    simpleDateFormat.parse(endDate),i+"",channelId);
//            map.put("alrmType",i);//0：故障，1：火警
//            map.put("count",count);
//            resultList.add(map);
//        }
//        responseResult.setData(resultList);
//        return responseResult;
//    }

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
        return daBiz.selectAlrm(query,handleFlag,alrmCategory,channelId);
    }

    @RequestMapping(value ="/selectAlrmByDetails",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据传感器id和报警记录id查询报警详情")
    public ObjectRestResponse selectAlrmByDetails(Integer alarmId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map map = new HashMap();
        Object object= daBiz.selectById(alarmId);//报警信息
        DeviceAbnormal deviceAbnormal= new DeviceAbnormal();
        BeanUtils.copyProperties(object,deviceAbnormal);
        JSONObject building = iDeviceFeign.getBuilding(deviceAbnormal.getBuildId());//查询建筑物信息
        Integer id = building.getJSONObject("data").getInteger("oid");//联网单位id
        JSONObject networkingUnit = iDeviceFeign.getById(id);//查询联网单位
        map.put("measuringPoint",deviceAbnormal.getMeasuringPoint());
        map.put("equipmentType",deviceAbnormal.getEquipmentType());
        map.put("sensorNo",deviceAbnormal.getSensorNo());
        map.put("fireFlag",deviceAbnormal.getFireFlag());
        map.put("bName",deviceAbnormal.getbName());
        map.put("positionDescription",deviceAbnormal.getFloor()+"F-"+deviceAbnormal.getPositionDescription());
        map.put("alrmType1",deviceAbnormal.getAlrmType());
        map.put("alrmType2",deviceAbnormal.getAlrmData()+deviceAbnormal.getDataUnit());
        map.put("alrmDate",deviceAbnormal.getAlrmDate());
        map.put("handleFlag",deviceAbnormal.getHandleFlag());
        map.put("safeDutyName",networkingUnit.getJSONObject("data").getString("safeDutyName"));
        map.put("safeDutyPhone",networkingUnit.getJSONObject("data").getString("safeDutyPhone"));
        map.put("alrmCategory",deviceAbnormal.getAlrmCategory());
        if(deviceAbnormal.getHandleFlag().equals("1")){
            if(deviceAbnormal.getAlrmCategory().equals("0")){
                map.put("handleDate",deviceAbnormal.getHandleDate());
                map.put("handlePerson",deviceAbnormal.getHandlePerson());
                map.put("time",DateUtil.getHandletime(deviceAbnormal.getHandleDate(),deviceAbnormal.getAlrmDate()));
            }
            if(deviceAbnormal.getAlrmCategory().equals("1")){
                map.put("handleDate",deviceAbnormal.getConfirDate());
                map.put("handlePerson",deviceAbnormal.getConfirPerson());
                map.put("time",DateUtil.getHandletime(deviceAbnormal.getConfirDate(),deviceAbnormal.getAlrmDate()));
            }
        }
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/selectAlrmCountByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据时间查询每天的故障报警次数")
    public ObjectRestResponse<DeviceAbnormal> selectAlrmCountByDate(@RequestParam String tag, Integer buildId, Integer channelId) throws Exception {
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        SimpleDateFormat  slf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = null;
        String endDate = null;
        if("0".equals(tag)){//今日
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if("1".equals(tag)){//本周
            startDate = simpleDateFormat.format(DateUtil.getBeginDayOfWeek());
            //endDate = simpleDateFormat.format(DateUtil.getEndDayOfWeek());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if("2".equals(tag)){//本月
            Date date = new Date();
            startDate = slf.format(DateUtil.getBeginDayofMonth(date));
            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        List<Date> list = DateUtil.getBetweenDates(simpleDateFormat.parse(startDate),simpleDateFormat.parse(endDate));
        Map<String,Object> resultMap = new HashMap<>();
        List faultList = new ArrayList();
        List callList = new ArrayList();
        List dateList = new ArrayList();
        for (Date date:list){
            Date start = slf.parse(simpleDateFormat.format(date)+" 00:00:00");
            Date end = slf.parse(simpleDateFormat.format(date)+" 23:59:59");
            Integer faultCount = daBiz.selectAlrmCountByDate(start,end,buildId,"0",channelId,null);
            Integer callCount = daBiz.selectAlrmCountByDate(start,end,buildId,"1",channelId,null);
            //消防主机
            Integer faultCount1 = dfmBiz.selectAlrmCountByDate(start,end,buildId,"0",channelId,null);
            Integer callCount1 = dfmBiz.selectAlrmCountByDate(start,end,buildId,"1",channelId,null);
            if((faultCount+faultCount1) ==0 && (callCount+callCount1)==0 && !"0".equals(tag)){
                continue;
            }
            faultList.add(faultCount+faultCount1);
            callList.add(callCount+callCount1);
            dateList.add(simpleDateFormat.format(date));
        }
        resultMap.put("faultList",faultList);
        resultMap.put("callList",callList);
        resultMap.put("dateList",dateList);
        responseResult.setData(resultMap);
        return responseResult;
    }

    @RequestMapping(value = "/selectFaultCountByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据时间查询每天的故障报警次数")
    public ObjectRestResponse<DeviceAbnormal> selectFaultCountByDate(@RequestParam String identity, Integer buildId, Integer channelId) throws Exception {
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        SimpleDateFormat  slf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = null;
        String endDate = null;
        if(identity.equals("0")){//今日
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("1")){//本周
            startDate = simpleDateFormat.format(DateUtil.getBeginDayOfWeek());
            //endDate = simpleDateFormat.format(DateUtil.getEndDayOfWeek());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("2")){//本月
            Date date = new Date();
            startDate = slf.format(DateUtil.getBeginDayofMonth(date));
            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        List<Date> list = DateUtil.getBetweenDates(simpleDateFormat.parse(startDate),simpleDateFormat.parse(endDate));
        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
        for (Date date:list){
            Map<String ,Object> map = new HashMap<>();
            Date start = slf.parse(simpleDateFormat.format(date)+" 00:00:00");
            Date end = slf.parse(simpleDateFormat.format(date)+" 23:59:59");
            Integer count = daBiz.selectAlrmCountByDate(start,end,buildId,"0",channelId,null);
            //消防主机
            Integer count1 = dfmBiz.selectAlrmCountByDate(start,end,buildId,"0",channelId,null);
            map.put("date",simpleDateFormat.format(date));
            map.put("count",count+count1);
            resultList.add(map);
        }
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/selectFireCountByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据时间查询每天的火警报警次数")
    public ObjectRestResponse<DeviceAbnormal> selectFireCountByDate(@RequestParam String identity, Integer buildId, Integer channelId) throws Exception {
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        SimpleDateFormat  slf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = null;
        String endDate = null;
        if(identity.equals("0")){//今日
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("1")){//本周
            startDate = simpleDateFormat.format(DateUtil.getBeginDayOfWeek());
            //endDate = simpleDateFormat.format(DateUtil.getEndDayOfWeek());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("2")){//本月
            Date date = new Date();
            startDate = slf.format(DateUtil.getBeginDayofMonth(date));
            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        List<Date> list = DateUtil.getBetweenDates(simpleDateFormat.parse(startDate),simpleDateFormat.parse(endDate));
        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
        for (Date date:list){
            Map<String ,Object> map = new HashMap<>();
            Date start = slf.parse(simpleDateFormat.format(date)+" 00:00:00");
            Date end = slf.parse(simpleDateFormat.format(date)+" 23:59:59");
            Integer count = daBiz.selectAlrmCountByDate(start,end,buildId,"1",channelId,null);
            //消防主机
            Integer count1 = dfmBiz.selectAlrmCountByDate(start,end,buildId,"1",channelId,null);
            map.put("date",simpleDateFormat.format(date));
            map.put("count",count+count1);
            resultList.add(map);
        }
        responseResult.setData(resultList);
        return responseResult;
    }

//    @RequestMapping(value = "/selectCountByDate",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("APP根据时间查询建筑物对应的报警、故障数量")
//    public ObjectRestResponse<DeviceAbnormal> selectCountByDate(@RequestParam String identity, @RequestParam String alrmCategory, Integer channelId) throws Exception{
//        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd");
//        String startDate=null;
//        String endDate=null;
//        if(identity.equals("0")){//本日
//            startDate = simpleDateFormat.format(DateUtil.getStartTime());
//            endDate = simpleDateFormat.format(DateUtil.getEndTime());
//        }
//        if(identity.equals("1")){//本周
//            startDate = slf.format(DateUtil.getBeginDayOfWeek())+" 00:00:00";
//            //endDate = slf.format(DateUtil.getEndTime())+" 23:59:59";
//            endDate = simpleDateFormat.format(DateUtil.getEndTime());
//        }
//        if(identity.equals("2")){//本月
//            Date date = new Date();
//            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
//            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
//            endDate = simpleDateFormat.format(DateUtil.getEndTime());
//        }
//        Integer allcount = daBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),null,alrmCategory,channelId);//所有的次数
//        DecimalFormat df = new DecimalFormat("0.00");
//        if(identity.equals("3")){//本年
//            startDate = slf.format(DateUtil.getCurrentYearStartTime())+" 00:00:00";
//            endDate = simpleDateFormat.format(DateUtil.getEndTime());
//        }
//        //查询建筑列表
//        JSONObject jsonObject = iDeviceFeign.getBuildingList(channelId);
//        JSONArray buildingData=jsonObject.getJSONArray("data");
//        List resultlist = new ArrayList();
//        for(int i = 0; i< buildingData.size(); i++){
//            Map<String ,Object> map = new HashMap<>();
//            String bName = buildingData.getJSONObject(i).getString("bname");
//            Integer count = daBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),bName,alrmCategory,channelId);
//            if(count!=0){
//                map.put("name",bName);
//                map.put("count",count);
//                map.put("percentage",df.format(((float)count/allcount)*100));//计算百分比
//                resultlist.add(map);
//            }
//        }
//        if(resultlist.size()>10){//如果list大于10取前10条
//            responseResult.setData(sort(resultlist).subList(0,10));
//        }else{
//            responseResult.setData(sort(resultlist));
//        }
//        return responseResult;
//    }

//    @RequestMapping(value = "/selectCallCountByDate",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("APP根据时间查询统计报警、故障数量")
//    public ObjectRestResponse<DeviceAbnormal> selectCallCountByDate(@RequestParam String identity, String alrmcategory, Integer channelId) throws Exception {//alrmcategory 0：故障，1：火警
//        ObjectRestResponse responseResult = new ObjectRestResponse<>();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        //SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
//        String startDate=null;
//        String endDate=null;
//        if(identity.equals("0")){//本日
//            startDate = simpleDateFormat.format(DateUtil.getStartTime());
//            endDate = simpleDateFormat.format(DateUtil.getEndTime());
//        }
//        if(identity.equals("1")){//本周
//            startDate = simpleDateFormat.format(DateUtil.getBeginDayOfWeek())+" 00:00:00";
//            //endDate = simpleDateFormat.format(DateUtil.getEndTime())+" 23:59:59";
//            endDate = simpleDateFormat.format(DateUtil.getEndTime());
//        }
//        if(identity.equals("2")){//本月
//            Date date = new Date();
//            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
//            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
//            endDate = simpleDateFormat.format(DateUtil.getEndTime());
//        }
//        if(identity.equals("3")){//本年
//            List<ResultVo> countlist = daBiz.selectByYear( DateUtil.getNowYear(),alrmcategory);
//            responseResult.setData(countlist);
//            return responseResult;
//        }
//        List<Date> list = DateUtil.getBetweenDates(simpleDateFormat.parse(startDate),simpleDateFormat.parse(endDate));
//        Map map = new HashMap();//返回数据集合
//        List datelist = new LinkedList();
//        List countlist = new LinkedList<>();
//        for (Date date:list){
//            Integer count = daBiz.selectCountByMonth(simpleDateFormat.format(date),alrmcategory,channelId);
//            datelist.add(simpleDateFormat.format(date).substring(5,10).replace("-","."));
//            countlist.add(count);
//        }
//        map.put("datelist",datelist);
//        map.put("countlist",countlist);
//        responseResult.setData(map);
//        return responseResult;
//    }

    @RequestMapping(value = "/getAlrmCountByChannel",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP查询所属系统下各自报警次数")
    public ObjectRestResponse getAlrmCountByChannel(String channelIds){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        String [] ids = channelIds.split(",");
        List list = new LinkedList();
        Integer allCount = daBiz.selectCountByAlrmCategoryAndFireFlag(null,null,"0",null,null);//全部
        list.add(allCount);
        for(int i=0;i<ids.length;i++){
            JSONObject jsonObject = IUserFeign.selectById(Integer.valueOf(ids[i]));
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            if(jsonObject1.getInteger("id")==5){
                Integer count = dafBiz.selectCountByChannelId("0",Integer.valueOf(ids[i]),null);//各自系统下次数
                list.add(count);
            }else if(jsonObject1.getInteger("id")==11){
                Integer count = dfmBiz.selectCountByAlrmCategoryAndFireFlag(null,null,"0",Integer.valueOf(ids[i]),null);
                list.add(count);
            }else {
                Integer count = daBiz.selectCountByAlrmCategoryAndFireFlag(null,null,"0",Integer.valueOf(ids[i]),null);//各自系统下次数
                list.add(count);
            }
        }
        responseResult.setData(list);
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
        List<Map> list = daBiz.selectByHandelFlagAndAlrm(alrmCategory,"0",channelId,null);
        for (Map deviceAbnormal:list){
            Map<String,Object> map=new HashMap<>();
            map.put("id",deviceAbnormal.get("id"));
            map.put("buildId",deviceAbnormal.get("buildId"));
            map.put("bName",deviceAbnormal.get("bName"));
            map.put("series",deviceAbnormal.get("series"));
            map.put("position",deviceAbnormal.get("floor")+"F-"+deviceAbnormal.get("position"));
            map.put("alarmType",deviceAbnormal.get("alrmType"));
            map.put("date",DateUtil.getFriendlytime((Date) deviceAbnormal.get("alrmDate")));
            map.put("logId",deviceAbnormal.get("logId"));
            map.put("measuringPoint",deviceAbnormal.get("measuringPoint"));
            if(deviceAbnormal.get("dataUnit")!= null){
                map.put("alarmData",deviceAbnormal.get("alrmData")+deviceAbnormal.get("dataUnit").toString());
            }else{
                map.put("alarmData",deviceAbnormal.get("alrmData"));
            }
            lists.add(map);
        }
        return new TableResultResponse(result.getTotal(),lists);
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
            DeviceAbnormal deviceAbnormal = new DeviceAbnormal();
            deviceAbnormal.setEquId(Integer.parseInt(sensorId));
            deviceAbnormal.setHandleFlag("1");
            deviceAbnormal.setTenantId(null);
            resultList = daBiz.selectByEquIdResultMP(deviceAbnormal);
            for(int i=0;i<resultList.size();i++){
                Map<String,Object> map = resultList.get(i);
                if(map.get("confirPerson")==null||"".equals(map.get("confirPerson").toString())){
                    map.put("confirPerson",map.get("handlePerson"));
                    map.put("confirDate",map.get("handleDate"));
                }
            }
        }
        return new TableResultResponse(result.getTotal(),resultList);
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
            List<DeviceAbnormal> list = daBiz.selectByEquId(Long.valueOf(sensorId),"1",null,null);
            for(DeviceAbnormal deviceAbnormal:list){
                Map map = new HashMap();
                map.put("id",deviceAbnormal.getId());
                map.put("type",deviceAbnormal.getAlrmType());
                map.put("alrmDate",deviceAbnormal.getAlrmDate());
                map.put("fireFlag",deviceAbnormal.getFireFlag());
                map.put("alrmCategory",deviceAbnormal.getAlrmCategory());
                if(deviceAbnormal.getAlrmCategory().equals("0")){
                    map.put("handlePerson",deviceAbnormal.getHandlePerson());
                    map.put("handleDate",deviceAbnormal.getHandleDate());
                }
                if(deviceAbnormal.getAlrmCategory().equals("1")){
                    map.put("handlePerson",deviceAbnormal.getConfirPerson());
                    map.put("handleDate",deviceAbnormal.getConfirDate());
                }
                map.put("alarmData",deviceAbnormal.getAlrmData()+deviceAbnormal.getDataUnit());
                map.put("measuringPoint",deviceAbnormal.getMeasuringPoint());
                resultList.add(map);
            }
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    @RequestMapping(value = "/getRatioByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据时间查询隐患处理比例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceAbnormal> getRatioByDate(String tag, String dateStrs, Integer channelId) throws Exception {
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
        Integer handleCount = daBiz.getCountByHandleFlag(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),"1",channelId,null);
        //查询当前时间报警数量
        Integer count = daBiz.getCountByToday(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),null,channelId,null);
        //List list = daBiz.selectAlrm("0",simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),channelId,null);
        Integer  notHandleCount = daBiz.selectCountByDateAndHandle(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),"0",channelId);
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

    @RequestMapping(value = "/selectAbnormal",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询建筑物对应楼层传感器报警记录")
    public TableResultResponse selectAbnormal(@RequestParam String page, @RequestParam String limit,@RequestParam Integer buildId,Integer floor,Integer channelId){
        Map<String ,Object> param = new LinkedHashMap<>();
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return daBiz.selectAbnormal(query,buildId,floor,channelId);
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
            JSONObject jsonObject = iDeviceFeign.deviceSensor(id);//根据id查询传感器
            JSONObject sensor = jsonObject.getJSONObject("data");
            Integer faultCount = null;
            Integer alrmCount = null;
            if(sensor.getString("buildingId")!=null){
                faultCount = daBiz.selectCountBySensorNo(sensor.getString("sensorNo"),"0",null);//故障次数
                alrmCount = daBiz.selectCountBySensorNo(sensor.getString("sensorNo"),"1",null);//报警次数
                map.put("faultCount",faultCount);
                map.put("alrmCount",alrmCount);
            }
            if(sensor.getString("hydrantId")!=null){
                faultCount = dafBiz.selectCountBySensorNo(sensor.getString("sensorNo"),null);//故障次数
                map.put("faultCount",faultCount);
            }
            result.add(map);
        }
        responseResult.setData(result);
        return responseResult;
    }

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
//        List<DeviceAbnormal> list = daBiz.selectAlrm("0",null,null,channelId);
//        List resultList = new ArrayList();
//        for(DeviceAbnormal deviceAbnormal:list){
//            Map map = new HashMap();
//            map.put("id",deviceAbnormal.getId());
//            map.put("name",deviceAbnormal.getbName()+deviceAbnormal.getFloor()+"F");
//            map.put("type",deviceAbnormal.getEquipmentType()+"设备"+deviceAbnormal.getMeasuringPoint()+deviceAbnormal.getAlrmType());
//            map.put("date",deviceAbnormal.getAlrmDate());
//            map.put("logId",deviceAbnormal.getLogId());
//            resultList.add(map);
//        }
//        return new TableResultResponse(result.getTotal(),resultList);
//    }

    @RequestMapping(value = "/getNothandlerCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有未处理数量")
    public ObjectRestResponse getNothandlerCount() {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //电气火灾 火灾报警
        Integer deviceAbnormalCount = daBiz.selectCountByAlrmCategoryAndFireFlag(null,null,"0",null,null);
        //消防给水
        Integer deviceFacilitiesAbnormalCount = dafBiz.selectCountByChannelId("0",null,null);
        //消防主机
        Integer deviceFireMainAbnormalCount = dfmBiz.selectCountByAlrmCategoryAndFireFlag(null,null,"0",null,null);
        //总数
        Integer count = deviceAbnormalCount+deviceFacilitiesAbnormalCount+deviceFireMainAbnormalCount;
        responseResult.setData(count);
        return responseResult;
    }

//    @RequestMapping(value = "/getNothandlerCountByChannelId",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("根据所属系统id查询所有未处理数量")
//    @ApiImplicitParam(name = "ids",value = "所属系统id字符串，已','隔开",paramType = "query")
//    public ObjectRestResponse getNothandlerCountByChannelId(String ids) {
//        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        if(StringUtils.isBlank(ids)){
//            return responseResult;
//        }
//        String [] channelIds = ids.split(",");
//        List resultList = new LinkedList();
//        for(int i =0;i<channelIds.length;i++){
//            //Map map = new HashMap();
//            Integer channelId = Integer.valueOf(channelIds[i]);
//            JSONObject jsonObject = IUserFeign.selectById(channelId);
//            if(!"消防给水".equals(jsonObject.getJSONObject("data").getString("channelName"))){
//                Integer count = daBiz.selectCountByAlrmCategoryAndFireFlag(null,null,"0",channelId);
//                resultList.add(count);
//            }else{
//                Integer count = dafBiz.selectCountByChannelId("0",channelId);
//                resultList.add(count);
//            }
//        }
//        responseResult.setData(resultList);
//        return responseResult;
//    }

    @RequestMapping(value = "/getChannelList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据所属系统id查询所有未处理数量")
    public ObjectRestResponse getChannelList() {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        JSONObject jsonObject = IUserFeign.getChannelList();
        JSONArray channelArray = jsonObject.getJSONArray("data");
        List resultList = new ArrayList();
        for(int i =0;i<channelArray.size();i++){
            Map map = new HashMap();
            Integer channelId = Integer.valueOf(channelArray.getJSONObject(i).getString("id"));
            if(channelArray.getJSONObject(i).getInteger("id")==5){//消防给水
                Integer count = dafBiz.selectCountByChannelId("0",channelId,null);
                map.put("count",count);
            }else if(channelArray.getJSONObject(i).getInteger("id")==11){//消防主机
                Integer count = dfmBiz.selectCountByAlrmCategoryAndFireFlag(null,null,"0",channelId,null);
                map.put("count",count);
            }else{
                Integer count = daBiz.selectCountByAlrmCategoryAndFireFlag(null,null,"0",channelId,null);
                map.put("count",count);
            }
            map.put("channelId",channelId);
            map.put("channelName",channelArray.getJSONObject(i).getString("channelName"));
            resultList.add(map);
        }
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/getAbnormalByTheLatestTen",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP查询最新10条报警信息")
    public ObjectRestResponse getAbnormalByTheLatestTen() throws Exception{
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String,Object>> list = new ArrayList<>();
        //电气火灾 火灾报警
        List<Map<String,Object>> deviceAbnormalList = daBiz.getAbnormalByTheLatestTen("0",null);
        list.addAll(deviceAbnormalList);
        //消防给水
        List<Map<String,Object>> deviceFacilitiesAbnormalList = dafBiz.getAbnormalByTheLatestTen("0",null);
        list.addAll(deviceFacilitiesAbnormalList);
        //消防主机
        List<Map<String,Object>> deviceFireMainAbnormalList = dfmBiz.getAbnormalByTheLatestTen("0",null);
        list.addAll(deviceFireMainAbnormalList);
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return o2.get("alrmDate").toString().compareTo(o1.get("alrmDate").toString());
            }
        });
        List<Map<String,Object>> resultList = new ArrayList<>();
        if(list.size()>10){
            resultList.addAll(list.subList(0,10));
        }else{
            resultList.addAll(list);
        }
        List<Map<String,Object>> resultlist = new ArrayList<>();
        for(Map<String,Object> map:resultList){
            Map map1 = new HashMap();
            map1.put("sensorId",map.get("equId"));//传感器id
            map1.put("alrmId",map.get("id"));//异常记录id
            if(map.get("buildId")==null){
                map1.put("buildId",map.get("fireCockId"));
                map1.put("name",map.get("hydrantName"));
                map1.put("deviceType","1");//消防给水
                map1.put("type",map.get("equipmentType"));
            }else{
                map1.put("buildId",map.get("buildId"));//建筑id
                map1.put("name",map.get("bName"));
                if(map.get("series")==null){
                    map1.put("type",map.get("equipmentType"));
                    map1.put("deviceType","0");//电气火灾 火灾报警
                    if(map.get("dataUnit")!=null){
                        map1.put("alrmType2",map.get("alrmData").toString()+map.get("dataUnit"));
                    }else{
                        map1.put("alrmType2",map.get("alrmData"));
                    }
                }else{
                    map1.put("type",map.get("series"));
                    map1.put("deviceType","2");//消防主机
                }
            }
            if(map.get("positionDescription")!=null){
                if(map.get("floor")==null){
                    map1.put("positionDescription",map.get("positionDescription"));
                }else{
                    map1.put("positionDescription",map.get("floor")+"F-"+map.get("positionDescription").toString().trim());
                }
            }
            map1.put("alrmType1",map.get("alrmType"));
            map1.put("fireFlag",map.get("fireFlag"));
            map1.put("handleFlag",map.get("handleFlag"));
            map1.put("date",DateUtil.getFriendlytime(simpleDateFormat.parse(map.get("alrmDate").toString())));
            map1.put("measuringPoint",map.get("measuringPoint"));
            if(map.get("alrmCategory")!=null){
                map1.put("alrmCegory",map.get("alrmCategory"));
            }else {
                map1.put("alrmCegory","0");
            }
            resultlist.add(map1);
        }
        responseResult.setData(resultlist);
        return responseResult;
    }


    private Map<String,Date> getTime(String dateStr){
        Date startTime = null,endTime = null,now = new Date();
        if(StringUtils.isBlank(dateStr)){
            startTime = DateUtil.getStartTime(now);
            endTime = DateUtil.getEndTime(now);
        }else {
            String[] timeStrs = dateStr.split(",");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                startTime = dateFormat.parse(timeStrs[0]);
                endTime = dateFormat.parse(timeStrs[1]);
            } catch (ParseException e) {
                log.error("日期错误");
                throw new ParamErrorException("日期错误");
            }
        }
        Map<String,Date> map = new HashMap<>();
        map.put("startTime",startTime == null ? null : DateUtil.getStartTime(startTime));
        map.put("endTime",endTime == null ? null : DateUtil.getEndTime(endTime));
        return map;
    }

    @RequestMapping(value = "/selectCountByDeviceSeriesAndBuilding",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("各建筑物各类型传感器异常数量统计")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "dateStr",value = "时间区间字符串，以','隔开,为空则查询当天的",paramType = "query")
    )
    public ObjectRestResponse queryAlarmTypeCountByBuilding(String dateStr){
        Map<String,Date> times = getTime(dateStr);
        Date startTime = times.get("startTime");
        Date endTime = times.get("endTime");
        try {
            Map<String,Object> map = daBiz.selectCountByDeviceSeriesAndBuilding(startTime,endTime);
            return new ObjectRestResponse().data(map);
        }catch (Exception e){
            log.error("查询失败",e);
            throw new ParamErrorException(e.getMessage());
        }

    }

    @RequestMapping(value = "/selectUnhandleCounts",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询未处理的故障、报警数量")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "dateStr",value = "时间区间字符串，以','隔开,为空则查询当天的",paramType = "query")
    )
    public ObjectRestResponse selectUnhandleCounts(String dateStr){
        Map<String,Date> times = getTime(dateStr);
        Date startTime = times.get("startTime");
        Date endTime = times.get("endTime");
        try {
            Map<String,Long> map = daBiz.selectUnhandleCounts(startTime,endTime);
            return new ObjectRestResponse().data(map);
        }catch (Exception e){
            log.error("查询失败",e);
            throw new ParamErrorException(e.getMessage());
        }

    }

    @RequestMapping(value = "/selectCountBySensor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("各设备报警次数（时间段，前几个数量最多的）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateStr",value = "时间区间字符串，以','隔开,为空则查询当天的",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "数据最大条数",paramType = "query")
    })
    public ObjectRestResponse selectCountBySensor(String dateStr,@RequestParam(defaultValue="10") Integer limit){
        Map<String,Date> times = getTime(dateStr);
        Date startTime = times.get("startTime");
        Date endTime = times.get("endTime");
        try {
            List<Map<String,Object>> list = daBiz.selectCountBySensor(startTime,endTime,limit);
            Collections.reverse(list);
            return new ObjectRestResponse().data(list);
        }catch (Exception e){
            log.error("查询失败",e);
            throw new ParamErrorException(e.getMessage());
        }

    }


    @RequestMapping(value = "/selectCountByDay",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("各设备报警次数（时间段，前几个数量最多的）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateStr",value = "时间区间字符串，以','隔开,为空则查询当天的",paramType = "query")
    })
    public ObjectRestResponse selectCountByDay(String dateStr){
        Map<String,Date> times = getTime(dateStr);
        Date startTime = times.get("startTime");
        Date endTime = times.get("endTime");
        try {
            Map<String,Object> data = new HashMap<>();
            data.put("titles",new String[]{"故障","报警"});
            String[] xAxis = DateUtil.getDatesBetween(startTime,endTime);
            data.put("xAxis",xAxis);
            Map<String,Map<String,Long>> map = daBiz.selectCountByDay(startTime,endTime);
            Map<String,Long> gCountMap = map.get("0");
            Map<String,Long> bCountMap = map.get("1");
            long[] gCount = new long[xAxis.length];
            Arrays.fill(gCount,0);
            long[] bCount = new long[xAxis.length];
            Arrays.fill(bCount,0);
            for(int i = 0;i<xAxis.length;i++){
                Long gc = gCountMap.get(xAxis[i]);
                if(gc != null){
                    gCount[i] = gc;
                }
                Long bc = bCountMap.get(xAxis[i]);
                if(bc != null){
                    bCount[i] = bc;
                }
            }
            data.put("guZhang",gCount);
            data.put("baoJing",bCount);
            return new ObjectRestResponse().data(data);
        }catch (Exception e){
            log.error("查询失败",e);
            throw new ParamErrorException(e.getMessage());
        }
    }



    @RequestMapping(value = "/selectByCategory",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "tag",value = "前端切换标识0：故障，1：报警",paramType = "query"),
    })
    public TableResultResponse<DeviceAbnormal> selectByCategory(@RequestParam(defaultValue = "1") String page,@RequestParam(defaultValue = "10") String limit, String dateStrs,String tag) throws ParseException {
        if(dateStrs ==null){
            throw new ParamErrorException("时间不能为空");
        }
        Map<String ,Object> param = new LinkedHashMap<>();
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        String startDate = null;
        String endDate = null;
        String[] date = dateStrs.split(",");
        startDate = date[0];
        endDate = date[1];
        Page pageInfo = PageHelper.startPage(query.getPage(),query.getLimit());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = DateUtil.getStartTime(dateFormat.parse(startDate));
        Date endTime = DateUtil.getEndTime(dateFormat.parse(endDate));
        List<DeviceAbnormal> list = baseBiz.selectByCategory(startTime,endTime,tag);
        return new TableResultResponse<>(pageInfo.getTotal(),list);
    }



    @RequestMapping(value = "/selectTypeCountByCategory",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("故障类型统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，以','隔开",paramType = "query"),
            @ApiImplicitParam(name = "tag",value = "前端切换标识0：故障，1：报警",paramType = "query"),
    })
    public ObjectRestResponse selectTypeCountByCategory(String dateStrs,String tag) throws ParseException {
        if(ValidatorUtils.hasAnyBlank(dateStrs,tag)){
            throw new ParamErrorException("参数不能为空");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] dates = dateStrs.split(",");
        Date startTime = DateUtil.getStartTime(dateFormat.parse(dates[0]));
        Date endTime = DateUtil.getEndTime(dateFormat.parse(dates[1]));
        List<Map<String,Object>> list = baseBiz.selectTypeCountByCategory(startTime,endTime,tag);
        return new ObjectRestResponse().data(list);
    }

    @RequestMapping(value = "/restore",method = RequestMethod.GET)
    @ResponseBody
    @IgnoreUserToken
    @IgnoreClientToken
    @ApiOperation("恢复异常")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sensorNo",value = "设备编号",paramType = "query")
    })
    public ObjectRestResponse restore(String sensorNo){
        if(StringUtils.isBlank(sensorNo)){
            throw new ParamErrorException("参数不能为空");
        }
        try {
            JSONObject sensorObject = deviceFeign.selectBySensorNo(sensorNo);
            if(sensorObject == null || sensorObject.getJSONObject("data") == null){
                throw new RuntimeException(String.format("设备%S不存在",sensorNo));
            }
            DeviceSensor sensor = sensorObject.getJSONObject("data").toJavaObject(DeviceSensor.class);
            businessI.restoreAbnormal(sensor,null);
            return new ObjectRestResponse();
        }catch (Exception e){
            log.error("恢复异常失败:{}",e);
            throw new ParamErrorException(e.getMessage());
        }
    }
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public ObjectRestResponse test(){
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        try {
            businessI.alertFireMainMSG("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectRestResponse;
    }

    @Autowired
    BusinessI businessI;




}