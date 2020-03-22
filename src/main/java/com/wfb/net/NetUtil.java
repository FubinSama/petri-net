package com.wfb.net;

import java.io.ObjectInputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NetUtil {
    private AtomicInteger placeCnt;
    private AtomicInteger transitionCnt;
    private AtomicInteger lockCnt;

    public NetUtil() {
        placeCnt = new AtomicInteger(0);
        transitionCnt = new AtomicInteger(0);
        lockCnt = new AtomicInteger(0);
    }

    public String generateLockPlaceName(int threadNumber, int lockNumber) {
        String name = "Plock" + lockCnt.getAndIncrement();
//        System.out.println("create lock:" + name);
        return name;
    }

    public String generateSourcePlaceName(int threadNumber) {
        String name = "P" + placeCnt.getAndIncrement();
//        System.out.println("create place:" + name);
        return name;
    }

    public String gengrateTransitionName(int iid, int threadNumber) {
        String name = "T" + transitionCnt.getAndIncrement();
//        System.out.println("create transition:" + name);
        return name;
    }

}
