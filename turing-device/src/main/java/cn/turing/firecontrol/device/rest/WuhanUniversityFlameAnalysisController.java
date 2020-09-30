package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.exception.base.BusinessException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.device.biz.DeviceSensorBiz;
import cn.turing.firecontrol.device.biz.DeviceVideoExtBiz;
import cn.turing.firecontrol.device.dto.NoticeRequest;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import cn.turing.firecontrol.device.entity.DeviceVideoExt;
import cn.turing.firecontrol.device.feign.AdminFeign;
import cn.turing.firecontrol.device.task.DeviceVideoTask;
import cn.turing.firecontrol.device.util.BaseUtil;
import cn.turing.firecontrol.device.util.Constants;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Description 对接武大火焰分析算法
 * @Author hanyong
 * @Date 2019/07/06 10:36
 * @Version V1.0
 */
@RestController
@RequestMapping("/whu/flameAnalysis")
@Api(tags = "对接武大火焰分析算法")
@Slf4j
public class WuhanUniversityFlameAnalysisController {

    @Autowired
    private DeviceVideoTask deviceVideoTask;
    @Autowired
    private DeviceVideoExtBiz deviceVideoExtBiz;
    @Autowired
    private DeviceSensorBiz deviceSensorBiz;
    @Autowired
    private AdminFeign adminFeign;

    @GetMapping("newVideos")
    @ApiOperation("获取视频直播地址")
    public  ObjectRestResponse<List<Map<String,Object>>> getNewVideos(@RequestParam(defaultValue = "1")Integer pageNum,@RequestParam(defaultValue = "20")Integer pageSize,String tenantNo){
        log.info("开始获取视频直播地址,站点编码:{}",tenantNo);
        List<Map<String,Object>> list = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        String tenantId="";
        if(StringUtils.isNotBlank(tenantNo)){
            JSONObject jsonObject = adminFeign.getByTenantNo(tenantNo);
            log.info("admin服务返回信息:{}",jsonObject.toJSONString());
            if(jsonObject!=null && jsonObject.getJSONObject("data")!=null){
                tenantId=jsonObject.getJSONObject("data").getString("id");
            }
        }
        List<DeviceVideoExt> exts =  deviceVideoExtBiz.getAllToAnalysisDevices(Constants.AnalysisSolution.FIRE.getCode(),tenantId);
        exts.parallelStream().forEach(device -> {
            Map<String,Object> video = new HashMap<>();
            video.put("sensorNo",device.getSensorNo());
            String address = device.getVideoLiveAddress();
            if(StringUtils.isNotBlank(address)){
                video.put("liveUrl",JSONObject.parseObject(address));
            }
            list.add(video);
        });
        return new ObjectRestResponse<>().data(list);
    }


    @GetMapping("notice")
    public ObjectRestResponse receiveNotice(String sensorNo){
        if(StringUtils.isBlank(sensorNo)){
            throw new BusinessException("设备编号不能为空");
        }
        DeviceVideoExt entity = new DeviceVideoExt();
        entity.setSensorNo(sensorNo);
        List<DeviceVideoExt> entities = deviceVideoExtBiz.queryOnlyExt(entity);
        if(entities.size() == 0){
            throw new BusinessException("设备不存在");
        }
        entity = entities.get(0);
        DeviceSensor sensor = deviceSensorBiz.selectBySensorNo(sensorNo);
        deviceVideoTask.handlerAlarm(sensor,new Date(),"火警",entity,null,null, Constants.AnalysisSolution.FIRE);
        return new ObjectRestResponse();
    }


    //报警并上传图片
    @Deprecated
    @PostMapping("notice_image")
    public ObjectRestResponse noticeAndImage(@RequestBody NoticeRequest request){
        log.info("接收参数................编号{}",request.getSensorNo());
        if(StringUtils.isBlank(request.getSensorNo())){
            throw new BusinessException("设备编号不能为空");
        }
        DeviceVideoExt entity = new DeviceVideoExt();
        entity.setSensorNo(request.getSensorNo());
        List<DeviceVideoExt> entities = deviceVideoExtBiz.queryOnlyExt(entity);
        if(entities.size() == 0){
            throw new BusinessException("设备不存在");
        }
        entity = entities.get(0);
        DeviceSensor sensor = deviceSensorBiz.selectBySensorNo(request.getSensorNo());

        String imageUrl = BaseUtil.GenerateAndUpload(request.getBase64str(),"/data/application/",request.getSensorNo());
        log.info("解析图片成功，imageUrl：{}",imageUrl);
        deviceVideoTask.handlerAlarm(sensor,new Date(),"火警",entity,imageUrl,null, Constants.AnalysisSolution.FIRE);
        log.info("完成...............");
        return new ObjectRestResponse();
    }




}
