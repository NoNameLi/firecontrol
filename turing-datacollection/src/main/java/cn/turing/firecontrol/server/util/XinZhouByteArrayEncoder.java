package cn.turing.firecontrol.server.util;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

public class XinZhouByteArrayEncoder extends ProtocolEncoderAdapter {
    private Charset charset;
    public XinZhouByteArrayEncoder(Charset charset){
        this.charset=charset;
    }
    @Override
    public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput out) throws Exception {

        byte[] bytes = (byte[])o;
        IoBuffer buffer = IoBuffer.allocate(256);
        buffer.setAutoExpand(true);

        buffer.put(bytes);
        buffer.flip();

        out.write(buffer);
        out.flush();
        buffer.free();
    }
}
