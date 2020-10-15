package cn.turing.common.entity.acrel.electricalFireV1;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.entity.acrel.AcrelDeviceInfo;
import cn.turing.common.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 安瑞科智慧用电
 */

@Data
public class MonitoringDataSensor extends AcrelDeviceInfo {
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    public Integer message_body_length;//消息体长度
    public Integer signal_strength;//当前信号值
    public String voltagePhaseOK;//电压相序标志位
    public String nLTValueFault;//漏电温度接线故障标志位
    public String Fault_temp1;//温度1  温度通道 1-4；
    public String Fault_temp2;//温度1
    public String Fault_temp3;//温度1
    public String Fault_temp4;//温度1
    public String nLTWarning;//漏电温度报警标志位
    public String Warning_temp1;//温度1  温度通道 1-4；
    public String Warning_temp2;//温度1
    public String Warning_temp3;//温度1
    public String Warning_temp4;//温度1
    public String OVWarning;//电压状态标志位  Bit0：过压状态位；Bit4：
    public String LVWarning;//欠压状态位
    public String OCWarning;//过流状态位；  电流状态标志位
    public String DI1;  //开关量输入状态  Bit0-Bit1:表示 DI1~DI2 状态；1：DI 闭合；0：DI 打开
    public String DI2;
    public String DO1;  //开关量输出状态  Bit0-Bit1:表示 DI1~DI2 状态；1：DI 闭合；0：DI 打开
    public String DO2;
    public Float soc_value;//剩余电流测量值
    public Float Fault_temp1_value;//温度 1 测量值
    public Float Fault_temp2_value;//温度 1 测量值
    public Float Fault_temp3_value;//温度 1 测量值
    public Float Fault_temp4_value;//温度 1 测量值
    public Float soc_alarm_value;//剩余电流报警测量值
    public Float Warning_temp1_value;//温度 1 测量值
    public Float Warning_temp2_value;//温度 1 测量值
    public Float Warning_temp3_value;//温度 1 测量值
    public Float Warning_temp4_value;//温度 1 测量值
    public Float Hz_value;//频率
    public Float Voltage_imbalance_value;//电压不平衡度
    public Float Vol_A_value; //A相电压
    public Float Vol_B_value; //b相电压
    public Float Vol_C_value; //C相电压
    public Float Vol_average_value;//相电压平均值
    public Float zero_voltage_value;//零序电压
    public Float AB_voltage;//ab相线电压
    public Float AC_voltage;//ab相线电压
    public Float BC_voltage;//ab相线电压
    public Float Line_voltage;//线电压平均值
    public Float A_over_value;//a相过压值
    public Float B_over_value;//B相过压值
    public Float C_over_value;//C相过压值
    public Float A_under_value;//a相欠压值
    public Float B_under_value;//B相欠压值
    public Float C_under_value;//C相欠压值
    public Float current_imbalance_value;//电流不平衡度
    public Float Curr_A_value; //A相电流
    public Float Curr_B_value; //b相电流
    public Float Curr_C_value; //C相电流
    public Float Curr_average_value;//电流平均值
    public Float zero_current_value;//零序电流
    public Float curr_A_alarm_value;//a相过流
    public Float curr_B_alarm_value;//B相过流
    public Float curr_C_alarm_value;//C相过流
    public Float A_active_power;//A 相有功功率
    public Float B_active_power;//B 相有功功率
    public Float C_active_power;//C 相有功功率
    public Float sum_active_power;//总有功功率
    public Float A_reactive_power;//A 相无功功率
    public Float B_reactive_power;//B 相无功功率
    public Float C_reactive_power;//B 相无功功率
    public Float sum_reactive_power;//总无功功率
    public Float A_at_power;//A 相视在功率
    public Float B_at_power;//B 相视在功率
    public Float C_at_power;//C 相视在功率
    public Float sum_at_power;//总视在功率
    public Float A_power_factor;//A 相功率因数
    public Float B_power_factor;//B 相功率因数
    public Float C_power_factor;//C 相功率因数
    public Float sum_power_factor;//总相功率因数
    public Float EPI_value;//输入有功电能，单位为 kWh
    public Float EPE_value;//输出有功电能，单位为 kWh
    public Float EQL_value;//输入无功电能，单位为 kvarh
    public Float EQC_value;//输出无功电能，单位为 kvarh
    public Float ES_value;//视在电能，单位为 kVAh
    public Float A_vol_harmonic;//A 相电压总谐波含量
    public Float B_vol_harmonic;//b 相电压总谐波含量
    public Float C_vol_harmonic;//c相电压总谐波含量
    public Float A_curr_harmonic;//A 相电压总谐波含量
    public Float B_curr_harmonic;//b相电压总谐波含量
    public Float C_curr_harmonic;//c 相电压总谐波含量
    public Float EPI_RealTime_demand;//实时需量，单位为 kWh
    public Float EPE_RealTime_demand;//输
    public Float EQL_RealTime_demand;//
    public Float EQC_RealTime_demand;//
    public Float ES_RealTime_demand;//
    public Float today_value;//当日最大需量
    public String today_time;//当日最大需量时间

