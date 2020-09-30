package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceCollectingDevice;
import cn.turing.firecontrol.device.entity.DeviceCollectingDeviceType;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

import java.util.List;
import cn.turing.firecontrol.device.entity.DeviceSensorType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceCollectingDeviceTypeMapper extends CommonMapper<DeviceCollectingDeviceType> {

    //根据传采集系列设备的的id查询出有多少站点使用了改采集设备，返回子站点的id
    public List<String> selectByCollectingDeviceSeriesIdResultTenantId(Integer collectingDeviceSeriesId);

    //根据厂商绑定的网关，查询多少人使用了改厂商的网关，返回子站点的id-
    public List<String> selectByCollectingManufacturerIdResultTenantID(Integer manufacturerId);

    List<Map> selectPageList(DeviceCollectingDeviceType entity);

    List<DeviceCollectingDeviceType> selectByType(Map map);

    //获取所有的厂商
    public List<String> getManufacturer();

    //获取所有的系列
    public List<String> getEquipmentType();

    //获取根据厂商获取所有的类型
    public List<String> getEquipmentTypeByMF(String manufacturer);

    //根据厂商，类型获取所有的型号
    public List<String> getModel(Map map);

    public void updateCollecting(DeviceCollectingDeviceType entity);

}
