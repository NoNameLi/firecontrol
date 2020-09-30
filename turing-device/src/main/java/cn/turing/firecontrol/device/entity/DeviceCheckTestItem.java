package cn.turing.firecontrol.device.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "device_check_test_item")
public class DeviceCheckTestItem implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @Id
    @Column(name = "ID")
    private Integer id;

    /**
     * 设施类型id
     */
    @Column(name = "FACILITIES_TYPE_ID")
    private Integer facilitiesTypeId;

    /**
     * 检查项或检测项[0=检查项/1=检测项]
     */
    @Column(name = "ITEM_FLAG")
    private String itemFlag;

    /**
     * 检查检测项
     */
    @Column(name = "CHECK_TEST_ITEM")
    private String checkTestItem;

    /**
     * 单选或输入[0=单选/1=输入]
     */
    @Column(name = "FLAG")
    private String flag;

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
     * 获取设施类型id
     *
     * @return FACILITIES_TYPE_ID - 设施类型id
     */
    public Integer getFacilitiesTypeId() {
        return facilitiesTypeId;
    }

    /**
     * 设置设施类型id
     *
     * @param facilitiesTypeId 设施类型id
     */
    public void setFacilitiesTypeId(Integer facilitiesTypeId) {
        this.facilitiesTypeId = facilitiesTypeId;
    }

    /**
     * 获取检查项或检测项[0=检查项/1=检测项]
     *
     * @return ITEM_FLAG - 检查项或检测项[0=检查项/1=检测项]
     */
    public String getItemFlag() {
        return itemFlag;
    }

    /**
     * 设置检查项或检测项[0=检查项/1=检测项]
     *
     * @param itemFlag 检查项或检测项[0=检查项/1=检测项]
     */
    public void setItemFlag(String itemFlag) {
        this.itemFlag = itemFlag;
    }

    /**
     * 获取检查检测项
     *
     * @return CHECK_TEST_ITEM - 检查检测项
     */
    public String getCheckTestItem() {
        return checkTestItem;
    }

    /**
     * 设置检查检测项
     *
     * @param checkTestItem 检查检测项
     */
    public void setCheckTestItem(String checkTestItem) {
        this.checkTestItem = checkTestItem;
    }

    /**
     * 获取单选或输入[0=单选/1=输入]
     *
     * @return FLAG - 单选或输入[0=单选/1=输入]
     */
    public String getFlag() {
        return flag;
    }

    /**
     * 设置单选或输入[0=单选/1=输入]
     *
     * @param flag 单选或输入[0=单选/1=输入]
     */
    public void setFlag(String flag) {
        this.flag = flag;
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