package com.wfb.base;

import com.wfb.flow.NetTraversal;

import java.io.Serializable;
import java.util.List;

public interface TransitionNode extends Serializable {
    int getThreadNumber();
    int getIid();
    String getName();
    String getDescription();
    int getLineNumber();
    void addUpPlaceNode(PlaceNode node);
    void addDownPlaceNode(PlaceNode node);
    void changeDownPlaceNode(PlaceNode oldPlaceNode, PlaceNode newPlaceNode);
    void traversal(NetTraversal netTraversal);
    List<PlaceNode> getUpPlaceNode();
}
