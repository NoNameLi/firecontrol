package cn.turing.firecontrol.datahandler.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "notice_log")
public class NoticeLog {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "JDBC")
    private Long id;


    /**
     * 传感器编号
     */
    @Column(name = "SENSOR_NO")
    private String sensorNo;

    /**
     * 通知方式
     */
    @Column(name = "NOTICE_TYPE")
    private String noticeType;

    /**
     * 推送内容
     */
    @Column(name = "NOTICE_CONTENT")
    private String noticeContent;

    /**
     * 所属系统 关联turing_admin的channel表
     */
    @Column(name = "CHANNEL_ID")
    private Integer channelId;

    /**
     * 接收账号
     */
    @Column(name = "USERNAME")
    private String username;

    /**
     * 接收手机号
     */
    @Column(name = "MOBILE_PHONE")
    private String mobilePhone;

    /**
     * 服务方
     */
    @Column(name = "SERVICE_SUPPLY_NAME")
    private String serviceSupplyName;

    /**
     * 通知结果
     */
    @Column(name = "NOTICE_RESULT")
    private String noticeResult;

    /**
     * 报警时间
     */
    @Column(name = "ALARM_TIME")
    private Date alarmTime;

    /**
     * 通知时间
     */
    @Column(name = "NOTICE_TIME")
    private Date noticeTime;

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
     * 租户ID
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * 租户ID
     */
    @Column(name = "SENSOR_ID")
    private Long sensorId;

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
     * 获取推送内容
     *
     * @return NOTICE_CONTENT - 推送内容
     */
    public String getNoticeContent() {
        return noticeContent;
    }

    /**
     * 设置推送内容
     *
     * @param noticeContent 推送内容
     */
    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    /**
     * 获取所属系统 关联turing_admin的channel表
     *
     * @return CHANNEL_ID - 所属系统 关联turing_admin的channel表
     */
    public Integer getChannelId() {
        return channelId;
    }

    /**
     * 设置所属系统 关联turing_admin的channel表
     *
     * @param channelId 所属系统 关联turing_admin的channel表
     */
    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    /**
     * 获取接收账号
     *
     * @return USERNAME - 接收账号
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置接收账号
     *
     * @param username 接收账号
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取接收手机号
     *
     * @return MOBILE_PHONE - 接收手机号
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * 设置接收手机号
     *
     * @param mobilePhone 接收手机号
     */
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    /**
     * 获取服务方
     *
     * @return SERVICE_SUPPLY_NAME - 服务方
     */
    public String getServiceSupplyName() {
        return serviceSupplyName;
    }

    /**
     * 设置服务方
     *
     * @param serviceSupplyName 服务方
     */
    public void setServiceSupplyName(String serviceSupplyName) {
        this.serviceSupplyName = serviceSupplyName;
    }

    /**
     * 获取通知结果
     *
     * @return NOTICE_RESULT - 通知结果
     */
    public String getNoticeResult() {
        return noticeResult;
    }

    /**
     * 设置通知结果
     *
     * @param noticeResult 通知结果
     */
    public void setNoticeResult(String noticeResult) {
        this.noticeResult = noticeResult;
    }

    /**
     * 获取报警时间
     *
     * @return ALARM_TIME - 报警时间
     */
    public Date getAlarmTime() {
        return alarmTime;
    }

    /**
     * 设置报警时间
     *
     * @param alarmTime 报警时间
     */
    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    /**
     * 获取通知时间
     *
     * @return NOTICE_TIME - 通知时间
     */
    public Date getNoticeTime() {
        return noticeTime;
    }

    /**
     * 设置通知时间
     *
     * @param noticeTime 通知时间
     */
    public void setNoticeTime(Date noticeTime) {
        this.noticeTime = noticeTime;
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

    public String getSensorNo() {
        return sensorNo;
    }

    public void setSensorNo(String sensorNo) {
        this.sensorNo = sensorNo;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSensorId() {
        return sensorId;
    }

    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
    }

    @Transient
    private String channelName;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}