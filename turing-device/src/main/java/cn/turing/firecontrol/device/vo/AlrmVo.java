package cn.turing.firecontrol.device.vo;

import java.util.Date;

/**
 * @author: zhangpeng
 * @email: 723308025@qq.com
 * @create: 2018-09-25 17:21
 **/
public class AlrmVo {
    private Integer id;
    private Integer channelId;
    private String name;
    private Integer floor;
    private String equipmentType;
    private String measuringPoint;
    private String alrmType;
    private Date alrmDate;
    private String logId;
    private String facilityType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getMeasuringPoint() {
        return measuringPoint;
    }

    public void setMeasuringPoint(String measuringPoint) {
        this.measuringPoint = measuringPoint;
    }

    public String getAlrmType() {
        return alrmType;
    }

    public void setAlrmType(String alrmType) {
        this.alrmType = alrmType;
    }

    public Date getAlrmDate() {
        return alrmDate;
    }

    public void setAlrmDate(Date alrmDate) {
        this.alrmDate = alrmDate;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }
}
