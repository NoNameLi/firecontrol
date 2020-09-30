package cn.turing.firecontrol.datahandler.listener.abnormalHandler;

import cn.turing.firecontrol.datahandler.base.Constant;
import cn.turing.firecontrol.datahandler.biz.DeviceAbnormalBiz;
import cn.turing.firecontrol.datahandler.business.BusinessI;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import cn.turing.firecontrol.device.entity.DeviceAbnormal;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class DefaultAbnormalHandler extends  AbstractAbnormalHandler{

    @Autowired
    private BusinessI businessI;

    /**
     *
     * @param data {"recievetime":"2019-04-18 12:00:33","alarms":[{"alarmType":"供电中断","alarmStatus":1,"alarmValue":0,"alarmCode":"VLA"},{"alarmType":"供电中断","alarmStatus":1,"alarmValue":0,"alarmCode":"VLB"},{"alarmType":"供电中断","alarmStatus":1,"alarmValue":0,"alarmCode":"VLC"}],"logId":"66e5f3521a964f63a14e9e05ac0dfe54","deviceid":"123456","uploadtime":"2019-04-18 12:00:33","status":"1"}
     * @throws ParseException
     */
    public void handleAbnormal(String data) throws Exception {
        log.info("接收到数据:{}",data);
        businessI.alertMSG(data);
    }

}