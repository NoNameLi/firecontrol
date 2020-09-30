package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceCollectingDevice;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceSensorType;
import org.apache.ibatis.annotations.CacheNamespace;
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
@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceCollectingDeviceMapper extends CommonMapper<DeviceCollectingDevice> {

    List<Map> selectQuery(DeviceCollectingDevice entity);

    List<DeviceCollectingDevice> selectByCollectingDeviceTypeId(Integer id);

    //获取所有的厂商
    public List<String> getManufacturer();

    //获取所有的系列
    public List<String> getEquipmentType();

    //查看代号是否重复
    public Integer selectByCount(String code);

    //网关删除前查询1.5
    public List<String> deleteCollectingQuery(Integer id);


}
