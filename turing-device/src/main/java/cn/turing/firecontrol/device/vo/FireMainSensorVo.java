package cn.turing.firecontrol.device.vo;

public class FireMainSensorVo {
    //传感器ID
    private Long id;
    //消防主机ID
    private Integer fireMainId;
    //消防主机IP
    private String serverIp;
    //消防主机端口
    private String port;
    //所属建筑ID
    private Integer buildingId;

    //所属建筑名称
    private String buildingName;

    //所属楼层
    private Integer floor;

    /**
     * 所属系统id
     */
    private Integer channelId;

    /**
     * 系列
     */
    private String series;

    /**
     * 回路
     */
    private String sensorLoop;

    /**
     * 地址
     */
    private String address;


    /**
     * 获取主键id
     *
     * @return ID - 主键id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键id
     *
     * @param id 主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取建筑列表id
     *
     * @return BUILDING_ID - 建筑列表id
     */
    public Integer getBuildingId() {
        return buildingId;
    }

    /**
     * 设置建筑列表id
     *
     * @param buildingId 建筑列表id
     */
    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    /**
     * 获取消防主机id
     *
     * @return FIRE_MAIN_ID - 消防主机id
     */
    public Integer getFireMainId() {
        return fireMainId;
    }

    /**
     * 设置消防主机id
     *
     * @param fireMainId 消防主机id
     */
    public void setFireMainId(Integer fireMainId) {
        this.fireMainId = fireMainId;
    }

    /**
     * 获取所属系统id
     *
     * @return CHANNEL_ID - 所属系统id
     */
    public Integer getChannelId() {
        return channelId;
    }

    /**
     * 设置所属系统id
     *
     * @param channelId 所属系统id
     */
    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    /**
     * 获取系列
     *
     * @return SERIES - 系列
     */
    public String getSeries() {
        return series;
    }

    /**
     * 设置系列
     *
     * @param series 系列
     */
    public void setSeries(String series) {
        this.series = series;
    }

    public String getSensorLoop() {
        return sensorLoop;
    }

    public void setSensorLoop(String sensorLoop) {
        this.sensorLoop = sensorLoop;
    }

    /**
     * 获取地址
     *
     * @return ADDRESS - 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置地址
     *
     * @param address 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取楼层
     *
     * @return FLOOR - 楼层
     */
    public Integer getFloor() {
        return floor;
    }

    /**
     * 设置楼层
     *
     * @param floor 楼层
     */
    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    private String  positionDescription;

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPositionDescription() {
        return positionDescription;
    }

    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
}
