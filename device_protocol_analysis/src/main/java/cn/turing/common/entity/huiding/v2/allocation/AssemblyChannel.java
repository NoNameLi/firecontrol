package cn.turing.common.entity.huiding.v2.allocation;

import lombok.Data;

/**
 * 配置包
 * @author TDS
 */
@Data
public class AssemblyChannel {
    /**
     * 报头-2个字节（2B）
     */
    public  String header="5555";
    /**
     * 包长度(2B)
     */
    public  String length="";
    /**
     * 设备编号(12B)
     */
    public  String deviceCode="";
    /**
     * 采集周期(4B)
     */
    public  String cycle="";
    /**
     * 包尾（2B）
     */
    public  String tail="AAAA";
}
