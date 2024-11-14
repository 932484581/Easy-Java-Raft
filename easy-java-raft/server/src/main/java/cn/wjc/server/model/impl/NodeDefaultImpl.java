package cn.wjc.server.model.impl;

import cn.wjc.server.action.TimeOutAction;
import cn.wjc.server.model.NodeDefault;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.PeerSet;
import cn.wjc.tool.entity.State;
import cn.wjc.tool.netty.client.impl.ClientImpl;
import cn.wjc.tool.netty.server.impl.ServerImpl;
import cn.wjc.tool.util.pool.RaftThreadPool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NodeDefaultImpl implements NodeDefault {
    private Node node;

    public NodeDefaultImpl(Node node) {
        this.node = node;
    }

    @Override
    public void setConfig(PeerSet peerSet) throws Throwable {
        node.peerSet = peerSet;
        log.info("开始初始化" + node.peerSet.getSelf().getAddr());
        // 初始化netty的server和client
        String[] parts = node.peerSet.getSelf().getAddr().split(":", 2);
        int port = Integer.parseInt(parts[1]);

        ServerImpl server = ServerImpl.builder().port(port).build();
        server.init();

        ClientImpl client = new ClientImpl();
        for (Peer peer : node.peerSet.getPeersWithOutSelf()) {
            client.connectToServer(peer.getAddr());
        }
        // 启动超时检测
        node.preElectionTime = System.currentTimeMillis();
        RaftThreadPool.scheduleAtFixedRate(new TimeOutAction(node), 300, 0);

    }

    @Override
    public String redirect() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'redirect'");
    }

    @Override
    public void changeState(int state) {
        if (state == State.FOLLOWER) {
            node.setState(State.FOLLOWER);
            log.debug("节点" + node.peerSet.getSelf() + "转变为了FOLLOWER");
        } else if (state == State.CANDIDATE) {
            node.setState(State.CANDIDATE);
            log.debug("节点" + node.peerSet.getSelf() + "转变为了CANDIDATE");
        } else if (state == State.LEADER) {
            node.setState(State.LEADER);
            log.debug("节点" + node.peerSet.getSelf() + "转变为了LEADER");
        }

    }

}
