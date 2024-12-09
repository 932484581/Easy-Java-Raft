package cn.wjc.tool.command.impl;

import java.util.ArrayList;
import java.util.List;

import cn.wjc.server.action.AppendAentryAction;
import cn.wjc.server.model.impl.NodeDefaultImpl;
import cn.wjc.tool.command.CommandProtocol;
import cn.wjc.tool.entity.Command;
import cn.wjc.tool.entity.CommandParam;
import cn.wjc.tool.entity.LogEntry;
import cn.wjc.tool.entity.Node;
import cn.wjc.tool.entity.Peer;
import cn.wjc.tool.entity.State;
import cn.wjc.tool.exception.CommandException;
import cn.wjc.tool.storage.impl.KVStorageImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandProtocolImpl implements CommandProtocol {
    Node node;
    KVStorageImpl kvStorageImpl;

    public CommandProtocolImpl(String databaseName, Node node) throws Throwable {
        this.kvStorageImpl = new KVStorageImpl(databaseName);
        this.kvStorageImpl.init();
        this.node = node;
    }

    public CommandProtocolImpl(KVStorageImpl kvStorageImpl, Node node) throws Throwable {
        this.kvStorageImpl = kvStorageImpl;
        this.kvStorageImpl.init();
        this.node = node;
    }

    @Override
    public CommandParam analysis(String command) {
        String[] parts = command.split(" ", 3); // 将指令分割为最多3部分
        CommandParam commandParam = new CommandParam();
        switch (parts[0]) {
            case "GET":
                if (parts.length != 2) {
                    System.err.println("Invalid command");
                    throw new CommandException("Invalid command");
                }
                commandParam.setKey(parts[1]);
                commandParam.setType(CommandParam.GETCOMMAND);
                return commandParam;

            case "SET":
                if (parts.length != 3) {
                    System.err.println("Invalid command");
                    throw new CommandException("Invalid command");
                }
                commandParam.setKey(parts[1]);
                commandParam.setVal(parts[2]);
                commandParam.setType(CommandParam.SETCOMMAND);
                return commandParam;

            case "CNEW":
                commandParam.setType(CommandParam.CNEW);
                return commandParam;

            case "CHANGE":
                if (parts.length != 3) {
                    System.err.println("Invalid command");
                    throw new CommandException("Invalid command");
                }
                if (parts[1].equals("ADD")) {
                    commandParam.setVal(parts[2]);
                    commandParam.setType(CommandParam.CHANGE_ADD_COMMAND);
                    return commandParam;
                } else if (parts[1].equals("REMOVE")) {
                    commandParam.setVal(parts[2]);
                    commandParam.setType(CommandParam.CHANGE_REMOVE_COMMAND);
                    return commandParam;
                }
            case "UPDATE":
                commandParam.setType(CommandParam.UPDATE);
                commandParam.setVal(parts[1]);
                return commandParam;
            default:
                System.err.println("Invalid command");
                throw new CommandException("Invalid command");
        }
    }

    @Override
    public String commitCommand(CommandParam commandParam) {
        if (commandParam.getType() == CommandParam.GETCOMMAND) {
            String res = kvStorageImpl.getString(commandParam.getKey());
            return res;
        } else if (commandParam.getType() == CommandParam.SETCOMMAND) {
            String target = kvStorageImpl.getString(commandParam.getKey());
            if (target != null) {
                kvStorageImpl.updataString(commandParam.getKey(), commandParam.getVal());
            } else {
                kvStorageImpl.setString(commandParam.getKey(), commandParam.getVal());
            }
            return null;
        } else if (commandParam.getType() == CommandParam.CHANGE_ADD_COMMAND
                || commandParam.getType() == CommandParam.CHANGE_REMOVE_COMMAND) {
            System.out.println("进入COldNew状态");
            String[] addr = commandParam.getVal().split(" ");
            List<Peer> peerlist = new ArrayList<>();
            for (int i = 0; i < addr.length; i++) {
                peerlist.add(new Peer(addr[i]));
            }
            node.peerSet.setCOldNewList(peerlist);
            for (Peer peer : node.peerSet.getCOldNewList()) {
                try {
                    node.getClient().connectToServer(peer.getAddr());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            node.setCOldNew(commandParam.getType());
            // 将CNEW指令追加到日志中
            if (node.getState() == State.LEADER) {
                LogEntry logEntry = LogEntry.builder()
                        .command(Command.builder().command("CNEW").build())
                        .index(node.getLogStorage().getLastLogIndex() + 1)
                        .term(node.getCurrentTerm())
                        .build();
                node.getLogStorage().appendEntry(logEntry);
                AppendAentryAction appendAentryAction = new AppendAentryAction(node);
                appendAentryAction.run();
            }
        } else if (commandParam.getType() == CommandParam.CNEW) {
            log.warn(node.getPeerSet().getSelf().getAddr() + ":进入CNEW:" + String.valueOf(node.getPeerSet()));
            // 如果移除节点，则断开其连接
            if (node.getCOldNew() == CommandParam.CHANGE_REMOVE_COMMAND) {
                for (Peer peer : node.peerSet.getList()) {
                    for (Peer peer2 : node.peerSet.getCOldNewList()) {
                        if (peer.getAddr().equals(peer2.getAddr())) {
                            node.peerSet.getList().remove(peer);
                            try {
                                node.getClient().disconnectAddr(peer.getAddr());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            // 如果是新增节点，建立连接
            else if (node.getCOldNew() == CommandParam.CHANGE_ADD_COMMAND) {
                for (Peer peer : node.peerSet.getCOldNewList()) {
                    boolean flag = true;
                    for (Peer peer2 : node.peerSet.getList()) {
                        if (peer.getAddr().equals(peer2.getAddr())) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        node.peerSet.getList().add(peer);
                        try {
                            node.getClient().connectToServer(peer.getAddr());
                        } catch (Throwable e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
            if ((node.getCOldNew() == CommandParam.CHANGE_REMOVE_COMMAND) && (node.getState() == State.LEADER)) {
                boolean flag = true;
                for (Peer peer : node.peerSet.getCOldNewList()) {
                    if (peer.getAddr().equals(node.getPeerSet().getSelf().getAddr())) {
                        flag = false;
                    }
                }
                if (flag) {
                    NodeDefaultImpl nodeDefault = new NodeDefaultImpl(node);
                    nodeDefault.changeState(State.FOLLOWER);
                }
            }
            node.getPeerSet().getCOldNewList().clear();
            log.warn(node.getPeerSet().getSelf().getAddr() + ":CNEW结束:" + String.valueOf(node.getPeerSet()));
            node.setCOldNew(CommandParam.CNEW);
        } else if (commandParam.getType() == CommandParam.UPDATE) {
            try {
                node.getClient().connectToServer(commandParam.getVal());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
