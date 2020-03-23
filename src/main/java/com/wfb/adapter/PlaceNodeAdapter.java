package com.wfb.adapter;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import com.wfb.flow.NetTraversal;
import lombok.*;

@AllArgsConstructor
public class PlaceNodeAdapter implements PlaceNode {
    @Getter private int threadNumber;
    @Getter private int tokenCnt;
    @Getter private String name;

    @Override
    public void addDownTransitionNode(TransitionNode node) {

    }

    @Override
    public void addUpTransitionNode(TransitionNode node) {

    }

    @Override
    public boolean haveToken() {
        return tokenCnt > 0;
    }

    @Override
    public void subToken() {
        tokenCnt--;
    }

    @Override
    public void incToken() {
        tokenCnt++;
    }

    @Override
    public void traversal(NetTraversal netTraversal) {

    }
}
