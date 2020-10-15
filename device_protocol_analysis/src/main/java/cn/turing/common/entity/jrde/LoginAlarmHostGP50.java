package cn.turing.common.entity.jrde;

import lombok.Data;

@Data
public class LoginAlarmHostGP50 extends AlarmHostGP50 {
    /**
     * 登录标识00 03
     */
    private String loginCmd;
    /**
     * 主机id
     */
    private String deviceCode;

    private String version;
    public LoginAlarmHostGP50(String data) {
        super(data);
        loginCmd=data.substring(14,18);
        String id=data.substring(18,58);
        StringBuffer stringBuffer=new StringBuffer();
        for (int i=1;i<=id.length()/2;i++){
            stringBuffer.append(String.valueOf(Integer.parseInt(id.substring(2*i-2,2*i),16)));
        }
        deviceCode=stringBuffer.toString().substring(5);
        version=data.substring(58,64);
    }
}
