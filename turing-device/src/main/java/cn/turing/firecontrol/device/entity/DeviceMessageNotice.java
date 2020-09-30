package cn.turing.firecontrol.device.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "device_message_notice")
public class DeviceMessageNotice implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "ID")
    private Integer id;

    /**
     * 通知类型[1=报警通知/2=故障通知]
     */
    @ApiModelProperty("通知类型[1=报警通知/2=故障通知]")
    @Column(name = "NOTICE_TYPE")
    private String noticeType;

    /**
     * 通知方式表id
     */
    @ApiModelProperty("通知方式表id")
    @Column(name = "NOTICE_ID")
    private Integer noticeId;

    /**
     * 创建者名称
     */
    @Column(name = "CRT_USER_NAME")
    private String crtUserName;

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

    @Column(name = "UPD_USER_ID")
    private String updUserId;

    /**
     * 修改时间
     */
    @Column(name = "UPD_TIME")
    private Date updTime;

    @Column(name = "DEPART_ID")
    private String departId;

    @Column(name = "TENANT_ID")
    private String tenantId;

    /**
     * 获取主键id
     *
     * @return ID - 主键id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键id
     *
     * @param id 主键id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取通知类型[1=报警通知/2=故障通知]
     *
     * @return NOTICE_TYPE - 通知类型[1=报警通知/2=故障通知]
     */
    public String getNoticeType() {
        return noticeType;
    }

    /**
     * 设置通知类型[1=报警通知/2=故障通知]
     *
     * @param noticeType 通知类型[1=报警通知/2=故障通知]
     */
    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    /**
     * 获取通知方式表id
     *
     * @return NOTICE_ID - 通知方式表id
     */
    public Integer getNoticeId() {
        return noticeId;
    }

    /**
     * 设置通知方式表id
     *
     * @param noticeId 通知方式表id
     */
    public void setNoticeId(Integer noticeId) {
        this.noticeId = noticeId;
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
     * @return CRT_USER_ID
     */
    public String getCrtUserId() {
        return crtUserId;
    }

    /**
     * @param crtUserId
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
     * @return UPD_USER_ID
     */
    public String getUpdUserId() {
        return updUserId;
    }

    /**
     * @param updUserId
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
     * @return DEPART_ID
     */
    public String getDepartId() {
        return departId;
    }

    /**
     * @param departId
     */
    public void setDepartId(String departId) {
        this.departId = departId;
    }

    /**
     * @return TENANT_ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}