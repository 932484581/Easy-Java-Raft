package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfigChangeResult implements Serializable {
    // 请求的结果
    private boolean result;
}
