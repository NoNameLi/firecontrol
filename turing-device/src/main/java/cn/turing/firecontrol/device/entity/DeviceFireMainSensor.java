package cn.turing.firecontrol.device.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "device_fire_main_sensor")
public class DeviceFireMainSensor {
    /**
     * 主键id
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 建筑列表id
     */
    @Column(name = "BUILDING_ID")
    private Integer buildingId;

    /**
     * 消防主机id
     */
    @Column(name = "FIRE_MAIN_ID")
    private Integer fireMainId;

    /**
     * 所属系统id
     */
    @Column(name = "CHANNEL_ID")
    private Integer channelId;

    /**
     * 系列
     */
    @Column(name = "SERIES")
    private String series;

    /**
     * 回路
     */
    @Column(name = "SENSOR_LOOP")
    private String sensorLoop;

    /**
     * 地址
     */
    @Column(name = "ADDRESS")
    private String address;

    /**
     * 状态[0=故障/1=报警/2=正常/3=未启用/4=离线]（需要产品确认）
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 楼层
     */
    @Column(name = "FLOOR")
    private Integer floor;

    /**
     * 位置描述
     */
    @Column(name = "POSITION_DESCRIPTION")
    private String positionDescription;

    /**
     * 传感器在平面图的位置标记（中间用,号隔开）
     */
    @Column(name = "POSITION_SIGN")
    private String positionSign;

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
     * 数据上传时间
     */
    @Column(name = "STATUS_TIME")
    private Date statusTime;

    /**
     * 获取主键id
     *
     * @return ID - 主键id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键id
     *
     * @param id 主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取建筑列表id
     *
     * @return BUILDING_ID - 建筑列表id
     */
    public Integer getBuildingId() {
        return buildingId;
    }

    /**
     * 设置建筑列表id
     *
     * @param buildingId 建筑列表id
     */
    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    /**
     * 获取消防主机id
     *
     * @return FIRE_MAIN_ID - 消防主机id
     */
    public Integer getFireMainId() {
        return fireMainId;
    }

    /**
     * 设置消防主机id
     *
     * @param fireMainId 消防主机id
     */
    public void setFireMainId(Integer fireMainId) {
        this.fireMainId = fireMainId;
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

    /**
     * 获取系列
     *
     * @return SERIES - 系列
     */
    public String getSeries() {
        return series;
    }

    /**
     * 设置系列
     *
     * @param series 系列
     */
    public void setSeries(String series) {
        this.series = series;
    }

    public String getSensorLoop() {
        return sensorLoop;
    }

    public void setSensorLoop(String sensorLoop) {
        this.sensorLoop = sensorLoop;
    }

    /**
     * 获取地址
     *
     * @return ADDRESS - 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置地址
     *
     * @param address 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取状态[0=故障/1=报警/2=正常/3=未启用/4=离线]（需要产品确认）
     *
     * @return STATUS - 状态[0=故障/1=报警/2=正常/3=未启用/4=离线]（需要产品确认）
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态[0=故障/1=报警/2=正常/3=未启用/4=离线]（需要产品确认）
     *
     * @param status 状态[0=故障/1=报警/2=正常/3=未启用/4=离线]（需要产品确认）
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取楼层
     *
     * @return FLOOR - 楼层
     */
    public Integer getFloor() {
        return floor;
    }

    /**
     * 设置楼层
     *
     * @param floor 楼层
     */
    public void setFloor(Integer floor) {
        this.floor = floor;
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
     * 获取传感器在平面图的位置标记（中间用,号隔开）
     *
     * @return POSITION_SIGN - 传感器在平面图的位置标记（中间用,号隔开）
     */
    public String getPositionSign() {
        return positionSign;
    }

    /**
     * 设置传感器在平面图的位置标记（中间用,号隔开）
     *
     * @param positionSign 传感器在平面图的位置标记（中间用,号隔开）
     */
    public void setPositionSign(String positionSign) {
        this.positionSign = positionSign;
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
     * 获取数据上传时间
     *
     * @return STATUS_TIME - 数据上传时间
     */
    public Date getStatusTime() {
        return statusTime;
    }

    /**
     * 设置数据上传时间
     *
     * @param statusTime 数据上传时间
     */
    public void setStatusTime(Date statusTime) {
        this.statusTime = statusTime;
    }
}