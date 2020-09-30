package cn.turing.firecontrol.device.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

@Table(name = "device_video_ext")
public class DeviceVideoExt extends DeviceSensor{


    /**
     * 设备组ID
     */
    @NotNull(message = "设备组ID不能为空")
    @Digits(integer = Integer.MAX_VALUE,fraction = 0,message = "设备组ID错误")
    @Column(name = "DEVICE_GROUP_ID")
    private Integer deviceGroupId;

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名不能为空")
    @Column(name = "DEVICE_VIDEO_NAME")
    @Length(max = 50,message = "设备名字符长度超过50")
    private String deviceVideoName;

    /**
     * 设备验证码
     */
    @NotBlank(message = "设备验证码不能为空")
    @Column(name = "DEVICE_VALIDATE_CODE")
    private String deviceValidateCode;

    /**
     * 显示标记[1=是/0=否（default）]
     */
    @Column(name = "SHOW_FLAG")
    private String showFlag;

    /**
     * 解析方案ID
     */
    @Column(name = "ANALYSIS_SOLUTION_ID")
    private Integer analysisSolutionId;

    /**
     * 设备视频播放地址JSON
     */
    @Column(name = "VIDEO_LIVE_ADDRESS")
    private String videoLiveAddress;

    /**
     * 告警状态信息JSON
     */
    @Column(name = "ALARM_MSG")
    private String alarmMsg;

    @NotNull
    public Integer getDeviceGroupId() {
        return deviceGroupId;
    }

    public void setDeviceGroupId(@NotNull Integer deviceGroupId) {
        this.deviceGroupId = deviceGroupId;
    }

    /**
     * 获取设备名称
     *
     * @return DEVICE_VIDEO_NAME - 设备名称
     */
    public String getDeviceVideoName() {
        return deviceVideoName;
    }

    /**
     * 设置设备名称
     *
     * @param deviceVideoName 设备名称
     */
    public void setDeviceVideoName(String deviceVideoName) {
        this.deviceVideoName = deviceVideoName;
    }

    /**
     * 获取设备验证码
     *
     * @return DEVICE_VALIDATE_CODE - 设备验证码
     */
    public String getDeviceValidateCode() {
        return deviceValidateCode;
    }

    /**
     * 设置设备验证码
     *
     * @param deviceValidateCode 设备验证码
     */
    public void setDeviceValidateCode(String deviceValidateCode) {
        this.deviceValidateCode = deviceValidateCode;
    }

    /**
     * 获取显示标记[1=是/0=否（default）]
     *
     * @return SHOW_FLAG - 显示标记[1=是/0=否（default）]
     */
    public String getShowFlag() {
        return showFlag;
    }

    /**
     * 设置显示标记[1=是/0=否（default）]
     *
     * @param showFlag 显示标记[1=是/0=否（default）]
     */
    public void setShowFlag(String showFlag) {
        this.showFlag = showFlag;
    }

    /**
     * 获取显示标记[1=是/0=否（default）]
     *
     * @return ANALYSIS_SOLUTION_ID - 显示标记[1=是/0=否（default）]
     */
    public Integer getAnalysisSolutionId() {
        return analysisSolutionId;
    }

    /**
     * 设置显示标记[1=是/0=否（default）]
     *
     * @param analysisSolutionId 显示标记[1=是/0=否（default）]
     */
    public void setAnalysisSolutionId(Integer analysisSolutionId) {
        this.analysisSolutionId = analysisSolutionId;
    }

    /**
     * 获取设备视频播放地址JSON
     *
     * @return VIDEO_LIVE_ADDRESS - 设备视频播放地址JSON
     */
    public String getVideoLiveAddress() {
        return videoLiveAddress;
    }

    /**
     * 设置设备视频播放地址JSON
     *
     * @param videoLiveAddress 设备视频播放地址JSON
     */
    public void setVideoLiveAddress(String videoLiveAddress) {
        this.videoLiveAddress = videoLiveAddress;
    }

    public String getAlarmMsg() {
        return alarmMsg;
    }

    public void setAlarmMsg(String alarmMsg) {
        this.alarmMsg = alarmMsg;
    }

    //移除父类的属性值
    public DeviceVideoExt toOnlyExt(){
        DeviceVideoExt ext = new DeviceVideoExt();
        ext.setId(this.getId());
        ext.setVideoLiveAddress(this.videoLiveAddress);
        ext.setDeviceVideoName(this.deviceVideoName);
        ext.setAnalysisSolutionId(analysisSolutionId);
        ext.setDeviceGroupId(this.deviceGroupId);
        ext.setDeviceValidateCode(this.deviceValidateCode);
        ext.setShowFlag(this.showFlag);
        ext.setSensorNo(this.getSensorNo());
        ext.setAlarmMsg(this.alarmMsg);
        return ext;
    }


}