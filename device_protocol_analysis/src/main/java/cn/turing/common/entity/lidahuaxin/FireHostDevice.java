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
public class FireHostDevice {
    /**
     * 回路
     */
    private List<String> loops;
    /**
     * 部件地址
     */
    private HashMap<String,String> componentsAddress;
    /**
     * 回路-状态
     */
    private HashMap<String, Map<String,String>> hashMap;
    /**
     * 回路数量
     */
    private Integer loop_count;
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

    public FireHostDevice(String data){
        deviceCode=data.substring(8,12);
        deviceType=data.substring(12,16);
        Integer sum=Integer.parseInt(data.substring(20,24),16);
        loop_count=Integer.parseInt(data.substring(24,28),16);
        port=String.valueOf(Integer.parseInt(data.substring(8,12),16));
        //起始地址
        Integer first_loop=sum;
        loops=new ArrayList<String>();
        hashMap=new HashMap<String, Map<String, String>>();
        componentsAddress=new HashMap<String, String>();
        for (int i=1;i<=loop_count;i++){
            String loop=String.valueOf(first_loop);
            loops.add(String.valueOf(loop));
            Integer loop_data=Integer.parseInt(data.substring(38+(4*i-4),38+(4*i)),16);
            String message= ByteUtil.buqi2(Integer.toBinaryString(loop_data));
            //默认为0：正常
            String status="0";
            Integer first=0;
            for (int j=0;j<message.length();j++){
                if (message.substring(j,j+1).equals("1")){
                    status="1";
                    first=j;
                    break;
                }
                first=j;
            }
            Map<String,String> map=new HashMap<String, String>();
            map.put(String.valueOf(first),status);
            hashMap.put(loop,map);
            String address=String.valueOf(first_loop/256);
            componentsAddress.put(loop,address);
            first_loop=first_loop+1;

        }

    }


}
