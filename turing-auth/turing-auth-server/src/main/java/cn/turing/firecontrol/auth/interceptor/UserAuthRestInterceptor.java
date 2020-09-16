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

package cn.turing.firecontrol.auth.interceptor;

import com.alibaba.fastjson.JSON;
import cn.turing.firecontrol.core.constants.CommonConstants;
import cn.turing.firecontrol.core.util.jwt.IJWTInfo;
import cn.turing.firecontrol.auth.configuration.UserConfiguration;
import cn.turing.firecontrol.auth.jwt.user.JwtTokenUtil;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.common.constant.RequestHeaderConstants;
import cn.turing.firecontrol.common.exception.auth.NonLoginException;
import cn.turing.firecontrol.common.msg.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ace
 * @version 2017/9/10
 */
@Slf4j
public class UserAuthRestInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserConfiguration userConfiguration;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(userConfiguration.getUserTokenHeader());
        if (StringUtils.isEmpty(token)) {
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals(userConfiguration.getUserTokenHeader())) {
                        token = cookie.getValue();
                    }
                }
            }
        }
        if (token != null && token.startsWith(RequestHeaderConstants.JWT_TOKEN_TYPE)) {
            token = token.substring(RequestHeaderConstants.JWT_TOKEN_TYPE.length(), token.length());
        }
        try {
            IJWTInfo infoFromToken = jwtTokenUtil.getInfoFromToken(token);
            BaseContextHandler.setToken(token);
            BaseContextHandler.setUsername(infoFromToken.getUniqueName());
            BaseContextHandler.setName(infoFromToken.getName());
            BaseContextHandler.setUserID(infoFromToken.getId());
            BaseContextHandler.setDepartID(infoFromToken.getOtherInfo().get(CommonConstants.JWT_KEY_DEPART_ID));
            BaseContextHandler.setTenantID(infoFromToken.getOtherInfo().get(CommonConstants.JWT_KEY_TENANT_ID));
        } catch (NonLoginException ex) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            log.error(ex.getMessage(), ex);
            response.setContentType("UTF-8");
            response.getOutputStream().println(JSON.toJSONString(new BaseResponse(ex.getStatus(), ex.getMessage())));
            return false;
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
