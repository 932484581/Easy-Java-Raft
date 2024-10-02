package cn.wjc.tool.rpc;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AbstractUserProcessor;

import cn.wjc.tool.entity.RpcRequest;
import cn.wjc.tool.exception.RaftException;

public abstract class RaftUserProcessor<T> extends AbstractUserProcessor<T> {

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, T request) {
        throw new RaftException(
                "Raft Server not support handleRequest(BizContext bizCtx, AsyncContext asyncCtx, T request) ");
    }

    @Override
    public String interest() {
        return RpcRequest.class.getName();
    }
}
