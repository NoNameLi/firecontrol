package cn.turing.common.entity.huiding.v2.device;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author TDS
 *
 */
@Data
public class HeartDevice {

    /**
     * 报头
     */
    protected String header;
    /**
     * 长度
     */
    protected int length;
    /**
     * 包类型
     */
    protected String backType;
    /**
     * 设备编号
     */
    protected String deviceCode;
    /**
     * 信号强度
     */
    protected int signalStrength;
    /**
     * 电量
     */
    protected Double electricity;
    /**
     * 包尾
     */
    protected String end;

    public HeartDevice(String data){
        header=data.substring(0,4);
        length=Integer.parseInt(data.substring(4,8),16);
        backType=data.substring(8,12);
        deviceCode=data.substring(12,36);
        signalStrength=Integer.parseInt(data.substring(36,38),16)-256;
        electricity=Integer.parseInt(data.substring(38,42),16)*0.01;
        electricity=new BigDecimal(electricity).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        end=data.substring(data.length()-4);
    }
}
