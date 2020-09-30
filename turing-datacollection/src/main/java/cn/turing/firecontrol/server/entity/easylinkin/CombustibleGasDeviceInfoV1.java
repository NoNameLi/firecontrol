package cn.turing.firecontrol.server.entity.easylinkin;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.firecontrol.server.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 可燃气体
 */
@Data
@Slf4j
public class CombustibleGasDeviceInfoV1 extends RabbitDeviceInfo {
    //以下为设备相关信息
    private String data;  //接收到的数据
    private String head; //第一个byte，头
    private String alarmState;//第二个byte，报警位
    private String sensorState;//第三个byte，传感器信息
    public CombustibleGasDeviceInfoV1(JSONObject jsonObject,String code) throws Exception {
        super(jsonObject);
        log.info("解析16进制数据开始");
        if (this.getData().length() == 6){
            this.sensorDetails = Lists.newArrayList();
            try {
                List<String> result = new ArrayList<String>();
                String header = data.substring(0, 2); //标识
                String alarmState = data.substring(2, 4); //报警
                String sensorState = ByteUtil.hexString2binaryString(data.substring(4, 6)); //状态

                SensorDetail sensorDetail = new SensorDetail();
                sensorDetail.setAlarmCode(code);
                //  sensorDetail.setAlarmValue(0);
                //目前只有烟感报警04，可燃气体02 、、03表示门磁
                if(alarmState.equalsIgnoreCase("04")){

                    sensorDetail.setAlarmType("报警");
                    sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                    sensorDetail.setAlarmValue(Constant.ST_ALARM);
                    has_alarm =true;
                } else
                if(alarmState.equalsIgnoreCase("03") ){

                    sensorDetail.setAlarmType("报警");
                    sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                    sensorDetail.setAlarmValue(Constant.ST_ALARM);
                    has_alarm =true;
                }else
                if(alarmState.equalsIgnoreCase("02")){

                    sensorDetail.setAlarmType("报警");
                    sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                    sensorDetail.setAlarmValue(Constant.ST_ALARM);
                    has_alarm =true;
                }
                //其余状态位的判断
                else if (alarmState.equalsIgnoreCase("00")){
                    //0-2按键状态，可以忽略，即是是测试也需要报警
                    //6,7 故障 4,5 电量低，3 拆卸了
                    if (data.substring(4, 6).equals("00")){
                        sensorDetail.setAlarmType("正常");
                        sensorDetail.setAlarmStatus(Constant.ST_NORM);
                        sensorDetail.setAlarmValue(Constant.ST_NORM);
                    }else{
                        if(sensorState.substring(0,2).equalsIgnoreCase("01")){
                            sensorDetail.setAlarmType("故障");
                            sensorDetail.setAlarmStatus(Constant.ST_WARN);
                            sensorDetail.setAlarmValue(Constant.ST_WARN);
                            has_guzhang =true;
                        }else if(sensorState.substring(4,5).equalsIgnoreCase("1")){
                            sensorDetail.setAlarmType("被拆");
                            sensorDetail.setAlarmStatus(Constant.ST_WARN);
                            sensorDetail.setAlarmValue(Constant.ST_WARN);
                            has_guzhang =true;
                        }else if (sensorState.substring(2,4).equalsIgnoreCase("01")){
                            sensorDetail.setAlarmType("电量低");
                            sensorDetail.setAlarmStatus(Constant.ST_WARN);
                            sensorDetail.setAlarmValue(Constant.ST_WARN);
                            has_guzhang =true;
                        }else if (sensorState.substring(5).equalsIgnoreCase("001")){
                            sensorDetail.setAlarmType("测试");
                            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                            sensorDetail.setAlarmValue(Constant.ST_ALARM);
                            has_alarm =true;
                        } else if (sensorState.substring(5).equalsIgnoreCase("010")){
                            sensorDetail.setAlarmType("消音");
                            sensorDetail.setAlarmStatus(Constant.ST_NORM);
                            sensorDetail.setAlarmValue(Constant.ST_NORM);
                        }
                    }

                }
                sensorDetails.add(sensorDetail);
                log.info("解析16进制数据完毕");
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("解析16进制数据完成");
        }else{
            log.info("非可识别的数据");
        }
    }
}
