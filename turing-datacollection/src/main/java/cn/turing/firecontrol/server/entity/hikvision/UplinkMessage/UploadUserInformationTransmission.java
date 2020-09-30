package cn.turing.firecontrol.server.entity.hikvision.UplinkMessage;

import cn.turing.firecontrol.server.base.Constant;
import cn.turing.firecontrol.server.entity.SensorDetail;
import cn.turing.firecontrol.server.util.ByteUtil;
import lombok.Data;

import java.util.Date;

/**
 * 上传用户信息传输装置操作信息
 */
@Data
public class UploadUserInformationTransmission {
    private SensorDetail sensorDetail;
    protected boolean has_alarm =false; //设备是否报警
    protected boolean has_guzhang =false;//设备是否故障
    protected Date upload_time; //设备信息数据的时间
    protected Date recieve_time;//我方程序接受时间
    private String partsAddress;//部件地址
    private String systemAddress;//系统地址
    public UploadUserInformationTransmission(String s) {
        String data=s.substring(0,2);
        String status= ByteUtil.hexString2binaryString(data);
        //00000011
        sensorDetail=new SensorDetail();
        if (status.substring(6,7).equals("1")){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("手动报警测试");
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            has_alarm=true;
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        upload_time=new Date();
        recieve_time=new Date();
        partsAddress="01";
        systemAddress="01";
    }
}
