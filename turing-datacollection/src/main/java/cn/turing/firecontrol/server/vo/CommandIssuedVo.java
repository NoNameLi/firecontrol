package cn.turing.firecontrol.server.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 下发指令的抽象化
 */
@Data
public class CommandIssuedVo {
    /**
     *
     */
    private String deviceCode;
    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * 具体定义
     */
    private Object data;
    /**
     * 时间
     */
    private LocalDateTime dateTime;
    /**
     * 通讯方式lora、nb
     */
    private String netType;
}
