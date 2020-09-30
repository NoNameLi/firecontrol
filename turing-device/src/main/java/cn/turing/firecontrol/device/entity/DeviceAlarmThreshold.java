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
@Table(name = "device_alarm_threshold")
public class DeviceAlarmThreshold implements Serializable {
	private static final long serialVersionUID = 1L;

		//主键id
	@GeneratedValue(generator = "JDBC")
	@Id
	private Integer id;
	
	    //测点id
    @Column(name = "MP_ID")
    private Integer mpId;
	
	    //报警等级id
    @Column(name = "AL_ID")
    private Integer alId;
	
	    //报警最小值
    @Column(name = "ALARM_MIN")
    private Double alarmMin;
	
	    //报警最大值
    @Column(name = "ALARM_MAX")
    private Double alarmMax;
	
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


	@Column(name = "ALARM_SYMBOL_MAX")
	private String alarmSymbolMax;

	@Column(name = "ALARM_SYMBOL_MIN")
	private String alarmSymbolMin;

	public String getAlarmSymbolMax() {
		return alarmSymbolMax;
	}

	public void setAlarmSymbolMax(String alarmSymbolMax) {
		this.alarmSymbolMax = alarmSymbolMax;
	}

	public String getAlarmSymbolMin() {
		return alarmSymbolMin;
	}

	public void setAlarmSymbolMin(String alarmSymbolMin) {
		this.alarmSymbolMin = alarmSymbolMin;
	}

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
	 * 设置：测点id
	 */
	public void setMpId(Integer mpId) {
		this.mpId = mpId;
	}
	/**
	 * 获取：测点id
	 */
	public Integer getMpId() {
		return mpId;
	}
	/**
	 * 设置：报警等级id
	 */
	public void setAlId(Integer alId) {
		this.alId = alId;
	}
	/**
	 * 获取：报警等级id
	 */
	public Integer getAlId() {
		return alId;
	}
	/**
	 * 设置：报警最小值
	 */
	public void setAlarmMin(Double alarmMin) {
		this.alarmMin = alarmMin;
	}
	/**
	 * 获取：报警最小值
	 */
	public Double getAlarmMin() {
		return alarmMin;
	}
	/**
	 * 设置：报警最大值
	 */
	public void setAlarmMax(Double alarmMax) {
		this.alarmMax = alarmMax;
	}
	/**
	 * 获取：报警最大值
	 */
	public Double getAlarmMax() {
		return alarmMax;
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
}
