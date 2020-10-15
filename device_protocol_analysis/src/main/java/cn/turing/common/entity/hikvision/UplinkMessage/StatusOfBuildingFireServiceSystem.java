package cn.turing.common.entity.hikvision.UplinkMessage;


import cn.turing.common.util.ByteUtil;
import lombok.Data;

/**
 * 上传建筑消防设施系统状态
 */
@Data
public class StatusOfBuildingFireServiceSystem {
    private String systemTypeFlag;//系统类型
    private String systemAddress;//系统地址
    public StatusOfBuildingFireServiceSystem(String s) {
        systemTypeFlag=s.substring(0,2);
        systemAddress=s.substring(2,4);
        String system_status= ByteUtil.hexString2binaryString(s.substring(4));

    }


}
