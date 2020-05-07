package com.wfb.flow;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import com.wfb.place.Selection2PlaceNode;
import com.wfb.utils.Util;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class HtmlShowNet implements NetTraversal {
    @NonNull private PrintStream ps;

    private Set<String> PTSet = new HashSet<>();
    private Set<String> TPSet = new HashSet<>();
    private Set<String> placeSet = new HashSet<>();
    private Set<String> transitionSet = new HashSet<>();

    @Override
    public void printPTFlow(PlaceNode placeNode, TransitionNode transitionNode) {
        printNode(transitionNode, placeNode);
        setEdge(placeNode.getName(), transitionNode.getName());
    }

    @Override
    public void printTPFlow(TransitionNode transitionNode, PlaceNode placeNode) {
        printNode(transitionNode, placeNode);
        setEdge(transitionNode.getName(), placeNode.getName());
    }

    private void printNode(TransitionNode transitionNode, PlaceNode placeNode) {
        if (!placeSet.contains(placeNode.getName())) {
            placeSet.add(placeNode.getName());
            setNode(placeNode.getName(), placeNode.getName(), "circle",
                    Util.getDescription(placeNode), "T" + placeNode.getThreadNumber());
        }
        if (!transitionSet.contains(transitionNode.getName())) {
            transitionSet.add(transitionNode.getName());
            setNode(transitionNode.getName(), transitionNode.getDescription(), "rect",
                    Util.getDescription(transitionNode),  "T" + transitionNode.getThreadNumber());
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

    public static String[] colors = new String[]{
        "red", "pink", "blue", "yellow", "green", "orange", "brown", "purple", "grey"
    };

    public void printHeader(Set<Integer> threadSet) {
        ps.println("<!DOCTYPE html>");
        ps.println("<html lang=\"en\">");
        ps.println("<head>");
        ps.println("<meta charset=\"utf-8\">");
        ps.println("    <title>PetriNet</title>");
        ps.println("    <link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\" />");
        ps.println("    <link rel=\"stylesheet\" type=\"text/css\" href=\"css/tipsy.css\" />");
        ps.println("    <script type=\"text/javascript\" src=\"js/dagre-d3.min.js\"></script>");
        ps.println("    <script src=\"js/d3.v4.min.js\"></script>");
        ps.println("    <script src=\"js/jquery-1.9.1.min.js\"></script>");
        ps.println("    <script src=\"js/tipsy.js\"></script>");
        ps.println("<style>");
        for(Integer thread: threadSet) {
            ps.println("g." + "T" + thread + ">circle {\n" +
                    "   fill: " + colors[thread % colors.length] + "; \n" +
                    "}\n");
            ps.println("g." + "T" + thread + ">rect {\n" +
                    "   fill: " + colors[thread % colors.length] + "; \n" +
                    "}\n");
        }
        ps.println("</style>");
        ps.println("</head>");
        ps.println("<body>");
        ps.println("<svg width=1400 height=1000></svg>");
        ps.println("<script type=\"text/javascript\">");
        ps.println("var g = new dagreD3.graphlib.Graph().setGraph({});");
    }

    public void printFooter() {
        ps.println("var render = new dagreD3.render();");
        ps.println("var svg = d3.select(\"svg\"), inner = svg.append(\"g\");");
        ps.println("var zoom = d3.zoom().on(\"zoom\", function() {inner.attr(\"transform\", d3.event.transform);});");
        ps.println("svg.call(zoom);");
        ps.println("var styleTooltip = function(name, description) {" +
                "return \"<p class='name'>\" + name + \"</p><p class='description'>\" + description + \"</p>\";};");
        ps.println("render(inner, g);");
        ps.println("inner.selectAll(\"g.node\")\n" +
                "  .attr(\"title\", function(v) { return styleTooltip(v, g.node(v).description) })\n" +
                "  .each(function(v) { $(this).tipsy({ gravity: \"w\", opacity: 1, html: true }); });");
        ps.println("var initialScale = 0.75;");
        ps.println("svg.call(zoom.transform, d3.zoomIdentity" +
                ".translate((svg.attr(\"width\") - g.graph().width * initialScale) / 2, 20).scale(initialScale));");
        ps.println("</script>");
        ps.println("</body>");
        ps.println("</html>");
    }

    private void setNode(String name, String label, String shape, String description, String clazz) {
        ps.println("g.setNode('" + name + "',{label:'" + label + "', shape:'" + shape + "', description:'" + description + "', class:'" + clazz +"'});");
    }

    private void setEdge(String name1, String name2) {
//        System.out.println(name1 + "=>" + name2);
        ps.println("g.setEdge('" + name1 + "', '" + name2 + "', {});");
    }

    public static void main(String[] args) {
        HtmlShowNet htmlShowNet = new HtmlShowNet(System.out);
        htmlShowNet.setNode("t1", "label", "shape", "description", "class");
        PlaceNode placeNode = new Selection2PlaceNode(0, 0, "0");
        htmlShowNet.setNode(placeNode.getName(), placeNode.getName(), "circle",
                Util.getDescription(placeNode), placeNode.getClass().getSimpleName());
    }
}
