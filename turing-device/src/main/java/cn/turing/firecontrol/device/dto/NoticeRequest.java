package cn.turing.firecontrol.device.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class NoticeRequest {
   private String sensorNo ;
   private String base64str;
}
