package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.device.biz.DeviceNoticeBiz;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("test")
public class TestController extends BaseController<DeviceNoticeBiz,DeviceNotice,Integer> {

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