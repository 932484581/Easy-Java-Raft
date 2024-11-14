package cn.wjc.server.model;

import cn.wjc.tool.entity.PeerSet;

public interface NodeDefault {

    /**
     * 设置配置文件.
     *
     * @param config
     * @throws Throwable
     */
    void setConfig(PeerSet peerSet) throws Throwable;

    /**
     * 转发给 leader 节点.
     * 
     * @param request
     * @return
     */
    String redirect();

    /**
     * @description: 转变角色
     * @param {int} FOLLOWER，CANDIDATE，LEADER
     * @return {*}
     * @author: WJC
     */
    void changeState(int state);
}
