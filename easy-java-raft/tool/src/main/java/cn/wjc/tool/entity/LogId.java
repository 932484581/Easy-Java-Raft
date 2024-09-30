package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogId implements Serializable {
    // 索引
    private long index;
    // 任期号
    private long term;
}
