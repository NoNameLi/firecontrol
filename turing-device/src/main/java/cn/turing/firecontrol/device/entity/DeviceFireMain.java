package cn.turing.firecontrol.device.entity;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import javax.persistence.*;

@Table(name = "device_fire_main")
public class DeviceFireMain {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    private Integer id;

    /**
     * 服务器IP
     */
    @ApiModelProperty("服务器IP")
    @Column(name = "SERVER_IP")
    private String serverIp;

    /**
     * 端口号
     */
    @ApiModelProperty("端口号")
    @Column(name = "PORT")
    private String port;

    /**
     * 地理坐标经度的十进制表达式
     */
    @ApiModelProperty("地理坐标经度的十进制表达式")
    @Column(name = "GIS")
    private String gis;

    /**
     * 位置描述
     */
    @ApiModelProperty("位置描述")
    @Column(name = "POSITION_DESCRIPTION")
    private String positionDescription;

    /**
     * 删除标记[1=是/0=否（default）]
     */
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
     * 获取服务器IP
     *
     * @return SERVER_IP - 服务器IP
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * 设置服务器IP
     *
     * @param serverIp 服务器IP
     */
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * 获取端口号
     *
     * @return PORT - 端口号
     */
    public String getPort() {
        return port;
    }

    /**
     * 设置端口号
     *
     * @param port 端口号
     */
    public void setPort(String port) {
        this.port = port;
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