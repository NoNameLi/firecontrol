package cn.turing.firecontrol.server.handler.device;

import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DeviceEventHandlerComposite implements InitializingBean {

    @Autowired
    List<DeviceEventHandler> deviceEventHandlerList;

    public void prcess(DeviceSensorMessage deviceSensorMessage){
        for (DeviceEventHandler deviceEventHandler: deviceEventHandlerList){
            if (deviceEventHandler.support(deviceSensorMessage)){
                //选择版本
//                if (deviceEventHandler.preHandle(deviceSensorMessage)){
                    try {
                        String uuid = UUIDUtils.generateUuid();
                        deviceEventHandler.handle(deviceSensorMessage,uuid);
                        //将数据集合
                        deviceEventHandler.postHandle(deviceSensorMessage);
                    }catch (Exception e){
                        e.printStackTrace();
                        log.error("设备信息错误：{}"+deviceSensorMessage.toString());
                    }

//                }
            }


        }



    }




    @Override
    public void afterPropertiesSet() throws Exception {
        AnnotationAwareOrderComparator.sort(deviceEventHandlerList);
    }
}
