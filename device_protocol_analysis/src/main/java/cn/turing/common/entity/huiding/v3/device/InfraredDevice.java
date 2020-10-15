package cn.turing.common.entity.huiding.v3.device;


import lombok.Data;

/**
 * 红外计数器
 * @author TDS
 */
@Data
public class InfraredDevice {
    /**
     * 红外计数器
     */
    private Integer red_value;

    public InfraredDevice(String data){
        red_value=Integer.parseInt(data.substring(0,8),16);
    }
}
