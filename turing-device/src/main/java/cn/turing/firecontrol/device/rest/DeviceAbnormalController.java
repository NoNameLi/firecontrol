package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.DateUtil;
import cn.turing.firecontrol.device.vo.ResultVo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("deviceAbnormal")
@CheckClientToken
@CheckUserToken
@Api("异常记录")
public class DeviceAbnormalController extends BaseController<DeviceAbnormalBiz,DeviceAbnormal,Integer> {

    @Autowired
    private DeviceAbnormalBiz daBiz;
    @Autowired
    private DeviceBuildingBiz dbBiz;
    @Autowired
    private DeviceSensorBiz dsBiz;
    @Autowired
    private DeviceSensorTypeBiz dstBiz;
    @Autowired
    private DeviceMeasuringPointBiz dmp;
    @Autowired
    private DeviceNetworkingUnitBiz dnuBiz;
    @Autowired
    private IUserFeign IUserFeign;
    @Autowired
    private DeviceFacilitiesAbnormalBiz dafBiz;

    /**
     *
     * @author  zhangpeng
     * @description 分页列表查询异常记录
     * @param page   分页时页码
     * @param limit  分页时每页显示条数
     * @param deviceAbnormal 条件搜索对象
     * @param dateStrs  时间段搜索，两个时间加逗号的字符串
     * @param tag   前端显示标识
     * @param bName 建筑名称，搜索条件
     * @param floorId 建筑楼层，搜索条件
     * @param sensorNo 传感器编号，搜索条件
     * @param positionDescription 位置描述，搜索条件
     * @param equipmentType 传感器类型，搜索条件
     * @param channelId 系统栏目id
     * @return List<DeviceAbnormal>
     */
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    public TableResultResponse<DeviceAbnormal> list(@RequestParam String page, @RequestParam String limit, DeviceAbnormal deviceAbnormal,
                                                   String dateStrs,@RequestParam String tag,String bName,String floorId,String sensorNo,String positionDescription,String equipmentType,Integer channelId){
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
        return daBiz.selectQuery(query,deviceAbnormal,startDate,endDate,tag,bName,floorId,sensorNo,positionDescription,equipmentType,channelId);
    }

