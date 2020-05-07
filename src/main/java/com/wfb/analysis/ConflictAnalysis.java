package com.wfb.analysis;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import com.wfb.net.PetriNet;
import com.wfb.utils.PetriUtil;
import pipe.reachability.algorithm.StateSpaceExplorer;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.Record;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConflictAnalysis {
    public Collection<Record> records;
    public Map<Integer, ClassifiedState> stateMappings;
    public PetriNet petriNet;

    public ConflictAnalysis(String pnmlFile, PetriNet petriNet) throws Exception {
        this(pnmlFile);
        this.petriNet = petriNet;
    }

    public ConflictAnalysis(String pnmlFile, String petriFile) throws Exception {
        this(pnmlFile);
        this.petriNet = PetriUtil.readPetriNet(petriFile);
    }

    private ConflictAnalysis(String pnmlFile) throws Exception{
        File file = new File(pnmlFile);
        StateSpaceLoader loader = new StateSpaceLoader(file);
        StateSpaceExplorer.StateSpaceExplorerResults results = loader.calculateResult(4);
        StateSpaceLoader.Results results1 = loader.loadStateSpace();
        this.records = results1.records;
        this.stateMappings = results1.stateMappings;
    }

    public boolean isConcurrency(String t1, String t2) {
        Map<String, Integer> map = getMap(new String[]{t1, t2});
        for (ClassifiedState classifiedState: stateMappings.values()) {
            //判断该状态M是否大于等于M(p)
            int cnt = 0;
            for (String p: map.keySet()) {
                int  m = classifiedState.getTokens(p).get("Default");
                if (m >= map.get(p)) ++cnt;
            }
            if (cnt == map.size()) return true;
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
