package cn.turing.common.entity.topsail;



import cn.turing.common.base.Constant;
import cn.turing.common.entity.DeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import cn.turing.common.util.NumberUtil;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
public class YaLiDeviceInfoV1 extends DeviceInfo {
    //以下为设备信息
    String data;
    String name;
    String msgType;
    Integer dianya;
    Integer qiangdu;

    public YaLiDeviceInfoV1(){

    }

    public static List<YaLiDeviceInfoV1> parse(String msg) {
        List<YaLiDeviceInfoV1> yaLiDeviceInfoV1List = Lists.newArrayList();
        Date recivevtime = new Date();
        DateTimeFormatter simpleDateFormat = DateTimeFormat .forPattern("yyyyMMddHHmmss");
        String data =msg;
        String imei = ByteUtil.hexStringToString(msg.substring(8,40));
        String name = ByteUtil.hexStringToString(msg.substring(40,72));
        String msgType = msg.substring(72,74);
        String reserved = msg.substring(74,76);
        Integer msglen = Integer.valueOf(msg.substring(76,80),16);
        String msgbody = msg.substring(80);
        if("001f".equalsIgnoreCase(msgbody)){
            return yaLiDeviceInfoV1List;
        }
        DateTime dateTime = DateTime.parse("20"+msg.substring(80,92),simpleDateFormat);
        Integer jiange = Integer.parseInt(msg.substring(92,94),16);
        Integer dianliang = Integer.parseInt(msg.substring(94,96),16);
        Integer qiangdu = Integer.parseInt(msg.substring(96,98),16);

        for(int i = 0;i<((msglen-10)/4);i++){
            String subdata = msg.substring(100+i*8,108+i*8);
            YaLiDeviceInfoV1 yaLiDeviceInfoV1 = new YaLiDeviceInfoV1();
            yaLiDeviceInfoV1.setData(data);
            yaLiDeviceInfoV1.setName(name);
            yaLiDeviceInfoV1.setMsgType(msgType);
            yaLiDeviceInfoV1.setDevice_id(imei);
            yaLiDeviceInfoV1.setRecieve_time(recivevtime);
            yaLiDeviceInfoV1.setUpload_time(dateTime.plusMinutes(i*1).toDate());
            yaLiDeviceInfoV1.setDianya(dianliang);
            yaLiDeviceInfoV1.setQiangdu(qiangdu);

            SensorDetail sensorDetail = new SensorDetail();
            sensorDetail.setAlarmCode(Constant.YL);
            yaLiDeviceInfoV1.sensorDetails = Lists.newArrayList();
            String alarmState = subdata.substring(0,1);//0 表示数据正常，1 表示数据低压超限，2表示数据高压超限，3 表示设备故障
            Integer point = Integer.parseInt(subdata.substring(2,3));
            Integer tmpvalue = Integer.parseInt(subdata.substring(3));
            //目前只有烟感报警，其余的
            double regVal = tmpvalue.doubleValue();
            if(point.intValue()>0) {
                regVal = NumberUtil.div(tmpvalue, getCiF(10,point), point);
            }
            sensorDetail.setAlarmValue(regVal);
            if(alarmState.equalsIgnoreCase("1")){
                sensorDetail.setAlarmType("低压");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                yaLiDeviceInfoV1.has_alarm =true;
            }else if(alarmState.equalsIgnoreCase("2")){
                sensorDetail.setAlarmType("高压");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                yaLiDeviceInfoV1.has_alarm =true;
            }else if(alarmState.equalsIgnoreCase("3")){//需要改为3吗
                sensorDetail.setAlarmType("故障");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                yaLiDeviceInfoV1.has_guzhang =true;
            }else if(dianliang.intValue()<5){
                sensorDetail.setAlarmType("低电");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                yaLiDeviceInfoV1.has_guzhang =true;
            }
            else{
                sensorDetail.setAlarmType("正常");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
            }
            yaLiDeviceInfoV1.sensorDetails.add(sensorDetail);
            yaLiDeviceInfoV1List.add(yaLiDeviceInfoV1);
        }
        return yaLiDeviceInfoV1List;
    }

    private static int getCiF(int in, Integer point) {
        int ret = in;
        for(int i=1;i<point;i++){
            ret = ret*in;
        }
        return ret;
    }

    @Override
    public JSONObject toDeviceJSON() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceid", this.getDevice_id());
        jsonObject.put("name", this.getName());
        jsonObject.put("datatype", this.getMsgType());
        jsonObject.put("qiangdu", this.getQiangdu());
        jsonObject.put("dianya", this.getDianya());
        jsonObject.put("uploadtime",this.getUpload_time());
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

    public JSONObject toDeviceMessage() {
        return null;
    }

}
