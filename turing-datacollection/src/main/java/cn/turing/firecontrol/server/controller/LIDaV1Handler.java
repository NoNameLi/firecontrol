package cn.turing.firecontrol.server.controller;

import cn.turing.common.entity.lidahuaxin.FireHostDeviceV1;
import cn.turing.firecontrol.server.service.FireHostService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
@Slf4j
public class LIDaV1Handler extends SimpleChannelInboundHandler<String> {
    @Autowired
    FireHostService fireHostService;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {

        log.info("收到利达主机的数据体育馆:"+s);
//        FireHostDevice fireHostDevice=new FireHostDevice(s);
        FireHostDeviceV1 fireHostDevice=new FireHostDeviceV1(s);
        fireHostService.readLiDaFireHost(fireHostDevice);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

}