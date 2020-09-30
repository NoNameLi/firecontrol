package cn.turing.firecontrol.device.dto;

public class FireDoorSensorDto {
    //传感器ID
    private Long id;
    //防火门编号
    private Long fireDoorId;
    //设备编号
    private String sensorNo;
    //设备类型（系列）
    private String equipmentType;
    //厂商
    private String manufacturer;
    //型号
    private String model;

    public Long getFireDoorId() {
        return fireDoorId;
    }

    public void setFireDoorId(Long fireDoorId) {
        this.fireDoorId = fireDoorId;
    }

    public String getSensorNo() {
        return sensorNo;
    }

    public void setSensorNo(String sensorNo) {
        this.sensorNo = sensorNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
