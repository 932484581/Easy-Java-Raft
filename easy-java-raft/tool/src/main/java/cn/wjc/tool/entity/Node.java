package cn.wjc.tool.entity;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

import cn.wjc.tool.command.impl.CommandProtocolImpl;
import cn.wjc.tool.netty.client.impl.ClientImpl;
import cn.wjc.tool.storage.LogStorage;
import cn.wjc.tool.storage.impl.KVStorageImpl;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Node {
    /* ============节点当前的状态============ */
    // 角色
    private volatile int state;
    // 存储的服务器和自身的节点信息
    public PeerSet peerSet;
    // 选举时间间隔基数
    @Builder.Default
    public volatile long electionTime = 15000 + ThreadLocalRandom.current().nextInt(10000);
    // 上一次选举时间
    @Builder.Default
    public volatile long preElectionTime = 0;
    // 上次一心跳时间戳
    @Builder.Default
    public volatile long preHeartBeatTime = 0;
    // 心跳间隔基数
    public final long heartBeatTick = 4 * 1000;

    /* ============服务器============ */
    // 服务器的任期号
    @Builder.Default
    public volatile long currentTerm = 0;
    // 在当前投出给选票的候选人的 Id
    public volatile String votedFor;
    // 投票相关，接收到的投票
    @Builder.Default
    public ConcurrentMap<String, Long> getResultMap = new ConcurrentHashMap<>();

    public ClientImpl client;

    /* ============日志============ */
    // 日志条目集
    public LogStorage logStorage;
    // 已知的最大的已经被提交的日志条目的索引值
    public volatile long commitIndex;
    // 最后被应用到状态机的日志条目索引值
    @Builder.Default
    public volatile long lastApplied = 0;

    /* ============指令============ */
    CommandProtocolImpl commandProtocolImpl;

    /* ============KV============ */
    KVStorageImpl kvStorageImpl;

    public void setCurrentTerm(long currentTerm) {
        this.currentTerm = currentTerm;
        kvStorageImpl.setString("currentTerm", String.valueOf(currentTerm));
    }

    public void setCommitIndex(long commitIndex) {
        this.commitIndex = commitIndex;
        kvStorageImpl.setString("commitIndex", String.valueOf(commitIndex));
    }
}
