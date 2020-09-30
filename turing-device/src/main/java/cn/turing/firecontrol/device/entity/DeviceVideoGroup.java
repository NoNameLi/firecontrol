package cn.turing.firecontrol.device.entity;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "device_video_group")
public class DeviceVideoGroup implements Serializable {
    /**
     * 主键id
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 设备组名称
     */
    @NotBlank(message = "设备组名称不能为空")
    @Column(name = "DEVICE_GROUP_NAME")
    @Length(max = 50,message = "设备组名称不能超过50个字符")
    private String deviceGroupName;

    /**
     * 设备组平面图URL
     */
    @Column(name = "DEVICE_GROUP_IMAGE")
    private String deviceGroupImage;

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

    /**
     * 创建者ID
     */
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

    /**
     * 更新者ID
     */
    @Column(name = "UPD_USER_ID")
    private String updUserId;

    /**
     * 修改时间
     */
    @Column(name = "UPD_TIME")
    private Date updTime;

    /**
     * 部门ID
     */
    @Column(name = "DEPART_ID")
    private String departId;

    /**
     * 租户ID
     */
    @Column(name = "TENANT_ID")
    private String tenantId;

    /**
     * 获取主键id
     *
     * @return ID - 主键id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键id
     *
     * @param id 主键id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取设备组名称
     *
     * @return DEVICE_GROUP_NAME - 设备组名称
     */
    public String getDeviceGroupName() {
        return deviceGroupName;
    }

    /**
     * 设置设备组名称
     *
     * @param deviceGroupName 设备组名称
     */
    public void setDeviceGroupName(String deviceGroupName) {
        this.deviceGroupName = deviceGroupName;
    }

    /**
     * 获取设备组平面图URL
     *
     * @return DEVICE_GROUP_IMAGE - 设备组平面图URL
     */
    public String getDeviceGroupImage() {
        return deviceGroupImage;
    }

    /**
     * 设置设备组平面图URL
     *
     * @param deviceGroupImage 设备组平面图URL
     */
    public void setDeviceGroupImage(String deviceGroupImage) {
        this.deviceGroupImage = deviceGroupImage;
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
     * 获取创建者ID
     *
     * @return CRT_USER_ID - 创建者ID
     */
    public String getCrtUserId() {
        return crtUserId;
    }

    /**
     * 设置创建者ID
     *
     * @param crtUserId 创建者ID
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
     * 获取更新者ID
     *
     * @return UPD_USER_ID - 更新者ID
     */
    public String getUpdUserId() {
        return updUserId;
    }

    /**
     * 设置更新者ID
     *
     * @param updUserId 更新者ID
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
     * 获取部门ID
     *
     * @return DEPART_ID - 部门ID
     */
    public String getDepartId() {
        return departId;
    }

    /**
     * 设置部门ID
     *
     * @param departId 部门ID
     */
    public void setDepartId(String departId) {
        this.departId = departId;
    }

    /**
     * 获取租户ID
     *
     * @return TENANT_ID - 租户ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}