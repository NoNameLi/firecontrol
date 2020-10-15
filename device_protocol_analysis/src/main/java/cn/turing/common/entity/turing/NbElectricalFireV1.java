package cn.turing.common.entity.turing;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import sun.management.Sensor;

/**
 * nB电气火灾
 */
@Data
public class NbElectricalFireV1 extends RabbitDeviceInfo {
    /**
     * 命令字段
     */
    private String cmd;
    /**
     * 报警字段24bit
     */
    private String alarmBit;

    //三项电流
    private Double ecaValue;
    private Double ecbValue;
    private Double eccValue;
    //三项电压
    private Integer voaValue;
    private Integer vobValue;
    private Integer vocValue;

    //4路温度
    private Double tempaValue;
    private Double tempbValue;
    private Double tempcValue;
    private Double tempdValue;

    //漏电流
    private Double louValue;
    //信号值
    private Integer signValue;


    public NbElectricalFireV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);

        cmd= this.getData().substring(26, 28);
        alarmBit= ByteUtil.hexString2binaryString(this.getData().substring(28, 34)); //标识
        ecaValue=Integer.parseInt(this.getData().substring(34,38),16)*10.0;
        voaValue=Integer.parseInt(this.getData().substring(38,42),16);
        String ta=this.getData().substring(42,44);//00为正；01为负
        if (ta.equals("00")){
            tempaValue=Integer.parseInt(this.getData().substring(44,48),16)*10.0;
        }else{
            tempaValue=-Integer.parseInt(this.getData().substring(44,48),16)*10.0;
        }
        ecbValue=Integer.parseInt(this.getData().substring(48,52),16)*10.0;
        vobValue=Integer.parseInt(this.getData().substring(52,56),16);
        String tb=this.getData().substring(56,58);//00为正；01为负
        if (tb.equals("00")){
            tempbValue=Integer.parseInt(this.getData().substring(58,62),16)*0.1;
        }else{
            tempbValue=-Integer.parseInt(this.getData().substring(58,62),16)*0.1;
        }
        eccValue=Integer.parseInt(this.getData().substring(62,66),16)*10.0;
        vocValue=Integer.parseInt(this.getData().substring(66,70),16);
        String tc=this.getData().substring(70,72);//00为正；01为负
        if (tc.equals("00")){
            tempcValue=Integer.parseInt(this.getData().substring(72,76),16)*0.1;
        }else{
            tempcValue=-Integer.parseInt(this.getData().substring(72,76),16)*0.1;
        }

        String td=this.getData().substring(76,78);//00为正；01为负
        if (td.equals("00")){
            tempcValue=Integer.parseInt(this.getData().substring(78,82),16)*0.1;
        }else{
            tempcValue=-Integer.parseInt(this.getData().substring(78,82),16)*0.1;
        }
        louValue=Integer.parseInt(this.getData().substring(82,86),16)*10.0;

        signValue=Integer.parseInt(this.getData().substring(86,88),16)-110;

        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(eca());
        sensorDetails.add(voa());
        sensorDetails.add(tempa());
        sensorDetails.add(ecb());
        sensorDetails.add(vob());
        sensorDetails.add(tempb());
        sensorDetails.add(ecc());
        sensorDetails.add(voc());
        sensorDetails.add(tempc());
        sensorDetails.add(tempd());
        sensorDetails.add(lou());
        sensorDetails.add(sign());
    }


    public SensorDetail eca(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECA);
        sensorDetail.setAlarmValue(ecaValue);
        ecl(sensorDetail,2);

        return sensorDetail;
    }

    public SensorDetail voa(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VLA);
        sensorDetail.setAlarmValue(voaValue);
        vol(sensorDetail,4);
        return sensorDetail;
    }

    public SensorDetail tempa(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMPT1);
        sensorDetail.setAlarmValue(tempaValue);
        temp(sensorDetail,6);

        return sensorDetail;
    }

    public SensorDetail ecb(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECB);
        sensorDetail.setAlarmValue(ecbValue);
        ecl(sensorDetail,8);

        return sensorDetail;
    }

    public SensorDetail vob(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VLB);
        sensorDetail.setAlarmValue(vobValue);
        vol(sensorDetail,10);
        return sensorDetail;
    }

    public SensorDetail tempb(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMPT2);
        sensorDetail.setAlarmValue(tempbValue);
        temp(sensorDetail,12);
        return sensorDetail;
    }
    public SensorDetail ecc(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECC);
        sensorDetail.setAlarmValue(eccValue);
        ecl(sensorDetail,14);

        return sensorDetail;
    }

    public SensorDetail voc(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VLC);
        sensorDetail.setAlarmValue(vocValue);
        vol(sensorDetail,16);
        return sensorDetail;
    }

    public SensorDetail tempc(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMPT3);
        sensorDetail.setAlarmValue(tempcValue);
        temp(sensorDetail,18);
        return sensorDetail;
    }

    public SensorDetail tempd(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMPT4);
        sensorDetail.setAlarmValue(tempcValue);
        temp(sensorDetail,20);
        return sensorDetail;
    }

    public SensorDetail lou(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.LK);
        sensorDetail.setAlarmValue(louValue);

        if ("00".equals(alarmBit.substring(22))){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
        if ("01".equals(alarmBit.substring(22))){
            sensorDetail.setAlarmType("电流过大");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            has_guzhang=true;
        }
        if ("10".equals(alarmBit.substring(22))){
            sensorDetail.setAlarmType("没有接传感器");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
        return sensorDetail;
    }

    public SensorDetail sign(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.SIGN);
        sensorDetail.setAlarmValue(signValue);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        return sensorDetail;
    }


    public void ecl(SensorDetail sensorDetail,Integer num){
        if ("00".equals(alarmBit.substring(num,num+2))){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
        if ("01".equals(alarmBit.substring(num,num+2))){
            sensorDetail.setAlarmType("电流过大");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            has_guzhang=true;
        }
        if ("10".equals(alarmBit.substring(num,num+2))){
            sensorDetail.setAlarmType("没有接传感器");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
    }

    public void vol(SensorDetail sensorDetail,Integer num){
        if ("00".equals(alarmBit.substring(num,num+2))){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
        if ("01".equals(alarmBit.substring(num,num+2))){
            sensorDetail.setAlarmType("电压过高");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            has_alarm=true;
        }
        if ("10".equals(alarmBit.substring(num,num+2))){
            sensorDetail.setAlarmType("电压过低");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            has_alarm=true;
        }
        if ("11".equals(alarmBit.substring(num,num+2))){
            sensorDetail.setAlarmType("供电中断");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            has_alarm=true;
        }
    }

    public void temp(SensorDetail sensorDetail,int num){
        if ("00".equals(alarmBit.substring(num,num+2))){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
        if ("01".equals(alarmBit.substring(num,num+2))){
            sensorDetail.setAlarmType("温度过高");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            has_alarm=true;
        }
        if ("10".equals(alarmBit.substring(num,num+2))){
            sensorDetail.setAlarmType("没有接传感器");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
    }
}
