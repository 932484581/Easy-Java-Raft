package cn.wjc.tool.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommandParam {
    /** 指令类型 */
    public static final int GETCOMMAND = 0;
    public static final int SETCOMMAND = 1;
    // 请求的服务器地址
    String addr;
    // 请求的类型，有GETCOMMAND和SETCOMMAND
    int type;
    // 请求的参数
    String key;
    String val;

    public CommandParam(String addr, int type, String key, String val) {
        this.addr = addr;
        this.type = type;
        this.key = key;
        this.val = val;
    }

    public CommandParam() {
    }
}