package com.wfb.place;

import com.wfb.adapter.PlaceNodeAdapter;
import com.wfb.base.TransitionNode;
import com.wfb.flow.NetTraversal;
import lombok.Getter;

public class SourcePlaceNode extends PlaceNodeAdapter {
    @Getter private TransitionNode upTransitionNode;
    @Getter private TransitionNode downTransitionNode;

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
        if (netTraversal.isTraversalTransitionNode(this, downTransitionNode)) {
            netTraversal.printPTFlow(this, downTransitionNode);
            downTransitionNode.traversal(netTraversal);
        }
    }
}
