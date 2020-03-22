package com.wfb.flow;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;

import java.util.HashSet;
import java.util.Set;

public class ConsoleShowTransitions implements NetTraversal {
    private Set<String> PTSet = new HashSet<>();
    private Set<String> TPSet = new HashSet<>();
    Set<String> set = new HashSet<>();
    @Override
    public void placeTraversal(PlaceNode placeNode, TransitionNode transitionNode) {
        if (set.contains(transitionNode.getName())) return;
        System.out.println(transitionNode.getDescription());
        set.add(transitionNode.getName());
    }

    @Override
    public void transitionTraversal(TransitionNode transitionNode, PlaceNode placeNode) {
    }

    @Override
    public boolean sholdTraversal(PlaceNode placeNode) {
        return true;
    }

    @Override
    public boolean sholdTraversal(TransitionNode transitionNode) {
        return true;
    }

    @Override
    public boolean traversalPTFlow(PlaceNode placeNode, TransitionNode transitionNode) {
        if (PTSet.contains(placeNode.getName()+transitionNode.getName())) return false;
        else PTSet.add(placeNode.getName()+transitionNode.getName());
        return true;
    }

    @Override
    public boolean traversalTPFlow(TransitionNode transitionNode, PlaceNode placeNode) {
        if (TPSet.contains(transitionNode.getName()+placeNode.getName())) return false;
        else TPSet.add(transitionNode.getName()+placeNode.getName());
        return true;
    }
}
