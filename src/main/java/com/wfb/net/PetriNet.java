package com.wfb.net;

import com.wfb.base.PlaceNode;
import com.wfb.base.TransitionNode;
import com.wfb.flow.*;
import com.wfb.place.LockPlaceNode;
import com.wfb.place.Selection1PlaceNode;
import com.wfb.place.Selection2PlaceNode;
import com.wfb.place.SourcePlaceNode;
import com.wfb.transition.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class PetriNet implements Serializable{
    private PlaceNode root;
    transient private NetUtil netUtil = new NetUtil();
    transient private Map<Integer, PlaceNode> curThreadPlaceNode = new HashMap<>();
    private ConcurrentHashMap<Integer, LockPlaceNode> lockPlaceNodeMap = new ConcurrentHashMap<>();
    // 某个锁资源的等待队列key:锁编号，value等待队列
    transient private ConcurrentHashMap<Integer, CopyOnWriteArraySet<TransitionNode>> lockWaitSet = new ConcurrentHashMap<>();
    transient private TransitionNode latestNotifyNode;

    // 用来存储所有的place和transition节点
    public Map<String, PlaceNode> allPlaceNodes;
    public Map<String, TransitionNode> allTransitionNodes;
    public Map<Long, List<TransitionNode>> varTransitionNodes;

    public void generateMap() {
        this.allPlaceNodes = new HashMap<>();
        this.allTransitionNodes = new HashMap<>();
        this.varTransitionNodes = new HashMap<>();
        GenerateMap generateMap = new GenerateMap(allPlaceNodes, allTransitionNodes, varTransitionNodes);
        root.traversal(generateMap);
    }

    public void copyToFile(String fileName) {
        File file = new File(fileName);
        try(FileOutputStream out = new FileOutputStream(file)) {
            try (ObjectOutputStream oos = new ObjectOutputStream(out)){
                oos.writeObject(this);
                oos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            htmlShowNet.printHeader(curThreadPlaceNode.keySet());
            root.traversal(htmlShowNet);
            htmlShowNet.printFooter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generatePXML(String path) {
        try (PrintStream ps = new PrintStream(new FileOutputStream(path))) {
            PXMLShowNet pxmlShowNet = new PXMLShowNet(ps);
            pxmlShowNet.printHeader();
            root.traversal(pxmlShowNet);
            pxmlShowNet.printFooter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createRootNode(int threadNumber) {
        this.root = new SourcePlaceNode(threadNumber,1,
                netUtil.generateSourcePlaceName(threadNumber));
        curThreadPlaceNode.put(threadNumber, root);
    }

    private void addTransitionNode(TransitionNode transitionNode, int threadNumber) {
        FlowUtil.createPTFlow(curThreadPlaceNode.get(threadNumber), transitionNode);
        PlaceNode placeNode = new SourcePlaceNode(threadNumber, 0,
                netUtil.generateSourcePlaceName(threadNumber));
        FlowUtil.createTPFlow(transitionNode, placeNode);
        curThreadPlaceNode.put(threadNumber, placeNode);
    }

    public void addStartTransitionNode(int iid, int threadNumber, int childThreadNumber) {
        StartTransitionNode transitionNode = new StartTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber),
                "start(" + threadNumber + "," + childThreadNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
        PlaceNode placeNode = new SourcePlaceNode(childThreadNumber, 0,
                netUtil.generateSourcePlaceName(childThreadNumber));
        FlowUtil.createTPFlow(transitionNode, placeNode);
        curThreadPlaceNode.put(childThreadNumber, placeNode);
    }

    public void addAcqTransitionNode(int iid, int threadNumber, int lockNumber) {
        TransitionNode transitionNode = new AcqTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber),
                "acq(" + threadNumber + "," + lockNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
        LockPlaceNode lockPlaceNode;
        if (lockPlaceNodeMap.containsKey(lockNumber)) lockPlaceNode = lockPlaceNodeMap.get(lockNumber);
        else {
            lockPlaceNode = new LockPlaceNode(0, 1,
                    netUtil.generateLockPlaceName(threadNumber, lockNumber));
            lockPlaceNodeMap.put(lockNumber, lockPlaceNode);
        }
        FlowUtil.createPTFlow(lockPlaceNode, transitionNode);
    }

    public void addRelTransitionNode(int iid, int threadNumber, int lockNumber) {
        TransitionNode transitionNode = new RelTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber),
                "rel(" + threadNumber + "," + lockNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
        LockPlaceNode lockPlaceNode;
        if (lockPlaceNodeMap.containsKey(lockNumber)) lockPlaceNode = lockPlaceNodeMap.get(lockNumber);
        else {
            lockPlaceNode = new LockPlaceNode(0, 1,
                    netUtil.generateLockPlaceName(threadNumber, lockNumber));
            lockPlaceNodeMap.put(lockNumber, lockPlaceNode);
        }
        FlowUtil.createTPFlow(transitionNode, lockPlaceNode);
    }

    public void addJoinTransitionNode(int iid, int threadNumber, int threadNumber2) {
        TransitionNode transitionNode = new JoinTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber),
                "join(" + threadNumber + "," + threadNumber2 + ")");
        addTransitionNode(transitionNode, threadNumber);
        PlaceNode placeNode = curThreadPlaceNode.get(threadNumber2);
        FlowUtil.createPTFlow(placeNode, transitionNode);
    }

    public void addReadTransitionNode(int iid, int threadNumber, long memory) {
        TransitionNode transitionNode = new ReadTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber), memory,
                "read(" + threadNumber + "," + memory + ")");
        addTransitionNode(transitionNode, threadNumber);
    }

    public void addWriteTransitionNode(int iid, int threadNumber, long memory) {
        TransitionNode transitionNode = new WriteTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber), memory,
                "write(" + threadNumber + "," + memory + ")");
        addTransitionNode(transitionNode, threadNumber);
    }

    public void addStopTransitionNode(int iid, int threadNumber) {
        TransitionNode transitionNode = new StopTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber),
                "stop(" + threadNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
    }

    public void addWaitTransitionNode(int iid, int threadNumber, int lockNumber) {
        //获取当前线程的最新资源节点，并将其变为选择资源节点
        PlaceNode curPlaceNode = curThreadPlaceNode.get(threadNumber);
        PlaceNode selection1PlaceNode = changePlaceNodeToSelection((SourcePlaceNode) curPlaceNode);
//        curThreadPlaceNode.put(threadNumber, selection1PlaceNode);

        //创建Skip变迁，并绑定selection1PlaceNode
        SkipTransitionNode skipTransitionNode = new SkipTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber),
                "skip");
        FlowUtil.createPTFlow(selection1PlaceNode, skipTransitionNode);

        // 创建wait变迁,并绑定selection1PlaceNode
        TransitionNode waitTransitionNode = new WaitTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber),
                "wait(" + threadNumber + "," + lockNumber + ")");
        FlowUtil.createPTFlow(selection1PlaceNode, waitTransitionNode);

        //将该wait变迁加入当前锁的等待队列，并将最近的notify变迁置空
        if (!lockWaitSet.containsKey(lockNumber)) lockWaitSet.put(lockNumber, new CopyOnWriteArraySet<>());
        lockWaitSet.get(lockNumber).add(waitTransitionNode);
        latestNotifyNode = null;

        //创建waitAfterPlaceNode，并绑定wait变迁
        SourcePlaceNode waitAfterPlaceNode = new SourcePlaceNode(threadNumber, 0,
                netUtil.generateSourcePlaceName(threadNumber));
        FlowUtil.createTPFlow(waitTransitionNode, waitAfterPlaceNode);
        //绑定wait变迁与锁
        LockPlaceNode lockPlaceNode = lockPlaceNodeMap.get(lockNumber); //当前等待的锁资源
        FlowUtil.createTPFlow(waitTransitionNode, lockPlaceNode);

        //创建wakeBeforePlaceNode和wake变迁，并绑定wait和wakeBeforePlaceNode及waitAfterPlaceNode
        SourcePlaceNode wakeBeforePlaceNode = new SourcePlaceNode(threadNumber, 0,
                netUtil.generateSourcePlaceName(threadNumber));
        WakeTransitionNode wakeTransitionNode = new WakeTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber),
                "wake");
        FlowUtil.createPTFlow(wakeBeforePlaceNode, wakeTransitionNode);
        FlowUtil.createPTFlow(waitAfterPlaceNode, wakeTransitionNode);

        //创建wakeAfterPlaceNode，并与wake变迁绑定
        SourcePlaceNode wakeAfterPlaceNode = new SourcePlaceNode(threadNumber, 0,
                netUtil.generateSourcePlaceName(threadNumber));
        FlowUtil.createTPFlow(wakeTransitionNode, wakeAfterPlaceNode);

        //创建Compete变迁，并与wakeAfterPlaceNode和lock绑定
        CompeteTransitionNode competeTransitionNode = new CompeteTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber),
                "compete");
        FlowUtil.createPTFlow(wakeAfterPlaceNode, competeTransitionNode);
        FlowUtil.createPTFlow(lockPlaceNode, competeTransitionNode);

        //创建selection2PlaceNode，并与skip和compete变迁绑定
        PlaceNode selection2PlaceNode = new Selection2PlaceNode(threadNumber, 0,
                netUtil.generateSourcePlaceName(threadNumber));
        FlowUtil.createTPFlow(skipTransitionNode, selection2PlaceNode);
        FlowUtil.createTPFlow(competeTransitionNode, selection2PlaceNode);

        //更新当前线程的最新资源节点
        curThreadPlaceNode.put(threadNumber, selection2PlaceNode);
    }

    private Selection1PlaceNode changePlaceNodeToSelection(SourcePlaceNode sourcePlaceNode) {
        Selection1PlaceNode selection1PlaceNode = new Selection1PlaceNode(sourcePlaceNode.getThreadNumber(),
                sourcePlaceNode.getTokenCnt(), sourcePlaceNode.getName());
        TransitionNode transitionNode = sourcePlaceNode.getUpTransitionNode();
        transitionNode.changeDownPlaceNode(sourcePlaceNode, selection1PlaceNode);
        selection1PlaceNode.addUpTransitionNode(transitionNode);
        return selection1PlaceNode;
    }

    /**
     * 先添加下属资源节点，再添加notify扩展节点
     * @param iid
     * @param threadNumber
     * @param lockNumber
     */
    public void addNotifyTransitionNode(int iid, int threadNumber, int lockNumber) {
        TransitionNode transitionNode = new NotifyTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber),
                "notify(" + threadNumber + "," + lockNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
        SourcePlaceNode placeNode = new SourcePlaceNode(threadNumber, 0,
                netUtil.generateSourcePlaceName(threadNumber));
        FlowUtil.createTPFlow(transitionNode, placeNode);
        latestNotifyNode = transitionNode;
    }

    public void addNotifyAllTransitionNode(int iid, int threadNumber, int lockNumber) {
        TransitionNode transitionNode = new NotifyAllTransitionNode(iid, threadNumber,
                netUtil.generateTransitionName(iid, threadNumber),
                "notifyAll(" + threadNumber + "," + lockNumber + ")");
        addTransitionNode(transitionNode, threadNumber);
        //TODO 实现notifyAll
        latestNotifyNode = transitionNode;
    }

    /**
     * 这个方法要放到waitAfter中执行，也就是说一定是之前刚执行了一个notify方法
     * @param thread
     * @param lockNumber
     */
    public void connectNotifyWait(int thread, int lockNumber) {
        //取到当前的wait，并赋值给transitionNode
        CopyOnWriteArraySet<TransitionNode> set = lockWaitSet.get(lockNumber); //获取当前锁的等待队列
        TransitionNode transitionNode = null;
        for (TransitionNode transitionNode1: set) {
            if (transitionNode1.getThreadNumber() == thread) {
                transitionNode = transitionNode1;
                break;
            }
        }
        set.remove(transitionNode);

        //获取wakeBeforePlaceNode节点
        PlaceNode wakeBeforePlaceNode = ((WaitTransitionNode)transitionNode).getWakeBeforePlaceNode();

        // 获取notify2BeforePlaceNode
        PlaceNode notify2BeforePlaceNode;
        if (latestNotifyNode instanceof NotifyTransitionNode) {
            notify2BeforePlaceNode = ((NotifyTransitionNode) latestNotifyNode).getNotify2BeforePlaceNode();
        } else {
            notify2BeforePlaceNode = ((NotifyAllTransitionNode)latestNotifyNode).getNotify2BeforePlaceNode();
        }

        //获取notify线程的线程信息
        int notifyThread = latestNotifyNode.getThreadNumber();
        int notifyIid = latestNotifyNode.getIid();

        //创建notify2变迁，并与notify2BeforePlaceNode绑定
        Notify2TransitionNode notify2TransitionNode = new Notify2TransitionNode(notifyIid, notifyThread,
                netUtil.generateTransitionName(notifyIid, notifyThread),
                "notify2");
        FlowUtil.createPTFlow(notify2BeforePlaceNode, notify2TransitionNode);

        //连接notify2变迁和wakeBeforePlaceNode
        FlowUtil.createTPFlow(notify2TransitionNode, wakeBeforePlaceNode);
    }

