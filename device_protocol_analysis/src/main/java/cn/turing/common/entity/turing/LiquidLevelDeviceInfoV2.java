package cn.turing.common.entity.turing;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * NB液位
 */
@Data

public class LiquidLevelDeviceInfoV2 extends RabbitDeviceInfo {
    //设备发送状态信息。指令码01   服务器发送门限设置数据。指令码02  设备接收到02指令后发送03应答指令。
    //指令码
    private String InstructionCode;
    //报警字段 01报警 00正常
    private  String alarmStatus;
    //水位值
    private Double shuiWeiValue;
    //电池电压
    private Double dianYaValue;
    //信号值
    private  Integer signal_value;

    public LiquidLevelDeviceInfoV2(JSONObject jsonObject) throws Exception {
        super(jsonObject);

        InstructionCode=this.getData().substring(26,28);
        alarmStatus=this.getData().substring(28,30);
        shuiWeiValue=Integer.parseInt(this.getData().substring(30,34),16)*0.001;
        dianYaValue=Integer.parseInt(this.getData().substring(34,38),16)*0.001;
        signal_value=Integer.parseInt(this.getData().substring(38,40),16)-110;
        this.sensorDetails= Lists.newArrayList();

        sensorDetails.add(shuiweiData());
        sensorDetails.add(electricityData());
        sensorDetails.add(signal());
    }
    public SensorDetail signal(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.SIGN);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(signal_value);
        return  sensorDetail;
    }


    public SensorDetail shuiweiData(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.YW);
        if (alarmStatus.equals("00")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("报警");
            this.has_alarm=true;
        }
        sensorDetail.setAlarmValue(ByteUtil.setScaleDouble(shuiWeiValue));

        return  sensorDetail;
    }
    public  SensorDetail electricityData(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.DL);

        if (dianYaValue<2.5){
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmType("电量低");
            this.has_guzhang=true;
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
        }
        sensorDetail.setAlarmValue(ByteUtil.setScaleDouble(dianYaValue));
        return  sensorDetail;
    }
}
