package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AentryParam implements Serializable {

    /** 候选人的任期号 */
    private long term;

    /** 被请求者 ID(ip:selfPort) */
    private String serverId;

    /** 领导人的 Id，以便于跟随者重定向请求 */
    private String leaderId;

    /** 前一个日志的日志号 */
    private long preLogIndex;

    /** 前一个日志的任期 */
    private long preLogTerm;

    /** 准备存储的日志条目（表示心跳时为空；一次性发送多个是为了提高效率） */
    private LogEntry[] entries;

    /** 领导人已经提交的日志号 */
    private long leaderCommit;

    public AentryParam(long term, String serverId, String leaderId, long preLogIndex, long preLogTerm,
            LogEntry[] entries, long leaderCommit) {
        this.term = term;
        this.serverId = serverId;
        this.leaderId = leaderId;
        this.preLogIndex = preLogIndex;
        this.preLogTerm = preLogTerm;
        this.entries = entries;
        this.leaderCommit = leaderCommit;
    }

}
