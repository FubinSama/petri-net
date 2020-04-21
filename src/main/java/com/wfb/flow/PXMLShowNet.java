package com.wfb.flow;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class PXMLShowNet implements NetTraversal {
    @NonNull private PrintStream ps;

    private Set<String> PTSet = new HashSet<>();
    private Set<String> TPSet = new HashSet<>();
    private Set<String> placeSet = new HashSet<>();
    private Set<String> transitionSet = new HashSet<>();

    @Override
    public void printPTFlow(PlaceNode placeNode, TransitionNode transitionNode) {
        printNode(transitionNode, placeNode);
        //TODO print arc
//        System.out.println(placeNode.getName() + "->" + transitionNode.getName());
        printArc(placeNode.getName(), transitionNode.getName());
    }

    @Override
    public void printTPFlow(TransitionNode transitionNode, PlaceNode placeNode) {
        printNode(transitionNode, placeNode);
        //TODO print arc
//        System.out.println(transitionNode.getName() + "->" + placeNode.getName());
        printArc(transitionNode.getName(), placeNode.getName());
    }

    private void printNode(TransitionNode transitionNode, PlaceNode placeNode) {
        if (!placeSet.contains(placeNode.getName())) {
            placeSet.add(placeNode.getName());
            printPlace(placeNode);
        }
        if (!transitionSet.contains(transitionNode.getName())) {
            transitionSet.add(transitionNode.getName());
            printTransition(transitionNode);
        }
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

    public void printHeader() {
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        ps.println("<pnml>");
        ps.println("    <net>");
        ps.println("        <token id=\"Default\" red=\"0\" green=\"0\" blue=\"0\"/>");
    }

    public void printFooter() {
        ps.println("</net>");
        ps.println("</pnml>");
    }

    private void printPlace(PlaceNode placeNode) {
        ps.println("        <place id=\"" + placeNode.getName() + "\">");
        printPosition();
        printName(placeNode.getName());
        printCapacity(placeNode.getCapacity());
        printInitialMarking(placeNode.getTokenCnt());
        ps.println("</place>");
    }

    private void printTransition(TransitionNode transitionNode) {
        ps.println("<transition id=\"" + transitionNode.getName() + "\">");
        printPosition();
        printName(transitionNode.getDescription());
        printInfiniteServer(false);
        printTimed(false);
        printPriority(1);
        printOrientation(0);
        printRate(1.0);
        ps.println("</transition>");
    }

    static class YGenerator{
        private static int y = 0;

        public static int getY() {
            return y += 30;
        }
    }

    private void printPosition() {
        ps.println("<graphics>");
        ps.println("<position x=\"100\" y=\"" + YGenerator.getY() + "\" />");
        ps.println("</graphics>");
    }

    private void printName(String value) {
        ps.println("<name>");
        ps.println("<value>" + value + "</value>");
        ps.println("<graphics>");
        ps.println("<offset x=\"0.0\" y=\"0.0\" />");
        ps.println("</graphics>");
        ps.println("</name>");
    }

    private void printInitialMarking(int value) {
        ps.println("<initialMarking>");
        ps.println("<value>Default," + value + "</value>");
        ps.println("<graphics>");
        ps.println("<offset x=\"0.0\" y=\"0.0\" />");
        ps.println("</graphics>");
        ps.println("</initialMarking>");
    }

    private void printCapacity(int value) {
        ps.println("<capacity>");
        ps.println("<value>" + value + "</value>");
        ps.println("</capacity>");
    }

    private void printOrientation(int value) {
        ps.println("<orientation>");
        ps.println("<value>" + value + "</value>");
        ps.println("</orientation>");
    }

    private void printRate(double value) {
        ps.println("<rate>");
        ps.println("<value>" + value + "</value>");
        ps.println("</rate>");
    }

    private void printTimed(boolean isTimed) {
        ps.println("<timed>");
        ps.println("<value>" + isTimed + "</value>");
        ps.println("</timed>");
    }

    private void printInfiniteServer(boolean isInfiniteServer) {
        ps.println("<infiniteServer>");
        ps.println("<value>" + isInfiniteServer + "</value>");
        ps.println("</infiniteServer>");
    }

    private void printPriority(int value) {
        ps.println("<priority>");
        ps.println("<value>" + value + "</value>");
        ps.println("</priority>");
    }

    private void printArc(String source, String target) {
        ps.println("<arc id=\"" + source + " to " + target +
                "\" source=\"" + source + "\" target=\"" + target + "\">");
        printArcPath("000");
        printArcPath("001");
        ps.println("<type value=\"normal\" />");
        printInscription(1);
        ps.println("</arc>");
    }

    private void printInscription(int value) {
        ps.println("<inscription>");
        ps.println("<value>Default," + value + "</value>");
        ps.println("<graphics />");
        ps.println("</inscription>");
    }

    private void printTagged(boolean isTagged) {
        ps.println("<tagged>");
        ps.println("<value>" + isTagged + "</value>");
        ps.println("</tagged>");
    }

    private void printArcPath(String id) {
        ps.println("<arcpath id=\"" + id + "\" x=\"0\" y=\"0\" curvePoint=\"false\" />");
    }

    public static void main(String[] args) {

    }
}
