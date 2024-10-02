package cn.wjc.tool.rpc;

import cn.wjc.tool.entity.RpcRequest;

public interface RpcClient extends LifeCycle {
    /**
     * @description: 发送请求并同步等待返回
     * @param {Request} request
     * @return {*}
     * @author: WJC
     */
    <R> R send(RpcRequest request);

    <R> R send(RpcRequest request, int timeout);
}
