package cn.turing.firecontrol.server.business;



import cn.turing.common.entity.DeviceInfo;
import cn.turing.firecontrol.server.entity.SensorDetail;
import cn.turing.firecontrol.server.entity.acrel.AcrelDeviceInfo;
import com.alibaba.fastjson.JSONObject;
import java.util.Map;

public interface BusinessI {
    /**
     * 发送警报
     * @param deviceInfo
     */
    void sendAlarm(String uuid, DeviceInfo deviceInfo);

    /**
     * 消防主机
     * @param uuid
     * @param jsonObject
     */
    void sendAlarm1(String uuid, JSONObject jsonObject);

    /**
     * 升级Redis 中的数据
     * @param deviceInfo
     */
    boolean updateInRedis(DeviceInfo deviceInfo, int timeout_second);

    /**
     *
     * @param deviceInfo
     * @param timeout_second
     * @return
     */
    boolean updateInRedisToAcrel(AcrelDeviceInfo deviceInfo, int timeout_second);
    /**
     * 发送报警消息
     * @param msg
     */
    public void sendSocketMessage(String msg);

    /**
     * 发送报警消息消防主机
     * @param msg
     */
    public void sendSocketMessage1(String msg);
    /**
     * 数据入ES库
     * @param map
     */
    public void insertData(String index, String type, String id, Map<String, Object> map);

    /**
     * 防火门
     * @param deviceInfo
     * @param timeout_second
     * @param uuid
     * @return
     */
    boolean updateInRedisByFireDoor(DeviceInfo deviceInfo, int timeout_second, String uuid);
    /**
     *
     * @param uuid
     * @param deviceInfo
     */
    public void updateStatus(String uuid, DeviceInfo deviceInfo, String status);

    /**
     *
     * @param uuid
     * @param deviceInfo
     * @param status
     */
    public void updateStatusToAcrel(String uuid, AcrelDeviceInfo deviceInfo, String status);
    /**
     * 消防主机
     * @param ip
     * @param port
     * @param loopNo
     * @param localtionNo
     * @param status
     */
    public void updateStatus(String ip, String port, String loopNo, String localtionNo, String status);
    /**
     *
     * @param string
     * @return
     */
    boolean updateInRedisTwo(String string);

    /**
     *
     * @return
     */
    boolean updateInRedisDianQiHuoZai();

    /**
     *
     * @return
     */
    boolean updateInRedisYanGan();

    /**
     *
     * @return
     */
    boolean updateInRedisXiaoHuoShuan();

    /**
     *
     * @return
     */
    boolean updateInRedisTcp();

    /**
     * 推送防火门
     * @param uuid
     * @param deviceInfo
     */
    public void sendFireDoorMessage(String uuid, DeviceInfo deviceInfo);

    /**
     * 推送到衡阳分公司
     * @param string
     */
    public void sendHengYangMessage(String string);

    /**
     * 更新防火门的状态
     * @param sensorNo
     * @param status
     */
    public void updateFireDoorStatus(String sensorNo, Integer status);

    public void sendDeviceAlarmMQ(String uuid,DeviceInfo deviceInfo,String type);

    public void sendDeviceAlarmMQToAcrel(String uuid,AcrelDeviceInfo deviceInfo,String type);
    //恢复异常
    public void abnormalRestore( String sensorNo);
    

    /**
     * 查找redis消防主机的信息
     * @param ip
     * @param port
     * @param loopNo
     * @param localtionNo
     * @return
     */
    JSONObject selectRedisFireMain(String ip,String port,String loopNo,String localtionNo);

    /**
     * 推送消防主机异常记录
     * @param js
     * @param jsonObject
     * @return
     */
    boolean sendMessageFireMain(JSONObject js,JSONObject jsonObject);

    /**
     *
     * @param ip
     * @param port
     * @param loopNo
     * @param localtionNo
     * @param flag
     * @param timeout
     * @param uuid
     * @param sensorDetail
     * @return
     */
    boolean updateFireMainInRedis(String ip, String port, String loopNo, String localtionNo, Boolean flag, int timeout, String uuid, SensorDetail sensorDetail);


    /**
     * 查询配置好的报警值
     * @param deviceId
     * @return
     */
    JSONObject selectMqttSensoralarmValue(String deviceId);
}
