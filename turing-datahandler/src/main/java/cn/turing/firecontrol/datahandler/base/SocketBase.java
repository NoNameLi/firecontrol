package cn.turing.firecontrol.datahandler.base;


import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

@Configuration
public class SocketBase {
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
        String data="";
        try {
            socket = new Socket(InetAddress.getByName(ip),port);
            os = socket.getOutputStream();
            os.write(message.getBytes());
            socket.shutdownOutput();
            is = socket.getInputStream();
            byte[] b = new byte[1024];
            int len;
            while((len = is.read(b)) != -1){
                 data = new String(b,0,len);
            }
            //接受到的消息
            System.out.print("客户端接受到的消息 ============"+data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (is != null) is.close();
                if (os != null) os.close();
                if (socket != null) socket.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return data;

    }
    //服务端
    public String server(Integer port){
        ServerSocket ss = null;
        Socket s = null;
        InputStream is = null;
        OutputStream os = null;
        String data="";
        try {
            ss = new ServerSocket(port);
            s = ss.accept();
            is = s.getInputStream();
            byte[] b = new byte[1024];
            int len;
            while((len = is.read(b)) != -1){
                 data = new String(b,0,len);
            }
            os = s.getOutputStream();
            //接受到的消息
            System.out.print("服务端接受到的消息 ============"+data);
            //返回的标识
            os.write("我已收到你的消息".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (os != null) os.close();
                if (is != null) is.close();
                if (s != null) s.close();
                if (ss != null) ss.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * @param port
     */
    public String startSocketServer(int port) {
        String str="";
        try {
            //打开通信信道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            //获取套接字
            ServerSocket serverSocket = serverSocketChannel.socket();
            //绑定端口号
            serverSocket.bind(new InetSocketAddress(port));
            //打开监听器
            selector = Selector.open();
            //将通信信道注册到监听器
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            //监听器会一直监听，如果客户端有请求就会进入相应的事件处理
            while (true) {
                selector.select();//select方法会一直阻塞直到有相关事件发生或超时
                Set<SelectionKey> selectionKeys = selector.selectedKeys();//监听到的事件
                for (SelectionKey key : selectionKeys) {
                    str=handle(key);
                }
                selectionKeys.clear();//清除处理过的事件

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
return str;

    }






        /**
         * 处理不同的事件
         *
         * @param selectionKey
         * @throws IOException
         */
    private String handle(SelectionKey selectionKey) throws IOException {

        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        String msg = "";
        String requestMsg="";
        int count = 0;
        if (selectionKey.isAcceptable()) {

            //每有客户端连接，即注册通信信道为可读
            serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);

        } else if (selectionKey.isReadable()) {

            socketChannel = (SocketChannel) selectionKey.channel();

            rBuffer.clear();

            count = socketChannel.read(rBuffer);

            //读取数据
            if (count > 0) {
                rBuffer.flip();
                msg = String.valueOf(cs.decode(rBuffer).array());
            }

            //String responseMsg = "已收到客户端的消息:" + requestMsg;
            String responseMsg =  msg;

            System.out.println(responseMsg);
            //返回数据
            sBuffer = ByteBuffer.allocate(responseMsg.getBytes("UTF-8").length);
            sBuffer.put(responseMsg.getBytes("UTF-8"));
            sBuffer.flip();
            socketChannel.write(sBuffer);
            socketChannel.close();
        }
        return requestMsg;
    }






}
