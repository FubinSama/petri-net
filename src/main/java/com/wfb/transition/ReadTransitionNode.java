package com.wfb.transition;

import com.wfb.adapter.TransitionNodeAdapter;
import com.wfb.base.PlaceNode;
import com.wfb.flow.NetTraversal;

public class ReadTransitionNode extends TransitionNodeAdapter {
    private PlaceNode upPlaceNode;
    private PlaceNode downPlaceNode;
    private long memory;

    public ReadTransitionNode(int iid, int threadNumber, String name, long memory, String description) {
        super(iid, threadNumber, name, description);
        this.memory = memory;
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
    public void traversal(NetTraversal netTraversal) {
        if (!netTraversal.traversalTPFlow(this, downPlaceNode)) return;
        netTraversal.transitionTraversal(this, downPlaceNode);
        if (netTraversal.sholdTraversal(downPlaceNode))
            downPlaceNode.traversal(netTraversal);
    }
}
