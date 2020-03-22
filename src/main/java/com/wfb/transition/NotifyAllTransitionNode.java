package com.wfb.transition;

import com.wfb.adapter.TransitionNodeAdapter;
import com.wfb.base.PlaceNode;
import com.wfb.flow.NetTraversal;

import java.util.ArrayList;
import java.util.List;

public class NotifyAllTransitionNode extends TransitionNodeAdapter {
    private PlaceNode upPlaceNode;
    private List<PlaceNode> downPlaceNodes = new ArrayList<>();

    public NotifyAllTransitionNode(int iid, int threadNumber, String name, String description) {
        super(iid, threadNumber, name, description);
    }

    @Override
    public void addUpPlaceNode(PlaceNode node) {
        upPlaceNode = node;
    }

    @Override
    public void addDownPlaceNode(PlaceNode node) {
        downPlaceNodes.add(node);
    }

    @Override
    public void traversal(NetTraversal netTraversal) {
        for (PlaceNode placeNode: downPlaceNodes) {
            if (!netTraversal.traversalTPFlow(this, placeNode)) continue;
            netTraversal.transitionTraversal(this, placeNode);
            if (netTraversal.sholdTraversal(placeNode))
                placeNode.traversal(netTraversal);
        }
    }
}
