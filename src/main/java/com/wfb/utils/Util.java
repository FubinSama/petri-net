package com.wfb.utils;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import com.wfb.place.LockPlaceNode;
import com.wfb.place.NotifyAllPlaceNode;
import com.wfb.place.SourcePlaceNode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static List<String> iidToLineMap;

    public static String getIidToLine(int iid) {
        if (iidToLineMap == null) return null;
        return iidToLineMap.get(iid).replaceAll(".html#", "#");
    }

    public static void setIidToLineMap(String iidToLineMapFile) {
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(iidToLineMapFile)))) {
            iidToLineMap = (ArrayList<String>) in.readObject();
        }  catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getDescription(TransitionNode transitionNode) {
        return "thread: " + transitionNode.getThreadNumber() + ", position: " + getIidToLine(transitionNode.getIid());
    }

    public static String getDescription(PlaceNode placeNode) {
        if (placeNode instanceof LockPlaceNode)
            return "this is a lock place node";
        else if (placeNode instanceof SourcePlaceNode)
            return "this is a source place node, it belongs thread:" + placeNode.getThreadNumber();
        else if (placeNode instanceof NotifyAllPlaceNode)
            return "this is a notifyAll place node, it belongs thread:" + placeNode.getThreadNumber();
        else {
            return "this is a selection place node, it belongs thread:" + placeNode.getThreadNumber();
        }
    }

    public static void main(String[] args) {
        Util.setIidToLineMap("/home/wfb/毕设/calfuzzer/src/benchmarks/iidToLine.map");
        iidToLineMap.forEach(item -> {
            System.out.println(item);
        });
    }
}
