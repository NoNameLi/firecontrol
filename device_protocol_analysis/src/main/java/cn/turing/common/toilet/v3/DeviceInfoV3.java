package cn.turing.common.toilet.v3;

import cn.turing.common.util.ByteUtil;
import lombok.Data;

@Data
public abstract class DeviceInfoV3 {
    /**
     * Type": "Sensor", "Mark": "EIC-2018-999", "Node": 3,
     */
    public String type;
    public String mark;
    public Integer node;

    public DeviceInfoV3(String s) {
        this.setType("Sensor");
        //020001095455520000010002
        if (s.length()==34){
            String year= String.valueOf(Integer.parseInt(s.substring(0,2),16))
                    +String.valueOf(Integer.parseInt(s.substring(2,4),16))
                    +String.valueOf(Integer.parseInt(s.substring(4,6),16))
                    +String.valueOf(Integer.parseInt(s.substring(6,8),16));
            String cum= ByteUtil.hexStr2Str(s.substring(8,14));
            String num=String.valueOf(Integer.parseInt(s.substring(14,16),16))
                    +String.valueOf(Integer.parseInt(s.substring(16,18),16))
                    +String.valueOf(Integer.parseInt(s.substring(18,20),16));
            if(num.length()>3){
                num=num.substring(num.length()-3);
            }
            this.setMark(cum+"-"+year+"-"+num);
        }else{
            String mark=s.substring(0,24);
            String year= String.valueOf(Integer.parseInt(mark.substring(0,2),16))
                    +String.valueOf(Integer.parseInt(mark.substring(2,4),16))
                    +String.valueOf(Integer.parseInt(mark.substring(4,6),16))
                    +String.valueOf(Integer.parseInt(mark.substring(6,8),16));
            String cum= ByteUtil.hexStr2Str(mark.substring(8,14));
            String num=String.valueOf(Integer.parseInt(mark.substring(14,16),16))
                    +String.valueOf(Integer.parseInt(mark.substring(16,18),16))
                    +String.valueOf(Integer.parseInt(mark.substring(18,20),16));
            if(num.length()>3){
                num=num.substring(num.length()-3);
            }
            this.setMark(cum+"-"+year+"-"+num);
            this.setNode(Integer.parseInt(mark.substring(20,24),16));
        }

    }
}
