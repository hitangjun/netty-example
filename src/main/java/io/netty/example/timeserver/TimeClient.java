package io.netty.example.timeserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author JohnTang
 * @date 2017/2/7
 */
public class TimeClient {
    public void connect(int port,String host) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class) //设置channel类型

                    /**
                     * http://stephen830.iteye.com/blog/2109006
                     * 为了尽可能的利用网络带宽，TCP总是希望尽可能的发送足够大的数据。
                     * Nagle的算法，该算法的目的就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
                     *
                     *  TCP_NODELAY就是用于启用或关闭Nagle算法。
                     *  如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；
                     *  如果要减少发送次数减少网络交互，就设置为false等累积一定大小后再发送。默认为false。
                     */
                    .option(ChannelOption.TCP_NODELAY,true)
                    /**
                     * handler在初始化时就会执行，而childHandler会在客户端成功connect后才执行，这是两者的区别。
                     */
                    .handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) throws Exception{
                            /**
                             * pipeline是伴随Channel的存在而存在的，交互信息通过它进行传递，
                             * 我们可以addLast（或者addFirst）多个handler，第一个参数是名字，无具体要求，如果填写null，系统会自动命名。
                             */
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new TimeClientHandler());
                        }
                    });

            ChannelFuture f = b.connect(host,port).sync();

            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        int port = 8080;
        if(args!=null && args.length>0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){

            }
        }
        new TimeClient().connect(port,"127.0.0.1");
    }
}
