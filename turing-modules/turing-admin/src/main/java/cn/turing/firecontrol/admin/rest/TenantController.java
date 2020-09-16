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

package cn.turing.firecontrol.admin.rest;

import cn.turing.firecontrol.admin.biz.ChannelBiz;
import cn.turing.firecontrol.admin.biz.MenuBiz;
import cn.turing.firecontrol.admin.biz.UserBiz;
import cn.turing.firecontrol.admin.entity.*;
import cn.turing.firecontrol.admin.biz.TenantBiz;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.BaseResponse;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.BooleanUtil;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * 租户管理
 * @author ace
 */
@RestController
@RequestMapping("tenant")
@CheckClientToken
@CheckUserToken
@Api(tags = "租户模块")
public class TenantController extends BaseController<TenantBiz,Tenant,String> {
    @Autowired
    private UserBiz userBiz;
    @Autowired
    private TenantBiz tenantBiz;
    @Autowired
    private MenuBiz menuBiz;
    @Autowired
    private ChannelBiz channelBiz;


    @ApiOperation("租户授予用户")
    @RequestMapping(value = "/{id}/user",method = RequestMethod.PUT)
    public ObjectRestResponse<Boolean> updateUser(@PathVariable("id") String id, String userId){
        baseBiz.updateUser(id,userId);
        return new ObjectRestResponse<>();
    }

    @ApiOperation("获取租户授予用户")
    @RequestMapping(value = "/{id}/user",method = RequestMethod.GET)
    public ObjectRestResponse<User> updateUser(@PathVariable("id") String id){
        Tenant tenant = baseBiz.selectById(id);
        return new ObjectRestResponse<>().data(userBiz.selectById(tenant.getOwner()));
    }


    //72e25c8c8cb46553b2d8854b09055c2e
    @ApiOperation("批量获取租户信息")
    @GetMapping(value = "list")
    public TableResultResponse<Map<String,Object>> batchQuery(String tenantIds, Integer page, Integer limit){
        if(ValidatorUtils.hasAnyBlank(tenantIds)){
            throw new RuntimeException("参数不能为空");
        }
        if(page == null){
            page = 1;
        }
        if(limit == null){
            limit = 15;
        }
        return tenantBiz.batchQueryInfo(tenantIds,page,limit);
    }

