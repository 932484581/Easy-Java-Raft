package cn.wjc.tool.exception;

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