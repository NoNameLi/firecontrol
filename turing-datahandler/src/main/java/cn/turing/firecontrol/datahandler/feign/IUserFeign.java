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

package cn.turing.firecontrol.datahandler.feign;

import cn.turing.firecontrol.auth.client.config.FeignApplyConfiguration;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * @author hanyong
 * @create 2018/2/11.
 */
@FeignClient(value = "turing-admin",configuration = FeignApplyConfiguration.class)
public interface IUserFeign {
    //获取当前用户授权的部门数据权限Id列表
    @RequestMapping(value="/user/dataDepart",method = RequestMethod.GET)
    List<String> getUserDataDepartIds(@RequestParam("userId") String userId);

    @GetMapping(value = "/user/isSuperAdmin")
    Boolean isSuperAdmin(@RequestParam("userId") String userId);

    //查询管理员信息接口
    @RequestMapping(value="/user/tenant/admin",method = RequestMethod.GET,headers = "service=data-handler")
    public JSONObject getTenantAdmin(@RequestParam("tenantId") String tenantId);

    @RequestMapping(value="channel/{id}",method ={RequestMethod.GET})
    JSONObject selectById(@PathVariable(value = "id") Integer id);

    @RequestMapping(value="channel/get",method ={RequestMethod.GET})
    JSONObject grtById(@RequestParam(value = "id") Integer id);
    //查询所属系统列表(分租户)
    @RequestMapping(value="/channel/list",method ={RequestMethod.GET})
    JSONObject getChannelList();

    //查询所属系统列表（所有）
    @RequestMapping(value="/channel/all",method ={RequestMethod.GET})
    JSONArray getAllChannel();

    @RequestMapping(value="/user/info/userId",method ={RequestMethod.POST})
    JSONObject getUserInfoByUserId(@RequestParam(value = "userId") String userId,@RequestParam(value = "noticeType") String noticeType);
    /**
     * 根据用户名查找用户信息
     * @param username
     * @return
     */
    @RequestMapping(value="/user/info",method = RequestMethod.POST)
    JSONObject getUser(@RequestParam("username") String username);



}
