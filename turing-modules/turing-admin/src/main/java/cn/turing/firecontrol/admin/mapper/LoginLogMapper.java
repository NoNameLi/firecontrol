package cn.turing.firecontrol.admin.mapper;

import cn.turing.firecontrol.admin.entity.LoginLog;
import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;

import java.util.List;

/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Tenant
public interface LoginLogMapper extends CommonMapper<LoginLog> {

    /**
     * 查询所有的登录日志
     * @return
     */
    public List<LoginLog> queryAll();

}
