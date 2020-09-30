package cn.turing.firecontrol.server.bean;


import java.util.Date;

/**
 * 查询包
 */
public class QueryPacket {
   private  String data;//数据
   private  String equipment_id;//设备id
   private  String functional_code ;//功能码
   private  String number_nodes ;//节点数量
   private  String loopNo;//回路
   private  String localtionNo;//地址
   private  String device_id;//节点编号
   private  String address; //具体位置
   private  String alarmType;//报警类型
   private  String floor; //楼层
   private  Date updata_time;
   private  Date receive_time;
   private  String device_type; //设备类型
    private boolean has_Alarm;
    private boolean has_GuZhang;
    private  String ip;
    private String port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isHas_Alarm() {
        return has_Alarm;
    }

    public void setHas_Alarm(boolean has_Alarm) {
        this.has_Alarm = has_Alarm;
    }

    public boolean isHas_GuZhang() {
        return has_GuZhang;
    }

    public void setHas_GuZhang(boolean has_GuZhang) {
        this.has_GuZhang = has_GuZhang;
    }

    public Date getUpdata_time() {
        return updata_time;
    }

    public void setUpdata_time(Date updata_time) {
        this.updata_time = updata_time;
    }

    public Date getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(Date receive_time) {
        this.receive_time = receive_time;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEquipment_id() {
        return equipment_id;
    }

    public void setEquipment_id(String equipment_id) {
        this.equipment_id = equipment_id;
    }

    public String getFunctional_code() {
        return functional_code;
    }

    public void setFunctional_code(String functional_code) {
        this.functional_code = functional_code;
    }

    public String getNumber_nodes() {
        return number_nodes;
    }

    public void setNumber_nodes(String number_nodes) {
        this.number_nodes = number_nodes;
    }

    public String getLoopNo() {
        return loopNo;
    }

    public void setLoopNo(String loopNo) {
        this.loopNo = loopNo;
    }

    public String getLocaltionNo() {
        return localtionNo;
    }

    public void setLocaltionNo(String localtionNo) {
        this.localtionNo = localtionNo;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

}
