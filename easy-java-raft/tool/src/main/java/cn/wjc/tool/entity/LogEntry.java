package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 日志信息
 * @return {*}
 * @author: WJC
 */
@SuppressWarnings("rawtypes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry implements Serializable, Comparable {
    // 日志索引
    private Long index;
    // 日志任期
    private long term;

    private Command command;

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return -1;
        }
        if (this.getIndex() > ((LogEntry) o).getIndex()) {
            return 1;
        }
        return -1;
    }

}
