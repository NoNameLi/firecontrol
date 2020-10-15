package cn.turing.common.entity.terminus;




import cn.turing.common.base.Constant;
import cn.turing.common.base.DeviceChannelConst;
import cn.turing.common.base.DeviceStatusEnum;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;



@Data
public class EffectiveSmartElectricityDeviceV1 extends SmartElectricityDeviceV1 {

    private String replyPackage;//回复包


    public EffectiveSmartElectricityDeviceV1(String data) {
        super(data);

        if (this.getEffectiveData().length()>2 && this.isFlag()){

            String rawCrc="1104"+this.getPackageNum()+"01"+"00";
            replyPackage="401104"+this.getPackageNum()+"01"+"00"+ ByteUtil.hexSum(rawCrc)+"23";

        }else{
            String rawCrc="1104"+this.getPackageNum()+"01"+"01";
            replyPackage="401104"+this.getPackageNum()+"01"+"01"+ ByteUtil.hexSum(rawCrc)+"23";
        }
        this.sensorDetails= Lists.newArrayList();
        if (this.getEffectiveData().length()>10){
            //报警信息
        String all_m=this.getEffectiveData().substring(0,28);
        //测点信息
        EffectiveSmartData effectiveSmartData=new EffectiveSmartData(this.getEffectiveData().substring(28));

                if (all_m.substring(20).equals("00000000")){
                    //正常，只修改设备状态
                }else{
                    String binary= ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(all_m.substring(20),16)));
                    if (binary.substring(0,1).equals("1")){
                        has_alarm=true;
                    }else{
                        if (binary.substring(1,2).equals("1")){
                            has_guzhang=true;
                        }else{
                            if (binary.substring(2,3).equals("1")){
                                has_guzhang=true;
                            }else{
                                //正常
                            }
                        }
                    }
                }

            //第一路剩余电流

                String res1_status=effectiveSmartData.getRES_CURRENT1().substring(20);
                Object res1_value=Integer.parseInt(effectiveSmartData.getRES_CURRENT1_VALUE().substring(20),16)*0.1;
                sensorDetails.add(paringDeviceChannel(Constant.RES1,res1_status,res1_value));


            //第二路剩余电流

                String res2_status=effectiveSmartData.getRES_CURRENT2().substring(20);
                Object res2_value=Integer.parseInt(effectiveSmartData.getRES_CURRENT2_VALUE().substring(20),16)*0.1;
                sensorDetails.add(paringDeviceChannel(Constant.RES2,res2_status,res2_value));

            //第三路剩余电流

                String res3_status=effectiveSmartData.getRES_CURRENT3().substring(20);
                Object res3_value=Integer.parseInt(effectiveSmartData.getRES_CURRENT3_VALUE().substring(20),16)*0.1;
                sensorDetails.add(paringDeviceChannel(Constant.RES3,res3_status,res3_value));


            //第四路剩余电流
            String res4_status=effectiveSmartData.getRES_CURRENT4().substring(20);
            Object res4_value=Integer.parseInt(effectiveSmartData.getRES_CURRENT4_VALUE().substring(20),16)*0.1;
            sensorDetails.add(paringDeviceChannel(Constant.RES4,res4_status,res4_value));


            //第一路温度

            String temp1_status=effectiveSmartData.getTEMP1().substring(20);
            Object temp1_value=Integer.parseInt(effectiveSmartData.getTEMP1_VALUE().substring(20),16)/10-50;
            sensorDetails.add(paringDeviceChannel(Constant.TEMPT1,temp1_status,temp1_value));

            //第2路温度
            String temp2_status=effectiveSmartData.getTEMP2().substring(20);
            Object temp2_value=Integer.parseInt(effectiveSmartData.getTEMP2_VALUE().substring(20),16)/10-50;
            sensorDetails.add(paringDeviceChannel(Constant.TEMPT2,temp2_status,temp2_value));

            //第3路温度
            String temp3_status=effectiveSmartData.getTEMP3().substring(20);
            Object temp3_value=Integer.parseInt(effectiveSmartData.getTEMP3_VALUE().substring(20),16)/10-50;
            sensorDetails.add(paringDeviceChannel(Constant.TEMPT3,temp3_status,temp3_value));

            //第4路温度
            String temp4_status=effectiveSmartData.getTEMP4().substring(20);
            Object temp4_value=Integer.parseInt(effectiveSmartData.getTEMP4_VALUE().substring(20),16)/10-50;
            sensorDetails.add(paringDeviceChannel(Constant.TEMPT4,temp4_status,temp4_value));

            //A相电压
            String VOLa_status=effectiveSmartData.getVOLTAGE_A().substring(20);
            Object VOLa_value=Integer.parseInt(effectiveSmartData.getVOLTAGE_A_VALUE().substring(20),16)*0.1;
            sensorDetails.add(paringDeviceChannel(Constant.VLA,VOLa_status,VOLa_value));

            //B相电压
            String VOLb_status=effectiveSmartData.getVOLTAGE_B().substring(20);
            Object VOLb_value=Integer.parseInt(effectiveSmartData.getVOLTAGE_B_VALUE().substring(20),16)*0.1;
            sensorDetails.add(paringDeviceChannel(Constant.VLB,VOLb_status,VOLb_value));
            //C相电压
            String VOLc_status=effectiveSmartData.getVOLTAGE_C().substring(20);
            Object VOLc_value=Integer.parseInt(effectiveSmartData.getVOLTAGE_C_VALUE().substring(20),16)*0.1;
            sensorDetails.add(paringDeviceChannel(Constant.VLC,VOLc_status,VOLc_value));

