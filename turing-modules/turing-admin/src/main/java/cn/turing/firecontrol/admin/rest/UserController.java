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
import cn.turing.firecontrol.admin.biz.PositionBiz;
import cn.turing.firecontrol.admin.biz.UserBiz;
import cn.turing.firecontrol.admin.biz.ValicodeBiz;
import cn.turing.firecontrol.admin.entity.Menu;
import cn.turing.firecontrol.admin.entity.Position;
import cn.turing.firecontrol.admin.entity.User;
import cn.turing.firecontrol.admin.rpc.service.PermissionService;
import cn.turing.firecontrol.admin.vo.AuthUser;
import cn.turing.firecontrol.admin.vo.FrontUser;
import cn.turing.firecontrol.admin.vo.MenuTree;
import cn.turing.firecontrol.admin.vo.PageUser;
import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.BaseResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Sha256PasswordEncoder;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.api.vo.user.UserInfo;
import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import com.github.pagehelper.Page;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @version 2017-06-08 11:51
 */
@RestController
@RequestMapping("user")
@CheckUserToken
@CheckClientToken
@Api(tags = "用户模块")
public class UserController extends BaseController<UserBiz, User,String> {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PositionBiz positionBiz;

    @Autowired
    private MenuBiz menuBiz;

    @Autowired
    private ValicodeBiz valicodeBiz;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @IgnoreUserToken
    @ApiOperation("账户密码验证")
    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public ObjectRestResponse<UserInfo> validate(String username, String password) {
        return new ObjectRestResponse<UserInfo>().data(permissionService.validate(username, password));
    }

    @IgnoreUserToken
    @ApiOperation("根据账户名获取用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public ObjectRestResponse<AuthUser> validate(String username) {
        if (StringUtils.isBlank(username)){
            throw new RuntimeException("用户名不能为空");
        }
        AuthUser user = new AuthUser();
        User u = baseBiz.getUserByUsername(username);
        BeanUtils.copyProperties(u, user);
        return new ObjectRestResponse<AuthUser>().data(user);
    }

