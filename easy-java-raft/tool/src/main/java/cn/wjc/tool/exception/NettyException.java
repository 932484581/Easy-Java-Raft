package cn.wjc.tool.exception;

import lombok.Data;

@Data
public class NettyException extends Throwable {

    private String errMsg;

    public NettyException() {
    }

    public NettyException(String message) {
        super(message);
    }

}
