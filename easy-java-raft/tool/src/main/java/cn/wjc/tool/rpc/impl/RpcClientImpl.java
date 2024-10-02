package cn.wjc.tool.rpc.impl;

import java.util.concurrent.TimeUnit;

import com.alipay.remoting.exception.RemotingException;

import cn.wjc.tool.entity.RpcRequest;
import cn.wjc.tool.entity.RpcResponse;
import cn.wjc.tool.exception.RaftException;
import cn.wjc.tool.rpc.RpcClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcClientImpl implements RpcClient {
    private final static com.alipay.remoting.rpc.RpcClient CLIENT = new com.alipay.remoting.rpc.RpcClient();

    @Override
    public <R> R send(RpcRequest request) {
        return send(request, (int) TimeUnit.SECONDS.toMillis(5));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R send(RpcRequest request, int timeout) {
        RpcResponse<R> result;
        try {
            result = (RpcResponse<R>) CLIENT.invokeSync(request.getUrl(), request, timeout);
            return result.getResult();
        } catch (RemotingException e) {
            throw new RaftException("rpc RaftRemotingException ", e);
        } catch (InterruptedException e) {
            // ignore
        }
        return null;
    }

    @Override
    public void init() throws Throwable {
        CLIENT.startup();
    }

    @Override
    public void destroy() throws Throwable {
        CLIENT.shutdown();
        log.info("destroy CLIENT success");
    }

}
