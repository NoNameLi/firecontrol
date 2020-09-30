package cn.turing.firecontrol.datahandler.vo;

import java.util.Date;

public class NoticeRuleVo {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则描述
     */
    private String ruleDescription;

    /**
     * 间隔时间文字
     */
    private String intervalTimeTxt;

    /**
     * 间隔时间小时
     */
    private Integer intervalTimeMinutes;

    /**
     * 所属系统
     */
    private Integer channelId;

    /**
     * 删除标记 1：删除，0：未删除
     */
    private String delFlag;

    /**
     * 创建者名称
     */
    private String crtUserName;

    /**
     * 创建者ID
     */
    private String crtUserId;

    /**
     * 创建时间
     */
    private Date crtTime;

    /**
     * 修改者名称
     */
    private String updUserName;

    /**
     * 修改者ID
     */
    private String updUserId;

    /**
     * 修改时间
     */
    private Date updTime;

    /**
     * 部门ID 未使用的字段
     */
    private String departId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 获取主键ID
     *
     * @return ID - 主键ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取规则名称
     *
     * @return RULE_NAME - 规则名称
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * 设置规则名称
     *
     * @param ruleName 规则名称
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    /**
     * 获取规则描述
     *
     * @return RULE_DESCRIPTION - 规则描述
     */
    public String getRuleDescription() {
        return ruleDescription;
    }

    /**
     * 设置规则描述
     *
     * @param ruleDescription 规则描述
     */
    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    /**
     * 获取间隔时间文字
     *
     * @return INTERVAL_TIME_TXT - 间隔时间文字
     */
    public String getIntervalTimeTxt() {
        return intervalTimeTxt;
    }

    /**
     * 设置间隔时间文字
     *
     * @param intervalTimeTxt 间隔时间文字
     */
    public void setIntervalTimeTxt(String intervalTimeTxt) {
        this.intervalTimeTxt = intervalTimeTxt;
    }

    /**
     * 获取间隔时间小时
     *
     * @return INTERVAL_TIME_MINUTES - 间隔时间小时
     */
    public Integer getIntervalTimeMinutes() {
        return intervalTimeMinutes;
    }

    /**
     * 设置间隔时间小时
     *
     * @param intervalTimeMinutes 间隔时间小时
     */
    public void setIntervalTimeMinutes(Integer intervalTimeMinutes) {
        this.intervalTimeMinutes = intervalTimeMinutes;
    }

    /**
     * 获取所属系统
     *
     * @return CHANNEL_ID - 所属系统
     */
    public Integer getChannelId() {
        return channelId;
    }

    /**
     * 设置所属系统
     *
     * @param channelId 所属系统
     */
    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    /**
     * 获取删除标记 1：删除，0：未删除
     *
     * @return DEL_FLAG - 删除标记 1：删除，0：未删除
     */
    public String getDelFlag() {
        return delFlag;
    }

    /**
     * 设置删除标记 1：删除，0：未删除
     *
     * @param delFlag 删除标记 1：删除，0：未删除
     */
    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    /**
     * 获取创建者名称
     *
     * @return CRT_USER_NAME - 创建者名称
     */
    public String getCrtUserName() {
        return crtUserName;
    }

    /**
     * 设置创建者名称
     *
     * @param crtUserName 创建者名称
     */
    public void setCrtUserName(String crtUserName) {
        this.crtUserName = crtUserName;
    }

    /**
     * 获取创建者ID
     *
     * @return CRT_USER_ID - 创建者ID
     */
    public String getCrtUserId() {
        return crtUserId;
    }

    /**
     * 设置创建者ID
     *
     * @param crtUserId 创建者ID
     */
    public void setCrtUserId(String crtUserId) {
        this.crtUserId = crtUserId;
    }

    /**
     * 获取创建时间
     *
     * @return CRT_TIME - 创建时间
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建时间
     *
     * @param crtTime 创建时间
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * 获取修改者名称
     *
     * @return UPD_USER_NAME - 修改者名称
     */
    public String getUpdUserName() {
        return updUserName;
    }

    /**
     * 设置修改者名称
     *
     * @param updUserName 修改者名称
     */
    public void setUpdUserName(String updUserName) {
        this.updUserName = updUserName;
    }

    /**
     * 获取修改者ID
     *
     * @return UPD_USER_ID - 修改者ID
     */
    public String getUpdUserId() {
        return updUserId;
    }

    /**
     * 设置修改者ID
     *
     * @param updUserId 修改者ID
     */
    public void setUpdUserId(String updUserId) {
        this.updUserId = updUserId;
    }

    /**
     * 获取修改时间
     *
     * @return UPD_TIME - 修改时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置修改时间
     *
     * @param updTime 修改时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }

    /**
     * 获取部门ID 未使用的字段
     *
     * @return DEPART_ID - 部门ID 未使用的字段
     */
    public String getDepartId() {
        return departId;
    }

    /**
     * 设置部门ID 未使用的字段
     *
     * @param departId 部门ID 未使用的字段
     */
    public void setDepartId(String departId) {
        this.departId = departId;
    }

    /**
     * 获取租户ID
     *
     * @return TENANT_ID - 租户ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    private String isStop;

    public String getIsStop() {
        return isStop;
    }

    public void setIsStop(String isStop) {
        this.isStop = isStop;
    }

    private String channelName;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
