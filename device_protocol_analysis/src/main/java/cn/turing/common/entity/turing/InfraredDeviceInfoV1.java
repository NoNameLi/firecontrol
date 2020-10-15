package cn.turing.common.entity.turing;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 上传周期：2 小时。 红外保持时间：20 秒钟。 含周期上报数据、手动按键报警数据，检测到人的数据
 */

@Data
public class InfraredDeviceInfoV1 extends RabbitDeviceInfo {
    public Integer length;//数据长度
    public String button;//手动按键
    public InfraredDeviceInfoV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        this.sensorDetails= Lists.newArrayList();
        length=Integer.parseInt(this.getData().substring(6,8),16);
        button=this.getData().substring(14,16);
        sensorDetails.add(vol(this.getData().substring(8,12)));
        sensorDetails.add(police(this.getData().substring(12,14)));
    }

  public SensorDetail vol(String data){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.DL);
        Double V1=4095*1.224/Integer.parseInt(data,16);
        Double value=(V1-2.8)/0.8*100;
        if (value<10.0){
            sensorDetail.setAlarmType("电量低");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(value);
            has_guzhang=true;
        }else{
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(value);
        }
        return sensorDetail;
  }

  public SensorDetail police(String data){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.INFR);
        if (data.equals("01")){
            has_alarm=true;
            if (this.button.equals("01")){
                sensorDetail.setAlarmType("手动按键报警");
            }else{
                sensorDetail.setAlarmType("报警");
            }
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
        }else{
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return  sensorDetail;
  }
}
