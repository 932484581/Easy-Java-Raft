package cn.wjc.tool.netty.client;

import cn.wjc.server.msg.impl.MsgProcessingImpl;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Response;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Sharable
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Response<?>> {
    private Node node;
    private MsgProcessingImpl msgProcessingImpl = new MsgProcessingImpl();

    public ClientHandler(Node node) {
        this.node = node;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response<?> msg) throws Exception {
        msgProcessingImpl.recRes(msg, node);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
