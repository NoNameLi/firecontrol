package cn.turing.common.entity.huiding.v2.allocation;

import lombok.Data;

/**
 * @author TDS
 */
@Data
public class AssemblyB extends AssemblyChannel{
    /**
     * 包头-（1b）
     */
    public  String header="55";
    /**
     * 通道编号-（1b）
     */
    public  String channelType="02";
    /**
     * 设备类型-(1B)
     */
    public  String sensor_type="FF";
    /**
     * 设备地址(8B)
     */
    public  String sensor_code="FFFFFFFFFFFFFFFF";
    /**
     * 预警下限（2b）
     */
    public  String warningUpper="FFFF";
    /**
     * 预警上限（2b）
     */
    public  String warningLower="FFFF";
}
