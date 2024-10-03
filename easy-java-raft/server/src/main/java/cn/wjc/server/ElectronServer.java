package cn.wjc.server;

import cn.wjc.tool.entity.RpcRequest;

public interface ElectronServer {
    public void startPrevote();

    public void election();

    public void handlePrevoteResponse(RpcRequest requestPreVoteResponse);

    public void handleElectionResponse(RpcRequest requestVoteResponse);
}
