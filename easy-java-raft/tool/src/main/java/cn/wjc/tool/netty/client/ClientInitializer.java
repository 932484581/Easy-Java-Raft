package cn.wjc.tool.netty.client;

import cn.wjc.tool.entity.Node;
import cn.wjc.tool.netty.RequestEncoder;
import cn.wjc.tool.netty.ResponseDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    private ResponseDecoder DECODER = new ResponseDecoder();
    private RequestEncoder ENCODER = new RequestEncoder();
    private static ClientHandler CLIENT_HANDLER;

    public ClientInitializer(Node node) {
        this.CLIENT_HANDLER = new ClientHandler(node);
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);

        pipeline.addLast(CLIENT_HANDLER);
    }
}
