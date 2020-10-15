package cn.turing.common.entity.xinhaosi;



import cn.turing.common.base.Constant;
import cn.turing.common.entity.EasyLinkInDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import cn.turing.common.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;


import java.util.*;


@Data
public class XinHaoSiDeviceInfoV1 extends EasyLinkInDeviceInfo {
    //以下为设备相关信息
    private String data;
    private String head;
    private String length;
    private String cmd;
    private String address;
    private String Reg11;
    private String crc;
    private String partType;
    private String partTypeText;

    
    public XinHaoSiDeviceInfoV1(JSONObject jsonObject) throws Exception{
        super(jsonObject);
        if (ByteUtil.checkXHSDeviceCRC(this.getData())){
            this.sensorDetails = Lists.newArrayList();
            try {
                List<String> result = new ArrayList<String>();
                String header = data.substring(0, 4); //标识
                String length = "" + Integer.parseInt(data.substring(4, 6), 16); //长度
                String cmd = data.substring(6, 8); //cmd
                String partType = data.substring(8, 12);//设备类型
                String address = data.substring(12, 14);//设备地址
                String temp = data.substring(14, 54);//数据
                String workingstate = data.substring(54, 56);//工作状态
                String crc = data.substring(56);
                int iLen = temp.length();
                while (iLen >= 4) {
                    String tmp = temp.substring(0, 4);
                    result.add(tmp);
                    temp = temp.substring(4);
                    iLen = temp.length();
                }
                //三相
                if (partType.equalsIgnoreCase("0200")) {
                    // reg1-4  温度和漏电
                    sensorDetails.add(parseLouDian(result.get(0)));
                    sensorDetails.add(parseWenDu(result.get(1), Constant.TEMP1));
                    sensorDetails.add(parseWenDu(result.get(2),Constant.TEMP2));
                    sensorDetails.add(parseWenDu(result.get(3),Constant.TEMP3));
                    //reg5-7 A，B，C电压
                    sensorDetails.add(parseDianYa(result.get(4),Constant.VLA));
                    sensorDetails.add(parseDianYa(result.get(5),Constant.VLB));
                    sensorDetails.add(parseDianYa(result.get(6),Constant.VLC));

                    // reg8-10 A，b,c相电流
                    sensorDetails.add(parseDianLiu(result.get(7),Constant.ECA));
                    sensorDetails.add(parseDianLiu(result.get(8),Constant.ECB));
                    sensorDetails.add(parseDianLiu(result.get(9),Constant.ECC));
                }
                this.setAddress(address);
                this.setCmd(cmd);
                this.setCrc(crc);
                this.setHead(header);
                this.setLength(length);
                this.setReg11(workingstate);
                this.setPartType(partType);// 0200:三相组合式电气火灾设备 ，0201:  单相组合式电气火灾设备
                this.setPartTypeText(partType.equals("0200") ? "三相组合式电气火灾设备" : "单相组合式电气火灾设备");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
        }
    }

    private SensorDetail parseLouDian(String input) {
        String regTemp = ByteUtil.buqi(Integer.toBinaryString(Integer.parseInt(input, 16)));
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.LK);
        //非屏蔽，可以进行分析
        if(regTemp.substring(0, 1).equals("0")){
            if(regTemp.substring(1, 2).equals("1")){
                sensorDetail.setAlarmType("报警");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                has_alarm =true;
            }else{
                if(regTemp.substring(2, 3).equals("1")){
                    sensorDetail.setAlarmType("故障");
                    sensorDetail.setAlarmStatus(Constant.ST_WARN);
                    has_guzhang =true;
                }else{
                    sensorDetail.setAlarmType("正常");
                    sensorDetail.setAlarmStatus(Constant.ST_NORM);
                }
            }
            String reg = regTemp.substring(4, regTemp.length());
            Integer regVal = Integer.parseInt(reg, 2);
            sensorDetail.setAlarmValue(regVal);
        }else{
            sensorDetail.setAlarmType("屏蔽");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(0);
        }
        return sensorDetail;
    }


    private SensorDetail parseWenDu(String input,String alarmCode) {
        String regTemp = ByteUtil.buqi(Integer.toBinaryString(Integer.parseInt(input, 16)));
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(alarmCode);
        //非屏蔽，可以进行分析
        if(regTemp.substring(0, 1).equals("0")){
            if(regTemp.substring(1, 2).equals("1")){
                sensorDetail.setAlarmType("报警");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                has_alarm =true;
            }else{
                if(regTemp.substring(2, 3).equals("1")){
                    sensorDetail.setAlarmType("故障");
                    sensorDetail.setAlarmStatus(Constant.ST_WARN);
                    has_guzhang =true;
                }else{
                    sensorDetail.setAlarmType("正常");
                    sensorDetail.setAlarmStatus(Constant.ST_NORM);
                }
            }
            String reg = regTemp.substring(4, regTemp.length());
            double regVal = NumberUtil.div(Integer.parseInt(reg, 2),10,1);
            sensorDetail.setAlarmValue(regVal);
        }else{
            sensorDetail.setAlarmType("屏蔽");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(0);
        }
        return sensorDetail;
    }

    private SensorDetail parseDianYa(String input,String alarmCode) {
        //电压
        String regTemp = ByteUtil.buqi(Integer.toBinaryString(Integer.parseInt(input, 16)));
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(alarmCode);
        //非屏蔽，可以进行分析
        if(regTemp.substring(0, 1).equals("0")){
            if(regTemp.substring(2, 3).equals("1")){
                //错相
                sensorDetail.setAlarmType("错相");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                has_guzhang =true;
            }else if(regTemp.substring(5, 6).equals("1")){
                //供电中断
                sensorDetail.setAlarmType("供电中断");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                has_alarm =true;
            }else if(regTemp.substring(1, 2).equals("1")){
                //缺相
                sensorDetail.setAlarmType("缺相");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                has_guzhang =true;
            }else if(regTemp.substring(3, 4).equals("1")){
                //过压
                sensorDetail.setAlarmType("过压");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                has_guzhang =true;
            }else if(regTemp.substring(4, 5).equals("1")){
                //欠压
                sensorDetail.setAlarmType("欠压");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                has_guzhang =true;
            }else {
                sensorDetail.setAlarmType("正常");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
            }
            String reg = regTemp.substring(6, regTemp.length());
            Integer regVal = Integer.parseInt(reg, 2);
            sensorDetail.setAlarmValue(regVal);
        }else{
            sensorDetail.setAlarmType("屏蔽");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(0);
        }
        return sensorDetail;
    }
    private SensorDetail parseDianLiu(String input,String alarmCode) {
        String regTemp = ByteUtil.buqi(Integer.toBinaryString(Integer.parseInt(input, 16)));
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(alarmCode);
        //非屏蔽，可以进行分析
        if(regTemp.substring(0, 1).equals("0")){
            if(regTemp.substring(3, 4).equals("1")){
                sensorDetail.setAlarmType("过流");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                has_guzhang =true;
            }else{
                sensorDetail.setAlarmType("正常");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
            }
            String reg = regTemp.substring(6, regTemp.length());
            Integer regVal = Integer.parseInt(reg, 2);
            sensorDetail.setAlarmValue(regVal);
        }else{
            sensorDetail.setAlarmType("屏蔽");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(0);
        }
        return sensorDetail;
    }

    public JSONObject toDeviceMessage() {
        return null;
    }
}


