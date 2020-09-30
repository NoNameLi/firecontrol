package cn.turing.firecontrol.server.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

@Slf4j
public class ShengZhiByteArrayDecoder extends CumulativeProtocolDecoder {
    private Charset charset;
    public ShengZhiByteArrayDecoder(Charset charset){
        this.charset=charset;
    }
    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (in.remaining()>0){//数据长度必须大于4，才有分析的必要。
            byte[] sizeBytes = new byte[in.remaining()];
            in.mark();//标记当前位置，以便reset
            in.get(sizeBytes);//读取前4字节
            String input = Hex.encodeHexString(sizeBytes);
            System.out.println("input:"+input);
            String sub="a55a";
            if (input.startsWith("a55a0104")){
                int num= ByteUtil.filter(input,sub);
                if (num==2){
                    int first=0;
                    for (int i=0;i<num;i++){
                        String data="";
                        if (first==0){
                            data=input.substring(first,input.indexOf("a55a", input.indexOf("a55a") + 1+i));
                        }else{
                            data=input.substring(first);
                        }
                        first=input.indexOf("a55a", input.indexOf("a55a") + 1+i);
                        out.write(data);
                    }
                }else{
                    out.write(input);
                }
            }else{
                log.info("数据格式不正确");
            }
            return false;
        }
        return false;//处理成功，让父类进行接收下个包
    }


}
