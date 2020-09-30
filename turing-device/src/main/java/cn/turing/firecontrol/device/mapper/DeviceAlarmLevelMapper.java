package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceAlarmLevel;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:57
 */
@Repository
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceAlarmLevelMapper extends CommonMapper<DeviceAlarmLevel> {
    //分页查询等级，按排序字段排序
    public List<DeviceAlarmLevel> selectPageList();

    //根据查询字段计数
    public Integer selectByCount(DeviceAlarmLevel entity);

    //查询所有的等级，只显示id，跟等级名称
    public List<Map> getAll();


}
