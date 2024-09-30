package cn.wjc.entity;

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

    public AentryResult(boolean success) {
        this.success = success;
    }

    public AentryResult(long term, boolean success) {
        this.term = term;
        this.success = success;
    }

    public static AentryResult fail() {
        return new AentryResult(false);
    }

    public static AentryResult ok() {
        return new AentryResult(true);
    }
}
