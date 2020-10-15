package cn.turing.common.entity.zhgd;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.DeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * 三相NB电表
 */
@Data
public class NbThreePhaseMeterDevice extends DeviceInfo {

    //地址
    private String address;
    //控制码
    private String controlCode;
    //长度
    private Integer length;
    //数据标识
    private String dataMarker;
    //A相电压
    private String volA;
    //B相电压
    private String volB;
    //C相电压
    private String volC;
    //A相电流
    private String eclA;
    //B相电流
    private String eclB;
    //C相电流
    private String eclC;
    //瞬时总有功功率
    private String pws;
    //瞬时A相有功功率
    private String pwsA;
    //瞬时B相有功功率
    private String pwsB;
    //瞬时C相有功功率
    private String pwsC;
    //总功率因数
    private String pwf;
    //A相功率因数
    private String pwfA;
    //B相功率因数
    private String pwfB;
    //C相功率因数
    private String pwfC;
    //状态位
    private String status;

    private BinaryStatusInfo statusInfo;

    //组合有功总电能
    private String eeg;
    //组合有功总电能尖
    private String eegJ;
    //组合有功总电能峰
    private String eegF;
    //组合有功总电能平
    private String eegP;
    //组合有功总电能谷
    private String eegG;
    //正向有功总电能
    private String zpw;
    //正向有功总电能尖
    private String zpwJ;
    //正向有功总电能峰
    private String zpwF;
    //正向有功总电能平
    private String zpwP;
    //正向有功总电能谷
    private String zpwG;
    //反向有功总电能
    private String fpw;
    //反向有功总电能尖
    private String fpwJ;
    //反向有功总电能峰
    private String fpwF;
    //反向有功总电能平
    private String fpwP;
    //反向有功总电能谷
    private String fpwG;
    //瞬时总无功功率
    private String wpw;
    //瞬时A相无功功率
    private String wpwA;
    //瞬时B相无功功率
    private String wpwB;
    //瞬时C相无功功率
    private String wpwC;
    //当前有功需量
    private String cadY;
    //无前有功需量
    private String cadW;
    //校验和
    private String check;
    //帧序号
    private Integer number;

