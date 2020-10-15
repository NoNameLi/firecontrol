package cn.turing.common.toilet.jobOn;

import lombok.Data;

/**
 *在岗识别
 * @author youngki
 */
@Data
public class JobOnDevice {
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
     * 在岗状态
     */
    private String cnt;

    public JobOnDevice(String message){
        message=message.replace(" ","");
        version=message.substring(0,14);
        deviceCode=message.substring(14,26);
        uploadTime=message.substring(26,44);
        StringBuffer sb=new StringBuffer(uploadTime);
        sb.insert(10," ");
        uploadTime=sb.toString();
        cnt=message.substring(50);

    }
}
