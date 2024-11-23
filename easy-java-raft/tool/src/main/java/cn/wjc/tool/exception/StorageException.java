package cn.wjc.tool.exception;

import lombok.Data;

@Data
public class StorageException extends Throwable {

    public StorageException() {
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}