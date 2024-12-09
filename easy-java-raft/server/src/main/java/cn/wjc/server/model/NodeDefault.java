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
     * 
     *
     * @param config
     * @throws Throwable
     */
    void setConfig2connectAndStart(PeerSet peerSet) throws Throwable;

    /**
     * @description: 配置客户端节点
     * @param {PeerSet} peerSet
     * @return {*}
     * @author: WJC
     */
    void setCLientConfig(PeerSet peerSet) throws Throwable;

    /**
     * @description: 转变角色
     * @param {int} FOLLOWER，CANDIDATE，LEADER
     * @return {*}
     * @author: WJC
     */
    void changeState(int state);

    void sendUpdateLog(String addr);
}
