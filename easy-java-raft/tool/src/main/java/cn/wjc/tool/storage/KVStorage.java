package cn.wjc.tool.storage;

import java.util.List;

import cn.wjc.tool.LifeCycle;
import cn.wjc.tool.entity.KVEntity;

public interface KVStorage extends LifeCycle {

    int apply(List<KVEntity> entries);

    String getString(String key);

    boolean setString(String key, String value);

    boolean updataString(String key, String value);

    void delString(String... key);
}
