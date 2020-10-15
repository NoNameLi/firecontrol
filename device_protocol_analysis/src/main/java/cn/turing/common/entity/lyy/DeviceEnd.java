package cn.turing.common.entity.lyy;

import cn.turing.common.entity.DeviceInfo;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import java.util.Date;

/**
 * 充电结束上报
 */
@Data
public class DeviceEnd extends DeviceInfo {

    /**
     * 设备编号
     */
    private String deviceNo;
    /**
     * 通道号
     */
    private String num;
    /**
     * 停止代码
     */
    private String code;
    /**
     * 停止原因
     */
    private String reason;
    /**
     * 剩余时间（单位、分钟）
     */
    private String time;
    /**
     * 剩余电量（单位、0.1度）
     */
    private String elec;






    public DeviceEnd(JSONObject jsonObject){

        deviceNo=jsonObject.getString("deviceNo");
        JSONObject data=jsonObject.getJSONObject("data");
        num=String.valueOf(data.getInteger("num"));
        code=String.valueOf(data.getInteger("code"));
        reason=String.valueOf(data.getString("reason"));
        time=String.valueOf(data.getInteger("time"));
        elec=String.valueOf(data.getFloat("elec"));

    }

    public JSONObject toDeviceJSON() {
        return null;
    }

    public JSONObject toDeviceMessage() {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("deviceCode",deviceNo);
        jsonObject.put("uploadTime",new Date());
        jsonObject.put("receiveTime",new Date());
        jsonObject.put("num",num);
        jsonObject.put("code",code);
        jsonObject.put("time",time);
        jsonObject.put("elec",elec);
        return jsonObject;
    }
}
