package cn.wjc.tool.netty;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import cn.wjc.tool.entity.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class ResponseEncoder extends MessageToByteEncoder<Response<?>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Response<?> msg, ByteBuf out) throws Exception {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(msg);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            out.writeInt(bytes.length); // 写入长度前缀
            out.writeBytes(bytes); // 写入序列化后的对象
        }
    }

}
