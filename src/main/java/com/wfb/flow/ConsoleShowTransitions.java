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
    public void printPTFlow(PlaceNode placeNode, TransitionNode transitionNode) {
        if (set.contains(transitionNode.getName())) return;
        System.out.println(transitionNode.getDescription());
        set.add(transitionNode.getName());
    }

    @Override
    public void printTPFlow(TransitionNode transitionNode, PlaceNode placeNode) { }

    @Override
    public boolean isTraversalPlaceNode(TransitionNode transitionNode, PlaceNode placeNode) {
        if (TPSet.contains(transitionNode.getName()+placeNode.getName())) return false;
        else TPSet.add(transitionNode.getName()+placeNode.getName());
        return true;
    }

    @Override
    public boolean isTraversalTransitionNode(PlaceNode placeNode, TransitionNode transitionNode) {
        if (PTSet.contains(placeNode.getName()+transitionNode.getName())) return false;
        else PTSet.add(placeNode.getName()+transitionNode.getName());
        return true;
    }
}
