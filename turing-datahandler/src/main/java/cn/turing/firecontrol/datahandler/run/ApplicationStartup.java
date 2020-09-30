package cn.turing.firecontrol.datahandler.run;


import cn.turing.firecontrol.datahandler.entity.SensorAbnormal;
import cn.turing.firecontrol.datahandler.util.ESTransportUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;

import java.util.Properties;

/**
 * 启动springboot后执行的第一个类
 */
//@Component
@Order(1)
@Slf4j
public class ApplicationStartup implements ApplicationRunner {


    @Autowired
    private ESTransportUtil esTransportUtil;
    @Value("${tmc.config.elasicSearch.abnormal.index}")
    private String ABNORMAL_INDEX;
    @Value("${tmc.config.elasicSearch.abnormal.type.firedoor}")
    private String ABNORMAL_TYPE_FIREDOOR;



    @Override
    public void run(ApplicationArguments args) throws Exception {
        initElasticSearchIndex();
    }

    //初始化ElasticSearch索引
    public void initElasticSearchIndex(){
        log.info("初始化ES环境开始");
        //创建视频分析数据索引
        if(!esTransportUtil.isTypeExist(ABNORMAL_INDEX,ABNORMAL_TYPE_FIREDOOR)){
            esTransportUtil.addIndexAndType(ABNORMAL_INDEX,ABNORMAL_TYPE_FIREDOOR, SensorAbnormal.class);
        }
        log.info("初始化ES环境结束");
    }





}
