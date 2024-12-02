package cn.wjc.tool.entity;

import java.util.Objects;

import lombok.Data;

/**
 * @description: 自身节点定义
 * @return {*}
 * @author: WJC
 */

@Data
public class Peer {
    // 节点地址 节点格式为"host:port"
    private String addr;

    public Peer(String addr) {
        this.addr = addr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Peer peer = (Peer) o;
        return Objects.equals(addr, peer.addr);
    }

}
