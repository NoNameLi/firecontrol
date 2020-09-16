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

package cn.turing.firecontrol.admin.biz;

import cn.turing.firecontrol.admin.entity.ResourceAuthority;
import cn.turing.firecontrol.admin.entity.User;
import cn.turing.firecontrol.admin.rpc.service.PermissionService;
import cn.turing.firecontrol.admin.vo.MenuTree;
import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import com.ace.cache.annotation.Cache;
import com.ace.cache.annotation.CacheClear;
import cn.turing.firecontrol.admin.constant.AdminCommonConstant;
import cn.turing.firecontrol.admin.entity.Menu;
import cn.turing.firecontrol.admin.mapper.MenuMapper;
import cn.turing.firecontrol.admin.mapper.UserMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.util.BooleanUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @version 2017-06-12 8:48
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MenuBiz extends BusinessBiz<MenuMapper, Menu> {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private ResourceAuthorityBiz resourceAuthorityBiz;

    @Override
    @Cache(key = "permission:menu")
    public List<Menu> selectListAll() {
        return super.selectListAll();
    }

    @Override
    @CacheClear(keys = {"permission:menu", "permission"})
    public void insertSelective(Menu entity) {
        if (AdminCommonConstant.ROOT.equals(entity.getParentId())) {
            entity.setPath("/" + entity.getCode());
        } else {
            Menu parent = this.selectById(entity.getParentId());
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        }
        super.insertSelective(entity);
    }

    @Override
    @CacheClear(keys = {"permission:menu", "permission"})
    public void deleteById(Object id) {
        super.deleteById(id);
    }

    @Override
    @CacheClear(keys = {"permission:menu", "permission"})
    public void updateById(Menu entity) {
        if (AdminCommonConstant.ROOT == entity.getParentId()) {
            entity.setPath("/" + entity.getCode());
        } else {
            Menu parent = this.selectById(entity.getParentId());
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        }
        super.updateById(entity);
    }

    @Override
    @CacheClear(keys = {"permission:menu", "permission"})
    public void updateSelectiveById(Menu entity) {
        super.updateSelectiveById(entity);
    }

    /**
     * 获取用户可以访问的菜单
     *
     * @param userId
     * @return
     */
    @Cache(key = "permission:menu:u{1}")
    public List<Menu> getUserAuthorityMenuByUserId(String userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        //如果租户为超级管理员则查询全部
        if (BooleanUtil.BOOLEAN_TRUE.equals(user.getIsSuperAdmin())) {
//            return this.selectListAll();
//            return mapper.selectAll();
            return mapper.selectAllOfSuperAdmin();
        }
        //如果租户为站点管理员，则查询其站点的所有菜单
        if(BooleanUtil.BOOLEAN_TRUE.equals(user.getAttr1())){
            return mapper.selectByTenantId(user.getTenantId());
        }
        return mapper.selectAuthorityMenuByUserId(userId, AdminCommonConstant.RESOURCE_TYPE_VIEW);
    }

    /**
     * 根据用户获取可以访问的系统
     *
     * @param id
     * @return
     */
    public List<Menu> getUserAuthoritySystemByUserId(String id) {
        return mapper.selectAuthoritySystemByUserId(id, AdminCommonConstant.RESOURCE_TYPE_VIEW);
    }

    /**
     *查询该用户所在的租户的所有菜单，如果角色不为空，指出当前角色有哪些菜单
     * @param groupId
     * @return
     * @throws Exception
     */
    public Map<String,Object> selectByGroup(String groupId) throws Exception {
        List<Menu> menus = mapper.selectByTenantId(BaseContextHandler.getTenantID());
        List<MenuTree> list = permissionService.getMenuTree(menus, AdminCommonConstant.ROOT);
        List<String> ids = mapper.selectByGroupId(groupId);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("menus",list);
        map.put("ids",ids);
        return map;
    }

    /**
     * 修改当前角色的权限
     * @param groupId
     * @param menuIds
     */
    @Transactional
    @CacheClear(keys = {"permission:menu", "permission"})
    public void editByGroup(String groupId, String menuIds){
        //删除该角色的所有菜单权限
        mapper.deleteByGroupId(groupId);
        //添加菜单权限
        if(StringUtils.isBlank(menuIds)){
            return;
        }
        String[] ids = menuIds.split(",");
        ResourceAuthority ra = null;
        for(String id : ids){
            Menu menu = selectById(id);
            if(menu == null){
                continue;
            }
            ra = new ResourceAuthority();
            ra.setId(UUIDUtils.generateShortUuid());
            ra.setAuthorityId(groupId);
            ra.setAuthorityType("group");
            ra.setResourceId(id);
            ra.setResourceType("menu");
            ra.setParentId("-1");
            resourceAuthorityBiz.insertSelective(ra);
        }

    }
}
