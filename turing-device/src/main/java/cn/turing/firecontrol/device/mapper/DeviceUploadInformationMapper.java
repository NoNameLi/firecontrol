package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import cn.turing.firecontrol.device.entity.DeviceUploadInformation;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;
@Tenant
@Repository
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceUploadInformationMapper extends CommonMapper<DeviceUploadInformation> {
    public DeviceUploadInformation selectByIdTemp(Integer id);
}
