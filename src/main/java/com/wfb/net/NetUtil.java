package com.wfb.net;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NetUtil {
    private AtomicInteger transitionCnt;
    private AtomicInteger lockCnt;
    private ConcurrentMap<Integer, Integer> placeCntMap;

    public NetUtil() {
        placeCntMap = new ConcurrentHashMap<>();
        transitionCnt = new AtomicInteger(0);
        lockCnt = new AtomicInteger(0);
    }

    public String generateLockPlaceName(int threadNumber, int lockNumber) {
//        return "P" + placeCnt.getAndIncrement();
        String name = "Plock" + lockCnt.getAndIncrement();
//        System.out.println("create lock:" + name);
        return name;
    }

    public String generateSourcePlaceName(int threadNumber) {
        if (!placeCntMap.containsKey(threadNumber)) placeCntMap.put(threadNumber, 0);
        int cnt = placeCntMap.get(threadNumber);
        String name = "P" + threadNumber + "_" + cnt++;
        placeCntMap.put(threadNumber, cnt);
//        System.out.println("create place:" + name);
        return name;
    }

    public String generateTransitionName(int iid, int threadNumber) {
        String name = "T" + transitionCnt.getAndIncrement();
//        System.out.println("create transition:" + name);
        return name;
    }

}
