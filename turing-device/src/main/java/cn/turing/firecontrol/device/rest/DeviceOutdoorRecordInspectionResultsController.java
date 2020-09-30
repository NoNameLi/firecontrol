package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.*;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.CellStyleUtil;
import cn.turing.firecontrol.device.util.MapUtils;
import cn.turing.firecontrol.device.util.SplitUtil;
import cn.turing.firecontrol.device.util.TrimUtil;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("deviceOutdoorRecordInspectionResults")
@CheckClientToken
@CheckUserToken
@Api(tags = "巡检记录室外")
public class DeviceOutdoorRecordInspectionResultsController extends BaseController<DeviceOutdoorRecordInspectionResultsBiz,DeviceOutdoorRecordInspectionResults,Integer> {

    @Autowired
    private DeviceOutdoorRecordInspectionResultsBiz dorirBiz;
    @Autowired
    protected DeviceItemValueBiz divBiz;
    @Autowired
    protected DeviceOutdoorLabelBiz dolBiz;
    @Autowired
    protected DeviceFacilitiesTypeBiz dftBiz;
    @Autowired
    protected DeviceInspectionTasksBiz ditBiz;
    @Autowired
    private DeviceUploadInformationBiz duiBiz;

    @RequestMapping(value = "/selectByLabelId",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询室外标签巡检记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId",value = "室外标签id",paramType = "query"),
            @ApiImplicitParam(name = "datestr",value = "查询年月",paramType = "query")
    })
    public TableResultResponse<DeviceOutdoorRecordInspectionResults> selectByLabelId(String page, String limit, @RequestParam Integer labelId, String datestr){
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
        return dorirBiz.selectByLabelId(query,labelId,datestr);
    }

    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询室内标签巡检记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页码",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页显示条数",paramType = "query"),
            @ApiImplicitParam(name = "inspectionStartDateStr",value = "巡检时间字符串",paramType = "query")
    })
    public TableResultResponse<DeviceOutdoorRecordInspectionResults> pageList(String page, String limit, DeviceIndoorRecordInspectionResults entity, String inspectionDateStr){
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
            @ApiImplicitParam(name = "plannedCompletionEndTimeStr",value = "计划时间字符串",paramType = "query"),
    })
    public TableResultResponse<DeviceOutdoorRecordInspectionResults> pageListLeak(String page, String limit,DeviceIndoorRecordInspectionResults entity,String plannedCompletionTimeStr){
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
        return baseBiz.selectQuery(query,entity,null, null,plannedCompletionStartTime, plannedCompletionEndTime);
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询室内记录明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "leakFlag",value = "1=漏检/0=已检",paramType = "query")
    })
    public ObjectRestResponse get(@RequestParam Integer id){
        //默认检测项
        String itemFlag = "0";
        //默认室外
        String inspectionFlag = "1";
        Map<String,Object> map = Maps.newHashMap();
        DeviceOutdoorRecordInspectionResults recordInspectionResults = baseBiz.selectById(id);
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

    @RequestMapping(value = "/export",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("已检记录导出2007")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inspectionStartDateStr",value = "巡检时间字符串",paramType = "query")
    })
    public void export(HttpServletResponse response,@RequestBody Map<String,Object> params) throws IOException {
        String inspectionDateStr = (String) params.get("inspectionDateStr");
        params.remove("limit");
        params.remove("page");
        params.remove("inspectionDateStr");
        params.remove("plannedCompletionTimeStr");
        DeviceOutdoorRecordInspectionResults entity;
        try {
            entity = (DeviceOutdoorRecordInspectionResults)MapUtils.mapToObject(params,DeviceIndoorRecordInspectionResults.class);
        }catch (Exception e){
            entity = new DeviceOutdoorRecordInspectionResults();
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        TrimUtil.trimObjectEscape(entity);
        List<DeviceOutdoorRecordInspectionResults> lists = baseBiz.export(entity,inspectionStartDate, inspectionEndDate,null, null);
        XSSFWorkbook wb = new XSSFWorkbook();
        CellStyleUtil cellStyleUtil = new CellStyleUtil(wb);
        XSSFSheet sheet=wb.createSheet("室外巡检记录");
        Integer [] width = {3000,5500,5500,3000,6500,5500,4000,5000,5000,6500};
        String [] columnName = {"巡检结果","状态变更","设施编号","设施类型","位置描述","巡检人","联系电话","巡检时间","计划完成时间","问题描述"};
        // 设置列宽,第一列字段名称
        cellStyleUtil.initWidthName(sheet,width,columnName);
        XSSFRow row = null;
        DeviceOutdoorRecordInspectionResults temp = null;
        int count = 0;
        if(lists.size()>100000){
            count = 100000;
        }else {
            count = lists.size();
        }
        for(int i=0;i<count;i++){
            temp = lists.get(i);
            row=sheet.createRow(i+1);
            String [] values = {"0".equals(temp.getInspectionResult())?"正常":"异常",temp.getEquipmentStatus(),temp.getFacilitiesNo(),temp.getEquipmentType(),temp.getPositionDescription(),temp.getInspectionPerson(),temp.getMobilePhone(),temp.getInspectionDate()==null?"":simpleDateFormat.format(temp.getInspectionDate()),temp.getPlannedCompletionTime()==null?"":simpleDateFormat.format(temp.getPlannedCompletionTime()),temp.getProblemDescription()};
            cellStyleUtil.setRowContent(row,values);
        }
        //输出Excel文件
        OutputStream output=response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename="+ URLEncoder.encode("室外巡检记录.xlsx", "UTF-8"));
        response.setContentType("application/octet-stream");
        wb.write(output);
        output.close();
    }

    @RequestMapping(value = "/exportLeak",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("漏检记录导出2007")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "plannedCompletionEndTimeStr",value = "计划时间字符串",paramType = "query")
    })
    public void exportLeak(HttpServletResponse response, @RequestBody Map<String,Object> params) throws IOException {
        String plannedCompletionTimeStr = (String) params.get("plannedCompletionTimeStr");
        params.remove("limit");
        params.remove("page");
        params.remove("inspectionDateStr");
        params.remove("plannedCompletionTimeStr");
        DeviceOutdoorRecordInspectionResults entity;
        try {
            entity = (DeviceOutdoorRecordInspectionResults)MapUtils.mapToObject(params,DeviceIndoorRecordInspectionResults.class);
        }catch (Exception e){
            entity = new DeviceOutdoorRecordInspectionResults();
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        TrimUtil.trimObjectEscape(entity);
        List<DeviceOutdoorRecordInspectionResults> lists = baseBiz.export(entity,null, null,plannedCompletionStartTime, plannedCompletionEndTime);
        XSSFWorkbook wb = new XSSFWorkbook();
        CellStyleUtil cellStyleUtil = new CellStyleUtil(wb);
        XSSFSheet sheet=wb.createSheet("室外漏检记录");
        Integer [] width = {5000,5500,6500,6500,6500};
        String [] columnName = {"设施编号","设施类型","位置描述","计划完成时间","漏检原因"};
        // 设置列宽,第一列字段名称
        cellStyleUtil.initWidthName(sheet,width,columnName);
        XSSFRow row = null;
        DeviceOutdoorRecordInspectionResults temp = null;
        int count = 0;
        if(lists.size()>100000){
            count = 100000;
        }else {
            count = lists.size();
        }
        for(int i=0;i<count;i++){
            temp = lists.get(i);
            row=sheet.createRow(i+1);
            String [] values = {temp.getFacilitiesNo(),temp.getEquipmentType(),temp.getPositionDescription(),temp.getPlannedCompletionTime()==null?"":simpleDateFormat.format(temp.getPlannedCompletionTime()),temp.getProblemDescription()};
            cellStyleUtil.setRowContent(row,values);
        }
        //输出Excel文件
        OutputStream output=response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename="+ URLEncoder.encode("室外漏检记录.xlsx", "UTF-8"));
        response.setContentType("application/octet-stream");
        wb.write(output);
        output.close();
    }

    @RequestMapping(value = "/addJumpInspectionResults",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("巡检设施跳过,生成巡检记录")
    public ObjectRestResponse addJumpInspectionResults(@RequestParam Integer id,@RequestParam Integer labelId,Integer routeId,String problemDescription) throws Exception{
        ObjectRestResponse responseResult =  new ObjectRestResponse();
//        List<DeviceOutdoorRecordInspectionResults> list = dorirBiz.selectByTaskId(id,labelId);
//        if(list.size()>0){
//            responseResult.setData("设施已巡检");
//            return responseResult;
//        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DeviceOutdoorLabel deviceOutdoorLabel = dolBiz.selectById(labelId);
        DeviceFacilitiesType deviceFacilitiesType = dftBiz.selectById(deviceOutdoorLabel.getFacilitiesTypeId());
        DeviceOutdoorRecordInspectionResults outdoorRecordInspectionResults = new DeviceOutdoorRecordInspectionResults();
        outdoorRecordInspectionResults.setLabelId(deviceOutdoorLabel.getId());//标签id
        outdoorRecordInspectionResults.setFacilitiesNo(deviceOutdoorLabel.getFacilitiesNo());//设施编号
        outdoorRecordInspectionResults.setEquipmentType(deviceFacilitiesType.getEquipmentType());//设施类型
        outdoorRecordInspectionResults.setPositionDescription(deviceOutdoorLabel.getPositionDescription());//位置描述
        outdoorRecordInspectionResults.setLeakFlag("1");//漏检
        outdoorRecordInspectionResults.setInspectionPerson(BaseContextHandler.getUsername());
        outdoorRecordInspectionResults.setInspectionDate(new Date());
        DeviceInspectionTasks deviceInspectionTasks = ditBiz.selectById(id);
        outdoorRecordInspectionResults.setTaskId(id);
        //DeviceInspectionTasks deviceInspectionTasks = ditBiz.selectByRouteId(routeId,BaseContextHandler.getUserID());
        String inspectionTimePeriod = deviceInspectionTasks.getInspectionTimePeriod();//巡检时段
        String plannedCompletionTime = simpleDateFormat.format(deviceInspectionTasks.getInspectionDate()).substring(0,10)+" "+inspectionTimePeriod.substring(inspectionTimePeriod.lastIndexOf("-")+1);
        outdoorRecordInspectionResults.setPlannedCompletionTime(sdf.parse(plannedCompletionTime));//计划完成时间
        outdoorRecordInspectionResults.setProblemDescription(BaseContextHandler.getUsername()+"("+BaseContextHandler.getName()+") 备注："+problemDescription);
        dorirBiz.insertSelective(outdoorRecordInspectionResults);
        //更新设施巡检结果状态
        if(ditBiz.getByRouteId(routeId,deviceOutdoorLabel.getId())==false){
            deviceOutdoorLabel.setResultFlag("3");
        }else{
            deviceOutdoorLabel.setResultFlag("0");
        }
        dolBiz.updateSelectiveById(deviceOutdoorLabel);
        //更新任务状态为进行中
        deviceInspectionTasks.setStatus("2");
        ditBiz.updateSelectiveById(deviceInspectionTasks);
        responseResult.setData(outdoorRecordInspectionResults.getId());
        return responseResult;
    }

    @RequestMapping(value = "/getSelected",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("设施类型下拉框")
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
}