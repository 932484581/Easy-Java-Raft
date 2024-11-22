package cn.wjc.tool.exception;

import lombok.Data;

/**
 * @description: 选举异常
 * @return {*}
 * @author: WJC
 */
@Data
public class ElectionException extends Throwable {

    private String errMsg;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
