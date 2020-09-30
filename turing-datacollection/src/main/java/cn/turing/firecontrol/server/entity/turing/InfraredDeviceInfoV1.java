package cn.turing.firecontrol.server.entity.turing;

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
@Slf4j
@Data
public class InfraredDeviceInfoV1 extends RabbitDeviceInfo {
    public Integer length;//数据长度
    public String button;//手动按键
    public String person;//有人无人
    public Double dianliang;//电量
    public InfraredDeviceInfoV1(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        this.sensorDetails= Lists.newArrayList();
        length=Integer.parseInt(this.getData().substring(6,8),16);
        dianliang=Double.valueOf(Integer.parseInt(this.getData().substring(8,12),16)/1000);
        person=this.getData().substring(12,14);
        button=this.getData().substring(14,16);
        sensorDetails.add(vol());
        sensorDetails.add(police());
        sensorDetails.add(anjian());
    }

    public SensorDetail anjian(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.AJZT);
        if (button.equals("01")){
            has_alarm=true;
            sensorDetail.setAlarmType("手动按键报警");
            System.out.println();
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
        }else{
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return sensorDetail;
    }
    public SensorDetail vol(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.DL);

        if (dianliang<1.2){
            sensorDetail.setAlarmType("电量低");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(dianliang);
            has_guzhang=true;
        }else{
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(dianliang);
        }
        return sensorDetail;
  }

  public SensorDetail police(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.INFR);
        if (person.equals("01")){
            has_alarm=true;
            sensorDetail.setAlarmType("报警");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
        }else{
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return  sensorDetail;
  }
}
