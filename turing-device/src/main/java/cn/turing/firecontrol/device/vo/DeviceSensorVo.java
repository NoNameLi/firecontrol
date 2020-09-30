package cn.turing.firecontrol.device.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2019/03/20 16:31
 *
 * @Description TODO
 * @Version V1.0
 */
@Data
public class DeviceSensorVo implements Serializable {

    private Long id;
    //设备编号
    private String sensorNo;
    //设备验证码（海康视频设备）
    private String validateCode;
    //设备通道号
    private String channelNo;
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
    //将厂商，设备，型号拼成数组
    private String[] deviceTypes;
    //设备组ID
    private Integer groupId;

    public String[] getDeviceTypes() {
        return new String[]{manufacturer,equipmentType,model};
    }

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