    @ApiOperation("分页显示站点,按站点名称搜索数据")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Tenant> list(Integer page,Integer limit, String name){
        //查询列表数据
        if(page == null){
            page = 1;
        }
        if(limit == null){
            limit = 15;
        }
        return baseBiz.pageList(page,limit,name);
    }


    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加站点")
    @Transactional
    public ObjectRestResponse<Tenant> add(String name, String attr1,String attr2,String userId, String channelIds,String site,String tenantNo){
        ObjectRestResponse<Tenant> responseResult = new ObjectRestResponse<Tenant>();
        Tenant entity = new Tenant();
        entity.setName(name);
        entity.setAttr1(attr1);
        entity.setAttr2(attr2);
        entity.setSite(site);
        entity.setTenantNo(tenantNo);
        if (ValidatorUtils.hasAnyBlank(entity, userId, channelIds)) {
            throw new ParamErrorException("参数不能为空");
        }
        ValidatorUtils.validateEntity(entity);
        //判断
        if (tenantBiz.selectByCount(name, null) > 0) {
            throw new RuntimeException("站点名已存在!");
        }
        if (StringUtils.isNotBlank(attr1) && tenantBiz.selectByCount(null, attr1) > 0) {
            throw new RuntimeException("域名已存在!");
        }
        //基本信息
        entity.setId(UUIDUtils.generateShortUuid());
        Date time = new Date();
        entity.setCrtTime(time);
        entity.setUpdTime(time);
        entity.setCrtUserId(BaseContextHandler.getUserID());
        entity.setUpdUserId(BaseContextHandler.getUserID());
        entity.setCrtUserName(BaseContextHandler.getUsername());
        entity.setUpdUserName(BaseContextHandler.getUsername());
        entity.setIdDeleted(BooleanUtil.BOOLEAN_FALSE);
        baseBiz.insertSelective(entity);
        //添加分站管理员，将user表的attr1字段 [0=否，1=是]
        User user = userBiz.selectById(userId);
        user.setAttr1("1");
        user.setTenantId(entity.getId());
        userBiz.updateSelectiveById(user);
        ChannelTenant channelTenant = null;
        String[] ids = channelIds.split(",");
        for (String id : ids) {
            if (StringUtils.isBlank(id)) {
                continue;
            }
            channelTenant = new ChannelTenant();
            channelTenant.setChannelId(id);
            channelTenant.setTenantId(entity.getId());
            channelBiz.addChannelTenant(channelTenant);
        }
        return responseResult;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改站点")
    @Transactional
    public ObjectRestResponse<Tenant> update(String tenantId,String name, String attr1,String attr2, String userId,String channelIds,String site,String tenantNo){
        ObjectRestResponse<Tenant> responseResult =  new ObjectRestResponse<Tenant>();
        Tenant entity = new Tenant();
        entity.setId(tenantId);
        entity.setName(name);
        entity.setAttr1(attr1);
        entity.setAttr2(attr2);
        entity.setSite(site);
        entity.setTenantNo(tenantNo);
        if (ValidatorUtils.hasAnyBlank(entity, userId, channelIds)) {
            throw new ParamErrorException("参数不能为空");
        }
        ValidatorUtils.validateEntity(entity);
        //判断站点名称是否修改，修改需要判重
        Tenant tenant = baseBiz.selectById(tenantId);
        if (!name.equals(tenant.getName()) && tenantBiz.selectByCount(name, null) > 0) {
            throw new RuntimeException("站点名已存在!");
        }
        //判断域名是否修改，修改需要判重
        if (StringUtils.isNotBlank(attr1) && !attr1.equals(tenant.getAttr1()) && tenantBiz.selectByCount(null, attr1) > 0) {
            throw new RuntimeException("域名已存在!");
        }
        baseBiz.updateSelectiveById(entity);
        //修改管理员信息
        User old = userBiz.queryTenantAdmin(tenantId);
        if(!userId.equals(old.getId())){
            old.setTenantId(null);
            old.setAttr1(BooleanUtil.BOOLEAN_FALSE);
            userBiz.updateById(old);
            User user = userBiz.selectById(userId);
            if(user == null){
                throw new RuntimeException("用户不存在");
            }
            if(BooleanUtil.BOOLEAN_TRUE.equals(user.getAttr1())){
                throw new RuntimeException("所选用户是其他站点管理员");
            }
            user.setTenantId(entity.getId());
            user.setAttr1(BooleanUtil.BOOLEAN_TRUE);
            userBiz.updateSelectiveById(user);
        }
        //删除用户已经订阅的栏目，增加现在订阅的栏目
        ChannelTenant channelTenant = null;
        channelBiz.deleteByTenant(tenantId);
        String[] ids = channelIds.split(",");
        for (String id : ids) {
            if (StringUtils.isBlank(id)) {
                continue;
            }
            channelTenant = new ChannelTenant();
            channelTenant.setChannelId(id);
            channelTenant.setTenantId(entity.getId());
            channelBiz.addChannelTenant(channelTenant);
        }
        return responseResult;
    }

    @ApiOperation("编辑查询")
    @RequestMapping(value = "/{id}/get",method = RequestMethod.GET)
    public ObjectRestResponse get(@PathVariable("id") String id){
        Tenant tenant = baseBiz.selectById(id);
        Map<String,Object> map = new HashMap<>();
        map.put("tenant",tenant);
        //查询绑定的租户管理员
        List<User> users=userBiz.selectUser(id,"1",null);
        if(users.size()>0){
            User user=users.get(0);
            map.put("user",user);
        }
        return new ObjectRestResponse<>().data(map);
    }

    @ApiOperation("搜索用户")
    @RequestMapping(value = "/selectByUsername",method = RequestMethod.GET)
    public ObjectRestResponse selectByUsername(@RequestParam String id, String username){
        //搜索用户
        List<User> users=userBiz.selectUser(id,null,username);
        return new ObjectRestResponse<>().data(users);
    }

    @ApiOperation("删除站点")
    @GetMapping("delete")
    public BaseResponse deleteTenant(String tenantId, String superAdminPassword){
        if(ValidatorUtils.hasAnyBlank(tenantId,superAdminPassword)){
            throw new ParamErrorException("参数不能为空");
        }
        baseBiz.deleteTanant(tenantId,superAdminPassword);
        return new BaseResponse();
    }




}