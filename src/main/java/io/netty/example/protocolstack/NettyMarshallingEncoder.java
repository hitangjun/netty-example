package io.netty.example.protocolstack;

/**
 * @author JohnTang
 * @date 2017/2/13
 */
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingEncoder;

public class NettyMarshallingEncoder extends MarshallingEncoder{

    public NettyMarshallingEncoder(MarshallerProvider provider) {
        super(provider);
    }

    public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception{
        super.encode(ctx, msg, out);
    }

}
