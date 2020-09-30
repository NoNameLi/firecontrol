package cn.turing.firecontrol.device.runner;

import cn.turing.firecontrol.device.entity.DeviceVideoAbnormalData;
import cn.turing.firecontrol.device.entity.DeviceVideoAnalysisData;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.ESTransportUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Created on 2019/02/22 14:53
 *
 * @Description
 * @Version V1.0
 */
@Component
@Slf4j
public class DeviceInitRunner implements ApplicationRunner {


    @Autowired
    private ESTransportUtil esTransportUtil;
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Value("${tmc.config.rabbitmq.abnormal.msg.exchange}")
    private String EXCHANGE_NAME;
    @Value("${tmc.config.rabbitmq.abnormal.msg.routingKey}")
    private String ROUTING_KEY;
    @Value("${tmc.config.rabbitmq.abnormal.msg.queue}")
    private String QUEUE_NAME;
    @Value("${tmc.config.elasicSearch.abnormal.index}")
    private String ABNORMAL_INDEX;
    @Value("${tmc.config.elasicSearch.abnormal.type.firedoor}")
    private String ABNORMAL_TYPE_FIREDOOR;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //初始化ElasticSearch索引
        initElasticSearchIndex();
        //初始化RabbitMQ消息队列
        initRabbitMQ();
    }


    //初始化ElasticSearch索引
    public void initElasticSearchIndex(){
        log.info("初始化ES环境开始");
        Constants.AnalysisSolution solution = Constants.AnalysisSolution.FIRE;
        //创建视频分析数据索引
        if(!esTransportUtil.isTypeExist(solution.getDataEsIndex(),solution.getCode())){
            esTransportUtil.addIndexAndType(solution.getDataEsIndex(),solution.getCode(), DeviceVideoAnalysisData.class);
        }
        //创建视频分析异常数据索引
        if(!esTransportUtil.isTypeExist(solution.getAbnormalEsIndex(),solution.getCode())){
            esTransportUtil.addIndexAndType(solution.getAbnormalEsIndex(),solution.getCode(), DeviceVideoAbnormalData.class);
        }
        log.info("初始化ES环境结束");
    }

    //初始化RabbitMQ消息队列
    public void initRabbitMQ(){
        log.info("初始化MQ环境开始");
        //创建交换机
        Exchange exchange = new DirectExchange(EXCHANGE_NAME,true,false);
        amqpAdmin.declareExchange(exchange);
        //创建用于接收报警消息的RabbitMQ消息队列
        Properties properties =  amqpAdmin.getQueueProperties(QUEUE_NAME);
        //如果队列已存在则无需创建
        if(properties != null){
            return;
        }
        Queue queue = new Queue(QUEUE_NAME,true,false,false);
        amqpAdmin.declareQueue(queue);
        Binding binding = BindingBuilder.bind(queue).to(new DirectExchange(EXCHANGE_NAME)).with(ROUTING_KEY);
        amqpAdmin.declareBinding(binding);
        log.info("初始化MQ环境结束");
    }



}
