package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.BaseResponse;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.biz.DeviceSensorManufacturerBiz;
import cn.turing.firecontrol.device.biz.DeviceSensorTypeBiz;
import cn.turing.firecontrol.device.biz.DeviceVideoExtBiz;
import cn.turing.firecontrol.device.biz.DeviceVideoGroupBiz;
import cn.turing.firecontrol.device.entity.DeviceSensorManufacturer;
import cn.turing.firecontrol.device.entity.DeviceSensorType;
import cn.turing.firecontrol.device.entity.DeviceVideoExt;
import cn.turing.firecontrol.device.entity.DeviceVideoGroup;
import cn.turing.firecontrol.device.util.POIUtil;
import cn.turing.firecontrol.device.vo.DeviceSensorVo;
import cn.turing.firecontrol.device.vo.VideoDeviceVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created on 2019/02/20 11:37
 *
 * @Description
 * @Version V1.0
 */
@Api(tags = "视频设备")
@RestController
@RequestMapping("deviceVideoExt")
@CheckUserToken
@CheckClientToken
@Slf4j
public class DeviceVideoExtController extends BaseController<DeviceVideoExtBiz, DeviceVideoExt,Long> {

    @Autowired
    private DeviceVideoExtBiz deviceVideoExtBiz;
    @Autowired
    private DeviceSensorTypeBiz deviceSensorTypeBiz;
    @Autowired
    private DeviceVideoGroupBiz deviceVideoGroupBiz;
    @Autowired
    private DeviceSensorManufacturerBiz deviceSensorManufacturerBiz;


