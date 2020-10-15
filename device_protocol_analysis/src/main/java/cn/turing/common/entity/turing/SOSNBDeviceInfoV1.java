package cn.turing.common.entity.turing;

import cn.turing.common.base.Constant;
import cn.turing.common.base.SensorExpirationTime;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

/**
 * NB sos 一键报警
 */
public class SOSNBDeviceInfoV1 extends RabbitDeviceInfo {
    /**
     * 报警值 1报警 0心跳
     */
    private String cmd;
    /**
     * 电池电压
     */
    private Double vol;
    /**
     * 信号值
     */
    private  Integer signal_value;
    /**
     * sos
     * IMEI号（15字节）+ICCID号（20字节）+数据长度（1字节）+设备类型（e0e0 2字节）+
     * 报警值（1字节 1报警 0心跳）+电池电压（2字节 单位mv）+信号强度（1字节）
     * @param jsonObject
     * @throws Exception
     */
    public SOSNBDeviceInfoV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);

        cmd=this.getData().substring(26,28);
        vol=Integer.parseInt(this.getData().substring(28,32),16)*0.001;
        signal_value=Integer.parseInt(this.getData().substring(32,34),16)-110;
        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(parseCmd());
        sensorDetails.add(parseVol());
        sensorDetails.add(parseSign());
    }

    public SensorDetail parseCmd(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.SOS);
        if (cmd.equals("01")){
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            this.has_alarm=true;
        }else{
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }

        return sensorDetail;

    }

    public SensorDetail parseVol(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOL);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(SensorExpirationTime.df.format(vol));

        return sensorDetail;

    }

    public SensorDetail parseSign(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.SIGN);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(signal_value);

        return sensorDetail;

    }

}
