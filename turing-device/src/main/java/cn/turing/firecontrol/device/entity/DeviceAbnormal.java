package cn.turing.firecontrol.device.entity;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;


/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Table(name = "device_abnormal")
public class DeviceAbnormal implements Serializable {
	private static final long serialVersionUID = 1L;

		//主键id
	@GeneratedValue(generator = "JDBC")
	@Id
	private Integer id;
	
	    //建筑列表id
    @Column(name = "BUILD_ID")
    private Integer buildId;
	
	    //设备id
    @Column(name = "EQU_ID")
    private Integer equId;

	   //传感器数据
	@Column(name = "ALRM_DATA")
	private Double alrmData;

	//报警类型(0：故障报警，1：火警报警)
	@Column(name = "ALRM_CATEGORY")
	private String alrmCategory;
	
	    //报警类型
    @Column(name = "ALRM_TYPE")
    private String alrmType;
	
	    //报警时间
    @Column(name = "ALRM_DATE")
    private Date alrmDate;
	
	    //是否处理[1=是/0=否]
    @Column(name = "HANDLE_FLAG")
    private String handleFlag;
	
	    //是否为真实火警[1=是/0=否]
    @Column(name = "FIRE_FLAG")
    private String fireFlag;
	
	    //确认时间
    @Column(name = "CONFIR_DATE")
    private Date confirDate;
	
	    //处理时间
    @Column(name = "HANDLE_DATE")
    private Date handleDate;
	
	    //确认人
    @Column(name = "CONFIR_PERSON")
    private String confirPerson;
	
	    //处理人
    @Column(name = "HANDLE_PERSON")
	@NotBlank(message = "处理人不能为空")
    private String handlePerson;
	
	    //删除标记[1=是/0=否（default）]
    @Column(name = "DEL_FLAG")
    private String delFlag;
	
	    //创建者名称
    @Column(name = "CRT_USER_NAME")
    private String crtUserName;
	
	    //创建者Id
    @Column(name = "CRT_USER_ID")
    private String crtUserId;
	
	    //创建时间
    @Column(name = "CRT_TIME")
    private Date crtTime;
	
	    //修改者名称
    @Column(name = "UPD_USER_NAME")
    private String updUserName;
	
	    //修改者Id
    @Column(name = "UPD_USER_ID")
    private String updUserId;
	
	    //修改时间
    @Column(name = "UPD_TIME")
    private Date updTime;
	
	    //部门Id
    @Column(name = "DEPART_ID")
    private String departId;
	
	    //租户Id
    @Column(name = "TENANT_ID")
    private String tenantId;

	   //测点代号
	@Column(name = "UNIT")
	private String unit;

	   //日志id
	@Column(name = "LOG_ID")
	private String logId;

	   //建筑名称
	@Column(name = "B_NAME")
	private String bName;

	   //传感器编号
	@Column(name = "SENSOR_NO")
	private String sensorNo;

	   //设备类型
	@Column(name = "EQUIPMENT_TYPE")
	private String equipmentType;

	   //楼层
	@Column(name = "FLOOR")
	private Integer floor;

	  //位置描述
	@Column(name = "POSITION_DESCRIPTION")
	private String positionDescription;

	  //测点
	@Column(name = "MEASURING_POINT")
	private String measuringPoint;

	  //报警等级
	@Column(name = "LEVEL")
	private String level;

	  //栏目id
	@Column(name = "CHANNEL_ID")
	private Integer channelId;

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	/**
	 * 设置：主键ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：主键ID
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置：建筑列表id
	 */
	public void setBuildId(Integer buildId) {
		this.buildId = buildId;
	}
	/**
	 * 获取：建筑列表id
	 */
	public Integer getBuildId() {
		return buildId;
	}
	/**
	 * 设置：设备id
	 */
	public void setEquId(Integer equId) {
		this.equId = equId;
	}
	/**
	 * 获取：设备id
	 */
	public Integer getEquId() {
		return equId;
	}

	public String getAlrmCategory() {
		return alrmCategory;
	}

	public void setAlrmCategory(String alrmCategory) {
		this.alrmCategory = alrmCategory;
	}

	public Double getAlrmData() {
		return alrmData;
	}

	public void setAlrmData(Double alrmData) {
		this.alrmData = alrmData;
	}

