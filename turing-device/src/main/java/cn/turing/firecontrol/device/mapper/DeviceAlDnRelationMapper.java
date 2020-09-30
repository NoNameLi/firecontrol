package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceAlDnRelation;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
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
@Tenant
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceAlDnRelationMapper extends CommonMapper<DeviceAlDnRelation> {

    //根据报警等级id查询出通知方式的id
    public List<Integer> selectByAlarmLevelId(@Param("alarmLevelId") Integer alarmLevelId, @Param("tenantId") String tenantId);
}
