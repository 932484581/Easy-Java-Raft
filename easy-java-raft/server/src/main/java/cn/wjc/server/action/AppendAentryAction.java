package cn.wjc.server.action;

import cn.wjc.tool.entity.AentryParam;
import cn.wjc.tool.entity.LogEntry;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.Request;
import cn.wjc.tool.entity.State;
import cn.wjc.tool.exception.NettyException;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 定期向未同步到最新的日志发出同步请求。
 * @return {*}
 * @author: WJC
 */
@Slf4j
public class AppendAentryAction implements Runnable {
    private Node node;

    public AppendAentryAction(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        if (node.getState() == State.LEADER) {
            log.info("进行一次同步检查");
            Long lastLogIndex = node.getLogStorage().getLastLogIndex();
            LogEntry lastLogEntry = node.getLogStorage().getEntry(lastLogIndex);
            LogEntry preLogEntry = node.getLogStorage().getEntry(lastLogIndex - 1);
            LogEntry[] wait2SentEntries = { lastLogEntry };

            AentryParam aentryParam = AentryParam.builder()
                    .term(node.currentTerm)
                    .preLogIndex(preLogEntry.getIndex())
                    .preLogTerm(preLogEntry.getTerm())
                    .leaderId(node.getPeerSet().getSelf().getAddr())
                    .entries(wait2SentEntries)
                    .build();
            Request request = Request.builder()
                    .cmd(Request.A_ENTRIES)
                    .obj(aentryParam)
                    .reqTerm(node.currentTerm)
                    .build();
            for (Peer peers : node.getPeerSet().getPeersWithOutSelf()) {
                if (node.getResultMap.get(peers.getAddr()) < lastLogIndex) {
                    aentryParam.setServerId(peers.getAddr());
                    request.setAddr(peers.getAddr());
                    try {
                        node.getClient().send(request);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (NettyException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
