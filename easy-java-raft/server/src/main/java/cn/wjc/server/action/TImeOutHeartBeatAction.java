package cn.wjc.server.action;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.tool.entity.AentryParam;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.Request;
import cn.wjc.tool.entity.State;
import cn.wjc.tool.exception.NettyException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 如果是LEADER，则检查是否发送心跳，否则检查阶段是否超时，超时则进入新一轮选举状态
 * @return {*}
 * @author: WJC
 */
@Data
@Slf4j
public class TImeOutHeartBeatAction implements Runnable {
    private Node node;
    private NodeDefaultImpl nodeDefault;

    public TImeOutHeartBeatAction(Node node) {
        this.node = node;
        nodeDefault = new NodeDefaultImpl(node);
    }

    @Override
    public void run() {

        long current = System.currentTimeMillis();

        if (node.getState() == State.LEADER) {
            // 发送心跳
            if (current - node.preHeartBeatTime > node.heartBeatTick) {
                List<Peer> peers = node.peerSet.getPeersWithOutSelf();
                for (Peer peer : peers) {

                    AentryParam param = AentryParam.builder()
                            .entries(null)// 心跳,空日志.
                            .leaderId(node.peerSet.getSelf().getAddr())
                            .serverId(peer.getAddr())
                            .term(node.currentTerm)
                            .leaderCommit(node.commitIndex) // 心跳时与跟随者同步 commit index
                            .build();

                    Request request = Request.builder()
                            .cmd(Request.A_ENTRIES)
                            .obj(param)
                            .addr(peer.getAddr())
                            .reqTerm(node.currentTerm)
                            .build();
                    try {
                        node.client.send(request);
                    } catch (Exception e) {
                        log.error("发送心跳到：" + peer.getAddr() + "失败 ");
                        e.printStackTrace();
                    } catch (NettyException e) {
                        log.error("发送心跳到：" + peer.getAddr() + "失败 ");
                        e.printStackTrace();
                    }
                }
            }
        }

        else {
            // 超时处理
            if (current - node.preElectionTime > node.electionTime) {
                // raft随机超时时间
                node.electionTime = 15000 + ThreadLocalRandom.current().nextInt(10000);
                node.currentTerm = node.currentTerm + 1;
                nodeDefault.changeState(State.CANDIDATE);
                node.preElectionTime = System.currentTimeMillis();
                log.debug("节点(" + node.getPeerSet().getSelf().getAddr() + ")超时，进入新一轮的选举状态，当前Trem:" + node.currentTerm);
            }
        }
    }
}
