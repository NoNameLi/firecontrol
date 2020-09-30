package cn.turing.firecontrol.datahandler.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "notice_rule_sensor")
public class NoticeRuleSensor {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 通知规则ID
     */
    @Column(name = "NOTICE_RULE_ID")
    private Long noticeRuleId;

    /**
     * 设备ID 设备的ID
     */
    @Column(name = "SENSOR_ID")
    private Long sensorId;

    /**
     * 栏目ID
     */
    @Column(name = "CHANNEL_ID")
    private Long channelId;

    /**
     * 删除标记 1：删除，0：未删除
     */
    @Column(name = "DEL_FLAG")
    private String delFlag;

    /**
     * 创建者名称
     */
    @Column(name = "CRT_USER_NAME")
    private String crtUserName;

    /**
     * 创建者ID
     */
    @Column(name = "CRT_USER_ID")
    private String crtUserId;

    /**
     * 创建时间
     */
    @Column(name = "CRT_TIME")
    private Date crtTime;

    /**
     * 修改者名称
     */
    @Column(name = "UPD_USER_NAME")
    private String updUserName;

    /**
     * 修改者ID
     */
    @Column(name = "UPD_USER_ID")
    private String updUserId;

    /**
     * 修改时间
     */
    @Column(name = "UPD_TIME")
    private Date updTime;

    /**
     * 部门ID 未使用的字段
     */
    @Column(name = "DEPART_ID")
    private String departId;

    /**
     * 租户ID
     */
    @Column(name = "TENANT_ID")
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
     * 获取通知规则ID
     *
     * @return NOTICE_RULE_ID - 通知规则ID
     */
    public Long getNoticeRuleId() {
        return noticeRuleId;
    }

    /**
     * 设置通知规则ID
     *
     * @param noticeRuleId 通知规则ID
     */
    public void setNoticeRuleId(Long noticeRuleId) {
        this.noticeRuleId = noticeRuleId;
    }

    /**
     * 获取设备ID 设备的ID
     *
     * @return SENSOR_ID - 设备ID 设备的ID
     */
    public Long getSensorId() {
        return sensorId;
    }

    /**
     * 设置设备ID 设备的ID
     *
     * @param sensorId 设备ID 设备的ID
     */
    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
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

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}