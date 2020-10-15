package cn.turing.common.toilet.v3;


import cn.turing.common.util.Hex2Float;
import lombok.Data;

@Data
public class TuringDeviceInfoAV3 extends DeviceInfoV3 {
    /**
     {"Mark": "WCZ-2018-004", "Type": "Sensor", "SD": 55.250846862792968, "WD": 25.675331115722656, "Node": 4, "YW": 0.25011873245239256, "PMS200C":
     {"cfpm2.5nd": 66, "dqpm2.5nd": 66}}
     */
    public Float WD;
    public Float YW;
    public Float SD;
    public Integer cfpm1_0;
    public Integer cfpm10;
    public Integer cfpm;
    public Integer dqpm;
    public TuringDeviceInfoAV3(String s) {
        super(s);
        this.setCfpm1_0(Integer.parseInt(s.substring(32,36),16));
        String c= s.substring(36,40);
        this.setCfpm(Integer.parseInt(c,16));

        this.setCfpm10(Integer.parseInt(s.substring(40,44),16));
        String f=s.substring(48,52);

        this.setDqpm(Integer.parseInt(f,16));
        String yw=s.substring(80,88);
        this.setYW(getFloat(yw));//00 00 80 3f
        String wd=s.substring(88,96);
        this.setWD(getFloat(wd));
        String sd=s.substring(96,104);
        this.setSD(getFloat(sd));

    }

    public Float getFloat(String data){
        Long l = Hex2Float.parseLong(data.substring(6)+data.substring(4,6)+data.substring(2,4)+data.substring(0,2), 16);
        Float f = Float.intBitsToFloat(l.intValue());
        return f;
    }

}
