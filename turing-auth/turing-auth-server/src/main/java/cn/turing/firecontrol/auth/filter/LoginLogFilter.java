package cn.turing.firecontrol.auth.filter;

import cn.turing.firecontrol.auth.feign.ILoginLogService;
import cn.turing.firecontrol.auth.feign.IUserService;
import cn.turing.firecontrol.auth.module.oauth.bean.OauthUser;
import cn.turing.firecontrol.common.util.IPUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@WebFilter(filterName = "loginLogFilter", urlPatterns = "/oauth/token")
@Component
public class LoginLogFilter implements Filter{

    @Autowired
    private ILoginLogService loginLogService;
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    private  final String IP_REGION_KEY = "region:ip:";
    private final Long IP_TIMEOUT_SECOND = 72*60*60L;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        if("/oauth/token".equalsIgnoreCase(req.getRequestURI()) && !req.getMethod().equalsIgnoreCase("POST")){
            chain.doFilter(request,response);
            return;
        }
        chain.doFilter(request,response);
        HttpServletResponse res = (HttpServletResponse)response;
        if(200 == res.getStatus()){
            new LoginLogThread(req).start();
        }
        return;
    }


    /**
     * 写入登录日志的线程
     */
    class LoginLogThread extends Thread{

        private HttpServletRequest req;

        public LoginLogThread(HttpServletRequest req){
            this.req = req;
        }

        @Override
        public void run() {
            Map<String,String> login = new HashMap<String, String>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = sdf.format(new Date());
            String username = req.getParameter("username");
            if(StringUtils.isBlank(username)){
                return;
            }
            String ip = IPUtils.getIpAddr(req);
            String key = IP_REGION_KEY + ip;
            String loginRegion = redisTemplate.opsForValue().get(key);
            if(StringUtils.isBlank(loginRegion) || (StringUtils.isNotBlank(loginRegion) && loginRegion.equals("未知"))){
                loginRegion = IPUtils.getRegion(ip);
                if(!loginRegion.equals("未知")){
                    redisTemplate.opsForValue().set(key,loginRegion);
                }
            }
            redisTemplate.expire(key,IP_TIMEOUT_SECOND, TimeUnit.SECONDS);
            Map<String,String> user = userService.getUserInfoByUsername(username).getData();
            login.put("loginUserId", user.get("id"));
            login.put("loginUserName",user.get("username"));
            login.put("loginIp",ip);
            login.put("loginRegion", loginRegion);
            login.put("loginTime",now);
            login.put("crtUserName",user.get("username"));
            login.put("crtUserId",user.get("id"));
            login.put("crtTime",now);
            login.put("updUserName",user.get("username"));
            login.put("updUserId",user.get("id"));
            login.put("updTime",now);
            login.put("departId",user.get("departId"));
            login.put("tenantId",user.get("tenantId"));
            loginLogService.add(login);
        }
    }

    @Override
    public void destroy() {

    }
}
