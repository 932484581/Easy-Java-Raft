import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.PeerSet;
import cn.wjc.tool.entity.State;

public class myTest {
    @Test
    public void mytest2() {
        Node node = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl nodeDefaultImpl = new NodeDefaultImpl(node);
        Peer selfpeer = new Peer("127.0.0.1:5000");
        PeerSet peerSet = new PeerSet();
        node.setPeerSet(peerSet);
        peerSet.setSelf(selfpeer);
        List<Peer> list = new ArrayList<>();
        List<Peer> peerlist = new ArrayList<>();

        peerSet.setList(list);
        peerSet.setCOldNewList(peerlist);

        peerSet.setList(list);

        list.add(new Peer("127.0.0.1:5003"));
        list.add(new Peer("127.0.0.1:5002"));
        list.add(new Peer("127.0.0.1:5001"));
        list.add(new Peer("127.0.0.1:5000"));

        for (Peer peer : node.getPeerSet().getList()) {
            peerlist.add(peer);
            System.out.println(peer);
        }
        peerlist.add(new Peer("127.0.0.1:5004"));

        System.out.println(peerSet);
        node.peerSet.getList().clear();
        for (Peer peer : node.peerSet.getCOldNewList()) {
            node.peerSet.getList().add(peer);
        }
        node.peerSet.getCOldNewList().clear();
        System.out.println(peerSet);

    }
}
