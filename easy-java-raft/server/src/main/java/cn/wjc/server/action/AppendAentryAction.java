package cn.wjc.server.action;

import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.State;
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
            log.info(node.getPeerSet().getSelf().getAddr() + ":进行一次同步检查" + String.valueOf(node.getPeerSet()) + ","
                    + node.getCOldNew());
            NodeDefaultImpl nodeDefaultImpl = new NodeDefaultImpl(node);
            for (Peer peers : node.getPeerSet().getPeersWithOutSelf()) {
                nodeDefaultImpl.sendUpdateLog(peers.getAddr());
            }
        }
    }
}
