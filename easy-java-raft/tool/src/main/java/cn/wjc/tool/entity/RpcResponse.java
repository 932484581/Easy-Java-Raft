package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcResponse<T> implements Serializable {
    private T result;

    public RpcResponse(T result) {
        this.result = result;
    }
}
