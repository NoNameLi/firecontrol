package cn.turing.common.entity.huiding.v2.device;


import cn.turing.common.entity.huiding.v2.parsing.SensorAnalysis;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 喷淋水压
 * @author TDS
 *
 */
@Data
public class SprayDevice extends SensorAnalysis {

    private double spray_value;

    public SprayDevice(String data){
        super(data);
        Integer value=Integer.parseInt(data.substring(44,48),16);
        double e=value*0.001;
        double t=e*1.25;
        double d=t-0.5;
        spray_value=(new BigDecimal(d).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
    }
}
