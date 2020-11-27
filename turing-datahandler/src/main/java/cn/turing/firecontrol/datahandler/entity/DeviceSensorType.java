package cn.turing.firecontrol.datahandler.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;


/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Table(name = "device_sensor_type")
public class DeviceSensorType implements Serializable {
	private static final long serialVersionUID = 1L;
	
	    //主键id
	@GeneratedValue(generator = "JDBC")
    @Id
    private Integer id;
	
	    //设备类型
    @Column(name = "EQUIPMENT_TYPE")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5A-Za-z0-9\\-/]+$",message = "设备类型只允许字母、数字、/、-")
    private String equipmentType;
	
	    //厂商
    @Column(name = "MANUFACTURER")
    private String manufacturer;
	
	    //型号
    @Column(name = "MODEL")
    private String model;
	
	    //数据单位
    @Column(name = "DATA_UNIT")
    private String dataUnit;
	
	    //数据采集周期单位[0=秒/1=分钟/2=小时/3=天/4=月](单位暂定，待产品规定)
    @Column(name = "DATA_ACQUISITION_CYCLE_UNIT")
    private String dataAcquisitionCycleUnit;
	
	    //数据采集周期数值
    @Column(name = "DATA_ACQUISITION_CYCLE_VALUE")
    private Integer dataAcquisitionCycleValue;
	
	    //数据采集周期转化为秒
    @Column(name = "DATA_ACQUISITION_CYCLE_SECOND")
    private Integer dataAcquisitionCycleSecond;
	
	    //采集延时时间单位[0=秒/1=分钟/2=小时/3=天/4=月](单位暂定，待产品规定)
    @Column(name = "ACQUISITION_DELAY_TIME_UNIT")
    private String acquisitionDelayTimeUnit;
	
	    //采集延时时间数值
    @Column(name = "ACQUISITION_DELAY_TIME_VALUE")
    private Integer acquisitionDelayTimeValue;
	
	    //采集延时时间转换为秒
    @Column(name = "ACQUISITION_DELAY_TIME_SECOND")
    private Integer acquisitionDelayTimeSecond;
	
	    //一级报警等级阈值(最小值)
    @Column(name = "FIRST_LEVEL_ALARM_MIN")
    private Integer firstLevelAlarmMin;
	
	    //一级报警等级阈值(最大值)
    @Column(name = "FIRST_LEVEL_ALARM_MAX")
    private Integer firstLevelAlarmMax;
	
	    //二级报警等级阈值(最小值)
    @Column(name = "TWO_LEVEL_ALARM_MIN")
    private Integer twoLevelAlarmMin;
	
	    //二级报警等级阈值(最大值)
    @Column(name = "TWO_LEVEL_ALARM_MAX")
    private Integer twoLevelAlarmMax;
	
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

	@Column(name = "CHANNEL_ID")
	private Integer channelId;

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
	 * 设置：数据单位
	 */
	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}
	/**
	 * 获取：数据单位
	 */
	public String getDataUnit() {
		return dataUnit;
	}
	/**
	 * 设置：数据采集周期单位[0=秒/1=分钟/2=小时/3=天/4=月](单位暂定，待产品规定)
	 */
	public void setDataAcquisitionCycleUnit(String dataAcquisitionCycleUnit) {
		this.dataAcquisitionCycleUnit = dataAcquisitionCycleUnit;
	}
	/**
	 * 获取：数据采集周期单位[0=秒/1=分钟/2=小时/3=天/4=月](单位暂定，待产品规定)
	 */
	public String getDataAcquisitionCycleUnit() {
		return dataAcquisitionCycleUnit;
	}
	/**
	 * 设置：数据采集周期数值
	 */
	public void setDataAcquisitionCycleValue(Integer dataAcquisitionCycleValue) {
		this.dataAcquisitionCycleValue = dataAcquisitionCycleValue;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public Integer getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Integer manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	/**
	 * 获取：数据采集周期数值
	 */
	public Integer getDataAcquisitionCycleValue() {
		return dataAcquisitionCycleValue;
	}
	/**
	 * 设置：数据采集周期转化为秒
	 */
	public void setDataAcquisitionCycleSecond(Integer dataAcquisitionCycleSecond) {
		this.dataAcquisitionCycleSecond = dataAcquisitionCycleSecond;
	}
	/**
	 * 获取：数据采集周期转化为秒
	 */
	public Integer getDataAcquisitionCycleSecond() {
		return dataAcquisitionCycleSecond;
	}
	/**
	 * 设置：采集延时时间单位[0=秒/1=分钟/2=小时/3=天/4=月](单位暂定，待产品规定)
	 */
	public void setAcquisitionDelayTimeUnit(String acquisitionDelayTimeUnit) {
		this.acquisitionDelayTimeUnit = acquisitionDelayTimeUnit;
	}
	/**
	 * 获取：采集延时时间单位[0=秒/1=分钟/2=小时/3=天/4=月](单位暂定，待产品规定)
	 */
	public String getAcquisitionDelayTimeUnit() {
		return acquisitionDelayTimeUnit;
	}
	/**
	 * 设置：采集延时时间数值
	 */
	public void setAcquisitionDelayTimeValue(Integer acquisitionDelayTimeValue) {
		this.acquisitionDelayTimeValue = acquisitionDelayTimeValue;
	}
	/**
	 * 获取：采集延时时间数值
	 */
	public Integer getAcquisitionDelayTimeValue() {
		return acquisitionDelayTimeValue;
	}
	/**
	 * 设置：采集延时时间转换为秒
	 */
	public void setAcquisitionDelayTimeSecond(Integer acquisitionDelayTimeSecond) {
		this.acquisitionDelayTimeSecond = acquisitionDelayTimeSecond;
	}
	/**
	 * 获取：采集延时时间转换为秒
	 */
	public Integer getAcquisitionDelayTimeSecond() {
		return acquisitionDelayTimeSecond;
	}
	/**
	 * 设置：一级报警等级阈值(最小值)
	 */
	public void setFirstLevelAlarmMin(Integer firstLevelAlarmMin) {
		this.firstLevelAlarmMin = firstLevelAlarmMin;
	}
	/**
	 * 获取：一级报警等级阈值(最小值)
	 */
	public Integer getFirstLevelAlarmMin() {
		return firstLevelAlarmMin;
	}
	/**
	 * 设置：一级报警等级阈值(最大值)
	 */
	public void setFirstLevelAlarmMax(Integer firstLevelAlarmMax) {
		this.firstLevelAlarmMax = firstLevelAlarmMax;
	}
	/**
	 * 获取：一级报警等级阈值(最大值)
	 */
	public Integer getFirstLevelAlarmMax() {
		return firstLevelAlarmMax;
	}
	/**
	 * 设置：二级报警等级阈值(最小值)
	 */
	public void setTwoLevelAlarmMin(Integer twoLevelAlarmMin) {
		this.twoLevelAlarmMin = twoLevelAlarmMin;
	}
	/**
	 * 获取：二级报警等级阈值(最小值)
	 */
	public Integer getTwoLevelAlarmMin() {
		return twoLevelAlarmMin;
	}
	/**
	 * 设置：二级报警等级阈值(最大值)
	 */
	public void setTwoLevelAlarmMax(Integer twoLevelAlarmMax) {
		this.twoLevelAlarmMax = twoLevelAlarmMax;
	}
	/**
	 * 获取：二级报警等级阈值(最大值)
	 */
	public Integer getTwoLevelAlarmMax() {
		return twoLevelAlarmMax;
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
	/**
	 * 设置：租户Id
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantId() {
		return tenantId;
	}

}
