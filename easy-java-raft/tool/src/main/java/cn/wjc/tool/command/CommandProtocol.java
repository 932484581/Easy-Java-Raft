package cn.wjc.tool.command;

import cn.wjc.tool.entity.CommandParam;

public interface CommandProtocol {
    /**
     * @description: 判断输入的指令是GET还是SET类型
     * @param {String} command
     * @return SET:0, GET:1, COldNew_add:2, COldNew_remove:3
     * @author: WJC
     */
    public CommandParam analysis(String command);

    /**
     * @description: 提交指令
     * @param {CommandParam} commandParam
     * @return {*}
     * @author: WJC
     */
    public String commitCommand(CommandParam commandParam);
}
