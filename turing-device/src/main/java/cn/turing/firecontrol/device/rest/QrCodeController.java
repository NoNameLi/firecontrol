package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.device.util.QrCodeUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created on 2019/01/14 10:47
 *
 * @Description
 * @Version V1.0
 */
@RestController
@RequestMapping("qrcode")
public class QrCodeController {

    @GetMapping(value = "/deviceNo")
    @ResponseBody
    @ApiOperation("二维码列表展示")
    @ApiImplicitParam(name = "deviceNo",value = "设备编号",paramType = "query")
    public void getImages(HttpServletResponse response, String deviceNo) throws IOException {
        if(StringUtils.isBlank(deviceNo)){
            throw new ParamErrorException("设备编号不能为空");
        }
        BufferedImage image = QrCodeUtil.createQrCodeBufferedImage(deviceNo,430);
        response.setDateHeader("Expires", 0L);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        out.flush();
    }




}
