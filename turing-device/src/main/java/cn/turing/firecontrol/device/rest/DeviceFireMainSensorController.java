package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.device.biz.DeviceBuildingBiz;
import cn.turing.firecontrol.device.biz.DeviceFireMainBiz;
import cn.turing.firecontrol.device.biz.DeviceFireMainSensorBiz;
import cn.turing.firecontrol.device.biz.DeviceFloorLayoutBiz;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.util.*;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.*;

@RestController
@RequestMapping("deviceFireMainSensor")
@CheckClientToken
@CheckUserToken
public class DeviceFireMainSensorController extends BaseController<DeviceFireMainSensorBiz,DeviceFireMainSensor,Integer> {

    @Autowired
    private DeviceFireMainBiz dfmbiz;
    @Autowired
    private DeviceBuildingBiz dbBiz;
    @Autowired
    private DeviceFireMainSensorBiz dfmsBiz;
    @Autowired
    private DeviceFloorLayoutBiz dflBiz;
    @Autowired
    private IUserFeign iUserFeign;

    private  static final Logger log = LoggerFactory.getLogger(DeviceFireMainSensorController.class);

    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    public TableResultResponse<Map> list(String page, String limit, String ids, String code, DeviceFireMainSensor deviceFireMainSensor, String floorId) {
        //当楼层乱输入时直接返回空，查不到
        if(deviceFireMainSensor.getFloor()==null&& StringUtils.isNotBlank(floorId)){
            try {
                deviceFireMainSensor.setFloor(Integer.parseInt(floorId));
            }catch (Exception e){
                return new TableResultResponse<Map>();
            }
        }
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "15";
        }
        //选择地区时传地区编码
        if(ids==null&&code!=null){
            if(code.length()!=6){
                throw new RuntimeException("错误的地区编码");
            }
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
            List<Integer> lists = dbBiz.selectByZxqzResultIds(code);
            ids = SplitUtil.merge(lists);
        }
        if(StringUtils.isBlank(ids)){
            return new TableResultResponse<Map>();
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return baseBiz.selectQuery(query,ids,deviceFireMainSensor);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "id",paramType = "query"),
            @ApiImplicitParam(name = "buildingId",value = "建筑id",paramType = "query")
    })
    @RequestMapping(value = "/selectType",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("添加编辑前的查询")
    public ObjectRestResponse  selectByType(Long id,@RequestParam Integer buildingId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map result = new HashMap();
        if(id!=null){
            DeviceFireMainSensor deviceFireMainSensor = baseBiz.selectById(id);
            result.put("deviceFireMainSensor",deviceFireMainSensor);
        }
        //获取楼层的下拉框
        DeviceBuilding deviceBuilding = dbBiz.selectById(buildingId);
        if(deviceBuilding!=null){
            int max = deviceBuilding.getUpFloor();
            int min = deviceBuilding.getUnderFloor();
            List<Integer> count = new ArrayList<>();
            for(int i=max;i>=-min;i--){
                if(i!=0){
                    count.add(i);
                }
            }
            result.put("floor",count);
        }
        responseResult.setData(result);
        return responseResult;
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象,传感器实时数据")
    public ObjectRestResponse getSensor(@RequestParam String id){
        ObjectRestResponse restResponse = new ObjectRestResponse<>();
        DeviceFireMainSensor deviceFireMainSensor = baseBiz.selectById(Long.parseLong(id));
        DeviceBuilding deviceBuilding = dbBiz.selectById(deviceFireMainSensor.getBuildingId());
        Map<String,Object> map = new HashMap<>();
        map.put("series",deviceFireMainSensor.getSeries());
        map.put("status",deviceFireMainSensor.getStatus());
        map.put("fireMianId",deviceFireMainSensor.getFireMainId());
        map.put("sensorLoop",deviceFireMainSensor.getSensorLoop());
        map.put("address",deviceFireMainSensor.getAddress());
        map.put("bName",deviceBuilding.getBName());
        map.put("floor",deviceFireMainSensor.getFloor());
        map.put("description",deviceFireMainSensor.getPositionDescription());
        return restResponse.data(map);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加传感器")
    public ObjectRestResponse<DeviceFireMainSensor> add(@RequestBody DeviceFireMainSensor entity){
        ObjectRestResponse<DeviceFireMainSensor> responseResult =  new ObjectRestResponse<DeviceFireMainSensor>();
        TrimUtil.trimObject(entity);
        Integer fireMainId =  entity.getFireMainId();
        DeviceFireMain deviceFireMain = dfmbiz.selectById(fireMainId);
        if(deviceFireMain==null){
            throw new RuntimeException("消防主机不存在");
        }
        String sensorLoop = entity.getSensorLoop();
        String address = entity.getAddress();
        if(ValidatorUtils.hasAnyBlank(sensorLoop,address,entity.getSeries(),entity.getFloor())){
            throw new RuntimeException("缺少参数");
        }
        //判重标准：主机+ip相同，回路+地址不一样
        if(baseBiz.selectByCount(fireMainId,sensorLoop,address)>0){
            throw new RuntimeException("传感器已存在，不可重复添加");
        }
        entity.setId(null);
        //默认未启用
        entity.setStatus("3");
        entity.setStatusTime(new Date());
        // TODO: 2018/11/16  等前端传channelId后去掉
        if(entity.getChannelId()==null){
//            List<Map<String,Object>> channelList = iUserFeign.getAll();
//            for(int i=0;i<channelList.size();i++){
//                if("11".equals(channelList.get(i).get("id"))){
//                    entity.setChannelId((Integer) channelList.get(i).get("id"));
//                }
//            }
            entity.setChannelId(11);
        }
        baseBiz.insertSelective(entity);
        return responseResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加传感器")
    public ObjectRestResponse<DeviceFireMainSensor> update(@RequestBody DeviceFireMainSensor entity){
        ObjectRestResponse<DeviceFireMainSensor> responseResult =  new ObjectRestResponse<DeviceFireMainSensor>();
        TrimUtil.trimObject(entity);
        Integer fireMainId =  entity.getFireMainId();
        DeviceFireMain deviceFireMain = dfmbiz.selectById(fireMainId);
        if(deviceFireMain==null){
            throw new RuntimeException("消防主机不存在");
        }
        DeviceFireMainSensor deviceFireMainSensor = baseBiz.selectById(entity.getId());
        if(deviceFireMainSensor==null){
            throw new RuntimeException("传感器不存在");
        }

        String sensorLoop = entity.getSensorLoop();
        String address = entity.getAddress();
        if(ValidatorUtils.hasAnyBlank(sensorLoop,address,entity.getSeries(),entity.getFloor())){
            throw new RuntimeException("缺少参数");
        }
        //判重标准：主机+ip相同，回路+地址不一样
        if(!(deviceFireMainSensor.getSensorLoop().equalsIgnoreCase(sensorLoop)&&deviceFireMainSensor.getAddress().equalsIgnoreCase(address))&&baseBiz.selectByCount(fireMainId,sensorLoop,address)>0){
            throw new RuntimeException("传感器已存在");
        }
        baseBiz.updateSelectiveById(entity);
        return responseResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除传感器")
    public ObjectRestResponse<DeviceFireMainSensor> delete(@RequestParam String ids){
        ObjectRestResponse<DeviceFireMainSensor> responseResult =  new ObjectRestResponse<DeviceFireMainSensor>();
        if(StringUtils.isBlank(ids)){
            return responseResult;
        }
        Long[] idDel = SplitUtil.splitLong(ids);
        DeviceFireMainSensor deviceFireMainSensor = null;
        //批量假删除
        for(int i=0;i<idDel.length;i++){
            //删除消防主机下面的传感器
            deviceFireMainSensor = new DeviceFireMainSensor();
            deviceFireMainSensor.setId(idDel[i]);
            deviceFireMainSensor.setDelFlag("1");
            dfmsBiz.updateSelectiveById(deviceFireMainSensor);
        }
        return responseResult;
    }

    @IgnoreUserToken
    @IgnoreClientToken
    @ResponseBody
    @ApiOperation("根据ip+端口+回路+地址查询传感器的信息")
    @RequestMapping(value = "/selectIgnoreTenantIpAndPortAndSensor",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverIp",value = "服务器ip",paramType = "query"),
            @ApiImplicitParam(name = "port",value = "端口",paramType = "query"),
            @ApiImplicitParam(name = "sensorLoop",value = "回路",paramType = "query"),
            @ApiImplicitParam(name = "address",value = "地址",paramType = "query"),
    })
    public ObjectRestResponse selectIgnoreTenantIpAndPortAndSensor(@RequestParam String serverIp,@RequestParam String port,@RequestParam String sensorLoop,@RequestParam String address){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(ValidatorUtils.hasAnyBlank(serverIp,port,sensorLoop,address)){
            throw new RuntimeException("参数错误");
        }
        List<DeviceFireMainSensor> lists = baseBiz.selectIgnoreTenantIpAndPortAndSensor(serverIp,port,sensorLoop,address);
        if(lists==null||lists.size()==0){
            throw new RuntimeException("消防主机下传感器不存在："+serverIp+"  "+port+" "+sensorLoop+" "+" "+address);
        }else {
            responseResult.setData(lists.get(0));
        }
        return responseResult;
    }


    @IgnoreUserToken
    @IgnoreClientToken
    @ResponseBody
    @ApiOperation("根据ip+端口修改所有传感器的状态")
    @RequestMapping(value = "/updateSensorStatus",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverIp",value = "服务器ip",paramType = "query"),
            @ApiImplicitParam(name = "port",value = "端口",paramType = "query"),
            @ApiImplicitParam(name = "sensorLoop",value = "回路",paramType = "query"),
            @ApiImplicitParam(name = "address",value = "地址",paramType = "query"),
            @ApiImplicitParam(name = "status",value = "状态",paramType = "query"),
            @ApiImplicitParam(name = "statusTime",value = "时间",paramType = "query")
    })
    public ObjectRestResponse updateSensorStatus(@RequestParam String serverIp,@RequestParam String port,@RequestParam String status,@RequestParam String statusTime,String sensorLoop,String address){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(ValidatorUtils.hasAnyBlank(serverIp,port,statusTime,status)){
            throw new RuntimeException("参数错误");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DeviceFireMainSensor> lists = baseBiz.selectIgnoreTenantIpAndPortAndSensor(serverIp,port,sensorLoop,address);
        DeviceFireMainSensor deviceFireMainSensor = null;
        if(lists == null ||lists.size()==0){
            throw new RuntimeException("没有找到传感器:"+serverIp+" "+port+" "+sensorLoop+" "+address);
        }
        for(int i=0;i<lists.size();i++){
            deviceFireMainSensor = lists.get(i);
            if(deviceFireMainSensor!=null){
                try {
                    if(deviceFireMainSensor.getStatusTime()==null || deviceFireMainSensor.getStatusTime().getTime()<simpleDateFormat.parse(statusTime).getTime()){
                        deviceFireMainSensor.setStatusTime(simpleDateFormat.parse(statusTime));
                        deviceFireMainSensor.setStatus(status);
                        baseBiz.updateSelectiveById(deviceFireMainSensor);//更新状态
                    }else {
                        throw new RuntimeException("传感器已经是最新状态!");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return responseResult;
    }

  /*  @IgnoreUserToken
    @IgnoreClientToken
    @RequestMapping(value = "/updateStatus",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("更新传感器状态")
    public ObjectRestResponse  updateStatus(@RequestParam Long id,@RequestParam String statusTime,@RequestParam String status) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        if(ValidatorUtils.hasAnyBlank(id,statusTime,status)){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DeviceFireMainSensor deviceFireMainSensor = dfmsBiz.selectById(id);
        if(deviceFireMainSensor!=null){
            try {
                if(deviceFireMainSensor.getStatusTime()==null || deviceFireMainSensor.getStatusTime().getTime()<simpleDateFormat.parse(statusTime).getTime()){
                    deviceFireMainSensor.setStatusTime(simpleDateFormat.parse(statusTime));
                    deviceFireMainSensor.setStatus(status);
                    dfmsBiz.updateSelectiveById(deviceFireMainSensor);//更新状态
                }else {
                    throw new RuntimeException("传感器已经是最新状态!");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return responseResult;
    }
*/
    @RequestMapping(value = "/getStatusByBuild",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询建筑物传感器状态和位置")
    public ObjectRestResponse getStatusByBuild(String bName,Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //查询建筑物
        List<DeviceBuilding> buildList = dbBiz.selectByBnameLike(bName,null);
        List resultlist = new ArrayList();
        for(DeviceBuilding deviceBuilding:buildList){
            Map<String ,Object> map = new HashMap<>();
            map.put("id",deviceBuilding.getId());
            map.put("name",deviceBuilding.getBName());
            if(StringUtils.isNotBlank(deviceBuilding.getGis())){
                String [] gis =deviceBuilding.getGis().split(",");
                map.put("gisx",gis[0]);
                map.put("gisy",gis[1]);
            }
            String status = dfmsBiz.getBuildingStatus(deviceBuilding.getBName(),channelId);
            map.put("status",status);
            resultlist.add(map);
        }
        responseResult.setData(resultlist);
        return responseResult;
    }

    @RequestMapping(value = "/getAllStatusCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询所有传感器状态的数量")
    @ApiImplicitParam(name = "channelId",value = "所属系统id",paramType = "query")
    public ObjectRestResponse getAllStatusCount(Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Integer faultCount = dfmsBiz.selectByChannelIdAndStatusAndBuilding(channelId,"0",null,null); //故障
        Integer faultTempCount =dfmsBiz.selectByChannelIdAndStatusAndBuilding(channelId,"4",null,null); //离线,算故障
        Integer callCount = dfmsBiz.selectByChannelIdAndStatusAndBuilding(channelId,"1",null,null); //报警
        Integer normalCount =dfmsBiz.selectByChannelIdAndStatusAndBuilding(channelId,"2",null,null); //正常
        Map map = new HashMap();//返回map
        map.put("faultCount",faultCount+faultTempCount);
        map.put("callCount",callCount);
        map.put("normalCount",normalCount);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getSensorAndFloor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑id和楼层查询传感器位置，状态和平面图")
    public ObjectRestResponse getSensorAndFloor(Integer buildId,Integer floor,Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List sensorList = new ArrayList();
        Map map = new HashMap();
        //传感器列表
        List<DeviceFireMainSensor> sensorlist =dfmsBiz.getSensorStatusByBuildAndFloor(buildId,floor,channelId);
        //平面图
        List<DeviceFloorLayout> floorLayoutlist = dflBiz.selectFloorLayout(buildId,floor);
        if(floorLayoutlist.size()==1){
            DeviceFloorLayout deviceFloorLayout = floorLayoutlist.get(0);
            map.put("url",deviceFloorLayout.getFilePath());
        }else {
            map.put("url","http://file.tmc.turing.ac.cn/not_img.png");
        }
        for(DeviceFireMainSensor deviceFireMainSensor:sensorlist){
            if(!deviceFireMainSensor.getStatus().equals("3")){
                Map sensormap = new HashMap();
                sensormap.put("id",deviceFireMainSensor.getId());
                if(StringUtils.isNotBlank(deviceFireMainSensor.getPositionSign())){
                    String[] positionSign =deviceFireMainSensor.getPositionSign().split(",");
                    sensormap.put("positionSignX",positionSign[0]);
                    sensormap.put("positionSignY",positionSign[1]);
                }
                sensormap.put("status",deviceFireMainSensor.getStatus());
                sensorList.add(sensormap);
            }
        }
        map.put("sensor",sensorList);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/selectStatusCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询建筑物对应楼层传感器状态数量")
    public ObjectRestResponse<DeviceSensor> selectStatusCount(Integer buildId,Integer floor,Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        Integer faultCount = dfmsBiz.selectStatusCount(buildId,floor,channelId,"0");//故障
        Integer faultTempCount = dfmsBiz.selectStatusCount(buildId,floor,channelId,"4");//离线，当故障
        Integer callCount = dfmsBiz.selectStatusCount(buildId,floor,channelId,"1");//报警
        Integer normalCount = dfmsBiz.selectStatusCount(buildId,floor,channelId,"2");//正常
        Map<String,Object> map = new HashMap<>();
        map.put("faultCount",faultCount + faultTempCount);
        map.put("callCount",callCount);
        map.put("normalCount",normalCount);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/selectCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器类型查询数量")
    public ObjectRestResponse<DeviceFireMainSensor> selectCount(Integer buildId,Integer floor,Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        //根据所属系统id查询传感器
        //List<DeviceFireMainSensor> sensorList = dfmsBiz.getSensorStatusByBuildAndFloor(buildId,floor,channelId);
        List<String> list = dfmsBiz.getSeriesByBuildAndFloor(buildId,floor,channelId);
        List resultlist = new ArrayList();
        for (String series:list){
            Map<String,Object> map = new HashMap();
            map.put("type",series);
            Integer count =dfmsBiz.selectCountByType(series,buildId,floor,channelId);
            map.put("count", count);
            resultlist.add(map);
        }
        responseResult.setData(resultlist);
        return responseResult;
    }


    @RequestMapping(value = "/sensorImport",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("导入消防主机")
    public ObjectRestResponse sensorImport(@RequestParam(value = "file", required = false) MultipartFile file ,@RequestParam Integer buildId,@RequestParam Integer fireMianId ) {
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if (file == null) {
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else if(file.getSize()>10*1024*1024){
            throw new RuntimeException("文件大小不能超过10M!");
        }else{
            String fileName = file.getOriginalFilename();
            InputStream inputStream= null;
            List<String[]> excel  = null;
            POIUtil poiUtil=new POIUtil();
            try {
                inputStream = file.getInputStream();
                excel = poiUtil.readExcel(fileName,inputStream);
                inputStream.close();
            } catch (Exception e) {
                throw new RuntimeException("导入出错!");
            }
            //模板的字段
            boolean sensorLoopFlag=false;
            boolean addressFlag=false;
            boolean seriesFlag=false;
            boolean floorFlag=false;
            boolean positionDescriptionFlag=false;
            //模板字段的位置标记
            int sensorLoopCount=0;
            int addressCount=0;
            int seriesCount=0;
            int floorCount=0;
            int positionDescriptionCount=0;
            if(excel.size()>1){
                String[] s =  excel.get(0);
                for(int i=0;i<s.length;i++){
                    if("回路".equals(s[i])||"回路(必填)".equals(s[i])||"回路（必填）".equals(s[i])){
                        sensorLoopFlag=true;
                        sensorLoopCount=i;
                    }
                    if("地址".equals(s[i])||"地址(必填)".equals(s[i])||"地址（必填）".equals(s[i])){
                        addressFlag=true;
                        addressCount=i;
                    }
                    if("系列".equals(s[i])||"系列(必填)".equals(s[i])||"系列（必填）".equals(s[i])){
                        seriesFlag=true;
                        seriesCount=i;
                    }
                    if("楼层".equals(s[i])||"楼层(必填)".equals(s[i])||"楼层（必填）".equals(s[i])){
                        floorFlag=true;
                        floorCount=i;
                    }
                    if("位置描述".equals(s[i])||"位置描述(必填)".equals(s[i])||"位置描述（必填）".equals(s[i])){
                        positionDescriptionFlag=true;
                        positionDescriptionCount=i;
                    }
                }
                //判断Excel表模板是否正确
                if(sensorLoopFlag&&addressFlag&&seriesFlag&&floorFlag&&positionDescriptionFlag){
                    //判断Excel里面回路+地址是否重复
                    Set<String> loopAndAddress = new HashSet<>();
                    //判断数据库里面回路+地址是否重复
                    List<DeviceFireMainSensor> deviceFireMainSensors = baseBiz.selectByFireMainId(fireMianId);
                    DeviceFireMainSensor deviceFireMainSensor = null;
                    Set<String> loopAndAddressBase = new HashSet<>();
                    for(int i=0;i<deviceFireMainSensors.size();i++){
                        deviceFireMainSensor = deviceFireMainSensors.get(i);
                        if(deviceFireMainSensor!=null){
                            loopAndAddressBase.add(deviceFireMainSensor.getSensorLoop()+","+deviceFireMainSensor.getAddress());
                        }
                    }
                    //获取楼层判断楼层是否为非法楼层
                    DeviceBuilding buildings=dbBiz.selectById(buildId);
                    //地下层数
                    int minFloor = buildings.getUnderFloor();
                    //地上层数
                    int maxFloor = buildings.getUpFloor();
                    //数据检验
                    for (int i=1;i<excel.size();i++){
                        String[] exs = new String[s.length];
                        String[] str = excel.get(i);
                        if(str.length<s.length) {
                            System.arraycopy(str,0,exs,0,str.length);
                        }else {
                            exs = str;
                        }
                        for(int j=1;j<s .length;j++){
                            if(StringUtils.isNotBlank(exs[j])){
                                exs[j] = exs[j].trim();
                            }
                            if(j==sensorLoopCount){
                                //判断回路是否为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的回路为空，无法导入");
                                }
                                if(exs[j].length()>10){
                                    throw new RuntimeException("第"+exs[0]+"行的回路长度超过10个字符");
                                }
                            }
                            else if(j==addressCount){
                                //判断地址是否为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的地址为空，无法导入");
                                }
                                if(exs[j].length()>10){
                                    throw new RuntimeException("第"+exs[0]+"行的地址长度超过10个字符");
                                }
                                //判断回路+地址组合是否在Excel中重复
                                if(!loopAndAddress.add(exs[sensorLoopCount]+","+exs[addressCount])){
                                    throw new RuntimeException("文件中回路地址"+exs[sensorLoopCount]+exs[addressCount]+"存在重复");
                                }
                                //判断回路+地址组合在数据库中是否存在
                                if(!loopAndAddressBase.add(exs[sensorLoopCount]+","+exs[addressCount])){
                                    throw new RuntimeException("第"+exs[0]+"行的回路地址"+exs[sensorLoopCount]+exs[addressCount]+"已存在");
                                }
                            }
                            else if(j==seriesCount){
                                //判断系列是否为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的系列为空，无法导入");
                                }
                                if(exs[j].length()>50){
                                    throw new RuntimeException("第"+exs[0]+"行的系列字符长度超过50");
                                }
                            }
                            else if(j==floorCount){
                                //判断楼层是否为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的楼层为空，无法导入");
                                }
                                //判断楼层是否合法
                                try {
                                    if(!(Integer.parseInt(exs[j])>=-minFloor&&Integer.parseInt(exs[j])<=maxFloor&&Integer.parseInt(exs[j])!=0)){
                                        throw new RuntimeException("第"+exs[0]+"行的楼层数在建筑中不存在");
                                    }
                                }catch (Exception e){
                                    throw new RuntimeException("第"+exs[0]+"行的楼层数在建筑中不存在");
                                }
                            }
                            else if(j==positionDescriptionCount){
                                //判断位置描述是否为空  excel最后一行数据为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的位置描述为空，无法导入");
                                }
                                if(exs[j].length()>50){
                                    throw new RuntimeException("第"+exs[0]+"行的位置描述字符长度超过50个字符");
                                }
                            }
                        }
                    }
                    //去除第一条数据
                    // TODO: 2018/11/16  等前端传channelId后去掉
                    Integer channelId = 11;
//                    if(true){
//                        List<Map<String,Object>> channelList = iUserFeign.getAll();
//                        for(int i=0;i<channelList.size();i++){
//                            if("11".equals(channelList.get(i).get("id"))){
//                                channelId = (Integer) channelList.get(i).get("id");
//                            }
//                        }
//                    }
                    excel.remove(0);
                    int insertCount = 0;
                    DeviceFireMainSensor fireMainSensor = null;
                    for(int i=0;i<excel.size();i++){
                        String [] temp = excel.get(i);
                        try {
                            fireMainSensor = new DeviceFireMainSensor();
                            fireMainSensor.setFireMainId(fireMianId);
                            fireMainSensor.setBuildingId(buildId);
                            fireMainSensor.setSensorLoop(temp[sensorLoopCount]);
                            fireMainSensor.setAddress(temp[addressCount]);
                            fireMainSensor.setSeries(temp[seriesCount]);
                            fireMainSensor.setFloor(Integer.parseInt(temp[floorCount]));
                            //默认未启用
                            fireMainSensor.setStatus("3");
                            fireMainSensor.setChannelId(channelId);
                            fireMainSensor.setStatusTime(new Date());
                            fireMainSensor.setPositionSign("");
                            //非必填
                            if(temp.length-1>=positionDescriptionCount)
                            fireMainSensor.setPositionDescription(temp[positionDescriptionCount]);
                        }catch (Exception e){
                            log.info("");
                        }
                        try {
                            //去掉字符串前后端的空格
                            TrimUtil.trimObject(fireMainSensor);
                            baseBiz.insertSelective(fireMainSensor);
                            insertCount = insertCount+1;
                        }catch (Exception e){
                            throw new RuntimeException("插入数据异常！");
                        }
                    }
                    responseResult.setData(insertCount);
                    return responseResult;
                }else{
                    throw new RuntimeException("Excel模板错误!");
                }
            }else{
                throw new RuntimeException("文件内容为空!");
            }
        }
    }

    @RequestMapping(value = "/getStatusCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询建筑物和楼层传感器未启用,未标记数量")
    public ObjectRestResponse<DeviceSensor> getStatusCount(Integer buildId,Integer floor,Integer channelId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        Integer notEnabledCount = dfmsBiz.selectStatusCount(buildId,floor,channelId,"3");//未启用
        Integer unlabeledCount = dfmsBiz.selectNotsignCount(buildId,floor,channelId,null);//未标记
        Integer allNotEnabledCount = dfmsBiz.selectStatusCount(buildId,null,channelId,"3");//未启用
        Map<String,Object> map = new HashMap<>();
        map.put("notEnabledCount",notEnabledCount);
        map.put("unlabeledCount",unlabeledCount);
        map.put("allNotEnabledCount",allNotEnabledCount);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getNotEnabledSensorList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑id，查询未启用传感器列表")
    public TableResultResponse getNotEnabledSensorList(Integer channelId,@RequestParam Integer buildingId,Integer floor,String page, String limit) {
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
        return dfmsBiz.getNotEnabledSensorList(query,channelId,buildingId,floor);
    }

    @RequestMapping(value = "/selectByFloorGetSensor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("设备列表，根据建筑id，楼层id获得传感器")
    public TableResultResponse selectByFloorGetSensor(Integer channelId,@RequestParam Integer buildingId,@RequestParam Integer floor, String page, String limit,String status) {
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
        return dfmsBiz.selectByFloorGetSensor(query,channelId,buildingId,floor,status);
    }

    @RequestMapping(value = "/getAllBuildingList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("列表模式，查询所有的楼层信息，传感器的信息")
    public TableResultResponse getAllBuildingList(Integer channelId, String page, String limit) {
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
        return dfmsBiz.getAllBuildingList(query,channelId);
    }

    @RequestMapping(value = "/getSensor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据传感器id查询")
    public ObjectRestResponse getSensor(@RequestParam Long sensorId){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map map = new HashMap();
        if(sensorId==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }else{
            DeviceFireMainSensor deviceFireMainSensor = dfmsBiz.selectById(sensorId);
            if("4".equals(deviceFireMainSensor.getStatus())){
                map.put("time", DateUtil.getHandletime(new Date(),deviceFireMainSensor.getStatusTime()));
                responseResult.setData(map);
                return responseResult;
            }
            map.put("series",deviceFireMainSensor.getSeries());
            map.put("sensorLoop",deviceFireMainSensor.getSensorLoop());
            map.put("address",deviceFireMainSensor.getAddress());
            map.put("fireMain",deviceFireMainSensor.getFireMainId());
            map.put("date",deviceFireMainSensor.getStatusTime());
            map.put("positionDescription",deviceFireMainSensor.getPositionDescription());
            map.put("status",deviceFireMainSensor.getStatus());
            if("0".equals(deviceFireMainSensor.getStatus())){
                map.put("type","故障");
                map.put("color","#F5A623");
            }
            if("1".equals(deviceFireMainSensor.getStatus())){
                map.put("type","报警");
                map.put("color","#FF001F");
            }
            if("2".equals(deviceFireMainSensor.getStatus())){
                map.put("type","正常");
                map.put("color","#4F9600");
            }
            responseResult.setData(map);
        }
        return responseResult;
    }

    @RequestMapping(value = "/selectListByBuildId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据建筑id分页查询传感器报警信息列表")
    public TableResultResponse  selectListByBuildId(Integer buildId,String status,String page,String limit,Integer channelId){
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
        return dfmsBiz.selectListByBuildId(query,buildId,status,channelId);
    }

    @RequestMapping(value = "/getSensorById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP根据传感器id查询设备详细信息")
    public ObjectRestResponse  getSelectById(@RequestParam long id){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        Map map = new HashMap();
        //传感器
        DeviceFireMainSensor deviceFireMainSensor = dfmsBiz.selectById(id);
        if(deviceFireMainSensor==null){
            throw new RuntimeException("传感器不存在!");
        }
        //建筑物
        DeviceBuilding deviceBuilding = dbBiz.selectById(deviceFireMainSensor.getBuildingId());
        if(deviceBuilding==null){
            throw new RuntimeException("建筑物不存在!");
        }
        //消防主机
        DeviceFireMain deviceFireMain = dfmbiz.selectById(deviceFireMainSensor.getFireMainId());
        if(deviceFireMain==null){
            throw new RuntimeException("消防主机不存在!");
        }
        map.put("id",deviceFireMainSensor.getId());//设备id
        map.put("fireMainPositionDescription",deviceFireMain.getPositionDescription());//消防主机位置描述
        map.put("type",deviceFireMainSensor.getSeries());//设备类型
        map.put("buildingId",deviceBuilding.getId());//建筑id
        map.put("channelId",deviceFireMainSensor.getChannelId());//所属系统id
        map.put("sensorLoop",deviceFireMainSensor.getSensorLoop());
        map.put("address",deviceFireMainSensor.getAddress());
        map.put("bName",deviceBuilding.getBName());
        map.put("floor",deviceFireMainSensor.getFloor());
        map.put("positionDescription",deviceFireMainSensor.getPositionDescription());
        map.put("status",deviceFireMainSensor.getStatus());
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getAlrmFloor",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑id获得报警楼层")
    public ObjectRestResponse getAlrmFloor(@RequestParam Integer buildId,Integer channelId) {
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        List<Map> result = new ArrayList<>();
        List<DeviceFireMainSensor> list = dfmsBiz.selectByBuildingId(buildId,channelId);
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
                DeviceFireMainSensor deviceFireMainSensor=list.get(j);
                //判断当前楼层报一个火警就是报警，没有报警，有故障就是故障，都没有是正常
                if(floorList.get(i)==deviceFireMainSensor.getFloor()){
                    if(deviceFireMainSensor.getStatus().equals("1")){//火警
                        status ="1";
                        map.put("status",status);
                        break;
                    }else if(deviceFireMainSensor.getStatus().equals("0")||deviceFireMainSensor.getStatus().equals("4")) {
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

    @RequestMapping(value = "/listFireMainSensorByIds",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("查询消防主机传感器信息")
    public TableResultResponse  listFireMainSensorByIds(@RequestParam(defaultValue = "1") Integer pageNo,@RequestParam(defaultValue = "15") Integer limit,@RequestParam String queryStr){
        if(pageNo == null || pageNo.intValue() <= 0){
            pageNo = 1;
        }
        if(limit == null || limit.intValue() <= 0){
            limit = 15;
        }

        JSONObject json = JSONObject.parseObject(queryStr);
        String ids = json.getString("ids");
        String serverIp = json.getString("serverIp");
        String port = json.getString("port");
        String sensorLoop = json.getString("sensorLoop");
        String address = json.getString("address");
        Integer buildingId = json.getInteger("buildingId");
        String exIds = json.getString("excludeIds");
        String series = json.getString("series");
        String code = json .getString("code");
       return this.dfmsBiz.listFireMainSensorByIds(pageNo,limit,ids,serverIp, port, sensorLoop, address,buildingId,exIds,series,code);
    }
}