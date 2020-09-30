package cn.turing.firecontrol.device.business;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;



public interface BusinessI {
    public  List<Map<String,Object>> querySensorData(JSONObject json) throws ParseException;
    public JSONArray querySensorDataTest(JSONObject json) throws ParseException ;
    public JSONArray querySensorDataDay(JSONObject json) throws ParseException ;
    public JSONArray queryMianSensorData(JSONObject json) throws ParseException ;
    public List<Map<String,Object>> queryElectricityByBuilding(Date startTime, Date endTime, String tenantId);
    public JSONObject queryLastData(String sensorNo);
}
