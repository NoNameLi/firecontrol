package cn.turing.firecontrol.server.util;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

public class AiDeByteArrayEncoder extends ProtocolEncoderAdapter {
    private Charset charset;
    public  AiDeByteArrayEncoder(Charset charset){
        this.charset=charset;
    }
    @Override
    public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {

        protocolEncoderOutput.write(o);
        protocolEncoderOutput.flush();
    }
}
