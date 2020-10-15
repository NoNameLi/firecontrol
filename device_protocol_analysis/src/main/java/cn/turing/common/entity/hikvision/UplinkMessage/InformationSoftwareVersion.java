package cn.turing.common.entity.hikvision.UplinkMessage;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.SensorDetail;

import cn.turing.common.util.ByteUtil;
import lombok.Data;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 上传建筑消防设施部件运行状态
 */
@Data
public class InformationSoftwareVersion  {
    private String systemTypeFlag;//系统类型
    private String systemAddress;//系统地址
    private Integer partsType;//部件类型
    private String partsAddress;//部件地址
    private String partsSim;//部件说明
    private SensorDetail sensorDetail;
    protected boolean has_alarm =false; //设备是否报警
    protected boolean has_guzhang =false;//设备是否故障
    protected Date upload_time; //设备信息数据的时间
    protected Date recieve_time;//我方程序接受时间
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    public InformationSoftwareVersion(String s) {
        systemTypeFlag=s.substring(0,2);
        systemAddress=s.substring(2,4);
        partsType=Integer.parseInt(s.substring(4,6));
        partsAddress=s.substring(6,14);
        sensorDetail=new SensorDetail();
        String partsStatus=s.substring(14,18);
       String status= ByteUtil.hexString2binaryString(partsStatus);
       if (status.substring(0,1).equals("1")){
           sensorDetail.setAlarmStatus(Constant.ST_NORM);
           sensorDetail.setAlarmType("正常");
           sensorDetail.setAlarmValue(Constant.ST_NORM);
       }else if (status.substring(1,2).equals("1")){
           sensorDetail.setAlarmStatus(Constant.ST_ALARM);
           sensorDetail.setAlarmType("火警");
           sensorDetail.setAlarmValue(Constant.ST_ALARM);
           has_alarm=true;
       }else if (status.substring(2,3).equals("1")){
           sensorDetail.setAlarmStatus(Constant.ST_WARN);
           sensorDetail.setAlarmType("故障");
           sensorDetail.setAlarmValue(Constant.ST_WARN);
           has_guzhang=true;
       }else if (status.substring(3,4).equals("1")){
           sensorDetail.setAlarmStatus(Constant.ST_NORM);
           sensorDetail.setAlarmType("屏蔽");
           sensorDetail.setAlarmValue(Constant.ST_NORM);
       }else if (status.substring(4,5).equals("1")){
           sensorDetail.setAlarmStatus(Constant.ST_NORM);
           sensorDetail.setAlarmType("监管");
           sensorDetail.setAlarmValue(Constant.ST_NORM);
       }else if (status.substring(5,6).equals("1")){
           sensorDetail.setAlarmStatus(Constant.ST_NORM);
           sensorDetail.setAlarmType("启动");
           sensorDetail.setAlarmValue(Constant.ST_NORM);
       }else if (status.substring(6,7).equals("1")){
           sensorDetail.setAlarmStatus(Constant.ST_NORM);
           sensorDetail.setAlarmType("反馈");
           sensorDetail.setAlarmValue(Constant.ST_NORM);
       }else if (status.substring(7,8).equals("1")){
           sensorDetail.setAlarmStatus(Constant.ST_NORM);
           sensorDetail.setAlarmType("延时状态");
           sensorDetail.setAlarmValue(Constant.ST_NORM);
       }else if (status.substring(8,9).equals("1")){
           sensorDetail.setAlarmStatus(Constant.ST_WARN);
           sensorDetail.setAlarmType("欠压");
           sensorDetail.setAlarmValue(Constant.ST_WARN);
       }
        String date=String.valueOf(Integer.parseInt(s.substring(24,26)))
                +String.valueOf(Integer.parseInt(s.substring(22,24)))
                +String.valueOf(Integer.parseInt(s.substring(20,22)))
                +String.valueOf(Integer.parseInt(s.substring(18,20)))
                +String.valueOf(Integer.parseInt(s.substring(16,18)))
                +String.valueOf(Integer.parseInt(s.substring(14,16)));
//        try {
            upload_time=new Date();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        recieve_time=new Date();
    }
}
