package cn.turing.firecontrol.datahandler.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "device_facilities_abnormal")
public class DeviceFacilitiesAbnormal implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @GeneratedValue(generator = "JDBC")
    @Id
    @Column(name = "ID")
    private Integer id;

    /**
     * 消火栓id
     */
    @Column(name = "FIRE_COCK_ID")
    private Integer fireCockId;

    /**
     * 设备id
     */
    @Column(name = "EQU_ID")
    private Integer equId;

    /**
     * 报警类型
     */
    @Column(name = "ALRM_TYPE")
    private String alrmType;

    /**
     * 报警时间
     */
    @Column(name = "ALRM_DATE")
    private Date alrmDate;

    /**
     * 是否处理[1=是/0=否]
     */
    @Column(name = "HANDLE_FLAG")
    private String handleFlag;

    /**
     * 是否故障[1=误报/0=故障]
     */
    @Column(name = "FAULT_FLAG")
    private String faultFlag;

    /**
     * 确认时间
     */
    @Column(name = "CONFIR_DATE")
    private Date confirDate;

    /**
     * 处理时间
     */
    @Column(name = "HANDLE_DATE")
    private Date handleDate;

    //恢复时间
    @Column(name = "RESTORE_DATE")
    private Date restoreDate;

    /**
     * 确认人
     */
    @Column(name = "CONFIR_PERSON")
    private String confirPerson;

    /**
     * 处理人
     */
    @Column(name = "HANDLE_PERSON")
    private String handlePerson;

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
     * 传感器数据
     */
    @Column(name = "ALRM_DATA")
    private Double alrmData;

    /**
     * 日志ID
     */
    @Column(name = "LOG_ID")
    private String logId;

    /**
     * 测点代号
     */
    @Column(name = "UNIT")
    private String unit;

    /**
     * 数据单位
     */
    @Column(name = "DATA_UNIT")
    private String dataUnit;

    /**
     * 消火栓名称
     */
    @Column(name = "HYDRANT_NAME")
    private String hydrantName;

    /**
     * 传感器编号
     */
    @Column(name = "SENSOR_NO")
    private String sensorNo;

    /**
     * 设备类型
     */
    @Column(name = "EQUIPMENT_TYPE")
    private String equipmentType;

    /**
     * 位置描述
     */
    @Column(name = "POSITION_DESCRIPTION")
    private String positionDescription;

    /**
     * 测点
     */
    @Column(name = "MEASURING_POINT")
    private String measuringPoint;

    /**
     * 报警等级
     */
    @Column(name = "LEVEL")
    private String level;

    /**
     * 所属系统id
     */
    @Column(name = "CHANNEL_ID")
    private Integer channelId;

    /**
     * 获取主键ID
     *
     * @return ID - 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取消火栓id
     *
     * @return FIRE_COCK_ID - 消火栓id
     */
    public Integer getFireCockId() {
        return fireCockId;
    }

    /**
     * 设置消火栓id
     *
     * @param fireCockId 消火栓id
     */
    public void setFireCockId(Integer fireCockId) {
        this.fireCockId = fireCockId;
    }

    /**
     * 获取设备id
     *
     * @return EQU_ID - 设备id
     */
    public Integer getEquId() {
        return equId;
    }

    /**
     * 设置设备id
     *
     * @param equId 设备id
     */
    public void setEquId(Integer equId) {
        this.equId = equId;
    }

    /**
     * 获取报警类型
     *
     * @return ALRM_TYPE - 报警类型
     */
    public String getAlrmType() {
        return alrmType;
    }

    /**
     * 设置报警类型
     *
     * @param alrmType 报警类型
     */
    public void setAlrmType(String alrmType) {
        this.alrmType = alrmType;
    }

    /**
     * 获取报警时间
     *
     * @return ALRM_DATE - 报警时间
     */
    public Date getAlrmDate() {
        return alrmDate;
    }

    /**
     * 设置报警时间
     *
     * @param alrmDate 报警时间
     */
    public void setAlrmDate(Date alrmDate) {
        this.alrmDate = alrmDate;
    }

    /**
     * 获取是否处理[1=是/0=否]
     *
     * @return HANDLE_FLAG - 是否处理[1=是/0=否]
     */
    public String getHandleFlag() {
        return handleFlag;
    }

    /**
     * 设置是否处理[1=是/0=否]
     *
     * @param handleFlag 是否处理[1=是/0=否]
     */
    public void setHandleFlag(String handleFlag) {
        this.handleFlag = handleFlag;
    }

    /**
     * 获取是否故障[1=误报/0=故障]
     *
     * @return FAULT_FLAG - 是否故障[1=误报/0=故障]
     */
    public String getFaultFlag() {
        return faultFlag;
    }

    /**
     * 设置是否故障[1=误报/0=故障]
     *
     * @param faultFlag 是否故障[1=误报/0=故障]
     */
    public void setFaultFlag(String faultFlag) {
        this.faultFlag = faultFlag;
    }

    /**
     * 获取确认时间
     *
     * @return CONFIR_DATE - 确认时间
     */
    public Date getConfirDate() {
        return confirDate;
    }

    /**
     * 设置确认时间
     *
     * @param confirDate 确认时间
     */
    public void setConfirDate(Date confirDate) {
        this.confirDate = confirDate;
    }

    /**
     * 获取处理时间
     *
     * @return HANDLE_DATE - 处理时间
     */
    public Date getHandleDate() {
        return handleDate;
    }

    /**
     * 设置处理时间
     *
     * @param handleDate 处理时间
     */
    public void setHandleDate(Date handleDate) {
        this.handleDate = handleDate;
    }

    /**
     * 获取确认人
     *
     * @return CONFIR_PERSON - 确认人
     */
    public String getConfirPerson() {
        return confirPerson;
    }

    /**
     * 设置确认人
     *
     * @param confirPerson 确认人
     */
    public void setConfirPerson(String confirPerson) {
        this.confirPerson = confirPerson;
    }

    /**
     * 获取处理人
     *
     * @return HANDLE_PERSON - 处理人
     */
    public String getHandlePerson() {
        return handlePerson;
    }

    /**
     * 设置处理人
     *
     * @param handlePerson 处理人
     */
    public void setHandlePerson(String handlePerson) {
        this.handlePerson = handlePerson;
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

    /**
     * 获取传感器数据
     *
     * @return ALRM_DATA - 传感器数据
     */
    public Double getAlrmData() {
        return alrmData;
    }

    /**
     * 设置传感器数据
     *
     * @param alrmData 传感器数据
     */
    public void setAlrmData(Double alrmData) {
        this.alrmData = alrmData;
    }

    /**
     * 获取日志ID
     *
     * @return LOG_ID - 日志ID
     */
    public String getLogId() {
        return logId;
    }

    /**
     * 设置日志ID
     *
     * @param logId 日志ID
     */
    public void setLogId(String logId) {
        this.logId = logId;
    }

    /**
     * 获取测点代号
     *
     * @return UNIT - 测点代号
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置测点代号
     *
     * @param unit 测点代号
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 获取数据单位
     *
     * @return DATA_UNIT - 数据单位
     */
    public String getDataUnit() {
        return dataUnit;
    }

    /**
     * 设置数据单位
     *
     * @param dataUnit 数据单位
     */
    public void setDataUnit(String dataUnit) {
        this.dataUnit = dataUnit;
    }

    /**
     * 获取消火栓名称
     *
     * @return HYDRANT_NAME - 消火栓名称
     */
    public String getHydrantName() {
        return hydrantName;
    }

    /**
     * 设置消火栓名称
     *
     * @param hydrantName 消火栓名称
     */
    public void setHydrantName(String hydrantName) {
        this.hydrantName = hydrantName;
    }

    /**
     * 获取传感器编号
     *
     * @return SENSOR_NO - 传感器编号
     */
    public String getSensorNo() {
        return sensorNo;
    }

    /**
     * 设置传感器编号
     *
     * @param sensorNo 传感器编号
     */
    public void setSensorNo(String sensorNo) {
        this.sensorNo = sensorNo;
    }

    /**
     * 获取设备类型
     *
     * @return EQUIPMENT_TYPE - 设备类型
     */
    public String getEquipmentType() {
        return equipmentType;
    }

    /**
     * 设置设备类型
     *
     * @param equipmentType 设备类型
     */
    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
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
     * 获取测点
     *
     * @return MEASURING_POINT - 测点
     */
    public String getMeasuringPoint() {
        return measuringPoint;
    }

    /**
     * 设置测点
     *
     * @param measuringPoint 测点
     */
    public void setMeasuringPoint(String measuringPoint) {
        this.measuringPoint = measuringPoint;
    }

    /**
     * 获取报警等级
     *
     * @return LEVEL - 报警等级
     */
    public String getLevel() {
        return level;
    }

    /**
     * 设置报警等级
     *
     * @param level 报警等级
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * 获取所属系统id
     *
     * @return CHANNEL_ID - 所属系统id
     */
    public Integer getChannelId() {
        return channelId;
    }

    /**
     * 设置所属系统id
     *
     * @param channelId 所属系统id
     */
    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Date getRestoreDate() {
        return restoreDate;
    }

    public void setRestoreDate(Date restoreDate) {
        this.restoreDate = restoreDate;
    }
}