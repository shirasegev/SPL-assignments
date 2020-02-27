package bgu.spl.mics.application.responses;

import bgu.spl.mics.*;

import java.util.List;

public class AgentAvailableResponse implements Response {
    private int moneypennyNum;
    private List<String> agentNames;
    private boolean available;
    private Future<AgentOperationResponse> agentOperationResponse;


    public AgentAvailableResponse (int moneypennyNum, boolean available) {
        this.moneypennyNum = moneypennyNum;
        this.available = available;
        agentOperationResponse = new Future<>();
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAgentNames(List<String> agentNames) {
        this.agentNames = agentNames;
    }

    public List<String> getAgentNames() {
        return agentNames;
    }

    public int getMoneypenny() {
        return moneypennyNum;
    }

    public Future<AgentOperationResponse> getOperationResponse(){
        return agentOperationResponse;
    }
}