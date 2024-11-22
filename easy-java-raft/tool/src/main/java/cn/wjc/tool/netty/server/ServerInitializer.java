package cn.wjc.tool.netty.server;

import cn.wjc.tool.entity.Node;
import cn.wjc.tool.netty.RequestDecoder;
import cn.wjc.tool.netty.ResponseEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    private RequestDecoder DECODER;
    private ResponseEncoder ENCODER;

    private ServerHandler SERVER_HANDLER;

    public ServerInitializer(Node node) {
        SERVER_HANDLER = new ServerHandler(node);
        this.DECODER = new RequestDecoder();
        this.ENCODER = new ResponseEncoder();
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new RequestDecoder());
        pipeline.addLast(ENCODER);

        // and then business logic.
        pipeline.addLast(SERVER_HANDLER);
    }
}
