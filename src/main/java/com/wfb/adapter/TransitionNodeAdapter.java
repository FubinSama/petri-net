package com.wfb.adapter;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import com.wfb.flow.NetTraversal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TransitionNodeAdapter implements TransitionNode {
    private int iid;
    private int threadNumber;
    private String name;
    private String description;

    @Override
    public void addUpPlaceNode(PlaceNode node) {

    }

    @Override
    public void addDownPlaceNode(PlaceNode node) {

    }

    @Override
    public void traversal(NetTraversal netTraversal) {

    }
}
