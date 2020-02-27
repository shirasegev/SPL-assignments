package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import com.google.gson.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OutputGenerator extends MI6Runner{

    public static void printDiary(String filename, List<Report> reports) {
        JsonObject obj = new JsonObject();
        JsonArray rep = new JsonArray();

        System.out.println("Creating a dairy");

        for(Report report : reports){
            JsonObject mission = new JsonObject ();
            mission.addProperty("missionName" , report.getMissionName());
            mission.addProperty("m" , report.getM());
            mission.addProperty("moneypenny" , report.getMoneypenny());
            JsonArray agentsSerialNumbers = new JsonArray();
            for(String serial : report.getAgentsSerialNumbers()){
                agentsSerialNumbers.add(serial);
            }
            mission.add("agentsSerialNumbers" , agentsSerialNumbers);
            JsonArray agentsNames = new JsonArray();
            for(String name : report.getAgentsNames()){
                agentsNames.add(name);
            }
            mission.add("agentsNames" , agentsNames);
            mission.addProperty("gadgetName" , report.getGadgetName());
            mission.addProperty("timeCreated" , report.getTimeCreated());
            mission.addProperty("timeIssued" , report.getTimeIssued());
            mission.addProperty("qTime",report.getQTime());
            rep.add(mission);
        }
        obj.add("reports",rep);
        obj.addProperty("total", Diary.getInstance().getTotal());

        try (FileWriter file = new FileWriter(filename)) {
            file.write(obj.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Dairy is created");
    }

    public static void printInventory(String filename, List<String> remains) {
        JsonArray gadgets = new JsonArray();
        for(String gadget : remains){
            gadgets.add(gadget);
        }

        try (FileWriter file = new FileWriter(filename)) {
            file.write(gadgets.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Inventory output created");
    }
}