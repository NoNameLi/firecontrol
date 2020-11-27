package cn.turing.firecontrol.datahandler.business;

import cn.turing.firecontrol.datahandler.entity.DeviceSensor;
import cn.turing.firecontrol.datahandler.listener.abnormalHandler.AbstractAbnormalHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface BusinessI {

  public static final String REDIS_ABNORMAL_HASH = "device:abnormals";

  //查询传感器信息
  public JSONObject getSensor(String deviceId);

  //查询管理员信息
    public JSONObject getAdmin(String tenantId);
    //查询建筑屋信息
    public JSONObject getArchitect(Integer buildingId);
  //查询传感器状态
  public JSONObject getSensorType(Integer sensorTypeId);
  //查询测点信息
  public JSONArray getPoint(Integer sensorId);
    //查询硬件设施信息
  public JSONObject getHardware(Integer hydrantId);

  /**
   * 数据入ES库
   * @param map
   */
  public void insertData(String index,String type,String id,Map<String, Object> map);

  /**
   * 升级传感器状态
   * @param sensorNo
   * @param statusTime
   * @param status
   */
  public void updataSensor(String sensorNo,String statusTime,String status);

  /**
   * 数据入常规库
   */
  public List<Integer> insertException(Integer equID,Integer buildId,Integer channelId,Date uploadTime,String tenantId,String logid,JSONObject warningMap) ;

  /**
   *
   * @param sensorNo
   * @param alemData
   * @param codeName
   * @return
   */
  public JSONObject selectAlarmLevel(String sensorNo, Double alemData,String codeName,String tenantId);

//    public boolean insterList(String key, List<Object> list);
//    //查询未推送的消息
//    public Map<String,Object> queryWarningData(Map<String, Object> map);
//    //修改传感器状态
//    public TreeSet<String> keys(String pattern);
//    //查询传感器实时数据
//    public  List<Map<String,Object>> querySensorData(JSONObject json);
  public void alertMSG(String data) throws Exception;

  /**
   * 数据入常规库
   */
  public List<Integer> insert(Integer equID,Integer hydrantId,Integer channelId,Date uploadTime,String tenantId,String logid,JSONObject warningMap) ;

  public void alertFireMainMSG(String data) throws Exception;

  /**
   * 查询消防主机传感器信息
   * @param serverIp
   * @param port
   * @param sensorLoop
   * @param address
   * @return
   */
  public JSONObject getFireMainSensor(String serverIp,String port,String sensorLoop,String address);

  /**
   * 修改消防主机传感器状态
   * @param serverIp
   * @param statusTime
   * @param status
   */
  public void updataFireMainSensor(String serverIp,String port,String status,String statusTime,String sensorLoop,String address);

  public List<Integer> insertFireMainException(Long equID,Integer hydrantId,Integer channelId,Date uploadTime,String tenantId,String logid,JSONObject warningMap) ;

  /**
   * 恢复异常信息
   * @param sensor
   * @param alarmCodes
   */
  public void restoreAbnormal(DeviceSensor sensor, List<AbstractAbnormalHandler.Alarm> alarmCodes);

}
