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
@Table(name = "device_collecting_device")
public class DeviceCollectingDevice implements Serializable {
	private static final long serialVersionUID = 1L;

		//主键id
	@GeneratedValue(generator = "JDBC")
	@Id
	private Integer id;
	
	    //采集设备类型id
    @Column(name = "COLLECTING_DEVIC_TYPE_ID")
    private Integer collectingDevicTypeId;
	
	    //状态[0=故障/1=正常]（需要产品确认值）
    @Column(name = "STATUS")
    private String status;
	
	    //编号
    @Column(name = "NO")
    private String no;
	
	    //网络形式 [](需要产品确认值)
    @Column(name = "NETWORK_FORM")
    private String networkForm;
	
	    //位置描述
    @Column(name = "POSITION_DESCRIPTION")
    private String positionDescription;
	
	    //地理位置
    @Column(name = "GEOGRAPHICAL_POSITION_SIGN")
    private String geographicalPositionSign;
	
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
	 * 设置：采集设备类型id
	 */
	public void setCollectingDevicTypeId(Integer collectingDevicTypeId) {
		this.collectingDevicTypeId = collectingDevicTypeId;
	}
	/**
	 * 获取：采集设备类型id
	 */
	public Integer getCollectingDevicTypeId() {
		return collectingDevicTypeId;
	}
	/**
	 * 设置：状态[0=故障/1=正常]（需要产品确认值）
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * 获取：状态[0=故障/1=正常]（需要产品确认值）
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * 设置：编号
	 */
	public void setNo(String no) {
		this.no = no;
	}
	/**
	 * 获取：编号
	 */
	public String getNo() {
		return no;
	}
	/**
	 * 设置：网络形式 [](需要产品确认值)
	 */
	public void setNetworkForm(String networkForm) {
		this.networkForm = networkForm;
	}
	/**
	 * 获取：网络形式 [](需要产品确认值)
	 */
	public String getNetworkForm() {
		return networkForm;
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
	 * 设置：地理位置
	 */
	public void setGeographicalPositionSign(String geographicalPositionSign) {
		this.geographicalPositionSign = geographicalPositionSign;
	}
	/**
	 * 获取：地理位置
	 */
	public String getGeographicalPositionSign() {
		return geographicalPositionSign;
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

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}
