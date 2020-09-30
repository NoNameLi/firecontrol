package cn.turing.firecontrol.server.handler.device;

import cn.turing.firecontrol.server.vo.DeviceSensorMessage;


/**
 * 设备处理接口
 */
public interface DeviceEventHandler {

    /**该handler是否支持该事件的处理，不同的预警事件交由不同的handler进行处理
     *
     * @param deviceSensorMessage
     * @return
     */
    boolean support(DeviceSensorMessage deviceSensorMessage);

    /**是否要进行后续handler操作
     *
     * @param deviceSensorMessage
     * @return
     */
    boolean preHandle(DeviceSensorMessage deviceSensorMessage);

    /**主要处理逻辑
     *
     * @param deviceSensorMessage
     */
    void handle(DeviceSensorMessage deviceSensorMessage, String uuid) throws Exception;

    /**
     * 将数据集合起来
     * @param deviceSensorMessage
     */
    void postHandle(DeviceSensorMessage deviceSensorMessage);


}
