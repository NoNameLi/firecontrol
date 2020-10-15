package cn.turing.common.entity.turing;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

@Data
public class SmartPowerDevice extends RabbitDeviceInfo {

    private Integer vol_a;
    private Integer vol_b;
    private Integer vol_c;
    private Integer ele_a;
    private Integer ele_b;
    private Integer ele_c;
    private Integer ele_lou_a;
    private Integer ele_lou_b;
    private Integer ele_lou_c;
    private Integer ele_lou_d;
    private Integer temp_a;
    private Integer temp_b;
    private Integer temp_c;
    private Integer temp_d;
    public SmartPowerDevice(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        vol_a=Integer.parseInt(this.getData().substring(6,10),16);
        vol_b=Integer.parseInt(this.getData().substring(10,14),16);
        vol_c=Integer.parseInt(this.getData().substring(14,18),16);
        ele_a=Integer.parseInt(this.getData().substring(18,22),16);
        ele_b=Integer.parseInt(this.getData().substring(22,26),16);
        ele_c=Integer.parseInt(this.getData().substring(26,30),16);
        ele_lou_a=Integer.parseInt(this.getData().substring(30,34),16);
        ele_lou_b=Integer.parseInt(this.getData().substring(34,38),16);
        ele_lou_c=Integer.parseInt(this.getData().substring(38,42),16);
        ele_lou_d=Integer.parseInt(this.getData().substring(42,46),16);
        temp_a=Integer.parseInt(this.getData().substring(46,50),16)-30;
        temp_b=Integer.parseInt(this.getData().substring(50,54),16)-30;
        temp_c=Integer.parseInt(this.getData().substring(54,58),16)-30;
        temp_d=Integer.parseInt(this.getData().substring(58,62),16)-30;
        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(vola());
        sensorDetails.add(volb());
        sensorDetails.add(volc());
        sensorDetails.add(elea());
        sensorDetails.add(eleb());
        sensorDetails.add(elec());
        sensorDetails.add(eleloua());
        sensorDetails.add(eleloub());
        sensorDetails.add(elelouc());
        sensorDetails.add(eleloud());
        sensorDetails.add(tempa());
        sensorDetails.add(tempb());
        sensorDetails.add(tempc());
        sensorDetails.add(tempd());
    }

    public SensorDetail vola(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VLA);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(vol_a);

        return  sensorDetail;
    }
    public SensorDetail volb(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VLB);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(vol_b);

        return  sensorDetail;
    }
    public SensorDetail volc(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VLC);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(vol_c);

        return  sensorDetail;
    }
    public SensorDetail elea(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECA);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(ele_a);

        return  sensorDetail;
    }
    public SensorDetail eleb(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECB);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(ele_b);

        return  sensorDetail;
    }
    public SensorDetail elec(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECC);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(ele_c);

        return  sensorDetail;
    }
    public SensorDetail eleloua(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.RES1);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(ele_lou_a);

        return  sensorDetail;
    }
    public SensorDetail eleloub(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.RES2);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(ele_lou_b);

        return  sensorDetail;
    }
    public SensorDetail elelouc(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.RES3);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(ele_lou_c);

        return  sensorDetail;
    }
    public SensorDetail eleloud(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.RES4);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(ele_lou_d);

        return  sensorDetail;
    }
    public SensorDetail tempa(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMPT1);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(temp_a);

        return  sensorDetail;
    }
    public SensorDetail tempb(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMPT2);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(temp_b);

        return  sensorDetail;
    }
    public SensorDetail tempc(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMPT3);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(temp_c);

        return  sensorDetail;
    }
    public SensorDetail tempd(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMPT4);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(temp_d);

        return  sensorDetail;
    }
}
