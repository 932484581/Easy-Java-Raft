package cn.wjc.tool.netty;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import cn.wjc.tool.entity.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RequestDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) { // 至少需要4个字节来读取长度前缀
            return;
        }

        in.markReaderIndex(); // 标记当前读取位置
        int length = in.readInt(); // 读取长度前缀

        if (in.readableBytes() < length) { // 如果没有足够的字节来读取完整的对象
            in.resetReaderIndex(); // 重置读取位置，等待更多数据
            return;
        }

        byte[] bytes = new byte[length];
        in.readBytes(bytes); // 读取对象字节

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Request user = (Request) objectInputStream.readObject();
        out.add(user); // 将解码后的对象添加到输出列表中

    }

}
