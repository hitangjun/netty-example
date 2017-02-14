package io.netty.example.protocolstack;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author JohnTang
 * @date 2017/2/13
 */
public class NettyServer {

    public void bind() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new NettyMessageDecoder(1024*1024,4,4,-8,0)
                        );
                        ch.pipeline().addLast(
                                new NettyMessageEncoder()
                        );
                        ch.pipeline().addLast("readTimeOut",new ReadTimeoutHandler(50));
//                        ch.pipeline().addLast(new LoginAuthRespHandler());
                        ch.pipeline().addLast("HeartBeatRespHandler",new HeartBeatRespHandler());
                    }
                });
        b.bind("192.168.1.150",9080).sync();
        System.out.println("Netty server start ok : "
                + "192.168.1.150:9080");
    }


    public static void main(String[] args) throws Exception {
        new NettyServer().bind();
    }
}
