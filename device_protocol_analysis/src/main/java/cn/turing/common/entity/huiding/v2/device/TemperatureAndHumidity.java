package cn.turing.common.entity.huiding.v2.device;

import cn.turing.common.entity.huiding.v2.parsing.SensorAnalysis;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class TemperatureAndHumidity extends SensorAnalysis {
    /**
     * 温度
     */
    private String temp_value;
    /**
     * 湿度
     */
    private String hum_value;

    public TemperatureAndHumidity(String data){
        super(data);
        BigDecimal n = new BigDecimal(String.valueOf((short)(Integer.valueOf(data.substring(44,48), 16) & 0xffff)*0.1)).setScale(2, RoundingMode.HALF_UP);
        hum_value =String.valueOf(n);
        BigDecimal n1 = new BigDecimal(String.valueOf((short)(Integer.valueOf(data.substring(48,52),16)&0xffff)*0.1)).setScale(2, RoundingMode.HALF_UP);
        temp_value=String.valueOf(n1);
    }
}
