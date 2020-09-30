package cn.turing.firecontrol.device.vo;

public class UserVo {
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

    //是否已关联配置规则  0 否 1 是
    private int hasNoticeRule;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getHasNoticeRule() {
        return hasNoticeRule;
    }

    public void setHasNoticeRule(int hasNoticeRule) {
        this.hasNoticeRule = hasNoticeRule;
    }
}
