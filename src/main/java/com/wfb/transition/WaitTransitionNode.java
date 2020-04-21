package com.wfb.transition;

import com.wfb.adapter.TransitionNodeAdapter;
import com.wfb.base.PlaceNode;
import com.wfb.flow.NetTraversal;
import com.wfb.place.SourcePlaceNode;

import java.util.ArrayList;
import java.util.List;

public class WaitTransitionNode extends TransitionNodeAdapter {
    private PlaceNode upPlaceNode;
    private List<PlaceNode> downPlaceNodes = new ArrayList<>();

    /**
     * @apiNote 先添加waitAfterPlaceNode，再添加lockPlaceNode
     */
    public WaitTransitionNode(int iid, int threadNumber, String name, String description) {
        super(iid, threadNumber, name, description);
    }

    @Override
    public void addUpPlaceNode(PlaceNode node) {
        upPlaceNode = node;
    }

    /**
     * @param node : waitAfterPlaceNode、lockPlaceNode
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

    public PlaceNode getWakeBeforePlaceNode() {
        WakeTransitionNode wakeTransitionNode = (WakeTransitionNode)((SourcePlaceNode)downPlaceNodes.get(0)).getDownTransitionNode();
        return wakeTransitionNode.getWakeBeforePlaceNode();
    }

    @Override
    public List<PlaceNode> getUpPlaceNode() {
        List<PlaceNode> rs = new ArrayList<>();
        rs.add(this.upPlaceNode);
        return rs;
    }
}
