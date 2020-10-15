package cn.turing.common.entity.huiding.v3.device;

import cn.turing.common.entity.huiding.v2.parsing.SensorAnalysis;
import lombok.Data;

/**
 * 摄像头-图片
 * @author TDS
 */
@Data
public class CameraDevice  {
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

        picture_length=Integer.parseInt(data.substring(0,4),16);
        message_package=Integer.parseInt(data.substring(4,8),16);
        first_package=Integer.parseInt(data.substring(8,12),16);
        package_length=Integer.parseInt(data.substring(12,16),16);
        validMessage=data.substring(16);

    }
}
