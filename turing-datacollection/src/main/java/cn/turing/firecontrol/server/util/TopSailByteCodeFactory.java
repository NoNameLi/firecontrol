package cn.turing.firecontrol.server.util;



import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

public class TopSailByteCodeFactory implements ProtocolCodecFactory {

    private  TopSailByteArrayDecoder decoder;

    private  TopSailByteArrayEncoder encoder;

    public TopSailByteCodeFactory(Charset charset){
        encoder= new TopSailByteArrayEncoder(charset);
        decoder=  new TopSailByteArrayDecoder(charset);
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
