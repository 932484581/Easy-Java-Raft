package cn.wjc.tool.netty.client.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Request;
import cn.wjc.tool.exception.NettyException;
import cn.wjc.tool.netty.client.Client;
import cn.wjc.tool.netty.client.ClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ClientImpl implements Client {
    private ConcurrentMap<String, Channel> channels = new ConcurrentHashMap();
    private Node node;

    @Override
    public void init() throws Throwable {
    }

    public ClientImpl(Node node) {
        this.node = node;
    }

    public void connectToServer(String addr) throws Throwable {
        String[] parts = addr.split(":", 2);
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);
        // 创建Bootstrap实例
        Bootstrap bootstrap = new Bootstrap();
        ClientInitializer clientInitializer = new ClientInitializer(node);
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(clientInitializer);
        ChannelFuture future = bootstrap.connect(host, port).sync();
        Channel channel = future.channel();
        channels.put(addr, channel);

        future.addListener(new GenericFutureListener<ChannelFuture>() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // 连接成功
                    System.out.println(node.peerSet.getSelf().getAddr() + "创建了一个Client，并成功连接:" + host + ":" + port);
                    log.info(node.peerSet.getSelf().getAddr() + "创建了一个Client，并成功连接:" + host + ":" + port);
                } else {
                    // 连接失败
                    Throwable cause = future.cause();
                    if (cause instanceof TimeoutException) {
                        // 处理连接超时的情况
                        log.error(node.peerSet.getSelf().getAddr() + "Client连接超时:" + host + ":" + port);
                    } else {
                        log.error(node.peerSet.getSelf().getAddr() + "Client连接失败:" + host + ":" + port);
                        // 处理其他类型的连接错误
                        cause.printStackTrace();
                    }
                }
            }

        });
    }

    @Override
    public void destroy() throws Throwable {
        for (Channel channel : channels.values()) {
            if (channel.isActive()) {
                channel.close().sync();
            }
        }
    }

    @Override
    public void send(Request request) throws Exception, NettyException {
        log.info("发送请求到：" + request.getAddr());
        Channel channel = channels.get(request.getAddr());

        if (channel != null) {
            channel.writeAndFlush(request);
        } else {
            // 处理无法找到Channel或Channel不活跃的情况
            throw new NettyException("发送消息到(" + request.getAddr() + ")节点失败");
        }
    }
}
