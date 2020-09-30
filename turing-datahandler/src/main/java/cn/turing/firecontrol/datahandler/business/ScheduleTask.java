package cn.turing.firecontrol.datahandler.business;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;


/**
 * 定时任务类
 */
@Component
public class ScheduleTask {
    private static final Logger log=LoggerFactory.getLogger(ScheduleTask.class);

    @Autowired
    RedisTemplate redisTemplate;
    /**
     * 定时遍历数据将传感器状态设置为正常
     *fixedRate = 每5分钟执行一次
     */
    @Scheduled(fixedDelayString  = "300000")
    public void getTask(){
        log.info("redis 心跳链接");
        redisTemplate.opsForValue().get("heartbeat");
    }


}
