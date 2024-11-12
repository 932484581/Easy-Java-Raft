package cn.wjc.server.utils.impl;

import cn.wjc.server.utils.LogModule;
import cn.wjc.tool.entity.LogEntry;

public class LogModuleImpl implements LogModule {

    @Override
    public void write(LogEntry logEntry) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }

    @Override
    public LogEntry read(Long index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

    @Override
    public void removeOnStartIndex(Long startIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeOnStartIndex'");
    }

    @Override
    public LogEntry getLast() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLast'");
    }

    @Override
    public Long getLastIndex() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLastIndex'");
    }

}
