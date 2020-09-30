package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceFacilitiesType;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceFacilitiesTypeMapper extends CommonMapper<DeviceFacilitiesType> {

    List<DeviceFacilitiesType> selectByType(String equipmentType);

    public List<Map> selectPageList(@Param(value = "equipmentType") String equipmentType);

    public Integer selectByCount(String equipmentType);

    public Set<Integer> getAllTypeId();

    List<String> getByType();

    DeviceFacilitiesType getById(Integer id);
}