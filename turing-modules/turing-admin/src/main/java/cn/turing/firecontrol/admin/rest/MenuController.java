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

import cn.turing.firecontrol.admin.biz.MenuBiz;
import cn.turing.firecontrol.admin.biz.UserBiz;
import cn.turing.firecontrol.admin.constant.AdminCommonConstant;
import cn.turing.firecontrol.admin.entity.Menu;
import cn.turing.firecontrol.admin.vo.MenuTree;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.BaseResponse;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @version 2017-06-12 8:49
 */
@Controller
@RequestMapping("menu")
@CheckUserToken
@CheckClientToken
@Api(tags="菜单模块")
public class MenuController extends BaseController<MenuBiz, Menu,String> {
    @Autowired
    private UserBiz userBiz;

    @ApiOperation("获取菜单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public List<Menu> list(String title) {
        Example example = new Example(Menu.class);
        if (StringUtils.isNotBlank(title)) {
            example.createCriteria().andLike("title", "%" + title + "%");
        }
        return baseBiz.selectByExample(example);
    }

    @ApiOperation("获取菜单树")
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    @ResponseBody
    public List<MenuTree> getTree(String title) {
        Example example = new Example(Menu.class);
        if (StringUtils.isNotBlank(title)) {
            example.createCriteria().andLike("title", "%" + title + "%");
        }
        return MenuTree.buildTree(baseBiz.selectByExample(example), AdminCommonConstant.ROOT);
    }

    @ApiOperation("获取角色权限分配清单")
    @GetMapping("group")
    @ResponseBody
    public ObjectRestResponse<List<MenuTree>> listByGroup(String groupId) throws Exception {
        if(ValidatorUtils.hasAnyBlank(groupId)){
            throw new ParamErrorException("参数不能为空");
        }
        Map<String,Object> map = baseBiz.selectByGroup(groupId);
        return new ObjectRestResponse<>().data(map);
    }

    @ApiOperation("编辑角色的权限")
    @GetMapping("group/edit")
    @ResponseBody
    public BaseResponse editByGroup(String groupId, String menuIds){
        if(ValidatorUtils.hasAnyBlank(groupId)){
            throw new ParamErrorException("参数不能为空");
        }
        baseBiz.editByGroup(groupId,menuIds);
        return new BaseResponse();
    }

}
