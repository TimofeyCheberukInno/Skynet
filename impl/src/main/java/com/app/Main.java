package com.app;

import com.app.faction.Faction;
import com.app.factory.Factory;

import java.util.concurrent.CyclicBarrier;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String factoryName = "Factory";
        String firstFactionName = "Faction1";
        String secondFactionName = "Faction2";

        int wordPeriodInDays = 100;

        CyclicBarrier endOfDay = new CyclicBarrier(
                3,
                () -> System.out.println("\n=== Day Is Over ==="));
        CyclicBarrier endOfNight = new CyclicBarrier(
                3,
                () -> System.out.println("\n=== Night is Over ==="));

        Factory factory = new Factory(wordPeriodInDays,
                endOfDay,
                endOfNight);

        Thread factoryThread = new Thread(
                factory,
                factoryName);

        Thread factionThread1 = new Thread(
                new Faction(
                        wordPeriodInDays,
                        endOfDay,
                        endOfNight,
                        factory
                ),
                firstFactionName
        );

        Thread factionThread2 = new Thread(
                new Faction(
                        wordPeriodInDays,
                        endOfDay,
                        endOfNight,
                        factory
                ),
                secondFactionName
        );

        factoryThread.start();
        factionThread1.start();
        factionThread2.start();

        factoryThread.join();
        factionThread1.join();
        factionThread2.join();
    }
}