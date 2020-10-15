package cn.turing.common.entity.turing;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;


@Data
public class LoraXiaoFangShuanDeviceInfoV2 extends RabbitDeviceInfo {
    //以下为设备相关信息
    //报警状态
    private String cmd;
    //漏水状态
    private String louShui;
    //水压状态
    private String shuiYa;
    //防拆
    private String fangChai;
    //撞击
    private String zhuangJi;
    //倾斜
    private String qingXie;
    //电压值
    private Double vol_value;
    //水压值
    private Double shuiYa_value;
    //温度
    private Double temp_value;

    public LoraXiaoFangShuanDeviceInfoV2(JSONObject jsonObject) throws Exception {
        super(jsonObject);
        this.sensorDetails = Lists.newArrayList();

        String kaiguan = ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(this.getData().substring(6, 8), 16)));
        cmd = kaiguan.substring(0, 1);
        louShui = kaiguan.substring(1, 2);
        shuiYa = kaiguan.substring(2, 3);
        fangChai = kaiguan.substring(3, 4);
        zhuangJi = kaiguan.substring(4, 5);
        qingXie = kaiguan.substring(5);
        vol_value = Integer.parseInt(this.getData().substring(8, 12), 16) * 0.001;
        shuiYa_value = Integer.parseInt(this.getData().substring(12, 16), 16) * 0.001;
        temp_value = Integer.parseInt(this.getData().substring(16, 20), 16) * 1.0;

        this.sensorDetails = Lists.newArrayList();
        sensorDetails.add(shuiYa());
        sensorDetails.add(loushui());
        sensorDetails.add(fangChai());
        sensorDetails.add(zhuangJi());
        sensorDetails.add(qingxie());
        sensorDetails.add(vol1());
        sensorDetails.add(tempX());


    }

    public SensorDetail shuiYa(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XWPS);
        if (shuiYa.equals("0")){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(shuiYa_value);
        }else{
            sensorDetail.setAlarmType("欠压");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(shuiYa_value);
            has_guzhang =true;
        }
        return sensorDetail;
    }

    public SensorDetail loushui(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XWLS);
        if (louShui.equals("0")){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else{
            sensorDetail.setAlarmType("漏水");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang =true;
        }
        return sensorDetail;
    }

    public SensorDetail fangChai(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XFC);
        if (fangChai.equals("0")){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else{
            sensorDetail.setAlarmType("被拆");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang =true;
        }
        return sensorDetail;
    }

    public SensorDetail zhuangJi(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XZJ);
        if (zhuangJi.equals("0")){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else{
            sensorDetail.setAlarmType("被撞");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang =true;
        }
        return sensorDetail;
    }
    public SensorDetail qingxie(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XQX);
        if (qingXie.contains("1")){
            sensorDetail.setAlarmType("倾斜");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang =true;
        }else{
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        return sensorDetail;
    }

    public SensorDetail vol1(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XELE);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmValue(vol_value);

        return sensorDetail;
    }
    public SensorDetail tempX(){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XTEMP);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);

        sensorDetail.setAlarmValue(ByteUtil.setScaleDouble(temp_value));

        return sensorDetail;
    }

}
