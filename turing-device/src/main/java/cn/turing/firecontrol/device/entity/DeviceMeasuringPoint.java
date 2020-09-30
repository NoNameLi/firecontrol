package cn.turing.firecontrol.device.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;


/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Table(name = "device_measuring_point")
public class DeviceMeasuringPoint implements Serializable {
	private static final long serialVersionUID = 1L;

		//主键id
	@GeneratedValue(generator = "JDBC")
	@Id
	private Integer id;
	
	    //测点
    @Column(name = "MEASURING_POINT")
    private String measuringPoint;
	
	    //代号
    @Column(name = "CODE_NAME")
    private String codeName;
	
	    //数据单位
    @Column(name = "DATA_UNIT")
    private String dataUnit;
	
	    //测点正常值最大
    @Column(name = "NORMAL_VALUE_MAX")
    private String normalValueMax;


	//测点正常值是否包含最大值[0=不包含/1=包含]
	@Column(name = "NORMAL_SYMBOL_MAX")
	private String normalSymbolMax;

	//测点正常值最小
	@Column(name = "NORMAL_VALUE_MIN")
	private String normalValueMin;


	//测点正常值是否包含最小值[0=不包含/1=包含]
	@Column(name = "NORMAL_SYMBOL_MIN")
	private String normalSymbolMin;


	//测点类型[0=火警测点/1=监测测点]
	@Column(name = "MEASURING_POINT_TYPE")
	private String measuringPointType;
	
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

       //参数公用传参
    @Transient
	private String param;



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
	 * 设置：测点
	 */
	public void setMeasuringPoint(String measuringPoint) {
		this.measuringPoint = measuringPoint;
	}
	/**
	 * 获取：测点
	 */
	public String getMeasuringPoint() {
		return measuringPoint;
	}
	/**
	 * 设置：代号
	 */
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
	/**
	 * 获取：代号
	 */
	public String getCodeName() {
		return codeName;
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
	 * 设置：测点类型[0=火警测点/1=监测测点]
	 */
	public void setMeasuringPointType(String measuringPointType) {
		this.measuringPointType = measuringPointType;
	}
	/**
	 * 获取：测点类型[0=火警测点/1=监测测点]
	 */
	public String getMeasuringPointType() {
		return measuringPointType;
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


	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getNormalValueMax() {
		return normalValueMax;
	}

	public void setNormalValueMax(String normalValueMax) {
		this.normalValueMax = normalValueMax;
	}

	public String getNormalSymbolMax() {
		return normalSymbolMax;
	}

	public void setNormalSymbolMax(String normalSymbolMax) {
		this.normalSymbolMax = normalSymbolMax;
	}

	public String getNormalValueMin() {
		return normalValueMin;
	}

	public void setNormalValueMin(String normalValueMin) {
		this.normalValueMin = normalValueMin;
	}

	public String getNormalSymbolMin() {
		return normalSymbolMin;
	}

	public void setNormalSymbolMin(String normalSymbolMin) {
		this.normalSymbolMin = normalSymbolMin;
	}
}
