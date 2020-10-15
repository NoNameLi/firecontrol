package cn.turing.common.entity.huiding.v3.device;


import lombok.Data;

/**
 * 红外抄表
 */
@Data
public class InfraredMeterReadingDevice {
    /**
     * 功率
     */
    private String power;

    public InfraredMeterReadingDevice(String message){

     String a=String.valueOf(Integer.parseInt(message.substring(0,6),16));
     String b=message.substring(6);
     power=a+"."+b;
    }
}
