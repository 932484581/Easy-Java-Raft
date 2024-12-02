package cn.wjc.tool.database.mapper;

import org.apache.ibatis.annotations.Param;

import cn.wjc.tool.entity.KVEntity;

public interface KVMapper {
    KVEntity getKVEntityByKey(@Param("key") String key, @Param("name") String name);

    void insertKVEntity(@Param("data") KVEntity kvEntity, @Param("name") String name);

    void deleteKVEntityByKey(@Param("key") String key, @Param("name") String name);

    void updataKVEntityByKey(@Param("data") KVEntity kvEntity, @Param("name") String name);
}
