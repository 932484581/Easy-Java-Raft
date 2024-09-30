package cn.wjc.tool.entity;

import cn.wjc.tool.storage.LogStorage;

public class Node {
    /* ============节点当前的状态============ */
    // 角色
    public State state;
    // 存储的服务器和自身的节点信息
    public PeerSet peerSet;
    // 选举时间间隔基数
    public volatile long electionTime = 15 * 1000;
    // 上一次选举时间
    public volatile long preElectionTime = 0;
    // 上次一心跳时间戳
    public volatile long preHeartBeatTime = 0;
    // 心跳间隔基数
    public final long heartBeatTick = 5 * 100;

    /* ============服务器============ */
    // 服务器的任期号
    volatile long currentTerm = 0;
    // 在当前获得选票的候选人的 Id
    volatile String votedFor;

    /* ============日志============ */
    // 日志条目集
    LogStorage logStorage;
    // 已知的最大的已经被提交的日志条目的索引值
    volatile long commitIndex;
    // 最后被应用到状态机的日志条目索引值
    volatile long lastApplied = 0;

    /* ============传输相关============ */
    // public RpcService rpcServer;

}
