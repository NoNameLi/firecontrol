package cn.turing.firecontrol.device.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "device_item_value")
public class DeviceItemValue implements Serializable {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "ID")
    private Integer id;

    /**
     * 检查项id
     */
    @Column(name = "ITEM_ID")
    private Integer itemId;

    /**
     * 巡检结果记录id
     */
    @Column(name = "RESULTS_ID")
    private Integer resultsId;

    /**
     * 0=室内设施巡检,1=室外设施巡检
     */
    @Column(name = "INSPECTION_FLAG")
    private String inspectionFlag;

    /**
     * 检查值
     */
    @Column(name = "ITEM_VAULE")
    private String itemVaule;

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
     * 获取检查项id
     *
     * @return ITEM_ID - 检查项id
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 设置检查项id
     *
     * @param itemId 检查项id
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 获取巡检结果记录id
     *
     * @return RESULTS_ID - 巡检结果记录id
     */
    public Integer getResultsId() {
        return resultsId;
    }

    /**
     * 设置巡检结果记录id
     *
     * @param resultsId 巡检结果记录id
     */
    public void setResultsId(Integer resultsId) {
        this.resultsId = resultsId;
    }

    /**
     * 获取0=室内设施巡检,1=室外设施巡检
     *
     * @return INSPECTION_FLAG - 0=室内设施巡检,1=室外设施巡检
     */
    public String getInspectionFlag() {
        return inspectionFlag;
    }

    /**
     * 设置0=室内设施巡检,1=室外设施巡检
     *
     * @param inspectionFlag 0=室内设施巡检,1=室外设施巡检
     */
    public void setInspectionFlag(String inspectionFlag) {
        this.inspectionFlag = inspectionFlag;
    }

    /**
     * 获取检查值
     *
     * @return ITEM_VAULE - 检查值
     */
    public String getItemVaule() {
        return itemVaule;
    }

    /**
     * 设置检查值
     *
     * @param itemVaule 检查值
     */
    public void setItemVaule(String itemVaule) {
        this.itemVaule = itemVaule;
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