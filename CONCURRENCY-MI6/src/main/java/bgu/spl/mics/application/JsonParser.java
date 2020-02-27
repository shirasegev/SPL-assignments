package bgu.spl.mics.application;

public class JsonParser extends MI6Runner{

    String[] inventory;
    Services services;
    Agent[] squad;

    class Services {
        int M;
        int Moneypenny;
        Intelligence[] intelligence;
        int time;
    }

    class Intelligence {
        Mission[] missions;
    }

    class Mission {
        String[] serialAgentsNumbers;
        int duration;
        String gadget;
        String name;
        int timeExpired;
        int timeIssued;
    }

    class Agent {
        String name;
        String serialNumber;
    }
}