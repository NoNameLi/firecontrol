package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceSensorManufacturer;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Repository
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceSensorManufacturerMapper extends CommonMapper<DeviceSensorManufacturer> {

    //分页查询  搜索传感器厂商
    public List<DeviceSensorManufacturer>  selectPageList(DeviceSensorManufacturer entity);

    //判断传感器厂商是否重复
    public Integer selectByCount(DeviceSensorManufacturer entity);

    //传感器厂商的下拉框，选择传感器厂商
    public List<String> selectedType();

    //传感器厂商的下拉框，选择传感器厂商
    public List<DeviceSensorManufacturer> selectedTypeId();

    //
    public DeviceSensorManufacturer selecteByName(String para);
}
