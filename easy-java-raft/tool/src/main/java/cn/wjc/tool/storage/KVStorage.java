package cn.wjc.tool.storage;

import java.util.List;

import cn.wjc.tool.entity.KVEntity;
import cn.wjc.tool.rpc.LifeCycle;

public interface KVStorage extends LifeCycle {

    int apply(List<KVEntity> entries);

    String getString(String key);

    boolean setString(String key, String value);

    void delString(String... key);
}
