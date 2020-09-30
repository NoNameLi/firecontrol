package cn.turing.firecontrol.server.handler.device;


import cn.turing.common.entity.RabbitDeviceInfo;
import cn.turing.firecontrol.server.base.Constant;
import cn.turing.firecontrol.server.business.BusinessI;
import cn.turing.firecontrol.server.vo.DeviceSensorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(value = 100)
public abstract class AbstractDeviceEventHandler implements DeviceEventHandler, InitializingBean {

    @Autowired
    BusinessI businessI;


    @Override
    public boolean support(DeviceSensorMessage deviceSensorMessage){
        return  false;
    }

    @Override
    public abstract void handle(DeviceSensorMessage warningMessage,String uuid) throws Exception;


    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void postHandle(DeviceSensorMessage deviceSensorMessage){
        //将数据集合起来
    }

    /**
     * 抽出公共处理方法
     */
    public void parsingHandle(RabbitDeviceInfo deviceInfo, int normal_timeout, String uuid, String type,String channel){
        businessI.updateInRedis(deviceInfo, normal_timeout);
        //报警
        if (deviceInfo.isHas_alarm() || deviceInfo.isHas_guzhang()) {
            log.error("报警发送报警:");
            businessI.sendDeviceAlarmMQ(uuid,deviceInfo,type);
        } else {
            log.error("更新为正常状态: {调用device服务/deviceSensor/updateStatus}" + deviceInfo.getDevice_id());
            businessI.updateStatus(deviceInfo.getDevice_id(), deviceInfo, "2");
            //自动回复处理机制
            businessI.abnormalRestore(deviceInfo.getDevice_id());
        }
        //将原始数据插入ES
        businessI.insertData(Constant.ESConstant.ES_INDEX, Constant.ESConstant.ES_STRING, uuid, deviceInfo.toDeviceLogJSON(channel));
        //将解析后的数据插入ES
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, deviceInfo.toDeviceJSON());
    }
}
