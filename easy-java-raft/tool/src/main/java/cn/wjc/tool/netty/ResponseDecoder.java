package cn.wjc.tool.netty;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import cn.wjc.tool.entity.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ResponseDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return; // 等待更多数据
        }

        in.markReaderIndex();
        int length = in.readInt();

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return; // 等待更多数据
        }

        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Response wrapper = (Response) objectInputStream.readObject();
        out.add(wrapper);
    }
}