    public MonitoringDataSensor(String s) {
        super(s);
        try {

            message_body_length = Integer.parseInt(s.substring(6, 10).substring(0, 2) + s.substring(6, 10).substring(2), 16);
            try {
                this.upload_time = simpleDateFormat.parse(uploadTime(s));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            device_id = deviceId(s.substring(22, 50));
            signal_strength = Integer.parseInt(s.substring(78, 82), 16);
            voltagePhaseOK = ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(s.substring(82, 86), 16))).substring(0, 1);
            String nLTF = ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(s.substring(86, 90), 16)));
            nLTValueFault = nLTF.substring(0, 1);
            Fault_temp1 = nLTF.substring(1, 2);
            Fault_temp2 = nLTF.substring(2, 3);
            Fault_temp3 = nLTF.substring(3, 4);
            Fault_temp4 = nLTF.substring(4, 5);
            String nLTW = ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(s.substring(90, 94), 16)));
            nLTWarning = nLTW.substring(0, 1);
            Warning_temp1 = nLTW.substring(1, 2);
            Warning_temp2 = nLTW.substring(2, 3);
            Warning_temp3 = nLTW.substring(3, 4);
            Warning_temp4 = nLTW.substring(4, 5);
            String nVol = ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(s.substring(98, 102), 16)));
            OVWarning = nVol.substring(0, 1);
            LVWarning = nVol.substring(4, 5);
            String nCurr = ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(s.substring(102, 106), 16)));
            OCWarning = nCurr.substring(0, 1);
            String nDI = ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(s.substring(106, 110), 16)));
            DI1 = nDI.substring(0, 1);
            DI2 = nDI.substring(1, 2);
            String nDO = ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(s.substring(110, 114), 16)));
            DO1 = nDO.substring(0, 1);
            DO2 = nDO.substring(1, 2);
            soc_value = calculate(s.substring(114, 122));
            Fault_temp1_value = calculate(s.substring(122, 130));
            System.out.println(s.substring(122, 130)+"temp1");
            Fault_temp2_value = calculate(s.substring(130, 138));
            System.out.println(s.substring(130, 138)+"+temp2");
            Fault_temp3_value = calculate(s.substring(138, 146));
            System.out.println(s.substring(138, 146)+"+temp3");
            Fault_temp4_value = calculate(s.substring(146, 154));
            System.out.println(s.substring(146, 154)+"+temp4");
            soc_alarm_value = calculate(s.substring(154, 162));
            Warning_temp1_value = calculate(s.substring(162, 170));
            Warning_temp2_value = calculate(s.substring(170, 178));
            Warning_temp3_value = calculate(s.substring(178, 186));
            Warning_temp4_value = calculate(s.substring(186, 194));
            Hz_value = calculate(s.substring(194, 202));
            Voltage_imbalance_value = calculate(s.substring(202, 210));
            Vol_A_value = calculate(s.substring(210, 218));
            Vol_B_value = calculate(s.substring(218, 226));
            Vol_C_value = calculate(s.substring(226, 234));
            Vol_average_value=calculate(s.substring(234, 242));
            zero_voltage_value = calculate(s.substring(242, 250));
            AB_voltage = calculate(s.substring(250, 258));
            AC_voltage = calculate(s.substring(258, 266));
            BC_voltage = calculate(s.substring(266, 274));
            Line_voltage = calculate(s.substring(274, 282));//线电压平均值
            A_over_value = calculate(s.substring(282, 290));//a相过压值
            B_over_value = calculate(s.substring(290, 298));//B相过压值
            C_over_value = calculate(s.substring(298, 306));//C相过压值
            A_under_value = calculate(s.substring(306, 314));//a相过压值
            B_under_value = calculate(s.substring(314, 322));//B相过压值
            C_under_value = calculate(s.substring(322, 330));//C相过压值
            current_imbalance_value = calculate(s.substring(330, 338));//电流不平衡度
            Curr_A_value = calculate(s.substring(338, 346));//A相电流
            Curr_B_value = calculate(s.substring(346, 354));//b相电流
            Curr_C_value = calculate(s.substring(354, 362)); //C相电流
            Curr_average_value = calculate(s.substring(362, 370));//电流平均值
            zero_current_value = calculate(s.substring(370, 378));//零序电流
            curr_A_alarm_value = calculate(s.substring(378, 386));//a相过流
            curr_B_alarm_value = calculate(s.substring(386, 394));//B相过流
            curr_C_alarm_value = calculate(s.substring(394, 402));//C相过流
            A_active_power = calculate(s.substring(402, 410));//A 相有功功率
            B_active_power = calculate(s.substring(410, 418));//B 相有功功率
            C_active_power = calculate(s.substring(418, 426));//C 相有功功率
            sum_active_power = calculate(s.substring(426, 434));//总有功功率
            A_reactive_power = calculate(s.substring(434, 442));//A 相无功功率
            B_reactive_power = calculate(s.substring(442, 450));//B 相无功功率
            C_reactive_power = calculate(s.substring(450, 458));//B 相无功功率
            sum_reactive_power = calculate(s.substring(458, 466));//总无功功率
            A_at_power = calculate(s.substring(466, 474));//A 相视在功率
            B_at_power = calculate(s.substring(474, 482));//B 相视在功率
            C_at_power = calculate(s.substring(482, 490));//C 相视在功率
            sum_at_power = calculate(s.substring(490, 498));//总视在功率
            A_power_factor = calculate(s.substring(498,506));//A 相功率因数
            B_power_factor = calculate(s.substring(506, 514));//B 相功率因数
            C_power_factor = calculate(s.substring(514, 522));//C 相功率因数
            sum_power_factor = calculate(s.substring(522, 530));//总相功率因数
            EPI_value = calculate(s.substring(530, 538));//输入有功电能，单位为 kWh
            EPE_value = calculate(s.substring(538, 546));//输出有功电能，单位为 kWh
            EQL_value = calculate(s.substring(546, 554));//输入无功电能，单位为 kvarh
            EQC_value = calculate(s.substring(554, 562));//输出无功电能，单位为 kvarh
            ES_value = calculate(s.substring(562, 570));//视在电能，单位为 kVAh
            A_vol_harmonic = calculate(s.substring(570, 578));//A 相电压总谐波含量
            B_vol_harmonic = calculate(s.substring(578, 586));//b 相电压总谐波含量
            C_vol_harmonic = calculate(s.substring(586, 594));//c相电压总谐波含量
            A_curr_harmonic = calculate(s.substring(594, 602));//A 相电压总谐波含量
            B_curr_harmonic = calculate(s.substring(602, 610));//b相电压总谐波含量
            C_curr_harmonic = calculate(s.substring(610, 618));//c 相电压总谐波含量
            EPI_RealTime_demand = calculate(s.substring(618, 626));//实时需量，单位为 kWh
            EPE_RealTime_demand = calculate(s.substring(626, 634));//输
            EQL_RealTime_demand = calculate(s.substring(634, 642));//
            EQC_RealTime_demand = calculate(s.substring(642, 650));//
            ES_RealTime_demand = calculate(s.substring(650, 658));//+32=708
            today_value = calculate(s.substring(722, 730));
            today_time = String.valueOf(Integer.parseInt(s.substring(730, 732),16)) + "." +
                    String.valueOf(Integer.parseInt(s.substring(732, 734),16)) + " " +
                    String.valueOf(Integer.parseInt(s.substring(734, 736),16)) + ":" +
                    String.valueOf(Integer.parseInt(s.substring(736, 738),16));
            this.sensorDetails = Lists.newArrayList();
            sensorDetails.add(louDianAndCurr(nLTWarning));
            sensorDetails.add(louDianAndTemp1(Warning_temp1));
            sensorDetails.add(louDianAndTemp2(Warning_temp2));
            sensorDetails.add(louDianAndTemp3(Warning_temp3));
            sensorDetails.add(louDianAndTemp4(Warning_temp4));
            sensorDetails.add(dianYaA(OVWarning, LVWarning));
            sensorDetails.add(dianYaB(OVWarning, LVWarning));
            sensorDetails.add(dianYaC(OVWarning, LVWarning));
            sensorDetails.add(hz());
            sensorDetails.add(DO1(DO1));
            sensorDetails.add(DO2(DO2));
            sensorDetails.add(DI1(DI1));
            sensorDetails.add(DI2(DI2));
            sensorDetails.add(VOLI());
            sensorDetails.add(VOLM());
            sensorDetails.add(VOLZ());
            sensorDetails.add(VOLAB());
            sensorDetails.add(VOLAC());
            sensorDetails.add(VOLBC());
            sensorDetails.add(VOLX());
            sensorDetails.add(guoLiuA(OCWarning));
            sensorDetails.add(guoLiuB(OCWarning));
            sensorDetails.add(guoLiuC(OCWarning));
            sensorDetails.add(ECCI());
            sensorDetails.add(ECCM());
            sensorDetails.add(ECCZ());
            sensorDetails.add(PFA());
            sensorDetails.add(PFB());
            sensorDetails.add(PFC());
            sensorDetails.add(today());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject toDeviceJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceid", this.getDevice_id().toLowerCase());
        jsonObject.put("uploadtime",this.getUpload_time());
        jsonObject.put("todayTime",this.getToday_time());
        jsonObject.put("recievetime",this.getRecieve_time());
        if(this.getSensorDetails()!=null) {
            for (SensorDetail sensorDetail:this.getSensorDetails()) {
                JSONObject val = new JSONObject();
                val.put("alarmValue",sensorDetail.getAlarmValue());
                val.put("alarmType",sensorDetail.getAlarmType());
                val.put("alarmStatus",sensorDetail.getAlarmStatus());
                jsonObject.put(sensorDetail.getAlarmCode(),val);
            }
        }
        return jsonObject;
    }

    /**
     * 补齐
     *
     * @param string 年月日时分秒
     * @return
     */
    public String AFilling(String string) {
        String data = "";
        if (string.length() < 2) {
            data = "0" + string;
        } else {
            data = string;
        }
        return data;
    }

    /**
     * 数据产生时间
     *
     * @param s
     * @return
     */
    public String uploadTime(String s) {
        String year = String.valueOf(Integer.parseInt(s.substring(10, 12), 16) + 2000); //year
        String month = AFilling(String.valueOf(Integer.parseInt(s.substring(12, 14), 16)));//月
        String day = AFilling(String.valueOf(Integer.parseInt(s.substring(14, 16), 16)));//日
        String hours = AFilling(String.valueOf(Integer.parseInt(s.substring(16, 18), 16)));//时
        String min = AFilling(String.valueOf(Integer.parseInt(s.substring(18, 20), 16))); //分
        String socd = AFilling(String.valueOf(Integer.parseInt(s.substring(20, 22), 16)));  //秒
        return year + month + day + hours + min + socd;
    }

    /**
     * 解析设备编号
     *
     * @param s
     * @return
     */
    public String deviceId(String s) {
        s=s.replace("3","");
       return s;
    }

    public Float calculate(String data) {
        Float f=0.f;
        try{
         f = Float.intBitsToFloat(Integer.valueOf(data.substring(6) + data.substring(4, 6) + data.substring(2, 4) + data.substring(0, 2), 16));

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return f;
    }

    public static void main(String[] args) {
       String data="cdccd841";
   //    String data="cdcc4cc1";
        System.out.println(Float.intBitsToFloat(Integer.valueOf(data.substring(6)+data.substring(4,6)+data.substring(2,4)+data.substring(0,2), 16)));

    }
    /**
     * 漏电 ,LK
     * @param status
     * @return
     */
    public SensorDetail louDianAndCurr(String status){
        SensorDetail  sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.LK);
        if (status.equals("1")){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmValue(soc_alarm_value);
            has_alarm=true;
        }else if (status.equals("0")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(soc_value);
        }
        return  sensorDetail;
    }

    /**
     * 漏电温度通道1
     * @param status
     * @return
     */
    public SensorDetail louDianAndTemp1(String status){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMPA1);
        if (status.equals("1")||Warning_temp1_value>60 ||Fault_temp1_value>60){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmValue(Warning_temp1_value);
            this.has_alarm=true;
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Fault_temp1_value);
        }
        return sensorDetail;
    }

    /**
     * 漏电温度通道2
     * @param status
     * @return
     */
    public SensorDetail louDianAndTemp2(String status){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMPA2);
        if (status.equals("1")||Warning_temp2_value>60 ||Fault_temp2_value>60){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmValue(Warning_temp2_value);
            this.has_alarm=true;
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Fault_temp2_value);
        }
        return sensorDetail;
    }
    /**
     * 漏电温度通道3
     * @param status
     * @return
     */
    public SensorDetail louDianAndTemp3(String status){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMPA3);
        if (status.equals("1")||Warning_temp3_value>60 ||Fault_temp3_value>60){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmValue(Warning_temp3_value);
            this.has_alarm=true;
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Fault_temp3_value);
        }
        return sensorDetail;
    }
    /**
     * 漏电温度通道4
     * @param status
     * @return
     */
    public SensorDetail louDianAndTemp4(String status){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TEMPA4);
        if (status.equals("1")||Warning_temp4_value>60 ||Fault_temp4_value>60){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmValue(Warning_temp4_value);
            this.has_alarm=true;
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Fault_temp4_value);
        }
        return sensorDetail;
    }

    /**
     * A相电压
     * @return
     */
    public SensorDetail dianYaA(String stat_dowm,String stat_up){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VLA);
        if (stat_dowm.equals("0") && stat_up.equals("0")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Vol_A_value);

        }else if (stat_dowm.equals("1")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("过压");
            sensorDetail.setAlarmValue(A_over_value);
            this.has_alarm=true;
        } else if (stat_up.equals("1")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("欠压");
            sensorDetail.setAlarmValue(A_under_value);
            this.has_alarm=true;
        }
        return  sensorDetail;
    }
    public SensorDetail dianYaB(String stat_dowm,String stat_up){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VLB);
        if (stat_dowm.equals("0") && stat_up.equals("0")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Vol_B_value);
        }else if (stat_dowm.equals("1")){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("过压");
            sensorDetail.setAlarmValue(B_over_value);
            this.has_alarm=true;
        } else if (stat_up.equals("1")){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("欠压");
            sensorDetail.setAlarmValue(B_under_value);
            this.has_alarm=true;
        }
        return  sensorDetail;
    }
    public SensorDetail dianYaC(String stat_dowm,String stat_up){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VLC);
        if (stat_dowm.equals("0") && stat_up.equals("0")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Vol_C_value);
        }else if (stat_dowm.equals("1")){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("过压");
            sensorDetail.setAlarmValue(C_over_value);
            this.has_alarm=true;
        } else if (stat_up.equals("1")){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("欠压");
            sensorDetail.setAlarmValue(C_under_value);
            this.has_alarm=true;
        }
        return  sensorDetail;
    }

    /**
     * 频率
     * @return
     */
    public SensorDetail hz(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.HZ);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(Hz_value);
        return sensorDetail;
    }

    /**
     *
     * @param status
     * @return
     */
    public SensorDetail DO1(String status){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.DO1);
        if (status.equals("1")){
            sensorDetail.setAlarmType("闭合");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
        }else{
            sensorDetail.setAlarmType("打开");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return sensorDetail;
    }
    public SensorDetail DO2(String status){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.DO2);
        if (status.equals("1")){
            sensorDetail.setAlarmType("闭合");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
        }else{
            sensorDetail.setAlarmType("打开");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return sensorDetail;
    }
    public SensorDetail DI1(String status){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.DI1);
        if (status.equals("1")){
            sensorDetail.setAlarmType("闭合");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
        }else{
            sensorDetail.setAlarmType("打开");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return sensorDetail;
    }
    public SensorDetail DI2(String status){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.DI2);
        if (status.equals("1")){
            sensorDetail.setAlarmType("闭合");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
        }else{
            sensorDetail.setAlarmType("打开");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return sensorDetail;
    }


    public SensorDetail VOLI(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOLI);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(Voltage_imbalance_value);
        return  sensorDetail;
    }
    public SensorDetail VOLM(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOLM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(Vol_average_value);
        return  sensorDetail;
    }
    public SensorDetail VOLZ(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOLZ);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(zero_voltage_value);
        return  sensorDetail;
    }
    public SensorDetail VOLAB(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOLAB);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(AB_voltage);
        return  sensorDetail;
    }
    public SensorDetail VOLAC(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOLAC);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(AC_voltage);
        return  sensorDetail;
    }
    public SensorDetail VOLBC(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOLBC);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(BC_voltage);
        return  sensorDetail;
    }
    public SensorDetail VOLX(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOLX);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(Line_voltage);
        return  sensorDetail;
    }

    public SensorDetail guoLiuA(String stuas){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECA);
        if (stuas.equals("0") ){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Curr_A_value);
        }else if (stuas.equals("1")){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("过流");
            sensorDetail.setAlarmValue(curr_A_alarm_value);
            this.has_alarm=true;
        }
        return  sensorDetail;
    }
    public SensorDetail guoLiuB(String stuas){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECB);
        if (stuas.equals("0") ){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Curr_B_value);
        }else if (stuas.equals("1")){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("过流");
            sensorDetail.setAlarmValue(curr_B_alarm_value);
            this.has_alarm=true;
        }
        return  sensorDetail;
    }
    public SensorDetail guoLiuC(String stuas){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECC);
        if (stuas.equals("0") ){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Curr_C_value);
        }else if (stuas.equals("1")){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("过流");
            sensorDetail.setAlarmValue(curr_C_alarm_value);
            this.has_alarm=true;
        }
        return  sensorDetail;
    }
    public SensorDetail ECCI(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECCI);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(current_imbalance_value);
        return  sensorDetail;
    }
    public SensorDetail ECCZ(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECCZ);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(zero_current_value);
        return  sensorDetail;
    }
    public SensorDetail ECCM(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.ECCM);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(Curr_average_value);
        return  sensorDetail;
    }
    public static String PFA="PFA";//A 相功率因数
    public static String PFB="PFB";
    public static String PFC="PFC";
    public static String TODAY="TODAY";

    public SensorDetail PFA(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.PFA);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(A_power_factor);
        return  sensorDetail;
    }
    public SensorDetail PFB(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.PFB);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(B_power_factor);
        return  sensorDetail;
    }
    public SensorDetail PFC(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.PFC);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(C_power_factor);
        return  sensorDetail;
    }
    public SensorDetail today(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.TODAY);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(today_value);
        return  sensorDetail;
    }


}
