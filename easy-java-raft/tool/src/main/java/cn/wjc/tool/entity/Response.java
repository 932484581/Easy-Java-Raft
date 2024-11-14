package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response<T> implements Serializable {
    // 响应的类型
    public int type;
    // 响应者的地址
    public String addr;
    private T result;
}
