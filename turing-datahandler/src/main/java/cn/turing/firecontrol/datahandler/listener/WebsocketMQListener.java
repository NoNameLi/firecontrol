package cn.turing.firecontrol.datahandler.listener;

import cn.turing.firecontrol.datahandler.business.AlarmWebSocket;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

/**
 * Created on 2019/01/02 16:08
 *
 * @Description TODO
 * @Version V1.0
 */
@Configuration
@Slf4j
public class WebsocketMQListener {

    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Value("${tmc.config.rabbitmq.abnormal.websocket.exchange}")
    private String EXCHANGE_NAME;
    @Value("${tmc.config.rabbitmq.abnormal.websocket.routingKey}")
    private String ROUTING_KEY;
    @Value("${tmc.config.rabbitmq.abnormal.websocket.queue}")
    private String QUEUE_NAME;

    @Bean
    public MessageListenerContainer alarmWebsocketListenerContainer(){
        SimpleMessageListenerContainer messageListenerContainer =  new SimpleMessageListenerContainer();
        messageListenerContainer.addQueueNames(QUEUE_NAME);
        messageListenerContainer.setConcurrentConsumers(3);
        messageListenerContainer.setMessageListener(alarmWebsocketMessageListener());
        messageListenerContainer.setConnectionFactory(connectionFactory);
        return messageListenerContainer;
    }

    @Bean
    public MessageListener alarmWebsocketMessageListener() {
        return new MessageListener() {
            @Override
            public void onMessage(Message message) {
                String json = new String(message.getBody(), Charset.forName("UTF-8"));
                log.info("接收到MQ消息:{}", json);
                JSONObject jsonObject = null;
                try {
                    jsonObject = JSONObject.parseObject(json);
                }catch (Exception e){
                    log.error("接收到的MQ消息不是JSON串");
                    return;
                }
                String msg = jsonObject.getString("msg");
                JSONArray jsonArray = jsonObject.getJSONArray("userIds");
                if(StringUtils.isBlank(msg) || jsonArray == null){
                    log.error("用户ID和消息内容不能为空");
                    return;
                }
                List<String> userIds = jsonArray.toJavaList(String.class);
                for (AlarmWebSocket item : AlarmWebSocket.webSocketSet) {
                    String userId = item.getCurrentUser();
                    if (userIds.contains(userId)) {
                        try {
                            item.sendMessage(msg);
                            log.info("发送给" + item.getCurrentUser() + "/" + userId);
                        } catch (IOException e) {
                            log.error("给{}发送WS消息失败",userId,e);
                        }
                    }
                }
            }
        };
    }

    @PostConstruct
    public void init(){
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
    }

}
