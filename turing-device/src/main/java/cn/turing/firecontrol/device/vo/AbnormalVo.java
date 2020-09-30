package cn.turing.firecontrol.device.vo;

import java.util.Date;

public class AbnormalVo {
    private String equipmentType;
    private String sensorNo;
    private Double alrmData;
    private String alrmType;
    private Date alrmDate;
    private String unit;
    private String level;
    private String dataUnit;
    private String alrmCategory;
    private String positionDescription;
    private String tenantId;

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getSensorNo() {
        return sensorNo;
    }

    public void setSensorNo(String sensorNo) {
        this.sensorNo = sensorNo;
    }

    public Double getAlrmData() {
        return alrmData;
    }

    public void setAlrmData(Double alrmData) {
        this.alrmData = alrmData;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDataUnit() {
        return dataUnit;
    }

    public void setDataUnit(String dataUnit) {
        this.dataUnit = dataUnit;
    }

    public String getAlrmCategory() {
        return alrmCategory;
    }

    public void setAlrmCategory(String alrmCategory) {
        this.alrmCategory = alrmCategory;
    }

    public String getPositionDescription() {
        return positionDescription;
    }

    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
