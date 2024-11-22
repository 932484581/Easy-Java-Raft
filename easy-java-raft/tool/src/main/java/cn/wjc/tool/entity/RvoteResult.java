package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class RvoteResult implements Serializable {
    /** 候选人赢得了此张选票时为真 */
    boolean voteGranted;

    public RvoteResult(boolean voteGranted) {
        this.voteGranted = voteGranted;
    }
}
