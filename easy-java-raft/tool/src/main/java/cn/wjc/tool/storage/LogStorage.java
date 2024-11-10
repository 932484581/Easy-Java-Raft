package cn.wjc.tool.storage;

import java.util.List;

import cn.wjc.tool.LifeCycle;
import cn.wjc.tool.entity.LogEntry;

public interface LogStorage extends LifeCycle {
    /**
     * 获取第一个日志
     */
    long getFirstLogIndex();

    /**
     * 获取最新的日志
     */
    long getLastLogIndex();

    /**
     * 根据index获取日志
     */
    LogEntry getEntry(final long index);

    /**
     * 使用前请先调用getEntry
     * 
     * @deprecated
     */
    @Deprecated
    long getTerm(final long index);

    /**
     * 追加日志
     */
    boolean appendEntry(final LogEntry entry);

    /**
     * @description: 追加多个日志
     * @param {finalList<LogEntry>} entries 日志列表
     * @return 写入成功的数量
     * @author: WJC
     */
    int appendEntries(final List<LogEntry> entries);

    /**
     * @description: 日志将会删除日志(1~firstIndexKept)
     * @param {finallong} firstIndexKept
     * @return {*}
     * @author: WJC
     */
    boolean truncatePrefix(final long firstIndexKept);

    /**
     * @description: 删除lastIndexKept以及之后的日志
     * @param {finallong} lastIndexKept
     * @return {*}
     * @author: WJC
     */
    boolean truncateSuffix(final long lastIndexKept);

}
