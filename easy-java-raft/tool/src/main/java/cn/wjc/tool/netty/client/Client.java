package cn.wjc.tool.netty.client;

import cn.wjc.tool.LifeCycle;
import cn.wjc.tool.entity.Request;
import cn.wjc.tool.exception.NettyException;

public interface Client extends LifeCycle {
    void send(Request request) throws Exception, NettyException;

    void connectToServer(String addr) throws Throwable;

    void disconnectAddr(String addr) throws InterruptedException;
}
