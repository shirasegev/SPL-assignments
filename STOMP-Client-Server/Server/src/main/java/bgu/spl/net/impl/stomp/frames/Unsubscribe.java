package bgu.spl.net.impl.stomp.frames;

import bgu.spl.net.impl.stomp.ConnectionsImpl;
import bgu.spl.net.impl.stomp.StompClientFrame;

public class Unsubscribe extends StompClientFrame {

    public Unsubscribe(String frame) {
        super(frame);
    }

    @Override
    public boolean process(ConnectionsImpl connections, int connectionId) {
        if(loggedIn(connections,connectionId)) {
            String subscriptionId = getHeader("id");

            // Make sure valid frame headers
            if (subscriptionId == null) {
                String details = getDetails("id");
                Error error = new Error("malformed frame received", "", toString(), details);
                connections.send(connectionId, error);
                return false;
            }
            connections.removeSubscription(connectionId, subscriptionId);
            // return if no subscription exist
            return true;
        }
        connections.send(connectionId,new Error("User is not logged in", getHeader("receipt"), toString()));
        return false;

    }

}