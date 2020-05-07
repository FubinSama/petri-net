package com.wfb.adapter;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import com.wfb.flow.NetTraversal;
import com.wfb.utils.Util;
import lombok.Getter;

import java.util.List;

@Getter
public class TransitionNodeAdapter implements TransitionNode {
    private int iid;
    private int threadNumber;
    private String name;
    private String description;
    private int lineNumber;

    public TransitionNodeAdapter(int iid, int threadNumber, String name, String description) {
        this.iid = iid;
        this.threadNumber = threadNumber;
        this.name = name;
        this.description = description;
        String util = Util.getIidToLine(iid);
        int index = util.indexOf("#");
        if (index < 0) this.lineNumber = 0;
        else this.lineNumber = Integer.valueOf(util.substring(index + 1));
    }

    @Override
    public void addUpPlaceNode(PlaceNode node) {

    }

    @Override
    public void addDownPlaceNode(PlaceNode node) {

    }

    @Override
    public void changeDownPlaceNode(PlaceNode oldPlaceNode, PlaceNode newPlaceNode) {

    }

    @Override
    public void traversal(NetTraversal netTraversal) {

    }

    @Override
    public List<PlaceNode> getUpPlaceNode() {
        return null;
    }
}
