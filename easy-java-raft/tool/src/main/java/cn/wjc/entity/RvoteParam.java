package cn.wjc.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * @description: 投票请求
 * @return {*}
 * @author: WJC
 */
@Data
@Builder
public class RvoteParam implements Serializable {
    // 候选人任期号
    private long term;

    // 被请求者 ID
    private String serverId;

    // 请求选票的候选人的 ID
    private String candidateId;

    // 候选人的最后日志条目的索引值
    private long lastLogIndex;

    // 候选人最后日志条目的任期号
    private long lastLogTerm;

    public RvoteParam(long term, String serverId, String candidateId, long lastLogIndex, long lastLogTerm) {
        this.candidateId = candidateId;
        this.lastLogIndex = lastLogIndex;
        this.lastLogTerm = lastLogTerm;
        this.serverId = serverId;
        this.term = term;
    }
}
