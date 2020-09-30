package cn.turing.firecontrol.datahandler.base;
import java.net.InetSocketAddress;


import cn.turing.firecontrol.datahandler.business.AlarmHandler;
import cn.turing.firecontrol.datahandler.business.FireMainAlarmHandler;
import cn.turing.firecontrol.datahandler.util.ServerCodeFactory;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MinaMessageConfiguration {
    private Logger log = LoggerFactory.getLogger(MinaMessageConfiguration.class);

    //@Value("${socketPort}")
    //private Integer socketPort;

    private final int  SOCKET_PORT = 3722;

    private final int  FIRE_MAIN_SOCKET_PORT = 3733;

    @Bean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    @Bean
    public IoHandler ioHandler() {
        return new AlarmHandler();
    }

    @Bean
    public IoHandler ioHandler1() {
        return new FireMainAlarmHandler();
    }

    @Bean
    public InetSocketAddress inetSocketAddress() {
        return new InetSocketAddress(SOCKET_PORT);
    }

    @Bean
    public InetSocketAddress inetSocketAddress1() {
        return new InetSocketAddress(FIRE_MAIN_SOCKET_PORT);
    }

    @Bean
    public IoAcceptor ioAcceptor() throws Exception {
        log.info("正在启动socket服务端。。。。");
        //监听传入 连接的对象
        NioSocketAcceptor  acceptor=new NioSocketAcceptor();

        //创建一个handler来实时处理客户端的连接和请求，这个handler 类必须实现 IoHandler这个接口。
        acceptor.setHandler(ioHandler());

        //记录所有的信息，比如创建session(会话)，接收消息，发送消息，关闭会话等
        DefaultIoFilterChainBuilder filterChainBuilder = new DefaultIoFilterChainBuilder();
        filterChainBuilder.addLast( "logging", loggingFilter() );
        //ProtocolCodecFilter(协议编解码过滤器).这个过滤器用来转换二进制或协议的专用数据到消息对象中
        filterChainBuilder.addLast( "codec",
                new ProtocolCodecFilter(
                        new ServerCodeFactory()));
        acceptor.setFilterChainBuilder(filterChainBuilder);
        acceptor.setReuseAddress(true);
        //设置读写缓冲区大小
        acceptor.bind(inetSocketAddress());
        log.info("socket 服务端 已经启动。。。。");
        return acceptor;
    }

    @Bean
    public IoAcceptor ioAcceptor1() throws Exception {
        log.info("正在启动消防主机socket服务端。。。。");
        //监听传入 连接的对象
        NioSocketAcceptor  acceptor=new NioSocketAcceptor();

        //创建一个handler来实时处理客户端的连接和请求，这个handler 类必须实现 IoHandler这个接口。
        acceptor.setHandler(ioHandler1());

        //记录所有的信息，比如创建session(会话)，接收消息，发送消息，关闭会话等
        DefaultIoFilterChainBuilder filterChainBuilder = new DefaultIoFilterChainBuilder();
        filterChainBuilder.addLast( "logging", loggingFilter() );
        //ProtocolCodecFilter(协议编解码过滤器).这个过滤器用来转换二进制或协议的专用数据到消息对象中
        filterChainBuilder.addLast( "codec",
                new ProtocolCodecFilter(
                        new ServerCodeFactory()));
        acceptor.setFilterChainBuilder(filterChainBuilder);
        acceptor.setReuseAddress(true);
        //设置读写缓冲区大小
        acceptor.bind(inetSocketAddress1());
        log.info("消防主机socket 服务端 已经启动。。。。");
        return acceptor;
    }

}