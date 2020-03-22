package com.wfb.base;

import com.wfb.flow.NetTraversal;

public interface TransitionNode {
    int getThreadNumber();
    int getIid();
    String getName();
    String getDescription();
    void addUpPlaceNode(PlaceNode node);
    void addDownPlaceNode(PlaceNode node);
    void traversal(NetTraversal netTraversal);
}
