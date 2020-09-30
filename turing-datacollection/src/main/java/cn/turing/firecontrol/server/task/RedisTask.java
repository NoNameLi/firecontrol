package cn.turing.firecontrol.server.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    //五分钟运行一次
    @Scheduled(fixedRate = 300000)
    public void saveData(){
        stringRedisTemplate.keys("sensor:*");
        log.warn("定时任务5分钟");
    }





}
