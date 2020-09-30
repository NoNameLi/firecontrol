package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceFireMain;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Tenant
@Repository
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceFireMainMapper extends CommonMapper<DeviceFireMain> {

    public List<Map> selectPageList(DeviceFireMain deviceFireMain);

    //主机+ip是否重复 忽略租户隔离
    public Integer selectIgnoreTenantByCount(DeviceFireMain deviceFireMain);

    //查询所有的没有删除的数据
    public List<DeviceFireMain> getIgnoreTenantAll();

    DeviceFireMain getById(Integer id);
}