package com.wfb.flow;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;

public class FlowUtil {
    public static void createPTFlow(PlaceNode placeNode, TransitionNode transitionNode) {
        placeNode.addDownTransitionNode(transitionNode);
        transitionNode.addUpPlaceNode(placeNode);
    }

    public static void createTPFlow(TransitionNode transitionNode, PlaceNode placeNode) {
        transitionNode.addDownPlaceNode(placeNode);
        placeNode.addUpTransitionNode(transitionNode);
    }

    public static void showPTFlow(PlaceNode placeNode, TransitionNode transitionNode) {
        System.out.println(placeNode.getName() + "->" + transitionNode.getName());
    }

    public static void showTPFlow(TransitionNode transitionNode, PlaceNode placeNode) {
        System.out.println(transitionNode.getName() + "->" + placeNode.getName());
    }
}
