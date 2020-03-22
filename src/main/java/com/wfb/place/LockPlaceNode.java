package com.wfb.place;

import com.wfb.adapter.PlaceNodeAdapter;
import com.wfb.base.TransitionNode;
import com.wfb.flow.NetTraversal;

import java.util.ArrayList;
import java.util.List;

public class LockPlaceNode extends PlaceNodeAdapter {
    private List<TransitionNode> upTransitionNodes = new ArrayList<>();
    private List<TransitionNode> downTransitionNodes = new ArrayList<>();

    public LockPlaceNode(int threadNumber, int tokenCnt, String name) {
        super(threadNumber, tokenCnt, name);
    }

    @Override
    public void addDownTransitionNode(TransitionNode node) {
        downTransitionNodes.add(node);
    }

    @Override
    public void addUpTransitionNode(TransitionNode node) {
        upTransitionNodes.add(node);
    }

    @Override
    public void traversal(NetTraversal netTraversal) {
        for (TransitionNode transitionNode: downTransitionNodes) {
            if (!netTraversal.traversalPTFlow(this, transitionNode)) continue;
            netTraversal.placeTraversal(this, transitionNode);
            if (netTraversal.sholdTraversal(transitionNode))
                transitionNode.traversal(netTraversal);
        }
    }
}