//    public static void main(String[] args) {
//        PetriNet petriNet = new PetriNet();
//        petriNet.createRootNode(0);
//        petriNet.addWriteTransitionNode(0, 0, 0);
//        petriNet.addStartTransitionNode(0, 0, 1);
//        petriNet.addStartTransitionNode(0, 0, 2);
//        petriNet.addWriteTransitionNode(0, 1, 1);
//        petriNet.addAcqTransitionNode(0, 1, 0);
//        petriNet.addWriteTransitionNode(0, 1, 0);
//        petriNet.addRelTransitionNode(0, 1, 0);
//        petriNet.addAcqTransitionNode(0, 2, 0);
//        petriNet.addReadTransitionNode(0, 2, 0);
//        petriNet.addRelTransitionNode(0, 2, 0);
//        petriNet.addWriteTransitionNode(0, 2, 1);
//        petriNet.addJoinTransitionNode(0, 0, 1);
//        petriNet.addJoinTransitionNode(0, 0, 2);
//        petriNet.addReadTransitionNode(0, 0, 1);
//        petriNet.addStopTransitionNode(0, 0);
//
//        petriNet.consoleShowNet();
//        System.out.println("------------------");
//        petriNet.consoleShowTransitions();
//        System.out.println("------------------");
//        petriNet.htmlShowNet("/home/wfb/毕设/calfuzzer/html/petri.html");
//    }

