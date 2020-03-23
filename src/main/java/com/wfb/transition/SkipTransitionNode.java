package com.wfb.transition;

import com.wfb.adapter.TransitionNodeAdapter;
import com.wfb.base.PlaceNode;
import com.wfb.flow.NetTraversal;

public class SkipTransitionNode extends TransitionNodeAdapter {
    private PlaceNode upPlaceNode;
    private PlaceNode downPlaceNode;

    public SkipTransitionNode(int iid, int threadNumber, String name, String description) {
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
        netTraversal.printTPFlow(this, downPlaceNode);
        if (netTraversal.isTraversalPlaceNode(this, downPlaceNode))
            downPlaceNode.traversal(netTraversal);
    }
}