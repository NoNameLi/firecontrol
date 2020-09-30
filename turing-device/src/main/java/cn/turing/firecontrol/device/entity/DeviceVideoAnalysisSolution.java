package cn.turing.firecontrol.device.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "device_video_analysis_solution")
public class DeviceVideoAnalysisSolution implements Serializable {
    /**
     * 主键id
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 分析方案名称
     */
    @Column(name = "ANALYSIS_SOLUTION_NAME")
    private String analysisSolutionName;

    /**
     * 分析方案代号
     */
    @Column(name = "ANALYSIS_SOLUTION_CODE")
    private String analysisSolutionCode;

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
     * 更新者ID
     */
    @Column(name = "UPD_USER_ID")
    private String updUserId;

    /**
     * 修改时间
     */
    @Column(name = "UPD_TIME")
    private Date updTime;

    /**
     * 部门ID
     */
    @Column(name = "DEPART_ID")
    private String departId;

    /**
     * 租户ID
     */
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "ANALYSIS_SOLUTION_IMAGE")
    private String analysisSolutionImage;

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
     * 获取分析方案名称
     *
     * @return ANALYSIS_SOLUTION_NAME - 分析方案名称
     */
    public String getAnalysisSolutionName() {
        return analysisSolutionName;
    }

    /**
     * 设置分析方案名称
     *
     * @param analysisSolutionName 分析方案名称
     */
    public void setAnalysisSolutionName(String analysisSolutionName) {
        this.analysisSolutionName = analysisSolutionName;
    }

    /**
     * 获取分析方案代号
     *
     * @return ANALYSIS_SOLUTION_CODE - 分析方案代号
     */
    public String getAnalysisSolutionCode() {
        return analysisSolutionCode;
    }

    /**
     * 设置分析方案代号
     *
     * @param analysisSolutionCode 分析方案代号
     */
    public void setAnalysisSolutionCode(String analysisSolutionCode) {
        this.analysisSolutionCode = analysisSolutionCode;
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
     * 获取更新者ID
     *
     * @return UPD_USER_ID - 更新者ID
     */
    public String getUpdUserId() {
        return updUserId;
    }

    /**
     * 设置更新者ID
     *
     * @param updUserId 更新者ID
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
     * 获取部门ID
     *
     * @return DEPART_ID - 部门ID
     */
    public String getDepartId() {
        return departId;
    }

    /**
     * 设置部门ID
     *
     * @param departId 部门ID
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

    public String getAnalysisSolutionImage() {
        return analysisSolutionImage;
    }

    public void setAnalysisSolutionImage(String analysisSolutionImage) {
        this.analysisSolutionImage = analysisSolutionImage;
    }
}