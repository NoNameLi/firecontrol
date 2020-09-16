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

package cn.turing.firecontrol.auth.client.interceptor;

import cn.turing.firecontrol.auth.client.annotation.TestUserToken;
import cn.turing.firecontrol.auth.client.config.UserAuthConfig;
import cn.turing.firecontrol.auth.client.feign.ServiceAuthFeign;
import cn.turing.firecontrol.auth.client.jwt.UserAuthUtil;
import com.alibaba.fastjson.JSON;
import cn.turing.firecontrol.core.constants.CommonConstants;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.core.util.jwt.IJWTInfo;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.constant.RequestHeaderConstants;
import cn.turing.firecontrol.common.exception.auth.NonLoginException;
import cn.turing.firecontrol.common.msg.BaseResponse;
import com.google.common.net.UrlEscapers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 * 用户token拦截认证
 *
 * @author ace
 * @version 2017/9/10
 */
public class UserAuthRestInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(UserAuthRestInterceptor.class);

    @Autowired
    private UserAuthUtil userAuthUtil;

    @Autowired
    private UserAuthConfig userAuthConfig;

    @Autowired
    private ServiceAuthFeign serviceAuthFeign;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if (HttpMethod.OPTIONS.matches(method)){
            return super.preHandle(request, response, handler);
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        CheckUserToken annotation = handlerMethod.getBeanType().getAnnotation(CheckUserToken.class);
        // 配置该注解，说明不进行用户拦截
        IgnoreUserToken ignoreUserToken = handlerMethod.getMethodAnnotation(IgnoreUserToken.class);
        if (annotation == null) {
            annotation = handlerMethod.getMethodAnnotation(CheckUserToken.class);
        }
        if (annotation == null || ignoreUserToken != null) {
            return super.preHandle(request, response, handler);
        } else {
            String token = null;
            //测试用户Token
            TestUserToken testUserToken = handlerMethod.getBeanType().getAnnotation(TestUserToken.class);
            if(testUserToken != null){
                token = testUserToken.token();
            }else {
                token = request.getHeader(userAuthConfig.getTokenHeader());
            }
            if (StringUtils.isEmpty(token)) {
                if (request.getCookies() != null) {
                    for (Cookie cookie : request.getCookies()) {
                        if (cookie.getName().equals(userAuthConfig.getTokenHeader())) {
                            token = cookie.getValue();
                            token = URLDecoder.decode(token,"UTF-8");
                        }
                    }
                }
            }
            if (token != null && token.startsWith(RequestHeaderConstants.JWT_TOKEN_TYPE)) {
                token = token.substring(RequestHeaderConstants.JWT_TOKEN_TYPE.length(),token.length());
            }
            try {
                String tenantID = BaseContextHandler.getTenantID();
                IJWTInfo infoFromToken = userAuthUtil.getInfoFromToken(token);
                BaseContextHandler.setToken(token);
                BaseContextHandler.setUsername(infoFromToken.getUniqueName());
                BaseContextHandler.setName(infoFromToken.getName());
                BaseContextHandler.setUserID(infoFromToken.getId());
                BaseContextHandler.setDepartID(infoFromToken.getOtherInfo().get(CommonConstants.JWT_KEY_DEPART_ID));
                BaseContextHandler.setIsSuperAdmin(infoFromToken.getOtherInfo().get(CommonConstants.CONTEXT_KEY_IS_SUPER_ADMIN));
                BaseContextHandler.setIsTenantAdmin(infoFromToken.getOtherInfo().get(CommonConstants.CONTEXT_KEY_IS_TENANT_ADMIN));
                String userTenantId = infoFromToken.getOtherInfo().get(CommonConstants.JWT_KEY_TENANT_ID);
                if(StringUtils.isNoneBlank(tenantID)){
                    if(!tenantID.equals(userTenantId)){
                        throw new NonLoginException("用户不合法!");
                    }
                }
                BaseContextHandler.setTenantID(userTenantId);
            }catch(NonLoginException ex){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                logger.error(ex.getMessage(),ex);
                response.setContentType("UTF-8");
                response.getOutputStream().println(JSON.toJSONString(new BaseResponse(ex.getStatus(), ex.getMessage())));
                return false;
            }

            return super.preHandle(request, response, handler);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
