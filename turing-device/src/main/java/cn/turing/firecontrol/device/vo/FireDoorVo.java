package cn.turing.firecontrol.device.vo;

import cn.turing.firecontrol.device.entity.DeviceFireDoor;

public class FireDoorVo extends  DeviceFireDoor{

    //所属建筑名称
    private String buildingName;

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
}
