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
    private static final ThreadLocal<SqlSession> sqlSessionThreadLocal = new ThreadLocal<>();
    private String dabaseName;
    SqlSessionFactory sqlSessionFactory;

    public LogStorageImpl(String dabaseName) {
        this.dabaseName = dabaseName;
        this.sqlSessionFactory = GetDataSource.getMybatisSessionFactory();
    }

    @Override
    public long getFirstLogIndex() {
        try {
            init();
            SqlSession session = sqlSessionThreadLocal.get();
            LogMapper LogMapper = session.getMapper(LogMapper.class);
            long result = LogMapper.getMinIndex(dabaseName);
            return result;
        } finally {
            destroy();
        }
    }

    @Override
    public long getLastLogIndex() {
        try {
            init();
            SqlSession session = sqlSessionThreadLocal.get();
            LogMapper LogMapper = session.getMapper(LogMapper.class);
            try {
                long result = LogMapper.getMaxIndex(dabaseName);
                return result;
            } catch (Exception e) {
                return 0;
            }
        } finally {
            destroy();
        }

    }

    @Override
    public LogEntry getEntry(long index) {
        try {
            init();
            SqlSession session = sqlSessionThreadLocal.get();
            LogMapper LogMapper = session.getMapper(LogMapper.class);
            try {
                LogEntry result = LogMapper.getLogEntryByIndex(index, dabaseName);
                if (result == null) {
                    return LogEntry.builder().index(0L).term(0L).build();
                }
                return result;
            } catch (Exception e) {
                System.err.println(index + "  " + dabaseName);
                System.err.println(e);
                log.error(index + "  " + dabaseName + "  " + e);
                return null;
            }
        } finally {
            destroy();
        }
    }

    @Override
    public long getTerm(long index) {
        try {
            init();
            SqlSession session = sqlSessionThreadLocal.get();
            LogMapper LogMapper = session.getMapper(LogMapper.class);
            LogEntry entity = LogMapper.getLogEntryByIndex(index, dabaseName);
            long result = entity.getTerm();
            return result;
        } finally {
            destroy();
        }
    }

    @Override
    public boolean appendEntry(LogEntry entry) {
        try {
            init();
            SqlSession session = sqlSessionThreadLocal.get();
            LogMapper LogMapper = session.getMapper(LogMapper.class);
            try {
                LogMapper.insertLogEntity(entry, dabaseName);
                session.commit();
                return true;
            } catch (Exception e) {
                return false;
            }
        } finally {
            destroy();
        }
    }

    @Override
    public int appendEntries(List<LogEntry> entries) {
        try {
            init();
            SqlSession session = sqlSessionThreadLocal.get();
            LogMapper LogMapper = session.getMapper(LogMapper.class);

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
        } finally {
            destroy();
        }
    }

    @Override
    public boolean truncatePrefix(long firstIndexKept) {
        try {
            init();
            SqlSession session = sqlSessionThreadLocal.get();
            LogMapper LogMapper = session.getMapper(LogMapper.class);
            try {
                LogMapper.deleteLogEntityLess(firstIndexKept, dabaseName);
                session.commit();
                return true;
            } catch (Exception e) {
                return false;
            }
        } finally {
            destroy();
        }

    }

    @Override
    public boolean truncateSuffix(long lastIndexKept) {
        try {
            init();
            SqlSession session = sqlSessionThreadLocal.get();
            LogMapper LogMapper = session.getMapper(LogMapper.class);
            try {
                LogMapper.deleteLogEntityGreater(lastIndexKept, dabaseName);
                session.commit();
                return true;
            } catch (Exception e) {
                return false;
            }
        } finally {
            destroy();
        }

    }

    @Override
    public void destroy() {
        SqlSession session = sqlSessionThreadLocal.get();
        session.close();
        sqlSessionThreadLocal.remove();
    }

    @Override
    public void init() {
        SqlSession session = sqlSessionThreadLocal.get();
        if (session == null) {
            session = sqlSessionFactory.openSession();
            sqlSessionThreadLocal.set(session);
        }
    }
}
