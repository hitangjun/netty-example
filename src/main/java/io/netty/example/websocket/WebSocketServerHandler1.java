package io.netty.example.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author JohnTang
 * @date 2017/2/9
 */
public class WebSocketServerHandler1 extends SimpleChannelInboundHandler<Object>{

    private WebSocketServerHandshaker handshaker;

    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channel read0");
        if(msg instanceof FullHttpRequest){
            handleHttpRequest(ctx,(FullHttpRequest) msg);
        }else if(msg instanceof WebSocketFrame){
            handleWebSocketFrame(ctx,(WebSocketFrame) msg);
        }
    }

    public void messageReceived(ChannelHandlerContext ctx,Object msg) throws Exception{
        System.out.println("messageReceived");
        if(msg instanceof FullHttpRequest){
            handleHttpRequest(ctx,(FullHttpRequest) msg);
        }else if(msg instanceof WebSocketFrame){
            handleWebSocketFrame(ctx,(WebSocketFrame) msg);
        }
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx,FullHttpRequest req) throws Exception{
        System.out.println("handleHttpRequest");
        if(!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return ;
        }

        WebSocketServerHandshakerFactory wsFactory
                = new WebSocketServerHandshakerFactory(
                "ws://127.0.0.1:8080/websocket",null,false);
        handshaker = wsFactory.newHandshaker(req);

        if(handshaker == null){
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        }else{
            handshaker.handshake(ctx.channel(),req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame){
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(),
                    (CloseWebSocketFrame) frame.retain());
            return ;
        }

        if(frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain())
            );
            return ;
        }

        if(!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported",frame.getClass().getName()
            ));
        }

        String request = ((TextWebSocketFrame) frame).text();
        System.out.println(String.format("%s received %s", ctx.channel(),request));

        ctx.channel().write(
                new TextWebSocketFrame(request
                    + ", 欢迎使用Netty WebSocket 服务，现在时刻："
                    + new java.util.Date().toString())
        );
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, FullHttpResponse res){
        if(res.status().code() !=200){
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            setContentLength(res,res.content().readableBytes());
        }

        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if(!isKeepAlive(req) || res.status().code() != 200){
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
        cause.printStackTrace();
        ctx.close();
    }

}
