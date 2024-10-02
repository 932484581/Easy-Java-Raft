package cn.wjc.tool.exception;

public class LogStorageException extends Throwable {

    private String errMsg;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}