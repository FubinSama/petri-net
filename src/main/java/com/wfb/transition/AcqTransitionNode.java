package com.wfb.transition;

import com.wfb.adapter.TransitionNodeAdapter;
import com.wfb.base.PlaceNode;
import com.wfb.flow.NetTraversal;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class AcqTransitionNode extends TransitionNodeAdapter {
    private List<PlaceNode> upPlaceNodes = new ArrayList<>();
    private PlaceNode downPlaceNode;

    public AcqTransitionNode(int iid, int threadNumber, String name, String description) {
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
    public void traversal(NetTraversal netTraversal) {
        if (!netTraversal.traversalTPFlow(this, downPlaceNode)) return;
        netTraversal.transitionTraversal(this, downPlaceNode);
        if (netTraversal.sholdTraversal(downPlaceNode))
            downPlaceNode.traversal(netTraversal);
    }
}
