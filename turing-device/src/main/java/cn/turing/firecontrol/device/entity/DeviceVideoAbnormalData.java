package cn.turing.firecontrol.device.entity;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/02/21 11:38
 *
 * @Description 图片识别异常记录
 * @Version V1.0
 */
@Data
public class DeviceVideoAbnormalData extends ElasticSearchEntity implements Serializable {

    private String id;
    //异常图片集
    private List<String> pictures;
    //设备名称
    private String deviceName;
    //设备编号
    private String sensorNo;
    //设备序列名
    private String deviceSerial;
    //报警类型描述
    private String alarmType;
    //报警时间
    private Date alarmTime;
    //恢复时间
    private Date restoreTime;
    //记录更新时间
    private Date updateTime;
    //分析数据ID
    private List<String> analysisDataIds;
    //分站点ID
    private String tenantId;
    //报警类型：0故障，1报警
    private Integer alarmCategory;

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", id);
        if (pictures != null) {
            map.put("pictures", JSONObject.toJSON(pictures));
        }
        map.put("deviceName", deviceName);
        map.put("sensorNo", sensorNo);
        map.put("deviceSerial", deviceSerial);
        map.put("alarmType", alarmType);
        map.put("tenantId", tenantId);
        map.put("alarmCategory", alarmCategory);
        if (alarmTime != null) {
            map.put("alarmTime", dateFormat.format(alarmTime));
        }
        if (restoreTime != null) {
            map.put("restoreTime", dateFormat.format(restoreTime));
        }
        if (updateTime != null) {
            map.put("updateTime", dateFormat.format(updateTime));
        }
        if (analysisDataIds != null) {
            map.put("analysisDataIds", JSONObject.toJSON(analysisDataIds));
        }
        return map;
    }


}
