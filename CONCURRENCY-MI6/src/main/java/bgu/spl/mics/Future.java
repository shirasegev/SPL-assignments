package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {

	// This class has no threads, but yet, it is thread safe
	//(that way it will work when used by few threads).

	private T result;
	private boolean done = false;
	
	/**
	 * This should be the the only public constructor in this class.
	 */

	// Constructor
	public Future() {
		result = null;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */

	// get method defines as synchronized, because it's a blocking method
	public synchronized T get() {
		while (!isDone()) {
			try {
				wait();
			} catch (InterruptedException ignored) {} // Ignore the exception
		}
		return result;
	}
	
	/**
     * Resolves the result of this Future object.
     */
	// Resolve is synchronized because we change the state and we notify.
	public synchronized void resolve (T result) {
		this.result = result;
		done = true;
		notify();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return done;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */

	// get method defines as synchronized, because it's a blocking method
	public synchronized T get(long timeout, TimeUnit unit) {
		long end = System.currentTimeMillis() + unit.toMillis(timeout);

		// This loop waits for the result, as long as there is still time.
		// We do not rely on the wait due to spurious wakeup
		while (!isDone() && System.currentTimeMillis() < end) {
			try {
				unit.timedWait(this, timeout);
			} catch (InterruptedException ignored) {
				return null;
			}
		}
		return result;
	}

}