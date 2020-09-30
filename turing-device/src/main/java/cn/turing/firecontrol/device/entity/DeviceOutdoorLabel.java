package cn.turing.firecontrol.device.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "device_outdoor_label")
public class DeviceOutdoorLabel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @GeneratedValue(generator = "JDBC")
    @Id
    @Column(name = "ID")
    private Integer id;

    /**
     * 设施类型id
     */
    @Column(name = "FACILITIES_TYPE_ID")
    private Integer facilitiesTypeId;

    /**
     * 联网单位ID
     */
    @Column(name = "OID")
    private Integer oid;

    /**
     * 设施编号
     */
    @Column(name = "FACILITIES_NO")
    private String facilitiesNo;

    /**
     * 状态[0=正常/1=维修中]
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 位置描述
     */
    @Column(name = "POSITION_DESCRIPTION")
    private String positionDescription;

    /**
     * 最近巡检日期
     */
    @Column(name = "LAST_INSPECTION_TIME")
    private Date lastInspectionTime;

    /**
     * 二维码路径
     */
    @Column(name = "QR_CODE_PATH")
    private String qrCodePath;

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
     * 位置标记（中间用,号隔开）
     */
    @Column(name = "POSITION_SIGN")
    private String positionSign;

    /**
     * 是否生成巡检路线[1=是/0=否]
     */
    @Column(name = "USE_FLAG")
    private String useFlag;

    /**
     * 是否生成维保路线[1=是/0=否]
     */
    @Column(name = "USE_TEST_FLAG")
    private String useTestFlag;

    /**
     * 检测结果[1=正常/0=未检测/2=异常3=跳过]
     */
    @Column(name = "RESULT_FLAG")
    private String resultFlag;

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
     * 获取联网单位ID
     *
     * @return OID - 联网单位ID
     */
    public Integer getOid() {
        return oid;
    }

    /**
     * 设置联网单位ID
     *
     * @param oid 联网单位ID
     */
    public void setOid(Integer oid) {
        this.oid = oid;
    }

    /**
     * 获取设施编号
     *
     * @return FACILITIES_NO - 设施编号
     */
    public String getFacilitiesNo() {
        return facilitiesNo;
    }

    /**
     * 设置设施编号
     *
     * @param facilitiesNo 设施编号
     */
    public void setFacilitiesNo(String facilitiesNo) {
        this.facilitiesNo = facilitiesNo;
    }

    /**
     * 获取状态[0=正常/1=维修中]
     *
     * @return STATUS - 状态[0=正常/1=维修中]
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态[0=正常/1=维修中]
     *
     * @param status 状态[0=正常/1=维修中]
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取位置描述
     *
     * @return POSITION_DESCRIPTION - 位置描述
     */
    public String getPositionDescription() {
        return positionDescription;
    }

    /**
     * 设置位置描述
     *
     * @param positionDescription 位置描述
     */
    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    /**
     * 获取最近巡检日期
     *
     * @return LAST_INSPECTION_TIME - 最近巡检日期
     */
    public Date getLastInspectionTime() {
        return lastInspectionTime;
    }

    /**
     * 设置最近巡检日期
     *
     * @param lastInspectionTime 最近巡检日期
     */
    public void setLastInspectionTime(Date lastInspectionTime) {
        this.lastInspectionTime = lastInspectionTime;
    }

    /**
     * 获取二维码路径
     *
     * @return QR_CODE_PATH - 二维码路径
     */
    public String getQrCodePath() {
        return qrCodePath;
    }

    /**
     * 设置二维码路径
     *
     * @param qrCodePath 二维码路径
     */
    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
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

    public String getPositionSign() {
        return positionSign;
    }

    public void setPositionSign(String positionSign) {
        this.positionSign = positionSign;
    }

    public String getUseFlag() {
        return useFlag;
    }

    public void setUseFlag(String useFlag) {
        this.useFlag = useFlag;
    }

    public String getResultFlag() {
        return resultFlag;
    }

    public void setResultFlag(String resultFlag) {
        this.resultFlag = resultFlag;
    }

    public String getUseTestFlag() {
        return useTestFlag;
    }

    public void setUseTestFlag(String useTestFlag) {
        this.useTestFlag = useTestFlag;
    }
}