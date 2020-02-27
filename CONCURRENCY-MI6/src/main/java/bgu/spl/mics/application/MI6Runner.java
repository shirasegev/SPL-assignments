package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.subscribers.*;
import bgu.spl.mics.application.publishers.TimeService;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.Arrays;
import java.util.Vector;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {

    private static int completeInit = 0;
    private static Object lock = new Object();
    private static int tickLongInMilli = 100;

    public static void main(String[] args) {
        if (args.length != 3)
            return;

        JsonParser parser = getJsonParser(args[0]);

        loadAgents(parser);

        int finalTick = parser.services.time;

        Vector<Thread> threads = new Vector<>();

        createIntelligence(parser, threads);
        createM(parser, threads);
        createQ(parser, threads);
        createMoneypenny(parser, threads);

        createTimeService(parser, threads, finalTick);

        // Wait for them to finish
        for (int i = 0; i < threads.size(); i++) {
            try {
                System.out.println("Waiting for Thread " + threads.get(i).getName() + " to end");
                threads.get(i).join();
            } catch (InterruptedException e) {}
        }

        // Generate output files
        Inventory.getInstance().printToFile(args[1]);
        Diary.getInstance().printToFile(args[2]);

    } // End of main

    public static JsonParser getJsonParser (String path) {
        Gson gson = new Gson();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(path));
        } catch (FileNotFoundException e) {}
        return gson.fromJson(reader, JsonParser.class);
    }

    public static void loadAgents(JsonParser parser) {
        Agent[] agents = new Agent[parser.squad.length];
        for (int i = 0; i < parser.squad.length; i++) {
            Agent agent = new Agent();
            agent.setName(parser.squad[i].name);
            agent.setSerialNumber(parser.squad[i].serialNumber);
            agents[i] = agent;
        }
        Squad.getInstance().load(agents);

        Inventory.getInstance().load(parser.inventory);
    }

    public static void createIntelligence(JsonParser parser, Vector<Thread> threads) {
        Intelligence[] intelligence = new Intelligence[parser.services.intelligence.length];
        for (int i = 0; i < intelligence.length; i++) {
            MissionInfo[] missions = new MissionInfo[parser.services.intelligence[i].missions.length];

            for (int j = 0; j < missions.length; j++) {

                MissionInfo missionInfo = new MissionInfo();

                JsonParser.Mission mission = parser.services.intelligence[i].missions[j];
                missionInfo.setMissionName(mission.name);
                missionInfo.setSerialAgentsNumbers(Arrays.asList(mission.serialAgentsNumbers));
                missionInfo.setGadget(mission.gadget);
                missionInfo.setDuration(mission.duration);
                missionInfo.setTimeIssued(mission.timeIssued);
                missionInfo.setTimeExpired(mission.timeExpired);

                missions[j] = missionInfo;
            }

            intelligence[i] = new Intelligence(i + 1, missions);

            Thread t = new Thread(intelligence[i], "Intelligence " + (i + 1));
            t.start();
            threads.add(t);
        }
    }

    public static void createM(JsonParser parser, Vector<Thread> threads) {
        M[] ms = new M[parser.services.M];

        for (int i = 0; i < ms.length; i++) {
            ms[i] = new M(i + 1);

            Thread t = new Thread(ms[i], "M " + (i + 1));
            t.start();
            threads.add(t);
        }
    }

    public static void createQ(JsonParser parser, Vector<Thread> threads) {
        Q q = new Q("Q");

        Thread t = new Thread(q, "Q");
        t.start();
        threads.add(t);
    }

    public static void createMoneypenny(JsonParser parser, Vector<Thread> threads) {
        Moneypenny[] moneypennys = new Moneypenny[parser.services.Moneypenny];

        for (int i = 0; i < moneypennys.length; i++) {
            moneypennys[i] = new Moneypenny(i + 1, tickLongInMilli);

            Thread t = new Thread(moneypennys[i], "Moneypenny " + (i + 1));
            t.start();
            threads.add(t);
        }
    }

    public static void createTimeService(JsonParser parser, Vector<Thread> threads, int finalTick) {

        TimeService timeService = new TimeService(finalTick, tickLongInMilli);

        Thread t = new Thread(timeService, "Timer");

        synchronized (lock) {
            while (completeInit != threads.size()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        t.start();
        threads.add(t);
    }

    public static void incrementCompleteInit() {
        synchronized (lock) {
            completeInit++;
            lock.notifyAll();
        }
    }
}