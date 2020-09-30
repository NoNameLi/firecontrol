package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.common.util.UploadUtil;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.core.exception.BaseException;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("deviceIndoorLabel")
@CheckClientToken
@CheckUserToken
public class DeviceIndoorLabelController extends BaseController<DeviceIndoorLabelBiz,DeviceIndoorLabel,Integer> {

    @Autowired
    protected DeviceIndoorLabelBiz dilBiz;
    @Autowired
    protected DeviceBuildingBiz dbBiz;
    @Autowired
    protected DeviceFacilitiesTypeBiz dftBiz;
    @Autowired
    protected DeviceOutdoorLabelBiz dolBiz;
    @Autowired
    private DeviceCheckTestItemBiz dctiBiz;
    @Autowired
    private DeviceRouteLabelBiz drlBiz;
    @Autowired
    private DeviceInspectionRouteBiz dirBiz;
    @Autowired
    private DeviceIndoorRecordInspectionResultsBiz dirirBiz;
    @Autowired
    private DeviceOutdoorRecordInspectionResultsBiz dorirBiz;
    @Autowired
    private DeviceInspectionTasksBiz ditBiz;

    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "ids",value = "建筑ids",paramType = "query"),
            @ApiImplicitParam(name = "code",value = "地区编码",paramType = "query"),
            @ApiImplicitParam(name = "equipmentType",value = "设施类型",paramType = "query"),
            @ApiImplicitParam(name = "floorId",value = "楼层",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "最近巡检日期",paramType = "query")
    })
    public TableResultResponse<DeviceIndoorLabel> pageList(String page, String limit, String ids, String code, DeviceIndoorLabel deviceIndoorLabel,String equipmentType, String floorId, String dateStrs) {
        //当楼层乱输入时直接返回空，查不到
        if(deviceIndoorLabel.getFloor()==null&& StringUtils.isNotBlank(floorId)){
            try {
                deviceIndoorLabel.setFloor(Integer.parseInt(floorId));
            }catch (Exception e){
                return new TableResultResponse<DeviceIndoorLabel>();
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
            return new TableResultResponse<DeviceIndoorLabel>();
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dilBiz.selectQuery(query,ids,deviceIndoorLabel,equipmentType,dateStrs);
    }

    @RequestMapping(value = "add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加室内标签")
    public ObjectRestResponse<DeviceIndoorLabel> add(@RequestBody Map<String,Object> params){
        ObjectRestResponse restResponse = new ObjectRestResponse();
        if(params!=null){
            //自动生成设施编号
            String facilitiesNo = UUIDUtils.getNumberByUUId();
            DeviceIndoorLabel deviceIndoorLabel = new DeviceIndoorLabel();
            try {
                deviceIndoorLabel.setFacilitiesTypeId(Integer.valueOf(params.get("facilitiesTypeId").toString()));
                deviceIndoorLabel.setBuildingId(Integer.valueOf(params.get("buildingId").toString()));
                deviceIndoorLabel.setFloor(Integer.valueOf(params.get("floor").toString()));
                deviceIndoorLabel.setPositionDescription((String) params.get("positionDescription"));
            }catch (Exception e){
                throw new BaseException("参数错误");
            }
            TrimUtil.trimObject(deviceIndoorLabel);
            deviceIndoorLabel.setFacilitiesNo(facilitiesNo);
            deviceIndoorLabel.setStatus("0");
//            deviceIndoorLabel.setLastInspectionTime(new Date());
            //默认不生成路线
            deviceIndoorLabel.setUseFlag("0");
            //默认未检测
            deviceIndoorLabel.setResultFlag("0");
            //根据设施编号生成二维码并上传
//            String path = QrCodeUtil.createQrCodeImage(facilitiesNo,430);
//            deviceIndoorLabel.setQrCodePath(path);
            dilBiz.insertSelective(deviceIndoorLabel);
            restResponse.setData(deviceIndoorLabel.getId()+","+facilitiesNo);
        }else{
            throw new BaseException("参数错误!");
        }
        return restResponse;
    }

    @RequestMapping(value = "/batchDelete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("批量假删除")
    @ApiImplicitParam(name = "id",value = "室内标签id",paramType = "query")
    public ObjectRestResponse<DeviceIndoorLabel> batchDelete(@RequestParam String id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        dilBiz.updateBatch(id);
        if(StringUtils.isNotBlank(id)){
            Integer [] idsInt = SplitUtil.splitInt(id);
            //默认室内
            String labelFlag = "0";
            //默认巡检
            String routeFlag = "0";
            DeviceRouteLabel temp = null;
            for(int i=0;i<idsInt.length;i++){
                Integer routeId = drlBiz.selectByLabelIdResultRouteId(labelFlag,routeFlag,idsInt[i]);
                if(routeId!=null){
                    //删除路线绑定的标
                    temp = new DeviceRouteLabel();
                    temp.setLabelFlag(labelFlag);
                    temp.setLabelId(idsInt[i]);
                    temp.setTenantId(BaseContextHandler.getTenantID());
                    drlBiz.delete(temp);
                    //修改巡检路线的个数
                    DeviceInspectionRoute deviceInspectionRoute = new DeviceInspectionRoute();
                    deviceInspectionRoute.setId(routeId);
                    deviceInspectionRoute.setLabelCount(drlBiz.selectByRouteId(routeId,routeFlag).size()+"");
                    dirBiz.updateSelectiveById(deviceInspectionRoute);
                }
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("编辑室内标签")
    public ObjectRestResponse<DeviceIndoorLabel> update(@RequestBody Map<String,Object> params){
        ObjectRestResponse restResponse = new ObjectRestResponse();
        if(params!=null){
            DeviceIndoorLabel deviceIndoorLabel = dilBiz.selectById(Integer.valueOf(params.get("id").toString()));
            try {
                deviceIndoorLabel.setFacilitiesTypeId(Integer.valueOf(params.get("facilitiesTypeId").toString()));
                deviceIndoorLabel.setFloor(Integer.valueOf(params.get("floor").toString()));
                deviceIndoorLabel.setPositionDescription((String) params.get("positionDescription"));
            }catch (Exception e){
                throw new BaseException("参数错误");
            }
            TrimUtil.trimObject(deviceIndoorLabel);
            dilBiz.updateSelectiveById(deviceIndoorLabel);
        }else{
            throw new BaseException("参数错误!");
        }
        return restResponse;
    }

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("编辑前查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "室内标签id",paramType = "query"),
            @ApiImplicitParam(name = "buildingId",value = "建筑id",paramType = "query")
    })
    public ObjectRestResponse<DeviceIndoorLabel> select(@RequestParam Integer id,@RequestParam Integer buildingId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        Map map = new HashMap();
        if(id ==null || buildingId ==null){
            throw new BaseException("参数错误!");
        }
        DeviceIndoorLabel deviceIndoorLabel  = dilBiz.selectById(id);
        map.put("deviceIndoorLabel",deviceIndoorLabel);
        List<DeviceFacilitiesType> list =dftBiz.selectByType(null);
        map.put("deviceFacilitiesType",list);
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
            map.put("floor",count);
        }
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/getListById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("二维码列表展示")
    @ApiImplicitParam(name = "ids",value = "室内标签ids",paramType = "query")
    public ObjectRestResponse<DeviceIndoorLabel> getListById(@RequestParam String ids){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(ids ==null){
            throw new BaseException("参数错误!");
        }
        String[] idstr =ids.split(",");
        List idlist = Arrays.asList(idstr);
        List<Map> list= dilBiz.getListById(idlist);
        responseResult.setData(list);
        return responseResult;
    }


    @RequestMapping(value = "/sensorImport",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("导入室内标签")
    public ObjectRestResponse sensorImport(@RequestParam(value = "file", required = false) MultipartFile file , @RequestParam Integer buildId) throws FileNotFoundException {
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
            boolean facilitiesTypeFlag=false;
            boolean floorFlag=false;
            boolean positionDescriptionFlag=false;
            //模板字段的位置标记
            int facilitiesTypeFlagCount=0;
            int floorCount=0;
            int positionDescriptionCount=0;
            if(excel.size()>1){
                String[] s =  excel.get(0);
                for(int i=0;i<s.length;i++){
                    if("设施类型编号".equals(s[i])||"设施类型编号(必填)".equals(s[i])||"设施类型编号（必填）".equals(s[i])){
                        facilitiesTypeFlag=true;
                        facilitiesTypeFlagCount=i;
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
                if(facilitiesTypeFlag&&floorFlag&&positionDescriptionFlag){
                    //获取所有的设施类型id
                    Set<Integer> setTypeIds = dftBiz.getAllTypeId();
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
                            if(j==facilitiesTypeFlagCount){
                                //设施类型编号是否为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的设施类型编号为空，无法导入");
                                }
                                try {
                                    if(setTypeIds.add(Integer.parseInt(exs[j]))){
                                        throw new RuntimeException("第"+exs[0]+"行的设施类型编号不存在");
                                    }
                                }catch (Exception e){
                                    throw new RuntimeException("第"+exs[0]+"行的设施类型编号不存在");
                                }
                            } else if(j==floorCount){
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
                            } else if(j==positionDescriptionCount){
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
                    excel.remove(0);
                    int insertCount = 0;
                    DeviceIndoorLabel deviceIndoorLabel = null;
                    String facilitiesNo = null;
                    List<String> nos = new ArrayList<>();
                    List<Integer> ids = new ArrayList<>();
                    for(int i=0;i<excel.size();i++){
                        String [] temp = excel.get(i);
                        try {
                            //自动生成设施编号
                            facilitiesNo = UUIDUtils.getNumberByUUId();
                            deviceIndoorLabel = new DeviceIndoorLabel();
                            deviceIndoorLabel.setFacilitiesTypeId(Integer.parseInt(temp[facilitiesTypeFlagCount]));
                            deviceIndoorLabel.setBuildingId(buildId);
                            deviceIndoorLabel.setFloor(Integer.parseInt(temp[floorCount]));
                            deviceIndoorLabel.setPositionDescription(temp[positionDescriptionCount]);
                            TrimUtil.trimObject(deviceIndoorLabel);
                            deviceIndoorLabel.setFacilitiesNo(facilitiesNo);
                            deviceIndoorLabel.setStatus("0");
                            //默认不生成路线
                            deviceIndoorLabel.setUseFlag("0");
                            //默认未检测
                            deviceIndoorLabel.setResultFlag("0");
//                            deviceIndoorLabel.setLastInspectionTime(new Date());
                            baseBiz.insertSelective(deviceIndoorLabel);
                            nos.add(facilitiesNo);
                            ids.add(deviceIndoorLabel.getId());
                            insertCount = insertCount+1;
                        }catch (Exception e){
                            throw new RuntimeException("插入数据异常！");
                        }
                    }
                    //异步根据设施编号生成二维码并上传
                    /*new Thread(new Runnable() {
                        @Override
                        public void run() {
                           String path = null;
                           DeviceIndoorLabel deviceIndoorLabel = null;
                           for(int i=0;i<nos.size();i++){
                               deviceIndoorLabel = new DeviceIndoorLabel();
                               path = QrCodeUtil.createQrCodeImage(nos.get(i),430);
                               deviceIndoorLabel.setId(ids.get(i));
                               deviceIndoorLabel.setQrCodePath(path);
                               baseBiz.updateQrCodePath(deviceIndoorLabel);
                           }
                        }
                    }).start();*/
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

    @RequestMapping(value = "/getCount",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("APP查询设施的个数")
    public ObjectRestResponse getCount(){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        Map map = new HashMap();
        //查询室内设施标签个数
        Integer indoorLabelCount = dilBiz.getCount(null,null,null);
        //查询室外设施标签个数
        Integer outdoorLabelCount = dolBiz.getCount(null,null,null);
        //设施总数
        map.put("totalCount",indoorLabelCount+outdoorLabelCount);
        //查询需巡检设施的个数
        Integer indoor_needInspectionCount =dilBiz.getNeedInspectionCount(null,"1","0");
        Integer outdoor_needInspectionCount = dolBiz.getNeedInspectionCount(null,"1","0");
        map.put("needInspectionCount",indoor_needInspectionCount+outdoor_needInspectionCount);
        //维护设施个数
        Integer indoor_maintainCount = dilBiz.getCount("1",null,null);
        Integer outdoor_maintainCount = dolBiz.getCount("1",null,null);
        map.put("maintainCount",indoor_maintainCount+outdoor_maintainCount);
        responseResult.setData(map);
        return responseResult;
    }

    @RequestMapping(value = "/selectIndoorLabelList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询室内标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页条数",paramType = "query"),
            @ApiImplicitParam(name = "status",value = "状态[0=正常/1=维修中/2=需巡检]",paramType = "query"),
            @ApiImplicitParam(name = "zxqy",value = "地区编码",paramType = "query")
    })
    public TableResultResponse  selectIndoorLabelList(String page,String limit,String status,String zxqy){
        Map<String ,Object> param = new LinkedHashMap<>();
        if(page==null||"".equals(page)){
            page = "1";
        }
        if(limit==null||"".equals(limit)){
            limit = "10";
        }
        if(StringUtils.isNotBlank(zxqy)&&zxqy.trim().length()==6){
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
        return dilBiz.selectIndoorLabelList(query,status,zxqy);
    }


    @RequestMapping(value = "/pageListRoute",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，显示没有绑定路线的的标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "ids",value = "建筑ids",paramType = "query"),
            @ApiImplicitParam(name = "code",value = "地区编码",paramType = "query"),
            @ApiImplicitParam(name = "equipmentType",value = "设施类型",paramType = "query"),
            @ApiImplicitParam(name = "floorId",value = "楼层",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "最近巡检日期",paramType = "query")
    })
    public TableResultResponse<DeviceIndoorLabel> pageListRoute(String page, String limit, String ids, String code, DeviceIndoorLabel deviceIndoorLabel,String equipmentType, String floorId, String dateStrs) {
        //显示没有绑定路线的标签
        deviceIndoorLabel.setUseFlag("0");
        //当楼层乱输入时直接返回空，查不到
        if(deviceIndoorLabel.getFloor()==null&& StringUtils.isNotBlank(floorId)){
            try {
                deviceIndoorLabel.setFloor(Integer.parseInt(floorId));
            }catch (Exception e){
                return new TableResultResponse<DeviceIndoorLabel>();
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
            return new TableResultResponse<DeviceIndoorLabel>();
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return dilBiz.selectQuery(query,ids,deviceIndoorLabel,equipmentType,dateStrs);
    }

    @RequestMapping(value = "/selectIndoorLabelListByBuildingId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询室内标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页条数",paramType = "query"),
            @ApiImplicitParam(name = "routeId",value = "路线id",paramType = "query"),
            @ApiImplicitParam(name = "buildingId",value = "建筑id",paramType = "query")
    })
    public TableResultResponse  selectIndoorLabelListByBuildingId(String page,String limit,Integer routeId,Integer taskId,Integer buildingId){
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
        return dilBiz.selectIndoorLabelListByBuildingId(query,routeId,taskId,buildingId);
    }

    @RequestMapping(value = "/selectListByBuildingId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询室内标签列表,不分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页条数",paramType = "query"),
            @ApiImplicitParam(name = "routeId",value = "路线id",paramType = "query"),
            @ApiImplicitParam(name = "buildingId",value = "建筑id",paramType = "query")
    })
    public ObjectRestResponse selectListByBuildingId(Integer routeId,Integer buildingId,Integer taskId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List<Map> list = dilBiz.selectListByBuildingId(routeId,buildingId,taskId);
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/getLabelByFacilitiesNo",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据设施编号查询标签")
    public ObjectRestResponse getLabelByFacilitiesNo(@RequestParam Integer routeId,@RequestParam String facilitiesNo,@RequestParam Integer taskId){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        List<Integer> list = drlBiz.selectByRouteId(routeId,"0");
        DeviceIndoorLabel deviceIndoorLabel =dilBiz.getIndoorLabelByFacilitiesNo(facilitiesNo);
        Map map = new HashMap();
        if(deviceIndoorLabel!=null){
            if(!list.contains(deviceIndoorLabel.getId())){
                responseResult.setData("当前设施不属于本次巡检任务");
                return responseResult;
            }
            List<DeviceIndoorRecordInspectionResults> inspectionResults= dirirBiz.selectByTaskId(taskId,deviceIndoorLabel.getId());
            if(inspectionResults.size()>0){
                responseResult.setData("当前设施已巡检");
                return responseResult;
            }
            DeviceFacilitiesType deviceFacilitiesType = dftBiz.selectById(deviceIndoorLabel.getFacilitiesTypeId());
            map.put("facilitiesNo",facilitiesNo);
            map.put("equipmentType",deviceFacilitiesType.getEquipmentType());
            DeviceBuilding deviceBuilding = dbBiz.selectById(deviceIndoorLabel.getBuildingId());
            map.put("buildingId",deviceBuilding.getId());
            map.put("bName",deviceBuilding.getBName());
            map.put("floor",deviceIndoorLabel.getFloor());
            map.put("positionDescription",deviceIndoorLabel.getPositionDescription());
            responseResult.setData(map);
        }else{
            DeviceOutdoorLabel deviceOutdoorLabel =dolBiz.getIndoorLabelByFacilitiesNo(facilitiesNo);
            if(deviceOutdoorLabel==null){
                //responseResult.setData("请扫描正确的二维码");
                responseResult.setData(null);
                return responseResult;
            }
            if(!list.contains(deviceOutdoorLabel.getId())){
                responseResult.setData("当前设施不属于本次巡检任务");
                return responseResult;
            }
            List<DeviceOutdoorRecordInspectionResults> inspectionResults= dorirBiz.selectByTaskId(taskId,deviceOutdoorLabel.getId());
            if(inspectionResults.size()>0){
                responseResult.setData("当前设施已巡检");
                return responseResult;
            }
            DeviceFacilitiesType deviceFacilitiesType = dftBiz.selectById(deviceOutdoorLabel.getFacilitiesTypeId());
            map.put("facilitiesNo",facilitiesNo);
            map.put("equipmentType",deviceFacilitiesType.getEquipmentType());
            map.put("positionDescription",deviceOutdoorLabel.getPositionDescription());
            responseResult.setData(map);
        }
        return responseResult;
    }

    @RequestMapping(value = "/getItemByFacilitiesNo",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据设施编号查询检测项")
    public ObjectRestResponse getItemByFacilitiesNo(@RequestParam String facilitiesNo){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        DeviceIndoorLabel deviceIndoorLabel =dilBiz.getIndoorLabelByFacilitiesNo(facilitiesNo);
        if(deviceIndoorLabel!=null){
            //List<DeviceCheckTestItem> list = dctiBiz.selectByQuery(deviceIndoorLabel.getFacilitiesTypeId(),"0",null);
            List<DeviceCheckTestItem> list = dctiBiz.selectByFacilitiesNo(deviceIndoorLabel.getFacilitiesTypeId(),"0",null);
            responseResult.setData(list);
        }else{
            DeviceOutdoorLabel deviceOutdoorLabel =dolBiz.getIndoorLabelByFacilitiesNo(facilitiesNo);
            //List<DeviceCheckTestItem> list = dctiBiz.selectByQuery(deviceOutdoorLabel.getFacilitiesTypeId(),"0",null);
            List<DeviceCheckTestItem> list = dctiBiz.selectByFacilitiesNo(deviceOutdoorLabel.getFacilitiesTypeId(),"0",null);
            responseResult.setData(list);
        }
        return responseResult;
    }

    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除前判断是否被路线绑定")
    public ObjectRestResponse deleteQuery(@RequestParam String ids){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(StringUtils.isNotBlank(ids)){
            Integer [] idInts = SplitUtil.splitInt(ids);
            for(int i=0;i<idInts.length;i++){
                
            }
        }
        return responseResult;
    }



}