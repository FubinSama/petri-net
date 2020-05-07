package com.wfb.place;

import com.wfb.adapter.PlaceNodeAdapter;
import com.wfb.base.TransitionNode;
import com.wfb.flow.NetTraversal;

import java.util.ArrayList;
import java.util.List;

public class Notify2PlaceNode extends PlaceNodeAdapter {

    private TransitionNode upTransitionNode;
    private List<TransitionNode> downTransitionNodes = new ArrayList<>();

    public Notify2PlaceNode(int threadNumber, int tokenCnt, String name) {
        super(threadNumber, tokenCnt, name);
    }

    @Override
    public void addUpTransitionNode(TransitionNode node) {
        upTransitionNode = node;
    }

    @Override
    public void addDownTransitionNode(TransitionNode node) {
        downTransitionNodes.add(node);
    }

    @Override
    public void traversal(NetTraversal netTraversal) {
        for (TransitionNode transitionNode: downTransitionNodes) {
            if (netTraversal.isTraversalTransitionNode(this, transitionNode)) {
                netTraversal.printPTFlow(this, transitionNode);
                transitionNode.traversal(netTraversal);
            }
        }
    }
}
