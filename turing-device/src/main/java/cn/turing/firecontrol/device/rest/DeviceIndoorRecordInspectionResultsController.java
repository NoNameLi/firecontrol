package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.core.exception.BaseException;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.config.ApplicationStartListener;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.feign.IUserFeign;
import cn.turing.firecontrol.device.util.*;
import cn.turing.firecontrol.device.vo.ItemValueVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("deviceIndoorRecordInspectionResults")
@CheckClientToken
@CheckUserToken
@Api(tags = "巡检记录室内")
public class DeviceIndoorRecordInspectionResultsController extends BaseController<DeviceIndoorRecordInspectionResultsBiz,DeviceIndoorRecordInspectionResults,Integer> {

    @Autowired
    private DeviceIndoorRecordInspectionResultsBiz dirirBiz;
    @Autowired
    protected DeviceIndoorLabelBiz dilBiz;
    @Autowired
    protected DeviceOutdoorLabelBiz dolBiz;
    @Autowired
    protected DeviceRouteLabelBiz drlBiz;
    @Autowired
    protected DeviceInspectionTasksBiz ditBiz;
    @Autowired
    private IUserFeign iUserFeign;
    @Autowired
    protected DeviceItemValueBiz divBiz;
    @Autowired
    protected DeviceFacilitiesTypeBiz dftBiz;
    @Autowired
    protected DeviceBuildingBiz dbBiz;
    @Autowired
    private DeviceOutdoorRecordInspectionResultsBiz dorirBiz;
    @Autowired
    private DeviceUploadInformationBiz duiBiz;

