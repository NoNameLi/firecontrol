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
@Table(name = "device_building")
public class DeviceBuilding implements Serializable {
	private static final long serialVersionUID = 1L;

		//主键id
	@GeneratedValue(generator = "JDBC")
	@Id
	private Integer id;
	
	    //建筑物ID（字段无意义，暂时保留）
    @Column(name = "BID")
    private Integer bid;
	
	    //建筑管理单位ID
    @Column(name = "OID")
    private Integer oid;
	
	    //建筑名称
    @Column(name = "B_NAME")
    private String bName;
	
	    //建筑地址
    @Column(name = "B_ADDRESS")
    private String bAddress;
	
	    //6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）
    @Column(name = "ZXQY")
    private String zxqy;
	
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

	    //联系人
    @Column(name = "LINKMAN")
    private String linkman;
	
	    //联系电话
    @Column(name = "LINKPHONE")
    private String linkphone;
	
	    //建筑情况[1=使用中,0未使用]
    @Column(name = "B_STATE")
    private String bState;
	
	    //竣工时间
    @Column(name = "B_TIME")
    private Date bTime;

	//竣工时间 字符串
	@Transient
	private Long bTimeLong;
	
	    //建筑产权及使用情况[0=独家产权，独立使用、1=独立产权，多家使用、2=多家产权，多家使用]
    @Column(name = "PROPERT_RIGHT")
    private String propertRight;
	
	    //建筑面积
    @Column(name = "B_AREA")
    private Double bArea;
	
	    //占地面积
    @Column(name = "B_ZD_AREA")
    private Double bZdArea;
	
	    //建筑高度
    @Column(name = "B_HIGHT")
    private Double bHight;
	
	    //标准层面积
    @Column(name = "B_ZC_AREA")
    private Double bZcArea;
	
	    //地上层数
    @Column(name = "UP_FLOOR")
    private Integer upFloor;
	
	    //地上面积
    @Column(name = "UP_FLOOR_AREA")
    private Double upFloorArea;
	
	    //地下层数
    @Column(name = "UNDER_FLOOR")
    private Integer underFloor;
	
	    //地下面积
    @Column(name = "UNDER_FLOOR_AREA")
    private Double underFloorArea;
	
	    //建筑分类
    @Column(name = "B_STORE")
    private String bStore;
	
	    //建筑结构
    @Column(name = "B_STRTURE")
    private String bStrture;
	
	    //建筑其他结构
    @Column(name = "B_STRTURE1")
    private String bStrture1;
	
	    //消防控制室位置
    @Column(name = "CTRLROOM_PLACE")
    private String ctrlroomPlace;
	
	    //耐火等级
    @Column(name = "FIRE_RATE")
    private String fireRate;
	
	    //火灾危险性
    @Column(name = "FIRE_DANGER")
    private String fireDanger;
	
	    //最大容纳人数
    @Column(name = "MOSTWORKERR")
    private Integer mostworkerr;
	
	    //消防电梯数
    @Column(name = "LIFT_COUNT")
    private Integer liftCount;
	
	    //消防电梯位置
    @Column(name = "LIFT_PLACE")
    private String liftPlace;
	
	    //避难层数量
    @Column(name = "REFUGE_NUMBER")
    private Integer refugeNumber;
	
	    //避难层面积
    @Column(name = "REFUGE_AREA")
    private Double refugeArea;
	
	    //避难层位置
    @Column(name = "REFUGE_PLACE")
    private String refugePlace;
	
	    //入住使用功能
    @Column(name = "USE_KIND")
    private String useKind;
	
	    //是否有自动消防设施[0=无、1=有]
    @Column(name = "HAVE_FIREPROOF")
    private String haveFireproof;
	
	    //消防设施
    @Column(name = "XFSS")
    private String xfss;
	
	    //其他消防设施
    @Column(name = "XFSS_OTHER")
    private String xfssOther;
	
	    //设施完好情况[1=合格；2=不合格]
    @Column(name = "XFSS_INTACT")
    private String xfssIntact;
	
