package cn.wjc.tool.exception;

import lombok.Data;

@Data
public class LogStorageException extends Throwable {

    private String errMsg;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}