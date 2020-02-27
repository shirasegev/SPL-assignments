package bgu.spl.mics.application.responses;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

public class AgentOperationResponse implements Response {

    private boolean operationStatus = false;
    private MissionInfo missionInfo;
    private boolean release = false;
    private Future<AgentOperationResponse> agentOperationResponse;

    public AgentOperationResponse(MissionInfo missionInfo) {
        this.missionInfo = missionInfo;
    }

    public void setRelease(boolean release) {
        this.release = release;
        agentOperationResponse = new Future<>();
    }

    public Future<AgentOperationResponse> getOperationResponse(){
        return agentOperationResponse;
    }

    public MissionInfo getMissionInfo() {
        return missionInfo;
    }

    public boolean toRelease(){
        return release;
    }

    public void setOperationStatus(boolean operationStatus) {
        this.operationStatus = operationStatus;
    }

    public boolean getOperationStatus() {
        return operationStatus;
    }

}