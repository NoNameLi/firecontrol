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

import cn.turing.firecontrol.admin.entity.Tenant;
import cn.turing.firecontrol.admin.mapper.GroupMapper;
import cn.turing.firecontrol.admin.mapper.TenantMapper;
import cn.turing.firecontrol.admin.vo.PageUser;
import cn.turing.firecontrol.common.exception.base.BusinessException;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.*;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.merge.core.MergeCore;
import cn.turing.firecontrol.admin.entity.User;
import cn.turing.firecontrol.admin.mapper.DepartMapper;
import cn.turing.firecontrol.admin.mapper.UserMapper;
import cn.turing.firecontrol.common.biz.BaseBiz;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @version 2017-06-08 16:23
 */
@Service
@Transactional(rollbackFor = Exception.class)
@CacheConfig(cacheNames = "users")
public class UserBiz extends BaseBiz<UserMapper, User> {
    @Autowired
    private MergeCore mergeCore;

    @Autowired
    private DepartMapper departMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private TenantMapper tenantMapper;

    private Sha256PasswordEncoder encoder = new Sha256PasswordEncoder();


    @Override
    public User selectById(Object id) {
        User user = super.selectById(id);
        try {
            mergeCore.mergeOne(User.class, user);
            return user;
        } catch (Exception e) {
            return super.selectById(id);
        }
    }

