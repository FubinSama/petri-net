package com.wfb.analysis;

public class Main {
    public static void main(String[] args) throws Exception {
        String pnmlFile = "/home/wfb/毕设/calfuzzer/html/petri5.xml";
        String petriFile = "/home/wfb/毕设/calfuzzer/html/petri5.obj";
        ConflictAnalysis conflictAnalysis = new ConflictAnalysis(pnmlFile, petriFile);
        boolean is = conflictAnalysis.isConflict("T0", "T1");
        System.out.println(is);
    }
}
