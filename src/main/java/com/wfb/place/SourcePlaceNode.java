package com.wfb.place;

import com.wfb.adapter.PlaceNodeAdapter;
import com.wfb.base.TransitionNode;
import com.wfb.flow.NetTraversal;

public class SourcePlaceNode extends PlaceNodeAdapter {
    private TransitionNode upTransitionNode;
    private TransitionNode downTransitionNode;

    public SourcePlaceNode(int threadNumber, int tokenCnt, String name) {
        super(threadNumber, tokenCnt, name);
    }

    @Override
    public void addDownTransitionNode(TransitionNode node) {
        downTransitionNode = node;
    }

    @Override
    public void addUpTransitionNode(TransitionNode node) {
        upTransitionNode = node;
    }

    @Override
    public void traversal(NetTraversal netTraversal) {
        if (downTransitionNode == null) return;
        if (!netTraversal.traversalPTFlow(this, downTransitionNode)) return;
        netTraversal.placeTraversal(this, downTransitionNode);
        if (netTraversal.sholdTraversal(downTransitionNode))
            downTransitionNode.traversal(netTraversal);
    }
}
