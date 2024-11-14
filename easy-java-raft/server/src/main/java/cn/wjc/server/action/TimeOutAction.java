package cn.wjc.server.action;

import java.util.concurrent.ThreadLocalRandom;

import cn.wjc.server.model.NodeDefault;
import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.State;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 检查阶段是否超时，超时则进入新一轮选举状态
 * @return {*}
 * @author: WJC
 */
@Data
@Slf4j
public class TimeOutAction implements Runnable {
    private Node node;
    private NodeDefault nodeDefault = new NodeDefaultImpl(node);

    public TimeOutAction(Node node) {
        this.node = node;
    }

    @Override
    public void run() {

        if (node.getState() == State.LEADER) {
            return;
        }

        long current = System.currentTimeMillis();
        // 如果超时了
        if (current - node.preElectionTime < node.electionTime) {
            // raft随机超时时间
            node.electionTime = node.electionTime + ThreadLocalRandom.current().nextInt(3000);

            nodeDefault.changeState(State.CANDIDATE);
            node.currentTerm = node.currentTerm + 1;
            node.preElectionTime = System.currentTimeMillis();
            log.debug("定时器" + node.peerSet.getSelf().getAddr() + "超时，进入新一轮的选举状态，当前Trem:" + node.currentTerm);

            // // 开始投票
            // node.votedFor = peerSet.getSelf().getAddr();
            // List<Peer> peers = peerSet.getPeersWithOutSelf();
            // for (Peer peer : peers) {
            // LogEntry last = node.logStorage.getEntry(node.logStorage.getLastLogIndex());
            // long lastTerm = 0L;
            // if (last != null) {
            // lastTerm = last.getTerm();
            // }

            // RvoteParam param = RvoteParam.builder()
            // .term(node.currentTerm)
            // .candidateId(peerSet.getSelf().getAddr())
            // .lastLogIndex(node.logStorage.getLastLogIndex())
            // .lastLogTerm(lastTerm).build();

            // Request request = Request.builder()
            // .cmd(Request.R_VOTE)
            // .obj(param)
            // .addr(peer.getAddr())
            // .build();

            // try {
            // client.send(request);
            // } catch (Exception e) {
            // log.error("发送日志到：" + peer.getAddr() + "失败 ");
            // e.printStackTrace();
            // }
            // }
        } else {
            log.info("server " + node.peerSet.getSelf().getAddr() + "还未超时");
        }
    }

}