    @RequestMapping(value = "/selectByLabelId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询室内标签巡检记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId",value = "室内标签id",paramType = "query"),
            @ApiImplicitParam(name = "datestr",value = "查询年月",paramType = "query")
    })
    public TableResultResponse<DeviceIndoorRecordInspectionResults> selectByLabelId(String page, String limit, @RequestParam Integer labelId,String datestr){
        Map<String ,Object> param = new LinkedHashMap<>();
        if(StringUtils.isBlank(page)){
            page="1";
        }
        if(StringUtils.isBlank(limit)){
            limit="10";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        return baseBiz.selectByLabelId(query,labelId,datestr);
    }

    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询室内标签巡检记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "inspectionDateStr",value = "巡检时间字符串",paramType = "query")
    })
    public TableResultResponse<DeviceIndoorRecordInspectionResults> pageList(String page, String limit, DeviceIndoorRecordInspectionResults entity,String inspectionDateStr,String floorId){
        //当楼层乱输入时直接返回空，查不到
        if(entity.getFloor()==null&&StringUtils.isNotBlank(floorId)){
            try {
                entity.setFloor(Integer.parseInt(floorId));
            }catch (Exception e){
                return new TableResultResponse<>(0,new ArrayList<>());
            }
        }
        entity.setLeakFlag("0");
        String inspectionStartDate = null;
        String inspectionEndDate = null;
        if(StringUtils.isNotBlank(inspectionDateStr)&&!"null".equalsIgnoreCase(inspectionDateStr)){
            String [] temp = inspectionDateStr.split(",");
            if(temp.length==2){
                inspectionStartDate = temp[0]+" 00:00:00";
                inspectionEndDate = temp[1]+" 23:59:59";
            }
        }
        if("全部".equalsIgnoreCase(entity.getbName())){
            entity.setbName(null);
        }
        Map<String ,Object> param = new LinkedHashMap<>();
        if(StringUtils.isBlank(page)){
            page="1";
        }
        if(StringUtils.isBlank(limit)){
            limit="10";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        TrimUtil.trimObjectEscape(entity);
        return baseBiz.selectQuery(query,entity,inspectionStartDate, inspectionEndDate,null, null);
    }

    @RequestMapping(value = "/pageListLeak",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询室内漏检记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "plannedCompletionTimeStr",value = "计划时间字符串",paramType = "query"),
    })
    public TableResultResponse<DeviceIndoorRecordInspectionResults> pageListLeak(String page, String limit,DeviceIndoorRecordInspectionResults entity,String plannedCompletionTimeStr,String floorId){
        //当楼层乱输入时直接返回空，查不到
        if(entity.getFloor()==null&&StringUtils.isNotBlank(floorId)){
            try {
                entity.setFloor(Integer.parseInt(floorId));
            }catch (Exception e){
                return new TableResultResponse<>(0,new ArrayList<>());
            }
        }
        entity.setLeakFlag("1");
        String plannedCompletionStartTime = null;
        String plannedCompletionEndTime = null;
        if(StringUtils.isNotBlank(plannedCompletionTimeStr)&&!"null".equalsIgnoreCase(plannedCompletionTimeStr)){
            String [] temp = plannedCompletionTimeStr.split(",");
            if(temp.length==2){
                plannedCompletionStartTime = temp[0]+" 00:00:00";
                plannedCompletionEndTime = temp[1]+" 23:59:59";
            }
        }
        if("全部".equalsIgnoreCase(entity.getbName())){
            entity.setbName(null);
        }
        Map<String ,Object> param = new LinkedHashMap<>();
        if(StringUtils.isBlank(page)){
            page="1";
        }
        if(StringUtils.isBlank(limit)){
            limit="10";
        }
        param.put("page",page);
        param.put("limit",limit);
        Query query = new Query(param);
        TrimUtil.trimObjectEscape(entity);
        return dirirBiz.selectQuery(query,entity,null, null,plannedCompletionStartTime, plannedCompletionEndTime);
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询室内记录明细")
    public ObjectRestResponse get(@RequestParam Integer id){
        //默认检测项
        String itemFlag = "0";
        //默认室内
        String inspectionFlag = "0";
        Map<String,Object> map = Maps.newHashMap();
        DeviceIndoorRecordInspectionResults recordInspectionResults = baseBiz.selectById(id);
        Map<String,Object> recordInspectionResult = MapUtils.convertObjToMap(recordInspectionResults);
        List<Map> lists = new ArrayList<>();
        if(recordInspectionResults!=null){
             lists = divBiz.selectByResultId(id,inspectionFlag,itemFlag);
             String imgs = recordInspectionResults.getUploadPictureIds();
             Integer[] temp = SplitUtil.splitInt(imgs);
             if(temp.length!=0){
                 List<String> imgList = new ArrayList<>();
                 DeviceUploadInformation deviceUploadInformation = null;
                 for(int i=0;i<temp.length;i++){
                     deviceUploadInformation = duiBiz.selectByIdTemp(temp[i]);
                     if(deviceUploadInformation!=null){
                         imgList.add(deviceUploadInformation.getFilePath());
                     }
                 }
                 recordInspectionResult.put("uploadPictureIds",imgList);
             }
            if("0".equalsIgnoreCase(recordInspectionResults.getInspectionResult())){
                recordInspectionResult.put("color","#666666");
            }
            if("1".equalsIgnoreCase(recordInspectionResults.getInspectionResult())){
                recordInspectionResult.put("color","#F5A623");
            }
        }
        map.put("recordInspectionResults",recordInspectionResult);
        map.put("item",lists);
        return new ObjectRestResponse().data(map);
    }




    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加巡检结果记录表")
    public ObjectRestResponse add(@RequestBody Map<String,Object> params){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        try{
            String facilitiesNo = params.get("facilitiesNo").toString();
            List<ItemValueVo> itemValueList = JSON.parseArray((String) params.get("itemValue"),ItemValueVo.class);
            String inspectionResult = params.get("inspectionResult").toString();//巡检结果[0=正常/1=异常]
            Integer taskId = Integer.valueOf(params.get("taskId").toString());//任务id
            DeviceIndoorLabel deviceIndoorLabel =dilBiz.getIndoorLabelByFacilitiesNo(facilitiesNo);
            DeviceInspectionTasks deviceInspectionTasks = ditBiz.getById(taskId);
            if(deviceIndoorLabel!=null){//室内巡检结果记录表
                DeviceFacilitiesType deviceFacilitiesType = dftBiz.selectById(deviceIndoorLabel.getFacilitiesTypeId());
                DeviceBuilding deviceBuilding = dbBiz.selectById(deviceIndoorLabel.getBuildingId());

                DeviceIndoorRecordInspectionResults indoorRecordInspectionResults = new DeviceIndoorRecordInspectionResults();
                indoorRecordInspectionResults.setLabelId(deviceIndoorLabel.getId());//标签id
                indoorRecordInspectionResults.setFacilitiesNo(facilitiesNo);//设施编号
                indoorRecordInspectionResults.setEquipmentType(deviceFacilitiesType.getEquipmentType());//设施类型
                indoorRecordInspectionResults.setbName(deviceBuilding.getBName());//建筑名称
                indoorRecordInspectionResults.setFloor(deviceIndoorLabel.getFloor());//楼层
                indoorRecordInspectionResults.setPositionDescription(deviceIndoorLabel.getPositionDescription());//位置描述

                if("0".equals(inspectionResult)){
                    indoorRecordInspectionResults.setProblemDescription(null);//问题描述
                    indoorRecordInspectionResults.setUploadPictureIds(null);//上传图片ids
                    indoorRecordInspectionResults.setHandling(null);//处理方式[0=已自行处理/1=上报维修]
                    if("0".equals(deviceIndoorLabel.getStatus())){
                        indoorRecordInspectionResults.setEquipmentStatus("正常→正常");
                    }else {
                        indoorRecordInspectionResults.setEquipmentStatus("维修中→正常");
                        //更改标签状态
                        deviceIndoorLabel.setStatus("0");
                    }
                }
                if("1".equals(inspectionResult)){
                    indoorRecordInspectionResults.setProblemDescription(params.get("problemDescription").toString());//问题描述
                    indoorRecordInspectionResults.setHandling(params.get("handling").toString());//处理方式[0=已自行处理/1=上报维修]
                    if(params.get("ids")!=null){
                        indoorRecordInspectionResults.setUploadPictureIds(params.get("ids").toString());//上传图片ids
                    }
                    if("0".equals(params.get("handling").toString())){
                        if("0".equals(deviceIndoorLabel.getStatus())){
                            indoorRecordInspectionResults.setEquipmentStatus("正常→正常");
                            //更新标签最近巡检时间
                            deviceIndoorLabel.setLastInspectionTime(new Date());
                        }else{
                            indoorRecordInspectionResults.setEquipmentStatus("维修中→正常");
                            deviceIndoorLabel.setStatus("0");
                            deviceIndoorLabel.setLastInspectionTime(new Date());
                        }
                    }
                    if("1".equals(params.get("handling").toString())){
                        if("0".equals(deviceIndoorLabel.getStatus())){
                            indoorRecordInspectionResults.setEquipmentStatus("正常→维修中");
                            deviceIndoorLabel.setStatus("1");
                            deviceIndoorLabel.setLastInspectionTime(new Date());
                        }else{
                            indoorRecordInspectionResults.setEquipmentStatus("维修中→维修中");
                            deviceIndoorLabel.setLastInspectionTime(new Date());
                        }
                    }
                }
                indoorRecordInspectionResults.setInspectionResult(inspectionResult);//巡检结果[0=正常/1=异常]
                //默认值
                indoorRecordInspectionResults.setLeakFlag("0");
                indoorRecordInspectionResults.setInspectionPerson(BaseContextHandler.getUsername()+"("+BaseContextHandler.getName()+")");//巡检人
                indoorRecordInspectionResults.setInspectionDate(new Date());//巡检时间
                JSONObject jsonObject = iUserFeign.getUserById(BaseContextHandler.getUserID());
                indoorRecordInspectionResults.setMobilePhone(jsonObject.getJSONObject("data").getString("mobilePhone"));
                indoorRecordInspectionResults.setTaskId(taskId);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String plannedCompletionTime = simpleDateFormat.format(deviceInspectionTasks.getInspectionDate()).substring(0,10)+" "+deviceInspectionTasks.getInspectionTimePeriod().substring(6,11);
                indoorRecordInspectionResults.setPlannedCompletionTime(sdf.parse(plannedCompletionTime));//计划完成时间
                dirirBiz.insertSelective(indoorRecordInspectionResults);
                //判断该路线是否还有任务
                if(ditBiz.getByRouteId(deviceInspectionTasks.getInspectionRouteId(),deviceIndoorLabel.getId())==false){
                    deviceIndoorLabel.setResultFlag("1");
                }else{
                    //还有任务，设施巡检结果为未检测
                    deviceIndoorLabel.setResultFlag("0");
                }
                dilBiz.updateSelectiveById(deviceIndoorLabel);
                //巡检任务状态更新
                deviceInspectionTasks.setStatus("2");
                ditBiz.updateSelectiveById(deviceInspectionTasks);
                for(ItemValueVo itemValue:itemValueList){
                    DeviceItemValue deviceItemValue = new DeviceItemValue();
                    deviceItemValue.setItemId(itemValue.getId());
                    deviceItemValue.setResultsId(indoorRecordInspectionResults.getId());
                    deviceItemValue.setInspectionFlag("0");
                    deviceItemValue.setItemVaule(itemValue.getValue());
                    divBiz.insertSelective(deviceItemValue);
                }
            }else{//室外巡检结果记录表
                DeviceOutdoorLabel deviceOutdoorLabel =dolBiz.getIndoorLabelByFacilitiesNo(facilitiesNo);

                DeviceFacilitiesType deviceFacilitiesType = dftBiz.selectById(deviceOutdoorLabel.getFacilitiesTypeId());
                DeviceOutdoorRecordInspectionResults outdoorRecordInspectionResults = new DeviceOutdoorRecordInspectionResults();
                outdoorRecordInspectionResults.setLabelId(deviceOutdoorLabel.getId());//标签id
                outdoorRecordInspectionResults.setFacilitiesNo(facilitiesNo);//设施编号
                outdoorRecordInspectionResults.setEquipmentType(deviceFacilitiesType.getEquipmentType());//设施类型
                outdoorRecordInspectionResults.setPositionDescription(deviceOutdoorLabel.getPositionDescription());//位置描述

                if("0".equals(inspectionResult)){
                    outdoorRecordInspectionResults.setProblemDescription(null);//问题描述
                    outdoorRecordInspectionResults.setUploadPictureIds(null);//上传图片ids
                    outdoorRecordInspectionResults.setHandling(null);//处理方式[0=已自行处理/1=上报维修]
                    if("0".equals(deviceOutdoorLabel.getStatus())){
                        outdoorRecordInspectionResults.setEquipmentStatus("正常→正常");
                        deviceOutdoorLabel.setLastInspectionTime(new Date());
                    }
                    if("1".equals(deviceOutdoorLabel.getStatus())){
                        outdoorRecordInspectionResults.setEquipmentStatus("维修中→正常");
                        //更改标签状态
                        deviceOutdoorLabel.setStatus("0");
                        deviceOutdoorLabel.setLastInspectionTime(new Date());
                    }
                }
                if("1".equals(inspectionResult)){
                    outdoorRecordInspectionResults.setProblemDescription(params.get("problemDescription").toString());//问题描述
                    outdoorRecordInspectionResults.setHandling(params.get("handling").toString());//处理方式[0=已自行处理/1=上报维修]
                    if(params.get("ids")!=null){
                        outdoorRecordInspectionResults.setUploadPictureIds(params.get("ids").toString());//上传图片ids
                    }
                    if("0".equals(params.get("handling").toString())){
                        if("0".equals(deviceOutdoorLabel.getStatus())){
                            outdoorRecordInspectionResults.setEquipmentStatus("正常→正常");
                            deviceOutdoorLabel.setLastInspectionTime(new Date());

                        }
                        if("1".equals(deviceOutdoorLabel.getStatus())){
                            outdoorRecordInspectionResults.setEquipmentStatus("维修中→正常");
                            deviceOutdoorLabel.setStatus("0");
                            deviceOutdoorLabel.setLastInspectionTime(new Date());
                        }
                    }
                    if("1".equals(params.get("handling").toString())){
                        if("0".equals(deviceOutdoorLabel.getStatus())){
                            outdoorRecordInspectionResults.setEquipmentStatus("正常→维修中");
                            deviceOutdoorLabel.setStatus("1");
                            deviceOutdoorLabel.setLastInspectionTime(new Date());
                        }
                        if("1".equals(deviceOutdoorLabel.getStatus())){
                            outdoorRecordInspectionResults.setEquipmentStatus("维修中→维修中");
                            deviceOutdoorLabel.setLastInspectionTime(new Date());
                        }
                    }
                }
                outdoorRecordInspectionResults.setInspectionResult(inspectionResult);//巡检结果[0=正常/1=异常]
                //默认值
                outdoorRecordInspectionResults.setLeakFlag("0");
                outdoorRecordInspectionResults.setInspectionPerson(BaseContextHandler.getUsername()+"("+BaseContextHandler.getName()+")");//巡检人
                outdoorRecordInspectionResults.setInspectionDate(new Date());//巡检时间
                JSONObject jsonObject = iUserFeign.getUserById(BaseContextHandler.getUserID());
                outdoorRecordInspectionResults.setMobilePhone(jsonObject.getJSONObject("data").getString("mobilePhone"));
                outdoorRecordInspectionResults.setTaskId(taskId);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String plannedCompletionTime = simpleDateFormat.format(deviceInspectionTasks.getInspectionDate()).substring(0,10)+" "+deviceInspectionTasks.getInspectionTimePeriod().substring(6,11);
                outdoorRecordInspectionResults.setPlannedCompletionTime(sdf.parse(plannedCompletionTime));//计划完成时间
                dorirBiz.insertSelective(outdoorRecordInspectionResults);
                //判断该路线是否还有任务
                if(ditBiz.getByRouteId(deviceInspectionTasks.getInspectionRouteId(),deviceOutdoorLabel.getId())==false){
                    deviceOutdoorLabel.setResultFlag("1");
                }else{
                    //还有任务，设施巡检结果为未检测
                    deviceOutdoorLabel.setResultFlag("0");
                }
                dolBiz.updateSelectiveById(deviceOutdoorLabel);
                //巡检任务状态更新
                deviceInspectionTasks.setStatus("2");
                ditBiz.updateSelectiveById(deviceInspectionTasks);
                for(ItemValueVo itemValue:itemValueList){
                    DeviceItemValue deviceItemValue = new DeviceItemValue();
                    deviceItemValue.setItemId(itemValue.getId());
                    deviceItemValue.setResultsId(outdoorRecordInspectionResults.getId());
                    deviceItemValue.setInspectionFlag("1");
                    deviceItemValue.setItemVaule(itemValue.getValue());
                    divBiz.insertSelective(deviceItemValue);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseResult;
    }

    @RequestMapping(value = "/getSelected",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("联想搜索设施类型下拉框")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "leakFlag",value = "1=漏检/0=已检",paramType = "query")
    })
    public ObjectRestResponse<DeviceIndoorLabel> getSelected(String leakFlag){
        ObjectRestResponse responseResult =  new ObjectRestResponse<>();
        //获取所有的设施
        List<String> type = baseBiz.getSelected(leakFlag);
        Map<String,Object> map = new HashMap<>();
        LinkedList<String> temp = new LinkedList<>();
        if(type!=null&&type.size()>0){
            if(!"".equals(type.get(0))){
                temp.addAll(type);
            }
        }
        temp.addFirst("全部");
        map.put("type",temp);
        responseResult.setData(map);
        return responseResult;
    }


/*    @RequestMapping(value = "/export",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("已检记录导出2003")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inspectionDateStr",value = "巡检时间字符串",paramType = "query")
    })
    public void export(HttpServletResponse response, DeviceIndoorRecordInspectionResults entity, String inspectionDateStr) throws IOException {
        entity.setLeakFlag("0");
        String inspectionStartDate = null;
        String inspectionEndDate = null;
        if(StringUtils.isNotBlank(inspectionDateStr)){
            inspectionStartDate = inspectionDateStr.split(",")[0];
            inspectionEndDate = inspectionDateStr.split(",")[1];
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<DeviceIndoorRecordInspectionResults> lists = dirirBiz.export(entity,inspectionStartDate, inspectionEndDate,null, null);
        HSSFWorkbook wb = new HSSFWorkbook();
        CellStyleUtil cellStyleUtil = new CellStyleUtil(wb);
        HSSFSheet sheet=wb.createSheet("室内巡检记录");
        Integer [] width = {3000,5500,5500,6500,6500,3000,6500,5500,4000,5000,5000,6500};
        String [] columnName = {"巡检结果","状态变更","设施编号","设施类型","所属建筑","楼层","位置描述","巡检人","联系电话","巡检时间","计划完成时间","问题描述"};
        // 设置列宽,第一列字段名称
        cellStyleUtil.initWidthName(sheet,width,columnName);
        HSSFRow row = null;
        DeviceIndoorRecordInspectionResults temp = null;
        for(int i=0;i<lists.size();i++){
            temp = lists.get(i);
            row=sheet.createRow(i+1);
            String [] values = {"0".equals(temp.getInspectionResult())?"正常":"异常", temp.getEquipmentStatus(), temp.getFacilitiesNo(), temp.getEquipmentType(), temp.getbName(), temp.getFloor()+"", temp.getPositionDescription(), temp.getInspectionPerson(), temp.getMobilePhone(), temp.getInspectionDate()==null?"":simpleDateFormat.format(temp.getInspectionDate()), temp.getPlannedCompletionTime()==null?"":simpleDateFormat.format(temp.getPlannedCompletionTime()), temp.getProblemDescription()};
            cellStyleUtil.setRowContent(row,values);
        }
        //输出Excel文件
        OutputStream output=response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename="+ URLEncoder.encode("室内巡检记录.xls", "UTF-8"));
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();
    }*/

    @RequestMapping(value = "/export",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("已检记录导出2007")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inspectionDateStr",value = "巡检时间字符串",paramType = "query")
    })
    public void export(HttpServletResponse response, @RequestBody Map<String,Object> params) throws Exception {
        String inspectionDateStr = (String) params.get("inspectionDateStr");
        params.remove("limit");
        params.remove("page");
        params.remove("inspectionDateStr");
        params.remove("plannedCompletionTimeStr");
        DeviceIndoorRecordInspectionResults entity = null;
        boolean floorFlag = true;
        try {
            if(params.get("floorId")!=null&&StringUtils.isNotBlank(((String)params.get("floorId")).trim())){
                params.put("floor",Integer.parseInt((String)params.get("floorId")));
            }else {
                params.put("floor",null);
            }
        }catch (Exception e){
            floorFlag = false;
        }
        try {
            entity = (DeviceIndoorRecordInspectionResults)MapUtils.mapToObject(params,DeviceIndoorRecordInspectionResults.class);
        }catch (Exception e){
            entity = new DeviceIndoorRecordInspectionResults();
        }
        entity.setLeakFlag("0");
        if("全部".equalsIgnoreCase(entity.getbName())){
            entity.setbName(null);
        }
        String inspectionStartDate = null;
        String inspectionEndDate = null;
        if(StringUtils.isNotBlank(inspectionDateStr)&&!"null".equalsIgnoreCase(inspectionDateStr)){
            String [] temp = inspectionDateStr.split(",");
            if(temp.length==2){
                inspectionStartDate = temp[0]+" 00:00:00";
                inspectionEndDate = temp[1]+" 23:59:59";
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        TrimUtil.trimObjectEscape(entity);
        List<DeviceIndoorRecordInspectionResults> lists = new ArrayList<>();
        if(floorFlag){
            lists = dirirBiz.export(entity,inspectionStartDate, inspectionEndDate,null, null);
        }
        XSSFWorkbook wb = new XSSFWorkbook();
        CellStyleUtil cellStyleUtil = new CellStyleUtil(wb);
        XSSFSheet sheet=wb.createSheet("室内巡检记录");
        Integer [] width = {3000,5500,5500,6500,6500,3000,6500,5500,4000,5000,5000,6500};
        String [] columnName = {"巡检结果","状态变更","设施编号","设施类型","所属建筑","楼层","位置描述","巡检人","联系电话","巡检时间","计划完成时间","问题描述"};
        // 设置列宽,第一列字段名称
        cellStyleUtil.initWidthName(sheet,width,columnName);
        XSSFRow row = null;
        DeviceIndoorRecordInspectionResults temp = null;
        int count = 0;
        if(lists.size()>100000){
            count = 100000;
        }else {
            count = lists.size();
        }
        for(int i=0;i<count;i++){
            temp = lists.get(i);
            row=sheet.createRow(i+1);
            String [] values = {"0".equals(temp.getInspectionResult())?"正常":"异常", temp.getEquipmentStatus(), temp.getFacilitiesNo(), temp.getEquipmentType(), temp.getbName(), temp.getFloor()+"", temp.getPositionDescription(), temp.getInspectionPerson(), temp.getMobilePhone(), temp.getInspectionDate()==null?"":simpleDateFormat.format(temp.getInspectionDate()), temp.getPlannedCompletionTime()==null?"":simpleDateFormat.format(temp.getPlannedCompletionTime()), temp.getProblemDescription()};
            cellStyleUtil.setRowContent(row,values);
        }

        //输出Excel文件
        OutputStream output=response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename="+ URLEncoder.encode("室内巡检记录.xlsx", "UTF-8"));
        response.setContentType("application/octet-stream");
        wb.write(output);
        output.close();
    }



    @RequestMapping(value = "/exportLeak",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("漏检记录导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "plannedCompletionTimeStr",value = "计划时间字符串",paramType = "query"),
    })
    public void exportLeak(HttpServletResponse response,@RequestBody Map<String,Object> params) throws IOException {
        String plannedCompletionTimeStr = (String) params.get("plannedCompletionTimeStr");
        params.remove("limit");
        params.remove("page");
        params.remove("inspectionDateStr");
        params.remove("plannedCompletionTimeStr");
        DeviceIndoorRecordInspectionResults entity = null;
        boolean floorFlag = true;
        try {
            if(params.get("floorId")!=null&&StringUtils.isNotBlank(((String)params.get("floorId")).trim())){
                params.put("floor",Integer.parseInt((String)params.get("floorId")));
            }else {
                params.put("floor",null);
            }
        }catch (Exception e){
            floorFlag = false;
        }
        try {
            entity = (DeviceIndoorRecordInspectionResults)MapUtils.mapToObject(params,DeviceIndoorRecordInspectionResults.class);
        }catch (Exception e){
            entity = new DeviceIndoorRecordInspectionResults();
        }
        entity.setLeakFlag("1");
        if("全部".equalsIgnoreCase(entity.getbName())){
            entity.setbName(null);
        }
        String plannedCompletionStartTime = null;
        String plannedCompletionEndTime = null;
        if(StringUtils.isNotBlank(plannedCompletionTimeStr)&&!"null".equalsIgnoreCase(plannedCompletionTimeStr)){
            String [] temp = plannedCompletionTimeStr.split(",");
            if(temp.length==2){
                plannedCompletionStartTime = temp[0]+" 00:00:00";
                plannedCompletionEndTime = temp[1]+" 23:59:59";
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        TrimUtil.trimObjectEscape(entity);
        List<DeviceIndoorRecordInspectionResults> lists = new ArrayList<>();
        if(floorFlag){
            lists = dirirBiz.export(entity,null, null,plannedCompletionStartTime, plannedCompletionEndTime);
        }
        XSSFWorkbook wb = new XSSFWorkbook();
        CellStyleUtil cellStyleUtil = new CellStyleUtil(wb);
        XSSFSheet sheet=wb.createSheet("室内漏检记录");
        Integer [] width = {5000,5500,11000,6500,6500,6500,6500};
        String [] columnName = {"设施编号","设施类型","所属建筑","楼层","位置描述","计划完成时间","漏检原因"};
        // 设置列宽,第一列字段名称
        cellStyleUtil.initWidthName(sheet,width,columnName);
        XSSFRow row = null;
        DeviceIndoorRecordInspectionResults temp = null;
        int count = 0;
        if(lists.size()>100000){
            count = 100000;
        }else {
            count = lists.size();
        }
        for(int i=0;i<count;i++){
            temp = lists.get(i);
            String [] values = {temp.getFacilitiesNo(),temp.getEquipmentType(),temp.getbName(),temp.getFloor()+"",temp.getPositionDescription(),temp.getPlannedCompletionTime()==null?"":simpleDateFormat.format(temp.getPlannedCompletionTime()),temp.getProblemDescription()};
            row=sheet.createRow(i+1);
            cellStyleUtil.setRowContent(row,values);
        }
        //输出Excel文件
        OutputStream output=response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename="+ URLEncoder.encode("室内漏检记录.xlsx", "UTF-8"));
        response.setContentType("application/octet-stream");
        wb.write(output);
        output.close();
    }

    @RequestMapping(value = "/addJumpInspectionResults",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("巡检设施跳过,生成巡检记录")
    public ObjectRestResponse addJumpInspectionResults(@RequestParam Integer id,@RequestParam Integer labelId,Integer routeId,String problemDescription) throws Exception{
        ObjectRestResponse responseResult =  new ObjectRestResponse();
//        List<DeviceIndoorRecordInspectionResults> list = dirirBiz.selectByTaskId(id,labelId);
//        if(list.size()>0){
//            responseResult.setData("设施已巡检");
//            return responseResult;
//        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DeviceIndoorLabel deviceIndoorLabel = dilBiz.selectById(labelId);
        DeviceFacilitiesType deviceFacilitiesType = dftBiz.selectById(deviceIndoorLabel.getFacilitiesTypeId());
        DeviceBuilding deviceBuilding = dbBiz.selectById(deviceIndoorLabel.getBuildingId());
        DeviceIndoorRecordInspectionResults indoorRecordInspectionResults = new DeviceIndoorRecordInspectionResults();
        indoorRecordInspectionResults.setLabelId(deviceIndoorLabel.getId());//标签id
        indoorRecordInspectionResults.setFacilitiesNo(deviceIndoorLabel.getFacilitiesNo());//设施编号
        indoorRecordInspectionResults.setEquipmentType(deviceFacilitiesType.getEquipmentType());//设施类型
        indoorRecordInspectionResults.setbName(deviceBuilding.getBName());//建筑名称
        indoorRecordInspectionResults.setFloor(deviceIndoorLabel.getFloor());//楼层
        indoorRecordInspectionResults.setPositionDescription(deviceIndoorLabel.getPositionDescription());//位置描述
        indoorRecordInspectionResults.setLeakFlag("1");//漏检
        indoorRecordInspectionResults.setInspectionPerson(BaseContextHandler.getUsername());
        indoorRecordInspectionResults.setInspectionDate(new Date());
        //DeviceInspectionTasks deviceInspectionTasks = ditBiz.selectByRouteId(routeId,BaseContextHandler.getUserID());
        DeviceInspectionTasks deviceInspectionTasks = ditBiz.selectById(id);
        indoorRecordInspectionResults.setTaskId(id);
        String inspectionTimePeriod = deviceInspectionTasks.getInspectionTimePeriod();//巡检时段
        String plannedCompletionTime = simpleDateFormat.format(deviceInspectionTasks.getInspectionDate()).substring(0,10)+" "+inspectionTimePeriod.substring(inspectionTimePeriod.lastIndexOf("-")+1);
        indoorRecordInspectionResults.setPlannedCompletionTime(sdf.parse(plannedCompletionTime));//计划完成时间
        indoorRecordInspectionResults.setProblemDescription(BaseContextHandler.getUsername()+"("+BaseContextHandler.getName()+") 备注："+problemDescription);
        dirirBiz.insertSelective(indoorRecordInspectionResults);
        //更新设施巡检结果状态
        if(ditBiz.getByRouteId(routeId,deviceIndoorLabel.getId())==false){
            deviceIndoorLabel.setResultFlag("3");
        }else{
            deviceIndoorLabel.setResultFlag("0");
        }
        dilBiz.updateSelectiveById(deviceIndoorLabel);
        //更新任务状态为进行中
        deviceInspectionTasks.setStatus("2");
        ditBiz.updateSelectiveById(deviceInspectionTasks);
        responseResult.setData(indoorRecordInspectionResults.getId());
        return responseResult;
    }

    @RequestMapping(value = "/voiceToText",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("语音转文字")
    public ObjectRestResponse voiceToText(MultipartFile file,String fileName) throws Exception{
        ObjectRestResponse responseResult =  new ObjectRestResponse();
/*        if("mp3".equals(fileName.substring(fileName.lastIndexOf(".") + 1,fileName.length()).toLowerCase())){
            System.out.println(request.getSession().getServletContext().getRealPath(fileName));
            request.getSession().getServletContext().getRealPath(fileName).replaceAll('\\\\\\\\\\',"\\\\\\\\");
            AudioUtils.getInstance().convertMP32Pcm(request.getSession().getServletContext().getRealPath(fileName).replace("\",""),request.getSession().getServletContext().getResourcePaths("/")+"a.pcm");
            //String filePath = file.getName();//类路径，编译后classes目录下
            Resource resource = new ClassPathResource(fileName);
            System.out.println("------------"+resource.getURI());
            File pcmfile = new File(resource.getURI());
            String test = VoiceToTextUtil.voiceToText(pcmfile);
            responseResult.setData(test);
        }else{
            //String test = VoiceToTextUtil.voiceToText(file);
            //responseResult.setData(test);
        }*/
        AudioUtils.getInstance().convertMP32Pcm("C:\\Users\\Administrator\\Desktop\\111.mp3", "C:\\Users\\Administrator\\Desktop\\a.pcm");
        File pcmfile = new File("C:\\Users\\Administrator\\Desktop\\a.pcm");
        String test = VoiceToTextUtil.voiceToText(pcmfile);
        responseResult.setData(test);
        return responseResult;
    }

}