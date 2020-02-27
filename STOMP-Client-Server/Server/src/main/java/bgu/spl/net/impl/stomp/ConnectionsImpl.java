package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {
    private Map<Integer, ConnectionHandler<T>> activeClients;
    private Map<Integer, Map<String, String>> clientSubscriptionsById;
    private Map<Integer, Map<String, String>> clientSubscriptionsByTopic;
    private Map<String, Set<Integer>> topics;
    private Map<Integer, User> activeUsers;
    private Map<String, User> usersMap;

    private static int messageId = 0;

    public ConnectionsImpl() {
        topics = new ConcurrentHashMap<>();
        activeClients = new ConcurrentHashMap<>();
        usersMap = new ConcurrentHashMap<>();
        clientSubscriptionsById = new ConcurrentHashMap<>();
        clientSubscriptionsByTopic = new ConcurrentHashMap<>();
        activeUsers = new ConcurrentHashMap<>();
    }

    public boolean send(int connectionId, T msg) {
        ConnectionHandler<T> handler = activeClients.get(connectionId);
        if (handler != null) {
            handler.send(msg);
            return true;
        }
        return false;
    }

    public void send(String topic, T msg) {
        Set<Integer> topicSubscribers = topics.get(topic);
        // topicSubscribers is not null, handled at SEND
        StompFrame message = (StompFrame) msg;
        synchronized (topicSubscribers) {
            for (int id : topicSubscribers) {
                String subscriptionId = clientSubscriptionsByTopic.get(id).get(topic);
                message.addHeader("subscription", subscriptionId);
                message.addHeader("message-id", Integer.toString(messageId++));
                activeClients.get(id).send(msg);
            }
        }
    }

    @Override
    public synchronized void disconnect(int connectionId) {
        for (String topic : topics.keySet()) {
            topics.get(topic).remove(connectionId);
        }
        removeConnectionHandler(connectionId);
        if(activeUsers.get(connectionId) != null) {
            activeUsers.get(connectionId).setLoggedIn(false);
        }
        activeUsers.remove(connectionId);
        clientSubscriptionsById.remove(connectionId);
        clientSubscriptionsByTopic.remove(connectionId);
    }

    @Override
    public void addConnectionHandler(int connectionId, ConnectionHandler handler) {
        activeClients.put(connectionId, handler);
    }

    @Override
    public void removeConnectionHandler(int connectionId) {
        activeClients.remove(connectionId);
    }

    @Override
    public void addSubscription(int connectionId, String subscriptionId, String topic) {
        synchronized (topics) {
            Set<Integer> topicSubscribers = topics.get(topic);
            if (topicSubscribers == null) {
                topicSubscribers = new HashSet<>();
                topics.put(topic, topicSubscribers);
            }
            synchronized (topicSubscribers) {
                topicSubscribers.add(connectionId);
            }
        }

        synchronized (clientSubscriptionsById) {
            Map<String, String> clientSubsById = clientSubscriptionsById.get(connectionId);
            if (clientSubsById == null) {
                clientSubsById = new HashMap<>();
                clientSubscriptionsById.put(connectionId, clientSubsById);
            }
            clientSubsById.put(subscriptionId, topic);
        }

        synchronized (clientSubscriptionsByTopic) {
            Map<String, String> clientSubsByTopic = clientSubscriptionsByTopic.get(connectionId);
            if (clientSubsByTopic == null) {
                clientSubsByTopic = new HashMap<>();
                clientSubscriptionsByTopic.put(connectionId, clientSubsByTopic);
            }
            clientSubsByTopic.put(topic, subscriptionId);
        }
    }

    @Override
    public synchronized void removeSubscription(int connectionId, String subscriptionId) {
        Map<String, String> clientSubsById = clientSubscriptionsById.get(connectionId);
        if (clientSubsById != null) {
            String topic = clientSubsById.remove(subscriptionId);
            clientSubscriptionsByTopic.get(connectionId).remove(topic);
        }
    }

    public synchronized User getUser(String userName, String passcode) {
        User user = usersMap.get(userName);
        if (user == null) { // if new user
            user = new User(userName, passcode);
            addUser(user);
        }
        return user;
    }

    public boolean containsActiveClient(int id) {
        return activeClients.containsKey(id);
    }

    public boolean topicExist(String topic) {
        return topics.containsKey(topic);
    }

    public void addActiveUser(int connectionId, User user) {
        activeUsers.put(connectionId, user);
    }

    public void addUser(User user) {
        usersMap.put(user.getUserName(), user);
    }

}