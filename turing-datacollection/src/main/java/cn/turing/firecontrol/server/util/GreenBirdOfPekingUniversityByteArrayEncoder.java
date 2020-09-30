package cn.turing.firecontrol.server.util;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

public class GreenBirdOfPekingUniversityByteArrayEncoder extends ProtocolEncoderAdapter {

    private Charset charset;
    public GreenBirdOfPekingUniversityByteArrayEncoder(Charset charset){
        this.charset=charset;
    }

    @Override
    public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
        byte[] p = (byte[]) o;
        protocolEncoderOutput.write(IoBuffer.wrap(p));
        protocolEncoderOutput.flush();
    }
}
