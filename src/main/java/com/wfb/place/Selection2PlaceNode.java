package com.wfb.place;

import com.wfb.adapter.PlaceNodeAdapter;
import com.wfb.base.TransitionNode;
import com.wfb.flow.NetTraversal;

import java.util.ArrayList;
import java.util.List;

public class Selection2PlaceNode extends PlaceNodeAdapter {

    private List<TransitionNode> upTransitionNodes = new ArrayList<>();
    private TransitionNode downTransitionNode;

    /**
     * @apiNote: 先添加skipTransitionNode、再添加competeTransitionNode
     */
    public Selection2PlaceNode(int threadNumber, int tokenCnt, String name) {
        super(threadNumber, tokenCnt, name);
    }

    /**
     * @param node：skipTransitionNode、competeTransitionNode
     */
    @Override
    public void addUpTransitionNode(TransitionNode node) {
        upTransitionNodes.add(node);
    }

    @Override
    public void addDownTransitionNode(TransitionNode node) {
        downTransitionNode = node;
    }

    @Override
    public void traversal(NetTraversal netTraversal) {
        if (downTransitionNode == null) return;
        netTraversal.printPTFlow(this, downTransitionNode);
        if (netTraversal.isTraversalTransitionNode(this, downTransitionNode))
            downTransitionNode.traversal(netTraversal);
    }
}
