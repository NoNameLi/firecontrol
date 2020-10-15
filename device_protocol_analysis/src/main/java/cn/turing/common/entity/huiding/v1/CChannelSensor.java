package cn.turing.common.entity.huiding.v1;

import lombok.Data;

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
        }
        try{
            orderCode = bdata.substring(10, 12);
        }catch (Exception e){
            orderCode="0";
        }
       try{
           counter =String.valueOf(Integer.parseInt(bdata.substring(14, 22))) ;
       }catch (Exception e){
           counter="无效数据";
       }


    }

}
