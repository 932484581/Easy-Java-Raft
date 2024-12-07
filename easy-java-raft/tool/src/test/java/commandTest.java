import org.junit.Test;

import cn.wjc.tool.command.impl.CommandProtocolImpl;
import cn.wjc.tool.entity.CommandParam;

public class commandTest {
    @Test
    public void testcommand() throws Throwable {
        String command = "SET test3 测试信息";
        CommandProtocolImpl commandProtocolImpl = new CommandProtocolImpl("kv_store");
        CommandParam commandParam = commandProtocolImpl.analysis(command);
        System.out.println(commandParam);
        commandProtocolImpl.commitCommand(commandParam);
    }
}
