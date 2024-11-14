package cn.wjc.server;

import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.PeerSet;
import cn.wjc.tool.entity.State;

public class server1 {
    public static void main(String[] args) throws Throwable {
        Node node = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl nodeDefaultImpl = new NodeDefaultImpl(node);
        Peer selfpeer = new Peer("127.0.0.1:8000");
        PeerSet peerSet = new PeerSet();
        peerSet.setSelf(selfpeer);
        nodeDefaultImpl.setConfig(peerSet);
        Thread.sleep(150000);
    }
}
