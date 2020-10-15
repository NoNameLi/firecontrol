package cn.turing.common.entity.terminus;

import cn.turing.common.entity.DeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

/**
 * 特斯联智慧用电
 */
@Data
public class SmartElectricityDeviceV1 extends DeviceInfo {
    private String startSign;//起始符
    private String version;//协议版本号
    private String packageType;//消息包类型
    private String packageNum;//消息包索引
    private Integer systemType;//系统类型
    private Integer systemCode;//系统编号
    private String sourceUsage;//来源使用情况
    private Integer loopNo;//回路
    private Integer deviceCode;//设备地址
    private Integer messageUnit;//数据单元数
    private Integer messageLength;//数据长度
    private Integer deviceChannel;//设备通道
    private String messageType;//数据类型
    private String produceTime;//产生时间
    private Integer messageDataLength;//有效数据长度
    private String effectiveData;//有效数据
    private String CRC;//校验和
    private String endSign;//结束符

    private boolean flag=false;//校验是否成功

    public SmartElectricityDeviceV1(String data){
        startSign = data.substring(0,2);
        version = data.substring(2,4);
        packageType = data.substring(4,6);
        packageNum =data.substring(6,14);
        systemType = Integer.parseInt(data.substring(16,20),16);
        systemCode = Integer.parseInt(data.substring(20,32),16);
        sourceUsage = data.substring(32,34);
        loopNo= Integer.parseInt(data.substring(34,36),16);
        deviceCode = Integer.parseInt(data.substring(36,42),16);
        messageUnit= Integer.parseInt(data.substring(42,46),16);
        messageLength=Integer.parseInt(data.substring(46,48),16);
        deviceChannel=Integer.parseInt(data.substring(48,50),16);
        messageType= data.substring(50,54);
        produceTime=data.substring(54,66);
        messageDataLength=Integer.parseInt(data.substring(66,68),16);
        effectiveData=data.substring(48,data.length()-4);
        CRC= data.substring(data.length()-4,data.length()-2);
        endSign=data.substring(data.length()-2);


        String cre=data.substring(2,data.length()-4);
        String resultCrc= ByteUtil.hexSum(cre);
        if (resultCrc.equals(CRC)){
            flag=true;
        }
        device_id=String.valueOf(systemType)+String.valueOf(systemCode)+
                String.valueOf(loopNo)+String.valueOf(deviceCode);

        Date date=new Date();
        upload_time=date;
        recieve_time=date;
    }

    public JSONObject toDeviceJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceid", this.getDevice_id().toLowerCase());
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
