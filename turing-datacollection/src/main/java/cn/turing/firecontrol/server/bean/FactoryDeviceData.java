package cn.turing.firecontrol.server.bean;

import lombok.Data;

import java.util.Date;

/**
 * 智慧工厂
 */
@Data
public class FactoryDeviceData {
    private String DEVICE_ID;
    private String CHANNEL_NO;
    private Integer SWITCH;
    private Integer SENSOR_TYPE;
    private String CMD;
    private Integer ACQUISITION_CYCLE;
    private String USER_DEFINED;
    private Date UPLOAD_TIME;
    private Integer FACTORY_ID;
}
