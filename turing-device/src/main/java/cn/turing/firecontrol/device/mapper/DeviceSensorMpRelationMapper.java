package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceMeasuringPoint;
import cn.turing.firecontrol.device.entity.DeviceSensorMpRelation;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceSensorMpRelationMapper extends CommonMapper<DeviceSensorMpRelation> {

    //根据传感器系列id获取关联的测点id
    public List<Integer> selectBySensorSeriesId(int sensorSeriesId);

    //根据测点id删除时，删除相应得关联表
    public void deleteByMPIds(Integer mpId);

    //根据传感器系列id删除时，删除相应得关联表
    public void deleteBySSIds(Integer sensorSeriesId);
}
