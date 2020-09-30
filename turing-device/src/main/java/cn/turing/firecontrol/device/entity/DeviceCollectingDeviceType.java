package cn.turing.firecontrol.device.entity;

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
@Table(name = "device_collecting_device_type")
public class DeviceCollectingDeviceType implements Serializable {
	private static final long serialVersionUID = 1L;

		//主键id
	@GeneratedValue(generator = "JDBC")
	@Id
	private Integer id;
	
	    //设备类型
    @Column(name = "EQUIPMENT_TYPE")
    private String equipmentType;
	
	    //厂商
    @Column(name = "MANUFACTURER")
    private String manufacturer;
	
	    //型号
    @Column(name = "MODEL")
    private String model;
	
	    //维保周期单位[0=天/1=月/2=年](单位暂定，待产品规定)
    @Column(name = "MAINTENANCE_CYCLE_UNIT")
    private String maintenanceCycleUnit;
	
	    //维保周期数值
    @Column(name = "MAINTENANCE_CYCLE_VALUE")
    private Integer maintenanceCycleValue;
	
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

	//厂商ID
	@Column(name = "MANUFACTURER_ID")
	private Integer manufacturerId;

	/**
	 * 设置：主键id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：主键id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置：设备类型
	 */
	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}
	/**
	 * 获取：设备类型
	 */
	public String getEquipmentType() {
		return equipmentType;
	}
	/**
	 * 设置：厂商
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	/**
	 * 获取：厂商
	 */
	public String getManufacturer() {
		return manufacturer;
	}

	public Integer getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Integer manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	/**
	 * 设置：型号
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * 获取：型号
	 */
	public String getModel() {
		return model;
	}
	/**
	 * 设置：维保周期单位[0=天/1=月/2=年](单位暂定，待产品规定)
	 */
	public void setMaintenanceCycleUnit(String maintenanceCycleUnit) {
		this.maintenanceCycleUnit = maintenanceCycleUnit;
	}
	/**
	 * 获取：维保周期单位[0=天/1=月/2=年](单位暂定，待产品规定)
	 */
	public String getMaintenanceCycleUnit() {
		return maintenanceCycleUnit;
	}
	/**
	 * 设置：维保周期数值
	 */
	public void setMaintenanceCycleValue(Integer maintenanceCycleValue) {
		this.maintenanceCycleValue = maintenanceCycleValue;
	}
	/**
	 * 获取：维保周期数值
	 */
	public Integer getMaintenanceCycleValue() {
		return maintenanceCycleValue;
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
}
