package cn.turing.firecontrol.device.entity;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Table(name = "device_sensor")
public class DeviceSensor implements Serializable {
	private static final long serialVersionUID = 1L;

		//主键id
	@GeneratedValue(generator = "JDBC")
	@Id
	private Long id;
	
	    //建筑列表id
    @Column(name = "BUILDING_ID")
    private Integer buildingId;

	    //栏目id
    @Column(name = "CHANNEL_ID")
//	@NotNull(message = "栏目ID不能为空")
    private Integer channelId;
	
	    //传感器类型id
    @Column(name = "SENSOR_TYPE_ID")
    private Integer sensorTypeId;
	
	    //采集设备id(备用字段)
    @Column(name = "CD_ID")
    private Integer cdId;
	
	    //数据字段状态[0=不正常/1=正常]（设备，厂商，型号，楼层是否正常）
    @Column(name = "FIELD_STATUS")
    private String fieldStatus;
	
	    //状态[0=故障/1=报警/2=正常]（需要产品确认）
	@Column(name = "STATUS")
	private String status;

	    //数据上报时间，此时间为传感器上传数据的时间
	@Column(name = "STATUS_TIME")
	private Date statusTime;
	
	    //传感器编号
	@Length(max = 16,min = 1,message = "设备编号长度最大16个字符")
	@Pattern(regexp = "^[A-Za-z0-9:]+$",message = "设备编号只允许字母与数字")
	@NotBlank(message = "设备编号不能为空")
    @Column(name = "SENSOR_NO")
    private String sensorNo;
	
	    //楼层
    @Column(name = "FLOOR")
    private Integer floor;
	
	    //位置描述
    @Column(name = "POSITION_DESCRIPTION")
    private String positionDescription;
	
	    //传感器在平面图的位置标记（中间用,号隔开）
    @Column(name = "POSITION_SIGN")
    private String positionSign;
	
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


	//设备类型
	@Transient
	private String equipmentType;

	//厂商
	@Transient
	private String manufacturer;

	//型号
	@Transient
	private String model;

	  //室外消火栓id
	@Column(name = "HYDRANT_ID")
	private Integer hydrantId;
	

	/**
	 * 设置：主键id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：主键id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：建筑列表id
	 */
	public void setBuildingId(Integer buildingId) {
		this.buildingId = buildingId;
	}
	/**
	 * 获取：建筑列表id
	 */
	public Integer getBuildingId() {
		return buildingId;
	}
	/**
	 * 获取：栏目id
	 */
	public Integer getChannelId() { return channelId; }
	/**
	 * 设置：栏目id
	 */
	public void setChannelId(Integer channelId) { this.channelId = channelId; }

	/**
	 * 设置：传感器类型id
	 */
	public void setSensorTypeId(Integer sensorTypeId) {
		this.sensorTypeId = sensorTypeId;
	}
	/**
	 * 获取：传感器类型id
	 */
	public Integer getSensorTypeId() {
		return sensorTypeId;
	}
	/**
	 * 设置：采集设备id(备用字段)
	 */
	public void setCdId(Integer cdId) {
		this.cdId = cdId;
	}
	/**
	 * 获取：采集设备id(备用字段)
	 */
	public Integer getCdId() {
		return cdId;
	}
	/**
	 * 设置：数据字段状态[0=不正常/1=正常]（设备，厂商，型号，楼层是否正常）
	 */
	public void setFieldStatus(String fieldStatus) {
		this.fieldStatus = fieldStatus;
	}
	/**
	 * 获取：数据字段状态[0=不正常/1=正常]（设备，厂商，型号，楼层是否正常）
	 */
	public String getFieldStatus() {
		return fieldStatus;
	}
	/**
	 * 设置：状态[0=故障/1=报警/2=正常]（需要产品确认）
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * 获取：状态[0=故障/1=报警/2=正常]（需要产品确认）
	 */
	public String getStatus() {
		return status;
	}

	public Date getStatusTime() {
		return statusTime;
	}

	public void setStatusTime(Date statusTime) {
		this.statusTime = statusTime;
	}

	/**
	 * 设置：传感器编号
	 */
	public void setSensorNo(String sensorNo) {
		this.sensorNo = sensorNo;
	}
	/**
	 * 获取：传感器编号
	 */
	public String getSensorNo() {
		return sensorNo;
	}
	/**
	 * 设置：楼层
	 */
	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	/**
	 * 获取：楼层
	 */
	public Integer getFloor() {
		return floor;
	}
	/**
	 * 设置：位置描述
	 */
	public void setPositionDescription(String positionDescription) {
		this.positionDescription = positionDescription;
	}
	/**
	 * 获取：位置描述
	 */
	public String getPositionDescription() {
		return positionDescription;
	}
	/**
	 * 设置：传感器在平面图的位置标记（中间用,号隔开）
	 */
	public void setPositionSign(String positionSign) {
		this.positionSign = positionSign;
	}
	/**
	 * 获取：传感器在平面图的位置标记（中间用,号隔开）
	 */
	public String getPositionSign() {
		return positionSign;
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

	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Integer getHydrantId() {
		return hydrantId;
	}

	public void setHydrantId(Integer hydrantId) {
		this.hydrantId = hydrantId;
	}
}
