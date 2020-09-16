package cn.turing.firecontrol.admin.mapper;

import cn.turing.firecontrol.admin.entity.Channel;
import cn.turing.firecontrol.admin.vo.ChannelDto;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
public interface ChannelMapper extends CommonMapper<Channel> {

    List<Map<String,Object>> queryAll(@Param("tenantId") String tenantId);

    List<Map<String,Object>> list(@Param("tenantId") String tenantId);
    List<ChannelDto> getByIds(@Param("ids")String ids);
	
}
