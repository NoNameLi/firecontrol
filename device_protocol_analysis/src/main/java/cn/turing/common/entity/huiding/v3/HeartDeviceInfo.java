package cn.turing.common.entity.huiding.v3;

import lombok.Data;

/**
 * 心跳包
 */
@Data
public class HeartDeviceInfo {
    /**
     * 报头
     */
    private String head;
    /**
     * 长度，不包含报头
     */
    private String length;
    /**
     * 数据类型/包类型：0100 :4G心跳 0200：nb心跳  0001 A通道上行 0002 B通道上行
     * 0003 C通道上行
     */
    private String messageType;
    /**
     * 设备id
     */
    private String deviceCode;
    /**
     * 信号强度
     */
    private String sign;
    /**
     * 电量
     */
    private String vol;
    /**
     * 包尾
     */
    private String end;


    public  HeartDeviceInfo(String data){
        head=data.substring(0,4);
        length=String.valueOf(Integer.parseInt(data.substring(4,8),16));
        messageType=data.substring(8,12);
        deviceCode=data.substring(12,36);
        sign=String.valueOf(Integer.parseInt(data.substring(36,38),16)-256);
        vol=String.format("%.2f",(Integer.parseInt(data.substring(38,42),16)*0.001));
        end=data.substring(42);

    }
}
