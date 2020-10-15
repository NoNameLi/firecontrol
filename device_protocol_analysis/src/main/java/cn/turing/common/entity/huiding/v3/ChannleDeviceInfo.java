package cn.turing.common.entity.huiding.v3;

import cn.turing.common.entity.huiding.v3.device.*;
import lombok.Data;

/**
 * 上行数据包
 */
@Data
public class ChannleDeviceInfo {

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
     * 设备类型
     */
    private String deviceType;

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
     * 数据区
     */
    private String message;

    /**
     * 包尾
     */
    private String end;
    /**
     * 数据区里内容
     */
    private Object value;


    public ChannleDeviceInfo(String data){
        head=data.substring(0,4);
        length=String.valueOf(Integer.parseInt(data.substring(4,8),16));
        messageType=data.substring(8,12);
        deviceType=data.substring(12,14);
        deviceCode=data.substring(14,38);
        sign=String.valueOf(Integer.parseInt(data.substring(38,40),16)-256);
        Double v=Integer.parseInt(data.substring(40,44),16)*0.001;
        vol=String.format("%.2f",v);
        message=data.substring(44,data.length()-4);
        end=data.substring(data.length()-4);

        //摄像头
        if ("01".equals(deviceType)) {
            value= new CameraDevice(message);
         //红外读电表
        } else if ("10".equals(deviceType)) {
            value=new InfraredMeterReadingDevice(message);

          //红外计数器
        } else if ("30".equals(deviceType)) {
            value=new InfraredDevice(message);
            //温湿度
        } else if ("31".equals(deviceType)) {

            value=new TemperatureAndHumidity(message);
            //喷淋水压
        } else if ("61".equals(deviceType)) {
            value=new SprayDevice(message,v);
            //水箱水位
        } else if ("62".equals(deviceType)) {
            value=new WaterTankDevice(message,v);
        }
    }
}
