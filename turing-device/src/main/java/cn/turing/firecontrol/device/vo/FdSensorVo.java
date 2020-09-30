package cn.turing.firecontrol.device.vo;

/**
 * 用于传感器列表展示
 */
public class FdSensorVo {
    //传感器ID
    private Long id;
    //防火门ID
    private Long fireDoorId;
    //防火门名称(所属设施)
    private String fireDoorName;
    //状态(报警、正常、未启用、故障、离线)
    private String status;
    //设备编号
    private String sensorNo;
    //设备类型（系列）
    private String equipmentType;
    //厂商
    private String manufacturer;
    //型号
    private String model;
    //所属建筑ID
    private String buildingId;
    //所属建筑名称
    private String buildingName;
    //位置描述
    private String positionDescription;
    //传感器位置
    private String positionSign;
    //门磁传感器的状态
    private String doorStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFireDoorId() {
        return fireDoorId;
    }

    public void setFireDoorId(Long fireDoorId) {
        this.fireDoorId = fireDoorId;
    }

    public String getFireDoorName() {
        return fireDoorName;
    }

    public void setFireDoorName(String fireDoorName) {
        this.fireDoorName = fireDoorName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSensorNo() {
        return sensorNo;
    }

    public void setSensorNo(String sensorNo) {
        this.sensorNo = sensorNo;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getPositionDescription() {
        return positionDescription;
    }

    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    public String getPositionSign() {
        return positionSign;
    }

    public void setPositionSign(String positionSign) {
        this.positionSign = positionSign;
    }

    public String getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(String doorStatus) {
        this.doorStatus = doorStatus;
    }
}
