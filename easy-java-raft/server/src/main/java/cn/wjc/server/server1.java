package cn.wjc.server;

import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.PeerSet;
import cn.wjc.tool.entity.State;

public class server1 {
    public static void main(String[] args) throws Throwable {
        System.out.println("测试");
        Node node = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl nodeDefaultImpl = new NodeDefaultImpl(node);
        Peer selfpeer = new Peer("127.0.0.1:8000");
        PeerSet peerSet = new PeerSet();
        peerSet.setSelf(selfpeer);
        nodeDefaultImpl.setConfig(peerSet);

        System.out.println("测试");
        try {
            Thread.sleep(15000); // 让主线程休眠15秒，以便观察周期性任务的执行
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
