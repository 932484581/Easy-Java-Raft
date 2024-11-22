package cn.wjc.tool.exception;

import lombok.Data;

@Data
public class RaftException extends RuntimeException {

    public RaftException() {
    }

    public RaftException(String message) {
        super(message);
    }

    public RaftException(String message, Throwable cause) {
        super(message, cause);
    }
}