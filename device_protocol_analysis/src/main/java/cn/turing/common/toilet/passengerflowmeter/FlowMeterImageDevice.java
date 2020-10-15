package cn.turing.common.toilet.passengerflowmeter;

import cn.turing.common.util.ByteUtil;
import lombok.Data;

/**
 *客流计图片
 * @author youngki
 */
@Data
public class FlowMeterImageDevice {
    /**
     * 设备编号
     */
    private String deviceCode;
    /**
     * 总包数
     */
    private Integer sumNumber;
    /**
     * 序号
     */
    private Integer serialNumber;
    /**
     * 有效数据
     */
    private String validData;
    public FlowMeterImageDevice(String message){

        deviceCode= ByteUtil.hexStringToString(message.substring(0,24));
        sumNumber=Integer.parseInt(ByteUtil.stringToString(message.substring(24,32)),16);
        serialNumber=Integer.parseInt(ByteUtil.stringToString(message.substring(32,40)),16);
        validData=message.substring(40);
    }
}
