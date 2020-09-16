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

import cn.turing.firecontrol.admin.entity.Element;
import cn.turing.firecontrol.admin.entity.Group;
import cn.turing.firecontrol.admin.entity.Menu;
import cn.turing.firecontrol.admin.mapper.ElementMapper;
import cn.turing.firecontrol.admin.mapper.GroupMapper;
import cn.turing.firecontrol.admin.mapper.MenuMapper;
import cn.turing.firecontrol.admin.mapper.UserMapper;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import com.ace.cache.annotation.CacheClear;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.merge.annonation.MergeResult;
import cn.turing.firecontrol.admin.constant.AdminCommonConstant;
import cn.turing.firecontrol.admin.entity.ResourceAuthority;
import cn.turing.firecontrol.admin.mapper.*;
import cn.turing.firecontrol.admin.vo.AuthorityMenuTree;
import cn.turing.firecontrol.admin.vo.GroupUsers;
import cn.turing.firecontrol.admin.vo.MenuTree;
import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.util.BooleanUtil;
import cn.turing.firecontrol.common.util.UUIDUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @version 2017-06-12 8:48
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GroupBiz extends BusinessBiz<GroupMapper, Group> {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ResourceAuthorityBiz resourceAuthorityBiz;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private ElementMapper elementMapper;

    @Override
    public void insertSelective(Group entity) {
        /*if (AdminCommonConstant.ROOT.equals(entity.getParentId())) {
            entity.setPath("/" + entity.getCode());
        } else {
            Group parent = this.selectById(entity.getParentId());
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        }*/
        //判重
        String name = entity.getName();
        if(ValidatorUtils.hasAnyBlank(name)){
            throw new RuntimeException("角色名不能为空");
        }
        List<Group> groups = mapper.pageList(entity.getName());
        if(!groups.isEmpty()){
            throw new RuntimeException("角色已存在");
        }
        entity.setParentId("-1");
        entity.setPath("/" + entity.getCode());
        entity.setGroupType("role");
        super.insertSelective(entity);
    }

    @Override
    public void updateSelectiveById(Group entity) {
        String name = entity.getName();
        if(ValidatorUtils.hasAnyBlank(name)){
            throw new RuntimeException("角色名不能为空");
        }
        List<Group> groups = mapper.pageList(name);
        if(groups.size()>0 && !entity.getId().equals(groups.get(0).getId())){
            throw new RuntimeException("角色已存在");
        }
        super.updateSelectiveById(entity);
    }

    @Override
    @Transactional
    public void deleteById(Object id) {
        //删除角色
        super.deleteById(id);
        //解除用户角色关联
        mapper.deleteGroupLeadersById(id.toString());
        mapper.deleteGroupMembersById(id.toString());
    }

    /**
     * 获取群组关联用户
     *
     * @param groupId
     * @return
     */
    public GroupUsers getGroupUsers(String groupId) {
        return new GroupUsers(userMapper.selectMemberByGroupId(groupId), userMapper.selectLeaderByGroupId(groupId));
    }

    /**
     * 变更群主所分配用户
     *
     * @param groupId
     * @param members
     * @param leaders
     */
    @CacheClear(pre = "permission")
    public void modifyGroupUsers(String groupId, String members, String leaders) {
        mapper.deleteGroupLeadersById(groupId);
        mapper.deleteGroupMembersById(groupId);
        if (!StringUtils.isEmpty(members)) {
            String[] mem = members.split(",");
            for (String m : mem) {
                mapper.insertGroupMembersById(UUIDUtils.generateUuid(), groupId, m, BaseContextHandler.getTenantID());
            }
        }
        if (!StringUtils.isEmpty(leaders)) {
            String[] mem = leaders.split(",");
            for (String m : mem) {
                mapper.insertGroupLeadersById(UUIDUtils.generateUuid(), groupId, m, BaseContextHandler.getTenantID());
            }
        }
    }

    /**
     * 变更群组关联的菜单
     *
     * @param groupId
     * @param menus
     */
    @CacheClear(keys = {"permission:menu", "permission:u"})
    public void modifyAuthorityMenu(String groupId, String[] menus, String type) {
        resourceAuthorityBiz.deleteByAuthorityIdAndResourceType(groupId + "", AdminCommonConstant.RESOURCE_TYPE_MENU, type);
        List<Menu> menuList = menuMapper.selectAll();
        Map<String, String> map = new HashMap<String, String>();
        for (Menu menu : menuList) {
            map.put(menu.getId().toString(), menu.getParentId().toString());
        }
        Set<String> relationMenus = new HashSet<String>();
        relationMenus.addAll(Arrays.asList(menus));
        ResourceAuthority authority = null;
        for (String menuId : menus) {
            findParentID(map, relationMenus, menuId);
        }
        for (String menuId : relationMenus) {
            authority = new ResourceAuthority(AdminCommonConstant.AUTHORITY_TYPE_GROUP, AdminCommonConstant.RESOURCE_TYPE_MENU);
            authority.setAuthorityId(groupId + "");
            authority.setResourceId(menuId);
            authority.setParentId("-1");
            authority.setType(type);
            resourceAuthorityBiz.insertSelective(authority);
        }
    }

    private void findParentID(Map<String, String> map, Set<String> relationMenus, String id) {
        String parentId = map.get(id);
        if (String.valueOf(AdminCommonConstant.ROOT).equals(id) || parentId == null) {
            return;
        }
        relationMenus.add(parentId);
        findParentID(map, relationMenus, parentId);
    }

    /**
     * SimpleRouteLocator
     * 分配资源权限
     *
     * @param groupId
     * @param menuId
     * @param elementId
     */
    @CacheClear(keys = {"permission:ele", "permission:u"})
    public void modifyAuthorityElement(String groupId, String menuId, String elementId, String type) {
        ResourceAuthority authority = new ResourceAuthority(AdminCommonConstant.AUTHORITY_TYPE_GROUP, AdminCommonConstant.RESOURCE_TYPE_BTN);
        authority.setAuthorityId(groupId + "");
        authority.setResourceId(elementId + "");
        authority.setParentId("-1");
        authority.setType(type);
        resourceAuthorityBiz.insertSelective(authority);
    }

    /**
     * 移除资源权限
     *
     * @param groupId
     * @param elementId
     */
    @CacheClear(keys = {"permission:ele", "permission:u"})
    public void removeAuthorityElement(String groupId, String elementId, String type) {
        ResourceAuthority authority = new ResourceAuthority();
        authority.setAuthorityId(groupId + "");
        authority.setResourceId(elementId + "");
        authority.setParentId("-1");
        authority.setType(type);
        resourceAuthorityBiz.delete(authority);
    }


    /**
     * 获取群主关联的菜单
     *
     * @param groupId
     * @return
     */
    public List<AuthorityMenuTree> getAuthorityMenu(String groupId, String type) {
        List<Menu> menus = menuMapper.selectMenuByAuthorityId(String.valueOf(groupId), AdminCommonConstant.AUTHORITY_TYPE_GROUP, type);
        List<AuthorityMenuTree> trees = new ArrayList<AuthorityMenuTree>();
        AuthorityMenuTree node = null;
        for (Menu menu : menus) {
            node = new AuthorityMenuTree();
            node.setText(menu.getTitle());
            BeanUtils.copyProperties(menu, node);
            trees.add(node);
        }
        return trees;
    }

    /**
     * 获取群组关联的资源
     *
     * @param groupId
     * @return
     */
    public List<String> getAuthorityElement(String groupId, String type) {
        ResourceAuthority authority = new ResourceAuthority(AdminCommonConstant.AUTHORITY_TYPE_GROUP, AdminCommonConstant.RESOURCE_TYPE_BTN);
        authority.setAuthorityId(groupId);
        authority.setType(type);
        List<ResourceAuthority> authorities = resourceAuthorityBiz.selectList(authority);
        List<String> ids = new ArrayList<String>();
        for (ResourceAuthority auth : authorities) {
            ids.add(auth.getResourceId());
        }
        return ids;
    }

    /**
     * 获取当前管理员可以分配的菜单
     * @return
     */
    public List<MenuTree> getAuthorizeMenus() {
        if (BooleanUtil.BOOLEAN_TRUE.equals(userMapper.selectByPrimaryKey(BaseContextHandler.getUserID()).getIsSuperAdmin())) {
            return MenuTree.buildTree(menuMapper.selectAll(), AdminCommonConstant.ROOT);
        }
        return MenuTree.buildTree(menuMapper.selectAuthorityMenuByUserId(BaseContextHandler.getUserID(), AdminCommonConstant.RESOURCE_TYPE_AUTHORISE), AdminCommonConstant.ROOT);
    }

    /**
     * 获取当前管理员可以分配的资源
     * @param menuId
     * @return
     */
    @MergeResult
    public List<Element> getAuthorizeElements(String menuId) {
        if (BooleanUtil.BOOLEAN_TRUE.equals(userMapper.selectByPrimaryKey(BaseContextHandler.getUserID()).getIsSuperAdmin())) {
            Example example = new Example(Element.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("menuId", menuId);
            return elementMapper.selectByExample(example);
        }
        return elementMapper.selectAuthorityMenuElementByUserId(BaseContextHandler.getUserID(),menuId,AdminCommonConstant.RESOURCE_TYPE_AUTHORISE);
    }


    public Integer countUser(String groupId){
        Integer leaders = mapper.countLeaderByGroupId(groupId);
        Integer members = mapper.countMemberByGroupId(groupId);
        return leaders + members;
    }

    /**
     * 分页查询
     * @param name
     * @param page
     * @param limit
     * @return
     */
    public TableResultResponse<Group> pageList(String name, Integer page, Integer limit) {
        Page<Group> groupPage = PageHelper.startPage(page,limit);
        List<Group> groups = mapper.pageList(name);
        return new TableResultResponse<>(groupPage.getTotal(),groups);
    }

    public List<Map<String,Object>> listByUser(String userId){
        return mapper.listByUser(userId);
    }

    public List<AuthorityMenuTree> listMenuByGroup(String groupId) throws InvocationTargetException, IllegalAccessException {
        List<Map<String,Object>> menus = menuMapper.selectAllByGroupId(groupId);
        List<AuthorityMenuTree> trees = new ArrayList<AuthorityMenuTree>();
        AuthorityMenuTree node = null;
        for (Map<String,Object> menu : menus) {
            node = new AuthorityMenuTree();
            node.setText(menu.get("title").toString());
            org.apache.commons.beanutils.BeanUtils.populate(node,menu);
            trees.add(node);
        }
        return trees;
    }

    /**
     * 查询用户角色
     * @param userId
     * @return
     */
    public Group queryByUser(String userId){
        List<Group> groups = mapper.queryByUser(userId);
        if(groups != null && !groups.isEmpty()){
            return groups.get(0);
        }else {
            return null;
        }
    }
}