    @GetMapping(value = "queryById")
    @ApiOperation("查询设备信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "设备ID",paramType = "query",required = true)
    })
    public ObjectRestResponse queryById(Long id){
        if(id == null){
            throw new ParamErrorException("设备ID不能为空");
        }
        DeviceSensorVo vo = deviceVideoExtBiz.getById(id);
        if(vo == null){
            throw new ParamErrorException("设备不存在");
        }
        return new ObjectRestResponse().data(vo);
    }

    @GetMapping(value = "getVideoDeviceDetail")
    @ApiOperation("查询设备信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "设备ID",paramType = "query",required = true)
    })
    public ObjectRestResponse getVideoDeviceDetail(Long id){
        if(id == null){
            throw new ParamErrorException("设备ID不能为空");
        }
        VideoDeviceVo vo = deviceVideoExtBiz.getVideoDeviceDetail(id);
        return new ObjectRestResponse().data(vo);
    }



    @PostMapping(value = "save")
    @ApiOperation("添加视频设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name="groupId",value = "设备组ID",paramType = "query",required = true),
            @ApiImplicitParam(name="sensorNo",value = "设备编号",paramType = "query",required = true),
            @ApiImplicitParam(name="deviceValidateCode",value = "设备验证码",paramType = "query",required = true),
            @ApiImplicitParam(name="channelNo",value = "通道号",paramType = "query",required = true),
            @ApiImplicitParam(name="manufacturerName",value = "厂商名称",paramType = "query",required = true),
            @ApiImplicitParam(name="equipmentType",value = "设备系列",paramType = "query",required = true),
            @ApiImplicitParam(name="model",value = "设备型号",paramType = "query",required = true),
            @ApiImplicitParam(name="deviceName",value = "设备名称",paramType = "query",required = true),
            @ApiImplicitParam(name="positionSign",value = "标记位置",paramType = "query")
    })
    public ObjectRestResponse saveDevice(Integer groupId,String sensorNo,String deviceValidateCode,@RequestParam(defaultValue = "1") Integer channelNo,String manufacturerName,
                                         String equipmentType, String model,String deviceName, String positionSign){
        Boolean hasBlank = ValidatorUtils.hasAnyBlank(groupId,sensorNo,deviceValidateCode,channelNo,manufacturerName,equipmentType,model,deviceName);
        if(hasBlank){
            throw new ParamErrorException("参数不能为空");
        }
        if(channelNo < 1 || channelNo > 64){
            throw new ParamErrorException("通道号仅支持输入1-64之间的整数");
        }
        DeviceVideoGroup videoGroup = deviceVideoGroupBiz.selectById(groupId);
        if(videoGroup == null){
            throw new ParamErrorException("设备组不存在");
        }
        /*manufacturerName = StringEscapeUtils.unescapeHtml4(manufacturerName); //html转义
        model = StringEscapeUtils.unescapeHtml4(model);
        equipmentType =  StringEscapeUtils.unescapeHtml4(equipmentType);*/
        DeviceSensorManufacturer manufacturer =  deviceSensorManufacturerBiz.selecteByName(manufacturerName);
        if(manufacturer == null){
            throw new ParamErrorException("设备厂商不存在");
        }
        List<DeviceSensorType> sensorTypes = deviceSensorTypeBiz.selectByType(manufacturerName,model,equipmentType);
        if(sensorTypes == null || sensorTypes.isEmpty()){
            throw new ParamErrorException("设备型号不存在");
        }
        DeviceSensorType sensorType = sensorTypes.get(0);
        DeviceVideoExt entity = new DeviceVideoExt();
        entity.setSensorNo(sensorNo + ":" + channelNo);
        entity.setShowFlag("0");
        entity.setDeviceValidateCode(deviceValidateCode);
        entity.setDeviceGroupId(groupId);
        entity.setDeviceVideoName(deviceName);
        entity.setDelFlag("0");
        entity.setChannelId(sensorType.getChannelId());
        entity.setSensorTypeId(sensorType.getId());
        entity.setPositionSign(positionSign);
        try {
            entity = baseBiz.saveDevices(entity);
        }catch (Exception e){
            throw new ParamErrorException(e.getMessage());
        }
        return new ObjectRestResponse().data(entity);
    }


    @PostMapping("update")
    @ApiOperation("修改视频设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "设备ID",paramType = "query",required = true),
            @ApiImplicitParam(name="manufacturerName",value = "厂商名",paramType = "query",required = true),
            @ApiImplicitParam(name="equipmentType",value = "设备系列",paramType = "query",required = true),
            @ApiImplicitParam(name="model",value = "设备型号",paramType = "query",required = true),
            @ApiImplicitParam(name="deviceName",value = "设备名称",paramType = "query",required = true),
            @ApiImplicitParam(name="positionSign",value = "标记位置",paramType = "query")
    })
    public ObjectRestResponse updateDevice(Long id,String manufacturerName,String equipmentType, String model,String deviceName,String positionSign){
        Boolean hasBlank = ValidatorUtils.hasAnyBlank(id,manufacturerName,equipmentType,model,deviceName);
        if(hasBlank){
            throw new ParamErrorException("参数不能为空");
        }
        /*manufacturerName = StringEscapeUtils.unescapeHtml4(manufacturerName); //html转义
        model = StringEscapeUtils.unescapeHtml4(model);
        equipmentType =  StringEscapeUtils.unescapeHtml4(equipmentType);*/
        DeviceSensorManufacturer manufacturer =  deviceSensorManufacturerBiz.selecteByName(manufacturerName);
        if(manufacturer == null){
            throw new ParamErrorException("设备厂商不存在");
        }
        List<DeviceSensorType> sensorTypes = deviceSensorTypeBiz.selectByType(manufacturerName,model,equipmentType);
        if(sensorTypes == null || sensorTypes.isEmpty()){
            throw new ParamErrorException("设备型号不存在");
        }
        DeviceSensorType sensorType = sensorTypes.get(0);
        DeviceVideoExt queryEntity = new DeviceVideoExt();
        queryEntity.setId(id);
        List<DeviceVideoExt> entitys = baseBiz.queryOnlyExt(queryEntity);
        if(entitys == null || entitys.isEmpty()){
            throw new ParamErrorException("设备不存在");
        }
        DeviceVideoExt entity = new DeviceVideoExt();
        entity.setId(id);
        entity.setDeviceVideoName(deviceName);
        entity.setSensorTypeId(sensorType.getId());
        entity.setPositionSign(positionSign);
        try {
            baseBiz.updateSelectiveById(entity);
        }catch (Exception e){
            log.error("更新设备失败",e);
            throw new ParamErrorException(e.getMessage());
        }
        ObjectRestResponse res = new ObjectRestResponse();
        res.setStatus(200);
        res.setMessage("修改成功");
        return res;
    }

    @PostMapping("importDevices")
    @ApiOperation("批量导入视频设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name="file",value = "Excel文件",paramType = "query",required = true),
            @ApiImplicitParam(name="channelId",value = "所属系统ID",paramType = "query",required = true),
            @ApiImplicitParam(name="deviceGroupId",value = "设备组ID",paramType = "query",required = true)
    })
    @Transactional
    public BaseResponse importDevices(@RequestParam(value = "file") MultipartFile file,Integer channelId, Integer deviceGroupId){
        if(ValidatorUtils.hasAnyBlank(file,channelId,deviceGroupId)){
            throw new ParamErrorException("参数不能为空");
        }
        if(file.getSize()>10*1024*1024){
            throw new ParamErrorException("文件大小不能超过10M!");
        }
        String filename = file.getOriginalFilename();
        if(!filename.endsWith("xls") && !filename.endsWith("xlsx")){
            throw new ParamErrorException("只支持上传Excel文件");
        }
        InputStream inputStream = null;
        List<DeviceVideoExt> devices = new ArrayList<>();
        List<String> sensorNos = new ArrayList<>();
        try {
            inputStream = file.getInputStream();
            List<String[]> table = null;
            try {
                table = new POIUtil().readExcel(filename,inputStream);
            }catch (Exception e){
                throw new RuntimeException("文件错误");
            }
            if(table == null || table.size() <= 1){
                throw new ParamErrorException("文件内容为空");
            }
            //遍历数据
            DeviceVideoExt device = null;
            String[] row = null;
            DeviceSensorType sensorType = null;
            DeviceSensorType deviceSensorType = null;
            String[] shouldTitles = {"1","设备编号（必填）","验证码（必填）","通道号（必填，默认1）","类型编号（必填）","设备名（必填）"};
            for(int i=0; i< table.size(); i++){
                row = table.get(i);
                //检验模板正确性
                if(i == 0){
                    if(!Arrays.equals(row,shouldTitles)){
                        throw new ParamErrorException("Excel模板错误!");
                    }
                    continue;
                }
                if(shouldTitles.length != row.length || ValidatorUtils.hasAnyBlank(row)){
                    throw new ParamErrorException("第" + (i + 1) + "行为空，无法导入");
                }
                //写入设备信息
                try {
                    device = new DeviceVideoExt();
                    row = table.get(i);
                    if(!StringUtils.isNumeric(row[3]) || Integer.valueOf(row[3]) < 1 || Integer.valueOf(row[3]) > 64){
                        throw new ParamErrorException("通道号仅支持输入1-64之间的整数");
                    }
                    device.setSensorNo(row[1] + ":" + row[3]);
                    device.setShowFlag("0");
                    device.setDelFlag("0");
                    device.setDeviceValidateCode(row[2]);
                    device.setSensorTypeId(new Integer(row[4]));
                    sensorType =  deviceSensorTypeBiz.getById(device.getSensorTypeId());
                    if(sensorType == null){
                        throw new ParamErrorException("类型编号不存在");
                    }
                    device.setDeviceVideoName(row[5]);
                    device.setChannelId(channelId);
                    device.setDeviceGroupId(deviceGroupId);
                    device.setStatus("3");//默认未启用
                    devices.add(device);
                    sensorNos.add(device.getSensorNo());
                }catch (Exception e){
                    throw new ParamErrorException("第" + (i + 1) + "行:" + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.info("导入设备失败",e);
            throw new ParamErrorException("导入设备失败:" + e.getMessage());
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.info("导入设备失败",e);
                throw new ParamErrorException("导入设备失败:" + e.getMessage());
            }
        }
        Set<String> sensorNoSet = new HashSet<>(sensorNos);
        sensorNos.removeAll(sensorNoSet);
        StringBuilder sb = new StringBuilder();
        if(!sensorNos.isEmpty()){
            for(int i =0; i<sensorNos.size(); i++){
                sb.append(sensorNos.get(i));
                if(i != sensorNos.size() -1){
                    sb.append(",");
                }
            }
            throw new ParamErrorException("文件中设备编号" + sb.toString() +"存在重复");
        }
        baseBiz.saveDevices(devices);
        return new BaseResponse();
    }

    //删除设备
    @GetMapping("deleteDevices")
    @ApiOperation("批量删除视频设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name="ids",value = "设备ID",paramType = "query",required = true)
    })
    public BaseResponse deleteDevice(Long[] ids){
        if(ids == null){
            throw new ParamErrorException("设备ID不能为空");
        }
        try{
            for(Long id : ids){
                baseBiz.deleteById(id);
            }
            return new BaseResponse();
        }catch (Exception e){
            throw new ParamErrorException(e.getMessage());
        }
    }

    @GetMapping("queryList")
    @ApiOperation("分页查询设备信息(设备管理)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码（默认1）", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页显示条数（默认10）", paramType = "query"),
            @ApiImplicitParam(name = "groupId", value = "设备组ID（空则查询全部）", paramType = "query"),
            @ApiImplicitParam(name = "sensorNo", value = "设备编号（模糊查询）（空则查询全部）", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "设备状态（0=故障/1=报警/2=正常）（空则查询全部）", paramType = "query"),
            @ApiImplicitParam(name = "deviceName", value = "设备名（模糊查询）（空则查询全部）", paramType = "query"),
            @ApiImplicitParam(name = "manufacturer", value = "厂商名称", paramType = "query"),
            @ApiImplicitParam(name = "equipmentType", value = "系列（空则查询全部）", paramType = "query"),
            @ApiImplicitParam(name = "model", value = "型号（空则查询全部）", paramType = "query"),
            @ApiImplicitParam(name = "isMark", value = "是否标记（空则查询全部）", paramType = "query")
    })
    public TableResultResponse<Map<String,Object>> queryByGroupId(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "15") Integer limit, Integer groupId,
                                              String sensorNo, String status, String deviceName, String manufacturer, String equipmentType, String model, String isMark){
        try {
            if("全部".equals(manufacturer)){
                manufacturer = null;
            }
            if("全部".equals(equipmentType)){
                equipmentType = null;
            }
            Page<Map<String,Object>> pageData = deviceVideoExtBiz.queryByPage(page,limit,groupId,sensorNo,status,deviceName,manufacturer,equipmentType,model,isMark);
            return new TableResultResponse(pageData.getTotal(),pageData.getResult());
        }catch (Exception e){
            throw new ParamErrorException(e.getMessage());
        }
    }

    @GetMapping("queryShowDevice")
    @ApiOperation("分页查询需要显示的设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码（默认1）", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页显示条数（默认10）", paramType = "query")
    })
    public TableResultResponse<Map<String,Object>> queryShowDevice(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "15") Integer limit){
        try{
            Page<Map<String,Object>> pageData = baseBiz.queryShowDevice(page,limit);
            return new TableResultResponse<>(pageData.getTotal(),pageData.getResult());
        }catch (Exception e){
            throw new ParamErrorException(e.getMessage());
        }
    }


    @PostMapping("updateShowStatus")
    @ApiOperation("修改视频设备的显示状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "设备ID（多个用逗号隔开）,为空则修改所有视频设备的状态", paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "待修改的状态", paramType = "query")
    })
    public BaseResponse updateShowStatus(Long[] ids, Boolean isShow){
        if(ValidatorUtils.hasAnyBlank(isShow)){
            throw new ParamErrorException("参数不能为空");
        }
        try{
            baseBiz.updateDeviceShowStatus(ids,isShow);
            return new BaseResponse();
        }catch (Exception e){
            throw new ParamErrorException(e.getMessage());
        }
    }

    /**
     *
     * @param hasSolution
     * @param isShow
     * @return
     */
    @GetMapping("queryAll")
    @ApiOperation("查询所有设备(非树形结构)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hasSolution", value = "是否已配置分析方案", paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示", paramType = "query"),
            @ApiImplicitParam(name = "groupId", value = "设备组ID", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页数量", paramType = "query")
    })
    public TableResultResponse queryDevices(Boolean hasSolution, Boolean isShow,Integer groupId, @RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "15") Integer limit){
        try {
            Page pageInfo = PageHelper.startPage(page,limit);
            List<Map<String,Object>> list = baseBiz.queryDevices(isShow,hasSolution,groupId);
            //将直播地址JSON字符串转成Map
            for(Map<String,Object> map : list){
                Object liveAddress = map.get("liveAddress");
                if(liveAddress != null && StringUtils.isNotBlank(liveAddress.toString())){
                    map.put("liveAddress", JSONObject.parseObject(liveAddress.toString()));
                }
                Object alarmMsg = map.get("alarmMsg");
                if(alarmMsg!= null && StringUtils.isNotBlank(alarmMsg.toString())){
                    map.put("alarmMsg", JSONArray.parseArray(alarmMsg.toString()));
                }
                String[] sensorNoAndChannelNo = map.get("sensorNo").toString().split(":");
                map.put("sensorNo",sensorNoAndChannelNo[0]);
                map.put("channelNo",sensorNoAndChannelNo.length >= 2 ? sensorNoAndChannelNo[1] : "1");
            }
            return new TableResultResponse(pageInfo.getTotal(),list);
        }catch (Exception e){
            throw new ParamErrorException(e.getMessage());
        }
    }


    @PostMapping("configSolution")
    @ApiOperation("配置分析方案")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "solutionId", value = "方案ID（为空则为取消配置）", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "设备ID", paramType = "query", required = true)
    })
    public BaseResponse configSolution(Integer solutionId,Long deviceId){
        if(deviceId == null){
            throw new ParamErrorException("设备ID不能为空");
        }
        try {
            baseBiz.configSolution(solutionId,deviceId);
            return new BaseResponse();
        }catch (Exception e){
            throw new ParamErrorException(e.getMessage());
        }
    }

    @GetMapping("monitorUrl")
    @ApiOperation("获取监控地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID", paramType = "query", required = true),
            @ApiImplicitParam(name = "isHd", value = "是否高清（true/false）", paramType = "query", required = true),
            @ApiImplicitParam(name = "dateStrs", value = "开始和结束时间（yyyy-MM-dd HH:mm:ss,yyyy-MM-dd HH:mm:ss）", paramType = "query", required = true)
    })
    public  ObjectRestResponse<Map<String,String>> getMonitorUrl(Long deviceId,Boolean isHd,String dateStrs){
        if(ValidatorUtils.hasAnyBlank(deviceId,isHd)){
            throw new ParamErrorException("参数不能为空");
        }
        Date begin = null,end = null;
        if(dateStrs != null){
            String[] dates = dateStrs.split(",");
            if(dates.length == 2){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    begin = dateFormat.parse(dates[0]);
                    end = dateFormat.parse(dates[1]);
                } catch (ParseException e) {
                    log.error("获取监控地址失败",e);
                    throw new ParamErrorException("时间格式错误");
                }
            }
        }
        try {
            Map<String,String> map = baseBiz.getMonitorUrl(deviceId,isHd,begin,end);
            return new ObjectRestResponse<>().data(map);
        }catch (Exception e){
            log.error("获取监控地址失败",e);
            throw new ParamErrorException(e.getMessage());
        }
    }

    @GetMapping("getImage")
    @ApiOperation("获取设备截图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID", paramType = "query", required = true)
    })
    public ObjectRestResponse<String> getImage(Long deviceId){
        if(deviceId == null){
            throw new ParamErrorException("参数不能为空");
        }
        try {
            String image = baseBiz.getImage(deviceId);
            return new ObjectRestResponse<>().data(image);
        }catch (Exception e){
            log.error("截图失败",e);
            throw new ParamErrorException(e.getMessage());
        }
    }

    @GetMapping("test")
    public ObjectRestResponse test(){
        ObjectRestResponse response = new ObjectRestResponse();
        int[] arr = baseBiz.countVideoSensor();
        response.data(arr);
        return response;
    }



}
