package cn.turing.firecontrol.admin.rest;

import cn.turing.firecontrol.admin.entity.Tenant;
import cn.turing.firecontrol.admin.entity.User;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.util.Sha256PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("site")
public class SiteController {

    /**
     * 增加站点
     * @param name
     * @param domain
     * @param logoUrl
     * @param username
     * @param mobilePhone
     * @param pwd
     * @param channelIds
     * @return
     */
    /*@PostMapping("add")
    @ResponseBody*/
    public ObjectRestResponse<String> addSite(String name, String domain, String logoUrl, String username, String mobilePhone, String pwd, String channelIds){
        //1、添加租户信息（回传ID）
        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setAttr1(domain);
        tenant.setAttr2(logoUrl);
        //2、添加用户信息（回传ID）
        User user = new User();
        user.setUsername(username);
        user.setMobilePhone(mobilePhone);
        user.setPassword(new Sha256PasswordEncoder().encode(pwd));
        //3、添加该用户订阅的栏目信息（Menu）
        return null;

    }



}
