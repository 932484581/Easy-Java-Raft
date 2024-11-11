package cn.wjc.tool.netty.server.impl;

import cn.wjc.tool.netty.server.Server;
import cn.wjc.tool.netty.server.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerImpl implements Server {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    public int port;
    public ChannelFuture channelFuture;

    @Override
    public void init() throws Throwable {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // 设置 NioServerSocketChannel 的处理器
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ServerInitializer());
        channelFuture = b.bind(port);
    }

    @Override
    public void destroy() throws Throwable {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public void testwait() throws InterruptedException {
        channelFuture.channel().closeFuture().sync();
    }
}