    @IgnoreUserToken
    @ApiOperation("根据账户名获取用户信息")
    @RequestMapping(value = "/info/userId", method = RequestMethod.POST)
    public ObjectRestResponse<Map<String,Object>> getUserInfo(String userId){
        if(StringUtils.isBlank(userId)){
            throw new ParamErrorException("用户ID不能为空");
        }
        User user = baseBiz.selectById(userId);
        if(user == null){
            throw new ParamErrorException("用户不存在");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("userId",user.getId());
        map.put("username",user.getUsername());
        map.put("name",user.getName());
        map.put("mobilePhone",user.getMobilePhone());
        return new ObjectRestResponse<>().data(map);
    }


    @ApiOperation("账户修改密码")
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ObjectRestResponse<Boolean> changePassword(String oldPass, String newPass) {
        return new ObjectRestResponse<Boolean>().data(baseBiz.changePassword(oldPass, newPass));
    }



    @ApiOperation("获取用户信息接口")
    @RequestMapping(value = "/front/info", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getUserInfo() throws Exception {
        FrontUser userInfo = permissionService.getUserInfo();
        if (userInfo == null) {
            return ResponseEntity.status(401).body(false);
        } else {
            return ResponseEntity.ok(userInfo);
        }
    }

    @ApiOperation("获取用户访问菜单")
    @RequestMapping(value = "/front/menus", method = RequestMethod.GET)
    public
    @ResponseBody
    List<MenuTree> getMenusByUsername() throws Exception {
        List<MenuTree> list = permissionService.getMenusByUsername();
        return list;
    }

    @ApiOperation("获取所有菜单")
    @RequestMapping(value = "/front/menu/all", method = RequestMethod.GET)
    public @ResponseBody
    List<Menu> getAllMenus() throws Exception {
        return menuBiz.selectListAll();
    }

    @ApiOperation("获取用户可管辖部门id列表")
    @RequestMapping(value = "/dataDepart", method = RequestMethod.GET)
    public List<String> getUserDataDepartIds(String userId) {
        if (BaseContextHandler.getUserID().equals(userId)) {
            return baseBiz.getUserDataDepartIds(userId);
        }
        return new ArrayList<>();
    }

    @ApiOperation("获取用户流程审批岗位")
    @RequestMapping(value = "/flowPosition", method = RequestMethod.GET)
    @IgnoreClientToken
    @IgnoreUserToken
    public List<String> getUserFlowPositions(String userId) {
        if (BaseContextHandler.getUserID().equals(userId)) {
            return positionBiz.getUserFlowPosition(userId).stream().map(Position::getName).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @ApiOperation("查询用户是否为超级用户")
    @RequestMapping(value = "/isSuperAdmin", method = RequestMethod.GET)
    public Boolean isSuperAdmin(String userId) {
        if(StringUtils.isBlank(userId)){
            return false;
        }
        User user = baseBiz.selectById(userId);
        if(user == null){
            return false;
        }
        if("1".equals(user.getIsSuperAdmin())){
            return true;
        }else {
            return false;
        }
    }

    @IgnoreUserToken
    @ApiOperation("忘记密码")
    @PostMapping("/updatepwd")
    public BaseResponse updatePwd(String username, String code, String pwd){
        if(ValidatorUtils.hasAnyBlank(username,code,pwd)){
            throw new ParamErrorException("参数不能为空");
        }
        User user = baseBiz.getUserByUsername(username);
        if(user == null){
            throw new ParamErrorException("用户不存在");
        }
        valicodeBiz.validateCode(username,code);
        user.setPassword(new Sha256PasswordEncoder().encode(pwd));
        baseBiz.updatePwd(user);
        return new BaseResponse();
    }

    @ApiOperation("查询用户列表")
    @GetMapping("list")
    public TableResultResponse<Map<String,String>> list(Integer page, Integer limit, String username, String groupId, String name, String mobilePhone){
        if(page == null){
            page = 1;
        }
        if(limit == null){
            limit = 15;
        }
        return baseBiz.pageList(page,limit,username,groupId,name,mobilePhone);
    }

    @ApiOperation("添加用户")
    @PostMapping("add")
    public BaseResponse addUser(String username, String name, String mobilePhone, String password, String groupId){
        if(ValidatorUtils.hasAnyBlank(username,mobilePhone,password)){//name可以为空
            throw new ParamErrorException("参数不能为空");
        }
        baseBiz.addUser(username, name, mobilePhone, password, groupId);
        return new BaseResponse();
    }

    @ApiOperation("修改用户信息")
    @PostMapping("update")
    public BaseResponse updateUser(String username, String name, String mobilePhone, String groupId){
        if(ValidatorUtils.hasAnyBlank(username,mobilePhone)){
            throw new ParamErrorException("参数不能为空");
        }
        baseBiz.updateUser(username, name, mobilePhone, groupId);
        return new BaseResponse();
    }

    @ApiOperation("批量删除用户")
    @GetMapping("delete")
    public BaseResponse batchDelete(String userIds){
        if(ValidatorUtils.hasAnyBlank(userIds)){
            throw new ParamErrorException("参数不能为空");
        }
        baseBiz.deleteUser(userIds);
        return new BaseResponse();
    }

    @ApiOperation("查询超级管理员创建的未设置为站点管理员的用户")
    @GetMapping("unbindUser")
    public ObjectRestResponse<List<Map<String,String>>> unbindUser(String tenantId){
        List<Map<String,String>> users= baseBiz.selectUnbindTenantAdmin();
        if(tenantId != null){
            User user = baseBiz.queryTenantAdmin(tenantId);
            if(user != null){
                Map<String,String> map = new HashMap<String,String>();
                map.put("id",user.getId());
                map.put("username",user.getUsername());
                map.put("name",user.getName());
                users.add(0,map);
            }
        }
        return new ObjectRestResponse<>().data(users);
    }

    /**
     * 查询指定站点的管理员信息
     * @param tenantId
     * @return
     */
    @ApiOperation("查询站点管理员信息")
    @GetMapping("tenant/admin")
    @IgnoreUserToken
    @IgnoreClientToken
    public User getTenantAdmin(String tenantId){
        if(ValidatorUtils.hasAnyBlank(tenantId)){
            throw new ParamErrorException("参数不能为空");
        }
        User user = baseBiz.queryTenantAdmin(tenantId);
        user.setPassword(null);
        return user;
    }

    /**
     * 查询指定用户的手机号码（中间4位用*代替）
     * @param username
     * @return
     */
    @ApiOperation("获取用户手机号")
    @GetMapping("tel")
    @IgnoreUserToken
    @IgnoreClientToken
    public ObjectRestResponse<String> getTel(String username){
        if(ValidatorUtils.hasAnyBlank(username)){
            throw new ParamErrorException("参数不能为空");
        }
        User user = baseBiz.getUserByUsername(username);
        String mobilePhone = user.getMobilePhone();
        if(StringUtils.isNotBlank(mobilePhone)){
            mobilePhone = mobilePhone.substring(0,3) + "****" + mobilePhone.substring(7);
        }
        return new ObjectRestResponse<String>().data(mobilePhone);
    }

    @GetMapping("queryUsersByPage")
    @ApiOperation("批量查询站点下用户信息（模糊搜索）: 分页")
    @ApiImplicitParams({@ApiImplicitParam(name = "ids",value = "需要查询用户ID（多个用逗号隔开）",paramType ="query"),
            @ApiImplicitParam(name = "tenantId",value = "站点ID",paramType ="query"),
            @ApiImplicitParam(name = "username",value = "用户名",paramType ="query"),
            @ApiImplicitParam(name = "name",value = "真实姓名",paramType ="query"),
            @ApiImplicitParam(name = "mobilePhone",value = "用户手机号",paramType ="query"),
            @ApiImplicitParam(name = "isReverse",value = "是否取反，true(查询不包含ids的用户),false(只查询包含ids的用户)",paramType ="query",dataType = "boolean",defaultValue = "false"),
            @ApiImplicitParam(name = "pageNum",value = "分页页数",paramType ="query"),
            @ApiImplicitParam(name = "limit",value = "每页数量",paramType ="query")
    })
    public TableResultResponse<PageUser> queryUsers(String tenantId,String ids,String username, String name, String mobilePhone, @RequestParam(defaultValue = "false") Boolean isReverse,
                                                @RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "15") Integer limit){
        Page<PageUser> page = baseBiz.queryUsers(tenantId,ids,username,name,mobilePhone,isReverse,pageNum,limit);
        return new TableResultResponse<PageUser>(page.getTotal(),page.getResult());
    }

    @GetMapping("queryUsers")
    @IgnoreUserToken
    @ApiOperation("批量查询站点下用户信息（模糊搜索）: 不分页")
    @ApiImplicitParams({@ApiImplicitParam(name = "ids",value = "需要查询用户ID（多个用逗号隔开）",paramType ="query"),
            @ApiImplicitParam(name = "tenantId",value = "站点ID",paramType ="query"),
            @ApiImplicitParam(name = "username",value = "用户名",paramType ="query"),
            @ApiImplicitParam(name = "name",value = "真实姓名",paramType ="query"),
            @ApiImplicitParam(name = "mobilePhone",value = "用户手机号",paramType ="query"),
            @ApiImplicitParam(name = "isReverse",value = "是否取反，true(查询不包含ids的用户),false(只查询包含ids的用户)",paramType ="query",dataType = "boolean",defaultValue = "false")
    })
    public ObjectRestResponse<List<PageUser>> queryUsers(String tenantId,String ids,String username, String name, String mobilePhone, @RequestParam(defaultValue = "false") Boolean isReverse){
        List<PageUser> users = baseBiz.queryUsers(tenantId,ids,username,name,mobilePhone,isReverse);
        return new ObjectRestResponse<List<PageUser>>().data(users);
    }






}
