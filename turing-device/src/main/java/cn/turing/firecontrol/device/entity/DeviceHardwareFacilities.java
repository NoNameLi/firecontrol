package cn.turing.firecontrol.device.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Table(name = "device_hardware_facilities")
@ApiModel(value = "硬件设施",description = "硬件设施实体表")
public class DeviceHardwareFacilities implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @GeneratedValue(generator = "JDBC")
    @Id
    @Column(name = "ID")
    private Integer id;

    /**
     * 联网单位单位ID
     */
    @ApiModelProperty("联网单位id")
    @Column(name = "OID")
    private Integer oid;

    /**
     * 消火栓名称
     */
    @ApiModelProperty("消火栓名称")
    @Column(name = "HYDRANT_NAME")
    private String hydrantName;

    /**
     * 所属区域
     */
    @ApiModelProperty("所属区域")
    @Column(name = "AREA")
    private String area;

    /**
     * 6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）
     */
    @ApiModelProperty("6位行政区编码")
    @Column(name = "ZXQY")
    private String zxqy;

    /**
     * 设施类型[0=室外消火栓]
     */
    @ApiModelProperty("设施类型[0=室外消火栓]")
    @Column(name = "FACILITY_TYPE")
    private String facilityType;

    /**
     * 消火栓类型[0=地下式/1=地上式/2=直埋伸缩]
     */
    @ApiModelProperty("消火栓类型[0=地下式/1=地上式/2=直埋伸缩]")
    @Column(name = "HYDRANT_TYPE")
    private String hydrantType;

    /**
     * 出水口[0=单口式/1=双口式/2=三出水口式]
     */
    @ApiModelProperty("出水口[0=单口式/1=双口式/2=三出水口式]")
    @Column(name = "OUTLET")
    private String outlet;

    /**
     * 第一出水口类型[0=外螺旋式/1=内扣式]
     */
    @ApiModelProperty("第一出水口类型[0=外螺旋式/1=内扣式]")
    @Column(name = "OUTLET_TYPE_ONE")
    private String outletTypeOne;

    /**
     * 第一出水口数值
     */
    @Max(value = 999,message = "第一出水口请输入0-999的数字")
    @Min(value = 0,message = "第一出水口请输入0-999的数字")
    @ApiModelProperty("第一出水口数值")
    @Column(name = "OUTLET_VALUE_ONE")
    private Integer outletValueOne;

    /**
     * 第二出水口类型[0=外螺旋式/1=内扣式]
     */
    @ApiModelProperty("第二出水口类型[0=外螺旋式/1=内扣式]")
    @Column(name = "OUTLET_TYPE_TWO")
    private String outletTypeTwo;

    /**
     * 第二出水口数值
     */
    @Max(value = 999,message = "第二出水口请输入0-999的数字")
    @Min(value = 0,message = "第二出水口请输入0-999的数字")
    @ApiModelProperty("第二出水口数值")
    @Column(name = "OUTLET_VALUE_TWO")
    private Integer outletValueTwo;

    /**
     * 第三出水口类型[0=外螺旋式/1=内扣式]
     */
    @ApiModelProperty("第三出水口类型[0=外螺旋式/1=内扣式]")
    @Column(name = "OUTLET_TYPE_THREE")
    private String outletTypeThree;

    /**
     * 第三出水口数值
     */
    @Max(value = 999,message = "第三出水口请输入0-999的数字")
    @Min(value = 0,message = "第三出水口请输入0-999的数字")
    @ApiModelProperty("第三出水口数值")
    @Column(name = "OUTLET_VALUE_THREE")
    private Integer outletValueThree;

    /**
     * 保护半径
     */
    @ApiModelProperty("保护半径")
    @Column(name = "PROTECTION_RADIUS")
    private Integer protectionRadius;

    /**
     * 位置描述
     */
    @ApiModelProperty("位置描述")
    @Column(name = "POSITION_DESCRIPTION")
    private String positionDescription;

    /**
     * 水管道[0=高压给水/1=临时高压给水/2=低压给水]
     */
    @ApiModelProperty("水管道[0=高压给水/1=临时高压给水/2=低压给水]")
    @Column(name = "WATER_PIPE")
    private String waterPipe;

    /**
     * 地理坐标经度的十进制表达式
     */
    @ApiModelProperty("地理坐标经度的十进制表达式")
    @Column(name = "GIS")
    private String gis;

    /**
     * 删除标记[1=是/0=否（default）]
     */
    @ApiModelProperty("删除标记[1=是/0=否（default）]")
    @Column(name = "DEL_FLAG")
    private String delFlag;

    /**
     * 创建者名称
     */
    @Column(name = "CRT_USER_NAME")
    private String crtUserName;

    @Column(name = "CRT_USER_ID")
    private String crtUserId;

    /**
     * 创建时间
     */
    @Column(name = "CRT_TIME")
    private Date crtTime;

    /**
     * 修改者名称
     */
    @Column(name = "UPD_USER_NAME")
    private String updUserName;

    @Column(name = "UPD_USER_ID")
    private String updUserId;

    /**
     * 修改时间
     */
    @Column(name = "UPD_TIME")
    private Date updTime;

    @Column(name = "DEPART_ID")
    private String departId;

    @Column(name = "TENANT_ID")
    private String tenantId;




    /**
     * 获取主键ID
     *
     * @return ID - 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取联网单位单位ID
     *
     * @return OID - 联网单位单位ID
     */
    public Integer getOid() {
        return oid;
    }

    /**
     * 设置联网单位单位ID
     *
     * @param oid 联网单位单位ID
     */
    public void setOid(Integer oid) {
        this.oid = oid;
    }

    /**
     * 获取消火栓名称
     *
     * @return HYDRANT_NAME - 消火栓名称
     */
    public String getHydrantName() {
        return hydrantName;
    }

    /**
     * 设置消火栓名称
     *
     * @param hydrantName 消火栓名称
     */
    public void setHydrantName(String hydrantName) {
        this.hydrantName = hydrantName;
    }

    /**
     * 获取所属区域
     *
     * @return AREA - 所属区域
     */
    public String getArea() {
        return area;
    }

    /**
     * 设置所属区域
     *
     * @param area 所属区域
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * 获取6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）
     *
     * @return ZXQY - 6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）
     */
    public String getZxqy() {
        return zxqy;
    }

    /**
     * 设置6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）
     *
     * @param zxqy 6位行政区编码（应符合现行国家标准《中华人民共和国行政区划代码》GB2260的规定）
     */
    public void setZxqy(String zxqy) {
        this.zxqy = zxqy;
    }

    /**
     * 获取设施类型[0=室外消火栓]
     *
     * @return FACILITY_TYPE - 设施类型[0=室外消火栓]
     */
    public String getFacilityType() {
        return facilityType;
    }

    /**
     * 设置设施类型[0=室外消火栓]
     *
     * @param facilityType 设施类型[0=室外消火栓]
     */
    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    /**
     * 获取消火栓类型[0=地下式/1=地上式/2=直埋伸缩]
     *
     * @return HYDRANT_TYPE - 消火栓类型[0=地下式/1=地上式/2=直埋伸缩]
     */
    public String getHydrantType() {
        return hydrantType;
    }

    /**
     * 设置消火栓类型[0=地下式/1=地上式/2=直埋伸缩]
     *
     * @param hydrantType 消火栓类型[0=地下式/1=地上式/2=直埋伸缩]
     */
    public void setHydrantType(String hydrantType) {
        this.hydrantType = hydrantType;
    }

    /**
     * 获取出水口[0=单口式/1=双口式/2=三出水口式]
     *
     * @return OUTLET - 出水口[0=单口式/1=双口式/2=三出水口式]
     */
    public String getOutlet() {
        return outlet;
    }

    /**
     * 设置出水口[0=单口式/1=双口式/2=三出水口式]
     *
     * @param outlet 出水口[0=单口式/1=双口式/2=三出水口式]
     */
    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }

    /**
     * 获取第一出水口类型[0=外螺旋式/1=内扣式]
     *
     * @return OUTLET_TYPE_ONE - 第一出水口类型[0=外螺旋式/1=内扣式]
     */
    public String getOutletTypeOne() {
        return outletTypeOne;
    }

    /**
     * 设置第一出水口类型[0=外螺旋式/1=内扣式]
     *
     * @param outletTypeOne 第一出水口类型[0=外螺旋式/1=内扣式]
     */
    public void setOutletTypeOne(String outletTypeOne) {
        this.outletTypeOne = outletTypeOne;
    }

    /**
     * 获取第一出水口数值
     *
     * @return OUTLET_VALUE_ONE - 第一出水口数值
     */
    public Integer getOutletValueOne() {
        return outletValueOne;
    }

    /**
     * 设置第一出水口数值
     *
     * @param outletValueOne 第一出水口数值
     */
    public void setOutletValueOne(Integer outletValueOne) {
        this.outletValueOne = outletValueOne;
    }

    /**
     * 获取第二出水口类型[0=外螺旋式/1=内扣式]
     *
     * @return OUTLET_TYPE_TWO - 第二出水口类型[0=外螺旋式/1=内扣式]
     */
    public String getOutletTypeTwo() {
        return outletTypeTwo;
    }

    /**
     * 设置第二出水口类型[0=外螺旋式/1=内扣式]
     *
     * @param outletTypeTwo 第二出水口类型[0=外螺旋式/1=内扣式]
     */
    public void setOutletTypeTwo(String outletTypeTwo) {
        this.outletTypeTwo = outletTypeTwo;
    }

    /**
     * 获取第二出水口数值
     *
     * @return OUTLET_VALUE_TWO - 第二出水口数值
     */
    public Integer getOutletValueTwo() {
        return outletValueTwo;
    }

    /**
     * 设置第二出水口数值
     *
     * @param outletValueTwo 第二出水口数值
     */
    public void setOutletValueTwo(Integer outletValueTwo) {
        this.outletValueTwo = outletValueTwo;
    }

    /**
     * 获取第三出水口类型[0=外螺旋式/1=内扣式]
     *
     * @return OUTLET_TYPE_THREE - 第三出水口类型[0=外螺旋式/1=内扣式]
     */
    public String getOutletTypeThree() {
        return outletTypeThree;
    }

    /**
     * 设置第三出水口类型[0=外螺旋式/1=内扣式]
     *
     * @param outletTypeThree 第三出水口类型[0=外螺旋式/1=内扣式]
     */
    public void setOutletTypeThree(String outletTypeThree) {
        this.outletTypeThree = outletTypeThree;
    }

    /**
     * 获取第三出水口数值
     *
     * @return OUTLET_VALUE_THREE - 第三出水口数值
     */
    public Integer getOutletValueThree() {
        return outletValueThree;
    }

    /**
     * 设置第三出水口数值
     *
     * @param outletValueThree 第三出水口数值
     */
    public void setOutletValueThree(Integer outletValueThree) {
        this.outletValueThree = outletValueThree;
    }

    /**
     * 获取保护半径
     *
     * @return PROTECTION_RADIUS - 保护半径
     */
    public Integer getProtectionRadius() {
        return protectionRadius;
    }

    /**
     * 设置保护半径
     *
     * @param protectionRadius 保护半径
     */
    public void setProtectionRadius(Integer protectionRadius) {
        this.protectionRadius = protectionRadius;
    }

    /**
     * 获取位置描述
     *
     * @return POSITION_DESCRIPTION - 位置描述
     */
    public String getPositionDescription() {
        return positionDescription;
    }

    /**
     * 设置位置描述
     *
     * @param positionDescription 位置描述
     */
    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    /**
     * 获取水管道[0=高压给水/1=临时高压给水/2=低压给水]
     *
     * @return WATER_PIPE - 水管道[0=高压给水/1=临时高压给水/2=低压给水]
     */
    public String getWaterPipe() {
        return waterPipe;
    }

    /**
     * 设置水管道[0=高压给水/1=临时高压给水/2=低压给水]
     *
     * @param waterPipe 水管道[0=高压给水/1=临时高压给水/2=低压给水]
     */
    public void setWaterPipe(String waterPipe) {
        this.waterPipe = waterPipe;
    }

    /**
     * 获取地理坐标经度的十进制表达式
     *
     * @return GIS - 地理坐标经度的十进制表达式
     */
    public String getGis() {
        return gis;
    }

    /**
     * 设置地理坐标经度的十进制表达式
     *
     * @param gis 地理坐标经度的十进制表达式
     */
    public void setGis(String gis) {
        this.gis = gis;
    }

    /**
     * 获取删除标记[1=是/0=否（default）]
     *
     * @return DEL_FLAG - 删除标记[1=是/0=否（default）]
     */
    public String getDelFlag() {
        return delFlag;
    }

    /**
     * 设置删除标记[1=是/0=否（default）]
     *
     * @param delFlag 删除标记[1=是/0=否（default）]
     */
    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    /**
     * 获取创建者名称
     *
     * @return CRT_USER_NAME - 创建者名称
     */
    public String getCrtUserName() {
        return crtUserName;
    }

    /**
     * 设置创建者名称
     *
     * @param crtUserName 创建者名称
     */
    public void setCrtUserName(String crtUserName) {
        this.crtUserName = crtUserName;
    }

    /**
     * @return CRT_USER_ID
     */
    public String getCrtUserId() {
        return crtUserId;
    }

    /**
     * @param crtUserId
     */
    public void setCrtUserId(String crtUserId) {
        this.crtUserId = crtUserId;
    }

    /**
     * 获取创建时间
     *
     * @return CRT_TIME - 创建时间
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建时间
     *
     * @param crtTime 创建时间
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * 获取修改者名称
     *
     * @return UPD_USER_NAME - 修改者名称
     */
    public String getUpdUserName() {
        return updUserName;
    }

    /**
     * 设置修改者名称
     *
     * @param updUserName 修改者名称
     */
    public void setUpdUserName(String updUserName) {
        this.updUserName = updUserName;
    }

    /**
     * @return UPD_USER_ID
     */
    public String getUpdUserId() {
        return updUserId;
    }

    /**
     * @param updUserId
     */
    public void setUpdUserId(String updUserId) {
        this.updUserId = updUserId;
    }

    /**
     * 获取修改时间
     *
     * @return UPD_TIME - 修改时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置修改时间
     *
     * @param updTime 修改时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }

    /**
     * @return DEPART_ID
     */
    public String getDepartId() {
        return departId;
    }

    /**
     * @param departId
     */
    public void setDepartId(String departId) {
        this.departId = departId;
    }

    /**
     * @return TENANT_ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

}