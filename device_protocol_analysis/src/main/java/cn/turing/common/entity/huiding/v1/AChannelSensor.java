package cn.turing.common.entity.huiding.v1;

import cn.turing.common.entity.DeviceInfo;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * A通道-摄像头
 */
@Data
public class AChannelSensor extends DeviceInfo {
    private Integer signalStrength;//信号强度
    private String factoryId;//工厂id
    private Integer cycle;//周期
    private Integer volX;//电池电量
    private Integer sum_package;//总包数
    private Integer first_package;//第几个包
    private String validMessage;//有效数据
    public AChannelSensor(String s) {
        device_id=s.substring(12,20);
        signalStrength=Integer.parseInt(s.substring(20,22),16);
        factoryId=s.substring(22,30);
        cycle=Integer.parseInt(s.substring(30,38),16);
        volX=Integer.parseInt(s.substring(38,42),16);
        sum_package=Integer.parseInt(s.substring(46,50),16);
        first_package=Integer.parseInt(s.substring(50,54),16);
        validMessage=s.substring(58,s.length()-4);
    }

    @Override
    public JSONObject toDeviceJSON() {
        return null;
    }

    public JSONObject toDeviceMessage() {
        return null;
    }
}
