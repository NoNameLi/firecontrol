package cn.turing.firecontrol.server.service.impl;


import cn.turing.common.entity.sanjiangsea.SanJiangSeaDevice;
import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.server.base.Constant;
import cn.turing.firecontrol.server.business.BusinessI;
import cn.turing.firecontrol.server.entity.SensorDetail;
import cn.turing.firecontrol.server.feign.IDevcieFeigndataHandler;
import cn.turing.firecontrol.server.service.FireHostService;
import cn.turing.firecontrol.server.service.SanJiangSeaService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class SanJiangSeaServiceImpl implements SanJiangSeaService {

    @Autowired
    BusinessI businessI;

    @Autowired
    FireHostService fireHostService;

    @Autowired
    IDevcieFeigndataHandler devcieFeigndataHandler;

    @Override
    public void ReadSanJiangSea(SanJiangSeaDevice sanJiangSeaDevice) {
        sanJiangSeaDevice.setIp("172.16.211.79");
        String uuid = UUIDUtils.generateUuid();
        SensorDetail sensorDetail=new SensorDetail();
        int status=2;
        if (sanJiangSeaDevice.getAlarm_type().equals("20")){//火警或反馈
            if (sanJiangSeaDevice.getFrom_type().equals("01")){
                sensorDetail.setAlarmType("火警发生");
                sensorDetail.setAlarmStatus(Constant.ST_ALARM);
                sensorDetail.setAlarmValue(Constant.ST_ALARM);
                status=1;
            }else{
                sensorDetail.setAlarmType("火警消失");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);
            }
        }else if (sanJiangSeaDevice.getAlarm_type().equals("40")){//故障
            if (sanJiangSeaDevice.getFrom_type().equals("01")){//丢失
                sensorDetail.setAlarmType("丢失");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                sensorDetail.setAlarmValue(Constant.ST_WARN);
                status=0;
            }else if (sanJiangSeaDevice.getFrom_type().equals("81")){//丢失消失
                sensorDetail.setAlarmType("丢失消失");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);
            }
            else if (sanJiangSeaDevice.getFrom_type().equals("02")){//模块负载开路
                sensorDetail.setAlarmType("模块负载开路");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                sensorDetail.setAlarmValue(Constant.ST_WARN);
                status=0;
            }
            else if (sanJiangSeaDevice.getFrom_type().equals("82")){//模块负载开路消失
                sensorDetail.setAlarmType("模块负载开路消失");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);
            }
            else if (sanJiangSeaDevice.getFrom_type().equals("04")){//模块负载短路
                sensorDetail.setAlarmType("模块负载短路");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                sensorDetail.setAlarmValue(Constant.ST_WARN);
                status=0;
            }
            else if (sanJiangSeaDevice.getFrom_type().equals("84")){//模块负载短路消失
                sensorDetail.setAlarmType("模块负载短路消失");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);
            }
            else if (sanJiangSeaDevice.getFrom_type().equals("05")){//KZJ_LD 设备故障
                sensorDetail.setAlarmType("KZJ_LD 设备故障");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                sensorDetail.setAlarmValue(Constant.ST_WARN);
                status=0;
            }
            else if (sanJiangSeaDevice.getFrom_type().equals("85")){//KZJ_LD 设备故障消失
                sensorDetail.setAlarmType("KZJ_LD 设备故障消失");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);
            }
            else if (sanJiangSeaDevice.getFrom_type().equals("06")){//生产类型不匹配
                sensorDetail.setAlarmType("生产类型不匹配");
                sensorDetail.setAlarmStatus(Constant.ST_WARN);
                sensorDetail.setAlarmValue(Constant.ST_WARN);
                status=0;
            }
            else if (sanJiangSeaDevice.getFrom_type().equals("86")){//生产类型不匹配消失
                sensorDetail.setAlarmType("生产类型不匹配消失");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);
            }
        }else if (sanJiangSeaDevice.getAlarm_type().equals("24")){//启动或停止
            if (sanJiangSeaDevice.getFrom_type().equals("01")){//模块启动请求
                sensorDetail.setAlarmType("模块启动请求");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);
            }else if (sanJiangSeaDevice.getFrom_type().equals("01")){//模块停止请求
                sensorDetail.setAlarmType("模块停止请求");
                sensorDetail.setAlarmStatus(Constant.ST_NORM);
                sensorDetail.setAlarmValue(Constant.ST_NORM);
                status=0;
            }
        }
        //将数据存入redis中！
        //没有离线机制
