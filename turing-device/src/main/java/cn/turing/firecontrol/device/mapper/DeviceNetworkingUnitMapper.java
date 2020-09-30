package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceNetworkingUnit;
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
 * @version 2018-07-26 09:17:58
 */
@Tenant
@Repository
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceNetworkingUnitMapper extends CommonMapper<DeviceNetworkingUnit> {

    //分页查询，根据单位名称搜索
    public List<DeviceNetworkingUnit> selectPageList(DeviceNetworkingUnit entity);

    //查看测点，代号名是否重复
    public Integer selectByCount(DeviceNetworkingUnit entity);

    DeviceNetworkingUnit getById(Integer id);

    public List<DeviceNetworkingUnit> getAllUnit(String oName);
}