//    public static void main(String[] args) {
////        Util.setIidToLineMap("");
//        PetriNet petriNet = new PetriNet();
//        petriNet.createRootNode(4);
//        petriNet.addStartTransitionNode(0, 4, 6);
//        petriNet.addAcqTransitionNode(0, 6, 7);
//        petriNet.addWaitTransitionNode(0, 6, 7);
//        petriNet.addStartTransitionNode(0, 4, 9);
//        petriNet.addAcqTransitionNode(0, 9, 7);
//        petriNet.addNotifyTransitionNode(0, 9, 7);
//        petriNet.addRelTransitionNode(0, 9, 7);
//        petriNet.connectNotifyWait(6, 7);
//        petriNet.addNotifyTransitionNode(0, 6, 7);
//        petriNet.addRelTransitionNode(0, 6, 7);
//
//        petriNet.consoleShowTransitions();
//        System.out.println("--------------------");
//        petriNet.consoleShowNet();
//        System.out.println("--------------------");
//        petriNet.htmlShowNet("/home/wfb/毕设/calfuzzer/html/petri.html");
//    }

    public static void main(String[] args) {
        PetriNet petriNet = new PetriNet();
        petriNet.createRootNode(4);
        petriNet.addStartTransitionNode(0, 4, 6);
        petriNet.addAcqTransitionNode(0, 6, 7);
        petriNet.addWaitTransitionNode(0, 6, 7);
        petriNet.addStartTransitionNode(0, 4, 9);
        petriNet.addAcqTransitionNode(0, 9, 7);
        petriNet.addNotifyTransitionNode(0, 9, 7);
        petriNet.addRelTransitionNode(0, 9, 7);
        petriNet.connectNotifyWait(6, 7);
        petriNet.addNotifyTransitionNode(0, 6, 7);
        petriNet.addRelTransitionNode(0, 6, 7);

        petriNet.htmlShowNet("/home/wfb/毕设/calfuzzer/html/petri.html");
        petriNet.generatePXML("/home/wfb/毕设/calfuzzer/html/petri5.xml");
        petriNet.generateMap();
        petriNet.copyToFile("/home/wfb/毕设/calfuzzer/html/petri.obj");

    }
}