    public Boolean changePassword(String oldPass, String newPass) {
        User user = this.getUserByUsername(BaseContextHandler.getUsername());
        if (!encoder.matches(oldPass, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        String password = encoder.encode(newPass);
        user.setPassword(password);
        this.updateSelectiveById(user);
        return true;
    }

    @Override
    public void insertSelective(User entity) {
        String password = encoder.encode(entity.getPassword());
        String departId = entity.getDepartId();
        EntityUtils.setCreatAndUpdatInfo(entity);
        entity.setPassword(password);
        entity.setDepartId(departId);
        entity.setIsDeleted(BooleanUtil.BOOLEAN_FALSE);
        entity.setIsDisabled(BooleanUtil.BOOLEAN_FALSE);
        String userId = UUIDUtils.generateUuid();
        entity.setTenantId(BaseContextHandler.getTenantID());
        entity.setId(userId);
        entity.setIsSuperAdmin(BooleanUtil.BOOLEAN_FALSE);
        ValidatorUtils.validateEntity(entity);
        List<User> users = mapper.selectUserByMobilePhone(entity.getMobilePhone());
        if(users.size()>0){
            throw new ParamErrorException("手机号码已存在");
        }
        // 如果非超级管理员,无法修改用户的租户信息
        if (BooleanUtil.BOOLEAN_FALSE.equals(mapper.selectByPrimaryKey(BaseContextHandler.getUserID()).getIsSuperAdmin())) {
            entity.setIsSuperAdmin(BooleanUtil.BOOLEAN_FALSE);
        }
        departMapper.insertDepartUser(UUIDUtils.generateUuid(), entity.getDepartId(), entity.getId(),BaseContextHandler.getTenantID());
        super.insertSelective(entity);
    }

    @Override
    public void updateSelectiveById(User entity) {
        EntityUtils.setUpdatedInfo(entity);
        User user = mapper.selectByPrimaryKey(entity.getId());
        if (!user.getDepartId().equals(entity.getDepartId())) {
            departMapper.deleteDepartUser(user.getDepartId(), entity.getId());
            departMapper.insertDepartUser(UUIDUtils.generateUuid(), entity.getDepartId(), entity.getId(),BaseContextHandler.getTenantID());
        }
        // 如果非超级管理员,无法修改用户的租户信息
        if (BooleanUtil.BOOLEAN_FALSE.equals(mapper.selectByPrimaryKey(BaseContextHandler.getUserID()).getIsSuperAdmin())) {
            entity.setTenantId(BaseContextHandler.getTenantID());
        }
        // 如果非超级管理员,无法修改用户的租户信息
        if (BooleanUtil.BOOLEAN_FALSE.equals(mapper.selectByPrimaryKey(BaseContextHandler.getUserID()).getIsSuperAdmin())) {
            entity.setIsSuperAdmin(BooleanUtil.BOOLEAN_FALSE);
        }
        super.updateSelectiveById(entity);
    }

    @Override
    public void deleteById(Object id) {
        User user = mapper.selectByPrimaryKey(id);
        user.setIsDeleted(BooleanUtil.BOOLEAN_TRUE);
        this.updateSelectiveById(user);
    }

    @Override
    public List<User> selectByExample(Object obj) {
        Example example = (Example) obj;
        example.createCriteria().andEqualTo("isDeleted", BooleanUtil.BOOLEAN_FALSE);
        List<User> users = super.selectByExample(example);
        /*try {
            mergeCore.mergeResult(User.class, users);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return users;
        }*/
        return users;
    }

    /**
     * 分页查询用户信息：管理员只查看自己创建的用户信息，站点用户管理员可以查看自己站点的用户信息
     * @param query
     * @return
     */
    @Override
    public TableResultResponse<User> selectByQuery(Query query) {
        User user = mapper.selectByPrimaryKey(BaseContextHandler.getUserID());
        TableResultResponse<User> response = null;
        if(BooleanUtil.BOOLEAN_TRUE.equals(user.getIsSuperAdmin())){
            Page<User> page = PageHelper.startPage(query.getPage(),query.getLimit());
            List<User> users = mapper.selectByCreater(user.getId());
            response = new TableResultResponse<User>(page.getTotal(),users);
        }else {
            response = super.selectByQuery(query);
        }
        return response;
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username
     * @return
     */
    public synchronized User getUserByUsername(String username) {
        if(StringUtils.isBlank(username)){
            return null;
        }
        User user = new User();
        user.setUsername(username);
        user.setIsDeleted(BooleanUtil.BOOLEAN_FALSE);
        user.setIsDisabled(BooleanUtil.BOOLEAN_FALSE);
        user = mapper.selectOne(user);
        return user;
    }

    @Override
    public void query2criteria(Query query, Example example) {
        if (query.entrySet().size() > 0) {
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                Example.Criteria criteria = example.createCriteria();
                criteria.andLike(entry.getKey(), "%" + entry.getValue().toString() + "%");
                example.or(criteria);
            }
        }
    }

    public List<String> getUserDataDepartIds(String userId) {
        return mapper.selectUserDataDepartIds(userId);
    }

    /**
     * 修改用户登陆密码
     * @param entity
     */
    public void updatePwd(User entity){
        User user = new User();
        user.setId(entity.getId());
        user.setPassword(entity.getPassword());
        super.updateSelectiveById(user);
    }
    public List<User> selectUser(String tenantId,String attr1,String username){
        User user = new User();
        user.setAttr1(attr1);
        user.setTenantId(tenantId);
        user.setUsername(username);
        return userMapper.selectUser(user);
    }

    /**
     * 查询用户信息
     * @param page
     * @param limit
     * @return
     */
    public TableResultResponse<Map<String,String>> pageList(Integer page, Integer limit, String username, String groupId, String name, String mobilePhone){
        User user = getUserByUsername(BaseContextHandler.getUsername());
        String crtUserId = null;
        // 超级管理员只能看本账号创建的用户
        if(BooleanUtil.BOOLEAN_TRUE.equals(user.getIsSuperAdmin())){
            crtUserId = user.getId();
        }
        Page<Map<String,String>> pageInfo = PageHelper.startPage(page,limit);
        List<Map<String,String>> users = userMapper.listUser(username,groupId,name,mobilePhone,crtUserId);
        return new TableResultResponse<Map<String,String>>(pageInfo.getTotal(),users);
    }

    /**
     * 修改用户信息
     * @param username
     * @param name
     * @param mobilePhone
     * @param groupId
     */
    public void updateUser(String username,String name, String mobilePhone, String groupId){
        User user = getUserByUsername(username);
        if(user == null){
            throw new ParamErrorException("用户名不存在");
        }
        List<User> users = userMapper.selectUserByMobilePhone(mobilePhone);
        if(users.size() > 1 || (users.size() == 1 && !users.get(0).getUsername().equals(username))){
            throw new ParamErrorException("手机号已存在");
        }
        user.setName(name);
        user.setMobilePhone(mobilePhone);
        ValidatorUtils.validateEntity(user);
        updateSelectiveById(user);
        //修改用户角色
        groupMapper.deleteGroupLeadersByUserId(user.getId());
        groupMapper.deleteGroupMembersByUserId(user.getId());
        groupMapper.insertGroupMembersById(UUIDUtils.generateShortUuid(),groupId,user.getId(),user.getTenantId());
    }

    /**
     * 增加用户
     * @param username
     * @param name
     * @param mobilePhone
     * @param password
     * @param groupId
     */
    public void addUser(String username,String name, String mobilePhone, String password, String groupId){
        User user = new User();
        user.setUsername(username);
        user = mapper.selectOne(user);
        if(user != null){
            throw new ParamErrorException("用户名已存在");
        }
        List<User> users = userMapper.selectUserByMobilePhone(mobilePhone);
        if(users.size() > 1 || (users.size() == 1 && !users.get(0).getUsername().equals(username))){
            throw new ParamErrorException("手机号已存在");
        }
        user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setMobilePhone(mobilePhone);
        user.setPassword(encoder.encode(password));
        EntityUtils.setCreateInfo(user);
        EntityUtils.setUpdatedInfo(user);
        user.setIsDeleted(BooleanUtil.BOOLEAN_FALSE);
        user.setIsDisabled(BooleanUtil.BOOLEAN_FALSE);
        User current = mapper.selectByPrimaryKey(BaseContextHandler.getUserID());
        //如果是超级管理员
        if(BooleanUtil.BOOLEAN_TRUE.equals(current.getIsSuperAdmin())){
            user.setTenantId(null);
        }else {
            user.setTenantId(BaseContextHandler.getTenantID());
        }
        String userId = UUIDUtils.generateShortUuid();
        user.setId(userId);
        user.setIsSuperAdmin(BooleanUtil.BOOLEAN_FALSE);
        user.setAttr1(BooleanUtil.BOOLEAN_FALSE);
        ValidatorUtils.validateEntity(user);
        mapper.insertSelective(user);
        if(StringUtils.isNotBlank(groupId)){
            groupMapper.insertGroupMembersById(UUIDUtils.generateShortUuid(),groupId,user.getId(),user.getTenantId());
        }
    }

    @Transactional
    public void deleteUser(String userIds){
        String[] ids = userIds.split(",");
        StringBuilder sb = new StringBuilder();
        for(String id : ids){
            sb.append("'").append(id).append("'").append(",");
        }
        if(sb.toString().isEmpty()){
            throw new RuntimeException("参数不能为空");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        List<User> users = userMapper.batchQuery(sb.toString());
        for(User u : users){
            if(BooleanUtil.BOOLEAN_TRUE.equals(u.getIsSuperAdmin())){
                throw new BusinessException(String.format("用户%s是超级管理员，无法删除",u.getUsername()));
            }
            if(u.getId().equals(BaseContextHandler.getUserID())){
                throw new BusinessException("无法删除自己");
            }
            if(StringUtils.isNotBlank(u.getTenantId()) && BooleanUtil.BOOLEAN_TRUE.equals(u.getAttr1())){
                Tenant tenant = tenantMapper.selectByPrimaryKey(u.getTenantId());
                if(tenant == null){
                    continue;
                }
                throw new BusinessException(String.format("用户%s是%s站点管理员，无法删除",u.getUsername(),tenant.getName()));
            }
        }
        //判断被删除的用户中有没有站点管理员
        userMapper.batchDelete(sb.toString());
    }

    public User queryTenantAdmin(String tenantId){
        if(StringUtils.isBlank(tenantId)){
            throw new ParamErrorException("站点不能为空");
        }
        User user = mapper.selelcTenantAdmin(tenantId);
        return user;
    }

    /**
     * 查询由超级管理员创建的未设置为站点管理员的用户
     * @return
     */
    public List<Map<String,String>> selectUnbindTenantAdmin(){
        return mapper.selectUnbindTenantAdmin();
    }


    /**
     *查询用户信息: 分页
     * @param ids 用逗号隔开的用户ID
     * @param username 用户名
     * @param name 真实姓名
     * @param mobilePhone 手机号
     * @param isReserve 是否取反
     * @return
     */
    public Page<PageUser> queryUsers(String tenantId, String ids, String username, String name, String mobilePhone, Boolean isReserve,Integer pageNum, Integer limit){
        StringBuilder userIds = new StringBuilder("''");
        if(StringUtils.isNotBlank(ids)){
            String[] id = ids.split(",");
            for(String i : id){
                userIds.append(",").append("'").append(i).append("'");
            }
        }
        Page<PageUser> page = PageHelper.startPage(pageNum,limit);
        List<PageUser> users = mapper.queryUsers(userIds.toString(),username,name,mobilePhone,isReserve,tenantId);
        return page;
    }

    /**
     *查询用户信息：不分页
     * @param ids 用逗号隔开的用户ID
     * @param username 用户名
     * @param name 真实姓名
     * @param mobilePhone 手机号
     * @param isReserve 是否取反
     * @return
     */
    public List<PageUser> queryUsers(String tenantId, String ids, String username, String name, String mobilePhone, Boolean isReserve){
        StringBuilder userIds = new StringBuilder("''");
        if(StringUtils.isNotBlank(ids)){
            String[] id = ids.split(",");
            for(String i : id){
                userIds.append(",").append("'").append(i).append("'");
            }
        }
        List<PageUser> users = mapper.queryUsers(userIds.toString(),username,name,mobilePhone,isReserve,tenantId);
        return users;
    }







}
