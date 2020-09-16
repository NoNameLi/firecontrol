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

import cn.turing.firecontrol.admin.entity.Group;
import cn.turing.firecontrol.common.data.Tenant;
import cn.turing.firecontrol.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Tenant
public interface GroupMapper extends CommonMapper<Group> {

    public void deleteGroupMembersById (@Param("groupId") String groupId);
    public void deleteGroupLeadersById (@Param("groupId") String groupId);
    public void deleteGroupMembersByUserId (@Param("userId") String userId);
    public void deleteGroupLeadersByUserId (@Param("userId") String userId);
    public void insertGroupMembersById (@Param("id") String id,@Param("groupId") String groupId,@Param("userId") String userId,@Param("tenantId") String tenantId);
    public void insertGroupLeadersById (@Param("id") String id,@Param("groupId") String groupId,@Param("userId") String userId,@Param("tenantId") String tenantId);
    public Integer countLeaderByGroupId(String groupId);
    public Integer countMemberByGroupId(String groupId);
    public List<Group> pageList(@Param("name") String name);
    public List<Map<String,Object>> listByUser(@Param("userId")String userId);
    public List<Group> selectByName(@Param("name") String name);
    public List<Group> queryByUser(@Param("userId")String userId);//查询用户角色
}
