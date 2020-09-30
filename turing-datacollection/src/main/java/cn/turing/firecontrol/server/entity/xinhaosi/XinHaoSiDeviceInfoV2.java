package cn.turing.firecontrol.server.entity.xinhaosi;

import cn.turing.firecontrol.server.base.Constant;
import cn.turing.firecontrol.server.entity.EasyLinkInDeviceInfo;
import cn.turing.firecontrol.server.entity.Gateways;
import cn.turing.firecontrol.server.entity.SensorDetail;
import cn.turing.firecontrol.server.util.ByteUtil;
import cn.turing.firecontrol.server.util.NumberUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.log4j.Log4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;

@Log4j
@Data
public class XinHaoSiDeviceInfoV2 extends EasyLinkInDeviceInfo {
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

    public XinHaoSiDeviceInfoV2(JSONObject jsonObject) throws Exception{
        super(jsonObject);

        log.info("解析16进制数据开始");
        if (ByteUtil.checkXHSDeviceCRC(this.getData())){
            this.sensorDetails = Lists.newArrayList();
            try {
                List<String> result = new ArrayList<String>();
                String header = data.substring(0, 4); //标识
                String length = "" + Integer.parseInt(data.substring(4, 6), 16); //长度
                String cmd = data.substring(6, 8); //cmd
                String partType = data.substring(8, 12);//设备类型
                String address = data.substring(12, 14);//设备地址
                String states = ByteUtil.reverse(ByteUtil.hexString2binaryString(data.substring(14, 18))); //标识
                String temp = data.substring(18, 58);//数据
                String workingstate = data.substring(58, 60);//工作状态
                String crc = data.substring(60);
                int iLen = temp.length();
                while (iLen >= 4) {
                    String tmp = temp.substring(0, 4);
                    result.add(tmp);
                    temp = temp.substring(4);
                    iLen = temp.length();
                }
                //状态位解析
                //BitSet
                //三相
                if (partType.equalsIgnoreCase("0200")) {
                    // reg1-4  温度和漏电
                    sensorDetails.add(parseLouDian(result.get(0),states.substring(0,1).equalsIgnoreCase("1")));
                    sensorDetails.add(parseWenDu(result.get(1),Constant.TEMP1,states.substring(1,2).equalsIgnoreCase("1")));
                    sensorDetails.add(parseWenDu(result.get(2),Constant.TEMP2,states.substring(2,3).equalsIgnoreCase("1")));
                    sensorDetails.add(parseWenDu(result.get(3),Constant.TEMP3,states.substring(3,4).equalsIgnoreCase("1")));
                    //reg5-7 A，B，C电压
                    sensorDetails.add(parseDianYa(result.get(4),Constant.VLA,states.substring(4,5).equalsIgnoreCase("1")));
                    sensorDetails.add(parseDianYa(result.get(5),Constant.VLB,states.substring(5,6).equalsIgnoreCase("1")));
                    sensorDetails.add(parseDianYa(result.get(6),Constant.VLC,states.substring(6,7).equalsIgnoreCase("1")));

                    // reg8-10 A，b,c相电流
                    sensorDetails.add(parseDianLiu(result.get(7),Constant.ECA,states.substring(7,8).equalsIgnoreCase("1")));
                    sensorDetails.add(parseDianLiu(result.get(8),Constant.ECB,states.substring(8,9).equalsIgnoreCase("1")));
                    sensorDetails.add(parseDianLiu(result.get(9),Constant.ECC,states.substring(9,10).equalsIgnoreCase("1")));
                }
                this.setAddress(address);
                this.setCmd(cmd);
                this.setCrc(crc);
                this.setHead(header);
                this.setLength(length);
                this.setReg11(workingstate);
                this.setPartType(partType);// 0200:三相组合式电气火灾设备 ，0201:  单相组合式电气火灾设备
                this.setPartTypeText(partType.equals("0200") ? "三相组合式电气火灾设备" : "单相组合式电气火灾设备");
                log.info("解析16进制数据完毕");
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("解析16进制数据完成");
        }else{
            log.info("非可识别的数据");
        }
    }

    private SensorDetail parseLouDian(String input,boolean changed) {
        String regTemp = ByteUtil.buqi(Integer.toBinaryString(Integer.parseInt(input, 16)));
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(Constant.LK);
        //非屏蔽，可以进行分析
        if(regTemp.substring(0, 1).equals("0")){
            if(regTemp.substring(1, 2).equals("1")){
                sensorDetail.setAlarmType("报警");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                has_alarm =true;
                sensorDetail.setChanged(changed);
            }else{
                if(regTemp.substring(2, 3).equals("1")){
                    sensorDetail.setAlarmType("故障");
                    sensorDetail.setAlarmStatus(Constant.ST_WARN);
                    has_guzhang =true;
                    sensorDetail.setChanged(changed);
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


    private SensorDetail parseWenDu(String input,String alarmCode,boolean changed) {
        String regTemp = ByteUtil.buqi(Integer.toBinaryString(Integer.parseInt(input, 16)));
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(alarmCode);
        //非屏蔽，可以进行分析
        if(regTemp.substring(0, 1).equals("0")){
            if(regTemp.substring(1, 2).equals("1")){
                sensorDetail.setAlarmType("报警");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                has_alarm =true;
                sensorDetail.setChanged(changed);
            }else{
                if(regTemp.substring(2, 3).equals("1") ){
                    sensorDetail.setAlarmType("故障");
                    sensorDetail.setAlarmStatus(Constant.ST_WARN);
                    has_guzhang =true;
                    sensorDetail.setChanged(changed);
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

    private SensorDetail parseDianYa(String input,String alarmCode,boolean changed) {
        //电压
        String regTemp = ByteUtil.buqi(Integer.toBinaryString(Integer.parseInt(input, 16)));
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(alarmCode);
        //非屏蔽，可以进行分析
        if(regTemp.substring(0, 1).equals("0")){
            if(regTemp.substring(2, 3).equals("1")  ){
                //错相
                sensorDetail.setAlarmType("错相");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                has_guzhang =true;
                sensorDetail.setChanged(changed);
            }else if(regTemp.substring(5, 6).equals("1")  ){
                //供电中断
                sensorDetail.setAlarmType("供电中断");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                has_alarm =true;
                sensorDetail.setChanged(changed);
            }else if(regTemp.substring(1, 2).equals("1")  ){
                //缺相
                sensorDetail.setAlarmType("缺相");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                has_guzhang =true;
                sensorDetail.setChanged(changed);
            }else if(regTemp.substring(3, 4).equals("1") ){
                //过压
                sensorDetail.setAlarmType("过压");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                has_guzhang =true;
                sensorDetail.setChanged(changed);
            }else if(regTemp.substring(4, 5).equals("1") ){
                //欠压
                sensorDetail.setAlarmType("欠压");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                has_guzhang =true;
                sensorDetail.setChanged(changed);
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
    private SensorDetail parseDianLiu(String input,String alarmCode,boolean changed) {
        String regTemp = ByteUtil.buqi(Integer.toBinaryString(Integer.parseInt(input, 16)));
        SensorDetail sensorDetail = new SensorDetail();
        sensorDetail.setAlarmCode(alarmCode);
        //非屏蔽，可以进行分析
        if(regTemp.substring(0, 1).equals("0")){
            if(regTemp.substring(3, 4).equals("1") ){
                sensorDetail.setAlarmType("过流");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                has_guzhang =true;
                sensorDetail.setChanged(changed);
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
    public JSONObject toAlarmJSON() {
        //{"deviceid":"设备的ID","uploaddate":"上传时间","recievedate":"接受时间","alarms":[{"alarmCode":"约定的缩写","alarmType":"约定的缩写","alarmValue":"约定的值"}]}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceid",this.getDevice_id());
        jsonObject.put("uploadtime",sdf.format(this.getUpload_time()));
        jsonObject.put("recievetime",sdf.format(this.getRecieve_time()));

        boolean hasAlarm = false;
        boolean hasGuzhang = false;

        JSONArray jsonArray = new JSONArray();
        for(SensorDetail sensorDetail:getSensorDetails()){
            if(sensorDetail.getAlarmStatus()<2 && sensorDetail.getChanged()){
                JSONObject jo = new JSONObject();
                jo.put("alarmCode",sensorDetail.getAlarmCode());
                jo.put("alarmType",sensorDetail.getAlarmType());
                jo.put("alarmValue",sensorDetail.getAlarmValue());
                jo.put("alarmStatus",sensorDetail.getAlarmStatus());
                if(sensorDetail.getAlarmStatus()==0) hasGuzhang =true;
                if(sensorDetail.getAlarmStatus()==1) hasAlarm =true;
                jsonArray.add(jo);
            }
        }
        if(hasAlarm){
            jsonObject.put("status",Integer.toString(Constant.ST_ALARM));
        }else if(hasGuzhang){
            jsonObject.put("status",Integer.toString(Constant.ST_WARN));
        }else{
            return null;
        }
        jsonObject.put("alarms",jsonArray);
        return jsonObject;
    }

}


