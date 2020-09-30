package cn.turing.firecontrol.server.bean;

import lombok.Data;

import java.util.Date;

/**
 * 智慧工厂
 */
@Data
public class FactorySensor {
    private String DEVICE_ID;
    private Integer FACTORY_ID;
    private String CHANNELA_ID;
    private String CHANNELB_ID;
    private String CHANNELC_ID;
    private String CHANNELD_ID;
    private String CHANNELE_ID;
    private Date UPLOAD_TIME;
}
