package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.device.biz.DeviceUploadInformationBiz;
import cn.turing.firecontrol.device.config.ApplicationStartListener;
import cn.turing.firecontrol.device.entity.DeviceUploadInformation;
import cn.turing.firecontrol.device.util.Constants;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("deviceUploadInformation")
@CheckClientToken
@CheckUserToken
public class DeviceUploadInformationController extends BaseController<DeviceUploadInformationBiz, DeviceUploadInformation,Integer> {

    @Autowired
    private DeviceUploadInformationBiz duiBiz;

    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = "multipart/form-data")
    @ResponseBody
    @ApiOperation("上传文件，保证文件信息到数据表中")
    public ObjectRestResponse saveFileInformation(@RequestParam MultipartFile file, String fileName) {
        ObjectRestResponse restResponse = new ObjectRestResponse();
        if (file.isEmpty() && file.getSize() == 0) {
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_TYPE_ERROR);
        }
        Map map = duiBiz.saveFileInformation(file, fileName);
        restResponse.setData(map);
        return restResponse;
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("批量上传文件")
    public ObjectRestResponse uploadFile(@RequestParam(value = "file", required = false) MultipartFile[] file) {
        ObjectRestResponse restResponse = new ObjectRestResponse();
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        for (int i = 0,len = file.length; i < len; i++) {
            if (!file[i].isEmpty()) {
                String fileName = file[i].getOriginalFilename();
                Map<String,Object> map = duiBiz.uploadFile(file[i], fileName);
                list.add(map);
            }
        }
        restResponse.setData(list);
        return restResponse;
    }

    @ResponseBody
    @RequestMapping(path = "/uploadMp3", method = {RequestMethod.POST})
    @ApiOperation("上传mp3文件")
    public ObjectRestResponse uploadMp3(@RequestParam("file") MultipartFile file) throws Exception {
        //存放在服务临时目录   /mp3
        String path = "/mp3/";
        if (file != null) {// 判断上传的文件是否为空
            String type = null;// 文件类型
            type = "mp3";
            if (type != null) {// 判断文件类型是否为空
                if ("mp3".equals(type)) {
                    String trueFileName = String.valueOf(System.currentTimeMillis()) + "." + type;
                    // 设置存放图片文件的路径
                    path = path+trueFileName;
                    file.transferTo(new File(path));
                }
            }
        }
        return new ObjectRestResponse();
    }



}
