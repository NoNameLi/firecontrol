package cn.turing.common.entity.gongyuan;



import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * 井盖     电压 单位 mv
 */

@Data
public class WellCoverDeviceInfoV2 extends RabbitDeviceInfo {
    double vole;

    public WellCoverDeviceInfoV2(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        this.sensorDetails = Lists.newArrayList();
        //1D1103000F9C
         String length=this.getData().substring(4,6);
         sensorDetails.add(parsePolice(this.getData().substring(6,8)));
         sensorDetails.add(parseVole(this.getData().substring(8)));
    }

    private SensorDetail parseVole(String substring) {
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.VOL);
        Double value=Integer.parseInt(substring,16)*0.001;
        sensorDetail.setAlarmValue(value);
        if (value<1.000){
            has_guzhang=true;
            sensorDetail.setAlarmType("电量低");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
        }else {
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
        return  sensorDetail;
    }

    private SensorDetail parsePolice(String substring) {
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.WC);
        if (substring.equals("00")){
            has_alarm=true;
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("打开");
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
        }else if (substring.equals("01")){
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("关闭");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return sensorDetail;
    }
}