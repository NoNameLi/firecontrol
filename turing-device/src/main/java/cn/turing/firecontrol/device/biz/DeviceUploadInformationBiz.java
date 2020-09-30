package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.util.UploadUtil;
import cn.turing.firecontrol.device.entity.DeviceUploadInformation;
import cn.turing.firecontrol.device.mapper.DeviceUploadInformationMapper;
import cn.turing.firecontrol.device.util.Constants;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
public class DeviceUploadInformationBiz extends BusinessBiz<DeviceUploadInformationMapper, DeviceUploadInformation> {

    private static Logger logger = Logger.getLogger(DeviceUploadInformationBiz.class);

    @Autowired
    private DeviceUploadInformationMapper deviceUploadInformationMapper;

    public DeviceUploadInformation selectByIdTemp(Integer id){
        return mapper.selectByIdTemp(id);
    }

    //上传文件并保存信息到文件信息表中
    public Map saveFileInformation(MultipartFile file,String fileName){
        Map resultmap = new HashMap();
        try{
            //上传文件
            Map map = UploadUtil.simpleupload(file.getBytes(), UUID.randomUUID().toString());
            if(map.get("result").equals("fail")){
                throw new RuntimeException(Constants.FILE_UPLOAD_ERROR);
            }
            if(map.get("result").equals("success")){
                //保持文件信息到信息表中
                DeviceUploadInformation deviceUploadInformation = new DeviceUploadInformation();
                //String fileName = file.getOriginalFilename();//文件名
                deviceUploadInformation.setFileName(UUID.randomUUID().toString());
                deviceUploadInformation.setFilePath(map.get("url")+"?imageView2/0/w/60");
                String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);//文件类型
                deviceUploadInformation.setFileType(fileType);
                deviceUploadInformation.setFileSize(file.getInputStream().available());
                deviceUploadInformation.setDelFlag("0");
                deviceUploadInformation.setCrtTime(new Date());
                deviceUploadInformationMapper.insert(deviceUploadInformation);
                resultmap.put("url",deviceUploadInformation.getFilePath());
            }
        }catch (Exception e){
            logger.error(e);
        }
        return resultmap;
    }

    public Map uploadFile(MultipartFile file, String fileName) {
        Map<String,Object> resultmap = new HashMap<String,Object>();
        InputStream inputStream = null;
        try{
            //上传文件
            Map map = UploadUtil.simpleupload(file.getBytes(), UUID.randomUUID().toString());
            if(map.get("result").equals("fail")){
                throw new RuntimeException(Constants.FILE_UPLOAD_ERROR);
            }
            if(map.get("result").equals("success")){
                inputStream = file.getInputStream();
                //保持文件信息到信息表中
                DeviceUploadInformation deviceUploadInformation = new DeviceUploadInformation();
                deviceUploadInformation.setFileName(UUID.randomUUID().toString());
                deviceUploadInformation.setFilePath(map.get("url").toString());
                String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);//文件类型
                deviceUploadInformation.setFileType(fileType);
                deviceUploadInformation.setFileSize(inputStream.available());
                deviceUploadInformation.setDelFlag("0");
                deviceUploadInformation.setCrtTime(new Date());
                deviceUploadInformationMapper.insert(deviceUploadInformation);
                resultmap.put("url",deviceUploadInformation.getFilePath());
                resultmap.put("id",deviceUploadInformation.getId());
            }
        }catch (Exception e){
            logger.error(e);
        }finally {
            IOUtils.closeQuietly(inputStream);
        }
        return resultmap;
    }
}
