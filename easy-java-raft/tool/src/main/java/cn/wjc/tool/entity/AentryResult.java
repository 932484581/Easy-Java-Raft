package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Builder
@Setter
public class AentryResult implements Serializable {

    /** 当前的任期号，用于领导人去更新自己 */
    long term;

    /** 跟随者包含了匹配上 prevLogIndex 和 prevLogTerm 的日志时为真 */
    boolean success;
    // 匹配的日志条目的最大索引
    long MatchIndex;
    // 匹配失败时候对应的日志Index
    long ConflictIndex;

    public AentryResult(boolean success) {
        this.success = success;
    }

    public AentryResult(long term, boolean success) {
        this.term = term;
        this.success = success;
    }

    public AentryResult(long term, boolean success, long matchIndex, long conflictIndex) {
        this.term = term;
        this.success = success;
        MatchIndex = matchIndex;
        ConflictIndex = conflictIndex;
    }

    public static AentryResult fail() {
        return new AentryResult(false);
    }

    public static AentryResult ok() {
        return new AentryResult(true);
    }
}
