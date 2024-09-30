package cn.wjc.tool.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class KVEntity implements Serializable {
    String value;
    String key;
}