	    //毗邻建筑情况
    @Column(name = "NEAR_BUILDING")
    private String nearBuilding;
	
	    //地理情况
    @Column(name = "GEOG_INFO")
    private String geogInfo;
	
	    //消防控制室情况[0=无；1=有]
    @Column(name = "HAVE_CTRLROOM")
    private String haveCtrlroom;
	
	    //建筑用途分类
    @Column(name = "USE_TYPE")
    private String useType;
	
	    //消防管辖单位（默认-1）
    @Column(name = "SYS_ORGAN_ID")
    private Integer sysOrganId;
	
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

    @Transient
    private Double imageX;
	@Transient
    private Double imageY;

	public Double getImageX() {
		return imageX;
	}

	public void setImageX(Double imageX) {
		this.imageX = imageX;
	}

	public Double getImageY() {
		return imageY;
	}

	public void setImageY(Double imageY) {
		this.imageY = imageY;
	}

	/**
	 * 设置：主键ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：主键ID
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置：建筑物ID（字段无意义，暂时保留）
	 */
	public void setBid(Integer bid) {
		this.bid = bid;
	}
	/**
	 * 获取：建筑物ID（字段无意义，暂时保留）
	 */
	public Integer getBid() {
		return bid;
	}
	/**
	 * 设置：建筑管理单位ID
	 */
	public void setOid(Integer oid) {
		this.oid = oid;
	}
	/**
	 * 获取：建筑管理单位ID
	 */
	public Integer getOid() {
		return oid;
	}
	/**
	 * 设置：建筑名称
	 */
	public void setBName(String bName) {
		this.bName = bName;
	}
	/**
	 * 获取：建筑名称
	 */
	public String getBName() {
		return bName;
	}
	/**
	 * 设置：建筑地址
	 */
	public void setBAddress(String bAddress) {
		this.bAddress = bAddress;
	}
	/**
	 * 获取：建筑地址
	 */
	public String getBAddress() {
		return bAddress;
	}
	/**
	 * 设置：6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）
	 */
	public void setZxqy(String zxqy) {
		this.zxqy = zxqy;
	}
	/**
	 * 获取：6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）
	 */
	public String getZxqy() {
		return zxqy;
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
	 * 设置：联系人
	 */
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	/**
	 * 获取：联系人
	 */
	public String getLinkman() {
		return linkman;
	}
	/**
	 * 设置：联系电话
	 */
	public void setLinkphone(String linkphone) {
		this.linkphone = linkphone;
	}
	/**
	 * 获取：联系电话
	 */
	public String getLinkphone() {
		return linkphone;
	}
	/**
	 * 设置：建筑情况[1=使用中,0未使用]
	 */
	public void setBState(String bState) {
		this.bState = bState;
	}
	/**
	 * 获取：建筑情况[1=使用中,0未使用]
	 */
	public String getBState() {
		return bState;
	}
	/**
	 * 设置：竣工时间
	 */
	public void setBTime(Date bTime) {
		this.bTime = bTime;
	}
	/**
	 * 获取：竣工时间
	 */
	public Date getBTime() {
		return bTime;
	}
	/**
	 * 设置：建筑产权及使用情况[0=独家产权，独立使用、1=独立产权，多家使用、2=多家产权，多家使用]
	 */
	public void setPropertRight(String propertRight) {
		this.propertRight = propertRight;
	}
	/**
	 * 获取：建筑产权及使用情况[0=独家产权，独立使用、1=独立产权，多家使用、2=多家产权，多家使用]
	 */
	public String getPropertRight() {
		return propertRight;
	}
	/**
	 * 设置：建筑面积
	 */
	public void setBArea(Double bArea) {
		this.bArea = bArea;
	}
	/**
	 * 获取：建筑面积
	 */
	public Double getBArea() {
		return bArea;
	}
	/**
	 * 设置：占地面积
	 */
	public void setBZdArea(Double bZdArea) {
		this.bZdArea = bZdArea;
	}
	/**
	 * 获取：占地面积
	 */
	public Double getBZdArea() {
		return bZdArea;
	}
	/**
	 * 设置：建筑高度
	 */
	public void setBHight(Double bHight) {
		this.bHight = bHight;
	}
	/**
	 * 获取：建筑高度
	 */
	public Double getBHight() {
		return bHight;
	}
	/**
	 * 设置：标准层面积
	 */
	public void setBZcArea(Double bZcArea) {
		this.bZcArea = bZcArea;
	}
	/**
	 * 获取：标准层面积
	 */
	public Double getBZcArea() {
		return bZcArea;
	}
	/**
	 * 设置：地上层数
	 */
	public void setUpFloor(Integer upFloor) {
		this.upFloor = upFloor;
	}
	/**
	 * 获取：地上层数
	 */
	public Integer getUpFloor() {
		return upFloor;
	}
	/**
	 * 设置：地上面积
	 */
	public void setUpFloorArea(Double upFloorArea) {
		this.upFloorArea = upFloorArea;
	}
	/**
	 * 获取：地上面积
	 */
	public Double getUpFloorArea() {
		return upFloorArea;
	}
	/**
	 * 设置：地下层数
	 */
	public void setUnderFloor(Integer underFloor) {
		this.underFloor = underFloor;
	}
	/**
	 * 获取：地下层数
	 */
	public Integer getUnderFloor() {
		return underFloor;
	}
	/**
	 * 设置：地下面积
	 */
	public void setUnderFloorArea(Double underFloorArea) {
		this.underFloorArea = underFloorArea;
	}
	/**
	 * 获取：地下面积
	 */
	public Double getUnderFloorArea() {
		return underFloorArea;
	}
	/**
	 * 设置：建筑分类
	 */
	public void setBStore(String bStore) {
		this.bStore = bStore;
	}
	/**
	 * 获取：建筑分类
	 */
	public String getBStore() {
		return bStore;
	}
	/**
	 * 设置：建筑结构
	 */
	public void setBStrture(String bStrture) {
		this.bStrture = bStrture;
	}
	/**
	 * 获取：建筑结构
	 */
	public String getBStrture() {
		return bStrture;
	}
	/**
	 * 设置：建筑其他结构
	 */
	public void setBStrture1(String bStrture1) {
		this.bStrture1 = bStrture1;
	}
	/**
	 * 获取：建筑其他结构
	 */
	public String getBStrture1() {
		return bStrture1;
	}
	/**
	 * 设置：消防控制室位置
	 */
	public void setCtrlroomPlace(String ctrlroomPlace) {
		this.ctrlroomPlace = ctrlroomPlace;
	}
	/**
	 * 获取：消防控制室位置
	 */
	public String getCtrlroomPlace() {
		return ctrlroomPlace;
	}
	/**
	 * 设置：耐火等级
	 */
	public void setFireRate(String fireRate) {
		this.fireRate = fireRate;
	}
	/**
	 * 获取：耐火等级
	 */
	public String getFireRate() {
		return fireRate;
	}
	/**
	 * 设置：火灾危险性
	 */
	public void setFireDanger(String fireDanger) {
		this.fireDanger = fireDanger;
	}
	/**
	 * 获取：火灾危险性
	 */
	public String getFireDanger() {
		return fireDanger;
	}
	/**
	 * 设置：最大容纳人数
	 */
	public void setMostworkerr(Integer mostworkerr) {
		this.mostworkerr = mostworkerr;
	}
	/**
	 * 获取：最大容纳人数
	 */
	public Integer getMostworkerr() {
		return mostworkerr;
	}
	/**
	 * 设置：消防电梯数
	 */
	public void setLiftCount(Integer liftCount) {
		this.liftCount = liftCount;
	}
	/**
	 * 获取：消防电梯数
	 */
	public Integer getLiftCount() {
		return liftCount;
	}
	/**
	 * 设置：消防电梯位置
	 */
	public void setLiftPlace(String liftPlace) {
		this.liftPlace = liftPlace;
	}
	/**
	 * 获取：消防电梯位置
	 */
	public String getLiftPlace() {
		return liftPlace;
	}
	/**
	 * 设置：避难层数量
	 */
	public void setRefugeNumber(Integer refugeNumber) {
		this.refugeNumber = refugeNumber;
	}
	/**
	 * 获取：避难层数量
	 */
	public Integer getRefugeNumber() {
		return refugeNumber;
	}
	/**
	 * 设置：避难层面积
	 */
	public void setRefugeArea(Double refugeArea) {
		this.refugeArea = refugeArea;
	}
	/**
	 * 获取：避难层面积
	 */
	public Double getRefugeArea() {
		return refugeArea;
	}
	/**
	 * 设置：避难层位置
	 */
	public void setRefugePlace(String refugePlace) {
		this.refugePlace = refugePlace;
	}
	/**
	 * 获取：避难层位置
	 */
	public String getRefugePlace() {
		return refugePlace;
	}
	/**
	 * 设置：入住使用功能
	 */
	public void setUseKind(String useKind) {
		this.useKind = useKind;
	}
	/**
	 * 获取：入住使用功能
	 */
	public String getUseKind() {
		return useKind;
	}
	/**
	 * 设置：是否有自动消防设施[0=无、1=有]
	 */
	public void setHaveFireproof(String haveFireproof) {
		this.haveFireproof = haveFireproof;
	}
	/**
	 * 获取：是否有自动消防设施[0=无、1=有]
	 */
	public String getHaveFireproof() {
		return haveFireproof;
	}
	/**
	 * 设置：消防设施
	 */
	public void setXfss(String xfss) {
		this.xfss = xfss;
	}
	/**
	 * 获取：消防设施
	 */
	public String getXfss() {
		return xfss;
	}
	/**
	 * 设置：其他消防设施
	 */
	public void setXfssOther(String xfssOther) {
		this.xfssOther = xfssOther;
	}
	/**
	 * 获取：其他消防设施
	 */
	public String getXfssOther() {
		return xfssOther;
	}
	/**
	 * 设置：设施完好情况[1=合格；2=不合格]
	 */
	public void setXfssIntact(String xfssIntact) {
		this.xfssIntact = xfssIntact;
	}
	/**
	 * 获取：设施完好情况[1=合格；2=不合格]
	 */
	public String getXfssIntact() {
		return xfssIntact;
	}
	/**
	 * 设置：毗邻建筑情况
	 */
	public void setNearBuilding(String nearBuilding) {
		this.nearBuilding = nearBuilding;
	}
	/**
	 * 获取：毗邻建筑情况
	 */
	public String getNearBuilding() {
		return nearBuilding;
	}
	/**
	 * 设置：地理情况
	 */
	public void setGeogInfo(String geogInfo) {
		this.geogInfo = geogInfo;
	}
	/**
	 * 获取：地理情况
	 */
	public String getGeogInfo() {
		return geogInfo;
	}
	/**
	 * 设置：消防控制室情况[0=无；1=有]
	 */
	public void setHaveCtrlroom(String haveCtrlroom) {
		this.haveCtrlroom = haveCtrlroom;
	}
	/**
	 * 获取：消防控制室情况[0=无；1=有]
	 */
	public String getHaveCtrlroom() {
		return haveCtrlroom;
	}
	/**
	 * 设置：建筑用途分类
	 */
	public void setUseType(String useType) {
		this.useType = useType;
	}
	/**
	 * 获取：建筑用途分类
	 */
	public String getUseType() {
		return useType;
	}
	/**
	 * 设置：消防管辖单位（默认-1）
	 */
	public void setSysOrganId(Integer sysOrganId) {
		this.sysOrganId = sysOrganId;
	}
	/**
	 * 获取：消防管辖单位（默认-1）
	 */
	public Integer getSysOrganId() {
		return sysOrganId;
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

	public Long getbTimeLong() {
		return bTimeLong;
	}

	public void setbTimeLong(Long bTimeLong) {
		this.bTimeLong = bTimeLong;
	}

}
