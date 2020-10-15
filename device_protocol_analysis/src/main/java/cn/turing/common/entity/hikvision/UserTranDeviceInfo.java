package cn.turing.common.entity.hikvision;


import cn.turing.common.entity.SensorDetail;
import lombok.Data;

import java.util.Date;
@Data
public abstract class UserTranDeviceInfo {

    private SensorDetail sensorDetail;
    protected boolean has_alarm =false; //设备是否报警
    protected boolean has_guzhang =false;//设备是否故障
    protected Date upload_time; //设备信息数据的时间
    protected Date recieve_time;//我方程序接受时间
    private Integer partsType;//部件类型
    private String partsAddress;//部件地址
}
