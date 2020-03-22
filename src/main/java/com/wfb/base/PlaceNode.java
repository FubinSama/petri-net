package com.wfb.base;

import com.wfb.flow.NetTraversal;

public interface PlaceNode {
    void addDownTransitionNode(TransitionNode node);
    void addUpTransitionNode(TransitionNode node);
    boolean haveToken();
    void loseToken();
    void getToken();
    String getName();
    int getThreadNumber();
    void traversal(NetTraversal netTraversal);
}
