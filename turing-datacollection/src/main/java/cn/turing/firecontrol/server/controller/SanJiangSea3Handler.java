package cn.turing.firecontrol.server.controller;

import cn.turing.common.entity.sanjiangsea.SanJiangSeaDevice;
import cn.turing.firecontrol.server.service.SanJiangSeaService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TB_Arm485_Ver2.4
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class SanJiangSea3Handler extends SimpleChannelInboundHandler<String> {

    @Autowired
    SanJiangSeaService sanJiangSeaService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        log.info("收到有效数据三江   8892："+s);
        //判断是否是有效数据
        if (s.substring(8,10).equals("e0")&& s.substring(14,16).equals("01")){
            SanJiangSeaDevice device=new SanJiangSeaDevice(s);
            device.setPort("8892");
            sanJiangSeaService.ReadSanJiangSea(device);
        }else{
            log.error("无用数据不作处理");
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }
}
