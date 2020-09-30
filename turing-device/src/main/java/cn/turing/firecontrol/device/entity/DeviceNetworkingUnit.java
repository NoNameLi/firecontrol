package cn.turing.firecontrol.device.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;


/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Table(name = "device_networking_unit")
public class DeviceNetworkingUnit implements Serializable {
	private static final long serialVersionUID = 1L;

		//主键id
	@GeneratedValue(generator = "JDBC")
	@Id
	private Integer id;
	
	    //单位ID
    @Column(name = "OID")
    private Integer oid;
	
	    //单位名称
    @Column(name = "O_NAME")
    private String oName;
	
	    //统一社会信用代码
    @Column(name = "O_LICENSE")
    private String oLicense;
	
	    //单位注册时间
    @Column(name = "O_LICENSE_TIME")
    private Date oLicenseTime;

	   //单位注册时间戳
	@Transient
	private Long oLicenseTimeLong;
	
	    //单位地址
    @Column(name = "O_ADDRESS")
    private String oAddress;
	
	    //6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）
    @Column(name = "XZQY")
    private String xzqy;
	
	    //3位街道编码（应符合现行国家标准《县以下行政区划代码编码规则》GB10114的规定）
    @Column(name = "STREET")
    private String street;
	
	    //路名
    @Column(name = "ROAD")
    private String road;
	
	    //门弄牌号
    @Column(name = "MNPH")
    private String mnph;
	
	    //楼栋幢
    @Column(name = "LDZ")
    private String ldz;
	
	    //详细地址
    @Column(name = "ADDRESS_DETAIL")
    private String addressDetail;
	
	    //地理坐标
    @Column(name = "GIS")
    private String gis;

	
	    //单位电话
    @Column(name = "O_PHONE")
    private String oPhone;
	
	    //单位消防安全责任人姓名
    @Column(name = "SAFE_DUTY_NAME")
    private String safeDutyName;
	
	    //单位消防安全责任人电话
    @Column(name = "SAFE_DUTY_PHONE")
    private String safeDutyPhone;
	
	    //单位安全消防责任人身份证号
    @Column(name = "SAFE_DUTY_IDCARD")
    private String safeDutyIdcard;
	
	    //企业法人姓名
    @Column(name = "LEGAL_NAME")
    private String legalName;
	
	    //企业法人电话
    @Column(name = "LEGAL_PHONE")
    private String legalPhone;
	
	    //企业法人身份证号
    @Column(name = "LEGAL_IDCARD")
    private String legalIdcard;
	
	    //单位消防安全管理人员姓名
    @Column(name = "SAFE_MANAGER_NAME")
    private String safeManagerName;
	
	    //单位消防安全管理人员电话
    @Column(name = "SAFE_MANAGER_PHONE")
    private String safeManagerPhone;
	
	    //单位安全消防管理人员身份证号
    @Column(name = "SAFE_MANAGER_IDCARD")
    private String safeManagerIdcard;
	
	    //单位联系人
    @Column(name = "O_LINKMAN")
    private String oLinkman;
	
	    //单位联系电话
    @Column(name = "O_LINKPHONE")
    private String oLinkphone;
	
	    //单位类别[1=重点单位；2=一般单位；3=九小场所；4=其他单位
    @Column(name = "O_TYPE")
    private String oType;
	
	    //单位性质/经济所有制
    @Column(name = "O_NATURE")
    private String oNature;
	
	    //单位类型
    @Column(name = "O_CLASS")
    private String oClass;
	
	    //确定重点单位时间
    @Column(name = "KEYUNIT_TIME")
    private Date keyunitTime;

	  //确定重点单位时间错
	@Transient
	private Long keyunitTimeLong;
	
	    //是否重点单位[0=否；1=是]
    @Column(name = "IS_KEYUNIT")
    private String isKeyunit;
	
	    //单位其他情况
    @Column(name = "O_OTHER")
    private String oOther;
	
	    //修改时间
    @Column(name = "CHANGETIME")
    private Date changetime;
	
	    //创建时间
    @Column(name = "CREATETIME")
    private Date createtime;
	
	    //修改人
    @Column(name = "CHANGEACC")
    private String changeacc;
	
