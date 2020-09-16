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

import cn.turing.firecontrol.admin.constant.AdminCommonConstant;
import cn.turing.firecontrol.admin.entity.Element;
import cn.turing.firecontrol.admin.entity.User;
import cn.turing.firecontrol.admin.mapper.ElementMapper;
import cn.turing.firecontrol.admin.mapper.UserMapper;
import com.ace.cache.annotation.Cache;
import com.ace.cache.annotation.CacheClear;
import cn.turing.firecontrol.merge.annonation.MergeResult;
import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.util.BooleanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @version 2017-06-23 20:27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ElementBiz extends BusinessBiz<ElementMapper,Element> {
//    @Autowired
//    private UserMapper userMapper;
    /**
     * 获取用户可以访问的资源
     * @param userId
     * @return
     */
    @Cache(key="permission:ele:u{1}")
    public List<Element> getAuthorityElementByUserId(String userId){
       /* User user = userMapper.selectByPrimaryKey(userId);
        if(BooleanUtil.BOOLEAN_TRUE.equals(user.getIsSuperAdmin())){
            return mapper.selectAllElementPermissions();
        }
        //如果租户为站点管理员，则查询所有按钮,暂时不做增删改查权限管控
        if(BooleanUtil.BOOLEAN_TRUE.equals(user.getAttr1())){
            return mapper.selectAllElementPermissions();
        }
       return mapper.selectAuthorityElementByUserId(userId, AdminCommonConstant.RESOURCE_TYPE_VIEW);*/
        return mapper.selectAllElementPermissions();
    }

    public List<Element> getAuthorityElementByUserId(String userId,String menuId){
        return mapper.selectAuthorityMenuElementByUserId(userId,menuId,AdminCommonConstant.RESOURCE_TYPE_VIEW);
    }

    @Cache(key="permission:ele")
    public List<Element> getAllElementPermissions(){
        return mapper.selectAllElementPermissions();
    }

    @Override
    @CacheClear(keys={"permission:ele","permission"})
    public void insertSelective(Element entity) {
        super.insertSelective(entity);
    }

    @Override
    @CacheClear(keys={"permission:ele","permission"})
    public void updateSelectiveById(Element entity) {
        super.updateSelectiveById(entity);
    }

    @MergeResult
    @Override
    public List<Element> selectByExample(Object example) {
        return super.selectByExample(example);
    }
}
