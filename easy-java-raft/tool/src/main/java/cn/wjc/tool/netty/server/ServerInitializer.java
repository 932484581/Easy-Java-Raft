package cn.wjc.tool.netty.server;

import cn.wjc.tool.netty.RequestDecoder;
import cn.wjc.tool.netty.ResponseEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    private RequestDecoder DECODER = new RequestDecoder();
    private ResponseEncoder ENCODER = new ResponseEncoder();

    private static final ServerHandler SERVER_HANDLER = new ServerHandler();

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);

        // and then business logic.
        pipeline.addLast(SERVER_HANDLER);
    }
}
