package cn.turing.firecontrol.admin.mapper;

import cn.turing.firecontrol.admin.entity.ChannelTenant;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ChannelTenantMapper extends Mapper<ChannelTenant> {

    public void deleteByTenant(@Param("tenantId") String tenantId);

}