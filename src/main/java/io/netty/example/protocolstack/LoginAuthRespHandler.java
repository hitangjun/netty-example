package io.netty.example.protocolstack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JohnTang
 * @date 2017/2/13
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

    private Map<String,Boolean> nodeCheck = new ConcurrentHashMap<String,Boolean>();
    private String[] whitekList = {"127.0.0.1","192.168.1.150"};

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        NettyMessage message = (NettyMessage)msg;
        System.out.println("LoginAuthRespHandler channelRead..."+msg);

        if(message.getHeader() != null
                && message.getHeader().getType() == NettyMessageType.LOGIN_REQ.byteValue()){
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;

            if(nodeCheck.containsKey(nodeIndex)){
                loginResp = buildResponse((byte) -1);
            }else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                for(String WIP : whitekList){
                    if(WIP.equals(ip)){
                        isOK = true;
                        break;
                    }
                }

                loginResp = isOK ? buildResponse((byte) 0)
                        : buildResponse((byte) -1);
                if(isOK){
                    nodeCheck.put(nodeIndex,true);
                }
            }
                System.out.println("The login response is : " + loginResp
                        + " body ["+ loginResp.getBody() + "]");
                ctx.writeAndFlush(loginResp);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildResponse(byte result){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(NettyMessageType.LOGIN_RESP.byteValue());
        message.setHeader(header);
        message.setBody(result);
        return message;
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
