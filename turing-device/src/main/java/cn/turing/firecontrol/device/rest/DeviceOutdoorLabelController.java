package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.core.exception.BaseException;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("deviceOutdoorLabel")
@CheckClientToken
@CheckUserToken
public class DeviceOutdoorLabelController extends BaseController<DeviceOutdoorLabelBiz,DeviceOutdoorLabel,Integer> {

    @Autowired
    protected DeviceOutdoorLabelBiz dolBiz;
    @Autowired
    protected DeviceFacilitiesTypeBiz dftBiz;
    @Autowired
    protected DeviceNetworkingUnitBiz dnuBiz;
    @Autowired
    private DeviceRouteLabelBiz drlBiz;
    @Autowired
    private DeviceInspectionRouteBiz dirBiz;

    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询数据，按条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "equipmentType",value = "设施类型",paramType = "query"),
            @ApiImplicitParam(name = "dateStrs",value = "最近巡检日期",paramType = "query")
    })
    public TableResultResponse<DeviceOutdoorLabel> pageList(String page, String limit, DeviceOutdoorLabel deviceOutdoorLabel, String equipmentType,String dateStrs) {
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
        return dolBiz.selectQuery(query,deviceOutdoorLabel,equipmentType,dateStrs);
    }

    @RequestMapping(value = "add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加室外标签")
    public ObjectRestResponse<DeviceOutdoorLabel> add(@RequestBody Map<String,Object> params){
        ObjectRestResponse restResponse = new ObjectRestResponse();
        if(params!=null){
            //自动生成设施编号
            String facilitiesNo = UUIDUtils.getNumberByUUId();
            DeviceOutdoorLabel deviceOutdoorLabel = new DeviceOutdoorLabel();
            try {
                deviceOutdoorLabel.setFacilitiesTypeId(Integer.valueOf(params.get("facilitiesTypeId").toString()));
                deviceOutdoorLabel.setPositionSign((String) params.get("positionSign"));
                deviceOutdoorLabel.setPositionDescription((String) params.get("positionDescription"));
                deviceOutdoorLabel.setOid(Integer.valueOf(params.get("oid").toString()));
            }catch (Exception e){
                throw new BaseException("参数错误!");
            }
            TrimUtil.trimObject(deviceOutdoorLabel);
            deviceOutdoorLabel.setFacilitiesNo(facilitiesNo);
            deviceOutdoorLabel.setStatus("0");
//            deviceOutdoorLabel.setLastInspectionTime(new Date());
            //默认不生成路线
            deviceOutdoorLabel.setUseFlag("0");
            //默认未检测
            deviceOutdoorLabel.setResultFlag("0");
            //根据设施编号生成二维码并上传
//            String path = QrCodeUtil.createQrCodeImage(facilitiesNo,430);
//            deviceOutdoorLabel.setQrCodePath(path);
            dolBiz.insertSelective(deviceOutdoorLabel);
            restResponse.setData(deviceOutdoorLabel.getId()+","+facilitiesNo);
        }else{
            throw new BaseException("参数错误!");
        }
        return restResponse;
    }

    @RequestMapping(value = "/batchDelete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("批量假删除")
    @ApiImplicitParam(name = "id",value = "室内标签id",paramType = "query")
    public ObjectRestResponse<DeviceOutdoorLabel> batchDelete(@RequestParam String id){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        dolBiz.updateBatch(id);
        if(StringUtils.isNotBlank(id)){
            Integer [] idsInt = SplitUtil.splitInt(id);
            //默认室外
            String labelFlag = "1";
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
    public ObjectRestResponse<DeviceOutdoorLabel> update(@RequestBody Map<String,Object> params){
        ObjectRestResponse restResponse = new ObjectRestResponse();
        if(params!=null){
            DeviceOutdoorLabel deviceOutdoorLabel = dolBiz.selectById(Integer.valueOf(params.get("id").toString()));
            try {
                deviceOutdoorLabel.setFacilitiesTypeId(Integer.valueOf(params.get("facilitiesTypeId").toString()));
                deviceOutdoorLabel.setPositionSign((String) params.get("positionSign"));
                deviceOutdoorLabel.setPositionDescription((String) params.get("positionDescription"));
                deviceOutdoorLabel.setOid(Integer.valueOf(params.get("oid").toString()));
            }catch (Exception e){
                throw new BaseException("参数错误!");
            }
            TrimUtil.trimObject(deviceOutdoorLabel);
            dolBiz.updateSelectiveById(deviceOutdoorLabel);
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
    public ObjectRestResponse<DeviceOutdoorLabel> select(@RequestParam Integer id,@RequestParam Integer oid){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        Map map = new HashMap();
        if(id ==null){
            throw new BaseException("参数错误!");
        }
        List<DeviceFacilitiesType> list =dftBiz.selectByType(null);
        map.put("deviceFacilitiesType",list);
        DeviceOutdoorLabel deviceOutdoorLabel  = dolBiz.selectById(id);
        map.put("deviceOutdoorLabel",deviceOutdoorLabel);
        DeviceNetworkingUnit deviceNetworkingUnit = dnuBiz.selectById(oid);
        map.put("OName",deviceNetworkingUnit.getOName());
        List<DeviceNetworkingUnit> list1 = dnuBiz.getAll(null);
        map.put("DeviceNetworkingUnit",list1);
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
        List<Map> list= dolBiz.getListById(idlist);
        responseResult.setData(list);
        return responseResult;
    }

    @RequestMapping(value = "/sensorImport",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("导入室外标签")
    public ObjectRestResponse sensorImport(@RequestParam(value = "file", required = false) MultipartFile file ) throws FileNotFoundException {
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
            boolean networkUnitFlag=false;
            boolean positionDescriptionFlag=false;
            //模板字段的位置标记
            int facilitiesTypeFlagCount=0;
            int networkUnitCount=0;
            int positionDescriptionCount=0;
            if(excel.size()>1){
                String[] s =  excel.get(0);
                for(int i=0;i<s.length;i++){
                    if("设施类型编号".equals(s[i])||"设施类型编号(必填)".equals(s[i])||"设施类型编号（必填）".equals(s[i])){
                        facilitiesTypeFlag=true;
                        facilitiesTypeFlagCount=i;
                    }
                    if("联网单位".equals(s[i])||"联网单位(必填)".equals(s[i])||"联网单位（必填）".equals(s[i])){
                        networkUnitFlag=true;
                        networkUnitCount=i;
                    }
                    if("位置描述".equals(s[i])||"位置描述(必填)".equals(s[i])||"位置描述（必填）".equals(s[i])){
                        positionDescriptionFlag=true;
                        positionDescriptionCount=i;
                    }
                }
                //判断Excel表模板是否正确
                if(facilitiesTypeFlag&&networkUnitFlag&&positionDescriptionFlag){
                    //获取所有的设施类型id
                    Set<Integer> setTypeIds = dftBiz.getAllTypeId();
                    //获取所有的联网单位
                    List<DeviceNetworkingUnit> units = dnuBiz.getAllUnit(null);
                    Set<String> setAllUnit = new HashSet<>();
                    for(int i=0;i<units.size();i++){
                        setAllUnit.add(units.get(i).getOName());
                    }
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
                            } else if(j==networkUnitCount){
                                //判断联网单位
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的联网单位为空，无法导入");
                                }
                                if(setAllUnit.add(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的联网单位不存在");
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
                    List<String> nos = new ArrayList<>();
                    List<Integer> ids = new ArrayList<>();
                    DeviceOutdoorLabel deviceOutdoorLabel = null;
                    String facilitiesNo = null;
                    for(int i=0;i<excel.size();i++){
                        String [] temp = excel.get(i);
                        try {
                            //自动生成设施编号
                            facilitiesNo = UUIDUtils.getNumberByUUId();
                            deviceOutdoorLabel = new DeviceOutdoorLabel();
                            deviceOutdoorLabel.setFacilitiesTypeId(Integer.parseInt(temp[facilitiesTypeFlagCount]));
                            deviceOutdoorLabel.setPositionDescription(temp[positionDescriptionCount]);
                            deviceOutdoorLabel.setOid(dnuBiz.getAllUnit(temp[networkUnitCount]).get(0).getId());
                            TrimUtil.trimObject(deviceOutdoorLabel);
                            deviceOutdoorLabel.setFacilitiesNo(facilitiesNo);
                            deviceOutdoorLabel.setStatus("0");
                            //默认不生成路线
                            deviceOutdoorLabel.setUseFlag("0");
                            //默认未检测
                            deviceOutdoorLabel.setResultFlag("0");
//                            deviceOutdoorLabel.setLastInspectionTime(new Date());
                            dolBiz.insertSelective(deviceOutdoorLabel);
                            nos.add(facilitiesNo);
                            ids.add(deviceOutdoorLabel.getId());
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
                            DeviceOutdoorLabel deviceOutdoorLabel = null;
                            for(int i=0;i<nos.size();i++){
                                deviceOutdoorLabel = new DeviceOutdoorLabel();
                                path = QrCodeUtil.createQrCodeImage(nos.get(i),430);
                                deviceOutdoorLabel.setId(ids.get(i));
                                deviceOutdoorLabel.setQrCodePath(path);
                                baseBiz.updateQrCodePath(deviceOutdoorLabel);
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

    @RequestMapping(value = "/selectOutdoorLabelList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("分页查询室外标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页条数",paramType = "query"),
            @ApiImplicitParam(name = "status",value = "状态[0=正常/1=维修中/2=需巡检]",paramType = "query")
    })
    public TableResultResponse  selectOutdoorLabelList(String page,String limit,String status){
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
        return dolBiz.selectOutdoorLabelList(query,status);
    }

    @RequestMapping(value = "/selectOutdoorLabelListByRouteId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据路线id分页查询室外标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页条数",paramType = "query"),
            @ApiImplicitParam(name = "routeId",value = "路线id",paramType = "query")
    })
    public TableResultResponse  selectOutdoorLabelListByRouteId(String page,String limit,Integer routeId,Integer taskId){
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
        return dolBiz.selectOutdoorLabelListByRouteId(query,routeId,taskId);
    }

}