    /**
     *
     * @author  zhangpeng
     * @description  查询所有报警类型
     * @param channelId  系统栏目id
     * @param alrmCategory  报警类型：0：故障，1：火警
     * @param handleFlag  是否处理[1=是/0=否]
     * @return list<String>
     */
    @RequestMapping(value = "/selectAlrmType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有报警类型")
    public ObjectRestResponse<DeviceAbnormal> selectAlrmType(Integer channelId,String alrmCategory,String handleFlag) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<String> list = daBiz.selectAlrmType(channelId,alrmCategory,handleFlag);//查询报警类型列表
        responseResult.setData(list);
        return responseResult;
    }

    /**
     *
     * @author  zhangpeng
     * @description  查询真实火警、误报火警、故障报警各自的数量
     * @param channelId 系统栏目id
     * @return  Map<String,Object>
     */
    @RequestMapping(value = "/selectCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有真实火警、误报火警、故障报警数量")
    public ObjectRestResponse<DeviceAbnormal> selectCount(Integer channelId) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Integer falseFire = daBiz.selectCountByAlrmCategoryAndFireFlag("0","1","1",channelId);//误报
        Integer realFire = daBiz.selectCountByAlrmCategoryAndFireFlag("1","1","1",channelId);//真实火警
        Integer faultAlarm = daBiz.selectCountByAlrmCategoryAndFireFlag(null,"0",null,channelId);//故障报警
        Map<String,Object> map = new HashMap<>();//返回map
        map.put("realFire",realFire);
        map.put("falseFire",falseFire);
        map.put("faultAlarm",faultAlarm);
        responseResult.setData(map);
        return responseResult;
    }

    /**
     *
     * @author zhangpeng
     * @description 确认是否是真实火警,并处理
     * @param id 异常记录id
     * @param flag 是否为真实火警[1=是/0=否/2=火警测试]
     */
    @RequestMapping(value = "/affirmFire",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("确认是否是真实火警,并处理")
    public ObjectRestResponse<DeviceAbnormal> affirmFire(@RequestParam Integer id,@RequestParam String flag) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        if(id==null || flag==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            DeviceAbnormal deviceAbnormal = daBiz.selectById(id);
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

    /**
     *
     * @author  zhangpeng
     * @description  确认处理
     * @param id  异常记录id
     */
    @RequestMapping(value = "/affirmHandle",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("确认处理")
    public ObjectRestResponse<DeviceAbnormal> affirmHandle(@RequestParam Integer id) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        if(id==null ){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            DeviceAbnormal deviceAbnormal = daBiz.selectById(id);
            deviceAbnormal.setHandleFlag("1");//是否处理[1=是/0=否]'
            deviceAbnormal.setHandlePerson(BaseContextHandler.getUsername());//处理人
            deviceAbnormal.setHandleDate(new Date());//处理时间
            daBiz.updateSelectiveById(deviceAbnormal);
        }
        return responseResult;
    }

    /**
     *
     * @author  zhangpeng
     * @description  根据建筑物查询对应的异常数量并排名
     * @param tag  月、季度、年的标识，0:本月 1:本季度 2:本年度
     * @param dateStrs  时间段，以逗号隔开的字符串
     * @param channelId  系统栏目id
     * @return  List<Map<String,Object>>
     * @throws Exception
     */
    @RequestMapping(value = "/selectCountByBuildId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑物查询对应的异常数量并排名")
    public ObjectRestResponse<DeviceAbnormal> selectCountByBuildId(String tag, String dateStrs,Integer channelId) throws Exception{
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = null;
        String endDate = null;
        if(dateStrs !=null){
            String[] date = dateStrs.split(",");
            startDate = date[0];
            endDate = date[1];
        }
        if(tag !=null){
            if(tag.equals("0")){//当前月份
                Date date = new Date();
                startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
            if(tag.equals("1")){//本季度
                startDate = simpleDateFormat.format(DateUtil.getCurrentQuarterStartTime());
                //endDate = simpleDateFormat.format(DateUtil.getCurrentQuarterEndTime());
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
            if(tag.equals("2")){//本年度
                startDate = slf.format(DateUtil.getCurrentYearStartTime())+" 00:00:00";
                //endDate = simpleDateFormat.format(DateUtil.getCurrentYearEndTime())+" 23:59:59";
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
        }
        if(startDate==null && endDate==null){//当前月份
            Date date = new Date();
            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        List<DeviceBuilding> list = dbBiz.selectBySensor(channelId);
        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
        for (DeviceBuilding deviceBuilding:list){
            Map<String ,Object> map = new HashMap<>();
//            List<DeviceAbnormal> abnormallist = daBiz.selectCountByBuildId(simpleDateFormat.parse(startDate+ " 00:00:00"),
//                    simpleDateFormat.parse(endDate + " 23:59:59"),deviceBuilding.getId(),null);
            Integer count = daBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate+ " 00:00:00"),simpleDateFormat.parse(endDate + " 23:59:59"),deviceBuilding.getBName(),null,channelId);
            if(count!=0){
                map.put("name",deviceBuilding.getBName());
                map.put("count",count);
                resultList.add(map);
            }
        }
        if(resultList.size()>1){
            sort(resultList);//按count排序
        }
        int i =1;
        for (Map map:resultList) {//生成排名id
            map.put("id",i);
            i =i+1;
        }
        responseResult.setData(resultList);
        return responseResult;
    }

//    @RequestMapping(value = "/selectCountByBuildId",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("根据建筑物查询对应的异常数量并排名")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
//            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，已','隔开",paramType = "query"),
//            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
//    })
//    public ObjectRestResponse<DeviceAbnormal> selectCountByBuildId(String tag, String dateStrs,Integer channelId) throws Exception{
//        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String startDate = null;
//        String endDate = null;
//        if(dateStrs !=null){
//            String[] date = dateStrs.split(",");
//            startDate = date[0];
//            endDate = date[1];
//        }
//        if(tag !=null){
//            if(tag.equals("0")){//今天
//                startDate = simpleDateFormat.format(DateUtil.getStartTime());
//                endDate = simpleDateFormat.format(DateUtil.getEndTime());
//            }
//            if(tag.equals("1")){//昨天
//                startDate = DateUtil.getYesterdayStartDay();
//                endDate = DateUtil.getYesterdayEedDay();
//            }
//            if(tag.equals("2")){//最近7天
//                startDate = DateUtil.getRecentlySevenStartDay();
//                endDate = simpleDateFormat.format(DateUtil.getEndTime());
//            }
//            if(tag.equals("3")){//最近30天
//                startDate = DateUtil.getRecentlyStartDay();
//                endDate = simpleDateFormat.format(DateUtil.getEndTime());
//            }
//        }
//        if(startDate==null && endDate==null){//最近7天
//            startDate = DateUtil.getRecentlySevenStartDay();
//            endDate = simpleDateFormat.format(DateUtil.getEndTime());
//        }
//        List<DeviceBuilding> list = dbBiz.selectBySensor(channelId);
//        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
//        for (DeviceBuilding deviceBuilding:list){
//            Map<String ,Object> map = new HashMap<>();
//            Integer count = daBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate+ " 00:00:00"),simpleDateFormat.parse(endDate + " 23:59:59"),deviceBuilding.getBName(),null);
//            if(count!=0){
//                map.put("name",deviceBuilding.getBName());
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

    /**
     *
     * @author  zhangpeng
     * @description  根据报警类型查询对应的异常数量
     * @param tag  月、季度、年的标识，0:本月 1:本季度 2:本年度
     * @param dateStrs   时间段，以逗号隔开的字符串
     * @param channelId  系统栏目id
     * @return  List<Map<String,Object>>
     * @throws Exception
     */
    @RequestMapping(value = "/selectCountByType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据报警类型查询对应的异常数量")
    public ObjectRestResponse<DeviceAbnormal> selectCountByType(String tag, String dateStrs,Integer channelId) throws Exception{
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = null;
        String endDate = null;
        if(dateStrs !=null){
            String[] date = dateStrs.split(",");
            startDate = date[0];
            endDate = date[1];
        }
        if(tag !=null){
            if(tag.equals("0")){//当前月份
                Date date = new Date();
                startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
            if(tag.equals("1")){//本季度
                startDate = simpleDateFormat.format(DateUtil.getCurrentQuarterStartTime());
                //endDate = simpleDateFormat.format(DateUtil.getCurrentQuarterEndTime());
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
            if(tag.equals("2")){//本年度
                startDate = slf.format(DateUtil.getCurrentYearStartTime())+" 00:00:00";
                //endDate = simpleDateFormat.format(DateUtil.getCurrentYearEndTime())+" 23:59:59";
                endDate = simpleDateFormat.format(DateUtil.getEndTime());
            }
        }
        if(startDate==null && endDate==null){//当前月份
            Date date = new Date();
            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        //List<DeviceAbnormal> list = daBiz.selectAlrmType();//查询所有报警类型
        List<String> typelist = daBiz.selectAlrmType(channelId,null,null);//报警类型list
        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
        for (String s:typelist){
            Map<String ,Object> map = new HashMap<>();
            Integer count = daBiz.selectCountByType(simpleDateFormat.parse(startDate+ " 00:00:00"),
                    simpleDateFormat.parse(endDate + " 23:59:59"),s);
            map.put("alrmType",s);
            map.put("count",count);
            resultList.add(map);
        }
        responseResult.setData(resultList);
        return responseResult;
    }

//    @RequestMapping(value = "/selectCountByType",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("根据报警类型查询对应的异常数量")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
//            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，已','隔开",paramType = "query"),
//            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
//    })
//    public ObjectRestResponse<DeviceAbnormal> selectCountByType(String tag, String dateStrs,Integer channelId) throws Exception{
//        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String startDate = null;
//        String endDate = null;
//        if(dateStrs !=null){
//            String[] date = dateStrs.split(",");
//            startDate = date[0];
//            endDate = date[1];
//        }
//        if(tag !=null){
//            if(tag.equals("0")){//今天
//                startDate = simpleDateFormat.format(DateUtil.getStartTime());
//                endDate = simpleDateFormat.format(DateUtil.getEndTime());
//            }
//            if(tag.equals("1")){//昨天
//                startDate = DateUtil.getYesterdayStartDay();
//                endDate = DateUtil.getYesterdayEedDay();
//            }
//            if(tag.equals("2")){//最近7天
//                startDate = DateUtil.getRecentlySevenStartDay();
//                endDate = simpleDateFormat.format(DateUtil.getEndTime());
//            }
//            if(tag.equals("3")){//最近30天
//                startDate = DateUtil.getRecentlyStartDay();
//                endDate = simpleDateFormat.format(DateUtil.getEndTime());
//            }
//        }
//        if(startDate==null && endDate==null){//最近7天
//            startDate = DateUtil.getRecentlySevenStartDay();
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

    /**
     *
     * @author  zhangpeng
     * @description  根据时间来统计每天的异常数量
     * @param tag   月、季度、年的标识，0:本月 1:本季度 2:本年度
     * @param dateStrs  时间段，以逗号隔开的字符串
     * @param channelId  系统栏目id
     * @return   list
     * @throws Exception
     */
    @RequestMapping(value = "/selectCountByMonth",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("每天的异常数量统计")
    public ObjectRestResponse<DeviceAbnormal> selectCountByMonth(String tag, String dateStrs,Integer channelId) throws Exception {
       ObjectRestResponse responseResult = new ObjectRestResponse<>();
        SimpleDateFormat  slf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = null;
        String endDate = null;
        if(dateStrs !=null){
            String[] date = dateStrs.split(",");
            startDate = date[0]+" 00:00:00";
            endDate = date[1]+" 23:59:59";
        }
        if(tag !=null){
            if(tag.equals("0")){//当前月份
                Date date = new Date();
                startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date))+" 00:00:00";
                endDate = slf.format(DateUtil.getEndTime());
            }
            if(tag.equals("1")){//本季度
                startDate = slf.format(DateUtil.getCurrentQuarterStartTime());
                //endDate = simpleDateFormat.format(DateUtil.getCurrentQuarterEndTime());
                endDate = slf.format(DateUtil.getEndTime());
            }
            if(tag.equals("2")){//本年度
                startDate = simpleDateFormat.format(DateUtil.getCurrentYearStartTime())+" 00:00:00";
                //endDate = simpleDateFormat.format(DateUtil.getCurrentYearEndTime())+" 23:59:59";
                endDate = slf.format(DateUtil.getEndTime());
            }
        }
        if(startDate==null && endDate==null){//当前月份
            Date date = new Date();
            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date))+" 00:00:00";
            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
            endDate = slf.format(DateUtil.getEndTime());
        }
        List<ResultVo> resultList = daBiz.selectCountByDate(slf.parse(startDate),slf.parse(endDate),channelId,null);
//        List<ResultVo> faultList = daBiz.selectCountByDate(slf.parse(startDate),slf.parse(endDate),channelId,"0");//故障
//        List<ResultVo> fireList = daBiz.selectCountByDate(slf.parse(startDate),slf.parse(endDate),channelId,"1");//火警
//        Map map = new HashMap();
//        map.put("faultList",faultList);
//        map.put("fireList",fireList);
//        responseResult.setData(map);
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/selectFaultCountByMonth",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("每天的故障数量统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，已','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceAbnormal> selectFaultCountByMonth(String tag, String dateStrs,Integer channelId) throws Exception {
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        if(startDate==null && endDate==null){//最近7天
            startDate = DateUtil.getRecentlySevenStartDay();
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        List<ResultVo> resultList = daBiz.selectCountByDate(simpleDateFormat.parse(startDate),simpleDateFormat.parse(endDate),channelId,"0");
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/selectCallCountByMonth",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("每天的报警数量统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag",value = "时间切换标识，0=今天 1=昨天 2=最近7天 3=最近30天",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，已','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceAbnormal> selectCallCountByMonth(String tag, String dateStrs,Integer channelId) throws Exception {
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        SimpleDateFormat  simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        if(startDate==null && endDate==null){//最近7天
            startDate = DateUtil.getRecentlySevenStartDay();
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        List<ResultVo> resultList = daBiz.selectCountByDate(simpleDateFormat.parse(startDate),simpleDateFormat.parse(endDate),channelId,"1");
        responseResult.setData(resultList);
        return responseResult;
    }

    /**
     *
     * @author  zhangpeng
     * @description   根据时间查询火警、异常各自的数量
     * @param identity  日，周，月、年的标识，0:本日 1:本周 2:本月 3:本年
     * @return  List<Map<String,Object>>
     * @throws Exception
     */
    @RequestMapping(value = "/getCountByToday",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据时间查询火警、异常数量")
    public ObjectRestResponse<DeviceAbnormal> getCountByToday(@RequestParam String identity,Integer channelId) throws Exception {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate=null;
        String endDate=null;
        if(identity.equals("0")){//本日
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("1")){//本周
            startDate = slf.format(DateUtil.getBeginDayOfWeek())+" 00:00:00";
            endDate = slf.format(DateUtil.getEndTime())+" 23:59:59";
        }
        if(identity.equals("2")){//本月
            Date date = new Date();
            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
            endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
        }
        if(identity.equals("3")){//本年
            startDate = slf.format(DateUtil.getCurrentYearStartTime())+" 00:00:00";
            endDate = simpleDateFormat.format(DateUtil.getCurrentYearEndTime())+" 23:59:59";
        }
        List<Map<String,Object>> resultList = new ArrayList();//返回数据集合
        for(int i = 0; i<2;i++){
            Map<String ,Object> map = new HashMap<>();
            Integer count = daBiz.getCountByToday(simpleDateFormat.parse(startDate),
                    simpleDateFormat.parse(endDate),i+"",channelId);
            map.put("alrmType",i);//0：故障，1：火警
            map.put("count",count);
            resultList.add(map);
        }
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/selectAlrm",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP分页查询报警信息")
    public TableResultResponse selectAlrm(String handleFlag,String alrmCategory,Integer channelId, String page, String limit) {
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
    public ObjectRestResponse selectAlrmByDetails(Long sensorId,Integer alarmId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map map = new HashMap();
//        DeviceSensor deviceSensor = dsBiz.selectById(sensorId);//传感器信息
//        DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());//传感器类型
        DeviceAbnormal deviceAbnormal = daBiz.selectById(alarmId);//报警信息
        DeviceBuilding deviceBuilding = dbBiz.selectById(deviceAbnormal.getBuildId());//建筑物信息
        DeviceNetworkingUnit deviceNetworkingUnit =dnuBiz.selectById(deviceBuilding.getOid());//联网单位
        DeviceMeasuringPoint deviceMeasuringPoint = dmp.selectByCodeName(deviceAbnormal.getUnit());//测点
        if(deviceAbnormal.getAlrmCategory().equals("1")){
            map.put("alrmType3",deviceAbnormal.getLevel());
        }
        map.put("measuringPoint",deviceMeasuringPoint.getMeasuringPoint());
        map.put("equipmentType",deviceAbnormal.getEquipmentType());
        map.put("sensorNo",deviceAbnormal.getSensorNo());
        map.put("fireFlag",deviceAbnormal.getFireFlag());
        map.put("bName",deviceBuilding.getBName());
        map.put("positionDescription",deviceAbnormal.getFloor()+"F-"+deviceAbnormal.getPositionDescription());
        map.put("alrmType1",deviceAbnormal.getAlrmType());
        map.put("alrmType2",deviceAbnormal.getAlrmData()+deviceMeasuringPoint.getDataUnit());
        map.put("alrmDate",deviceAbnormal.getAlrmDate());
        map.put("handleFlag",deviceAbnormal.getHandleFlag());
        map.put("safeDutyName",deviceNetworkingUnit.getSafeDutyName());
        map.put("safeDutyPhone",deviceNetworkingUnit.getSafeDutyPhone());
        //map.put("handleDate",deviceAbnormal.getHandleDate());
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

    @RequestMapping(value = "/selectByBuildId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据建筑物id查询对应的报警信息")
    public ObjectRestResponse<DeviceAbnormal> selectByBuildId(@RequestParam Integer buildId,Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map resultmap = new HashMap();
        List faultlist = new ArrayList();
        List alrmlist = new ArrayList();
        List normallist = new ArrayList();
        //传感器集合
        List<DeviceSensor> list = dsBiz.selectByBuildingId(buildId,channelId);
        Integer faultCount = 0;
        Integer alrmCount = 0;
        Integer normalCount = 0;
        for(DeviceSensor deviceSensor:list) {
            //根据传感器类型id查询类型
            DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());
            if (deviceSensor.getStatus().equals("2")) {//正常
                normalCount++;
                Map<String, Object> map = new HashMap<>();
                map.put("type", deviceSensorType.getEquipmentType());//设备类型
                map.put("positionDescription", deviceSensor.getPositionDescription());//设备位置
                map.put("floor", deviceSensor.getFloor());//楼层
                normallist.add(map);
            }
            //根据传感器id，建筑物id查询报警记录
            List<DeviceAbnormal> abnormallist = daBiz.selectByEquIdAndBuildId(deviceSensor.getId(), buildId);
            if(abnormallist.size()==0){
                continue;
            }
            if (deviceSensor.getStatus().equals("0")) {//故障
                faultCount++;
                Map<String, Object> map = new HashMap<>();
                map.put("type", deviceSensorType.getEquipmentType());//设备类型
                map.put("positionDescription", deviceSensor.getPositionDescription());//设备位置
                map.put("date", abnormallist.get(0).getAlrmDate());//报警时间
                map.put("floor", deviceSensor.getFloor());//楼层
                faultlist.add(map);
            }
            if (deviceSensor.getStatus().equals("1")) {//报警
                alrmCount++;
                Map<String, Object> map = new HashMap<>();
                map.put("type", deviceSensorType.getEquipmentType());//设备类型
                map.put("positionDescription", deviceSensor.getPositionDescription());//设备位置
                map.put("date", abnormallist.get(0).getAlrmDate());//报警时间
                map.put("floor", deviceSensor.getFloor());//楼层
                alrmlist.add(map);
            }
        }
        Map map1 = new HashMap();
        map1.put("faultCount",faultCount);
        faultlist.add(map1);
        Map map2 = new HashMap();
        map2.put("alrmCount",alrmCount);
        alrmlist.add(map2);
        Map map3 = new HashMap();
        map3.put("normalCount",normalCount);
        normallist.add(map3);
        resultmap.put("faultlist",faultlist);
        resultmap.put("alrmlist",alrmlist);
        resultmap.put("normallist",normallist);
        responseResult.setData(resultmap);
        return responseResult;
    }

    @RequestMapping(value = "/selectFaultCountByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据时间查询每天的故障报警次数")
    public ObjectRestResponse<DeviceAbnormal> selectFaultCountByDate(@RequestParam String identity,Integer buildId,Integer channelId) throws Exception {
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
            Integer count = daBiz.selectAlrmCountByDate(simpleDateFormat.format(date),buildId,"0",channelId);
            map.put("date",simpleDateFormat.format(date));
            map.put("count",count);
            resultList.add(map);
        }
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/selectFireCountByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据时间查询每天的火警报警次数")
    public ObjectRestResponse<DeviceAbnormal> selectFireCountByDate(@RequestParam String identity,Integer buildId,Integer channelId) throws Exception {
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
            Integer count = daBiz.selectAlrmCountByDate(simpleDateFormat.format(date),buildId,"1",channelId);
            map.put("date",simpleDateFormat.format(date));
            map.put("count",count);
            resultList.add(map);
        }
        responseResult.setData(resultList);
        return responseResult;
    }

    @RequestMapping(value = "/selectCountByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据时间查询建筑物对应的报警、故障数量")
    public ObjectRestResponse<DeviceAbnormal> selectCountByDate(@RequestParam String identity,@RequestParam String alrmCategory,Integer channelId) throws Exception{
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate=null;
        String endDate=null;
        if(identity.equals("0")){//本日
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("1")){//本周
            startDate = slf.format(DateUtil.getBeginDayOfWeek())+" 00:00:00";
            //endDate = slf.format(DateUtil.getEndTime())+" 23:59:59";
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("2")){//本月
            Date date = new Date();
            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        Integer allcount = daBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),null,alrmCategory,channelId);//所有的次数
        DecimalFormat df = new DecimalFormat("0.00");
        if(identity.equals("3")){//本年
            startDate = slf.format(DateUtil.getCurrentYearStartTime())+" 00:00:00";
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        List<DeviceBuilding> list = dbBiz.getAll(null);//查询所有建筑
        List resultlist = new ArrayList();
        for (DeviceBuilding deviceBuilding:list){
            Map<String ,Object> map = new HashMap<>();
            //List<DeviceAbnormal> countlist = daBiz.selectCountByBuildId(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),deviceBuilding.getId(),alrmCategory);
            Integer count = daBiz.selectCountByBuildIdAndDate(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),deviceBuilding.getBName(),alrmCategory,channelId);
            if(count!=0){
                map.put("name",deviceBuilding.getBName());
                map.put("count",count);
                //List<DeviceAbnormal> alllist = daBiz.selectCountByBuildId(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),null,alrmCategory);
                map.put("percentage",df.format(((float)count/allcount)*100));//计算百分比
                resultlist.add(map);
            }
        }
        if(resultlist.size()>10){//如果list大于10取前10条
            responseResult.setData(sort(resultlist).subList(0,10));
        }else{
            responseResult.setData(sort(resultlist));
        }
        return responseResult;
    }

    @RequestMapping(value = "/selectCallCountByDate",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据时间查询统计报警、故障数量")
    public ObjectRestResponse<DeviceAbnormal> selectCallCountByDate(@RequestParam String identity,String alrmcategory,Integer channelId) throws Exception {//alrmcategory 0：故障，1：火警
        ObjectRestResponse responseResult = new ObjectRestResponse<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String startDate=null;
        String endDate=null;
        if(identity.equals("0")){//本日
            startDate = simpleDateFormat.format(DateUtil.getStartTime());
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("1")){//本周
            startDate = simpleDateFormat.format(DateUtil.getBeginDayOfWeek())+" 00:00:00";
            //endDate = simpleDateFormat.format(DateUtil.getEndTime())+" 23:59:59";
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("2")){//本月
            Date date = new Date();
            startDate = simpleDateFormat.format(DateUtil.getBeginDayofMonth(date));
            //endDate = simpleDateFormat.format(DateUtil.getEndDayofMonth(date));
            endDate = simpleDateFormat.format(DateUtil.getEndTime());
        }
        if(identity.equals("3")){//本年
            List<ResultVo> countlist = daBiz.selectByYear( DateUtil.getNowYear(),alrmcategory);
            responseResult.setData(countlist);
            return responseResult;
        }
        List<Date> list = DateUtil.getBetweenDates(simpleDateFormat.parse(startDate),simpleDateFormat.parse(endDate));
        Map map = new HashMap();//返回数据集合
        List datelist = new LinkedList();
        List countlist = new LinkedList<>();
        for (Date date:list){
            Integer count = daBiz.selectCountByMonth(simpleDateFormat.format(date),alrmcategory,channelId);
            datelist.add(simpleDateFormat.format(date).substring(5,10).replace("-","."));
            countlist.add(count);
        }
        map.put("datelist",datelist);
        map.put("countlist",countlist);
        responseResult.setData(map);
        return responseResult;
    }

    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("添加异常记录表")
    public ObjectRestResponse  add(@RequestBody DeviceAbnormal deviceAbnormal){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        daBiz.insertSelective(deviceAbnormal);
        return responseResult;
    }

    @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/batchInsert",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("批量添加异常记录")
    public ObjectRestResponse batchInsert(@RequestBody List<DeviceAbnormal> list){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        if(list.size()==0){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else{
            List idList = new LinkedList();
            for(DeviceAbnormal deviceAbnormal:list){
                daBiz.insertSelective(deviceAbnormal);
                idList.add(deviceAbnormal.getId());
            }
            responseResult.setData(idList);//返回id集合
        }
        return responseResult;
    }

    @RequestMapping(value = "/getAlrmCountByChannel",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP查询所属系统下各自报警次数")
    public ObjectRestResponse getAlrmCountByChannel(String channelIds){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        String [] ids = channelIds.split(",");
        List list = new LinkedList();
        Integer allCount = daBiz.selectCountByAlrmCategoryAndFireFlag(null,null,"0",null);//全部
        list.add(allCount);
        for(int i=0;i<ids.length;i++){
            JSONObject jsonObject = IUserFeign.selectById(Integer.valueOf(ids[i]));
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            if(!"5".equals(jsonObject1.getString("id"))){
                Integer count = daBiz.selectCountByAlrmCategoryAndFireFlag(null,null,"0",Integer.valueOf(ids[i]));//各自系统下次数
                list.add(count);
            }else {
                Integer count = dafBiz.selectCountByChannelId("0",Integer.valueOf(ids[i]));//各自系统下次数
                list.add(count);
            }
        }
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/getAllAlrmAndFault",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("实时监测，火警处理列表-故障处理列表，未处理的记录")
    public TableResultResponse getAllAlrmAndFault(Integer channelId,@RequestParam String alrmCategory,String limit,String page) throws Exception{
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
//            //获取测点的代号
//            String codeName =  deviceAbnormal.get("unit").toString();
//            //获取测点
//            DeviceMeasuringPoint deviceMeasuringPoint = dmp.selectByCodeName(codeName);
            map.put("id",deviceAbnormal.get("id"));
            map.put("bName",deviceAbnormal.get("bName"));
            map.put("series",deviceAbnormal.get("series"));
            map.put("position",deviceAbnormal.get("floor")+"F-"+deviceAbnormal.get("position"));
            map.put("alarmType",deviceAbnormal.get("alrmType"));
            map.put("date",DateUtil.getFriendlytime((Date) deviceAbnormal.get("alrmDate")));
            map.put("logId",deviceAbnormal.get("logId"));
//            if(deviceMeasuringPoint!=null){
//                map.put("measuringPoint",deviceMeasuringPoint.getMeasuringPoint());
//                map.put("alarmData",deviceAbnormal.get("alrmData")+deviceMeasuringPoint.getDataUnit());
//            }else {
//                map.put("alarmData",deviceAbnormal.get("alrmData"));
//            }
            if(deviceAbnormal.get("dataUnit")!=null){
                map.put("alarmData",deviceAbnormal.get("alrmData")+deviceAbnormal.get("dataUnit").toString());
            }else {
                map.put("alarmData",deviceAbnormal.get("alrmData"));
            }
            map.put("measuringPoint",deviceAbnormal.get("measuringPoint"));
            map.put("level",deviceAbnormal.get("level"));
            lists.add(map);
        }
        return new TableResultResponse(result.getTotal(),lists);
    }

    @RequestMapping(value = "/getAlrmBySensorId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器id分页查询异常记录list,云平台")
    public TableResultResponse getAlrmBySensorId(@RequestParam(value = "sensorId") String sensorId,String limit,String page){
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
            resultList = daBiz.selectByEquIdResultMP(deviceAbnormal);
            for(int i=0;i<resultList.size();i++){
                Map<String,Object> map = resultList.get(i);
                if(map.get("confirPerson")==null||"".equals(map.get("confirPerson").toString())){
                    map.put("confirPerson",map.get("handlePerson"));
                    map.put("confirDate",map.get("handleDate"));
                }
/*                if(map.get("alrmCategory")!=null&&"0".equals(map.get("alrmCategory"))){
                    map.put("confirPerson",map.get("handlePerson"));
                    map.put("confirDate",map.get("handleDate"));
                }*/
            }
        }
        return new TableResultResponse(result.getTotal(),resultList);
    }

    @RequestMapping(value = "/getAlrmListBySensorId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器id分页查询异常记录listAPP")
    public TableResultResponse getAlrmListBySensorId(@RequestParam(value = "sensorId") String sensorId,String limit,String page){
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
            List<DeviceAbnormal> list = daBiz.selectByEquId(Long.valueOf(sensorId),"1",null);
//            DeviceSensor deviceSensor =dsBiz.selectById(Long.valueOf(sensorId));
            for(DeviceAbnormal deviceAbnormal:list){
                Map map = new HashMap();
                //获取测点的代号
                String codeName = deviceAbnormal.getUnit();
                //获取测点
                DeviceMeasuringPoint deviceMeasuringPoint = dmp.selectByCodeName(codeName);
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
                if(deviceMeasuringPoint!=null){
                    map.put("alarmData",deviceAbnormal.getAlrmData()+deviceMeasuringPoint.getDataUnit());
                    map.put("measuringPoint",deviceMeasuringPoint.getMeasuringPoint());
                    //当报警时获取报警等级
                    if("1".equals(deviceAbnormal.getAlrmCategory())){
                        map.put("level",deviceAbnormal.getLevel());
                    }
                }
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
            @ApiImplicitParam(name = "dateStrs",value = "时间区间字符串，已','隔开",paramType = "query"),
            @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    })
    public ObjectRestResponse<DeviceAbnormal> getRatioByDate(String tag,String dateStrs,Integer channelId) throws Exception {
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
        Integer handleCount = daBiz.getCountByHandleFlag(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),"1",channelId);
        //查询当前时间报警数量
        Integer count = daBiz.getCountByToday(simpleDateFormat.parse(startDate), simpleDateFormat.parse(endDate),null,channelId);
        DecimalFormat df = new DecimalFormat("0.00");
        Map map = new HashMap();
        if(count!=0){
            map.put("handle_percentage",df.format(((float)handleCount/count)*100));
            map.put("notHandle_percentage",df.format(100-((float)handleCount/count)*100));
        }else{
            map.put("handle_percentage","100");
            map.put("notHandle_percentage","0");
        }
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getAlrmFloor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑id获得报警楼层")
    public ObjectRestResponse getAlrmFloor(@RequestParam Integer buildId,Integer channelId) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
//        Map map = new HashMap();
        List<Map> result = new ArrayList<>();
        List<DeviceSensor> list = dsBiz.selectByBuildingId(buildId,channelId);
        //获取所有的楼层
        Set<Integer> floor = new HashSet<>();
        for(int i=0;i<list.size();i++ ){
            floor.add(list.get(i).getFloor());
        }
        List<Integer> floorList = new ArrayList<>(floor);
        //根据楼层查看异常的传感器
        for(int i=0;i<floorList.size();i++){
            Map map = new HashMap();
            String status ="2";
            map.put("floor",floorList.get(i));
            for(int j=0;j<list.size();j++){
                DeviceSensor deviceSensor=list.get(j);
                //判断当前楼层报一个火警就是报警，没有报警，有故障就是故障，都没有是正常
                if(floorList.get(i)==deviceSensor.getFloor()){
                    if(deviceSensor.getStatus().equals("1")){//火警
                        status ="1";
                        map.put("status",status);
                        break;
                    }else if(deviceSensor.getStatus().equals("0")||deviceSensor.getStatus().equals("4")) {
                        status ="0";
                        map.put("status",status);
                    }
                }
            }
            //正常不返回
            if(!status.equals("2")){
                result.add(map);
            }
        }
        responseResult.setData(result);
        return responseResult;
    }


    private static List sort(List<Map<String,Object>> list) {
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return (Integer)o2.get("count")-((Integer)o1.get("count"));
            }
        });
        return new ArrayList(list);
    }

//    @RequestMapping(value = "/selectListByBuildId",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation("APP根据建筑id分页查询传感器报警信息列表")
//    public TableResultResponse  selectListByBuildId(Integer buildId,String status,String page,String limit){
//        Map<String ,Object> param = new LinkedHashMap<>();
//        if(page==null||"".equals(page)){
//            page = "1";
//        }
//        if(limit==null||"".equals(limit)){
//            limit = "10";
//        }
//        param.put("page",page);
//        param.put("limit",limit);
//        Query query = new Query(param);
//        return dsBiz.selectListByBuildId(query,buildId,status);
//    }
}