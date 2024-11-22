package cn.wjc.tool.netty.client;

import cn.wjc.tool.entity.Node;
import cn.wjc.tool.netty.RequestEncoder;
import cn.wjc.tool.netty.ResponseDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    private ResponseDecoder DECODER;
    private RequestEncoder ENCODER;
    private ClientHandler CLIENT_HANDLER;

    public ClientInitializer(Node node) {
        this.CLIENT_HANDLER = new ClientHandler(node);
        this.DECODER = new ResponseDecoder();
        this.ENCODER = new RequestEncoder();
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(ENCODER);
        pipeline.addLast(DECODER);
        pipeline.addLast(CLIENT_HANDLER);
    }
}
