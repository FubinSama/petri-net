package com.wfb.flow;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;

/**
 * 该接口定义了遍历函数走到某个变迁（或库所）后，
 * 遍历它的后继库所（或变迁）时，是否输出该条弧？是否要遍历后继库所（或变迁）？
 */
public interface NetTraversal {
    /**
     * 当前处在placeNode，遍历了它的transitionNode后继，如何输出当前的PT弧
     * @param placeNode 当前所处的位置
     * @param transitionNode 它的某个后继变迁
     * @return
     */
    void printPTFlow(PlaceNode placeNode, TransitionNode transitionNode);

    /**
     * 当前处在transitionNode，遍历了它的placeNode后继，如何输出当前的TP弧
     * @param transitionNode 当前所处的位置
     * @param placeNode 它的某个后继库所
     * @return
     */
    void printTPFlow(TransitionNode transitionNode, PlaceNode placeNode);

    /**
     * 当前处在transitionNode，它有一个placeNode后继，是否要遍历它
     * @param transitionNode 当前的位置
     * @param placeNode 确定是否要遍历的库所
     * @return
     */
    boolean isTraversalPlaceNode(TransitionNode transitionNode, PlaceNode placeNode);

    /**
     * 当前处在placeNode，它有一个transitionNode后继，是否要遍历它
     * @param placeNode 当前的位置
     * @param transitionNode 确定是否要遍历的变迁
     * @return
     */
    boolean isTraversalTransitionNode(PlaceNode placeNode, TransitionNode transitionNode);
}
