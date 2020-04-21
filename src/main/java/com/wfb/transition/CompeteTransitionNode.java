package com.wfb.transition;

import com.wfb.adapter.TransitionNodeAdapter;
import com.wfb.base.PlaceNode;
import com.wfb.flow.NetTraversal;

import java.util.ArrayList;
import java.util.List;

public class CompeteTransitionNode extends TransitionNodeAdapter {

    private List<PlaceNode> upPlaceNodes = new ArrayList<>();
    private PlaceNode downPlaceNode;

    /**
     * @apiNote 先添加wakeAfterPlaceNode，再添加lockPlaceNode
     */
    public CompeteTransitionNode(int iid, int threadNumber, String name, String description) {
        super(iid, threadNumber, name, description);
    }

    /**
     * @param node：wakeAfterPlaceNode、lockPlaceNode
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
        if (downPlaceNode == null) return;
        if (!netTraversal.isTraversalPlaceNode(this, downPlaceNode)) return;
        netTraversal.printTPFlow(this, downPlaceNode);
        downPlaceNode.traversal(netTraversal);
    }

    @Override
    public List<PlaceNode> getUpPlaceNode() {
        return this.upPlaceNodes;
    }
}
