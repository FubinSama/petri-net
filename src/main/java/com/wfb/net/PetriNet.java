package com.wfb.net;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import com.wfb.flow.*;
import com.wfb.place.LockPlaceNode;
import com.wfb.place.SourcePlaceNode;
import com.wfb.transition.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class PetriNet {
    private PlaceNode root;
    private NetUtil netUtil = new NetUtil();
    private Map<Integer, PlaceNode> curThreadPlaceNode = new HashMap<>();
    private ConcurrentHashMap<Integer, LockPlaceNode> lockPlaceNodeMap = new ConcurrentHashMap<>();
    // 某个锁资源的等待队列key:锁编号，value等待队列
    private ConcurrentHashMap<Integer, CopyOnWriteArraySet<TransitionNode>> lockWaitSet = new ConcurrentHashMap<>();
    private TransitionNode latestNotifyNode;

    public void consoleShowNet() {
        NetTraversal consoleShowNet = new ConsoleShowNet();
        root.traversal(consoleShowNet);
    }

    public void consoleShowTransitions() {
        NetTraversal consoleShowTransitions = new ConsoleShowTransitions();
        root.traversal(consoleShowTransitions);
    }

    public void htmlShowNet(String path) {
        try (PrintStream ps = new PrintStream(new FileOutputStream(path))) {
            HtmlShowNet htmlShowNet = new HtmlShowNet(ps);
            htmlShowNet.printHeader();
            root.traversal(htmlShowNet);
            htmlShowNet.printFooter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createRootNode(int threadNumber) {
        this.root = new SourcePlaceNode(threadNumber,1, netUtil.generateSourcePlaceName(threadNumber));
        curThreadPlaceNode.put(threadNumber, root);
    }

    private void addTransitionNode(TransitionNode transitionNode, int threadNumber) {
        FlowUtil.createPTFlow(curThreadPlaceNode.get(threadNumber), transitionNode);
        PlaceNode placeNode = new SourcePlaceNode(threadNumber, 0, netUtil.generateSourcePlaceName(threadNumber));
        FlowUtil.createTPFlow(transitionNode, placeNode);
        curThreadPlaceNode.put(threadNumber, placeNode);
    }

    public void addStartTransitionNode(int iid, int threadNumber, int childThreadNumber) {
        StartTransitionNode transitionNode = new StartTransitionNode(iid, threadNumber, netUtil.gengrateTransitionName(iid, threadNumber),
                "start(" + threadNumber + "," + childThreadNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
        PlaceNode placeNode = new SourcePlaceNode(childThreadNumber, 0, netUtil.generateSourcePlaceName(childThreadNumber));
        FlowUtil.createTPFlow(transitionNode, placeNode);
        curThreadPlaceNode.put(childThreadNumber, placeNode);
    }

    public void addAcqTransitionNode(int iid, int threadNumber, int lockNumber) {
        TransitionNode transitionNode = new AcqTransitionNode(iid, threadNumber, netUtil.gengrateTransitionName(iid, threadNumber),
                "acq(" + threadNumber + "," + lockNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
        LockPlaceNode lockPlaceNode;
        if (lockPlaceNodeMap.containsKey(lockNumber)) lockPlaceNode = lockPlaceNodeMap.get(lockNumber);
        else {
            lockPlaceNode = new LockPlaceNode(threadNumber, 1, netUtil.generateLockPlaceName(threadNumber, lockNumber));
            lockPlaceNodeMap.put(lockNumber, lockPlaceNode);
        }
        FlowUtil.createPTFlow(lockPlaceNode, transitionNode);
    }

    public void addRelTransitionNode(int iid, int threadNumber, int lockNumber) {
        TransitionNode transitionNode = new RelTransitionNode(iid, threadNumber, netUtil.gengrateTransitionName(iid, threadNumber),
                "rel(" + threadNumber + "," + lockNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
        LockPlaceNode lockPlaceNode;
        if (lockPlaceNodeMap.containsKey(lockNumber)) lockPlaceNode = lockPlaceNodeMap.get(lockNumber);
        else {
            lockPlaceNode = new LockPlaceNode(threadNumber, 1, netUtil.generateLockPlaceName(threadNumber, lockNumber));
            lockPlaceNodeMap.put(lockNumber, lockPlaceNode);
        }
        FlowUtil.createTPFlow(transitionNode, lockPlaceNode);
    }

    public void addJoinTransitionNode(int iid, int threadNumber, int threadNumber2) {
        TransitionNode transitionNode = new JoinTransitionNode(iid, threadNumber, netUtil.gengrateTransitionName(iid, threadNumber),
                "join(" + threadNumber + "," + threadNumber2 + ")");
        addTransitionNode(transitionNode, threadNumber);
        PlaceNode placeNode = curThreadPlaceNode.get(threadNumber2);
        FlowUtil.createPTFlow(placeNode, transitionNode);
    }

    public void addReadTransitionNode(int iid, int threadNumber, long memory) {
        TransitionNode transitionNode = new ReadTransitionNode(iid, threadNumber, netUtil.gengrateTransitionName(iid, threadNumber), memory,
                "read(" + threadNumber + "," + memory + ")");
        addTransitionNode(transitionNode, threadNumber);
    }

    public void addWriteTransitionNode(int iid, int threadNumber, long memory) {
        TransitionNode transitionNode = new WriteTransitionNode(iid, threadNumber, netUtil.gengrateTransitionName(iid, threadNumber), memory,
                "write(" + threadNumber + "," + memory + ")");
        addTransitionNode(transitionNode, threadNumber);
    }

    public void addStopTransitionNode(int iid, int threadNumber) {
        TransitionNode transitionNode = new StopTransitionNode(iid, threadNumber, netUtil.gengrateTransitionName(iid, threadNumber),
                "stop(" + threadNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
    }

    public void addWaitTransitionNode(int iid, int threadNumber, int lockNumber) {
        TransitionNode transitionNode = new WaitTransitionNode(iid, threadNumber, netUtil.gengrateTransitionName(iid, threadNumber),
                "wait(" + threadNumber + "," + lockNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
        //TODO 实现wait
        if (!lockWaitSet.containsKey(lockNumber)) lockWaitSet.put(lockNumber, new CopyOnWriteArraySet<>());
        lockWaitSet.get(lockNumber).add(transitionNode);
        latestNotifyNode = null;
    }

    public void addNotifyTransitionNode(int iid, int threadNumber, int lockNumber) {
        TransitionNode transitionNode = new NotifyTransitionNode(iid, threadNumber, netUtil.gengrateTransitionName(iid, threadNumber),
                "notify(" + threadNumber + "," + lockNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
        //TODO 实现notify
        latestNotifyNode = transitionNode;
    }

    public void addNotifyAllTransitionNode(int iid, int threadNumber, int lockNumber) {
        TransitionNode transitionNode = new NotifyAllTransitionNode(iid, threadNumber, netUtil.gengrateTransitionName(iid, threadNumber),
                "notifyAll(" + threadNumber + "," + lockNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
        //TODO 实现notifyAll
        latestNotifyNode = transitionNode;
    }

    public void connectNotifyWait(int thread, int lockNumber) {
        PlaceNode placeNode = new SourcePlaceNode(thread, 0, netUtil.generateSourcePlaceName(thread));
        CopyOnWriteArraySet<TransitionNode> set = lockWaitSet.get(lockNumber);
        TransitionNode transitionNode = null;
        for (TransitionNode transitionNode1: set) {
            if (transitionNode1.getThreadNumber() == thread) {
                transitionNode = transitionNode1;
                break;
            }
        }
        set.remove(transitionNode);
        FlowUtil.createPTFlow(placeNode, transitionNode);
        if(latestNotifyNode != null) FlowUtil.createTPFlow(latestNotifyNode, placeNode);
    }

    public static void main(String[] args) {
        PetriNet petriNet = new PetriNet();
        petriNet.createRootNode(0);
        petriNet.addWriteTransitionNode(0, 0, 0);
        petriNet.addStartTransitionNode(0, 0, 1);
        petriNet.addStartTransitionNode(0, 0, 2);
        petriNet.addWriteTransitionNode(0, 1, 1);
        petriNet.addAcqTransitionNode(0, 1, 0);
        petriNet.addWriteTransitionNode(0, 1, 0);
        petriNet.addRelTransitionNode(0, 1, 0);
        petriNet.addAcqTransitionNode(0, 2, 0);
        petriNet.addReadTransitionNode(0, 2, 0);
        petriNet.addRelTransitionNode(0, 2, 0);
        petriNet.addWriteTransitionNode(0, 2, 1);
        petriNet.addJoinTransitionNode(0, 0, 1);
        petriNet.addJoinTransitionNode(0, 0, 2);
        petriNet.addReadTransitionNode(0, 0, 1);
        petriNet.addStopTransitionNode(0, 0);

//        petriNet.showNet();
//        petriNet.consoleShowNet();
        petriNet.htmlShowNet("/home/wfb/毕设/calfuzzer/html/petri.html");

    }
}
