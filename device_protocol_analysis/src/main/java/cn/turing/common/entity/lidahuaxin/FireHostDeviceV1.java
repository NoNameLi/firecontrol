package cn.turing.common.entity.lidahuaxin;

import cn.turing.common.util.ByteUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * AA 55 00 D9 00 01 00 00 01 04 1B BC 00 64 36 E1 01 04 C8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 9F B5 A5 5A
 * 利达华信-消防主机
 * aa5500db0001000001041bbc006436e10104c800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009fb5a55a
 */
@Data
public class FireHostDeviceV1 {


    /**
     * 端口
     */
    private String port;
    /**
     * ip
     */
    private String ip;
    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * 设备编号
     */
    private String deviceCode;
    /**
     * 回路
     */
    private List<String> loops=new ArrayList();

    private List<String> address=new ArrayList();

    private List<String> alarmType=new ArrayList<String>();

    public FireHostDeviceV1(String data){
        deviceCode=data.substring(8,12);
        deviceType=data.substring(12,16);
        //起始地址
        Integer sum=Integer.parseInt(data.substring(20,24),16);
        int loop_count=Integer.parseInt(data.substring(24,28),16);
        port=String.valueOf(Integer.parseInt(data.substring(8,12),16));

        for (int i=0;i<loop_count;i++){
            loops.add(String.valueOf((sum+i)/256));
            address.add(String.valueOf((sum+i)%256));
            Integer loop_data=Integer.parseInt(data.substring(38+(4*(i+1)-4),38+(4*(i+1))),16);
            String message= ByteUtil.buqi2(Integer.toBinaryString(loop_data));
            message=new StringBuffer(message).reverse().toString();
            String alarm="0";//正常
            if (message.startsWith("1")){
                alarm="1";
            }
            alarmType.add(alarm);
        }

    }


}
