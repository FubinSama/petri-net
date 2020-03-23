package com.wfb.transition;

import com.wfb.adapter.TransitionNodeAdapter;
import com.wfb.base.PlaceNode;
import com.wfb.flow.NetTraversal;

import java.util.ArrayList;
import java.util.List;


public class JoinTransitionNode extends TransitionNodeAdapter {
    private List<PlaceNode> upPlaceNodes = new ArrayList<>();
    private PlaceNode downPlaceNode;

    public JoinTransitionNode(int iid, int threadNumber, String name, String description) {
        super(iid, threadNumber, name, description);
    }

    @Override
    public void addUpPlaceNode(PlaceNode node) {
        upPlaceNodes.add(node);
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
