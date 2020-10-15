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
public class SOSDeviceInfoV2 extends RabbitDeviceInfo {
    String deviceType; //设备类型
    String version; //  软件版本V1.0,硬件版V1.0
    String messageType;//消息类型
    String length;
    double vole; //电池电压

    public SOSDeviceInfoV2(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        this.sensorDetails= Lists.newArrayList();
        //03110301106C
        //03110101
        try{
            this.deviceType=this.getData().substring(0,2);
            if (deviceType.equals("03")){
                this.version=this.getData().substring(2,4);
                this.length =this.getData().substring(4,6);
                this.messageType=this.getData().substring(6);
                if (messageType.equals("01")) { //01 报警
                    sensorDetails.add(baojin2(Constant.SOS));
                }else{

                }
            }else{

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private SensorDetail baojin2(String sos) {
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(sos);
        sensorDetail.setAlarmStatus(Constant.ST_ALARM);
        sensorDetail.setAlarmType("报警");
        sensorDetail.setAlarmValue(Constant.ST_ALARM);
        this.has_alarm=true;
        return sensorDetail;
    }
}
