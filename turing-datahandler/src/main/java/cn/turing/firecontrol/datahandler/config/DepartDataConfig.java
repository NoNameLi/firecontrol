/*
 *
 *  *  Copyright (C) 2018  Wanghaobin<463540703@qq.com>
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

package cn.turing.firecontrol.datahandler.config;

import cn.turing.firecontrol.common.data.IUserDepartDataService;
import cn.turing.firecontrol.common.data.MybatisDataInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author hanyong
 * @create 2018/2/11.
 */
@Configuration
public class DepartDataConfig {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private IUserDepartDataService userDepartDataService;

    @PostConstruct
    public void init(){
        sqlSessionFactory.getConfiguration().addInterceptor(new MybatisDataInterceptor(userDepartDataService,
                "cn.turing.firecontrol.datahandler.mapper.NoticeRuleSensorMapper.queryBySensorIdAndChannelId",
                "cn.turing.firecontrol.datahandler.mapper.NoticeRuleMapper.queryById",
                "cn.turing.firecontrol.datahandler.mapper.NoticeRuleUserMapper.queryByNoticeRuleId",
                "cn.turing.firecontrol.datahandler.mapper.NoticeLogMapper.queryLastLog",
                "cn.turing.firecontrol.datahandler.mapper.DeviceAbnormalMapper.getLatestList",
                "cn.turing.firecontrol.datahandler.mapper.DeviceFireMainAbnormalMapper.handleAbnormal"
                ));
//        sqlSessionFactory.getConfiguration().addInterceptor(new TenantMybatisInterceptor());
    }
}
