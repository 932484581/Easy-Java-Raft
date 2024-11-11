package cn.wjc.tool.netty.client;

import cn.wjc.tool.LifeCycle;
import cn.wjc.tool.entity.Request;

public interface Client extends LifeCycle {
    void send(String serverId, Request request) throws Exception;

    void send(String serverId, Request request, int timeout) throws Exception;
}
