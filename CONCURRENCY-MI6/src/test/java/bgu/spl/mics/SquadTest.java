package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SquadTest {
    Squad sqd;
    Agent[] agents = new Agent[3];
    @BeforeEach
    public void setUp(){

        sqd = Squad.getInstance();

        Agent a1 = new Agent();
        a1.setName("James Bond");
        a1.setSerialNumber("007");

        Agent a2 = new Agent();
        a2.setName("Bill Fairbanks");
        a2.setSerialNumber("002");

        Agent a3 = new Agent();
        a3.setName("Sam Johnston");
        a3.setSerialNumber("0012");
    }

    @Test
    public void getInstance(){
        assertNotNull(sqd, "Squad is a null object");
    }

    @Test
    public void load(){
        sqd.load(agents);
        List<String> listOfAgentsNames;
        List<String> listOfSerialNumbers = new LinkedList<>();
        listOfSerialNumbers.add("007");
        listOfSerialNumbers.add("002");
        listOfSerialNumbers.add("0012");
        assertTrue(sqd.getAgents(listOfSerialNumbers),"load() method doesn't work as expected, or, getAgent() method's return value is invalid");
        listOfAgentsNames = sqd.getAgentsNames(listOfSerialNumbers);
        assertTrue(listOfAgentsNames.contains("James Bond"),"load() method doesn't work as expected, or, getAgent() method's return value is invalid");
    }

    @Test
    public void getAgentsNames(){
        sqd.load(agents);
        List<String> listOfSerialNumbers = new LinkedList<>();
        listOfSerialNumbers.add("007");
        List<String> listOfAgentsNames = sqd.getAgentsNames(listOfSerialNumbers);
        assertTrue(listOfAgentsNames.contains("James Bond"),"getAgentsName() method's return value's content is not as expected");
        listOfSerialNumbers.remove("007");
        listOfAgentsNames = sqd.getAgentsNames(listOfSerialNumbers);
        assertFalse(listOfAgentsNames.contains("James Bond"),"getAgentsName() method's return value's content is not as expected");
        listOfSerialNumbers.add("0012");
        listOfAgentsNames = sqd.getAgentsNames(listOfSerialNumbers);
        assertTrue(listOfAgentsNames.contains("Sam Johnston"),"getAgentsName() method's return value's content is not as expected");
    }

    @Test
    public void getAgents(){
        sqd.load(agents);
        List<String> listOfSerialNumbers = new LinkedList<>();
        listOfSerialNumbers.add("007");
        listOfSerialNumbers.add("002");
        assertTrue(sqd.getAgents(listOfSerialNumbers),"getAgents() method doesn't work as expected.");
        listOfSerialNumbers.add("07");
        assertFalse(sqd.getAgents(listOfSerialNumbers),"No problem was detected at inserted list, even though the agent's serial number is invalid");
    }

    @Test
    public void releaseAgents(){
        sqd.load(agents);
        List<String> listOfSerialNumbers = new LinkedList<>();
        listOfSerialNumbers.add("007");
        listOfSerialNumbers.add("002");
        sqd.releaseAgents(listOfSerialNumbers);
        assertTrue(agents[0].isAvailable(),"The requested agent was just released, but yet, he is unavailable");
        assertTrue(agents[1].isAvailable(),"The requested agent was just released, but yet, he is unavailable");
    }

    @Test
    public void sendAgents(){
        sqd.load(agents);
        List<String> listOfSerialNumbers = new LinkedList<>();
        listOfSerialNumbers.add("007");
        sqd.sendAgents(listOfSerialNumbers,5);
        assertTrue(agents[0].isAvailable(),"The requested agent was just sent on a mission (which means complete() method was called), but yet, he is unavailable");
    }
}