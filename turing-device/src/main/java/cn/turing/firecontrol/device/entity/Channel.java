package cn.turing.firecontrol.device.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Table(name = "channel")
public class Channel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	    //主键id
    @Id
    private Integer id;

    // 栏目类型（0：消防系统 /1：其他）
    @Column(name = "CHANNEL_TYPE")
	private String channelType;

    @Column(name = "PARENT_ID")
	private Integer parentId;
	
	    //栏目名
    @Column(name = "CHANNEL_NAME")
    private String channelName;
	
	    //删除标记[1=是/0=否（default）]
    @Column(name = "DEL_FLAG")
    private String delFlag;
	
	    //创建者名称
    @Column(name = "CRT_USER_NAME")
    private String crtUserName;
	
	    //创建者Id
    @Column(name = "CRT_USER_ID")
    private Integer crtUserId;
	
	    //创建时间
    @Column(name = "CRT_TIME")
    private Date crtTime;
	
	    //修改者名称
    @Column(name = "UPD_USER_NAME")
    private String updUserName;
	
	    //修改者Id
    @Column(name = "UPD_USER_ID")
    private Integer updUserId;
	
	    //修改时间
    @Column(name = "UPD_TIME")
    private Date updTime;
	
	    //部门Id
    @Column(name = "DEPART_ID")
    private Integer departId;
	
	    //租户Id
    @Column(name = "TENANT_ID")
    private Integer tenantId;
	

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

	public void setParentId(Integer parentId){
		this.parentId = parentId;
	}

	public Integer getParentId(){
		return this.parentId;
	}

	public void setChannelType(String channelType){
		this.channelType = channelType;
	}

	public String getChannelType(){
		return this.channelType;
	}

	/**
	 * 设置：系统名
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	/**
	 * 获取：系统名
	 */
	public String getChannelName() {
		return channelName;
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
	 * 设置：创建者Id
	 */
	public void setCrtUserId(Integer crtUserId) {
		this.crtUserId = crtUserId;
	}
	/**
	 * 获取：创建者Id
	 */
	public Integer getCrtUserId() {
		return crtUserId;
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
	 * 设置：修改者Id
	 */
	public void setUpdUserId(Integer updUserId) {
		this.updUserId = updUserId;
	}
	/**
	 * 获取：修改者Id
	 */
	public Integer getUpdUserId() {
		return updUserId;
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
	/**
	 * 设置：部门Id
	 */
	public void setDepartId(Integer departId) {
		this.departId = departId;
	}
	/**
	 * 获取：部门Id
	 */
	public Integer getDepartId() {
		return departId;
	}
	/**
	 * 设置：租户Id
	 */
	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}
	/**
	 * 获取：租户Id
	 */
	public Integer getTenantId() {
		return tenantId;
	}
}
