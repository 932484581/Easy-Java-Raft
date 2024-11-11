package cn.wjc.tool.netty.server;

import cn.wjc.tool.entity.Request;
import cn.wjc.tool.entity.Response;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

@Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Request> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel socketChannel = (SocketChannel) ctx.channel();
        int localPort = socketChannel.localAddress().getPort();
        System.out.println("连接到端口: " + localPort);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        // Generate and write a response.
        Response response = Response.builder().result("已经接收到了数据" + request).build();
        if (request != null) {
            System.out.println("接受到了请求信息：" + request);
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
