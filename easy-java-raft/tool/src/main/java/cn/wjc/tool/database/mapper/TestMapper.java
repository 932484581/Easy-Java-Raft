package cn.wjc.tool.database.mapper;

import cn.wjc.tool.entity.KVEntity;

public interface TestMapper {
    // @Select("SELECT * FROM kv_store WHERE key = #{key}")
    KVEntity selectTest(String key);

    void insertTest(KVEntity data);
}
