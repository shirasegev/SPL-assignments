package bgu.spl.mics;

/**
 * The Publisher is an abstract class that any publisher in the system
 * must extend. The abstract Publisher class is responsible to send
 * messages to the singleton {@link MessageBroker} instance.
 * <p>
 * Derived classes of Publisher should never directly touch the MessageBroker.
 * method).
 *
 * Only private fields and methods may be added to this class.
 * <p>
 */
public abstract class Publisher extends RunnableSubPub {

    /**
     * @param name the Publisher name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public Publisher(String name) {
        super(name);
    }
}