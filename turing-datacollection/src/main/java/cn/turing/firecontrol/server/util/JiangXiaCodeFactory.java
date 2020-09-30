package cn.turing.firecontrol.server.util;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

public class JiangXiaCodeFactory implements ProtocolCodecFactory {

    private  JiangXiaByteArrayDecoder decoder;

    private  JiangXiaByteArrayEncoder encoder;

    public JiangXiaCodeFactory(Charset charset){
        encoder= new JiangXiaByteArrayEncoder(charset);
        decoder=  new JiangXiaByteArrayDecoder(charset);
    }
    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }
}
