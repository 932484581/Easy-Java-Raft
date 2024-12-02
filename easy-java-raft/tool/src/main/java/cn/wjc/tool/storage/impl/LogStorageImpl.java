package cn.wjc.tool.storage.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import cn.wjc.tool.database.mapper.LogMapper;
import cn.wjc.tool.entity.LogEntry;
import cn.wjc.tool.storage.LogStorage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogStorageImpl implements LogStorage {
    private SqlSession session = null;
    private LogMapper LogMapper = null;
    private String dabaseName;

    public LogStorageImpl(String dabaseName) {
        this.dabaseName = dabaseName;
    }

    @Override
    public long getFirstLogIndex() {
        long result = LogMapper.getMinIndex(dabaseName);
        return result;
    }

    @Override
    public long getLastLogIndex() {
        try {
            long result = LogMapper.getMaxIndex(dabaseName);
            return result;
        } catch (Exception e) {
            return 0;
        }

    }

    @Override
    public LogEntry getEntry(long index) {
        LogEntry result = LogMapper.getLogEntryByIndex(index, dabaseName);
        if (result == null) {
            return LogEntry.builder().index(0L).term(0L).build();
        }
        return result;
    }

    @Override
    public long getTerm(long index) {
        LogEntry entity = LogMapper.getLogEntryByIndex(index, dabaseName);
        long result = entity.getTerm();
        return result;
    }

    @Override
    public boolean appendEntry(LogEntry entry) {
        try {
            LogMapper.insertLogEntity(entry, dabaseName);
            session.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int appendEntries(List<LogEntry> entries) {
        Iterator<LogEntry> iterator = entries.iterator();
        int result = 0;
        while (iterator.hasNext()) {
            LogEntry entry = iterator.next();
            try {
                LogMapper.insertLogEntity(entry, dabaseName);
                result = result + 1;
            } catch (Exception e) {
                log.error(e.toString());
                break;
            }
        }
        session.commit();
        return result;
    }

    @Override
    public boolean truncatePrefix(long firstIndexKept) {
        try {
            LogMapper.deleteLogEntityLess(firstIndexKept, dabaseName);
            session.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean truncateSuffix(long lastIndexKept) {
        try {
            LogMapper.deleteLogEntityGreater(lastIndexKept, dabaseName);
            session.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void destroy() throws Throwable {
        session.close();
    }

    @Override
    public void init() throws Throwable {
        SqlSessionFactory sqlSessionFactory = GetDataSource.getMybatisSessionFactory();
        session = sqlSessionFactory.openSession();
        LogMapper = session.getMapper(LogMapper.class);
    }
}
