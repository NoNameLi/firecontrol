package cn.turing.firecontrol.device.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;


/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:57
 */
@Table(name = "device_alarm_level")
public class DeviceAlarmLevel implements Serializable {
	private static final long serialVersionUID = 1L;

		//主键id
	@GeneratedValue(generator = "JDBC")
	@Id
	private Integer id;
	
	    //报警等级
    @Column(name = "LEVEL")
    private String level;
	
	    //报警颜色
    @Column(name = "COLOR")
    private String color;
	
	    //排序
    @Column(name = "SORT")
    private Integer sort;
	
	    //等级说明
    @Column(name = "LEVEL_INSTRUCTIONS")
    private String levelInstructions;
	
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
	 * 设置：报警等级
	 */
	public void setLevel(String level) {
		this.level = level;
	}
	/**
	 * 获取：报警等级
	 */
	public String getLevel() {
		return level;
	}
	/**
	 * 设置：报警颜色
	 */
	public void setColor(String color) {
		this.color = color;
	}
	/**
	 * 获取：报警颜色
	 */
	public String getColor() {
		return color;
	}
	/**
	 * 设置：排序
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	/**
	 * 获取：排序
	 */
	public Integer getSort() {
		return sort;
	}
	/**
	 * 设置：等级说明
	 */
	public void setLevelInstructions(String levelInstructions) {
		this.levelInstructions = levelInstructions;
	}
	/**
	 * 获取：等级说明
	 */
	public String getLevelInstructions() {
		return levelInstructions;
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
