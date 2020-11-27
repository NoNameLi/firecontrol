package cn.turing.firecontrol.datahandler.listener.abnormalHandler;

import cn.turing.firecontrol.datahandler.business.BusinessI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;

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