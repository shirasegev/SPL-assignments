package bgu.spl.mics;

import java.util.HashMap;

/**
 * The Subscriber is an abstract class that any subscriber in the system
 * must extend. The abstract Subscriber class is responsible to get and
 * manipulate the singleton {@link MessageBroker} instance.
 * <p>
 * Derived classes of Subscriber should never directly touch the MessageBroker.
 * the derived class also supplies a {@link Callback} that should be called when
 * a message of the subscribed type was taken from the Subscriber
 * message-queue (see {@link MessageBroker#register(Subscriber)}
 * method). The abstract Subscriber stores this callback together with the
 * type of the message is related to.
 * <p>
 * Only private fields and methods may be added to this class.
 * <p>
 */
public abstract class Subscriber extends RunnableSubPub {

	private boolean terminated = false;

	private MessageBroker messageBroker;
	private HashMap<Class, Callback> callbacks;

	/**
	 * @param name the Subscriber name (used mainly for debugging purposes -
	 *             does not have to be unique)
	 */
	// Constructor
	public Subscriber(String name) {
		super(name);
		messageBroker = MessageBrokerImpl.getInstance();
		callbacks = new HashMap();
	}

	/**
	 * Subscribes to events of type {@code type} with the callback
	 * {@code callback}. This means two things:
	 * 1. Subscribe to events in the singleton MessageBroker using the supplied
	 * {@code type}
	 * 2. Store the {@code callback} so that when events of type {@code type}
	 * are received it will be called.
	 * <p>
	 * For a received message {@code m} of type {@code type = m.getClass()}
	 * calling the callback {@code callback} means running the method
	 * {@link Callback#call(java.lang.Object)} by calling
	 * {@code callback.call(m)}.
	 * <p>
	 *
	 * @param <E>      The type of event to subscribe to.
	 * @param <T>      The type of result expected for the subscribed event.
	 * @param type     The {@link Class} representing the type of event to
	 *                 subscribe to.
	 * @param callback The callback that should be called when messages of type
	 *                 {@code type} are taken from this Subscriber message
	 *                 queue.
	 */
	protected final <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback) {
		callbacks.put(type, callback);
		messageBroker.subscribeEvent(type, this);
	}

	/**
	 * Subscribes to broadcast message of type {@code type} with the callback
	 * {@code callback}. This means two things:
	 * 1. Subscribe to broadcast messages in the singleton MessageBroker using the
	 * supplied {@code type}
	 * 2. Store the {@code callback} so that when broadcast messages of type
	 * {@code type} received it will be called.
	 * <p>
	 * For a received message {@code m} of type {@code type = m.getClass()}
	 * calling the callback {@code callback} means running the method
	 * {@link Callback#call(java.lang.Object)} by calling
	 * {@code callback.call(m)}.
	 * <p>
	 *
	 * @param <B>      The type of broadcast message to subscribe to
	 * @param type     The {@link Class} representing the type of broadcast
	 *                 message to subscribe to.
	 * @param callback The callback that should be called when messages of type
	 *                 {@code type} are taken from this Subscriber message
	 *                 queue.
	 */
	protected final <B extends Broadcast> void subscribeBroadcast(Class<B> type, Callback<B> callback) {
		callbacks.put(type, callback);
		messageBroker.subscribeBroadcast(type, this);
	}

	/**
	 * Completes the received request {@code e} with the result {@code result}
	 * using the MessageBroker.
	 * <p>
	 *
	 * @param <T>    The type of the expected result of the processed event
	 *               {@code e}.
	 * @param e      The event to complete.
	 * @param result The result to resolve the relevant Future object.
	 *               {@code e}.
	 */
	protected final <T> void complete(Event<T> e, T result) {
		messageBroker.complete(e, result);
	}

	/**
	 * Signals the event loop that it must terminate after handling the current
	 * message.
	 */
	protected final void terminate() {
		this.terminated = true;
	}

	/**
	 * The entry point of the Subscriber.
	 * otherwise you will end up in an infinite loop.
	 */
	@Override
	public final void run() {
		messageBroker.register(this);
		initialize();
		System.out.println("Thread " + Thread.currentThread().getName() + " is running now");
		// loop
		while (!terminated) {
			try {
				Message msg = messageBroker.awaitMessage(this);
				if (msg != null) {
					Callback callback = callbacks.get(msg.getClass());
					if (callback != null) {
						callback.call(msg);
					}
				}
			} catch (InterruptedException e) {}
		}

		messageBroker.unregister(this);
		System.out.println("Thread " + Thread.currentThread().getName() + " is terminating now");
	}
}