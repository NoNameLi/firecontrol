package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceCollectingDeviceSeries;
import cn.turing.firecontrol.common.mapper.CommonMapper;
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
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceCollectingDeviceSeriesMapper extends CommonMapper<DeviceCollectingDeviceSeries> {

    //分页搜索查询
    public List<DeviceCollectingDeviceSeries> selectPageList(DeviceCollectingDeviceSeries entity);

    //根据查询字段计数
    public Integer selectByCount(DeviceCollectingDeviceSeries entity);

    //采集设备的下拉框
    public List<String> selectedType();
}
