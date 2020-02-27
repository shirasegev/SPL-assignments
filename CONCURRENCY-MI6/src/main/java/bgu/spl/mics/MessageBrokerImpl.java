package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.*;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

	// Hold all the subscribers
	private ConcurrentMap<Subscriber,SubscriberData> subscribers;

	// Hold a map of event types to subscribers
	private ConcurrentMap<Class, EventTypeSubscribers> eventTypeSubscribers;

	// Hold a map of broadcast type to subscribers
	private ConcurrentMap<Class, LinkedList<Subscriber>> broadcastTypeSubscribers;

	// Holds a mapping between the event to the relevant future object
	private ConcurrentMap<Event, Future> futures;

	// Private constructor so that the MB can't be created outside getInstance()
	private MessageBrokerImpl() {
		subscribers = new ConcurrentHashMap<>();
		eventTypeSubscribers = new ConcurrentHashMap<>();
		broadcastTypeSubscribers = new ConcurrentHashMap<>();
		futures = new ConcurrentHashMap<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBroker getInstance() {

		// MessageBrokerImpl is a singleton.
		// The first time getInstance() method is called,
		// we want to update instance by creating a new MessageBrokerImpl.
		// While doing it, we want to make sure it is only created once.
		return singletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {

		// Add the event type to the list of subscribed events for Subscriber m
		SubscriberData subData = subscribers.get(m);
		if (!subData.eventTypes.contains(type)) {
			subData.eventTypes.add(type);
		}

		// Add the subscriber to the list of event type subscribers
		EventTypeSubscribers eventSubscribers;
		synchronized (eventTypeSubscribers) {
			eventSubscribers = eventTypeSubscribers.get(type);
			if (eventSubscribers == null) {
				eventSubscribers = new EventTypeSubscribers();
				eventTypeSubscribers.put(type, eventSubscribers);
			}
		}

		// Synchronize on the linked list of all subscribers to a specific event type
		synchronized (eventSubscribers) {
			eventSubscribers.subscribers.add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {

		// Add the broadcast type to the list of subscribed broadcasts for Subscriber m
		SubscriberData subData = subscribers.get(m);
		subData.broadcastTypes.add(type);

		// Add the subscriber to the list of broadcast type subscribers
		LinkedList<Subscriber> broadcastSubscribers;
		synchronized (broadcastTypeSubscribers) {
			broadcastSubscribers = broadcastTypeSubscribers.get(type);
			if (broadcastSubscribers == null) {
				broadcastSubscribers = new LinkedList<>();
				broadcastTypeSubscribers.put(type, broadcastSubscribers);
			}
		}

		// Synchronize on the linked list of all subscribers to a specific broadcast type
		synchronized (broadcastSubscribers) {
			broadcastSubscribers.add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future f = futures.remove(e);
		f.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		LinkedList<Subscriber> broadcastSubscribers = broadcastTypeSubscribers.get(b.getClass());
			synchronized (broadcastSubscribers) {
				for (Subscriber sub : broadcastSubscribers) {
					try {
						subscribers.get(sub).queue.put(b);
					} catch (InterruptedException e) {
					}
				}
			}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = null;
		EventTypeSubscribers eventSubscribers = eventTypeSubscribers.get(e.getClass());

		if (eventSubscribers != null) {
			synchronized (eventSubscribers) {
				if (!eventSubscribers.subscribers.isEmpty()) {
					Subscriber sub = eventSubscribers.subscribers.get(eventSubscribers.next);
					eventSubscribers.next = (eventSubscribers.next + 1) % eventSubscribers.subscribers.size();
					future = new Future<>();
					futures.put(e, future);
					try {
						subscribers.get(sub).queue.put(e);
					} catch (InterruptedException ex) {}
				}
			}
		}
		return future;
	}

	/**
	 *
	 * @param m
	 * @POST A new queue was created for the subscriber who called this method
	 * 		in order to register itself
	 */
	@Override
	public void register(Subscriber m) {
		subscribers.put(m, new SubscriberData());
	}

	@Override
	public void unregister(Subscriber m) {
		SubscriberData subData = subscribers.get(m);

		// Remove sub from list of events and broadcasts
		if (subData != null) {
			synchronized (subData) {
				System.out.println("Thread " + Thread.currentThread().getName() + " operate unregister");
				for (Object b : subData.broadcastTypes) {
					LinkedList<Subscriber> broadcastSubscribers = broadcastTypeSubscribers.get(b);
					synchronized (broadcastSubscribers) {
						broadcastSubscribers.remove(m);
					}
				}
				subData.broadcastTypes.clear();

				// Empty the queue & complete all events that were left inside
				for (Object e : subData.eventTypes) {
					EventTypeSubscribers eventSubscribers = eventTypeSubscribers.get(e);
					synchronized (eventSubscribers) {
						int removedSubscriberIndex = eventSubscribers.subscribers.indexOf(m);
						eventSubscribers.subscribers.remove(m);

						// Adjust the next subscriber to receive event in a round robin manner
						if (removedSubscriberIndex < eventSubscribers.next) {
							eventSubscribers.next--;
						}

						// Removed last subscriber
						else if (eventSubscribers.next >= eventSubscribers.subscribers.size()) {
							eventSubscribers.next = 0;
						}
					}
				}
				subData.eventTypes.clear();

				for (Message msg: subData.queue) {
					if (msg instanceof Event){
						complete((Event)msg,null);
					}
				}
				subData.queue.clear();
				subscribers.remove(m);
			}
		}
	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		SubscriberData subData = subscribers.get(m);
		if (subData == null) {
			throw new IllegalStateException("Subscriber is not registered");
		}
		return subData.queue.take();
	}

	private class SubscriberData {
		List eventTypes = Collections.synchronizedList(new ArrayList<Class<? extends Event>>());
		List broadcastTypes = Collections.synchronizedList(new ArrayList<Class<? extends Broadcast>>());
		BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
	}

	private class EventTypeSubscribers {
		ArrayList<Subscriber> subscribers = new ArrayList<>();
		int next = 0;
	}

	private static class singletonHolder {
		private static MessageBrokerImpl instance = new MessageBrokerImpl();
	}
}