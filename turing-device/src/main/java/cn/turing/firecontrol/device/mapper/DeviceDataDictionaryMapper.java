package cn.turing.firecontrol.device.mapper;

import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.device.entity.DeviceDataDictionary;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:57
 */
@Tenant
@CacheNamespace(implementation=cn.turing.firecontrol.device.config.MybatisRedisCache.class,size = 1024)
public interface DeviceDataDictionaryMapper extends CommonMapper<DeviceDataDictionary> {
	
}
