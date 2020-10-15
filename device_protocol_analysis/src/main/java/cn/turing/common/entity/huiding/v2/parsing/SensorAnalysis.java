package cn.turing.common.entity.huiding.v2.parsing;

import lombok.Data;

import java.math.BigDecimal;


/**
 * 上传数据
 * @author TDS
 */
@Data
public class SensorAnalysis {
    /**
     * 报头
     */
    protected String header;
    /**
     * 长度
     */
    protected int length;
    /**
     * 通道类型
     */
    protected String channelType;
    /**
     * 设备类型
     */
    protected String deviceType;
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

    public SensorAnalysis(String data){
        header=data.substring(0,4);
        length=Integer.parseInt(data.substring(4,8),16);
        channelType=data.substring(8,12);
        deviceType=data.substring(12,14);
        deviceCode=data.substring(14,38);
        signalStrength=Integer.parseInt(data.substring(38,40),16)-256;
        electricity=Integer.parseInt(data.substring(40,44),16)*0.001;
        electricity=new BigDecimal(electricity).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        end=data.substring(data.length()-4);
    }
}
