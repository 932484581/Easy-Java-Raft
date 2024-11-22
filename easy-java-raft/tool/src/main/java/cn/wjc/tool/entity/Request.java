package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Request implements Serializable {
    /** 请求投票 */
    public static final int R_VOTE = 0;
    /** 附加日志 */
    public static final int A_ENTRIES = 1;
    /** 客户端 */
    public static final int CLIENT_REQ = 2;
    /** 配置变更. add */
    public static final int CHANGE_CONFIG_ADD = 3;
    /** 配置变更. remove */
    public static final int CHANGE_CONFIG_REMOVE = 4;

    // 请求类型
    private int cmd = -1;
    // 请求体
    private Object obj;
    // 请求的地址
    private String addr;
    // 请求者的任期
    public long reqTerm;

    public Request(int cmd, Object obj, String addr, long reqTerm) {
        this.cmd = cmd;
        this.obj = obj;
        this.addr = addr;
        this.reqTerm = reqTerm;
    }
}
