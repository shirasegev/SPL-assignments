package bgu.spl.mics.application.publishers;
import bgu.spl.mics.application.messages.TickBroadcast;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Publisher;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {

	private int duration;
	private int tickLongInMilli;

	// Constructor
	public TimeService(int duration, int tickLongInMilli) {
		super("Timer Service");
		this.duration = duration;
		this.tickLongInMilli = tickLongInMilli;
	}

	@Override
	// No implementation is needed
	protected void initialize() {
	}

	@Override
	// TimeService is a thread.
	// Run() method sends TickBroadcast as long as "duration" deadline hasn't been reached
	public void run() {
		System.out.println("Thread " + Thread.currentThread().getName() + " is running now");
		for (int i=0; i < duration; i++) {
			try {
				Thread.sleep(tickLongInMilli);
			} catch (InterruptedException e) {}

			Broadcast b = new TickBroadcast(i+1, i+1 == duration);
			getSimplePublisher().sendBroadcast(b);
		}
		System.out.println("Thread " + Thread.currentThread().getName() + " doing terminate");
	}
}