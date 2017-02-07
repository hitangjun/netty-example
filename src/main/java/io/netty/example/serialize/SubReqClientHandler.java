package io.netty.example.serialize;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author JohnTang
 * @date 2017/2/7
 */
public class SubReqClientHandler extends ChannelInboundHandlerAdapter {

    public SubReqClientHandler(){

    }

    public void channelActive(ChannelHandlerContext ctx){
        for (int i = 0; i < 10; i++) {
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    private SubscribeReq subReq(int i){
        SubscribeReq req = new SubscribeReq();
        req.setAddress("中山路");
        req.setPhoneNumber("138xxxxxxx"+i);
        req.setProductName("Netty");
        req.setSubReqID(i);
        req.setUserName("Lilinfeng");
        return req;
    }

    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
        System.out.println("Receive server response : [" + msg +"]");
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
