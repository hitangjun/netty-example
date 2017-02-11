package io.netty.example.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author JohnTang
 * @date 2017/2/9
 */
public class WebSocketServer1 {
    public void run(int port) throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch){
                            ChannelPipeline pipeline = ch.pipeline();
                            //将请求和应答消息编码或者解码为HTTP消息
                            pipeline.addLast("http-codec",new HttpServerCodec());
                            //将http消息的多个部分组成一条完整的http消息
                            pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
                            //向客户端发送HTML5文件，用于支持浏览器和服务端进行websocket通讯
                            pipeline.addLast("http-chunked",new ChunkedWriteHandler());
                            pipeline.addLast("handler",new WebSocketServerHandler1());
                        }
                    });

            Channel ch = b.bind(port).sync().channel();
            System.out.println("WebSocket server start run on port:"+port+".");
            System.out.println("Open your browser and navigate to ws://127.0.0.1:"+port+"/");
            ch.closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new WebSocketServer1().run(8080);
    }
}
