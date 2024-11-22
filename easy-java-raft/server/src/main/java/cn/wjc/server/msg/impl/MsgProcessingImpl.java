package cn.wjc.server.msg.impl;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.server.msg.MsgProcessing;
import cn.wjc.tool.entity.AentryResult;
import cn.wjc.tool.entity.LogEntry;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.Request;
import cn.wjc.tool.entity.Response;
import cn.wjc.tool.entity.RvoteParam;
import cn.wjc.tool.entity.RvoteResult;
import cn.wjc.tool.entity.State;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MsgProcessingImpl implements MsgProcessing {
    public final ReentrantLock voteLock = new ReentrantLock();

    @Override
    public void recRes(Response response, Node node) {
        node.preElectionTime = System.currentTimeMillis();
        // 测试
        if (response.type == -1) {
            System.out.println("接收到了返回的消息" + (Response<String>) response);
            return;
        }

        // 对方的任期比自己的新，更新任期，并降级为候选者
        if (response.resTerm > node.currentTerm) {
            node.setCurrentTerm(response.resTerm);
            NodeDefaultImpl nodeDefaultImpl = new NodeDefaultImpl(node);
            nodeDefaultImpl.changeState(State.FOLLOWER);
        }

        if (response.type == Request.R_VOTE) {
            recVoteRes(response, node);
        } else if (response.type == Request.A_ENTRIES) {

        }

    }

    @Override
    public Response recReq(Request request, Node node) {
        node.preElectionTime = System.currentTimeMillis();
        if (request.getCmd() == -1) {
            System.out.println("接受到了请求的测试信息：" + request);
            log.info("接受到了请求的测试信息：" + request);
            Response<String> response = new Response<>();
            response.setResult("已经接收到了测试数据" + request);
            response.setType(-1);
            return response;
        }
        System.out.println(node.peerSet.getSelf().getAddr() + "接收到请求");
        // 任期比自己的新，退回follower
        if (request.reqTerm > node.currentTerm) {
            node.setCurrentTerm(request.reqTerm);
            if (node.getState() != State.FOLLOWER) {
                NodeDefaultImpl nodeDefaultImpl = new NodeDefaultImpl(node);
                nodeDefaultImpl.changeState(State.FOLLOWER);
            }
        }

        // 接收到投票请求
        if (request.getCmd() == Request.R_VOTE) {
            log.info(node.peerSet.getSelf().getAddr() + "接收到投票请求");
            System.out.println(node.peerSet.getSelf().getAddr() + "接收到投票请求");
            Response res = recVoteReq(request, node);
            return res;
            // Response<String> response = new Response<>();
            // response.setResult("已经接收到了测试数据" + request);
            // response.setType(-1);
            // return response;

        } else if (request.getCmd() == Request.A_ENTRIES) {
            Response res = recAentryReq(request, node);
            return res;
        }

        return null;

    }

    @Override
    public void recVoteRes(Response response, Node node) {
        log.info(node.peerSet.getSelf().getAddr() + "接收到投票请求的结果");
        Response<RvoteResult> rvoResult = (Response<RvoteResult>) response;
        // 接收到了过期的选票
        if (response.reqTerm != node.currentTerm) {
            log.error("这个投票过时了，不处理，当前任期为{}，接收到的任期为{}", node.getCurrentTerm(),
                    response.reqTerm);
        }
        // 接收到了投票的请求信息
        else {
            if (rvoResult.getResult().isVoteGranted()) {
                log.info("接收到赞同票，来自" + rvoResult.addr);
                node.voteGetMap.put(rvoResult.addr, true);
            } else {
                log.info("接收到拒绝票，来自" + rvoResult.addr);
                node.voteGetMap.put(rvoResult.addr, false);
            }
            // 判断接收到的选票数是否超过一半
            List<Peer> peers = node.peerSet.getPeersWithOutSelf();
            int voteSucessNums = 0;
            NodeDefaultImpl nodeDefault = new NodeDefaultImpl(node);
            for (Peer peer : peers) {
                if (!node.voteGetMap.containsKey(peer.getAddr()) || (node.voteGetMap.get(peer.getAddr()) == null)) {
                    continue;
                }
                voteSucessNums += node.voteGetMap.get(peer.getAddr()) ? 1 : 0;
            }
            log.info("当前收到的赞成票:" + voteSucessNums);
            // 收到的赞同票超过半数节点，转变为LEADER
            if (voteSucessNums > (node.peerSet.getList().size() / 2)) {
                nodeDefault.changeState(State.LEADER);
            }
        }
    }

    @Override
    public Response recVoteReq(Request request, Node node) {
        Response<RvoteResult> response = new Response<>(Request.R_VOTE, node.peerSet.getSelf().getAddr(),
                request.reqTerm, node.getCurrentTerm());
        response.setResult(new RvoteResult(false));
        try {
            if (!voteLock.tryLock()) {
                return response;
            }
            // 对方任期没有自己新
            if (request.getReqTerm() < node.getCurrentTerm()) {
                log.info("对方任期没有自己新，请求节点term：" + request.reqTerm + "自身节点term：" + node.getCurrentTerm());
                response.getResult().setVoteGranted(false);
                return response;
            }

            node.setCurrentTerm(request.reqTerm);

            // (尚未投票||投票的节点与请求节点一致)
            if ((StringUtil.isNullOrEmpty(node.getVotedFor()) || node.votedFor.equals(response.getAddr()))) {
                LogEntry nodelogEntry = node.getLogStorage().getEntry(node.getLogStorage().getLastLogIndex());
                // 请求投票的节点的日志不够新
                if ((((RvoteParam) request.getObj()).getLastLogTerm() < nodelogEntry.getTerm())
                        || (((RvoteParam) request.getObj()).getLastLogIndex() < nodelogEntry.getIndex())) {
                    log.info("对方日志没有自己新，请求节点：" + ((RvoteParam) request.getObj()).getLastLogTerm()
                            + ((RvoteParam) request.getObj()).getLastLogIndex() + "自身节点term：" + nodelogEntry.getTerm()
                            + nodelogEntry.getIndex());
                    response.getResult().setVoteGranted(false);
                    return response;
                }
                // 投赞同票
                response.getResult().setVoteGranted(true);
                return response;
            } else {
                response.getResult().setVoteGranted(false);
                return response;
            }

        } finally {
            voteLock.unlock();
        }

    }

    @Override
    public Response recAentryReq(Request request, Node node) {
        AentryResult result = new AentryResult(node.currentTerm, false);
        Response<AentryResult> response = new Response<>(Request.A_ENTRIES, node.peerSet.getSelf().getAddr(),
                request.reqTerm, node.getCurrentTerm());
        // 心跳请求
        if (Objects.isNull(request.getObj())) {
            log.info(node.peerSet.getSelf().getAddr() + "接收到心跳请求");
            result.setSuccess(true);
            response.setResult(result);
            return response;
        } else {
            log.info(node.peerSet.getSelf().getAddr() + "接收到日志请求");
        }
        return response;
    }

    @Override
    public void recAentryRes(Response response, Node node) {
        response = (Response<AentryResult>) response;
        if (response.getResult().equals(null)) {
            return;
        }
        log.info(node.peerSet.getSelf().getAddr() + "接收到日志请求的结果");
    }

}
