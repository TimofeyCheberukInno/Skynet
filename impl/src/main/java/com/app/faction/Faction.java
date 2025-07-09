package com.app.faction;

import com.app.enums.BodyPart;
import com.app.factory.Factory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

public class Faction implements Runnable {
    private final int WORK_PERIOD_IN_DAYS;
    private static final int MAX_AMOUNT_TO_CARRY = 5;
    Map<BodyPart, Integer> parts;
    private final CyclicBarrier endOfDay;
    private final CyclicBarrier endOfNight;
    private final Factory factory;

    public Faction(int workPeriod,
                   CyclicBarrier endOfDay,
                   CyclicBarrier endOfNight,
                   Factory factory){
        this.WORK_PERIOD_IN_DAYS = workPeriod;
        this.parts = new ConcurrentHashMap<>();
        this.endOfDay = endOfDay;
        this.endOfNight = endOfNight;
        this.factory = factory;
    }

    @Override
    public void run() {
        for(int day = 1; day <= 100; day++){
            try {
                endOfDay.await();

                requestParts();

                endOfNight.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }

        reportArmyComposition();
    }

    private void requestParts() throws InterruptedException {
        List<BodyPart> requestedParts = factory.getParts(MAX_AMOUNT_TO_CARRY);
        requestedParts.forEach(part -> parts.put(part, parts.getOrDefault(part, 0) + 1));
    }

    private void reportArmyComposition(){
        int readyRobots = Integer.MAX_VALUE;
        for(int value : parts.values()){
            readyRobots = Math.min(readyRobots, value);
        }

        readyRobots =  readyRobots == Integer.MAX_VALUE ? 0 : readyRobots;

        System.out.println(Thread.currentThread().getName() + "has " + readyRobots + " robots!");
    }
}
