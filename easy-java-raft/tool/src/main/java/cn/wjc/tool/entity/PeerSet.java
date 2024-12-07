package cn.wjc.tool.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @description: 节点集合
 * @return {*}
 * @author: WJC
 */
@Data
public class PeerSet implements Serializable {
    private List<Peer> list = new ArrayList<>();

    private volatile String leader;
    private volatile Peer self;

    public void addPeer(Peer peer) {
        list.add(peer);
    }

    public void removePeer(Peer peer) {
        list.remove(peer);
    }

    public List<Peer> getPeersWithOutSelf() {
        List<Peer> list2 = new ArrayList<>(list);
        list2.remove(self);
        return list2;
    }
}
