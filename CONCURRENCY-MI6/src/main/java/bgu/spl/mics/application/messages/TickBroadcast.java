package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    private int time;
    private boolean terminate;

    public TickBroadcast(int time, boolean terminate) {
        this.time = time;
        this.terminate = terminate;
    }

    // Returns current tick to all subscribers who are registered to this broadcast type
    public int getTime() {
        return time;
    }

    public boolean terminate () {
        return terminate;
    }

}