package cn.turing.firecontrol.server.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
@Slf4j
public class AiDeByteArrayDecoder  extends CumulativeProtocolDecoder {
    private Charset charset;
    public  AiDeByteArrayDecoder (Charset charset){
        this.charset=charset;
    }
    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (in.remaining() > 0) {//有数据时
            byte[] sizeBytes = new byte[in.remaining()];
            in.mark();//标记当前位置，以便reset
            in.get(sizeBytes);//读取前1字节
            String str= Hex.encodeHexString(sizeBytes);
            System.out.println(str);
            if (str.startsWith("55")){
                out.write(str);
            }else{
                log.error("接收到无法识别的数据");
            }
            return false;
        }
        return false;//处理成功，让父类进行接收下个包
    }



}
