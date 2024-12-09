package cn.wjc.server.model.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import cn.wjc.server.action.AppendAentryAction;
import cn.wjc.server.action.TImeOutHeartBeatAction;
import cn.wjc.server.action.VoteAction;
import cn.wjc.server.model.NodeDefault;
import cn.wjc.tool.command.impl.CommandProtocolImpl;
import cn.wjc.tool.entity.AentryParam;
import cn.wjc.tool.entity.LogEntry;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.PeerSet;
import cn.wjc.tool.entity.Request;
import cn.wjc.tool.entity.State;
import cn.wjc.tool.exception.NettyException;
import cn.wjc.tool.netty.client.impl.ClientImpl;
import cn.wjc.tool.netty.server.impl.ServerImpl;
import cn.wjc.tool.storage.impl.KVStorageImpl;
import cn.wjc.tool.storage.impl.LogStorageImpl;
import cn.wjc.tool.util.pool.RaftThreadPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class NodeDefaultImpl implements NodeDefault {
    private Node node;
    private LinkedList<ScheduledFuture<?>> futureList = new LinkedList<>();
    // private ScheduledFuture<?> getVotefuture;
    // private ScheduledFuture<?> appendAentryfuture;

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

        ServerImpl server = new ServerImpl(node);
        server.init();

        LogStorageImpl logStorage = new LogStorageImpl("log" + String.valueOf(port % 10));
        logStorage.init();
        node.logStorage = logStorage;
        node.setCommandProtocolImpl(new CommandProtocolImpl("kv_store" + String.valueOf(port % 10), node));
        node.setKvStorageImpl(new KVStorageImpl("kv_store" + String.valueOf(port % 10)));
        // 初始化commitIndex
        String initcommitIndex = node.getKvStorageImpl().getString("commitIndex");
        if (initcommitIndex == null) {
            node.getKvStorageImpl().setString("commitIndex", "0");
            node.setCommitIndex(0);
        } else {
            node.setCommitIndex(Long.valueOf(initcommitIndex));
        }
        // 初始化currentTerm
        String initCurrentTerm = node.getKvStorageImpl().getString("currentTerm");
        if (initCurrentTerm == null) {
            node.getKvStorageImpl().setString("currentTerm", "0");
            node.setCurrentTerm(0L);
        } else {
            node.currentTerm = Long.valueOf(initCurrentTerm);
        }
    }

    @Override
    public void setConfig2connectAndStart(PeerSet peerSet) throws Throwable {
        ClientImpl client = new ClientImpl(node);
        for (Peer peer : node.peerSet.getPeersWithOutSelf()) {
            client.connectToServer(peer.getAddr());
        }
        node.client = client;
        // 启动超时检测线程
        node.preElectionTime = System.currentTimeMillis();
        // testsend();

        RaftThreadPool.scheduleAtFixedRate(new TImeOutHeartBeatAction(node), 10,
                300);
    }

    public void testsend() {
        List<Peer> peers = node.peerSet.getPeersWithOutSelf();
        for (Peer peer : peers) {
            // 向还没有投票的节点发送投票请求
            if (!node.getResultMap.containsKey(peer.getAddr())) {
                Request request = Request.builder()
                        .cmd(-1)
                        .obj("这是一次通信测试")
                        .addr(peer.getAddr())
                        .reqTerm(node.currentTerm)
                        .build();
                try {
                    node.client.send(request);
                } catch (Exception e) {
                    log.error("发送投票到：" + peer.getAddr() + "失败 ");
                    e.printStackTrace();
                } catch (NettyException e) {
                    log.error("发送投票到：" + peer.getAddr() + "失败 " + e);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void changeState(int state) {
        node.getResultMap.clear();
        // 取消所有ScheduledFuture任务
        for (ScheduledFuture<?> future : futureList) {
            future.cancel(true);
        }

        if (state == State.FOLLOWER) {
            log.info(node.getPeerSet().getSelf().getAddr() + ": 改变角色为FOLLOWER");
            node.setState(State.FOLLOWER);
            node.votedFor = null;
            log.debug("节点" + node.peerSet.getSelf() + "转变为了FOLLOWER");
        } else if (state == State.CANDIDATE) {
            log.info(node.getPeerSet().getSelf().getAddr() + ": 改变角色为CANDIDATE");
            node.setState(State.CANDIDATE);
            // 给自己投票
            node.getResultMap.put(node.getPeerSet().getSelf().getAddr(), 1L);
            node.votedFor = node.getPeerSet().getSelf().getAddr();
            futureList.add(RaftThreadPool.scheduleAtFixedRate(new VoteAction(node), 0, 5000));
            log.debug("节点" + node.peerSet.getSelf() + "转变为了CANDIDATE");
        } else if (state == State.LEADER) {
            log.info(node.getPeerSet().getSelf().getAddr() + ": 改变角色为LEADER");
            List<Peer> peers = node.peerSet.getPeersWithOutSelf();
            for (Peer peer : peers) {
                node.getResultMap.put(peer.getAddr(), 0L);
            }
            node.setState(State.LEADER);
            futureList.add(RaftThreadPool.scheduleAtFixedRate(new AppendAentryAction(node), 0, 5000));
            log.debug("节点" + node.peerSet.getSelf() + "转变为了LEADER");
        }

    }

    @Override
    public void setCLientConfig(PeerSet peerSet) throws Throwable {
        node.peerSet = peerSet;
        // 初始化netty的server和client
        String[] parts = node.peerSet.getSelf().getAddr().split(":", 2);
        int port = Integer.parseInt(parts[1]);
        ServerImpl server = new ServerImpl(node);
        server.init();
        ClientImpl client = new ClientImpl(node);
        for (Peer peer : node.peerSet.getList()) {
            client.connectToServer(peer.getAddr());
        }
        node.client = client;
    }

    @Override
    public void sendUpdateLog(String addr) {
        log.info("节点" + node.getPeerSet().getSelf().getAddr() + ",!!接收到同步日志指令,当前节点commitindex："
                + String.valueOf(node.getCommitIndex()));
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
                .leaderCommit(node.commitIndex)
                .build();
        Request request2 = Request.builder()
                .cmd(Request.A_ENTRIES)
                .obj(aentryParam)
                .reqTerm(node.currentTerm)
                .addr(addr)
                .build();
        try {
            node.getClient().send(request2);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (NettyException e) {
            e.printStackTrace();
        }
    }

}
