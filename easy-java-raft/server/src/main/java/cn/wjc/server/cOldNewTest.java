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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class cOldNewTest {
    public static void main(String[] args) throws Throwable {

        System.out.println("测试");
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

        node.setCanJoin(true);
        node2.setCanJoin(true);
        node3.setCanJoin(true);
        node4.setCanJoin(true);

        List<Peer> list = new ArrayList<>();
        list.add(new Peer("127.0.0.1:5003"));
        list.add(new Peer("127.0.0.1:5002"));
        list.add(new Peer("127.0.0.1:5001"));
        list.add(new Peer("127.0.0.1:5000"));

        List<Peer> list2 = new ArrayList<>();
        list2.add(new Peer("127.0.0.1:5003"));
        list2.add(new Peer("127.0.0.1:5002"));
        list2.add(new Peer("127.0.0.1:5001"));
        list2.add(new Peer("127.0.0.1:5000"));

        List<Peer> list3 = new ArrayList<>();
        list3.add(new Peer("127.0.0.1:5003"));
        list3.add(new Peer("127.0.0.1:5002"));
        list3.add(new Peer("127.0.0.1:5001"));
        list3.add(new Peer("127.0.0.1:5000"));

        List<Peer> list4 = new ArrayList<>();
        list4.add(new Peer("127.0.0.1:5003"));
        list4.add(new Peer("127.0.0.1:5002"));
        list4.add(new Peer("127.0.0.1:5001"));
        list4.add(new Peer("127.0.0.1:5000"));

        peerSet.setList(list);
        peerSet2.setList(list2);
        peerSet3.setList(list3);
        peerSet4.setList(list4);

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

        // client
        Node[] nodelist = { node, node2, node3, node4 };

        // 判断是否选出leader
        boolean flag = true;
        while (flag) {
            for (Node detectnode : nodelist) {
                if (detectnode.getState() == State.LEADER) {
                    flag = false;
                }
            }
        }

        List<Peer> list5 = new ArrayList<>();
        list5.add(new Peer("127.0.0.1:5003"));
        list5.add(new Peer("127.0.0.1:5002"));
        list5.add(new Peer("127.0.0.1:5001"));
        list5.add(new Peer("127.0.0.1:5000"));

        // 节点5
        Node node5 = Node.builder().state(State.FOLLOWER).build();
        NodeDefaultImpl nodeDefaultImpl5 = new NodeDefaultImpl(node5);
        Peer selfpeer5 = new Peer("127.0.0.1:5004");
        PeerSet peerSet5 = new PeerSet();
        node5.setPeerSet(peerSet5);
        peerSet5.setSelf(selfpeer5);

        node5.setCanJoin(false);

        peerSet5.setList(list5);

        nodeDefaultImpl5.setConfig(peerSet5);

        nodeDefaultImpl5.setConfig2connectAndStart(peerSet5);

        Request cmdrequest = Request.builder()
                .cmd(Request.COMMAND_REQ)
                .addr("127.0.0.1:5002")
                .build();

        cmdrequest.setObj(Command.builder().command("UPDATE 127.0.0.1:5004").build());
        node5.client.send(cmdrequest);
        // 判断是否同步到了最新的日志
        while (true) {
            if (node5.getCanJoin()) {
                break;
            }
        }
        cmdrequest.setObj(Command.builder().command("CHANGE ADD 127.0.0.1:5004").build());
        node5.client.send(cmdrequest);
        while (true) {
            System.out.println(node2.getPeerSet());
            log.warn("观察！！！！！！！！" + String.valueOf(node2.getPeerSet()));
            try {
                Thread.sleep(1000); // 让主线程休眠3秒，以便观察周期性任务的执行
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}