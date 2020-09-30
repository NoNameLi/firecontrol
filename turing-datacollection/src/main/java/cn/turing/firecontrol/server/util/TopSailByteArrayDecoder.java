package cn.turing.firecontrol.server.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
@Slf4j
public class TopSailByteArrayDecoder extends CumulativeProtocolDecoder {
    private Charset charset;
    public TopSailByteArrayDecoder(Charset charset){
        this.charset=charset;
    }
    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

           if (in.remaining() > 4) {//数据长度必须大于4，才有分析的必要。
               byte[] sizeBytes = new byte[in.remaining()];
               in.mark();//标记当前位置，以便reset
               in.get(sizeBytes);//读取前4字节
               String input = bytesToHexString(sizeBytes);
               if (input.startsWith("7e7e7e7e")) {
                   out.write(input);
               }else if (input.equals("4a44436c6f7564")){
                   System.out.println("error:+");
                   out.write(input);
               } else{
                   log.error("接收到无法识别的数据");
               }
               return false;
           }

        return false;//处理成功，让父类进行接收下个包
    }


    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
