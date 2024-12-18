package cn.wjc.server.msg;

import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Request;
import cn.wjc.tool.entity.Response;

public interface MsgProcessing {
    public void recRes(Response response, Node node);

    public Response recReq(Request request, Node node);

    // 接收到指令
    public void recCommandRes(Response response, Node node);

    // 接收到指令响应,返回结果
    public Response recCommandReq(Request request, Node node);

    // 接收到投票响应信息
    public void recVoteRes(Response response, Node node);

    // 接收到投票请求信息，返回响应
    public Response recVoteReq(Request request, Node node);

    // 接收到日志响应信息
    public void recAentryRes(Response response, Node node);

    // 接收到日志请求信息，返回响应 || 接收到心跳请求，应用日志
    public Response recAentryReq(Request request, Node node);

}
