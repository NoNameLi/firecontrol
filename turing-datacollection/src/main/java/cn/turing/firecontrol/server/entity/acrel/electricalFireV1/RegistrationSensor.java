package cn.turing.firecontrol.server.entity.acrel.electricalFireV1;

import cn.turing.firecontrol.server.entity.acrel.AcrelDeviceInfo;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 注册信息解析
 */
@Data
public class RegistrationSensor extends AcrelDeviceInfo {
    public String registration_serial_number;//注册序列号
    public String card_number;//卡号
    public Integer operator_information;//运营商信息 1:中国电信2:中国移动3：中国联通
    public Integer signal_strength;//1~30 RSSI信号值
    public String version;//当前固件版本
    public String rated_current;//额定电流
    public String timed_upload_interval;//定时上传间隔
    public String domain_name;//域名
    public RegistrationSensor(String s) {
        super(s);
        registration_serial_number=registration(s.substring(6,46));
        card_number=registration(s.substring(46,106));
        operator_information=Integer.parseInt(s.substring(106,108));
        signal_strength=Integer.parseInt(s.substring(108,110));
        version=String.valueOf(Short.parseShort(s.substring(110,114),16));
        //预留
        
        rated_current=String.valueOf(Short.parseShort(s.substring(118,122),16));
        timed_upload_interval=String.valueOf(Integer.parseInt(s.substring(122,126),16));
        // TODO: 2019/8/27  数据来了再解 
        domain_name=String.valueOf(Integer.parseInt(s.substring(122,126),16));
    }

    @Override
    public JSONObject toDeviceJSON() {
        return null;
    }

    /**
     * 解析注册序列号
     * @param data
     * @return
     */
    public String registration(String data){
        StringBuffer stringBuffer=new StringBuffer();
        for (int i=0;i<data.length();i=i+2){
          String value= data.substring(i+1,i+2);
          stringBuffer.append(value);
        }
        return stringBuffer.toString();
    }
}
