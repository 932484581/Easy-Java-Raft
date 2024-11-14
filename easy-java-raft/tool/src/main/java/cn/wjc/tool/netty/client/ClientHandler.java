package cn.wjc.tool.netty.client;

import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Request;
import cn.wjc.tool.entity.Response;
import cn.wjc.tool.entity.RvoteResult;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Sharable
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Response<?>> {
    private Node node;

    public ClientHandler(Node node) {
        this.node = node;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response<?> msg) throws Exception {
        if (msg.type == -1)
            System.out.println("接收到了返回的消息" + (Response<String>) msg);
        if (msg.type == Request.R_VOTE) {
            Response<RvoteResult> rvoResult = (Response<RvoteResult>) msg;
            log.info("接收到投票请求信息");
            if (rvoResult.getResult().getReceiveTerm() != node.currentTerm) {
                log.error("这个消息与当前任期不匹配，可能过时了，当前任期为{}，接收到的任期为{}", node.getCurrentTerm(),
                        rvoResult.getResult().getReceiveTerm());
            } else {
                // 接收到了投票的请求信息
                if (rvoResult.getResult().isVoteGranted()) {
                    node.leaderRequest.put(rvoResult.addr, true);
                } else {
                    node.leaderRequest.put(rvoResult.addr, false);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
