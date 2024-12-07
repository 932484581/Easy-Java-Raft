package cn.wjc.tool.command.impl;

import cn.wjc.tool.command.CommandProtocol;
import cn.wjc.tool.entity.CommandParam;
import cn.wjc.tool.exception.CommandException;
import cn.wjc.tool.storage.impl.KVStorageImpl;

public class CommandProtocolImpl implements CommandProtocol {
    KVStorageImpl kvStorageImpl;

    public CommandProtocolImpl(String databaseName) throws Throwable {
        this.kvStorageImpl = new KVStorageImpl(databaseName);
        this.kvStorageImpl.init();
    }

    public CommandProtocolImpl(KVStorageImpl kvStorageImpl) throws Throwable {
        this.kvStorageImpl = kvStorageImpl;
        this.kvStorageImpl.init();
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
                return commandParam;

            case "SET":
                if (parts.length != 3) {
                    System.err.println("Invalid command");
                    throw new CommandException("Invalid command");
                }
                commandParam.setKey(parts[1]);
                commandParam.setVal(parts[2]);
                return commandParam;
            default:
                System.err.println("Invalid command");
                throw new CommandException("Invalid command");
        }
    }

    @Override
    public String commitCommand(CommandParam commandParam) {
        if (commandParam.getType() == 1) {
            String res = kvStorageImpl.getString(commandParam.getKey());
            return res;
        } else if (commandParam.getType() == 0) {
            String target = kvStorageImpl.getString(commandParam.getKey());
            if (target != null) {
                kvStorageImpl.updataString(commandParam.getKey(), commandParam.getVal());
            } else {
                kvStorageImpl.setString(commandParam.getKey(), commandParam.getVal());
            }

            return null;
        }
        return null;
    }
}
