package io.netty.example.timeserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author JohnTang
 * @date 2017/2/7
 */
public class TimeServer {

    public void bind(int port) throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            /**
             * 如果仅由一个EventLoopGroup处理所有请求和连接的话，在并发量很大的情况下，
             * 这个EventLoopGroup有可能会忙于处理已经接收到的连接而不能及时处理新的连接请求，
             * 用两个的话，会有专门的线程来处理连接请求，不会导致请求超时的情况，大大提高了并发处理能力。
             */
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class) //设置channel类型
                    /**
                     * 这个都是socket的标准参数，并不是netty自己的。

                     具体为：

                     ChannelOption.SO_BACKLOG, 1024
                     BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。

                     ChannelOption.SO_KEEPALIVE, true
                     是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。

                     ChannelOption.TCP_NODELAY, true
                     在TCP/IP协议中，无论发送多少数据，总是要在数据前面加上协议头，同时，对方接收到数据，也需要发送ACK表示确认。为了尽可能的利用网络带宽，TCP总是希望尽可能的发送足够大的数据。
                     这里就涉及到一个名为Nagle的算法，该算法的目的就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。TCP_NODELAY
                     就是用于启用或关于Nagle算法。如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；如果要减少发送次数减少网络交互，就设置为false等累积一定大小后再发送。默认为false。
                     */
                    .option(ChannelOption.SO_BACKLOG,1024)
                    /**
                     * handler在初始化时就会执行，而childHandler会在客户端成功connect后才执行，这是两者的区别。
                     */
                    .childHandler(new ChildChannelHandler());

            /**
             * Netty中的IO操作是异步的，包括bind、write、connect等操作会简单的返回一个ChannelFuture，调用者并不能立刻获得结果。
             当future对象刚刚创建时，处于非完成状态。
             可以通过isDone()方法来判断当前操作是否完成。
             通过isSuccess()判断已完成的当前操作是否成功，getCause()来获取已完成的当前操作失败的原因，
             isCancelled()来判断已完成的当前操作是否被取消。
             调用者可以通过返回的ChannelFuture来获取操作执行的状态，注册监听函数来执行完成后的操作。
             */
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            socketChannel.pipeline().addLast(new StringDecoder());
            socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if(args != null && args.length > 0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){
            }
        }
        new TimeServer().bind(port);
    }

}
