package cn.wjc.server;

import java.util.ArrayList;
import java.util.List;

import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.tool.entity.Command;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.PeerSet;
import cn.wjc.tool.entity.Request;
import cn.wjc.tool.entity.State;

public class EntryTest {
    public static void main(String[] args) throws Throwable {
        System.out.println("测试");

        List<Peer> list = new ArrayList<>();
        // 节点1
        Node node = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl nodeDefaultImpl = new NodeDefaultImpl(node);
        Peer selfpeer = new Peer("127.0.0.1:5000");
        PeerSet peerSet = new PeerSet();
        node.setPeerSet(peerSet);
        peerSet.setSelf(selfpeer);

        // 节点2
        Node node2 = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl nodeDefaultImpl2 = new NodeDefaultImpl(node2);
        Peer selfpeer2 = new Peer("127.0.0.1:5001");
        PeerSet peerSet2 = new PeerSet();
        node2.setPeerSet(peerSet2);
        peerSet2.setSelf(selfpeer2);

        // 节点3
        Node node3 = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl nodeDefaultImpl3 = new NodeDefaultImpl(node3);
        Peer selfpeer3 = new Peer("127.0.0.1:5002");
        PeerSet peerSet3 = new PeerSet();
        node3.setPeerSet(peerSet3);
        peerSet3.setSelf(selfpeer3);

        // 节点4
        Node node4 = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl nodeDefaultImpl4 = new NodeDefaultImpl(node4);
        Peer selfpeer4 = new Peer("127.0.0.1:5003");
        PeerSet peerSet4 = new PeerSet();
        node4.setPeerSet(peerSet4);
        peerSet4.setSelf(selfpeer4);

        list.add(new Peer("127.0.0.1:5003"));
        list.add(new Peer("127.0.0.1:5002"));
        list.add(new Peer("127.0.0.1:5001"));
        list.add(new Peer("127.0.0.1:5000"));

        peerSet.setList(list);
        peerSet2.setList(list);
        peerSet3.setList(list);
        peerSet4.setList(list);

        System.out.println("测试");
        nodeDefaultImpl.setConfig(peerSet);
        nodeDefaultImpl2.setConfig(peerSet2);
        nodeDefaultImpl3.setConfig(peerSet3);
        nodeDefaultImpl4.setConfig(peerSet4);

        Thread.sleep(1000);

        nodeDefaultImpl.setConfig2connectAndStart(peerSet);
        nodeDefaultImpl2.setConfig2connectAndStart(peerSet2);
        nodeDefaultImpl3.setConfig2connectAndStart(peerSet3);
        nodeDefaultImpl4.setConfig2connectAndStart(peerSet4);

        Request cmdrequest = Request.builder()
                .cmd(Request.COMMAND_REQ)
                .addr("127.0.0.1:5002")
                .build();
        cmdrequest.setObj(Command.builder().command("SET test323 323").build());
        // client
        Node[] nodelist = { node, node2, node3, node4 };
        Node clientNode = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl clientDefaultImpl = new NodeDefaultImpl(clientNode);
        Peer clientpeer = new Peer("127.0.0.1:6000");
        PeerSet clientSet = new PeerSet();
        clientNode.setPeerSet(clientSet);
        clientSet.setSelf(clientpeer);
        clientSet.setList(list);
        clientDefaultImpl.setCLientConfig(clientSet);

        // 判断是否选出leader
        boolean flag = true;
        while (flag) {
            for (Node detectnode : nodelist) {
                if (detectnode.getState() == State.LEADER) {
                    flag = false;
                }
            }
        }

        // 发送测试数据
        System.out.println("发送消息");
        clientNode.client.send(cmdrequest);

        try {
            Thread.sleep(30000); // 让主线程休眠3秒，以便观察周期性任务的执行
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}