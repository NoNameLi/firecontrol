/*
 *  Copyright (C) 2018  Wanghaobin<463540703@qq.com>

 *  AG-Enterprise 企业版源码
 *  郑重声明:
 *  如果你从其他途径获取到，请告知老A传播人，奖励1000。
 *  老A将追究授予人和传播人的法律责任!

 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package cn.turing.firecontrol.admin.mapper;

import cn.turing.firecontrol.admin.entity.User;
import cn.turing.firecontrol.admin.vo.PageUser;
import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Tenant
public interface UserMapper extends CommonMapper<User> {
    public List<User> selectMemberByGroupId(@Param("groupId") String groupId);
    public List<User> selectLeaderByGroupId(@Param("groupId") String groupId);
    List<String> selectUserDataDepartIds(String userId);
    public List<User> selectUserByMobilePhone(String mobilePhone);
    public List<User> selectUser(User uesr);
    List<Map<String,String>> listUser(@Param("username") String username, @Param("groupId") String groupId, @Param("name") String name, @Param("mobilePhone") String mobilePhone,
                                      @Param("crtUserId") String crtUserId);
    void batchDelete(@Param("ids") String ids);
    List<User> batchQuery(@Param("ids") String ids);
    List<Map<String,String>> selectUnbindTenantAdmin();
    User selelcTenantAdmin(@Param("tenantId") String tenantId);
    Integer countTenantAdmin(@Param("ids") String ids);

    /**
     * 禁用指定站点的所有用户包括管理员
     * @param tenantId
     */
    void disableUserByTenant(@Param("tenantId")String tenantId);

    /**
     * 查询由指定用户创建的用户
     * @param createrId 创建人用户ID
     * @return
     */
     List<User> selectByCreater(@Param("createrId") String createrId);

    /**
     *查询用户信息
     * @param ids 用逗号隔开的用户ID
     * @param username 用户名
     * @param name 真实姓名
     * @param mobilePhone 手机号
     * @param isReserve 是否取反
     * @return
     */
     List<PageUser> queryUsers(@Param("ids")String ids, @Param("username")String username, @Param("name")String name,
                               @Param("mobilePhone")String mobilePhone, @Param("isReserve")Boolean isReserve,
                               @Param("tenantId")String tenantId);
}
