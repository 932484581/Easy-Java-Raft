package cn.wjc.tool.storage;

import cn.wjc.tool.entity.LogEntry;

public interface KVStorage {

    void apply(LogEntry logEntry);

    String getString(String key);

    void setString(String key, String value);

    void delString(String... key);
}
