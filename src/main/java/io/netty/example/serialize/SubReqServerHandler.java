package io.netty.example.serialize;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author JohnTang
 * @date 2017/2/7
 */
public class SubReqServerHandler extends ChannelInboundHandlerAdapter {

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        SubscribeReq req = (SubscribeReq) msg;
        if("Lilinfeng".equalsIgnoreCase(req.getUserName())){
            System.out.println("Service accept client subscribe req : ["
            + req.toString() +"]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }
    }

    private SubscribeResp resp(int subReqID){
        SubscribeResp resp = new SubscribeResp();
        resp.setSubReqID(subReqID);
        resp.setRespCode(0);
        resp.setDesc("Netty book order succeed, 3days later sent to the designated address");
        return resp;
    }

    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
