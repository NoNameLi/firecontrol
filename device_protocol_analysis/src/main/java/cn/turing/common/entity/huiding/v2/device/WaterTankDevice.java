package cn.turing.common.entity.huiding.v2.device;

import cn.turing.common.entity.huiding.v2.parsing.SensorAnalysis;

/**
 * 水箱水位
 * @author TDS
 */
public class WaterTankDevice extends SensorAnalysis {
    /**
     * 水箱水位
     */
    private String water_value;

    public WaterTankDevice(String data){
        super(data);
        water_value=String.valueOf(Integer.parseInt(data.substring(44,48),16));
    }
}
