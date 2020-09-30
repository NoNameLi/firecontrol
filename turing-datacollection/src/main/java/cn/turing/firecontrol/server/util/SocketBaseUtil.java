package cn.turing.firecontrol.server.util;



import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
@Configuration
public class SocketBaseUtil {
    //解码buffer
    private Charset cs = Charset.forName("UTF-8");
        //接受数据缓冲区
    private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);
        //发送数据缓冲区
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);
        //选择器（叫监听器更准确些吧应该）
    private static Selector selector;

    //客户端
    public String client(String ip,Integer port,String message){
        Socket socket = null;
        OutputStream os = null;
        InputStream is = null;
        String data=message+"\n";
        try {
            System.out.println(ip+port);
            socket = new Socket(InetAddress.getByName(ip),port);

            os = socket.getOutputStream();
            os.write(data.getBytes(cs));
            socket.shutdownOutput();
            //接受到的消息
            System.out.print("客户端接受到的消息 ============"+message);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (is != null) is.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                if (os != null) os.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                if (socket != null) socket.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return data;

    }

}
