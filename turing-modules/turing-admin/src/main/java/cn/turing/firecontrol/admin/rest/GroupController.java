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

package cn.turing.firecontrol.admin.rest;

import cn.turing.firecontrol.admin.biz.GroupBiz;
import cn.turing.firecontrol.admin.biz.ResourceAuthorityBiz;
import cn.turing.firecontrol.admin.constant.AdminCommonConstant;
import cn.turing.firecontrol.admin.entity.Element;
import cn.turing.firecontrol.admin.entity.Group;
import cn.turing.firecontrol.admin.vo.AuthorityMenuTree;
import cn.turing.firecontrol.admin.vo.GroupTree;
import cn.turing.firecontrol.admin.vo.GroupUsers;
import cn.turing.firecontrol.admin.vo.MenuTree;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.TreeUtil;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @version 2017-06-12 8:49
 */
@RestController
@RequestMapping("group")
@Api("角色模块")
@CheckUserToken
@CheckClientToken
public class GroupController extends BaseController<GroupBiz, Group,String> {
    @Autowired
    private ResourceAuthorityBiz resourceAuthorityBiz;
    @ApiOperation("获取角色列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public List<Group> list(String name, String groupType) {
        if (StringUtils.isBlank(name) && StringUtils.isBlank(groupType)) {
            return new ArrayList<Group>();
        }
        Example example = new Example(Group.class);
        if (StringUtils.isNotBlank(name)) {
            example.createCriteria().andLike("name", "%" + name + "%");
        }
        if (StringUtils.isNotBlank(groupType)) {
            example.createCriteria().andEqualTo("groupType", groupType);
        }

        return baseBiz.selectByExample(example);
    }

    @ApiOperation("用户关联角色")
    @RequestMapping(value = "/{id}/user", method = RequestMethod.PUT)
    @ResponseBody
    public ObjectRestResponse modifiyUsers(@PathVariable String id, String members, String leaders) {
        baseBiz.modifyGroupUsers(id, members, leaders);
        return new ObjectRestResponse();
    }

