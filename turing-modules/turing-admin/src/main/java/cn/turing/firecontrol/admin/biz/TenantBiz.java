package cn.turing.firecontrol.admin.biz;

import cn.turing.firecontrol.admin.entity.Tenant;
import cn.turing.firecontrol.admin.entity.User;
import cn.turing.firecontrol.admin.feign.DeviceFeign;
import cn.turing.firecontrol.admin.mapper.TenantMapper;
import cn.turing.firecontrol.admin.mapper.UserMapper;
import cn.turing.firecontrol.common.biz.BaseBiz;
import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.exception.base.BusinessException;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.BooleanUtil;
import cn.turing.firecontrol.common.util.Sha256PasswordEncoder;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 租户表
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @version 2018-02-08 21:42:09
 */
@Service
public class TenantBiz extends BusinessBiz<TenantMapper,Tenant> {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private DeviceFeign deviceFeign;

    @Override
    public void insertSelective(Tenant entity) {
        entity.getName();
        List<Tenant> tenants = mapper.selectByName(entity.getName());
        if(!tenants.isEmpty()){
            throw new RuntimeException("站点名已存在");
        }
        super.insertSelective(entity);
    }

    public void updateUser(String id, String userId) {
        Tenant tenant = this.mapper.selectByPrimaryKey(id);
        tenant.setOwner(userId);
        updateSelectiveById(tenant);
        User user = userMapper.selectByPrimaryKey(userId);
        user.setTenantId(id);
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 批量查询站点信息
     * @param tenantIds
     * @param page
     * @param limit
     * @return
     */
    public TableResultResponse<Tenant> batchQuery(String tenantIds, Integer page, Integer limit){
        String[] ids = tenantIds.split(",");
        StringBuilder sb = new StringBuilder();
        for(String id : ids){
            sb.append("'").append(id).append("'").append(",");
        }
        if(sb.toString().isEmpty()){
            throw new RuntimeException("参数不能为空");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        Page<Tenant> tenantPage =  PageHelper.startPage(page,limit);
        List<Tenant> tenants = tenantMapper.batchQuery(sb.toString());
        return new TableResultResponse<Tenant>(tenantPage.getTotal(),tenants);
    }

    //分页查询站点，根据名称查询
    public TableResultResponse<Tenant> pageList(Integer page, Integer limit,String name){
        Page<Tenant> tenantPage =  PageHelper.startPage(page,limit);
        List<Tenant> tenants = tenantMapper.pageList(name);
        return new TableResultResponse<Tenant>(tenantPage.getTotal(),tenants);
    }

    //查询站点，或者域名的记录数
    public Integer selectByCount(String name , String attr1){
        Tenant tenant=new Tenant();
        tenant.setAttr1(attr1);
        tenant.setName(name);
        return tenantMapper.selectByCount(tenant);
    }

    /**
     * 删除站点
     * @param tenantId 站点ID
     * @param superAdminPassword 超级管理员密码
     */
    @Transactional
    public void deleteTanant(String tenantId, String superAdminPassword){
        //1、判断当前用户是否是超级管理员，及所输入的超管密码是否正确，非超管无此权限
        User user = userMapper.selectByPrimaryKey(BaseContextHandler.getUserID());
        if(! BooleanUtil.BOOLEAN_TRUE.equals(user.getIsSuperAdmin())){
            throw new BusinessException("无权删除站点");
        }
        if(! user.getPassword().equals(new Sha256PasswordEncoder().encode(superAdminPassword))){
            throw new BusinessException("密码错误，删除失败");
        }
        //2、删除当前站点、禁用当前站点所有用户
        tenantMapper.deleteTenant(tenantId);
        userMapper.disableUserByTenant(tenantId);
        deviceFeign.deleteTenant(tenantId);

    }

    /**
     * 批量查询租户及其管理员的信息
     * @param tenantIds
     * @param page
     * @param limit
     * @return
     */
    public TableResultResponse<Map<String,Object>> batchQueryInfo(String tenantIds, Integer page, Integer limit){
        String[] ids = tenantIds.split(",");
        StringBuilder sb = new StringBuilder();
        for(String id : ids){
            sb.append("'").append(id).append("'").append(",");
        }
        if(sb.toString().isEmpty()){
            throw new RuntimeException("参数不能为空");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        Page<Map<String,Object>> tenantPage =  PageHelper.startPage(page,limit);
        List<Map<String,Object>> tenants = tenantMapper.batchQueryInfo(sb.toString());
        return new TableResultResponse<Map<String,Object>>(tenantPage.getTotal(),tenants);
    }

}