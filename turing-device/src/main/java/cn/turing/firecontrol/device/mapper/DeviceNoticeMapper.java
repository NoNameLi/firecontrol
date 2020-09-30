package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceNotice;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
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
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceNoticeMapper extends CommonMapper<DeviceNotice> {
    //查看报警等级绑定的通知方式
    public List<DeviceNotice> selectByAlarmLevelIdResult(String ids);

    //查看报警，或者故障的通知方式
    public List<DeviceNotice> selectByNoticeTypeResult(String ids);

    //查询所有的通知方式
    public List<Map> getAll();

    //批量获取通知方式
    public List<String> getByIds( String ids);

}
