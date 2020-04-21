package com.wfb.transition;

import com.wfb.adapter.TransitionNodeAdapter;
import com.wfb.base.PlaceNode;
import com.wfb.flow.NetTraversal;

import java.util.ArrayList;
import java.util.List;

public class Notify2TransitionNode extends TransitionNodeAdapter {
    private PlaceNode upPlaceNode;
    private PlaceNode downPlaceNode;

    public Notify2TransitionNode(int iid, int threadNumber, String name, String description) {
        super(iid, threadNumber, name, description);
    }

    @Override
    public void addUpPlaceNode(PlaceNode node) {
        upPlaceNode = node;
    }

    @Override
    public void addDownPlaceNode(PlaceNode node) {
        downPlaceNode = node;
    }

    @Override
    public void changeDownPlaceNode(PlaceNode oldPlaceNode, PlaceNode newPlaceNode) {
        if (downPlaceNode == oldPlaceNode) downPlaceNode = newPlaceNode;
    }

    @Override
    public void traversal(NetTraversal netTraversal) {
        if (downPlaceNode == null) return;
        if (!netTraversal.isTraversalPlaceNode(this, downPlaceNode)) return;
        netTraversal.printTPFlow(this, downPlaceNode);
        downPlaceNode.traversal(netTraversal);
    }

    @Override
    public List<PlaceNode> getUpPlaceNode() {
        List<PlaceNode> rs = new ArrayList<>();
        rs.add(this.upPlaceNode);
        return rs;
    }
}
