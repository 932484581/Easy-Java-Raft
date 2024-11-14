package cn.wjc.tool.netty.client;

import cn.wjc.tool.LifeCycle;
import cn.wjc.tool.entity.Request;

public interface Client extends LifeCycle {
    void send(Request request) throws Exception;
}
