package cn.turing.firecontrol.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class SanJiangByteToStringDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int readableBytes = byteBuf.readableBytes();
        byteBuf.markReaderIndex();
        String data="";
        byte[] bytes = new byte[readableBytes];
        byteBuf.readBytes(bytes);//读字节
        data = Hex.encodeHexString(bytes);
        log.info("原始数据》》》："+data);
        if (data.contains("e0")){

            int k=appearNumber(data,"e0");
            String message=data;
            for (int i=1;i<=k;i++){

                int type=message.indexOf("e0");

                int hh=message.indexOf("af",type);
                //如果没有，说明出现的断包，
                if (hh==-1){
                    byteBuf.resetReaderIndex();
                    return;
                }
                String raw=message.substring(type-8,hh+4);
                list.add(raw);
                message=message.substring(hh+4);

            }


        }else{
            list.add(data);
        }

//        if (!data.substring(data.length()-4,data.length()-2).equals("af")){
//            byteBuf.resetReaderIndex();
//            return;
//        }else{
//            list.add(data);
//        }

//        if (data.startsWith("aaaaaaaa")){
//            ReferenceCountUtil.retain(data);
//            list.add(data);
//        }else{
//            data=data.substring(2);
//            ReferenceCountUtil.retain(data);
//            list.add(data);
//        }

    }

    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }
}
