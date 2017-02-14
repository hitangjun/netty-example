package io.netty.example.protocolstack;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author JohnTang
 * @date 2017/2/13
 */
public class NettyClient {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    EventLoopGroup group = new NioEventLoopGroup();
    public void connect(int port,String host) throws Exception{
        try{
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>(){

                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new NettyMessageDecoder(1024*1024,4,4,-8,0)
                            );
                            ch.pipeline().addLast("MessageEncoder",new NettyMessageEncoder());
                            ch.pipeline().addLast("readTimeOutHandler",new ReadTimeoutHandler(50));
//                            ch.pipeline().addLast("LoginAuthHandler",new LoginAuthReqHandler());
                            ch.pipeline().addLast("HeartBeatReqHandler",new HeartBeatReqHandler());
                        }
                    });
            ChannelFuture future = b.connect(
                    new InetSocketAddress(host,port),
                    new InetSocketAddress("192.168.1.150",12345)
                    ).sync();
            future.channel().closeFuture().sync();
        }finally {
            executor.execute(new Runnable(){

                public void run() {
                    try{
                        ByteBuf a;
                        ByteBuffer b;
                        TimeUnit.SECONDS.sleep(5);
                        try{
                            connect(9080,"192.168.1.150");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyClient().connect(9080,"192.168.1.150");
    }

}
