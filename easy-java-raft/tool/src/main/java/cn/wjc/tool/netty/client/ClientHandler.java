package cn.wjc.tool.netty.client;

import cn.wjc.tool.entity.Response;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class ClientHandler extends SimpleChannelInboundHandler<Response<?>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response<?> msg) throws Exception {
        System.out.println("接收到了返回的消息" + (Response<String>) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
