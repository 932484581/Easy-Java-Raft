import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.PeerSet;
import cn.wjc.tool.entity.Request;
import cn.wjc.tool.entity.State;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class mynettytest {
    @Test
    public void finaltest() throws Throwable {

        List<Peer> list = new ArrayList<>();
        // 节点1
        Node node = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl nodeDefaultImpl = new NodeDefaultImpl(node);
        Peer selfpeer = new Peer("127.0.0.1:5000");
        PeerSet peerSet = new PeerSet();
        peerSet.setSelf(selfpeer);

        // 节点2
        Node node2 = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl nodeDefaultImpl2 = new NodeDefaultImpl(node2);
        Peer selfpeer2 = new Peer("127.0.0.1:5001");
        PeerSet peerSet2 = new PeerSet();
        peerSet2.setSelf(selfpeer2);

        // 节点3
        Node node3 = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl nodeDefaultImpl3 = new NodeDefaultImpl(node3);
        Peer selfpeer3 = new Peer("127.0.0.1:5003");
        PeerSet peerSet3 = new PeerSet();
        peerSet3.setSelf(selfpeer3);

        list.add(selfpeer);
        list.add(selfpeer2);
        list.add(selfpeer3);

        peerSet.setList(list);
        peerSet2.setList(list);
        peerSet3.setList(list);

        nodeDefaultImpl.setConfig(peerSet);
        nodeDefaultImpl2.setConfig(peerSet2);
        nodeDefaultImpl3.setConfig(peerSet3);

        Thread.sleep(1000);

        nodeDefaultImpl.setConfig2connectAndStart(peerSet);
        nodeDefaultImpl2.setConfig2connectAndStart(peerSet2);
        nodeDefaultImpl3.setConfig2connectAndStart(peerSet3);

        node.client.send(Request.builder().cmd(-1).addr(node2.peerSet.getSelf().getAddr()).build());
        Thread.sleep(1000);
        node2.client.send(Request.builder().cmd(-1).addr(node.peerSet.getSelf().getAddr()).build());
        Thread.sleep(1000);
        node3.client.send(Request.builder().cmd(-1).addr(node.peerSet.getSelf().getAddr()).build());
        Thread.sleep(1000);

    }
}
