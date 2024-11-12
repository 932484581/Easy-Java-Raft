package cn.wjc.tool.database.mapper;

import org.apache.ibatis.annotations.Param;

import cn.wjc.tool.entity.LogEntry;

public interface LogMapper {

    LogEntry getLogEntryByIndex(@Param("index") Long index, @Param("name") String name);

    LogEntry getMaxIndexLogEntry(@Param("name") String name);

    Long getMaxIndex(@Param("name") String name);

    Long getMinIndex(@Param("name") String name);

    void insertLogEntity(@Param("data") LogEntry logEntry, @Param("name") String name);

    void deleteLogEntityByIndex(@Param("index") Long index, @Param("name") String name);

    void deleteLogEntityLess(@Param("index") Long index, @Param("name") String name);

    void deleteLogEntityGreater(@Param("index") Long index, @Param("name") String name);

}
