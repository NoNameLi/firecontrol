package cn.turing.common.entity.huiding.v2.device;

import cn.turing.common.entity.huiding.v2.parsing.SensorAnalysis;
import lombok.Data;

/**
 * 摄像头-图片
 * @author TDS
 */
@Data
public class CameraDevice extends SensorAnalysis {
    /**
     * 图片长度
     */
    private Integer picture_length;
    /**
     * 数据报个数
     */
    private Integer message_package;
    /**
     * 第几个包
     */
    private Integer first_package;
    /**
     * 包长
     */
    private Integer package_length;
    /**
     * 有效数据
     */
    private String validMessage;

    public CameraDevice(String data){
        super(data);

        picture_length=Integer.parseInt(data.substring(44,48),16);
        message_package=Integer.parseInt(data.substring(48,52),16);
        first_package=Integer.parseInt(data.substring(52,56),16);
        package_length=Integer.parseInt(data.substring(56,60),16);
        validMessage=data.substring(60,data.length()-4);

    }
}
