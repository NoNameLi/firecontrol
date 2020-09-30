package cn.turing.firecontrol.device.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "device_test_plan")
public class DeviceTestPlan implements Serializable {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "ID")
    private Integer id;

    /**
     * 检测路线id
     */
    @Column(name = "TEST_ROUTE_ID")
    private Integer testRouteId;

    /**
     * 开始日期
     */
    @Column(name = "START_TIME")
    private Date startTime;

    /**
     * 结束日期
     */
    @Column(name = "END_TIME")
    private Date endTime;

    /**
     * 巡检周期（单位：天）
     */
    @Column(name = "PATROL_CYCLE")
    private Integer patrolCycle;

    /**
     * 删除标记[1=是/0=否（default）]
     */
    @Column(name = "DEL_FLAG")
    private String delFlag;

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
     * 获取检测路线id
     *
     * @return TEST_ROUTE_ID - 检测路线id
     */
    public Integer getTestRouteId() {
        return testRouteId;
    }

    /**
     * 设置检测路线id
     *
     * @param testRouteId 检测路线id
     */
    public void setTestRouteId(Integer testRouteId) {
        this.testRouteId = testRouteId;
    }

    /**
     * 获取开始日期
     *
     * @return START_TIME - 开始日期
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 设置开始日期
     *
     * @param startTime 开始日期
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取结束日期
     *
     * @return END_TIME - 结束日期
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置结束日期
     *
     * @param endTime 结束日期
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取巡检周期（单位：天）
     *
     * @return PATROL_CYCLE - 巡检周期（单位：天）
     */
    public Integer getPatrolCycle() {
        return patrolCycle;
    }

    /**
     * 设置巡检周期（单位：天）
     *
     * @param patrolCycle 巡检周期（单位：天）
     */
    public void setPatrolCycle(Integer patrolCycle) {
        this.patrolCycle = patrolCycle;
    }

    /**
     * 获取删除标记[1=是/0=否（default）]
     *
     * @return DEL_FLAG - 删除标记[1=是/0=否（default）]
     */
    public String getDelFlag() {
        return delFlag;
    }

    /**
     * 设置删除标记[1=是/0=否（default）]
     *
     * @param delFlag 删除标记[1=是/0=否（default）]
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