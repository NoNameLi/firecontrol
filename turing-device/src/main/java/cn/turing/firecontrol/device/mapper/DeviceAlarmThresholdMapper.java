package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceAlarmThreshold;
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
 * @version 2018-07-26 09:18:17
 */
@Tenant
@Repository
//@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 102400)
public interface DeviceAlarmThresholdMapper extends CommonMapper<DeviceAlarmThreshold> {

    //查出所有得测点Id ++
    public List<Integer>  getMeasuringPointIds();
    //根据测点id查出所有的测点报警
    public List<Map> selectByMeasuringPointId(int measuringPointIds);

    //根据ids批量删除
    public void deleteByIds(String ids);

    //根据报警等级id删除
    public void deleteByAlId(Integer alId);
    //根据测点id查询
    List<DeviceAlarmThreshold> selectByMpid(Integer mpid);

    DeviceAlarmThreshold selectByAlrmData(Map map);

    //实时监测  获取等级
    public DeviceAlarmThreshold selectByAlrmLevel(Map map);
}
