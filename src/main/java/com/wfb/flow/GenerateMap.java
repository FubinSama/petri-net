package com.wfb.flow;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import com.wfb.transition.WriteTransitionNode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class GenerateMap implements NetTraversal{

    @NonNull private Map<String, PlaceNode> allPlaceNodes;
    @NonNull private Map<String, TransitionNode> allTransitionNodes;
    @NonNull private Map<Long, List<TransitionNode>> varTransitionNodes;

    private Set<String> PTSet = new HashSet<>();
    private Set<String> TPSet = new HashSet<>();
    private Set<String> placeSet = new HashSet<>();
    private Set<String> transitionSet = new HashSet<>();


    @Override
    public void printPTFlow(PlaceNode placeNode, TransitionNode transitionNode) {
        generateNode(transitionNode, placeNode);
    }

    @Override
    public void printTPFlow(TransitionNode transitionNode, PlaceNode placeNode) {
        generateNode(transitionNode, placeNode);
    }

    private void generateNode(TransitionNode transitionNode, PlaceNode placeNode) {
        if (!placeSet.contains(placeNode.getName())) {
            placeSet.add(placeNode.getName());
            generatePlace(placeNode);
        }
        if (!transitionSet.contains(transitionNode.getName())) {
            transitionSet.add(transitionNode.getName());
            generateTransition(transitionNode);
        }
    }

    private void generateTransition(TransitionNode transitionNode) {
        this.allTransitionNodes.put(transitionNode.getName(), transitionNode);
        if (transitionNode instanceof WriteTransitionNode) {
            WriteTransitionNode writeTransitionNode = (WriteTransitionNode) transitionNode;
            long memory = writeTransitionNode.getMemory();

        }
    }

    private void generateVarTransition(long memory, TransitionNode transitionNode) {
        if (this.varTransitionNodes.containsKey(memory)) {
            this.varTransitionNodes.get(memory).add(transitionNode);
        } else {
            List<TransitionNode> list = new ArrayList<>();
            list.add(transitionNode);
            this.varTransitionNodes.put(memory, list);
        }
    }

    private void generatePlace(PlaceNode placeNode) {
        this.allPlaceNodes.put(placeNode.getName(), placeNode);
    }

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
