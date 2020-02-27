package bgu.spl.mics;

/**
 * The MessageBroker is a shared object used for communication between
 * Subscribers\Publishers.
 * It should be implemented as a thread-safe singleton.
 * The MessageBroker implementation must be thread-safe as
 * it is shared between all the Subscribers\Publishers in the system.
 * You must not alter any of the given methods of this interface. 
 * You cannot add methods to this interface.
 */
public interface MessageBroker {

    /**
     * Subscribes {@code s} to receive {@link Event}s of type {@code type}.
     * <p>
     * @param <T>  The type of the result expected by the completed event.
     * @param type The type to subscribe to,
     * @param s    The subscribing Subscriber.
     */
    <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber s);

    /**
     * Subscribes {@code s} to receive {@link Broadcast}s of type {@code type}.
     * <p>
     * @param type 	The type to subscribe to.
     * @param s    	The Subscriber.
     */
    void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber s);

    /**
     * Notifies the MessageBroker that the event {@code e} is completed and its
     * result was {@code result}.
     * When this method is called, the MessageBroker will resolve the {@link Future}
     * object associated with {@link Event} {@code e}.
     * <p>
     * @param <T>    The type of the result expected by the completed event.
     * @param e      The completed event.
     * @param result The resolved result of the completed event.
     */
    <T> void complete(Event<T> e, T result);

    /**
     * Adds the {@link Broadcast} {@code b} to the message queues of all the
     * Subscriber subscribed to {@code b.getClass()}.
     * <p>
     * @param b 	The message to added to the queues.
     */
    void sendBroadcast(Broadcast b);

    /**
     * Adds the {@link Event} {@code e} to the message queue of one of the
     * Subscriber subscribed to {@code e.getClass()} in a round-robin
     * fashion. This method should be non-blocking.
     * <p>
     * @param <T>    	The type of the result expected by the event and its corresponding future object.
     * @param e     	The event to add to the queue.
     * @return {@link Future<T>} object to be resolved once the processing is complete,
     * 	       null in case no Subscriber has subscribed to {@code e.getClass()}.
     */
    <T> Future<T> sendEvent(Event<T> e);

    /**
     * Allocates a message-queue for the {@link Subscriber} {@code s}.
     * <p>
     * @param s the Subscriber to create a queue for.
     */
    void register(Subscriber s);

    /**
     * Removes the message queue allocated to {@code s} via the call to
     * {@link #register(Subscriber)} and cleans all references
     * related to {@code s} in this MessageBroker. If {@code s} was not
     * registered, nothing should happen.
     * <p>
     * @param s the Subscriber to unregister.
     */
    void unregister(Subscriber s);

    /**
     * Using this method, a <b>registered</b> Subscriber can take message
     * from its allocated queue.
     * This method is blocking meaning that if no messages
     * are available in the Subscriber queue it
     * should wait until a message becomes available.
     * The method should throw the {@link IllegalStateException} in the case
     * where {@code s} was never registered.
     * <p>
     * @param s The Subscriber requesting to take a message from its message
     *          queue.
     * @return The next message in the {@code s}'s queue (blocking).
     * @throws InterruptedException if interrupted while waiting for a message
     *                              to became available.
     */
    Message awaitMessage(Subscriber s) throws InterruptedException;
    
}