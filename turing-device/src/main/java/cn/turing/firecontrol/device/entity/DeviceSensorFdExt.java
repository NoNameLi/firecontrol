package cn.turing.firecontrol.device.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "device_sensor_fd_ext")
public class DeviceSensorFdExt {
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 防火门编号
     */
    @Column(name = "FIRE_DOOR_ID")
    private Long fireDoorId;

    /**
     * 传感器表示的门状态0:非门磁传感器　1:开门状态　2:关闭状态
     */
    @Column(name = "DOOR_STATUS")
    private String doorStatus;

    /**
     * 逻辑删除状态。1表示已删除 0表示正常
     */
    @Column(name = "DEL_FLAG")
    private String delFlag;

    /**
     * 创建人名称
     */
    @Column(name = "CRT_USER_NAME")
    private String crtUserName;

    /**
     * 创建人用户ID 
     */
    @Column(name = "CRT_USER_ID")
    private String crtUserId;

    @Column(name = "CRT_TIME")
    private Date crtTime;

    /**
     * 更新人姓名
     */
    @Column(name = "UPD_USER_NAME")
    private String updUserName;

    /**
     * 更新人用户ID
     */
    @Column(name = "UPD_USER_ID")
    private String updUserId;

    /**
     * 更新时间
     */
    @Column(name = "UPD_TIME")
    private Date updTime;

    @Column(name = "DEPART_ID")
    private String departId;

    /**
     * 租户ID
     */
    @Column(name = "TENANT_ID")
    private String tenantId;

    /**
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取防火门编号
     *
     * @return FIRE_DOOR_ID - 防火门编号
     */
    public Long getFireDoorId() {
        return fireDoorId;
    }

    /**
     * 设置防火门编号
     *
     * @param fireDoorId 防火门编号
     */
    public void setFireDoorId(Long fireDoorId) {
        this.fireDoorId = fireDoorId;
    }

    /**
     * 获取传感器表示的门状态0:非门磁传感器　1:开门状态　2:关闭状态
     *
     * @return DOOR_STATUS - 传感器表示的门状态0:非门磁传感器　1:开门状态　2:关闭状态
     */
    public String getDoorStatus() {
        return doorStatus;
    }

    /**
     * 设置传感器表示的门状态0:非门磁传感器　1:开门状态　2:关闭状态
     *
     * @param doorStatus 传感器表示的门状态0:非门磁传感器　1:开门状态　2:关闭状态
     */
    public void setDoorStatus(String doorStatus) {
        this.doorStatus = doorStatus;
    }

    /**
     * 获取逻辑删除状态。1表示已删除 0表示正常
     *
     * @return DEL_FLAG - 逻辑删除状态。1表示已删除 0表示正常
     */
    public String getDelFlag() {
        return delFlag;
    }

    /**
     * 设置逻辑删除状态。1表示已删除 0表示正常
     *
     * @param delFlag 逻辑删除状态。1表示已删除 0表示正常
     */
    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    /**
     * 获取创建人名称
     *
     * @return CRT_USER_NAME - 创建人名称
     */
    public String getCrtUserName() {
        return crtUserName;
    }

    /**
     * 设置创建人名称
     *
     * @param crtUserName 创建人名称
     */
    public void setCrtUserName(String crtUserName) {
        this.crtUserName = crtUserName;
    }

    /**
     * 获取创建人用户ID 
     *
     * @return CRT_USER_ID - 创建人用户ID 
     */
    public String getCrtUserId() {
        return crtUserId;
    }

    /**
     * 设置创建人用户ID 
     *
     * @param crtUserId 创建人用户ID 
     */
    public void setCrtUserId(String crtUserId) {
        this.crtUserId = crtUserId;
    }

    /**
     * @return CRT_TIME
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * @param crtTime
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * 获取更新人姓名
     *
     * @return UPD_USER_NAME - 更新人姓名
     */
    public String getUpdUserName() {
        return updUserName;
    }

    /**
     * 设置更新人姓名
     *
     * @param updUserName 更新人姓名
     */
    public void setUpdUserName(String updUserName) {
        this.updUserName = updUserName;
    }

    /**
     * 获取更新人用户ID
     *
     * @return UPD_USER_ID - 更新人用户ID
     */
    public String getUpdUserId() {
        return updUserId;
    }

    /**
     * 设置更新人用户ID
     *
     * @param updUserId 更新人用户ID
     */
    public void setUpdUserId(String updUserId) {
        this.updUserId = updUserId;
    }

    /**
     * 获取更新时间
     *
     * @return UPD_TIME - 更新时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置更新时间
     *
     * @param updTime 更新时间
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
}