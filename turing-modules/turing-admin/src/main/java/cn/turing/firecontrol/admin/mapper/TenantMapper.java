package cn.turing.firecontrol.admin.mapper;

import cn.turing.firecontrol.admin.entity.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 租户表
 * 
 * @author Mr.AG
 * @email 463540703@qq.com
 * @version 2018-02-08 21:42:09
 */
@cn.turing.firecontrol.common.data.Tenant
@Repository
public interface TenantMapper extends CommonMapper<Tenant> {

    /**
     * ids用逗号隔开，批量查询租户信息
     * @param tenantIds
     * @return
     */
    List<Tenant> batchQuery(@Param("tenantIds") String tenantIds);

    /**
     * 分页查询站点，根据名称查询
     * @param name
     * @return
     */
    public List<Tenant> pageList(String name);

    /**
     * 查看重复记录
     * @param tenant
     * @return
     */
    public Integer selectByCount(Tenant tenant);

    /**
     * 逻辑删除站点
     * @param tenantId
     */
    public void deleteTenant(@Param("tenantId") String tenantId);

    public List<Tenant> selectByName(@Param("name") String name);

    /**
     * 根据租户ID查询租户和租户管理员的信息
     * @param tenantIds 以逗号隔开的租户ID
     * @return
     */
    List<Map<String,Object>> batchQueryInfo(@Param("tenantIds") String tenantIds);

    Tenant queryById(@Param("id") String id);


	
}
