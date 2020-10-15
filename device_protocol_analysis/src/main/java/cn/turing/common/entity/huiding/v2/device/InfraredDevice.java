package cn.turing.common.entity.huiding.v2.device;

import cn.turing.common.entity.huiding.v2.parsing.SensorAnalysis;
import lombok.Data;

/**
 * 红外计数器
 * @author TDS
 */
@Data
public class InfraredDevice extends SensorAnalysis {
    /**
     * 红外计数器
     */
    private Integer red_value;

    public InfraredDevice(String data){
        super(data);
        red_value=Integer.parseInt(data.substring(44,52));
    }
}
