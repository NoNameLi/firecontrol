package cn.turing.firecontrol.device.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "device_fire_door")
public class DeviceFireDoor{
    @Id
    @Column(name = "ID")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 所属建筑物ID
     */
    @Column(name = "BUILDING_ID")
    protected Integer buildingId;

    /**
     * 防火门名称
     */
    @Column(name = "DOOR_NAME")
    protected String doorName;

    /**
     * 防火门类型：１常开门２常闭门
     */
    @Column(name = "DOOR_NORMAL_STATUS")
    protected String doorNormalStatus;

    /**
     * 所属楼层
     */
    @Column(name = "FLOOR")
    protected Integer floor;

    /**
     * 防火门位置描述
     */
    @Column(name = "POSITION_DESCRIPTION")
    protected String positionDescription;

    /**
     * 在楼层平面图中的坐标，以逗号分隔
     */
    @Column(name = "POSITION_SIGN")
    protected String positionSign;

    /**
     * 传感器数量
     */
    @Column(name = "SENSOR_NUM")
    protected Integer sensorNum;

    /**
     * JSON:[{'deviceId':1,'status':1},{'deviceId':1,'status':1}]
     */
    @Column(name = "DOOR_STATUS")
    protected String doorStatus;

    /**
     * 逻辑删除标识1表示已删除 0表示正常
     */
    @Column(name = "DEL_FLAG")
    protected String delFlag;

    /**
     * 创建人名称
     */
    @Column(name = "CRT_USER_NAME")
    protected String crtUserName;

    /**
     * 创建人用户ID 
     */
    @Column(name = "CRT_USER_ID")
    protected String crtUserId;

    /**
     * 创建时间
     */
    @Column(name = "CRT_TIME")
    protected Date crtTime;

    /**
     * 更新人姓名
     */
    @Column(name = "UPD_USER_NAME")
    protected String updUserName;

    /**
     * 更新人用户ID
     */
    @Column(name = "UPD_USER_ID")
    protected String updUserId;

    /**
     * 更新时间
     */
    @Column(name = "UPD_TIME")
    protected Date updTime;

    /**
     * 保留字段
     */
    @Column(name = "DEPART_ID")
    protected String departId;

    /**
     * 租户ID
     */
    @Column(name = "TENANT_ID")
    protected String tenantId;

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
     * 获取所属建筑物ID
     *
     * @return BUILDING_ID - 所属建筑物ID
     */
    public Integer getBuildingId() {
        return buildingId;
    }

    /**
     * 设置所属建筑物ID
     *
     * @param buildingId 所属建筑物ID
     */
    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    /**
     * 获取防火门名称
     *
     * @return DOOR_NAME - 防火门名称
     */
    public String getDoorName() {
        return doorName;
    }

    /**
     * 设置防火门名称
     *
     * @param doorName 防火门名称
     */
    public void setDoorName(String doorName) {
        this.doorName = doorName;
    }

    /**
     * 获取防火门类型：１常开门２常闭门
     *
     * @return DOOR_NORMAL_STATUS - 防火门类型：１常开门２常闭门
     */
    public String getDoorNormalStatus() {
        return doorNormalStatus;
    }

    /**
     * 设置防火门类型：１常开门２常闭门
     *
     * @param doorNormalStatus 防火门类型：１常开门２常闭门
     */
    public void setDoorNormalStatus(String doorNormalStatus) {
        this.doorNormalStatus = doorNormalStatus;
    }

    /**
     * 获取所属楼层
     *
     * @return FLOOR - 所属楼层
     */
    public Integer getFloor() {
        return floor;
    }

    /**
     * 设置所属楼层
     *
     * @param floor 所属楼层
     */
    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    /**
     * 获取防火门位置描述
     *
     * @return POSITION_DESCRIPTION - 防火门位置描述
     */
    public String getPositionDescription() {
        return positionDescription;
    }

    /**
     * 设置防火门位置描述
     *
     * @param positionDescription 防火门位置描述
     */
    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    /**
     * 获取在楼层平面图中的坐标，以逗号分隔
     *
     * @return POSITION_SIGN - 在楼层平面图中的坐标，以逗号分隔
     */
    public String getPositionSign() {
        return positionSign;
    }

    /**
     * 设置在楼层平面图中的坐标，以逗号分隔
     *
     * @param positionSign 在楼层平面图中的坐标，以逗号分隔
     */
    public void setPositionSign(String positionSign) {
        this.positionSign = positionSign;
    }

    /**
     * 获取传感器数量
     *
     * @return SENSOR_NUM - 传感器数量
     */
    public Integer getSensorNum() {
        return sensorNum;
    }

    /**
     * 设置传感器数量
     *
     * @param sensorNum 传感器数量
     */
    public void setSensorNum(Integer sensorNum) {
        this.sensorNum = sensorNum;
    }

    /**
     * 获取JSON:[{'deviceId':1,'status':1},{'deviceId':1,'status':1}]
     *
     * @return DOOR_STATUS - JSON:[{'deviceId':1,'status':1},{'deviceId':1,'status':1}]
     */
    public String getDoorStatus() {
        return doorStatus;
    }

    /**
     * 设置JSON:[{'deviceId':1,'status':1},{'deviceId':1,'status':1}]
     *
     * @param doorStatus JSON:[{'deviceId':1,'status':1},{'deviceId':1,'status':1}]
     */
    public void setDoorStatus(String doorStatus) {
        this.doorStatus = doorStatus;
    }

    /**
     * 获取逻辑删除标识1表示已删除 0表示正常
     *
     * @return DEL_FLAG - 逻辑删除标识1表示已删除 0表示正常
     */
    public String getDelFlag() {
        return delFlag;
    }

    /**
     * 设置逻辑删除标识1表示已删除 0表示正常
     *
     * @param delFlag 逻辑删除标识1表示已删除 0表示正常
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
     * 获取保留字段
     *
     * @return DEPART_ID - 保留字段
     */
    public String getDepartId() {
        return departId;
    }

    /**
     * 设置保留字段
     *
     * @param departId 保留字段
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