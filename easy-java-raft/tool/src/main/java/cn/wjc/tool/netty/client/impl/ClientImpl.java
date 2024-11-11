package cn.wjc.tool.netty.client.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cn.wjc.tool.entity.Request;
import cn.wjc.tool.netty.client.Client;
import cn.wjc.tool.netty.client.ClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.concurrent.GenericFutureListener;

public class ClientImpl implements Client {
    private EventLoopGroup group;
    private final ConcurrentMap<String, Channel> channels = new ConcurrentHashMap<>();

    public ClientImpl() {
        group = new NioEventLoopGroup();
    }

    @Override
    public void init() throws Throwable {
    }

    public void connectToServer(String host, int port, String serverId) throws Throwable {
        // 创建Bootstrap实例
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 设置连接超时时间为10秒
                .channel(NioSocketChannel.class)
                .handler(new ClientInitializer());
        ChannelFuture future = bootstrap.connect(host, port);
        Channel channel = future.channel();
        channels.put(serverId, channel);

        future.addListener(new GenericFutureListener<ChannelFuture>() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // 连接成功
                } else {
                    // 连接失败
                    Throwable cause = future.cause();
                    if (cause instanceof TimeoutException) {
                        // 处理连接超时的情况
                        System.err.println("连接失败，连接超时");
                    } else {
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
        group.shutdownGracefully().sync();
    }

    @Override
    public void send(String serverId, Request request) throws Exception {
        Channel channel = channels.get(serverId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(request);
        } else {
            // 处理无法找到Channel或Channel不活跃的情况
            System.err.println("Cannot send message to server " + serverId + ": Channel is not available.");
        }
    }

    @Override
    public void send(String serverId, Request request, int timeout) throws Exception {
        Channel channel = channels.get(serverId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(request);
        } else {
            // 处理无法找到Channel或Channel不活跃的情况
            System.err.println("Cannot send message to server " + serverId + ": Channel is not available.");
        }
    }

}
