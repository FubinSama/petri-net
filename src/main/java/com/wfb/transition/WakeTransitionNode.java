package com.wfb.transition;

import com.wfb.adapter.TransitionNodeAdapter;
import com.wfb.base.PlaceNode;
import com.wfb.flow.NetTraversal;

import java.util.ArrayList;
import java.util.List;

public class WakeTransitionNode extends TransitionNodeAdapter {

    private List<PlaceNode> upPlaceNodes = new ArrayList<>();
    private PlaceNode downPlaceNode;

    /**
     * @apiNote 先添加wakeBeforePlaceNode，再添加waitAfterPlaceNode
     */
    public WakeTransitionNode(int iid, int threadNumber, String name, String description) {
        super(iid, threadNumber, name, description);
    }

    /**
     * @param node: wakeBeforePlaceNode、waitAfterPlaceNode
     */
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
        for (PlaceNode placeNode: upPlaceNodes) {
            if (netTraversal.isTraversalTransitionNode(placeNode, this))
                netTraversal.printPTFlow(placeNode, this);
        }
        if (downPlaceNode == null) return;
        if (!netTraversal.isTraversalPlaceNode(this, downPlaceNode)) return;
        netTraversal.printTPFlow(this, downPlaceNode);
        downPlaceNode.traversal(netTraversal);
    }

    public PlaceNode getWakeBeforePlaceNode() {
        return upPlaceNodes.get(0);
    }
}
