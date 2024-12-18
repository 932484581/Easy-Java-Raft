import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.tool.command.impl.CommandProtocolImpl;
import cn.wjc.tool.entity.CommandParam;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.PeerSet;
import cn.wjc.tool.entity.State;

public class commandTest {
    @Test
    public void testcommand() throws Throwable {
        Node node = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl nodeDefaultImpl = new NodeDefaultImpl(node);
        Peer selfpeer = new Peer("127.0.0.1:5000");
        PeerSet peerSet = new PeerSet();
        node.setPeerSet(peerSet);
        peerSet.setSelf(selfpeer);

        List<Peer> list = new ArrayList<>();
        list.add(new Peer("127.0.0.1:5003"));
        list.add(new Peer("127.0.0.1:5002"));
        list.add(new Peer("127.0.0.1:5001"));
        list.add(new Peer("127.0.0.1:5000"));

        peerSet.setList(list);

        String command = "CHANGE REMOVE test3 127.0.0.1:5001";
        CommandProtocolImpl commandProtocolImpl = new CommandProtocolImpl("kv_store", node);
        CommandParam commandParam = commandProtocolImpl.analysis(command);
        System.out.println(commandParam);
        commandProtocolImpl.commitCommand(commandParam);
        System.out.println(node.getPeerSet());
    }
}
