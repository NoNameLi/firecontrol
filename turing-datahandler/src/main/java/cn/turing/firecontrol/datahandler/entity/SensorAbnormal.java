package cn.turing.firecontrol.datahandler.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/03/07 14:32
 *
 * @Description
 * @Version V1.0
 */
@Data
public class SensorAbnormal extends ElasticSearchEntity{

    private String id;
    //建筑物名称
    private String buildingName;
    //楼层
    private Integer floor;
    //位置描述
    private String address;
    //设备编号
    private String sensorNo;
    //设备系列
    private String deviceSeries;
    //设备厂商名称
    private String manufacturer;
    //设备型号
    private String model;
    //测点
    private String measuringPoint;
    //报警类型
    private String alarmType;
    //报警时间
    private Date alarmTime;
    //异常值
    private String alarmValue;
    //恢复时间
    private Date restoreTime;
    //异常类型: 0故障，1报警，2正常
    private Integer abnormalType;
    //是否已处理
    private Boolean isHandle;
    //处理时间
    private Date handleTime;
    //处理结果:0故障，1真实火警，2火警测试，3误报
    private Integer handleResult;
    //处理人真实姓名
    private String handlePersonName;
    //租户ID
    private String tenantId;
    //sensor_data索引的ID
    private List<String> logIds;


    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("buildingName",buildingName);
        map.put("floor",floor);
        map.put("address",address);
        map.put("sensorNo",sensorNo);
        map.put("deviceSeries",deviceSeries);
        map.put("manufacturer",manufacturer);
        map.put("model",model);
        map.put("measuringPoint",measuringPoint);
        map.put("alarmType",alarmType);
        map.put("alarmValue",alarmValue);
        if(alarmTime != null){
            map.put("alarmTime",dateFormat.format(alarmTime));
        }
        if(restoreTime != null){
            map.put("restoreTime",dateFormat.format(restoreTime));
        }
        map.put("abnormalType",abnormalType);
        map.put("isHandle",isHandle);
        map.put("handleTime",handleTime);
        map.put("handleResult",handleResult);
        map.put("handlePersonName",handlePersonName);
        map.put("tenantId",tenantId);
        if(logIds != null){
            map.put("logIds", JSONObject.toJSON(logIds));
        }
        return map;
    }



    public static JSONObject createOfflineData(String sensorNo){
        JSONObject data = new JSONObject();
        data.put("deviceid",sensorNo);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        data.put("uploadtime",simpleDateFormat.format(new Date()));
        data.put("recievetime",simpleDateFormat.format(new Date()));
        data.put("logId","N_OFFLINE");
        JSONObject offlinePoint = new JSONObject();
        offlinePoint.put("alarmCode","OFFLINE");
        offlinePoint.put("alarmType","离线");
        offlinePoint.put("alarmStatus",0);
        offlinePoint.put("alarmValue",0);
        JSONArray points = new JSONArray();
        points.add(offlinePoint);
        data.put("alarms",points);
        data.put("status",0);
        return data;
    }
}
