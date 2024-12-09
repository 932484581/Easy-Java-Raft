package cn.wjc.server.msg.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import cn.wjc.server.action.AppendAentryAction;
import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.server.msg.MsgProcessing;
import cn.wjc.tool.entity.AentryParam;
import cn.wjc.tool.entity.AentryResult;
import cn.wjc.tool.entity.Command;
import cn.wjc.tool.entity.CommandParam;
import cn.wjc.tool.entity.CommandResult;
import cn.wjc.tool.entity.LogEntry;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.Request;
import cn.wjc.tool.entity.Response;
import cn.wjc.tool.entity.RvoteParam;
import cn.wjc.tool.entity.RvoteResult;
import cn.wjc.tool.entity.State;
import cn.wjc.tool.exception.NettyException;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MsgProcessingImpl implements MsgProcessing {
    public final ReentrantLock voteLock = new ReentrantLock();
    public final ReentrantLock aentryLock = new ReentrantLock();

    @Override
    public void recRes(Response response, Node node) {
        node.preElectionTime = System.currentTimeMillis();
        // 测试
        if (response.type == -1) {
            System.out.println("接收到了返回的消息" + (Response<String>) response);
            return;
        }
        if (response.type == Request.COMMAND_REQ) {
            recCommandRes(response, node);
            return;
        }

        // 对方的任期比自己的新，更新任期，并降级为候选者
        if (response.resTerm > node.currentTerm) {
            node.setCurrentTerm(response.resTerm);
            NodeDefaultImpl nodeDefaultImpl = new NodeDefaultImpl(node);
            nodeDefaultImpl.changeState(State.FOLLOWER);
            return;
        }

        if (response.type == Request.R_VOTE) {
            recVoteRes(response, node);
        } else if (response.type == Request.A_ENTRIES) {
            recAentryRes(response, node);
        }

    }

    @Override
    public Response recReq(Request request, Node node) {
        if (request.getCmd() == -1) {
            System.out.println("接受到了请求的测试信息：" + request);
            log.info("接受到了请求的测试信息：" + request);
            Response<String> response = new Response<>();
            response.setResult("已经接收到了测试数据" + request);
            response.setType(-1);
            return response;
        }
        if (request.getCmd() == Request.COMMAND_REQ) {
            Response res = recCommandReq(request, node);
            return res;
        }
        // System.out.println(node.peerSet.getSelf().getAddr() + "接收到请求");
        // 任期比自己的新，退回follower
        if (request.reqTerm > node.currentTerm) {
            node.setCurrentTerm(request.reqTerm);
            if (node.getState() != State.FOLLOWER) {
                NodeDefaultImpl nodeDefaultImpl = new NodeDefaultImpl(node);
                nodeDefaultImpl.changeState(State.FOLLOWER);
            }
        } else if (request.reqTerm < node.currentTerm) {
            Response response = new Response<>();
            response.setAddr(request.getAddr());
            response.setReqTerm(request.reqTerm);
            response.setResTerm(node.currentTerm);
            return response;
        }

        // 接收到投票请求
        if (request.getCmd() == Request.R_VOTE) {
            log.info(node.peerSet.getSelf().getAddr() + "接收到投票请求");
            System.out.println(node.peerSet.getSelf().getAddr() + "接收到投票请求");
            Response res = recVoteReq(request, node);
            return res;

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
                node.getResultMap.put(rvoResult.addr, 1L);
            } else {
                log.info("接收到拒绝票，来自" + rvoResult.addr);
                node.getResultMap.put(rvoResult.addr, 0L);
            }
            // 判断接收到的选票数是否超过一半
            List<Peer> peers = node.peerSet.getPeersWithOutSelf();
            int voteSucessNums = 0;
            int cOldNewVoteSucessNums = 0;
            NodeDefaultImpl nodeDefault = new NodeDefaultImpl(node);
            for (Peer peer : peers) {
                if (!node.getResultMap.containsKey(peer.getAddr()) || (node.getResultMap.get(peer.getAddr()) == null)) {
                    continue;
                }
                voteSucessNums += node.getResultMap.get(peer.getAddr()) == 1L ? 1 : 0;
            }
            log.info("当前收到的赞成票:" + voteSucessNums);

            if (node.getCOldNew() != CommandParam.CHANGE_ADD_COMMAND) {
                // 收到的赞同票超过半数节点，转变为LEADER
                if (voteSucessNums > (node.peerSet.getList().size() / 2)) {
                    nodeDefault.changeState(State.LEADER);
                }
            } else {
                for (Peer peer : node.getPeerSet().getCOldNewList()) {
                    if (!node.getResultMap.containsKey(peer.getAddr())
                            || (node.getResultMap.get(peer.getAddr()) == null)) {
                        continue;
                    }
                    cOldNewVoteSucessNums += node.getResultMap.get(peer.getAddr()) == 1L ? 1 : 0;
                }
                cOldNewVoteSucessNums += voteSucessNums;

                if ((voteSucessNums > (node.peerSet.getList().size() / 2))
                        && (cOldNewVoteSucessNums > ((node.peerSet.getList().size()
                                + node.peerSet.getCOldNewList().size()) / 2))) {
                    log.info("符合COldNew下的选举要求，称为LEADER");
                    nodeDefault.changeState(State.LEADER);
                }
            }
        }
    }

    @Override
    public Response recVoteReq(Request request, Node node) {
        Response<RvoteResult> response = new Response<>(Request.R_VOTE, node.peerSet.getSelf().getAddr(),
                request.reqTerm, node.getCurrentTerm());
        response.setResult(new RvoteResult(false));
        try {
            while (!voteLock.tryLock()) {
                // 可以在这里添加一些延迟，比如Thread.sleep()，以避免忙等
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } // 延迟100毫秒
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
                node.preElectionTime = System.currentTimeMillis();
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
        response.setResult(result);
        AentryParam aentryParam = (AentryParam) request.getObj();

        node.getPeerSet().setLeader(aentryParam.getLeaderId());
        // 更新从超时时间
        node.preElectionTime = System.currentTimeMillis();
        try {
            if (!aentryLock.tryLock()) {
                return response;
            }

            // 心跳请求
            if (aentryParam.getEntries() == null) {
                // 同步未提交的日志
                if (node.commitIndex < aentryParam.getLeaderCommit()) {
                    // 应用日志
                    log.info(node.getPeerSet().getSelf().getAddr() + ": FOLLOWER提交日志");
                    for (long i = node.commitIndex + 1; i <= node.getLogStorage().getLastLogIndex(); i++) {
                        if (i > aentryParam.getLeaderCommit()) {
                            break;
                        }
                        node.getCommandProtocolImpl().commitCommand(node.getCommandProtocolImpl()
                                .analysis(node.getLogStorage().getEntry(i).getCommand().getCommand()));
                        node.setCommitIndex(i);
                    }
                }
                return null;
            }

            // 日志请求
            log.info(node.peerSet.getSelf().getAddr() + "接收到日志请求");
            LogEntry nodeLog = node.getLogStorage().getEntry(aentryParam.getPreLogIndex());
            LogEntry nodeLog2 = node.getLogStorage().getEntry(aentryParam.getPreLogIndex() + 1);

            // 找到最新的相同的日志
            if (nodeLog.getTerm() == aentryParam.getPreLogTerm()) {
                // 如果后续日志相同，代表该日志接收过了
                if ((nodeLog2.getTerm() == aentryParam.getEntries()[0].getTerm())) {
                    log.info(node.peerSet.getSelf().getAddr() + "已经接收过该日志");
                    if (node.getCanJoin()) {
                        return null;
                    }
                }
                // 删除与LEADER不相同的日志
                log.info(node.peerSet.getSelf().getAddr() + "追加日志");
                if (node.getLogStorage().getLastLogIndex() > nodeLog.getIndex()) {
                    node.getLogStorage().truncateSuffix(nodeLog.getIndex() + 1);
                }
                // 追加日志
                int succesWriteNum = node.getLogStorage().appendEntries(Arrays.asList(aentryParam.getEntries()));
                result.setSuccess(true);
                result.setMatchIndex(aentryParam.getPreLogIndex() + succesWriteNum);

                // 同步未提交的日志
                if (node.commitIndex < aentryParam.getLeaderCommit()) {
                    // 应用日志
                    log.info(node.getPeerSet().getSelf().getAddr() + ": FOLLOWER提交日志");
                    for (long i = node.commitIndex + 1; i <= node.getLogStorage().getLastLogIndex(); i++) {
                        if (i > aentryParam.getLeaderCommit()) {
                            break;
                        }
                        node.getCommandProtocolImpl().commitCommand(node.getCommandProtocolImpl()
                                .analysis(node.getLogStorage().getEntry(i).getCommand().getCommand()));
                        node.setCommitIndex(i);
                    }
                }
                if (!node.getCanJoin()) {
                    log.info(node.peerSet.getSelf().getAddr() + ":检查！！！" + String.valueOf(aentryParam));
                }
                if ((node.getCommitIndex() == aentryParam.getLeaderCommit())
                        && (!node.getCanJoin())) {
                    log.info(node.peerSet.getSelf().getAddr() + "日志已经是最新的了,可以加入节点");
                    node.setCanJoin(true);
                }
                return response;
            } else {
                log.info(node.peerSet.getSelf().getAddr() + "日志不一致，追溯前一个日志" + aentryParam.getPreLogIndex());
                result.setConflictIndex(aentryParam.getPreLogIndex());
                return response;
            }

        } finally {
            aentryLock.unlock();
        }
    }

    @Override
    public void recAentryRes(Response response, Node node) {
        try {
            if (!aentryLock.tryLock()) {
                return;
            }
            System.err.println(response);
            log.info(node.peerSet.getSelf().getAddr() + ":接收到" + response.getAddr() + "的日志回应");
            Response<AentryResult> getresponse = (Response<AentryResult>) response;
            Long median = 0L;
            // 获取到的是成功的日志
            if (getresponse.getResult().isSuccess() && getresponse.getResult().getMatchIndex() != 0) {
                // 判断当前过半日志接收到哪里,提交日志
                log.info(node.peerSet.getSelf().getAddr() + ":" + response.getAddr() + "接收成功");
                node.getResultMap.put(getresponse.getAddr(), getresponse.getResult().getMatchIndex());
                // 判断一半的选票接收到的日志位置
                List<Long> valuesList = node.getResultMap.values().stream().collect(Collectors.toList());
                Collections.sort(valuesList);
                median = valuesList.get(valuesList.size() / 2);
                // 超过半数的节点接收到日志
                if (median > node.commitIndex) {
                    // 应用日志
                    log.info(node.peerSet.getSelf().getAddr() + ":" + "超过半数接收日志成功，提交日志到" + median);
                    long i = node.commitIndex;
                    node.setCommitIndex(median);
                    for (i = i + 1; i <= median; i++) {
                        node.getCommandProtocolImpl().commitCommand(node.getCommandProtocolImpl()
                                .analysis(node.getLogStorage().getEntry(i).getCommand().getCommand()));
                    }
                }

                // 判断日志是否同步到最新的日志,没有则继续发送后续日志
                if (getresponse.getResult().getMatchIndex() < node.getLogStorage().getLastLogIndex()) {
                    log.warn(node.peerSet.getSelf().getAddr() + ":" + response.getAddr() + "还未同步到最新的日志");
                    LogEntry susseswsPreLog = node.getLogStorage().getEntry(getresponse.getResult().getMatchIndex());
                    AentryParam aentryParam = new AentryParam(node.currentTerm, getresponse.getAddr(),
                            node.getPeerSet().getSelf().getAddr(), susseswsPreLog.getIndex(), susseswsPreLog.getTerm(),
                            null, node.getCommitIndex());
                    LogEntry[] wait2SentEntries = new LogEntry[(int) (Math.min(
                            (node.getLogStorage().getLastLogIndex() - getresponse.getResult().getMatchIndex()),
                            5L))];
                    for (int i = (int) (getresponse.getResult().getMatchIndex())
                            + 1; i <= (int) (getresponse.getResult().getMatchIndex() + Math.min(
                                    (node.getLogStorage().getLastLogIndex() - getresponse.getResult().getMatchIndex()),
                                    5L)); i++) {
                        wait2SentEntries[i - ((int) (getresponse.getResult().getMatchIndex()) + 1)] = node
                                .getLogStorage()
                                .getEntry(i);
                    }
                    aentryParam.setEntries(wait2SentEntries);
                    Request request = Request.builder()
                            .cmd(Request.A_ENTRIES)
                            .addr(getresponse.getAddr())
                            .obj(aentryParam)
                            .reqTerm(node.currentTerm)
                            .build();
                    try {
                        node.client.send(request);
                    } catch (Exception e) {
                        log.error("发送投票到：" + getresponse.getAddr() + "失败 ");
                        e.printStackTrace();
                    } catch (NettyException e) {
                        log.error("发送投票到：" + getresponse.getAddr() + "失败 " + e);
                        e.printStackTrace();
                    }
                }
            }
            // 接收到了失败的日志
            else if (!getresponse.getResult().isSuccess()) {
                log.warn(node.peerSet.getSelf().getAddr() + ":" + response.getAddr() + "接收失败，追溯前一个日志"
                        + (getresponse.getResult().getConflictIndex() - 1) + " : " + getresponse.getResult());
                LogEntry waitToSendPreLog = node.getLogStorage()
                        .getEntry(getresponse.getResult().getConflictIndex() - 1);
                LogEntry[] wait2SentEntries = new LogEntry[1];
                wait2SentEntries[0] = node.getLogStorage().getEntry(getresponse.getResult().getConflictIndex());
                AentryParam aentryParam = AentryParam.builder()
                        .term(node.currentTerm)
                        .preLogIndex(waitToSendPreLog.getIndex())
                        .preLogTerm(waitToSendPreLog.getTerm())
                        .leaderId(node.getPeerSet().getSelf().getAddr())
                        .serverId(getresponse.getAddr())
                        .entries(wait2SentEntries)
                        .leaderCommit(node.commitIndex)
                        .build();
                Request request = Request.builder()
                        .cmd(Request.A_ENTRIES)
                        .addr(getresponse.getAddr())
                        .obj(aentryParam)
                        .reqTerm(node.currentTerm)
                        .build();
                try {
                    node.client.send(request);
                } catch (Exception e) {
                    log.error("发送投票到：" + getresponse.getAddr() + "失败 ");
                    e.printStackTrace();
                } catch (NettyException e) {
                    log.error("发送投票到：" + getresponse.getAddr() + "失败 " + e);
                    e.printStackTrace();
                }
            }
        } finally {
            aentryLock.unlock();
        }

    }

    @Override
    public void recCommandRes(Response response, Node node) {
        Response<CommandResult> getresponse = (Response<CommandResult>) response;
        if (!getresponse.getResult().isResult()) {
            log.warn("请求发送失败,应该发往:" + getresponse.getResult().getAddr());
            System.err.println("请求发送失败,应该发往:" + getresponse.getResult().getAddr());
            Request request = Request.builder()
                    .cmd(Request.COMMAND_REQ)
                    .addr(getresponse.getResult().getAddr())
                    .obj(Command.builder().command(getresponse.getResult().getResultmsg()).build())
                    .reqTerm(node.currentTerm)
                    .build();
            try {
                node.client.send(request);
            } catch (Exception e) {
                log.error("发送投票到：" + getresponse.getAddr() + "失败 ");
                e.printStackTrace();
            } catch (NettyException e) {
                log.error("发送投票到：" + getresponse.getAddr() + "失败 " + e);
                e.printStackTrace();
            }
        } else if (getresponse.getResult().getType() == CommandParam.GETCOMMAND) {
            log.info("获得查询结果:" + getresponse.getResult().getResultmsg());
        } else if (getresponse.getResult().getType() == CommandParam.SETCOMMAND) {
            log.info("写入成功");
        }
    }

    @Override
    public Response recCommandReq(Request request, Node node) {
        Command command = (Command) request.getObj();

        CommandParam commandParam = node.getCommandProtocolImpl().analysis(command.getCommand());

        log.info("节点:" + node.getPeerSet().getSelf().getAddr() + "  接收到指令:" + command.getCommand());
        System.out.println("节点:" + node.getPeerSet().getSelf().getAddr() + "  接收到指令:" + command.getCommand());
        Response<CommandResult> response = new Response<>(Request.COMMAND_REQ, node.peerSet.getSelf().getAddr(),
                request.reqTerm, node.getCurrentTerm());
        CommandResult commandResult = CommandResult.builder().resultmsg(command.getCommand()).result(true)
                .type(commandParam.getType()).build();
        response.setResult(commandResult);

        // 判断节点是否为LEADER，不是则返回失败并转发
        if (node.getState() != State.LEADER) {
            // 可能当时领导者还未选举出来，因此等待
            while (true) {
                if (node.getPeerSet().getLeader() != null) {
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } // 让线程休眠0.2s
            }
            commandResult.setResult(false);
            commandResult.setAddr(node.getPeerSet().getLeader());
            log.info("节点不是LEADER, LEADER地址为：" + node.getPeerSet().getLeader());
            return response;
        }

        // 指令是GET指令
        if (commandParam.getType() == CommandParam.GETCOMMAND) {
            log.info("节点" + node.getPeerSet().getSelf().getAddr() + ",接收到GET指令");
            String s = node.getKvStorageImpl().getString(commandParam.getKey());
            commandResult.setResultmsg(s);
        }
        // 指令是SET指令
        else if (commandParam.getType() == CommandParam.SETCOMMAND) {
            log.info("节点" + node.getPeerSet().getSelf().getAddr() + ",接收到SET指令");
            LogEntry logEntry = LogEntry.builder()
                    .command(command)
                    .index(node.getLogStorage().getLastLogIndex() + 1)
                    .term(node.getCurrentTerm())
                    .build();
            // 指令是SET指令，将指令追加到日志中
            node.getLogStorage().appendEntry(logEntry);
        }
        // 指令是节点变更指令
        else if (commandParam.getType() == CommandParam.CHANGE_ADD_COMMAND
                || commandParam.getType() == CommandParam.CHANGE_REMOVE_COMMAND) {
            log.info("节点" + node.getPeerSet().getSelf().getAddr() + ",接收到节点变更指令" + String.valueOf(node.getPeerSet()));
            LogEntry logEntry = LogEntry.builder()
                    .command(command)
                    .index(node.getLogStorage().getLastLogIndex() + 1)
                    .term(node.getCurrentTerm())
                    .build();
            // 将指令追加到日志中
            node.getLogStorage().appendEntry(logEntry);
            node.setCOldNew(commandParam.getType());
        } else if (commandParam.getType() == CommandParam.UPDATE) {
            log.info("节点" + node.getPeerSet().getSelf().getAddr() + ",接收到同步日志指令,当前节点commitindex："
                    + String.valueOf(node.getCommitIndex()));
            node.getCommandProtocolImpl().commitCommand(commandParam);
            NodeDefaultImpl nodeDefaultImpl = new NodeDefaultImpl(node);
            nodeDefaultImpl.sendUpdateLog(commandParam.getVal());
        }
        // 向各个节点
        AppendAentryAction appendAentryAction = new AppendAentryAction(node);
        appendAentryAction.run();
        return response;
    }

}
