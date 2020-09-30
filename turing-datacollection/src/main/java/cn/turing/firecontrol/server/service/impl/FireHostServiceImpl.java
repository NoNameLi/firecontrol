package cn.turing.firecontrol.server.service.impl;

import cn.turing.common.entity.lidahuaxin.FireHostDevice;
import cn.turing.common.entity.lidahuaxin.FireHostDeviceV1;
import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.server.base.Constant;
import cn.turing.firecontrol.server.business.BusinessI;
import cn.turing.firecontrol.server.entity.SensorDetail;
import cn.turing.firecontrol.server.feign.IDevcieFeigndataHandler;
import cn.turing.firecontrol.server.service.FireHostService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FireHostServiceImpl implements FireHostService {
    @Autowired
    BusinessI businessI;
    @Autowired
    IDevcieFeigndataHandler devcieFeigndataHandler;

   static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Autowired
    StringRedisTemplate redisTemplate;
    public static String sensorflag = "sensor:";
    @Override
    public void readLiDaFireHost(FireHostDevice fireHostDevice) {
        fireHostDevice.setIp("172.16.211.79");
        HashMap<String, Map<String,String>> hashMap=fireHostDevice.getHashMap();
        List<String> loops=fireHostDevice.getLoops();
        for (String loop:loops){
            String uuid = UUIDUtils.generateUuid();
            Map<String,String> map=hashMap.get(loop);
            SensorDetail sensorDetail=new SensorDetail();
            Integer status=2;
            for (Map.Entry<String,String> vo:map.entrySet()){
                if (vo.getValue().equals("0")){
                    sensorDetail.setAlarmType("正常");
                    sensorDetail.setAlarmStatus(Constant.ST_NORM);
                    sensorDetail.setAlarmValue(Constant.ST_NORM);
                }else{
                    status= paring(vo,sensorDetail,status);
                }
            }

            HashMap<String,String> hashMap1=fireHostDevice.getComponentsAddress();
//            将数据存入redis中！

            JSONObject jsonObject = alarmOrNormal(fireHostDevice, loop, sensorDetail, status,hashMap1.get(loop));
            JSONObject alarm_data=alarm(fireHostDevice,loop,sensorDetail,hashMap1.get(loop),uuid);
            log.info("处理："+fireHostDevice.getIp()+":"+fireHostDevice.getPort()+":"+hashMap1.get(loop)+":"+loop+"的数据");
            JSONObject object=businessI.selectRedisFireMain(fireHostDevice.getIp(),
                    fireHostDevice.getPort(),hashMap1.get(loop),loop);
            if (object==null){
                if (status==2){
                    businessI.updateStatus(fireHostDevice.getIp(),fireHostDevice.getPort(),hashMap1.get(loop),loop, "2");
                    devcieFeigndataHandler.fireMainAbnormalRestore(fireHostDevice.getIp(),fireHostDevice.getPort(),hashMap1.get(loop),loop);
                }else{
                    if (alarm_data.getBoolean("status")){
                        log.error("发送报警信息");
                        businessI.sendMessageFireMain(alarm_data,alarm_data);
                    }
                }
                //将数据存入ES
                businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, jsonObject);
            }else{
                if (object.getBoolean("status") ==alarm_data.getBoolean("status")){
                    log.info("与上一条状态相比一样，不做处理");
                }else{
                    if (status==2){
                        businessI.updateStatus(fireHostDevice.getIp(),fireHostDevice.getPort(),hashMap1.get(loop),loop, "2");
                        devcieFeigndataHandler.fireMainAbnormalRestore(fireHostDevice.getIp(),fireHostDevice.getPort(),hashMap1.get(loop),loop);
                    }else{
                        if (alarm_data.getBoolean("status")){
                            log.error("发送报警信息");
                            businessI.sendMessageFireMain(alarm_data,alarm_data);
                        }
                    }
                    //将数据存入ES
                    businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, jsonObject);
                }
            }
            boolean flag=false;
            if (status!=2) {
                flag = true;
            }
            updateFireMainInRedis(fireHostDevice.getIp(),fireHostDevice.getPort(),hashMap1.get(loop),loop,flag,
                    1800,uuid,sensorDetail);
        }
    }

    @Override
    public void readLiDaFireHost(FireHostDeviceV1 fireHostDevice) {
        fireHostDevice.setIp("172.16.211.79");
        //地址
       List<String> addresss= fireHostDevice.getAddress();
       //回路
        List<String> loops=fireHostDevice.getLoops();
        List<String> alarmType=fireHostDevice.getAlarmType();
        for (int i=0; i<addresss.size();i++){
            String loop=loops.get(i);
            String address=addresss.get(i);
            String alarm=alarmType.get(i);
            SensorDetail sensorDetail=new SensorDetail();
            if (alarm.equalsIgnoreCase("1")){
                sensorDetail.setAlarmType("报警");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                sensorDetail.setAlarmValue(Constant.ST_ALARM);
            }else{
                sensorDetail.setAlarmType("正常");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);
            }
            //ES
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("ip", fireHostDevice.getIp());
            jsonObject.put("port", fireHostDevice.getPort());
            jsonObject.put("loopNo", loop);
            jsonObject.put("localtionNo", address);
            jsonObject.put("uploadtime", new Date());
            jsonObject.put("recievetime", new Date());
            JSONObject jo=new JSONObject();
            if (sensorDetail!=null){
                jo.put("alarmType", sensorDetail.getAlarmType());
                jo.put("alarmValue", sensorDetail.getAlarmValue());
                jo.put("alarmStatus", sensorDetail.getAlarmStatus());
            }
            jsonObject.put("alarm",jo);
            if (alarm.equals("1")){
                jsonObject.put("status", Integer.toString(Constant.ST_ALARM));
            }else{
                jsonObject.put("status", Integer.toString(Constant.ST_NORM));
            }

            if (alarm.equals("0")){

                String flag=  redisTemplate.opsForValue().get("fireMain:"+fireHostDevice.getIp()+fireHostDevice.getPort()+loop+address);
                if (!flag.equals("0")){
                    businessI.updateStatus(fireHostDevice.getIp(),fireHostDevice.getPort(),loop,address, "2");
                    devcieFeigndataHandler.fireMainAbnormalRestore(fireHostDevice.getIp(),fireHostDevice.getPort(),loop,address);
                }

                redisTemplate.opsForValue().set("fireMain:"+fireHostDevice.getIp()+fireHostDevice.getPort()+loop+address,"0");
            }else{

                log.error("发送报警信息");
                JSONObject jsonObject4=new JSONObject();
                jsonObject4.put("ip",fireHostDevice.getIp());
                jsonObject4.put("port",fireHostDevice.getPort());
                jsonObject4.put("loopNo",loop);
                jsonObject4.put("localtionNo",address);

                jsonObject4.put("status",true);
                jsonObject4.put("logid",UUIDUtils.generateUuid());
                jsonObject4.put("uploadtime",sdf.format(new Date()));
                JSONObject json=new JSONObject();
                json.put("alarmType",sensorDetail.getAlarmType());
                json.put("alarmStatus",sensorDetail.getAlarmStatus());
                json.put("alarmValue",sensorDetail.getAlarmValue());
                jsonObject4.put("alarm",json);

                JSONObject object=new JSONObject();
                object.put("ip",fireHostDevice.getIp());
                object.put("port",fireHostDevice.getPort());
                object.put("loopNo",loop);
                object.put("localtionNo",address);

                object.put("status",false);
                object.put("logid",UUIDUtils.generateUuid());
                object.put("uploadtime",sdf.format(new Date(System.currentTimeMillis()-5000)));
                JSONObject json1=new JSONObject();
                json1.put("alarmType","正常");
                json1.put("alarmStatus",Constant.ST_NORM);
                json1.put("alarmValue",Constant.ST_NORM);
                object.put("alarm",json1);

               if (redisTemplate.hasKey("fireMain:"+fireHostDevice.getIp()+fireHostDevice.getPort()+loop+address)){
                  String flag=  redisTemplate.opsForValue().get("fireMain:"+fireHostDevice.getIp()+fireHostDevice.getPort()+loop+address);
                    if (flag.equals("0")){
                        businessI.sendMessageFireMain(object,jsonObject4);
                    }
                }else{
                   businessI.sendMessageFireMain(object,jsonObject4);
               }
                //0:报警1:不报警
                redisTemplate.opsForValue().set("fireMain:"+fireHostDevice.getIp()+fireHostDevice.getPort()+loop+address,"1");

            }


        }



    }

    @Override
    public boolean updateFireMainInRedis(String ip, String port, String loopNo, String localtionNo, boolean flag, int timeout, String uuid, SensorDetail sensorDetail) {
        JSONObject jsonObject=new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObject.put("ip",ip);
        jsonObject.put("port",port);
        jsonObject.put("loopNo",loopNo);
        jsonObject.put("localtionNo",localtionNo);
        jsonObject.put("uploadtime",sdf.format(new Date()));
        jsonObject.put("status",flag);
        jsonObject.put("logid",uuid);
        System.out.println();
        JSONObject json=new JSONObject();
        json.put("alarmType",sensorDetail.getAlarmType());
        json.put("alarmStatus",sensorDetail.getAlarmStatus());
        json.put("alarmValue",sensorDetail.getAlarmValue());
        jsonObject.put("alarm",json);
        String deviceId=ip+port+loopNo+localtionNo;
        try {
            if (redisTemplate.hasKey(sensorflag+deviceId)) {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                JSONObject jsonObject1 = JSONObject.parseObject(redisTemplate.opsForValue().get(sensorflag+deviceId));

                Date dateinredis = sdf1.parse(jsonObject1.getString("uploadtime"));

                if (dateinredis.getTime() <= new Date().getTime()) {

                    redisTemplate.opsForValue().set(sensorflag+deviceId,jsonObject.toJSONString());
                    log.info("expire:"+sensorflag+deviceId+":"+timeout);

                }
            } else {
                redisTemplate.opsForValue().set(sensorflag+deviceId,jsonObject.toJSONString());

                log.info("expire:"+sensorflag+deviceId+":"+timeout);

            }
        }catch (Exception e){
            log.error("发到redis失败",e);
            return false;
        }
        return true;

    }


    public int paring(Map.Entry<String,String> vo,SensorDetail sensorDetail,int flag){

            if (vo.getKey().equals("7")){
                sensorDetail.setAlarmType("报警");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                sensorDetail.setAlarmValue(Constant.ST_ALARM);
                flag=1;

            }
            if (vo.getKey().equals("6")){
                sensorDetail.setAlarmType("故障");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                sensorDetail.setAlarmValue(Constant.ST_WARN);
                flag=0;
            }
            if (vo.getKey().equals("5")){
                sensorDetail.setAlarmType("启动");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);

            }
            if (vo.getKey().equals("4")){
                sensorDetail.setAlarmType("回答");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);

            }
            if (vo.getKey().equals("3")){
                sensorDetail.setAlarmType("屏蔽");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);

            }
            if (vo.getKey().equals("2")){
                sensorDetail.setAlarmType("监管");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);
            }
            return flag;
    }

    public  JSONObject alarmOrNormal(FireHostDevice fireHostDevice,String loop,SensorDetail sensorDetail,int flag,String comAddress){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("ip", fireHostDevice.getIp());
        jsonObject.put("port", fireHostDevice.getPort());
        jsonObject.put("loopNo", comAddress);
        jsonObject.put("localtionNo", loop);
        jsonObject.put("uploadtime", new Date());
        jsonObject.put("recievetime", new Date());
        JSONObject jo=new JSONObject();
        if (sensorDetail!=null){
            jo.put("alarmType", sensorDetail.getAlarmType());
            jo.put("alarmValue", sensorDetail.getAlarmValue());
            jo.put("alarmStatus", sensorDetail.getAlarmStatus());
        }
        jsonObject.put("alarm",jo);
        if (flag==1){
            jsonObject.put("status", Integer.toString(Constant.ST_ALARM));
        }else if (flag==0){
            jsonObject.put("status", Integer.toString(Constant.ST_WARN));
        }else{
            jsonObject.put("status", Integer.toString(Constant.ST_NORM));
        }

        return jsonObject;
    }


    public JSONObject alarm(FireHostDevice fireHostDevice,String loop,SensorDetail sensorDetail,String comAddress,String uuid){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject4=new JSONObject();
        jsonObject4.put("ip",fireHostDevice.getIp());
        jsonObject4.put("port",fireHostDevice.getPort());
        jsonObject4.put("loopNo",comAddress);
        jsonObject4.put("localtionNo",loop);
        boolean flag=false;
        if (sensorDetail.getAlarmStatus()!=2){
            flag=true;
        }
        jsonObject4.put("status",flag);
        jsonObject4.put("logid",uuid);
        jsonObject4.put("uploadtime",sdf.format(new Date()));
        JSONObject json=new JSONObject();
        json.put("alarmType",sensorDetail.getAlarmType());
        json.put("alarmStatus",sensorDetail.getAlarmStatus());
        json.put("alarmValue",sensorDetail.getAlarmValue());
        jsonObject4.put("alarm",json);

        return jsonObject4;
    }
}
