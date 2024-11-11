package cn.wjc.server.rpc;

import cn.wjc.tool.entity.Request;
import cn.wjc.tool.entity.Response;
import cn.wjc.tool.rpc.LifeCycle;

public interface RpcService extends LifeCycle {
    /**
     * @description: 处理接收到的请求
     * @return 根据接收类型返回对应的respone
     * @author: WJC
     */
    Response<?> handlerRequest(Request request);
}
