package com.wfb.flow;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;

public interface NetTraversal {
    /**
     * 遍历到本库所，它指向某个变迁，格式化输出一个PT弧
     */
    void placeTraversal(PlaceNode placeNode, TransitionNode transitionNode);

    /**
     * 遍历到本变迁，它指向某个库所，格式化输出一个TP弧
     */
    void transitionTraversal(TransitionNode transitionNode, PlaceNode placeNode);

    //遍历到本变迁，它指向某个库所，已经输出了该TP弧的信息，是否继续比遍历该库所
    boolean sholdTraversal(PlaceNode placeNode);

    //遍历到本库所，它指向某个变迁，已经输出了该PT弧的信息，是否继续比遍历该变迁
    boolean sholdTraversal(TransitionNode transitionNode);

    /**
     * 遍历到本库所，它指向某个变迁，形成了一个PT弧，该PT弧是否该遍历，并输出信息
     */
    boolean traversalPTFlow(PlaceNode placeNode, TransitionNode transitionNode);

    /**
     * 遍历到本变迁，它指向某个库所，形成了一个TP弧，该TP弧是否该遍历，并输出信息
     */
    boolean traversalTPFlow(TransitionNode transitionNode, PlaceNode placeNode);
}
