package cn.turing.firecontrol.server.controller;
import cn.turing.firecontrol.server.business.BusinessI;
import cn.turing.firecontrol.server.feign.IDeviceFeign;
import cn.turing.firecontrol.server.handler.device.DeviceEventHandlerComposite;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
@Component
@Slf4j
public class KafkaController {

    @Autowired
    BusinessI businessI;
    @Autowired
    IDeviceFeign iDeviceFeign;

    @Autowired
    DeviceEventHandlerComposite deviceEventHandlerComposite;

    @KafkaListener(topics ="${topics}")
    public void listenNew(ConsumerRecord<?,?> record){
        Optional<?> kafkaMessage=Optional.ofNullable(record.value());

        if (kafkaMessage.isPresent()){
            Object message=kafkaMessage.get();
            String data=message.toString();
            log.info("华为云------------------ message =" + data);
            if (data.contains("deviceType")){
                JSONObject jsonObject= JSONObject.parseObject(data);
                DeviceSensorMessage deviceSensorMessage=JSONObject.toJavaObject(jsonObject,DeviceSensorMessage.class);
                deviceEventHandlerComposite.prcess(deviceSensorMessage);
            }else {
                MQTTService mqttService = new MQTTService();
                mqttService.mqttAccept(message.toString().getBytes(), businessI, iDeviceFeign);

            }
        }
    }

}