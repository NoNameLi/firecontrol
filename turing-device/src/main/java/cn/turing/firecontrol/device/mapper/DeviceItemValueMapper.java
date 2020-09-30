package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceItemValue;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Tenant
@Repository
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceItemValueMapper extends CommonMapper<DeviceItemValue> {

    public List<Map> selectByResultId(@Param(value = "resultsId")Integer resultsId,@Param(value = "inspectionFlag")String inspectionFlag,@Param(value = "itemFlag")String itemFlag);
}