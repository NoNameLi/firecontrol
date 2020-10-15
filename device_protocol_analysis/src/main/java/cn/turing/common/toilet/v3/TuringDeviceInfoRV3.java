package cn.turing.common.toilet.v3;

import lombok.Data;



@Data
public class TuringDeviceInfoRV3 extends DeviceInfoV3 {
/**
 *
 {"Mark": "WCZ-2018-006", "Type": "Pit", "Node": 4, "volt": 3273, "Person": 0}
 02 00 01 09 57 43 5a 00 00 fe 00 03 f5 a5 5f 05 59 0d 01 80 5a
 */
    public Integer volt;
    public Integer Person;
//02000109574342000001000BF5A55F 05 0022 002B5A
    public TuringDeviceInfoRV3(String s) {
        super(s);
        this.setVolt(Integer.parseInt(s.substring(32,36),16));//V3版本不用
        this.setPerson(Integer.parseInt(s.substring(36,38),16));

    }
}
