package com.wfb.transition;

import com.wfb.adapter.TransitionNodeAdapter;
import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import com.wfb.flow.NetTraversal;

import java.util.ArrayList;
import java.util.List;

public class NotifyTransitionNode extends TransitionNodeAdapter {
    private PlaceNode upPlaceNode;
    private List<PlaceNode> downPlaceNodes = new ArrayList<>();

    public NotifyTransitionNode(int iid, int threadNumber, String name, String description) {
        super(iid, threadNumber, name, description);
    }

    @Override
    public void addUpPlaceNode(PlaceNode node) {
        upPlaceNode = node;
    }

    /**
     * @param node：notifyAfterPlaceNode、notify2BeforePlaceNode
     */
    @Override
    public void addDownPlaceNode(PlaceNode node) {
        downPlaceNodes.add(node);
    }

    @Override
    public void changeDownPlaceNode(PlaceNode oldPlaceNode, PlaceNode newPlaceNode) {
        for (PlaceNode downPlaceNode: downPlaceNodes){
            if (downPlaceNode == oldPlaceNode) downPlaceNode = newPlaceNode;
        }
    }

    @Override
    public void traversal(NetTraversal netTraversal) {
        for (PlaceNode placeNode: downPlaceNodes) {
            if (!netTraversal.isTraversalPlaceNode(this, placeNode)) continue;
            netTraversal.printTPFlow(this, placeNode);
            placeNode.traversal(netTraversal);
        }
    }

    public PlaceNode getNotify2BeforePlaceNode() {
        return downPlaceNodes.get(1);
    }
}
