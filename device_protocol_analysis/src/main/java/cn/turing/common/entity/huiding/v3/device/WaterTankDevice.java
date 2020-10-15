package cn.turing.common.entity.huiding.v3.device;


import lombok.Data;

/**
 * 水箱水位
 * @author TDS
 */
@Data
public class WaterTankDevice {
    /**
     * 水箱水位
     */
    private String water_value;

    public WaterTankDevice(String data,Double v){
      water_value=String.format("%.2f",0.3125*(Integer.parseInt(data,16)*v*10)/4096-1.25);
    }
}
