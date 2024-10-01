package cn.wjc.tool.database.mapper;

import cn.wjc.tool.entity.LogEntry;

public interface LogMapper {

    LogEntry getLogEntryByIndex(Long index);

    LogEntry getMaxIndexLogEntry();

    Long getMaxIndex();

    Long getMinIndex();

    void insertLogEntity(LogEntry logEntry);

    void deleteLogEntityByIndex(Long index);

    void deleteLogEntityLess(Long index);

    void deleteLogEntityGreater(Long index);

}
