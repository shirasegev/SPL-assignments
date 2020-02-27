package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.ConnectionHandler;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void send(String channel, T msg);

    void disconnect(int connectionId);

    void addConnectionHandler(int connectionId, ConnectionHandler handler);

    void removeConnectionHandler(int connectionId);

    void addSubscription(int connectionId, String subscriptionId, String topic);

    void removeSubscription(int connectionId, String subscriptionId);
}