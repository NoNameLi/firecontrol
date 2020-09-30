package cn.turing.firecontrol.server.util;



import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

public class AiDeByteCodeFactory implements ProtocolCodecFactory {
    private  AiDeByteArrayDecoder decoder;

    private  AiDeByteArrayEncoder encoder;

    public  AiDeByteCodeFactory(Charset charset){
        encoder= new AiDeByteArrayEncoder(charset);
        decoder=  new AiDeByteArrayDecoder(charset);

    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }
}
