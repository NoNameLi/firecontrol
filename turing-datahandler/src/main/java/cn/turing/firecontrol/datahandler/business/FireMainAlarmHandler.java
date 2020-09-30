package cn.turing.firecontrol.datahandler.business;

import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@Slf4j
public class FireMainAlarmHandler extends IoHandlerAdapter {
    @Autowired
    BusinessI businessI;

    //private final int IDLE = 3000;//(单位s)
//    private final Logger log = LoggerFactory.getLogger(AlarmHandler.class);
    // public static Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
    //public static ConcurrentHashMap<Long, IoSession> sessionsConcurrentHashMap = new ConcurrentHashMap<Long, IoSession>();

    //快进快出
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String data = message.toString();
        log.warn("客户端" + ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress() + "连接成功！");
        session.setAttribute("type", message);
        String remoteAddress = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
        session.setAttribute("ip", remoteAddress);
        log.warn("服务器收到的消息是：" + data);
        session.write("recieved");
        try {
              businessI.alertFireMainMSG(data);
        } catch (Exception e) {
            log.error("分析数据失败."+data ,e);
        }
        this.sessionClosed(session);
    }
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        super.sessionOpened(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        log.info("messageSent");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        // TODO Auto-generated method stub
        super.sessionClosed(session);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        if (log.isWarnEnabled()) {
            log.warn("EXCEPTION, please implement " + getClass().getName()
                    + ".exceptionCaught() for proper handling:", cause);
        }
    }

}