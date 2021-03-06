/*
 *
 *  *  Copyright (C) 2018  Wanghaobin<463540703@qq.com>
 *
 *  *  AG-Enterprise 企业版源码
 *  *  郑重声明:
 *  *  如果你从其他途径获取到，请告知老A传播人，奖励1000。
 *  *  老A将追究授予人和传播人的法律责任!
 *
 *  *  This program is free software; you can redistribute it and/or modify
 *  *  it under the terms of the GNU General Public License as published by
 *  *  the Free Software Foundation; either version 2 of the License, or
 *  *  (at your option) any later version.
 *
 *  *  This program is distributed in the hope that it will be useful,
 *  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  GNU General Public License for more details.
 *
 *  *  You should have received a copy of the GNU General Public License along
 *  *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package cn.turing.firecontrol.admin.config;

import cn.turing.firecontrol.common.data.MybatisDataInterceptor;
import cn.turing.firecontrol.common.data.IUserDepartDataService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 租户\部门数据隔离
 * @author ace
 * @create 2018/2/11.
 */
@Configuration
public class MybatisDataConfig {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 该方法主要是为了让当前用户可以获取授权的数据权限部门
     */
    @Autowired
    private IUserDepartDataService userDepartDataService;

    @PostConstruct
    public void init(){
        /**
         * 有些mapper的某些方法不需要进行隔离，则可以在配置忽略，按逗号隔开.
         * 如:"UserMapper.selectOne",表示该mapper下不进行租户隔离
         */
        sqlSessionFactory.getConfiguration().addInterceptor(new MybatisDataInterceptor(userDepartDataService,"cn.turing.firecontrol.admin.mapper.UserMapper.selectOne","cn.turing.firecontrol.admin.mapper.UserMapper.selectByPrimaryKey","cn.turing.firecontrol.admin.mapper.UserMapper.selectUnbindTenantAdmin","cn.turing.firecontrol.admin.mapper.UserMapper.selelcTenantAdmin",
                "cn.turing.firecontrol.admin.mapper.GroupMapper.countLeaderByGroupId","cn.turing.firecontrol.admin.mapper.GroupMapper.countMemberByGroupId","cn.turing.firecontrol.admin.mapper.TenantMapper.queryById"
                ,"cn.turing.firecontrol.admin.mapper.GroupMapper.queryByUser","cn.turing.firecontrol.admin.mapper.UserMapper.queryUsers","cn.turing.firecontrol.admin.mapper.UserMapper.queryUsers_COUNT"));
    }
}
