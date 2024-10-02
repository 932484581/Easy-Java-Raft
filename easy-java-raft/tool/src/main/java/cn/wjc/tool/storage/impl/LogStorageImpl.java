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

    @Override
    public long getFirstLogIndex() {
        long result = LogMapper.getMinIndex();
        return result;
    }

    @Override
    public long getLastLogIndex() {
        long result = LogMapper.getMaxIndex();
        return result;
    }

    @Override
    public LogEntry getEntry(long index) {
        LogEntry result = LogMapper.getLogEntryByIndex(index);
        return result;
    }

    @Override
    public long getTerm(long index) {
        LogEntry entity = LogMapper.getLogEntryByIndex(index);
        long result = entity.getTerm();
        return result;
    }

    @Override
    public boolean appendEntry(LogEntry entry) {
        try {
            LogMapper.insertLogEntity(entry);
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
                LogMapper.insertLogEntity(entry);
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
            LogMapper.deleteLogEntityLess(firstIndexKept);
            session.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean truncateSuffix(long lastIndexKept) {
        try {
            LogMapper.deleteLogEntityGreater(lastIndexKept);
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
