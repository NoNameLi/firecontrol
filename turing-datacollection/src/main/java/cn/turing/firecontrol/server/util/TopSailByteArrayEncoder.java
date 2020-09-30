package cn.turing.firecontrol.server.util;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

public class TopSailByteArrayEncoder extends ProtocolEncoderAdapter {
    private Charset charset;
    public TopSailByteArrayEncoder(Charset charset){
        this.charset=charset;
    }
    @Override
    public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
        System.out.println("oooooo:"+o.toString());
       if (o.toString().equals("success") || o.toString().equals("error")){
           protocolEncoderOutput.write(o);
           protocolEncoderOutput.flush();
       }else {
           byte[] p = (byte[]) o;
           protocolEncoderOutput.write(IoBuffer.wrap(p));
           protocolEncoderOutput.flush();
       }
    }
}
