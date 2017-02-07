package io.netty.example.timeserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *
 * 一般用netty来发送和接收数据都会继承SimpleChannelInboundHandler和ChannelInboundHandlerAdapter这两个抽象类，那么这两个到底有什么区别呢？

 其实用这两个抽象类是有讲究的，在客户端的业务Handler继承的是SimpleChannelInboundHandler，而在服务器端继承的是ChannelInboundHandlerAdapter。

 最主要的区别就是SimpleChannelInboundHandler在接收到数据后会自动release掉数据占用的Bytebuffer资源(自动调用Bytebuffer.release())。
 而为何服务器端不能用呢，因为我们想让服务器把客户端请求的数据发送回去，而服务器端有可能在channelRead方法返回前还没有写完数据，因此不能让它自动release。
 * @author JohnTang
 * @date 2017/2/7
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    private int counter;

    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
        String body = (String) msg;
        System.out.println("Server read: " + body + " , counter is " + ++counter);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(System.currentTimeMillis()).toString() : " BAD ORDER";
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.close();
    }
}
