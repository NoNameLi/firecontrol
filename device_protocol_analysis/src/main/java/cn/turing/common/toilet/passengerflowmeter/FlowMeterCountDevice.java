package cn.turing.common.toilet.passengerflowmeter;

import lombok.Data;

/**
 *客流计数量
 * @author youngki
 */
@Data
public class FlowMeterCountDevice {
    /**
     * 版本号
     */
    private String version;
    /**
     * 设备编号
     */
    private String deviceCode;
    /**
     * 设备上报时间
     */
    private String  uploadTime;
    /**
     * 数量，累加的
     */
    private String cnt;

    public FlowMeterCountDevice(String message){
        message=message.replace(" ","");
        version=message.substring(0,13);
        deviceCode=message.substring(13,25);
        uploadTime=message.substring(25,43);
        StringBuffer sb=new StringBuffer(uploadTime);
        sb.insert(10," ");
        uploadTime=sb.toString();
        cnt=message.substring(47);

    }
}
