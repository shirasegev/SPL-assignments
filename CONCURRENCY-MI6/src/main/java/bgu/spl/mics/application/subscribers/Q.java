package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.responses.GadgetAvailableResponse;
import bgu.spl.mics.application.passiveObjects.Inventory;

import static bgu.spl.mics.application.MI6Runner.incrementCompleteInit;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {

	private int time;

	public Q(String name) {
		super(name);
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, tick -> {
			time = tick.getTime();
			if (tick.terminate()) {
				terminate();
			}
		});

		subscribeEvent(GadgetAvailableEvent.class, event -> {
			GadgetAvailableResponse result = new GadgetAvailableResponse(Inventory.getInstance().getItem(event.getGadget()), time);
			complete(event, result);
		});
		incrementCompleteInit();
	}

}