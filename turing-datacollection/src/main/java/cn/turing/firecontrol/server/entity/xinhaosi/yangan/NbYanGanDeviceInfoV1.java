package cn.turing.firecontrol.server.entity.xinhaosi.yangan;

import cn.turing.common.base.Constant;
import cn.turing.common.entity.DeviceInfo;
import cn.turing.common.entity.SensorDetail;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

import java.text.SimpleDateFormat;

@Data
public class NbYanGanDeviceInfoV1 extends DeviceInfo {
        public String sensorCode;
        public String type;
    public NbYanGanDeviceInfoV1(JSONObject jsonObject){
        /**
         * {
         *     "data": {
         *         "currentEvents": [
         *             {
         *                 "name": "火警",
         *                 "time": 1564994994000,
         *                 "type": 1
         *             }
         *         ],
         *         "currentStatus": [
         *             {
         *                 "category": 1,
         *                 "name": "报警"
         *             }
         *         ],
         *         "physicalNumber": "8653520357369810",
         *         "sensorCode": "00jxu 0ozl 01 001 01"
         *     },
         *     "publishTime": 1564995006548,
         *     "type": "EVENT"
         * }
         */
        JSONObject data=jsonObject.getJSONObject("data");
        JSONArray events=data.getJSONArray("currentEvents");
        JSONArray status=data.getJSONArray("currentStatus");
        JSONObject event=events.getJSONObject(0);
        JSONObject stat=status.getJSONObject(0);
        this.device_id=data.getString("physicalNumber");
        this.upload_time=event.getDate("time");
        this.recieve_time=jsonObject.getDate("publishTime");
        this.sensorDetails= Lists.newArrayList();
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.YG);
        if (stat.getInteger("category")==1){//状态分类，1 表示报警类, 2 故障类, 3 屏蔽类, 4 启动类(输出) 5 正常
            sensorDetail.setAlarmType(stat.getString("name"));
            sensorDetail.setAlarmStatus(Constant.ST_ALARM);
            sensorDetail.setAlarmValue(Constant.ST_ALARM);
            this.has_alarm=true;
        }
        if (stat.getInteger("category")==2){//状态分类，1 表示报警类, 2 故障类, 3 屏蔽类, 4 启动类(输出) 5 正常
            sensorDetail.setAlarmType(stat.getString("name"));
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            this.has_guzhang=true;
        }
        if (stat.getInteger("category")==3){//状态分类，1 表示报警类, 2 故障类, 3 屏蔽类, 4 启动类(输出) 5 正常
            sensorDetail.setAlarmType(stat.getString("name"));
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            this.has_guzhang=true;
        }
        if (stat.getInteger("category")==4){//状态分类，1 表示报警类, 2 故障类, 3 屏蔽类, 4 启动类(输出) 5 正常
            sensorDetail.setAlarmType(stat.getString("name"));
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        if (stat.getInteger("category")==5){//状态分类，1 表示报警类, 2 故障类, 3 屏蔽类, 4 启动类(输出) 5 正常
            sensorDetail.setAlarmType(stat.getString("name"));
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }
        sensorDetails.add(sensorDetail);
        this.sensorCode=data.getString("sensorCode");
        this.type=jsonObject.getString("type");
    }

    @Override
    public JSONObject toDeviceJSON() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceid", this.getDevice_id().toLowerCase());
        jsonObject.put("sensorCode", this.getSensorCode());
        jsonObject.put("type", this.getType());
        jsonObject.put("uploadtime",this.getUpload_time());
        jsonObject.put("recievetime",this.getRecieve_time());
        if(this.getSensorDetails()!=null) {
            for (SensorDetail sensorDetail:this.getSensorDetails()) {
                JSONObject val = new JSONObject();
                val.put("alarmValue",sensorDetail.getAlarmValue());
                val.put("alarmType",sensorDetail.getAlarmType());
                val.put("alarmStatus",sensorDetail.getAlarmStatus());
                jsonObject.put(sensorDetail.getAlarmCode(),val);
            }
        }
        return jsonObject;
    }

    @Override
    public JSONObject toDeviceMessage() {
        return null;
    }
}