    public NbThreePhaseMeterDevice(String data){
        address= ByteUtil.reversalHexByNo(data.substring(10,22));
        controlCode=data.substring(24,26);
        length=Integer.parseInt(data.substring(26,28),16);
        dataMarker=ByteUtil.reversalHex(data.substring(28,36));
        volA=ByteUtil.insertToThree(ByteUtil.reversalHex(data.substring(36,40)));
        volB=ByteUtil.insertToThree(ByteUtil.reversalHex(data.substring(40,44)));
        volC=ByteUtil.insertToThree(ByteUtil.reversalHex(data.substring(44,48)));
        eclA=ByteUtil.insertToThree(ByteUtil.reversalHex(data.substring(48,52)));
        eclB=ByteUtil.insertToThree(ByteUtil.reversalHex(data.substring(52,58)));
        eclC=ByteUtil.insertToThree(ByteUtil.reversalHex(data.substring(58,64)));
        pws=ByteUtil.insertToTwo(ByteUtil.reversalHex(data.substring(64,70)));
        pwsA=ByteUtil.insertToTwo(ByteUtil.reversalHex(data.substring(70,76)));
        pwsB=ByteUtil.insertToTwo(ByteUtil.reversalHex(data.substring(76,82)));
        pwsC=ByteUtil.insertToTwo(ByteUtil.reversalHex(data.substring(82,88)));
        pwf=ByteUtil.insertToOne(ByteUtil.reversalHex(data.substring(88,92)));
        pwfA=ByteUtil.insertToOne(ByteUtil.reversalHex(data.substring(92,96)));
        pwfB=ByteUtil.insertToOne(ByteUtil.reversalHex(data.substring(96,100)));
        pwfC=ByteUtil.insertToOne(ByteUtil.reversalHex(data.substring(100,104)));
        status=Integer.toBinaryString(Integer.parseInt(data.substring(104,106))-33);
        statusInfo=new BinaryStatusInfo(status);
        eeg=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(106,114)));
        eegJ=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(114,122)));
        eegF=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(122,130)));
        eegP=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(130,138)));
        eegG=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(138,146)));
        zpw=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(146,154)));
        zpwJ=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(154,162)));
        zpwF=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(162,170)));
        zpwP=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(170,178)));
        zpwG=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(178,186)));
        fpw=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(186,194)));
        fpwJ=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(194,202)));
        fpwF=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(202,210)));
        fpwP=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(210,218)));
        fpwG=ByteUtil.insertToSix(ByteUtil.reversalHex(data.substring(218,226)));
        wpw=ByteUtil.insertToTwo(ByteUtil.reversalHex(data.substring(226,232)));
        wpwA=ByteUtil.insertToTwo(ByteUtil.reversalHex(data.substring(232,238)));
        wpwB=ByteUtil.insertToTwo(ByteUtil.reversalHex(data.substring(238,244)));
        wpwC=ByteUtil.insertToTwo(ByteUtil.reversalHex(data.substring(244,250)));
        cadY=ByteUtil.insertToTwo(ByteUtil.reversalHex(data.substring(250,256)));
        cadW=ByteUtil.insertToTwo(ByteUtil.reversalHex(data.substring(256,262)));
        check=data.substring(262,264);
        number=Integer.parseInt(data.substring(268),16);

        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(vola());
        sensorDetails.add(volb());
        sensorDetails.add(volc());
        sensorDetails.add(ecla());
        sensorDetails.add(eclb());
        sensorDetails.add(eclc());
        sensorDetails.add(pws());
        sensorDetails.add(pwsA());
        sensorDetails.add(pwsB());
        sensorDetails.add(pwsC());
        sensorDetails.add(pwf());
        sensorDetails.add(pwfA());
        sensorDetails.add(pwfB());
        sensorDetails.add(pwfC());
        sensorDetails.add(eeg());
        sensorDetails.add(eegJ());
        sensorDetails.add(eegF());
        sensorDetails.add(eegP());
        sensorDetails.add(eegG());
        sensorDetails.add(zpw());
        sensorDetails.add(zpwJ());
        sensorDetails.add(zpwF());
        sensorDetails.add(zpwP());
        sensorDetails.add(zpwG());
        sensorDetails.add(fpw());
        sensorDetails.add(fpwJ());
        sensorDetails.add(fpwF());
        sensorDetails.add(fpwP());
        sensorDetails.add(fpwG());
        sensorDetails.add(wpw());
        sensorDetails.add(wpwA());
        sensorDetails.add(wpwB());
        sensorDetails.add(wpwC());
        sensorDetails.add(cady());
        sensorDetails.add(cadw());

    }
    public SensorDetail vola(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.VLA);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(volA);
        return sensorDetails;
    }
    public SensorDetail volb(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.VLB);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(volB);
        return sensorDetails;
    }
    public SensorDetail volc(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.VLC);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(volC);
        return sensorDetails;
    }
    public SensorDetail ecla(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.ECA);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(eclA);
        return sensorDetails;
    }
    public SensorDetail eclb(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.ECB);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(eclB);
        return sensorDetails;
    }
    public SensorDetail eclc(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.ECC);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(eclC);
        return sensorDetails;
    }

    public SensorDetail pws(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.PW);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(pws);
        return sensorDetails;
    }
    public SensorDetail pwsA(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.PWA);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(pwsA);
        return sensorDetails;
    }
    public SensorDetail pwsB(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.PWB);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(pwsB);
        return sensorDetails;
    }
    public SensorDetail pwsC(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.PWC);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(pwsC);
        return sensorDetails;
    }

    public SensorDetail wpw(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.WPW);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(wpw);
        return sensorDetails;
    }
    public SensorDetail wpwA(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.WPWA);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(wpwA);
        return sensorDetails;
    }
    public SensorDetail wpwB(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.WPWB);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(wpwB);
        return sensorDetails;
    }
    public SensorDetail wpwC(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.WPWC);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(wpwC);
        return sensorDetails;
    }


    public SensorDetail pwfA(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.PFA);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(pwfA);
        return sensorDetails;
    }

    public SensorDetail pwfB(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.PFB);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(pwfB);
        return sensorDetails;
    }

    public SensorDetail pwfC(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.PFC);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(pwfC);
        return sensorDetails;
    }

    public SensorDetail pwf(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.PF);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(pwf);
        return sensorDetails;
    }

    public SensorDetail eeg(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.EEG);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(eeg);
        return sensorDetails;
    }
    public SensorDetail eegJ(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.EEGJ);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(eegJ);
        return sensorDetails;
    }
    public SensorDetail eegF(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.EEGF);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(eegF);
        return sensorDetails;
    }
    public SensorDetail eegP(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.EEGP);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(eegP);
        return sensorDetails;
    }
    public SensorDetail eegG(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.EEGG);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(eegG);
        return sensorDetails;
    }

    public SensorDetail zpw(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.ZPW);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(zpw);
        return sensorDetails;
    }

    public SensorDetail zpwJ(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.ZPWJ);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(zpwJ);
        return sensorDetails;
    }
    public SensorDetail zpwF(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.ZPWF);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(zpwF);
        return sensorDetails;
    }
    public SensorDetail zpwP(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.ZPWP);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(zpwP);
        return sensorDetails;
    }
    public SensorDetail zpwG(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.ZPWG);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(zpwG);
        return sensorDetails;
    }

    public SensorDetail fpw(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.FPW);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(fpw);
        return sensorDetails;
    }

    public SensorDetail fpwJ(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.FPWJ);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(fpwJ);
        return sensorDetails;
    }
    public SensorDetail fpwF(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.FPWF);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(fpwF);
        return sensorDetails;
    }
    public SensorDetail fpwP(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.FPWP);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(fpwP);
        return sensorDetails;
    }
    public SensorDetail fpwG(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.FPWG);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(fpwG);
        return sensorDetails;
    }

    public SensorDetail cady(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.CADY);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(cadY);
        return sensorDetails;
    }

    public SensorDetail cadw(){
        SensorDetail sensorDetails=new SensorDetail();
        sensorDetails.setAlarmCode(Constant.CADW);
        sensorDetails.setAlarmStatus(Constant.ST_NORM);
        sensorDetails.setAlarmType("正常");
        sensorDetails.setAlarmValue(cadW);
        return sensorDetails;
    }









    @Override
    public JSONObject toDeviceJSON() {
        return null;
    }

    @Override
    public JSONObject toDeviceMessage() {
        return null;
    }



}
