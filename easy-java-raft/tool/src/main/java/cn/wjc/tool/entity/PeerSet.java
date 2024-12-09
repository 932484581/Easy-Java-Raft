package cn.wjc.tool.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Data;

/**
 * @description: 节点集合
 * @return {*}
 * @author: WJC
 */
@Data
public class PeerSet implements Serializable {
    private List<Peer> list = new CopyOnWriteArrayList<>();

    private List<Peer> cOldNewList = new CopyOnWriteArrayList<>();

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
        for (Peer peer : cOldNewList) {
            list2.add(peer);
        }
        list2.remove(self);
        return list2;
    }

}
