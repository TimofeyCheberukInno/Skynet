package com.app.faction;

import com.app.enums.BodyPart;
import com.app.factory.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

public class Faction implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Faction.class);
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

                requestParts(day);

                endOfNight.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }

        reportArmyComposition();
    }

    private void requestParts(int day) throws InterruptedException {
        List<BodyPart> requestedParts = factory.getParts(MAX_AMOUNT_TO_CARRY);
        logger.info("List of requested parts for {} for day {} : {} ", Thread.currentThread().getName(), day,  requestedParts);
        requestedParts.forEach(part -> parts.put(part, parts.getOrDefault(part, 0) + 1));
    }

    private void reportArmyComposition(){
        int readyRobots = Integer.MAX_VALUE;
        for(int value : parts.values()){
            readyRobots = Math.min(readyRobots, value);
        }

        readyRobots =  readyRobots == Integer.MAX_VALUE ? 0 : readyRobots;

        logger.info("{}has {} robots!", Thread.currentThread().getName(), readyRobots);
    }
}
