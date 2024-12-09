package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommandParam implements Serializable {
    /** 指令类型 */
    public static final int SETCOMMAND = 0;
    public static final int GETCOMMAND = 1;
    public static final int CHANGE_ADD_COMMAND = 2;
    public static final int CHANGE_REMOVE_COMMAND = 3;
    public static final int CNEW = 4;
    // 请求更新日志
    public static final int UPDATE = 5;
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
