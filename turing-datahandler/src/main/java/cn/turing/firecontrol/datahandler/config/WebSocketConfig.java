package cn.turing.firecontrol.datahandler.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;
import java.util.List;
import java.util.Map;


@Configuration
public class WebSocketConfig extends Configurator{
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        super.modifyHandshake(sec, request, response);
        /*如果没有监听器,那么这里获取到的HttpSession是null*/
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        Map<String,List<String>> params = request.getParameterMap();
        for(String k : params.keySet()){
            System.out.println("所有参数 : "+k + ":" + params.get(k));
        }
        sec.getUserProperties().put("userId",params.get("userId").get(0));
      //  sec.getUserProperties().put("username",params.get("username").get(0));
        //关键操作
        sec.getUserProperties().put("sessionId", httpSession.getId());
        System.out.println("获取到的SessionID：" + httpSession.getId());
    }

    @Bean //放开jar包冲突
    public ServerEndpointExporter serverEndpointExporter() {
        //这个对象说一下，貌似只有服务器是tomcat的时候才需要配置,具体我没有研究
        return new ServerEndpointExporter();
    }

}
