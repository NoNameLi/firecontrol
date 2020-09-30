package cn.turing.firecontrol.server.vo;


import lombok.Data;

@Data
public class DeviceSensorMessage {
    /**
     * 设备编号
      */
    private String id;
    /**
     * 版本
     */
    private String version;
    /**
     * 时间
     */
    private String time;
    /**
     * 设备数据
     */
    private String data;
    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 物联网id
     */
    private String iccid;
}
