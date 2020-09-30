package cn.turing.firecontrol.device.vo;

import lombok.Data;

@Data
public class VideoDeviceVo {
    private Long id;
    //设备编号
    private String sensorNo;
    //设备验证码（海康视频设备）
    private String validateCode;
    //厂商名称
    private String manufacturer;
    //设备系列
    private String equipmentType;
    //设备型号
    private String model;
    //设备名称
    private String deviceName;
    //平面图打点位置
    private String positionSign;
    //状态[0=故障/1=报警/2=正常/3=未启用]
    private String status;
    //设备组名称
    private String groupName;
    //分析解决方案名称
    private String solutionName;
    //视频播放地址
    private String liveAddress;
    //设备系列
    private String sensorType;
    //设备通道号
    private String ChannelNo;

    //将设备编号以“：”为分割符进行拆分，前面为设备系列号，后面后设备通道号
    public String getSensorNo(){
        return this.sensorNo.split(":")[0];
    }

    //将设备编号以“：”为分割符进行拆分，前面为设备系列号，后面后设备通道号
    public String getChannelNo(){
        String channelNo = this.sensorNo.split(":")[1];
        return channelNo == null ? "1" : channelNo;
    }
}
