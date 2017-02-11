package io.netty.example.udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThreadLocalRandom;

/**
 * @author JohnTang
 * @date 2017/2/11
 */
public class ChineseProverServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final String[] DICTIONARY= {"只有功夫深，铁杵磨成针。","旧时王谢堂前燕，飞入寻常百姓家",
            "洛阳亲友如相问，一片冰心在玉壶","一寸光阴一寸金，寸金难买寸光阴。","老骥伏枥，志在千里。烈士暮年，壮心不已"};

    private String nextQuote(){
        int quoteId = ThreadLocalRandom.current().nextInt(DICTIONARY.length);
        return DICTIONARY[quoteId];
    }

    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        String req = msg.content().toString(CharsetUtil.UTF_8);
        System.out.println(req);

        if("谚语".equals(req)){
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("谚语："+nextQuote(), CharsetUtil.UTF_8),
                    msg.sender()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
        cause.printStackTrace();
    }

}
