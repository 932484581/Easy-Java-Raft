package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RvoteResult implements Serializable {
    /** 当前任期号，以便于候选人去更新自己的任期 */
    long term;

    /** 接收到的候选人的任期号 */
    long receiveTerm;

    /** 候选人赢得了此张选票时为真 */
    boolean voteGranted;
}
