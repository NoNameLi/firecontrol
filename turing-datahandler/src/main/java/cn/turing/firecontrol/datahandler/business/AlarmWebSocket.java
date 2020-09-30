package cn.turing.firecontrol.datahandler.business;


import cn.turing.firecontrol.datahandler.config.WebSocketConfig;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/alarm/websocket", configurator = WebSocketConfig.class)
@Component
@Log4j
public class AlarmWebSocket {
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    public static CopyOnWriteArraySet<AlarmWebSocket> webSocketSet = new CopyOnWriteArraySet<AlarmWebSocket>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    private String currentUser;

    public String getCurrentUser() {
        return currentUser;
    }

    /**
     * 连接建立成功调用的方法
     * <p>
     * config用来获取WebsocketConfig中的配置信息
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        //获取WebsocketConfig.java中配置的“sessionId”信息值
        // String httpSessionId = (String) config.getUserProperties().get("sessionId");
        currentUser=config.getUserProperties().get("userId").toString();
        log.info("链接 用户是"+currentUser);
        session.getUserProperties().put("userId", currentUser);
        this.session = session;
        webSocketSet.add(this);     //加入set中
        try {
            // 发消息入口
            onMessage(null, session);

        } catch (Exception e) {
            System.out.println("IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);

    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws  Exception{
        //session.getUserProperties().put("userId",  BaseContextHandler.getUserID());
        //不处理
        log.info("收到的消息："+message);
        if(StringUtils.isNotBlank(message)){
            Map map = new HashMap();
            map.put("HeartResponse","HeartResponse");
            String msg = JSONObject.toJSONString(map);
            this.sendMessage(msg);
            //System.out.println("握手成功");
            log.info("握手成功");
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * B 发送消息
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

}
