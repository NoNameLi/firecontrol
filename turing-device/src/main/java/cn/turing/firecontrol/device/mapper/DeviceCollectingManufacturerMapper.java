package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceCollectingManufacturer;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Repository
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceCollectingManufacturerMapper extends CommonMapper<DeviceCollectingManufacturer> {
    //分页查询  搜索传网关厂商
    public List<DeviceCollectingManufacturer> selectPageList(DeviceCollectingManufacturer entity);

    //判断网关厂商是否重复
    public Integer selectByCount(DeviceCollectingManufacturer entity);

    //传感器厂商的下拉框，选择传感器厂商
    public List<String> selectedType();

    //根据厂商名称，查询厂商id
    public Integer selectByName(String manufacturerName);
}
