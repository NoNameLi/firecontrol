package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceCheckTestItem;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceCheckTestItemMapper extends CommonMapper<DeviceCheckTestItem> {

    public List<DeviceCheckTestItem> selectByQuery(@Param(value = "facilitiesTypeId") Integer facilitiesTypeId,@Param(value = "itemFlag")String itemFlag,@Param(value = "flag")String flag);

    List<DeviceCheckTestItem> selectByFacilitiesNo(@Param(value = "facilitiesTypeId") Integer facilitiesTypeId,@Param(value = "itemFlag")String itemFlag,@Param(value = "flag")String flag);
}