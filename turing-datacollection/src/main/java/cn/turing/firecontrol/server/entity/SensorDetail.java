package cn.turing.firecontrol.server.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorDetail {
    private String alarmCode; // 测点值， 在Constant类设置，尽量通用
    private String alarmType;// 警告类型，通用是 正常，离线。其余的都是协议中的
    private Object alarmValue;// 传感器的值， 如果是状态，写0
    private Integer alarmStatus;// 故障 0，报警 1，正常 2，离线 4.
    private Boolean changed=true; //不再使用
}
