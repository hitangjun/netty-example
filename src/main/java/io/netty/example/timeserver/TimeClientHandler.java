package io.netty.example.timeserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

/**
 *
 * 一般用netty来发送和接收数据都会继承SimpleChannelInboundHandler和ChannelInboundHandlerAdapter这两个抽象类，那么这两个到底有什么区别呢？

 其实用这两个抽象类是有讲究的，在客户端的业务Handler继承的是SimpleChannelInboundHandler，而在服务器端继承的是ChannelInboundHandlerAdapter。

 最主要的区别就是SimpleChannelInboundHandler在接收到数据后会自动release掉数据占用的Bytebuffer资源(自动调用Bytebuffer.release())。
 而为何服务器端不能用呢，因为我们想让服务器把客户端请求的数据发送回去，而服务器端有可能在channelRead方法返回前还没有写完数据，因此不能让它自动release。
 * @author JohnTang
 * @date 2017/2/7
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());
    private int counter;
    private byte[] req;

    public TimeClientHandler(){
        req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
    }

    public void channelActive(ChannelHandlerContext ctx){
        ByteBuf message = null;
        System.out.println("time client active");
        for(int i=0;i<100;i++){
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
        String body = (String) msg;
        //sout
        System.out.println("Now is :" + body + " ; the counter is : " + ++counter);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warning("Unexcepted exception form downstream : " + cause.getMessage());
        ctx.close();
    }

    //psvm
    public static void main(String[] args) {

    }
}
