package cn.turing.firecontrol.admin.vo;

import lombok.Data;
import lombok.ToString;

/**
 * Created by hanyong on 2018/09/18 10:38
 *
 * @Description 用于页面展示的用户信息
 * @Version V1.0
 */
@Data
public class PageUser {

    //用户ID
    private String id;
    //用户名
    private String username;
    //用户真实姓名
    private String name;
    //用户手机号
    private String mobilePhone;
    //用户角色名称
    private String groupName;

}