//        fireHostService.updateFireMainInRedis(sanJiangSeaDevice.getIp(),sanJiangSeaDevice.getPort(),sanJiangSeaDevice.getLoopNo(),sanJiangSeaDevice.getAddressNo(),flag,
//                1800,uuid,sensorDetail);
        JSONObject jsonObject=alarmOrNormal(sanJiangSeaDevice,status,sensorDetail);
        JSONObject alarm_data=alarm(sanJiangSeaDevice,sensorDetail,uuid);
        log.info("处理:"+sanJiangSeaDevice.getIp()+":"+sanJiangSeaDevice.getPort()+":"+sanJiangSeaDevice.getLoopNo()+":"+sanJiangSeaDevice.getAddressNo()+"的数据");
        if (status==2){
            businessI.updateStatus(sanJiangSeaDevice.getIp(),sanJiangSeaDevice.getPort(),sanJiangSeaDevice.getLoopNo(),sanJiangSeaDevice.getAddressNo(), "2");
            devcieFeigndataHandler.fireMainAbnormalRestore(sanJiangSeaDevice.getIp(),sanJiangSeaDevice.getPort(),sanJiangSeaDevice.getLoopNo(),sanJiangSeaDevice.getAddressNo());
        }else{
            log.error("发送报警信息");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONObject jsonObject4=new JSONObject();
            jsonObject4.put("ip",sanJiangSeaDevice.getIp());
            jsonObject4.put("port",sanJiangSeaDevice.getPort());
            jsonObject4.put("loopNo",sanJiangSeaDevice.getLoopNo());
            jsonObject4.put("localtionNo",sanJiangSeaDevice.getAddressNo());
            boolean flag=false;
            jsonObject4.put("status",flag);
            jsonObject4.put("logid",uuid+"12");
            jsonObject4.put("uploadtime",sdf.format(new Date(System.currentTimeMillis()-5000)));
            JSONObject json=new JSONObject();
            json.put("alarmType","正常");
            json.put("alarmStatus",Constant.ST_NORM);
            json.put("alarmValue",Constant.ST_NORM);
            jsonObject4.put("alarm",json);
            businessI.sendMessageFireMain(jsonObject4,alarm_data);
        }

        //将数据存入ES
        businessI.insertData(Constant.ESConstant.ES_INDEX_SENSOR, Constant.ESConstant.ES_SOURCE_TYPE, uuid, jsonObject);

    }

    private JSONObject alarm(SanJiangSeaDevice sanJiangSeaDevice, SensorDetail sensorDetail, String uuid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject4=new JSONObject();
        jsonObject4.put("ip",sanJiangSeaDevice.getIp());
        jsonObject4.put("port",sanJiangSeaDevice.getPort());
        jsonObject4.put("loopNo",sanJiangSeaDevice.getLoopNo());
        jsonObject4.put("localtionNo",sanJiangSeaDevice.getAddressNo());
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

    private JSONObject alarmOrNormal(SanJiangSeaDevice sanJiangSeaDevice, int flag, SensorDetail sensorDetail) {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("ip", sanJiangSeaDevice.getIp());
        jsonObject.put("port", sanJiangSeaDevice.getPort());
        jsonObject.put("loopNo", sanJiangSeaDevice.getLoopNo());
        jsonObject.put("localtionNo", sanJiangSeaDevice.getAddressNo());
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

    public static void main(String[] args) {
        System.out.println(0/2+1);
        System.out.println(0%2+1);
    }
}
