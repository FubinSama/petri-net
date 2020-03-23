package com.wfb.place;

import com.wfb.adapter.PlaceNodeAdapter;
import com.wfb.base.TransitionNode;
import com.wfb.flow.NetTraversal;

import java.util.ArrayList;
import java.util.List;

public class Selection1PlaceNode extends PlaceNodeAdapter {
    private TransitionNode upTransitionNode;
    private List<TransitionNode> downTransitionNodes = new ArrayList<>();

    /**
     * @apiNote 先添加SskipTransitionNode，再添加waitTransitionNode
     */
    public Selection1PlaceNode(int threadNumber, int tokenCnt, String name) {
        super(threadNumber, tokenCnt, name);
    }

    @Override
    public void addUpTransitionNode(TransitionNode node) {
        upTransitionNode = node;
    }

    /**
     * @param node: skipTransitionNode、waitTransitionNode
     */
    @Override
    public void addDownTransitionNode(TransitionNode node) {
        downTransitionNodes.add(node);
    }

    @Override
    public void traversal(NetTraversal netTraversal) {
        for (TransitionNode transitionNode: downTransitionNodes) {
            netTraversal.printPTFlow(this, transitionNode);
            if (netTraversal.isTraversalTransitionNode(this, transitionNode))
                transitionNode.traversal(netTraversal);
        }
    }
}
