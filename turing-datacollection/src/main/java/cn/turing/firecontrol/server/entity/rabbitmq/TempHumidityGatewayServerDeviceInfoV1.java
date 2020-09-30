package cn.turing.firecontrol.server.entity.rabbitmq;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * LoRa温湿度与网关和服务器
 * 1.当温度变化1℃或者湿度变化3%时会上报数据。
 * 2.每隔1小时会上报一次温湿度数据，无论温湿度是否有变化
 * 3.每隔6小时上报一次心跳数据
 * 测点：XTEMP VOL HUM
 */
@Slf4j
@Data
public class TempHumidityGatewayServerDeviceInfoV1 extends RabbitDeviceInfo {
    String deviceType; //设备类型
    String version; //  软件版本V1.0,硬件版V1.0
    String length;  //数据长度
    String messageType;//消息类型
    double temp; //温度
    double humidity;//湿度
    double vole; //电池电压

    public  TempHumidityGatewayServerDeviceInfoV1(JSONObject jsonObject ) throws  Exception{
        super(jsonObject);
        this.sensorDetails= Lists.newArrayList();
        try{
            this.deviceType=this.getData().substring(0,2);
            log.info("来自温湿度终端的数据");
            if (deviceType.equals("04")){
              this.version=this.getData().substring(2,4);
              this.length =this.getData().substring(4,6);
              this.messageType=this.getData().substring(6,8);
              if (messageType.equals("01")){ //01 表示 温湿度
                  sensorDetails.add(wendu());
                  sensorDetails.add(shidu());
              }else if (messageType.equals("02")){ // 02表示 心跳
                sensorDetails.add(dianya());
              }


            }else{
                log.info("接收到无法识别的数据");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private SensorDetail dianya() {
        SensorDetail sensorDetail=new SensorDetail();
        this.vole=Integer.valueOf(this.getData().substring(8,10)+this.getData().substring(10),16)*0.001;
        sensorDetail.setAlarmCode(Constant.VOL);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(vole);
            return sensorDetail;
    }

    private SensorDetail shidu() {
        SensorDetail sensorDetail=new SensorDetail();
        this.humidity=Integer.valueOf(this.getData().substring(12,14)+this.getData().substring(14),16)*0.01;
        sensorDetail.setAlarmCode(Constant.HUM);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(humidity);

        return  sensorDetail;
    }

    private SensorDetail wendu() {
        SensorDetail sensorDetail=new SensorDetail();
        this.temp= Integer.valueOf(this.getData().substring(8,10)+this.getData().substring(10,12),16)*0.01;
        sensorDetail.setAlarmCode(Constant.XTEMP);
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        sensorDetail.setAlarmType("正常");
        sensorDetail.setAlarmValue(temp);
        return sensorDetail;
    }



}