    @ApiOperation("获取角色关联用户")
    @RequestMapping(value = "/{id}/user", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<GroupUsers> getUsers(@PathVariable String id) {
        return new ObjectRestResponse<GroupUsers>().data(baseBiz.getGroupUsers(id));
    }

    @ApiOperation("分配角色可访问菜单")
    @RequestMapping(value = "/{id}/authority/menu", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse modifyMenuAuthority(@PathVariable String id, String menuTrees) {
        String[] menus = menuTrees.split(",");
        baseBiz.modifyAuthorityMenu(id, menus, AdminCommonConstant.RESOURCE_TYPE_VIEW);
        return new ObjectRestResponse();
    }

    @ApiOperation("获取角色可访问菜单")
    @RequestMapping(value = "/{id}/authority/menu", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<List<AuthorityMenuTree>> getMenuAuthority(@PathVariable String id) {
        return new ObjectRestResponse().data(baseBiz.getAuthorityMenu(id, AdminCommonConstant.RESOURCE_TYPE_VIEW));
    }

    @ApiOperation("角色分配菜单可访问资源")
    @RequestMapping(value = "/{id}/authority/element/add", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse addElementAuthority(@PathVariable String id, String menuId, String elementId) {
        baseBiz.modifyAuthorityElement(id, menuId, elementId, AdminCommonConstant.RESOURCE_TYPE_VIEW);
        return new ObjectRestResponse();
    }

    @ApiOperation("角色移除菜单可访问资源")
    @RequestMapping(value = "/{id}/authority/element/remove", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse removeElementAuthority(@PathVariable String id, String menuId, String elementId) {
        baseBiz.removeAuthorityElement(id, elementId, AdminCommonConstant.RESOURCE_TYPE_VIEW);
        return new ObjectRestResponse();
    }

    @ApiOperation("获取角色可访问资源")
    @RequestMapping(value = "/{id}/authority/element", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<List<String>> getElementAuthority(@PathVariable String id) {
        return new ObjectRestResponse().data(baseBiz.getAuthorityElement(id, AdminCommonConstant.RESOURCE_TYPE_VIEW));
    }

    @ApiOperation("分配角色可授权菜单")
    @RequestMapping(value = "/{id}/authorize/menu", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse modifyMenuAuthorize(@PathVariable String id, String menuTrees) {
        String[] menus = menuTrees.split(",");
        baseBiz.modifyAuthorityMenu(id, menus, AdminCommonConstant.RESOURCE_TYPE_AUTHORISE);
        return new ObjectRestResponse();
    }

    @ApiOperation("获取角色可授权菜单")
    @RequestMapping(value = "/{id}/authorize/menu", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<List<AuthorityMenuTree>> getMenuAuthorize(@PathVariable String id) {
        return new ObjectRestResponse().data(baseBiz.getAuthorityMenu(id, AdminCommonConstant.RESOURCE_TYPE_AUTHORISE));
    }

    @ApiOperation("角色分配菜单可授权资源")
    @RequestMapping(value = "/{id}/authorize/element/add", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse addElementAuthorize(@PathVariable String id, String menuId, String elementId) {
        baseBiz.modifyAuthorityElement(id, menuId, elementId, AdminCommonConstant.RESOURCE_TYPE_AUTHORISE);
        return new ObjectRestResponse();
    }

    @ApiOperation("角色移除菜单可授权资源")
    @RequestMapping(value = "/{id}/authorize/element/remove", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse removeElementAuthorize(@PathVariable String id, String menuId, String elementId) {
        baseBiz.removeAuthorityElement(id, elementId, AdminCommonConstant.RESOURCE_TYPE_AUTHORISE);
        return new ObjectRestResponse();
    }

    @ApiOperation("获取角色可授权资源")
    @RequestMapping(value = "/{id}/authorize/element", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<List<String>> getElementAuthorize(@PathVariable String id) {
        return new ObjectRestResponse().data(baseBiz.getAuthorityElement(id, AdminCommonConstant.RESOURCE_TYPE_AUTHORISE));
    }

    @ApiOperation("获取角色树")
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    @ResponseBody
    public List<GroupTree> tree(String name, String groupType) {
        Example example = new Example(Group.class);
        if (StringUtils.isNotBlank(name)) {
            example.createCriteria().andLike("name", "%" + name + "%");
        }
        if (StringUtils.isNotBlank(groupType)) {
            example.createCriteria().andEqualTo("groupType", groupType);
        }
        return getTree(baseBiz.selectByExample(example), AdminCommonConstant.ROOT);
    }

    /**
     * 获取可管理的资源
     * @param menuId
     * @return
     */
    @ApiOperation("获取用户可管理资源")
    @RequestMapping(value = "/element/authorize/list", method = RequestMethod.GET)
    public TableResultResponse<Element> getAuthorizeElement(String menuId) {
        List<Element> elements = baseBiz.getAuthorizeElements(menuId);
        return new TableResultResponse<Element>(elements.size(), elements);
    }

    /**
     * 获取可管理的菜单
     * @return
     */
    @ApiOperation("获取用户可管理菜单")
    @RequestMapping(value = "/menu/authorize/list", method = RequestMethod.GET)
    public List<MenuTree> getAuthorizeMenus() {
        return TreeUtil.bulid(baseBiz.getAuthorizeMenus(), AdminCommonConstant.ROOT, null);
    }

    private List<GroupTree> getTree(List<Group> groups, String root) {
        List<GroupTree> trees = new ArrayList<GroupTree>();
        GroupTree node = null;
        for (Group group : groups) {
            node = new GroupTree();
            node.setLabel(group.getName());
            BeanUtils.copyProperties(group, node);
            trees.add(node);
        }
        return TreeUtil.bulid(trees, root, null);
    }

    @ApiOperation("查询角色关联的用户数量")
    @GetMapping("user/count")
    public ObjectRestResponse<Integer> countUser(String groupId){
        if(ValidatorUtils.hasAnyBlank(groupId)){
            throw new ParamErrorException("参数不能为空");
        }
        Integer count = baseBiz.countUser(groupId);
        return new ObjectRestResponse<Integer>().data(count);
    }

    @ApiOperation("分页查询租户列表")
    @GetMapping("pageList")
    public TableResultResponse<Group> pageList(String name,Integer page, Integer limit){
        if(page == null){
            page = 1;
        }
        if(limit == null){
            limit = 15;
        }
        if(StringUtils.isBlank(name)){
            name = null;
        }
        return baseBiz.pageList(name,page,limit);
    }

    /**
     * 查询角色清单，如果用户属于该角色，则isUser为1
     * @param userId
     * @return
     */
    @ApiOperation("查询角色清单")
    @GetMapping("listByUser")
    public ObjectRestResponse<List<Map<String,Object>>> listByUser(String userId){
        List<Map<String,Object>> groups = baseBiz.listByUser(userId);
        return new ObjectRestResponse<List<Map<String,Object>>>().data(groups);
    }

    /**
     * 查询权限清单，如果某角色有该菜单权限，则ishave为1
     * @param groupId
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @ApiOperation("角色权限查询")
    @GetMapping("menus")
    public ObjectRestResponse<List<AuthorityMenuTree>> listMenus(String groupId) throws InvocationTargetException, IllegalAccessException {
        List<AuthorityMenuTree> menus = baseBiz.listMenuByGroup(groupId);
        return new ObjectRestResponse<List<AuthorityMenuTree>>().data(menus);
    }
}
