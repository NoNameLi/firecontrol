package cn.turing.firecontrol.server.entity.huiding;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class CChannelSensor {
      private Integer c_deviceId;//设备地址
      private String orderCode;//指令码
      private String counter;//计数器
    public CChannelSensor(String bdata) {
        try {
            c_deviceId = Integer.parseInt(bdata.substring(8, 10), 16);
        }catch (Exception e){
            c_deviceId=0;
            log.error(e.toString());
        }
        try{
            orderCode = bdata.substring(10, 12);
        }catch (Exception e){
            orderCode="0";
            log.error(e.toString());
        }
       try{
           counter =String.valueOf(Integer.parseInt(bdata.substring(14, 22))) ;
       }catch (Exception e){
           counter="无效数据";
           log.error(e.toString());
       }


    }

}
