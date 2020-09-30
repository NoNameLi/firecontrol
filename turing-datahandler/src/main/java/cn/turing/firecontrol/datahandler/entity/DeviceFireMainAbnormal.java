package cn.turing.firecontrol.datahandler.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "device_fire_main_abnormal")
public class DeviceFireMainAbnormal implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @GeneratedValue(generator = "JDBC")
    @Id
    @Column(name = "ID")
    private Integer id;

    /**
     * 建筑列表id
     */
    @Column(name = "BUILD_ID")
    private Integer buildId;

    /**
     * 消防主机id
     */
    @Column(name = "FIRE_MAIN_ID")
    private Integer fireMainId;

    /**
     * 设备id
     */
    @Column(name = "EQU_ID")
    private Integer equId;

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
     * 报警类型：0：故障，1：火警
     */
    @Column(name = "ALRM_CATEGORY")
    private String alrmCategory;

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
     * 是否真实火警[2=火警测试/1=是/0=否]
     */
    @Column(name = "FIRE_FLAG")
    private String fireFlag;

    /**
     * 处理时间
     */
    @Column(name = "HANDLE_DATE")
    private Date handleDate;

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
     * 建筑名称
     */
    @Column(name = "B_NAME")
    private String bName;

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
     * 获取建筑列表id
     *
     * @return BUILD_ID - 建筑列表id
     */
    public Integer getBuildId() {
        return buildId;
    }

    /**
     * 设置建筑列表id
     *
     * @param buildId 建筑列表id
     */
    public void setBuildId(Integer buildId) {
        this.buildId = buildId;
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

    /**
     * 获取回路
     *
     * @return LOOP - 回路
     */
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
     * 获取报警类型：0：故障，1：火警
     *
     * @return ALRM_CATEGORY - 报警类型：0：故障，1：火警
     */
    public String getAlrmCategory() {
        return alrmCategory;
    }

    /**
     * 设置报警类型：0：故障，1：火警
     *
     * @param alrmCategory 报警类型：0：故障，1：火警
     */
    public void setAlrmCategory(String alrmCategory) {
        this.alrmCategory = alrmCategory;
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
     * 获取是否真实火警[2=火警测试/1=是/0=否]
     *
     * @return FIRE_FLAG - 是否真实火警[2=火警测试/1=是/0=否]
     */
    public String getFireFlag() {
        return fireFlag;
    }

    /**
     * 设置是否真实火警[2=火警测试/1=是/0=否]
     *
     * @param fireFlag 是否真实火警[2=火警测试/1=是/0=否]
     */
    public void setFireFlag(String fireFlag) {
        this.fireFlag = fireFlag;
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
     * 获取建筑名称
     *
     * @return B_NAME - 建筑名称
     */
    public String getbName() {
        return bName;
    }

    /**
     * 设置建筑名称
     *
     * @param bName 建筑名称
     */
    public void setbName(String bName) {
        this.bName = bName;
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
}