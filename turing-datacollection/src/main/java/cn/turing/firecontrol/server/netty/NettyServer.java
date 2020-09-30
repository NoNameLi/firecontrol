package cn.turing.firecontrol.server.netty;


import cn.turing.firecontrol.server.controller.LIDaHandler;
import cn.turing.firecontrol.server.controller.SanJiangSea2Handler;
import cn.turing.firecontrol.server.controller.SanJiangSea3Handler;
import cn.turing.firecontrol.server.controller.SanJiangSeaHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class NettyServer {


    @Qualifier("executorGroup")
    @Autowired
    EventExecutorGroup eventExecutors;

    @Autowired
    LIDaHandler liDaHandler;

    @Autowired
    SanJiangSeaHandler sanJiangSeaHandler;

    @Autowired
    SanJiangSea2Handler sanJiangSea2Handler;

    @Autowired
    SanJiangSea3Handler sanJiangSea3Handler;

    public String wisdomFactoryServer(Integer port) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    //可以在socket接上来的时候添加很多指定义逻辑
                    ch.pipeline().addLast("HexDecode",new LengthFieldBasedFrameDecoder(1024,2,2,0,0));
                    ch.pipeline().addLast("StringDecode", new XBoosByteToStringDecoder());
                    ch.pipeline().addLast("encode", new ByteArrayEncoder());
                    //    ch.pipeline().addLast("ping", new IdleStateHandler(25, 15, 10, TimeUnit.SECONDS));
                    ch.pipeline().addLast(eventExecutors,liDaHandler);
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024 * 5);
            bootstrap.option(ChannelOption.SO_RCVBUF,1024 *3);
            bootstrap.option(ChannelOption.SO_SNDBUF,1024 *3);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);// (6)
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // Bind and start to accept incoming connections. 
            ChannelFuture future = bootstrap.bind(port).sync();
            //         logger.info("server started ,listen {}" ,port);
            log.info("开始监听 利达华信消防主机"+ port);
            //启动一个线程 来给客户端发消息
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

        return "msg:{成功}";
    }

    public String sanjiangSeaServer(Integer port) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        System.out.println();
        try {
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    //可以在socket接上来的时候添加很多指定义逻辑
//                    ch.pipeline().addLast("HexDecode",new LengthFieldBasedFrameDecoder(1024,2,2,0,0));//af  = "¯"
//                    ch.pipeline().addLast("sss",new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer(new byte[]{-81})));
                    ch.pipeline().addLast("StringDecode", new SanJiangByteToStringDecoder());
                    ch.pipeline().addLast("encode", new ByteArrayEncoder());
                    //    ch.pipeline().addLast("ping", new IdleStateHandler(25, 15, 10, TimeUnit.SECONDS));
                    ch.pipeline().addLast(eventExecutors,sanJiangSeaHandler);
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024 * 5);
            bootstrap.option(ChannelOption.SO_RCVBUF,1024 *3);
            bootstrap.option(ChannelOption.SO_SNDBUF,1024 *3);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);// (6)
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // Bind and start to accept incoming connections. 
            ChannelFuture future = bootstrap.bind(port).sync();
            //         logger.info("server started ,listen {}" ,port);
            log.info("开始监听 泛海三江消防主机"+ port);
            //启动一个线程 来给客户端发消息
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

        return "msg:{成功}";
    }

    public String sanjiangSeaServer8894(Integer port) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        System.out.println();
        try {
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    //可以在socket接上来的时候添加很多指定义逻辑
//                    ch.pipeline().addLast("sss",new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer(new byte[]{-81})));
//                    ch.pipeline().addLast("HexDecode",new LengthFieldBasedFrameDecoder(1024,2,2,0,0));
                    ch.pipeline().addLast("StringDecode", new SanJiangByteToStringDecoder());
                    ch.pipeline().addLast("encode", new ByteArrayEncoder());
                    //    ch.pipeline().addLast("ping", new IdleStateHandler(25, 15, 10, TimeUnit.SECONDS));
                    ch.pipeline().addLast(eventExecutors,sanJiangSea2Handler);
                }
            });

            bootstrap.option(ChannelOption.SO_BACKLOG, 1024 * 5);
            bootstrap.option(ChannelOption.SO_RCVBUF,1024 *3);
            bootstrap.option(ChannelOption.SO_SNDBUF,1024 *3);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);// (6)
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // Bind and start to accept incoming connections. 
            ChannelFuture future = bootstrap.bind(port).sync();
            //         logger.info("server started ,listen {}" ,port);
            log.info("开始监听 泛海三江消防主机"+ port);
            //启动一个线程 来给客户端发消息
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

        return "msg:{成功}";
    }

    public String sanjiangSeaServer8892(int port) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        System.out.println();
        try {
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    //可以在socket接上来的时候添加很多指定义逻辑
//                    ch.pipeline().addLast("HexDecode",new LengthFieldBasedFrameDecoder(1024,2,2,0,0));
//                    ch.pipeline().addLast("sss",new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer(new byte[]{-81})));
                    ch.pipeline().addLast("StringDecode", new SanJiangByteToStringDecoder());
                    ch.pipeline().addLast("encode", new ByteArrayEncoder());
                    //    ch.pipeline().addLast("ping", new IdleStateHandler(25, 15, 10, TimeUnit.SECONDS));
                    ch.pipeline().addLast(eventExecutors,sanJiangSea3Handler);
                }
            });

            bootstrap.option(ChannelOption.SO_BACKLOG, 1024 * 5);
            bootstrap.option(ChannelOption.SO_RCVBUF,1024 *3);
            bootstrap.option(ChannelOption.SO_SNDBUF,1024 *3);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);// (6)
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // Bind and start to accept incoming connections. 
            ChannelFuture future = bootstrap.bind(port).sync();
            //         logger.info("server started ,listen {}" ,port);
            log.info("开始监听 泛海三江消防主机"+ port);
            //启动一个线程 来给客户端发消息
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

        return "msg:{成功}";
    }

}
