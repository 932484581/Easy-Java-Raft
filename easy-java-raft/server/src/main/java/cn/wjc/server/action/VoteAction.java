package cn.wjc.server.action;

import java.util.List;

import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.tool.entity.LogEntry;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.Request;
import cn.wjc.tool.entity.RvoteParam;
import cn.wjc.tool.entity.State;
import cn.wjc.tool.exception.NettyException;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 检查自身选票是否足够，如果足够转变为LEADER，不够的话继续请求选票
 * @return {*}
 * @author: WJC
 */
@Slf4j
public class VoteAction implements Runnable {
    private Node node;
    private NodeDefaultImpl nodeDefault;

    public VoteAction(Node node) {
        this.node = node;
        this.nodeDefault = new NodeDefaultImpl(node);
    }

    @Override
    public void run() {
        log.info(node.peerSet.getSelf().getAddr() + "开始发起投票请求");
        if (node.getState() == State.CANDIDATE) {
            // 请求投票
            List<Peer> peers = node.peerSet.getPeersWithOutSelf();

            for (Peer peer : peers) {
                // 向还没有投票的节点发送投票请求
                if (!node.voteGetMap.containsKey(peer.getAddr())) {
                    LogEntry last = node.logStorage.getEntry(node.logStorage.getLastLogIndex());
                    long lastTerm = 0L;
                    if (last != null) {
                        lastTerm = last.getTerm();
                    }

                    RvoteParam param = RvoteParam.builder()
                            .term(node.currentTerm)
                            .candidateId(node.peerSet.getSelf().getAddr())
                            .lastLogIndex(node.logStorage.getLastLogIndex())
                            .lastLogTerm(lastTerm).build();

                    Request request = Request.builder()
                            .cmd(Request.R_VOTE)
                            .obj(param)
                            .addr(peer.getAddr())
                            .reqTerm(node.currentTerm)
                            .build();
                    try {
                        node.client.send(request);
                    } catch (Exception e) {
                        log.error("发送投票到：" + peer.getAddr() + "失败 ");
                        e.printStackTrace();
                    } catch (NettyException e) {
                        log.error("发送投票到：" + peer.getAddr() + "失败 " + e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
