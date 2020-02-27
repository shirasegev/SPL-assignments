package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import java.util.List;

public class AgentsAvailableEvent implements Event {

    private List<String> agentSerialNumbers;

    public AgentsAvailableEvent (List<String> agentSerialNumbers) {
        this.agentSerialNumbers = agentSerialNumbers;
    }

    public List<String> getAgentSerialNumbers() {
        return agentSerialNumbers;
    }

}