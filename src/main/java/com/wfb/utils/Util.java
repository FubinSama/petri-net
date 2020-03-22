package com.wfb.utils;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import com.wfb.place.LockPlaceNode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static List<String> iidToLineMap;

    public static String getIidToLine(Integer iid) {
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
        if (placeNode instanceof LockPlaceNode) return "this is a lock place";
        else return "this is a lock place, thread is: " + placeNode.getThreadNumber();
    }
}
