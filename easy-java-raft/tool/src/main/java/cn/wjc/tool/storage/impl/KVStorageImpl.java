package cn.wjc.tool.storage.impl;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import cn.wjc.tool.database.mapper.KVMapper;
import cn.wjc.tool.entity.KVEntity;
import cn.wjc.tool.storage.KVStorage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KVStorageImpl implements KVStorage {

    private SqlSession session = null;
    private KVMapper kvMapper = null;
    private String dabaseName;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public KVStorageImpl(String dabaseName) {
        this.dabaseName = dabaseName;
    }

    @Override
    public void destroy() throws Throwable {
        session.close();
    }

    @Override
    public void init() throws Throwable {
        SqlSessionFactory sqlSessionFactory = GetDataSource.getMybatisSessionFactory();
        session = sqlSessionFactory.openSession();
        kvMapper = session.getMapper(KVMapper.class);
    }

    @Override
    public int apply(List<KVEntity> entries) {
        Iterator<KVEntity> iterator = entries.iterator();
        int result = 0;
        while (iterator.hasNext()) {
            try {
                KVEntity entry = iterator.next();
                kvMapper.insertKVEntity(entry, dabaseName);
                result++;
            } catch (Exception e) {
                log.error(e.toString());
                break;
            }
        }
        session.commit();
        return result;
    }

    @Override
    public String getString(String key) {
        return kvMapper.getKVEntityByKey(key, dabaseName).getValue();
    }

    @Override
    public boolean setString(String key, String value) {
        try {
            kvMapper.insertKVEntity(KVEntity.builder().key(key).value(value).build(), dabaseName);
            session.commit();
            return true;
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
    }

    @Override
    public void delString(String... key) {
        try {
            for (String k : key) {
                kvMapper.deleteKVEntityByKey(k, dabaseName);
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        session.commit();
    }

}
