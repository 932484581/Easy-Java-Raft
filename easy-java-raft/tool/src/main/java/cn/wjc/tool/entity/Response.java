package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class Response<T> implements Serializable {
    // 响应的类型
    public int type;
    // 响应者的地址
    public String addr;
    // 请求者的任期
    public long reqTerm;
    // 响应者的任期
    public long resTerm;

    private T result;

    public Response() {

    }

    public Response(int type, String addr, long reqTerm, long resTerm) {
        this.type = type;
        this.addr = addr;
        this.reqTerm = reqTerm;
        this.resTerm = resTerm;
    }
}
