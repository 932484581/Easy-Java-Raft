package cn.wjc.tool.netty.server.impl;

import cn.wjc.tool.entity.Node;
import cn.wjc.tool.netty.server.Server;
import cn.wjc.tool.netty.server.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ServerImpl implements Server {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    public int port;
    public ChannelFuture channelFuture;
    private Node node;

    public ServerImpl(Node node) {
        this.node = node;
        String[] parts = node.peerSet.getSelf().getAddr().split(":", 2);
        this.port = Integer.parseInt(parts[1]);
    }

    @Override
    public void init() throws Throwable {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // 设置 NioServerSocketChannel 的处理器
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ServerInitializer(node));
        channelFuture = b.bind(port);
        log.info("启动了一个Server，端口为：" + port);
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
