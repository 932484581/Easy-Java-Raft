package cn.wjc.tool.database.mapper;

import cn.wjc.tool.entity.KVEntity;

public interface KVMapper {
    KVEntity getKVEntityByKey(String key);

    void insertKVEntity(KVEntity kvEntity);

    void deleteKVEntityByKey(String key);
}
