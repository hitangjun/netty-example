package io.netty.example.protocolstack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JohnTang
 * @date 2017/2/13
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {

    private Map<String,Boolean> nodeCheck = new ConcurrentHashMap<String,Boolean>();
    private String[] whitekList = {"127.0.0.1","192.168.1.150"};

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("LoginAuthReqHandler channelActive...");
        ctx.writeAndFlush(buildLoginReq());
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println("LoginAuthReqHandler channelRead..."+msg);

        NettyMessage message = (NettyMessage)msg;
        if(message.getHeader() != null
                && message.getHeader().getType() == NettyMessageType.LOGIN_RESP.byteValue()){
            byte loginResult = (Byte) message.getBody();
            if(loginResult != (byte) 0){
                ctx.close();
            }else{
                System.out.println("login is ok : "+message);
                ctx.fireChannelRead(msg);
            }
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildLoginReq(){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(NettyMessageType.LOGIN_REQ.byteValue());
        message.setHeader(header);
        return message;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
