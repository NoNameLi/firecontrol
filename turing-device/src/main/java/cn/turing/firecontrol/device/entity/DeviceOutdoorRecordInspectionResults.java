package cn.turing.firecontrol.device.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "device_outdoor_record_inspection_results")
public class DeviceOutdoorRecordInspectionResults implements Serializable {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "ID")
    private Integer id;

    /**
     * 上传图片ids
     */
    @ApiModelProperty("上传图片ids")
    @Column(name = "UPLOAD_PICTURE_IDS")
    private String uploadPictureIds;

    /**
     * 标签id
     */
    @ApiModelProperty("标签id")
    @Column(name = "LABEL_ID")
    private Integer labelId;

    /**
     * 是否漏检[1=是/0=否]
     */
    @ApiModelProperty("是否漏检[1=是/0=否]")
    @Column(name = "LEAK_FLAG")
    private String leakFlag;

    /**
     * 设施编号
     */
    @ApiModelProperty("设施编号")
    @Column(name = "FACILITIES_NO")
    private String facilitiesNo;

    /**
     * 设施类型
     */
    @ApiModelProperty("设施类型")
    @Column(name = "EQUIPMENT_TYPE")
    private String equipmentType;

    /**
     * 设施状态变更
     */
    @ApiModelProperty("设施状态变更")
    @Column(name = "EQUIPMENT_STATUS")
    private String equipmentStatus;

    /**
     * 位置描述
     */
    @ApiModelProperty("位置描述")
    @Column(name = "POSITION_DESCRIPTION")
    private String positionDescription;

    /**
     * 巡检结果[0=正常/1=异常]
     */
    @ApiModelProperty("巡检结果[0=正常/1=异常]")
    @Column(name = "INSPECTION_RESULT")
    private String inspectionResult;

    /**
     * 问题描述
     */
    @ApiModelProperty("问题描述")
    @Column(name = "PROBLEM_DESCRIPTION")
    private String problemDescription;

    /**
     * 处理方式[0=已自行处理/1=上报维修]
     */
    @ApiModelProperty("处理方式[0=已自行处理/1=上报维修]")
    @Column(name = "HANDLING")
    private String handling;

    /**
     * 巡检人
     */
    @ApiModelProperty("巡检人")
    @Column(name = "INSPECTION_PERSON")
    private String inspectionPerson;

    /**
     * 巡检时间
     */
    @ApiModelProperty("巡检时间")
    @Column(name = "INSPECTION_DATE")
    private Date inspectionDate;

    /**
     * 联系电话
     */
    @ApiModelProperty("联系电话")
    @Column(name = "MOBILE_PHONE")
    private String mobilePhone;

    /**
     * 计划完成时间
     */
    @ApiModelProperty("计划完成时间")
    @Column(name = "PLANNED_COMPLETION_TIME")
    private Date plannedCompletionTime;

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
     * 任务id
     */
    @ApiModelProperty("任务id")
    @Column(name = "TASK_ID")
    private Integer taskId;

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
     * 获取上传图片ids
     *
     * @return UPLOAD_PICTURE_IDS - 上传图片ids
     */
    public String getUploadPictureIds() {
        return uploadPictureIds;
    }

    /**
     * 设置上传图片ids
     *
     * @param uploadPictureIds 上传图片ids
     */
    public void setUploadPictureIds(String uploadPictureIds) {
        this.uploadPictureIds = uploadPictureIds;
    }

    /**
     * 获取标签id
     *
     * @return LABEL_ID - 标签id
     */
    public Integer getLabelId() {
        return labelId;
    }

    /**
     * 设置标签id
     *
     * @param labelId 标签id
     */
    public void setLabelId(Integer labelId) {
        this.labelId = labelId;
    }

    /**
     * 获取是否漏检[1=是/0=否]
     *
     * @return LEAK_FLAG - 是否漏检[1=是/0=否]
     */
    public String getLeakFlag() {
        return leakFlag;
    }

    /**
     * 设置是否漏检[1=是/0=否]
     *
     * @param leakFlag 是否漏检[1=是/0=否]
     */
    public void setLeakFlag(String leakFlag) {
        this.leakFlag = leakFlag;
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
     * 获取设施类型
     *
     * @return EQUIPMENT_TYPE - 设施类型
     */
    public String getEquipmentType() {
        return equipmentType;
    }

    /**
     * 设置设施类型
     *
     * @param equipmentType 设施类型
     */
    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    /**
     * 获取设施状态变更
     *
     * @return EQUIPMENT_STATUS - 设施状态变更
     */
    public String getEquipmentStatus() {
        return equipmentStatus;
    }

    /**
     * 设置设施状态变更
     *
     * @param equipmentStatus 设施状态变更
     */
    public void setEquipmentStatus(String equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
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
     * 获取巡检结果[0=正常/1=异常]
     *
     * @return INSPECTION_RESULT - 巡检结果[0=正常/1=异常]
     */
    public String getInspectionResult() {
        return inspectionResult;
    }

    /**
     * 设置巡检结果[0=正常/1=异常]
     *
     * @param inspectionResult 巡检结果[0=正常/1=异常]
     */
    public void setInspectionResult(String inspectionResult) {
        this.inspectionResult = inspectionResult;
    }

    /**
     * 获取问题描述
     *
     * @return PROBLEM_DESCRIPTION - 问题描述
     */
    public String getProblemDescription() {
        return problemDescription;
    }

    /**
     * 设置问题描述
     *
     * @param problemDescription 问题描述
     */
    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    /**
     * 获取处理方式[0=已自行处理/1=上报维修]
     *
     * @return HANDLING - 处理方式[0=已自行处理/1=上报维修]
     */
    public String getHandling() {
        return handling;
    }

    /**
     * 设置处理方式[0=已自行处理/1=上报维修]
     *
     * @param handling 处理方式[0=已自行处理/1=上报维修]
     */
    public void setHandling(String handling) {
        this.handling = handling;
    }

    /**
     * 获取巡检人
     *
     * @return INSPECTION_PERSON - 巡检人
     */
    public String getInspectionPerson() {
        return inspectionPerson;
    }

    /**
     * 设置巡检人
     *
     * @param inspectionPerson 巡检人
     */
    public void setInspectionPerson(String inspectionPerson) {
        this.inspectionPerson = inspectionPerson;
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
     * 获取联系电话
     *
     * @return MOBILE_PHONE - 联系电话
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * 设置联系电话
     *
     * @param mobilePhone 联系电话
     */
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    /**
     * 获取计划完成时间
     *
     * @return PLANNED_COMPLETION_TIME - 计划完成时间
     */
    public Date getPlannedCompletionTime() {
        return plannedCompletionTime;
    }

    /**
     * 设置计划完成时间
     *
     * @param plannedCompletionTime 计划完成时间
     */
    public void setPlannedCompletionTime(Date plannedCompletionTime) {
        this.plannedCompletionTime = plannedCompletionTime;
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

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}