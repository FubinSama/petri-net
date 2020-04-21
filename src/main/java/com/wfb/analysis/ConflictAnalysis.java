package com.wfb.analysis;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import com.wfb.net.PetriNet;
import pipe.reachability.algorithm.StateSpaceExplorer;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.Record;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConflictAnalysis {
    private Collection<Record> records;
    private Map<Integer, ClassifiedState> stateMappings;
    private PetriNet petriNet;

    public ConflictAnalysis(String pnmlFile, String petriFile) throws Exception {
        File file = new File(pnmlFile);
        StateSpaceLoader loader = new StateSpaceLoader(file);
        StateSpaceExplorer.StateSpaceExplorerResults results = loader.calculateResult(4);
//        System.out.println(results.numberOfStates + ", " + results.processedTransitions);
        StateSpaceLoader.Results results1 = loader.loadStateSpace();
        this.records = results1.records;
        this.stateMappings = results1.stateMappings;
        File file1 = new File(petriFile);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file1));
        Object o = ois.readObject();
        if (o instanceof PetriNet) this.petriNet = (PetriNet) o;
        else throw new Exception("Can't read petriNet!!!");
    }

    public boolean isConflict(String t1, String t2) {
        Map<String, Integer> map = getMap(new String[]{t1, t2});
        for (ClassifiedState classifiedState: stateMappings.values()) {
            for (String p: map.keySet()) {
                int cnt = classifiedState.getTokens(p).get("Default");
                if (cnt > map.get(p)) return true;
            }
        }
        return false;
    }

    private Map<String, Integer> getMap(String[] ts) {
        Map<String, Integer> map = new HashMap<>();
        for (String t: ts) {
            TransitionNode transitionNode = this.petriNet.allTransitionNodes.get(t);
            List<PlaceNode> list = transitionNode.getUpPlaceNode();
            for (PlaceNode placeNode: list) {
                map.merge(placeNode.getName(), 1, Integer::sum);
            }
        }
        return map;
    }
}
