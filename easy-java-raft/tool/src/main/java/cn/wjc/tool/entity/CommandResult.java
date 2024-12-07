package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommandResult implements Serializable {
    // 请求的结果
    private boolean result;
    // 如果请求的不是LEADER,返回LEADER的地址
    private String addr;
    // 请求的指令
    private Command command;
}
