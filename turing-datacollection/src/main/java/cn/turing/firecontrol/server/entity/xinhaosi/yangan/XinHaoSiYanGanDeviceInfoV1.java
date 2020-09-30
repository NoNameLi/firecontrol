package cn.turing.firecontrol.server.entity.xinhaosi.yangan;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.DeviceInfo;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

/**
 * 接入鑫豪斯烟感
 * 说明：
 * ICCID：Integrate circuit card identity 集成电路卡识别码即SIM卡卡号，
 * 相当于手机号码的身份证。 ICCID为IC卡的唯一识别号码，共有20位数字组成，
 * 其编码格式为：XXXXXX 0MFSS YYGXX XXXX。分别介绍如下： 前六位运营商代码：
 * 中国移动的为：898600；898602；898604；898607 ，中国联通的为：898601、898606、898609，中国电信898603、898611。
 *
 *  感烟数据:  0x01 + 组长度(1byte) + 事件类型(1byte) + 红光值(1byte) + 蓝光值(1byte)
         Event事件类型定义
         50 火警取消
         55 火警
         205 光源污染
         206 光源污染恢复
         38 电池欠压
         39 电池欠压恢复
         23 传感器故障
         24 传感器恢复
         242 探测器正常,无任何故障及报警
 */
@Data
public class XinHaoSiYanGanDeviceInfoV1 extends DeviceInfo {
    String header;//帧头
    String mac;//MAC地址
    String workTime;//工作时间
    String iccId;//ICCID
    String firmwareNumber;//固件编号
    String version;//硬件版本
    String signal;//信号强度
    String volt;//电池电压
    String deviceType;//设备类型
    String heartbeatCycle;//心跳周期
    String messageNumber;//消息数
    String messageType;//消息类型》》》1.感烟消息  3温度  4.入侵数据
    public XinHaoSiYanGanDeviceInfoV1(String data){
        header=data.substring(0,2);
        mac=data.substring(2,18);
        workTime=data.substring(18,24);
        iccId=data.substring(24,44);
        firmwareNumber=data.substring(44,48);
        version=data.substring(48,50);
        signal=data.substring(50,52);
        volt=data.substring(52,56);
        deviceType=data.substring(56,60);
        heartbeatCycle=data.substring(60,64);
        messageNumber=data.substring(64,66);
        messageType=data.substring(66,68);//01表示感烟，03表示温度，04表示入侵数据
        String length=data.substring(68,70);//表示组长度
        String event=data.substring(70,72);//事件类型
        this.sensorDetails= Lists.newArrayList();
        sensorDetails.add(deviceEvent(event));
    }

    public SensorDetail deviceEvent(String event){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.YG);
        if ("50".equals(event)){
            sensorDetail.setAlarmType("火警取消");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else if ("55".equals(event)){
            sensorDetail.setAlarmType("火警");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            has_alarm=true;
        }else if ("205".equals(event)){
            sensorDetail.setAlarmType("光源污染");
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            has_alarm=true;
        }else if ("206".equals(event)){
            sensorDetail.setAlarmType("光源污染恢复");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else if ("38".equals(event)){
            sensorDetail.setAlarmType("电池欠压");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang=true;
        }else if ("39".equals(event)){
            sensorDetail.setAlarmType("电池欠压恢复");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else if ("23".equals(event)){
            sensorDetail.setAlarmType("传感器故障");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang=true;
        }else if ("24".equals(event)){
            sensorDetail.setAlarmType("传感器恢复");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else if ("242".equals(event)){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return sensorDetail;
    }
    @Override
    public JSONObject toDeviceJSON() {

        return null;
    }

    @Override
    public JSONObject toDeviceMessage() {
        return null;
    }
}
