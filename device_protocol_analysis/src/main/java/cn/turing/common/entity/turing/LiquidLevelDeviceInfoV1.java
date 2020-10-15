package cn.turing.common.entity.turing;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * TR-19WD01L产品通信协议V1.0
 */
@Data

public class LiquidLevelDeviceInfoV1 extends RabbitDeviceInfo {
    private  Integer length;
    public LiquidLevelDeviceInfoV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);
      //9090000C01CD057b00005A5A
        this.sensorDetails= Lists.newArrayList();
        length=Integer.parseInt(this.getData().substring(6,8),16);
        sensorDetails.add(LiquidData(this.getData().substring(8,12),this.getData().substring(12,16)));
        sensorDetails.add(electricityData(this.getData().substring(12,16)));
    }
    public SensorDetail LiquidData(String str, String str1){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.YW);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        Integer M1=Integer.parseInt(str1,16);
        // 新方法，如果不需要四舍五入，可以使用RoundingMode.DOWN
        BigDecimal bg = new BigDecimal(liquid(str,M1)).setScale(2, BigDecimal.ROUND_HALF_UP);
        sensorDetail.setAlarmValue(bg.doubleValue());

        return  sensorDetail;
    }
    public  SensorDetail electricityData(String data){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.DL);
        BigDecimal bg=new BigDecimal(electricity(data)).setScale(5, RoundingMode.UP);
        if (bg.doubleValue()<2.5){
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmType("电量低"); 
        }else{
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmType("正常");
        }
        sensorDetail.setAlarmValue(bg.doubleValue());
        return  sensorDetail;
    }

    /**
     * 计算电压值
     * @param data
     * @return
     */
    public Double electricity(String data){
        Integer M1=Integer.parseInt(data,16);
        Double V1 =4095*1.224/M1;
        return V1;
    }

    public Double liquid(String data,Integer M1){
        Integer M2=Integer.parseInt(data,16);
        Double V2=M2*1.224/M1;
       // 液位值与量程有关系，常规5米量程计算  液位=(量程)*(电压值-0.4)/1.6=5*(0.402-0.4)/1.6=0.00625米
        return  5*(V2-0.4)/1.6;//单位米
    }
}
