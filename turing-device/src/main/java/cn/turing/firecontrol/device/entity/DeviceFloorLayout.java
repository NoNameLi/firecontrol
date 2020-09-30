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
@Table(name = "device_floor_layout")
public class DeviceFloorLayout implements Serializable {
	private static final long serialVersionUID = 1L;

		//主键id
	@GeneratedValue(generator = "JDBC")
	@Id
	private Integer id;
	
	    //建筑ID
    @Column(name = "BUILD_ID")
    private Integer buildId;
	
	    //附件所属楼层
    @Column(name = "FILE_FLOOR")
    private Integer fileFloor;
	
	    //文件名
    @Column(name = "FILE_NAME")
    private String fileName;
	
	    //文件类型/后缀名
    @Column(name = "FILE_TYPE")
    private String fileType;
	
	    //文件路径
    @Column(name = "FILE_PATH")
    private String filePath;
	
	    //说明
    @Column(name = "MEMO")
    private String memo;
	
	    //文件删除标记（0未删除，1已删除）
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
	 * 设置：建筑ID
	 */
	public void setBuildId(Integer buildId) {
		this.buildId = buildId;
	}
	/**
	 * 获取：建筑ID
	 */
	public Integer getBuildId() {
		return buildId;
	}
	/**
	 * 设置：附件所属楼层
	 */
	public void setFileFloor(Integer fileFloor) {
		this.fileFloor = fileFloor;
	}
	/**
	 * 获取：附件所属楼层
	 */
	public Integer getFileFloor() {
		return fileFloor;
	}
	/**
	 * 设置：文件名
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * 获取：文件名
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * 设置：文件类型/后缀名
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	/**
	 * 获取：文件类型/后缀名
	 */
	public String getFileType() {
		return fileType;
	}
	/**
	 * 设置：文件路径
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	/**
	 * 获取：文件路径
	 */
	public String getFilePath() {
		return filePath;
	}
	/**
	 * 设置：说明
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}
	/**
	 * 获取：说明
	 */
	public String getMemo() {
		return memo;
	}
	/**
	 * 设置：文件删除标记（0未删除，1已删除）
	 */
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	/**
	 * 获取：文件删除标记（0未删除，1已删除）
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
