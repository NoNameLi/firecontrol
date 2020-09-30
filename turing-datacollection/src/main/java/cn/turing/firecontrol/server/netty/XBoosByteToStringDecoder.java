package cn.turing.firecontrol.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.util.List;

@Slf4j
public class XBoosByteToStringDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int readableBytes = byteBuf.readableBytes();
        //假设需要解析 int 类型的消息（int 4个字节）
        if (readableBytes > 0) {
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);//读字节
            String data = Hex.encodeHexString(bytes);
            if (data.startsWith("aa55") &&data.endsWith("a55a")) {
                ReferenceCountUtil.retain(data);
                list.add(data);
            }else{

            }
        }
    }
}
