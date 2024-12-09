package cn.wjc.tool.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfigChangeParam implements Serializable {
    // 更改的节点
    private List<String> addrs = new ArrayList<>();
    // 请求的类型 CHANGE_ADD_COMMAND、CHANGE_REMOVE_COMMAND、CNEW
    private int type;
    // 日志信息
    private AentryParam aentryParam;
}
