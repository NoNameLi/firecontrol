package cn.turing.firecontrol.device.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "device_inspection_tasks")
public class DeviceInspectionTasks implements Serializable {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "ID")
    private Integer id;

    /**
     * 巡检人员id
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * 巡检路线id
     */
    @Column(name = "INSPECTION_ROUTE_ID")
    private Integer inspectionRouteId;

    /**
     * 巡检时间
     */
    @Column(name = "INSPECTION_DATE")
    private Date inspectionDate;

    /**
     * 巡检时段
     */
    @Column(name = "INSPECTION_TIME_PERIOD")
    private String inspectionTimePeriod;

    /**
     * 巡检时长
     */
    @Column(name = "PATROL_CYCLE")
    private Integer patrolCycle;

    /**
     * 状态[0=未接取/1=已接取/2=进行中/3=完成]
     */
    @Column(name = "STATUS")
    private String status;

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
     * 获取巡检人员id
     *
     * @return USER_ID - 巡检人员id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置巡检人员id
     *
     * @param userId 巡检人员id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取巡检路线id
     *
     * @return INSPECTION_ROUTE_ID - 巡检路线id
     */
    public Integer getInspectionRouteId() {
        return inspectionRouteId;
    }

    /**
     * 设置巡检路线id
     *
     * @param inspectionRouteId 巡检路线id
     */
    public void setInspectionRouteId(Integer inspectionRouteId) {
        this.inspectionRouteId = inspectionRouteId;
    }

    /**
     * 获取巡检时间
     *
     * @return INSPECTION_DATE - 巡检时间
     */
    public Date getInspectionDate() {
        return inspectionDate;
    }

    /**
     * 设置巡检时间
     *
     * @param inspectionDate 巡检时间
     */
    public void setInspectionDate(Date inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    /**
     * 获取巡检时段
     *
     * @return INSPECTION_TIME_PERIOD - 巡检时段
     */
    public String getInspectionTimePeriod() {
        return inspectionTimePeriod;
    }

    /**
     * 设置巡检时段
     *
     * @param inspectionTimePeriod 巡检时段
     */
    public void setInspectionTimePeriod(String inspectionTimePeriod) {
        this.inspectionTimePeriod = inspectionTimePeriod;
    }

    /**
     * 获取巡检时长
     *
     * @return PATROL_CYCLE - 巡检时长
     */
    public Integer getPatrolCycle() {
        return patrolCycle;
    }

    /**
     * 设置巡检时长
     *
     * @param patrolCycle 巡检时长
     */
    public void setPatrolCycle(Integer patrolCycle) {
        this.patrolCycle = patrolCycle;
    }

    /**
     * 获取状态[0=未接取/1=已接取/2=进行中/3=完成]
     *
     * @return STATUS - 状态[0=未接取/1=已接取/2=进行中/3=完成]
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态[0=未接取/1=已接取/2=进行中/3=完成]
     *
     * @param status 状态[0=未接取/1=已接取/2=进行中/3=完成]
     */
    public void setStatus(String status) {
        this.status = status;
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