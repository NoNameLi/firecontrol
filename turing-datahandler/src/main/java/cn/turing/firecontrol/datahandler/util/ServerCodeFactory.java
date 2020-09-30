package cn.turing.firecontrol.datahandler.util;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineDecoder;
import org.apache.mina.filter.codec.textline.TextLineEncoder;

public  class ServerCodeFactory implements ProtocolCodecFactory {

    private final TextLineEncoder encoder;
    private final TextLineDecoder decoder;
    /*final static char endchar = 0x1a;*/
    final static char endchar = 0x0d;
    public ServerCodeFactory() {
        this(Charset.forName("utf-8"));
    }
    public ServerCodeFactory(Charset charset) {
        encoder = new TextLineEncoder(charset, LineDelimiter.UNIX);
        decoder = new TextLineDecoder(charset, LineDelimiter.AUTO);
        encoder.setMaxLineLength(Integer.MAX_VALUE);// 设定后服务器可以接收大数据
        decoder.setMaxLineLength(Integer.MAX_VALUE);
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        return decoder;
    }
    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        return encoder;
    }
    public int getEncoderMaxLineLength() {
        return encoder.getMaxLineLength();
    }
    public void setEncoderMaxLineLength(int maxLineLength) {
        encoder.setMaxLineLength(maxLineLength);
    }
    public int getDecoderMaxLineLength() {
        return decoder.getMaxLineLength();
    }
    public void setDecoderMaxLineLength(int maxLineLength) {
        decoder.setMaxLineLength(maxLineLength);
    }


}
