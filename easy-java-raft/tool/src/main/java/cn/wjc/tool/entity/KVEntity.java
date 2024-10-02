package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * @description: 存储的键值对信息
 * @return {*}
 * @author: WJC
 */
@Data
@Builder
public class KVEntity implements Serializable {
    String value;
    String key;
}
