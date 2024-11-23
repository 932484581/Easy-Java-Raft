package cn.wjc.tool.command;

import cn.wjc.tool.entity.CommandParam;

public interface CommandProtocol {
    /**
     * @description: 判断输入的指令是GET还是SET类型
     * @param {String} command
     * @return GET:1, SET:0
     * @author: WJC
     */
    public CommandParam analysis(String command);
}
