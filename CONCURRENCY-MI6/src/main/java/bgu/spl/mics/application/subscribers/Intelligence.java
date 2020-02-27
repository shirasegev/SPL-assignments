package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import static bgu.spl.mics.application.MI6Runner.incrementCompleteInit;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {

    MissionInfo[] missions;

    public Intelligence(int num, MissionInfo[] missions) {
        super("Intelligence " + num);
        this.missions = missions;
    }

    @Override
    protected void initialize() {

        subscribeBroadcast(TickBroadcast.class, tick -> {
            // The subscriber abstract class implements terminate() method
            if (tick.terminate()) {
                terminate();
            }

            for (MissionInfo mission: missions) {
                if (mission.getTimeIssued() == tick.getTime()) {
                    MissionReceivedEvent missionEvent = new MissionReceivedEvent(mission);
                    getSimplePublisher().sendEvent(missionEvent);
                }
            }
        });
        incrementCompleteInit();
    }

}