            //A相电流
            String ELEA_status=effectiveSmartData.getELE_CURRENT_B().substring(20);
            Object ELEA_value=Integer.parseInt(effectiveSmartData.getELE_CURRENT_B_VALUE().substring(20),16)*0.1;
            sensorDetails.add(paringDeviceChannel(Constant.ECA,ELEA_status,ELEA_value));

            //B相电流
            String ELEB_status=effectiveSmartData.getELE_CURRENT_B().substring(20);
            Object ELEB_value=Integer.parseInt(effectiveSmartData.getELE_CURRENT_B_VALUE().substring(20),16)*0.1;
            sensorDetails.add(paringDeviceChannel(Constant.ECB,ELEB_status,ELEB_value));

            //C相电流
            String ELEC_status=effectiveSmartData.getELE_CURRENT_C().substring(20);
            Object ELEC_value=Integer.parseInt(effectiveSmartData.getELE_CURRENT_C_VALUE().substring(20),16)*0.1;
            sensorDetails.add(paringDeviceChannel(Constant.ECC,ELEC_status,ELEC_value));

            //A有功功率
            Object pa=Integer.parseInt(effectiveSmartData.getPA().substring(20),16)/1000;
            sensorDetails.add(paringDeviceChannelValuePA(Constant.PA,pa));

            //b有功功率
            Object pb=Integer.parseInt(effectiveSmartData.getPB().substring(20),16)/1000;
            sensorDetails.add(paringDeviceChannelValuePA(Constant.PB,pb));

            //c有功功率
            Object pc=Integer.parseInt(effectiveSmartData.getPC().substring(20),16)/1000;
            sensorDetails.add(paringDeviceChannelValuePA(Constant.PC,pc));

            //a功率因数

            Integer cosqa=Integer.parseInt(effectiveSmartData.getCOSQA().substring(20),16);
            String csa=ByteUtil.buqi(Integer.toBinaryString( cosqa));
            if (csa.substring(15).equals("1")){
                sensorDetails.add(paringDeviceChannelValuePA(Constant.PFA,-cosqa*0.1));
            }else{
                sensorDetails.add(paringDeviceChannelValuePA(Constant.PFA,cosqa*0.1));
            }


            //B功率因数
            Integer cosqb=Integer.parseInt(effectiveSmartData.getCOSQB().substring(20),16);
            String csb=ByteUtil.buqi(Integer.toBinaryString( cosqa));
            if (csb.substring(15).equals("1")){
                sensorDetails.add(paringDeviceChannelValuePA(Constant.PFB,-cosqb*0.1));
            }else{
                sensorDetails.add(paringDeviceChannelValuePA(Constant.PFB,cosqb*0.1));
            }


            //C功率因数
            Integer cosqc=Integer.parseInt(effectiveSmartData.getCOSQC().substring(20),16);
            String csc=ByteUtil.buqi(Integer.toBinaryString(cosqa));
            if (csc.substring(15).equals("1")){
                sensorDetails.add(paringDeviceChannelValuePA(Constant.PFC,-cosqc*0.1));
            }else{
                sensorDetails.add(paringDeviceChannelValuePA(Constant.PFC,cosqc*0.1));
            }
                //A相平率
            Object hza=Integer.parseInt(effectiveSmartData.getHZ_A().substring(20),16)*0.1;
            sensorDetails.add(paringDeviceChannelValuePA(Constant.HZA,hza));

            //B相平率
            Object hzb=Integer.parseInt(effectiveSmartData.getHZ_B().substring(20),16)*0.1;
            sensorDetails.add(paringDeviceChannelValuePA(Constant.HZB,hzb));

            //C相平率
            Object hzc=Integer.parseInt(effectiveSmartData.getHZ_C().substring(20),16)*0.1;
            sensorDetails.add(paringDeviceChannelValuePA(Constant.HZC,hzc));

            //A电量
            Object epa=Integer.parseInt(effectiveSmartData.getEPA().substring(20),16)*0.001;
            sensorDetails.add(paringDeviceChannelValuePA(Constant.EPA,epa));

            //B电量
            Object epb=Integer.parseInt(effectiveSmartData.getEPB().substring(20),16)*0.001;
            sensorDetails.add(paringDeviceChannelValuePA(Constant.EPB,epb));

            //C电量
            Object epc=Integer.parseInt(effectiveSmartData.getEPC().substring(20),16)*0.001;
            sensorDetails.add(paringDeviceChannelValuePA(Constant.EPC,epc));

        }else{
            if (getEffectiveData().substring(20).equals("00000000")){
                //正常，只修改设备状态
            }else{
                String binary= ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(this.getEffectiveData(),16)));
                if (binary.substring(0,1).equals("1")){
                    has_alarm=true;
                }else{
                    if (binary.substring(1,2).equals("1")){
                        has_guzhang=true;
                    }else{
                        if (binary.substring(2,3).equals("1")){
                            has_guzhang=true;
                        }else{
                            //正常
                        }
                    }
                }
            }

        }


    }

    public  SensorDetail paringDeviceChannel(String alarmCode,String status,Object value){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(alarmCode);
        String message= DeviceStatusEnum.getMessageByType(status);
        if (DeviceChannelConst.alarms.contains(status)){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            has_alarm=true;
        }else
        if (DeviceChannelConst.faults.contains(status)){
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            has_guzhang=true;
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
        sensorDetail.setAlarmType(message);
        sensorDetail.setAlarmValue(value);
        return sensorDetail;
    }

    /**
     * 功率计算
     * @param alarmCode
     * @return
     */
    public SensorDetail paringDeviceChannelValuePA(String alarmCode,Object value){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(alarmCode);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(value);
        return sensorDetail;
    }

}
