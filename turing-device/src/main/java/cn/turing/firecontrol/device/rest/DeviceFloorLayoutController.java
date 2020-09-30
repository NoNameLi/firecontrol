package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.UploadUtil;
import cn.turing.firecontrol.device.biz.DeviceFloorLayoutBiz;
import cn.turing.firecontrol.device.biz.DeviceSensorBiz;
import cn.turing.firecontrol.device.entity.DeviceFloorLayout;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import cn.turing.firecontrol.device.util.Constants;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("deviceFloorLayout")
@CheckClientToken
@CheckUserToken
public class DeviceFloorLayoutController extends BaseController<DeviceFloorLayoutBiz,DeviceFloorLayout,Integer> {

    @Autowired
    private DeviceFloorLayoutBiz dflBiz;
    @Autowired
    private DeviceSensorBiz dsBiz;

    @RequestMapping(value = "/uploadImage",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("根据绑定的建筑，上传对应平面图")
    public ObjectRestResponse uploadImage(@RequestParam Integer buildingId, @RequestParam(value = "file", required = false) MultipartFile file,@RequestParam Integer floor,@RequestParam String fileName) throws UnsupportedEncodingException {
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(file==null || buildingId==null || floor==null){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
//             String fileName = file.getOriginalFilename();
            List<DeviceFloorLayout> lists =  dflBiz.selectFloorLayout(buildingId,floor);
            try {
                //上传图片
                Map map = UploadUtil.simpleupload(file.getBytes(),UUID.randomUUID().toString());
                if(map.get("result").equals("fail")){
                    throw new RuntimeException(Constants.FILE_UPLOAD_ERROR);
                }else {
                    //获得图片路径
                    String savePath = map.get("url").toString()+"?imageView2/0/w/1000";
                    String name = fileName.substring(0,fileName.lastIndexOf("."));
                    String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
                    //封装属性
                    if(lists==null||lists.size()==0){
                        DeviceFloorLayout deviceFloorLayout = new DeviceFloorLayout();
                        deviceFloorLayout.setFileName(name);
                        deviceFloorLayout.setFileFloor(floor);//对应楼层
                        deviceFloorLayout.setFileType(fileType);
                        deviceFloorLayout.setBuildId(buildingId);
                        deviceFloorLayout.setFilePath(savePath);
                        deviceFloorLayout.setDelFlag("0");
                        //上传图片
                        dflBiz.insertSelective(deviceFloorLayout);
                    }else {
                        DeviceFloorLayout deviceFloorLayout = lists.get(0);
                        deviceFloorLayout.setFileName(name);
                        deviceFloorLayout.setFileType(fileType);
                        deviceFloorLayout.setFilePath(savePath);
                        deviceFloorLayout.setDelFlag("0");
                        //重新上传平面图
                        dflBiz.updateSelectiveById(deviceFloorLayout);

                        //重新上传平面图后，对应修改平面图上的打点为未标记
                        List<DeviceSensor> deviceSensors = dsBiz.getSensorStatusByBuildAndFloor(buildingId,floor,null);
                        for(int i=0;i<deviceSensors.size();i++){
                            DeviceSensor deviceSensor = new DeviceSensor();
                            deviceSensor.setId(deviceSensors.get(i).getId());
                            deviceSensor.setPositionSign("");
                            dsBiz.updateSelectiveById(deviceSensor);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return responseResult;
    }

    @RequestMapping(value = "/selectImage",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据建筑物id、对应楼层，查询对应楼层的平面图")
    public ObjectRestResponse selectFloorLayout(@RequestParam Integer buildingId, Integer floor){
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(buildingId==null ){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            List<DeviceFloorLayout> list = dflBiz.selectFloorLayout(buildingId, floor);
            responseResult.setData(list);
        }
        return responseResult;
    }

    @RequestMapping(value = "/deleteImage",method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation("根据建筑物id、对应楼层，删除对应楼层的平面图")
    public ObjectRestResponse deleteImage(@RequestBody Map<String,Integer> params){
        Integer buildingId = params.get("buildingId");
        Integer floor = params.get("floor");
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if(buildingId==null || floor==null ){
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else{
            List<DeviceFloorLayout> list = dflBiz.selectFloorLayout(buildingId, floor);
            if(list.size()==0){
                throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
            }else{
                for (DeviceFloorLayout deviceFloorLayout:list) {
                    //删除七牛云的图片
//                    UploadUtil.deleteImg(deviceFloorLayout.getFileName());
                    //删除楼层平面图
                    dflBiz.deleteById(deviceFloorLayout.getId());
                    //删除平面图后，对应修改平面图上的打点为未标记
                    List<DeviceSensor> deviceSensors = dsBiz.getSensorStatusByBuildAndFloor(buildingId,floor,null);
                    for(int i=0;i<deviceSensors.size();i++){
                        DeviceSensor deviceSensor = new DeviceSensor();
                        deviceSensor.setId(deviceSensors.get(i).getId());
                        deviceSensor.setPositionSign("");
                        dsBiz.updateSelectiveById(deviceSensor);
                    }
                }
            }
        }
        return responseResult;
    }
}