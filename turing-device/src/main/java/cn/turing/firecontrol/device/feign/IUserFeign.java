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

package cn.turing.firecontrol.device.feign;

import cn.turing.firecontrol.auth.client.config.FeignApplyConfiguration;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author hanyong
 * @create 2018/2/11.
 */
@FeignClient(value = "turing-admin", configuration = FeignApplyConfiguration.class)
public interface IUserFeign {
    /**
     * 获取当前用户授权的部门数据权限Id列表
     *
     * @return
     */
    @RequestMapping(value = "/user/dataDepart", method = RequestMethod.GET)
    List<String> getUserDataDepartIds(@RequestParam("userId") String userId);

    @GetMapping(value = "/user/isSuperAdmin")
    Boolean isSuperAdmin(@RequestParam("userId") String userId);

    @RequestMapping(value = "channel/all", method = {RequestMethod.GET})
    List<Map<String, Object>> getAll();

    @RequestMapping(value = "channel/{id}", method = {RequestMethod.GET})
    JSONObject selectById(@PathVariable(value = "id") Integer id);

    /**
     * 根据用户名查找用户信息
     *
     * @param username
     * @return
     */
    @RequestMapping(value = "/user/info", method = RequestMethod.POST)
    JSONObject getUser(@RequestParam("username") String username);

    /**
     * 根据租户id获取拥有的栏目
     */
    @RequestMapping(value = "/channel/tree", method = RequestMethod.GET)
    ObjectRestResponse tree(@RequestParam("tenantId") String tenantId);

    /**
     * 根据用户ids批量查询用户信息（分页）
     */
    @RequestMapping(value = "/user/queryUsersByPage", method = RequestMethod.GET)
    TableResultResponse<Map> queryUsersByPage(@RequestParam("ids") String ids, @RequestParam("username") String username, @RequestParam("name") String name, @RequestParam("mobilePhone") String mobilePhone, @RequestParam("isReverse") Boolean isReverse, @RequestParam("pageNum") String pageNum, @RequestParam("limit") String limit, @RequestParam("tenantId") String tenantId);

    /**
     * 根据用户ids批量查询用户信息（不分页）
     */
    @RequestMapping(value = "/user/queryUsers", method = RequestMethod.GET)
    ObjectRestResponse queryUsers(@RequestParam("ids") String ids, @RequestParam("username") String username, @RequestParam("name") String name, @RequestParam("mobilePhone") String mobilePhone, @RequestParam("isReverse") Boolean isReverse, @RequestParam("tenantId") String tenantId);

    @RequestMapping(value = "/user/tenant/admin", method = RequestMethod.GET)
    public JSONObject getTenantAdmin(@RequestParam("tenantId") String tenantId);

    @RequestMapping(value = "/user/info/userId", method = RequestMethod.POST)
    JSONObject getUserById(@RequestParam("userId") String userId);
}
