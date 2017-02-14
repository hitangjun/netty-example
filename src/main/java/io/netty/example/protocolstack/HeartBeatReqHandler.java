package io.netty.example.protocolstack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * @author JohnTang
 * @date 2017/2/13
 */
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {
    private volatile ScheduledFuture<?> heartBeat;

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HeartBeatReqHandler channelActive...");
        heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx),0,15000,
                TimeUnit.MILLISECONDS);
    }

    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {

        NettyMessage message = (NettyMessage) msg;
        System.out.println("HeartBeatReqHandler channelRead..."+msg);

        if(message.getHeader() !=null
                && message.getHeader().getType() == NettyMessageType.LOGIN_RESP.byteValue()){
            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx),0,5000,
                    TimeUnit.MILLISECONDS);
        }else if(message.getHeader().getType() == NettyMessageType.HEARTBEAT_RESP.byteValue()){
            System.out.println("Client receive server heart beat message : ---> "+message);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private class HeartBeatTask implements Runnable{

        private final ChannelHandlerContext ctx;

        public HeartBeatTask(final ChannelHandlerContext ctx){
            this.ctx = ctx;
        }

        public void run() {
            NettyMessage heatBeat = buildHeatBeat();
            System.out.println("Client send heart beat message to server : ---> "+heatBeat);
        }

        private NettyMessage buildHeatBeat(){
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(NettyMessageType.HEARTBEAT_REQ.byteValue());
            message.setHeader(header);
            message.setBody("heart beat request");
            return message;
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        if(heartBeat != null){
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }
}
