package com.wfb.analysis;

import com.wfb.net.PetriNet;
import com.wfb.utils.PetriUtil;

public class Main {
    public static void main(String[] args) throws Exception {
        String pnmlFile = "/home/wfb/毕设/calfuzzer/source/wfb_testcases_Test.xml";
        String petriFile = "/home/wfb/毕设/calfuzzer/source/wfb_testcases_Test.obj";
        PetriNet petriNet = PetriUtil.readPetriNet(petriFile);
        System.out.println(petriNet);

        ConflictAnalysis conflictAnalysis = new ConflictAnalysis(pnmlFile, petriFile);
        boolean is = conflictAnalysis.isConcurrency("T16", "T6");
        System.out.println(is);
    }
}
