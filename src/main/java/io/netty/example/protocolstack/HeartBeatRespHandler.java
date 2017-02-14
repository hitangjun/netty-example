package io.netty.example.protocolstack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author JohnTang
 * @date 2017/2/13
 */
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{

        NettyMessage message = (NettyMessage) msg;
        System.out.println("HeartBeatRespHandler channelRead..."+msg);
        if(message.getHeader() != null
                && message.getHeader().getType() == NettyMessageType.HEARTBEAT_REQ.byteValue()){
            System.out.println("Receive client heart beat message : ---> " + message);
            NettyMessage heartBeat = buildHeatBeat();
            System.out.println("Send heart beat respinse message to client : ---> " +heartBeat);
            ctx.writeAndFlush(heartBeat);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildHeatBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(NettyMessageType.HEARTBEAT_RESP.byteValue());
        message.setHeader(header);
        message.setBody("heartbeat response");
        return message;
    }
}
