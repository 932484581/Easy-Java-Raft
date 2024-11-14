package cn.wjc.server.model;

import cn.wjc.tool.entity.RvoteParam;
import cn.wjc.tool.entity.RvoteResult;

public interface Vote {
    /**
     * @description: 当前状态为candidate，请求投票
     * @param {RvoteParam} param
     * @return {*}
     * @author: WJC
     */
    void requestVote(RvoteParam param);

    /**
     * @description: 当前状态为candidate，处理投票请求
     * @param {RvoteParam} param
     * @return {*}
     * @author: WJC
     */
    RvoteResult handlerRequestVote(RvoteParam param);
}
