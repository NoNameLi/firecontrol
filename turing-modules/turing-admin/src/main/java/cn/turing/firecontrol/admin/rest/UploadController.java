package cn.turing.firecontrol.admin.rest;

import cn.turing.firecontrol.common.exception.base.BusinessException;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.common.util.UploadUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

//@Controller
//@RequestMapping("upload")
public class UploadController {

//    @PostMapping("qiniu")
//    @ResponseBody
    public ObjectRestResponse<String> qiNiu(MultipartFile file){
        if(file == null || file.isEmpty()){
            throw new ParamErrorException("文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        Integer index = originalFilename.lastIndexOf(".");
        String key = UUIDUtils.generateShortUuid();
        if(index>0){
            key += originalFilename.substring(index);
        }
        try {
            Map<String,String> map = UploadUtil.simpleupload(file.getBytes(),key);
            String url = map.get("url");
            return new ObjectRestResponse<String>().data(url);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }



}
