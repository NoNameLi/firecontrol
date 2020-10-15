package cn.turing.common.entity.gongyuan;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * SOS 报警按钮
 * 测点：SOS VOL
 */

@Data
public class SOSDeviceInfoV1 extends RabbitDeviceInfo {
    String deviceType; //设备类型
    String version; //  软件版本V1.0,硬件版V1.0
    String messageType;//消息类型
    String length;
    double vole; //电池电压

    public SOSDeviceInfoV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        this.sensorDetails= Lists.newArrayList();
        //03110301106C
        try{
            this.deviceType=this.getData().substring(0,2);
            if (deviceType.equals("03")){
                this.version=this.getData().substring(2,4);
                this.length =this.getData().substring(4,6);
                this.messageType=this.getData().substring(6,8);
                if (messageType.equals("01")) { //01
                    sensorDetails.add(baojin(Constant.SOS));
                    sensorDetails.add(dianya(Constant.VOL));
                }else{
                }
            }else{
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private SensorDetail dianya(String vol) {
        vole=Integer.valueOf(this.getData().substring(8),16)*0.001;
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(vol);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("报警");
        sensorDetail.setAlarmValue(vole);
        has_alarm=true;
        return sensorDetail;
    }

    private SensorDetail baojin(String sos) {
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.SOS);
        sensorDetail.setAlarmStatus(Constant.ST_ALARM);
        sensorDetail.setAlarmType("报警");
        sensorDetail.setAlarmValue(Constant.ST_ALARM);
        return sensorDetail;
    }
}
