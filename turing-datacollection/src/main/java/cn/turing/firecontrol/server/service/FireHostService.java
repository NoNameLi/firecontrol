package cn.turing.firecontrol.server.service;

import cn.turing.common.entity.lidahuaxin.FireHostDevice;
import cn.turing.common.entity.lidahuaxin.FireHostDeviceV1;
import cn.turing.firecontrol.server.entity.SensorDetail;

/**
 * @author TDS
 * @date 2019/11/27
 */
public interface FireHostService {
    /**
     * 处理利达消防主机
     * @param fireHostDevice
     */
    void readLiDaFireHost(FireHostDevice fireHostDevice);



    void readLiDaFireHost(FireHostDeviceV1 fireHostDevice);
    /**
     *
     * @param ip
     * @param port
     * @param loopNo
     * @param localtionNo
     * @param flag
     * @param timeout
     * @param uuid
     * @param sensorDetail
     * @return
     */
    boolean updateFireMainInRedis(String ip, String port, String loopNo, String localtionNo, boolean flag, int timeout, String uuid, SensorDetail sensorDetail);
}
