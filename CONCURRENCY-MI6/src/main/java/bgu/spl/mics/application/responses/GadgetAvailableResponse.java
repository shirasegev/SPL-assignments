package bgu.spl.mics.application.responses;

import bgu.spl.mics.Response;

public class GadgetAvailableResponse implements Response {
    private boolean available;
    private int time;

    public GadgetAvailableResponse(boolean available, int time){
        this.available = available;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public boolean isAvailable() {
        return available;
    }
}