	/**
	 * 设置：报警类型
	 */
	public void setAlrmType(String alrmType) {
		this.alrmType = alrmType;
	}
	/**
	 * 获取：报警类型
	 */
	public String getAlrmType() {
		return alrmType;
	}
	/**
	 * 设置：报警时间
	 */
	public void setAlrmDate(Date alrmDate) {
		this.alrmDate = alrmDate;
	}
	/**
	 * 获取：报警时间
	 */
	public Date getAlrmDate() {
		return alrmDate;
	}
	/**
	 * 设置：是否处理[1=是/0=否]
	 */
	public void setHandleFlag(String handleFlag) {
		this.handleFlag = handleFlag;
	}
	/**
	 * 获取：是否处理[1=是/0=否]
	 */
	public String getHandleFlag() {
		return handleFlag;
	}
	/**
	 * 设置：是否为真实火警[1=是/0=否]
	 */
	public void setFireFlag(String fireFlag) {
		this.fireFlag = fireFlag;
	}
	/**
	 * 获取：是否为真实火警[1=是/0=否]
	 */
	public String getFireFlag() {
		return fireFlag;
	}
	/**
	 * 设置：确认时间
	 */
	public void setConfirDate(Date confirDate) {
		this.confirDate = confirDate;
	}
	/**
	 * 获取：确认时间
	 */
	public Date getConfirDate() {
		return confirDate;
	}
	/**
	 * 设置：处理时间
	 */
	public void setHandleDate(Date handleDate) {
		this.handleDate = handleDate;
	}
	/**
	 * 获取：处理时间
	 */
	public Date getHandleDate() {
		return handleDate;
	}
	/**
	 * 设置：确认人
	 */
	public void setConfirPerson(String confirPerson) {
		this.confirPerson = confirPerson;
	}
	/**
	 * 获取：确认人
	 */
	public String getConfirPerson() {
		return confirPerson;
	}
	/**
	 * 设置：处理人
	 */
	public void setHandlePerson(String handlePerson) {
		this.handlePerson = handlePerson;
	}
	/**
	 * 获取：处理人
	 */
	public String getHandlePerson() {
		return handlePerson;
	}
	/**
	 * 设置：删除标记[1=是/0=否（default）]
	 */
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	/**
	 * 获取：删除标记[1=是/0=否（default）]
	 */
	public String getDelFlag() {
		return delFlag;
	}
	/**
	 * 设置：创建者名称
	 */
	public void setCrtUserName(String crtUserName) {
		this.crtUserName = crtUserName;
	}
	/**
	 * 获取：创建者名称
	 */
	public String getCrtUserName() {
		return crtUserName;
	}

	/**
	 * 设置：创建时间
	 */
	public void setCrtTime(Date crtTime) {
		this.crtTime = crtTime;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCrtTime() {
		return crtTime;
	}
	/**
	 * 设置：修改者名称
	 */
	public void setUpdUserName(String updUserName) {
		this.updUserName = updUserName;
	}
	/**
	 * 获取：修改者名称
	 */
	public String getUpdUserName() {
		return updUserName;
	}

	/**
	 * 设置：修改时间
	 */
	public void setUpdTime(Date updTime) {
		this.updTime = updTime;
	}
	/**
	 * 获取：修改时间
	 */
	public Date getUpdTime() {
		return updTime;
	}

	public String getCrtUserId() {
		return crtUserId;
	}

	public void setCrtUserId(String crtUserId) {
		this.crtUserId = crtUserId;
	}

	public String getUpdUserId() {
		return updUserId;
	}

	public void setUpdUserId(String updUserId) {
		this.updUserId = updUserId;
	}

	public String getDepartId() {
		return departId;
	}

	public void setDepartId(String departId) {
		this.departId = departId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public String getbName() {
		return bName;
	}

	public void setbName(String bName) {
		this.bName = bName;
	}

	public String getSensorNo() {
		return sensorNo;
	}

	public void setSensorNo(String sensorNo) {
		this.sensorNo = sensorNo;
	}

	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public String getPositionDescription() {
		return positionDescription;
	}

	public void setPositionDescription(String positionDescription) {
		this.positionDescription = positionDescription;
	}

	public String getMeasuringPoint() {
		return measuringPoint;
	}

	public void setMeasuringPoint(String measuringPoint) {
		this.measuringPoint = measuringPoint;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
}
