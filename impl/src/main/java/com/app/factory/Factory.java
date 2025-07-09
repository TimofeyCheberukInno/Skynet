package com.app.factory;

import com.app.enums.BodyPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Factory implements Runnable {
    private final int WORK_PERIOD_IN_DAYS;
    private BlockingQueue<BodyPart> parts;
    private static final int MAX_COUNT_OF_BODY_PARTS_PER_DAY = 10;
    private static final int COUNT_OF_BODY_PARTS = 4;
    private static final int MAX_POSSIBLE_QUEUE_SIZE = 10;
    private final CyclicBarrier endOfDay;
    private final CyclicBarrier endOfNight;

    public Factory(int workPeriod, CyclicBarrier endOfDay, CyclicBarrier endOfNight){
        this.WORK_PERIOD_IN_DAYS = workPeriod;
        this.parts = new ArrayBlockingQueue<>(MAX_POSSIBLE_QUEUE_SIZE);
        this.endOfDay = endOfDay;
        this.endOfNight = endOfNight;
    }

    @Override
    public void run() {
        for(int day = 1; day <= 100; day++){
            produceBodyParts();
            try {
                endOfDay.await();
                endOfNight.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void produceBodyParts(){
        Random random = new Random();
        int amount = random.nextInt(MAX_COUNT_OF_BODY_PARTS_PER_DAY + 1);

        for(int i = 0; i < amount; ++i){
            BodyPart part = BodyPart.values()[random.nextInt(COUNT_OF_BODY_PARTS)];
            parts.add(part);
        }
    }

    public synchronized List<BodyPart> getParts(int maxCarryAmount) throws InterruptedException {
        List<BodyPart> order = new ArrayList<>();
        int availableAmount = Math.min(maxCarryAmount, parts.size());
        while(availableAmount > 0){
            order.add(parts.take());

            availableAmount--;
        }

        return order;
    }
}
