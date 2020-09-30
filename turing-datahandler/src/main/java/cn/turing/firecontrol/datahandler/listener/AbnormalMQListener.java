package cn.turing.firecontrol.datahandler.listener;

import cn.turing.firecontrol.common.util.AliVmsSent;
import cn.turing.firecontrol.common.util.FeiGeSmsUtil;
import cn.turing.firecontrol.datahandler.business.JpushServiceImpl;
import cn.turing.firecontrol.datahandler.feign.IDeviceFeign;
import cn.turing.firecontrol.datahandler.listener.abnormalHandler.AbstractAbnormalHandler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;
import java.util.*;

/**
 * Created on 2019/02/22 16:13
 *
 * @Description TODO
 * @Version V1.0
 */
@Configuration
@Slf4j
public class AbnormalMQListener {

    @Autowired
    private ConnectionFactory connectionFactory;
    @Value("${tmc.config.rabbitmq.abnormal.msg.exchange}")
    private String ABNORMAL_EXCHANGE;
    @Value("${tmc.config.rabbitmq.abnormal.msg.routingKey}")
    private String ABNORMAL_ROUTING_KEY;
    @Value("${tmc.config.rabbitmq.abnormal.msg.queue}")
    private String ABNORMAL_QUEUE_NAME;
    @Autowired
    private AmqpAdmin amqpAdmin;




    @Bean
    public MessageListenerContainer abnormalListenerContainer(){
        initRabbitMQ();
        SimpleMessageListenerContainer messageListenerContainer =  new SimpleMessageListenerContainer();
        messageListenerContainer.addQueueNames(ABNORMAL_QUEUE_NAME);
        messageListenerContainer.setConcurrentConsumers(3);
        messageListenerContainer.setMessageListener(abnormalMessageListener());
        messageListenerContainer.setConnectionFactory(connectionFactory);
        return messageListenerContainer;
    }

    //初始化RabbitMQ消息队列
    public void initRabbitMQ(){
        log.info("初始化MQ环境开始");
        //创建交换机
        Exchange exchange = new DirectExchange(ABNORMAL_EXCHANGE,true,false);
        amqpAdmin.declareExchange(exchange);
        //创建用于接收报警消息的RabbitMQ消息队列
        Properties properties =  amqpAdmin.getQueueProperties(ABNORMAL_QUEUE_NAME);
        //如果队列已存在则无需创建
        if(properties != null){
            return;
        }
        Queue queue = new Queue(ABNORMAL_QUEUE_NAME,true,false,false);
        amqpAdmin.declareQueue(queue);
        Binding binding = BindingBuilder.bind(queue).to(new DirectExchange(ABNORMAL_EXCHANGE)).with(ABNORMAL_ROUTING_KEY);
        amqpAdmin.declareBinding(binding);
        log.info("初始化MQ环境结束");
    }

    //{"type":"video","data":"{}"}
    @Bean
    public MessageListener abnormalMessageListener(){

        return new MessageListener() {
            @Override
            public void onMessage(Message message) {
                String json = new String(message.getBody(), Charset.forName("UTF-8"));
                log.info("接收到的异常MQ消息:{}",json);
                JSONObject jsonObject = null;
                try{
                    jsonObject = JSONObject.parseObject(json);
                }catch (Exception e){
                    log.error("所接受到的数据不是JSON");
                    return;
                }
                if(jsonObject == null){
                    log.error("接收到空数据");
                    return;
                }
                String type = jsonObject.getString("type");
                AbstractAbnormalHandler handler = AbstractAbnormalHandler.getInstance(type);
                if(handler == null){
                    log.error("未找到处理该类消息的处理器");
                    return;
                }
                String data = jsonObject.getString("data");
                try{
                    handler.handleAbnormal(data);
                }catch (Exception e){
                    log.error("处理数据失败",e);
                }

            }
        };

    }



}