	    //创建人
    @Column(name = "CREATEACC")
    private String createacc;
	
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
	 * 设置：主键UUID
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：主键UUID
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置：单位ID
	 */
	public void setOid(Integer oid) {
		this.oid = oid;
	}
	/**
	 * 获取：单位ID
	 */
	public Integer getOid() {
		return oid;
	}
	/**
	 * 设置：单位名称
	 */
	public void setOName(String oName) {
		this.oName = oName;
	}
	/**
	 * 获取：单位名称
	 */
	public String getOName() {
		return oName;
	}
	/**
	 * 设置：统一社会信用代码
	 */
	public void setOLicense(String oLicense) {
		this.oLicense = oLicense;
	}
	/**
	 * 获取：统一社会信用代码
	 */
	public String getOLicense() {
		return oLicense;
	}
	/**
	 * 设置：单位注册时间
	 */
	public void setOLicenseTime(Date oLicenseTime) {
		this.oLicenseTime = oLicenseTime;
	}
	/**
	 * 获取：单位注册时间
	 */
	public Date getOLicenseTime() {
		return oLicenseTime;
	}
	/**
	 * 设置：单位地址
	 */
	public void setOAddress(String oAddress) {
		this.oAddress = oAddress;
	}
	/**
	 * 获取：单位地址
	 */
	public String getOAddress() {
		return oAddress;
	}
	/**
	 * 设置：6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）
	 */
	public void setXzqy(String xzqy) {
		this.xzqy = xzqy;
	}
	/**
	 * 获取：6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）
	 */
	public String getXzqy() {
		return xzqy;
	}
	/**
	 * 设置：3位街道编码（应符合现行国家标准《县以下行政区划代码编码规则》GB10114的规定）
	 */
	public void setStreet(String street) {
		this.street = street;
	}
	/**
	 * 获取：3位街道编码（应符合现行国家标准《县以下行政区划代码编码规则》GB10114的规定）
	 */
	public String getStreet() {
		return street;
	}
	/**
	 * 设置：路名
	 */
	public void setRoad(String road) {
		this.road = road;
	}
	/**
	 * 获取：路名
	 */
	public String getRoad() {
		return road;
	}
	/**
	 * 设置：门弄牌号
	 */
	public void setMnph(String mnph) {
		this.mnph = mnph;
	}
	/**
	 * 获取：门弄牌号
	 */
	public String getMnph() {
		return mnph;
	}
	/**
	 * 设置：楼栋幢
	 */
	public void setLdz(String ldz) {
		this.ldz = ldz;
	}
	/**
	 * 获取：楼栋幢
	 */
	public String getLdz() {
		return ldz;
	}
	/**
	 * 设置：详细地址
	 */
	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}
	/**
	 * 获取：详细地址
	 */
	public String getAddressDetail() {
		return addressDetail;
	}

	public String getGis() {
		return gis;
	}

	public void setGis(String gis) {
		this.gis = gis;
	}

	/**
	 * 设置：单位电话
	 */
	public void setOPhone(String oPhone) {
		this.oPhone = oPhone;
	}
	/**
	 * 获取：单位电话
	 */
	public String getOPhone() {
		return oPhone;
	}
	/**
	 * 设置：单位消防安全责任人姓名
	 */
	public void setSafeDutyName(String safeDutyName) {
		this.safeDutyName = safeDutyName;
	}
	/**
	 * 获取：单位消防安全责任人姓名
	 */
	public String getSafeDutyName() {
		return safeDutyName;
	}
	/**
	 * 设置：单位消防安全责任人电话
	 */
	public void setSafeDutyPhone(String safeDutyPhone) {
		this.safeDutyPhone = safeDutyPhone;
	}
	/**
	 * 获取：单位消防安全责任人电话
	 */
	public String getSafeDutyPhone() {
		return safeDutyPhone;
	}
	/**
	 * 设置：单位安全消防责任人身份证号
	 */
	public void setSafeDutyIdcard(String safeDutyIdcard) {
		this.safeDutyIdcard = safeDutyIdcard;
	}
	/**
	 * 获取：单位安全消防责任人身份证号
	 */
	public String getSafeDutyIdcard() {
		return safeDutyIdcard;
	}
	/**
	 * 设置：企业法人姓名
	 */
	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}
	/**
	 * 获取：企业法人姓名
	 */
	public String getLegalName() {
		return legalName;
	}
	/**
	 * 设置：企业法人电话
	 */
	public void setLegalPhone(String legalPhone) {
		this.legalPhone = legalPhone;
	}
	/**
	 * 获取：企业法人电话
	 */
	public String getLegalPhone() {
		return legalPhone;
	}
	/**
	 * 设置：企业法人身份证号
	 */
	public void setLegalIdcard(String legalIdcard) {
		this.legalIdcard = legalIdcard;
	}
	/**
	 * 获取：企业法人身份证号
	 */
	public String getLegalIdcard() {
		return legalIdcard;
	}
	/**
	 * 设置：单位消防安全管理人员姓名
	 */
	public void setSafeManagerName(String safeManagerName) {
		this.safeManagerName = safeManagerName;
	}
	/**
	 * 获取：单位消防安全管理人员姓名
	 */
	public String getSafeManagerName() {
		return safeManagerName;
	}
	/**
	 * 设置：单位消防安全管理人员电话
	 */
	public void setSafeManagerPhone(String safeManagerPhone) {
		this.safeManagerPhone = safeManagerPhone;
	}
	/**
	 * 获取：单位消防安全管理人员电话
	 */
	public String getSafeManagerPhone() {
		return safeManagerPhone;
	}
	/**
	 * 设置：单位安全消防管理人员身份证号
	 */
	public void setSafeManagerIdcard(String safeManagerIdcard) {
		this.safeManagerIdcard = safeManagerIdcard;
	}
	/**
	 * 获取：单位安全消防管理人员身份证号
	 */
	public String getSafeManagerIdcard() {
		return safeManagerIdcard;
	}
	/**
	 * 设置：单位联系人
	 */
	public void setOLinkman(String oLinkman) {
		this.oLinkman = oLinkman;
	}
	/**
	 * 获取：单位联系人
	 */
	public String getOLinkman() {
		return oLinkman;
	}
	/**
	 * 设置：单位联系电话
	 */
	public void setOLinkphone(String oLinkphone) {
		this.oLinkphone = oLinkphone;
	}
	/**
	 * 获取：单位联系电话
	 */
	public String getOLinkphone() {
		return oLinkphone;
	}
	/**
	 * 设置：单位类别[1=重点单位；2=一般单位；3=九小场所；4=其他单位
	 */
	public void setOType(String oType) {
		this.oType = oType;
	}
	/**
	 * 获取：单位类别[1=重点单位；2=一般单位；3=九小场所；4=其他单位
	 */
	public String getOType() {
		return oType;
	}

	public String getoNature() {
		return oNature;
	}

	public void setoNature(String oNature) {
		this.oNature = oNature;
	}

	/**
	 * 设置：单位类型
	 */
	public void setOClass(String oClass) {
		this.oClass = oClass;
	}
	/**
	 * 获取：单位类型
	 */
	public String getOClass() {
		return oClass;
	}
	/**
	 * 设置：确定重点单位时间
	 */
	public void setKeyunitTime(Date keyunitTime) {
		this.keyunitTime = keyunitTime;
	}
	/**
	 * 获取：确定重点单位时间
	 */
	public Date getKeyunitTime() {
		return keyunitTime;
	}
	/**
	 * 设置：是否重点单位[0=否；1=是]
	 */
	public void setIsKeyunit(String isKeyunit) {
		this.isKeyunit = isKeyunit;
	}
	/**
	 * 获取：是否重点单位[0=否；1=是]
	 */
	public String getIsKeyunit() {
		return isKeyunit;
	}
	/**
	 * 设置：单位其他情况
	 */
	public void setOOther(String oOther) {
		this.oOther = oOther;
	}
	/**
	 * 获取：单位其他情况
	 */
	public String getOOther() {
		return oOther;
	}
	/**
	 * 设置：修改时间
	 */
	public void setChangetime(Date changetime) {
		this.changetime = changetime;
	}
	/**
	 * 获取：修改时间
	 */
	public Date getChangetime() {
		return changetime;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreatetime() {
		return createtime;
	}
	/**
	 * 设置：修改人
	 */
	public void setChangeacc(String changeacc) {
		this.changeacc = changeacc;
	}
	/**
	 * 获取：修改人
	 */
	public String getChangeacc() {
		return changeacc;
	}
	/**
	 * 设置：创建人
	 */
	public void setCreateacc(String createacc) {
		this.createacc = createacc;
	}
	/**
	 * 获取：创建人
	 */
	public String getCreateacc() {
		return createacc;
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

	public Long getoLicenseTimeLong() {
		return oLicenseTimeLong;
	}

	public void setoLicenseTimeLong(Long oLicenseTimeLong) {
		this.oLicenseTimeLong = oLicenseTimeLong;
	}

	public Long getKeyunitTimeLong() {
		return keyunitTimeLong;
	}

	public void setKeyunitTimeLong(Long keyunitTimeLong) {
		this.keyunitTimeLong = keyunitTimeLong;
	}
}
