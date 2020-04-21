package com.wfb.base;

import com.wfb.flow.NetTraversal;

import java.io.Serializable;

public interface PlaceNode extends Serializable {
    void addDownTransitionNode(TransitionNode node);
    void addUpTransitionNode(TransitionNode node);
    boolean haveToken();
    void incToken();
    void subToken();
    int getTokenCnt();
    int getCapacity();
    String getName();
    int getThreadNumber();
    void traversal(NetTraversal netTraversal);
}
