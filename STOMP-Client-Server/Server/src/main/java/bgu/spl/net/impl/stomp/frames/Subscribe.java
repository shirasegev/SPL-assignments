package bgu.spl.net.impl.stomp.frames;

import bgu.spl.net.impl.stomp.ConnectionsImpl;
import bgu.spl.net.impl.stomp.StompClientFrame;

public class Subscribe extends StompClientFrame {

    public Subscribe(String frame) {
        super(frame);
    }

    @Override
    public boolean process(ConnectionsImpl connections, int connectionId) {

        if(loggedIn(connections,connectionId)) {
            String topic = getHeader("destination");
            String subscriptionId = getHeader("id");

            // Make sure valid frame headers
            if (topic == null || subscriptionId == null) {
                String details;
                if (topic == null) {
                    details = getDetails("destination");
                }
                else {
                    details = getDetails("id");
                }
                Error error = new Error("malformed frame received", "", toString(), details);
                connections.send(connectionId, error);
                return false;
            }
            connections.addSubscription(connectionId, subscriptionId, topic);

            return true;
        }
        connections.send(connectionId,new Error("User is not logged in", getHeader("receipt"), toString()));
        return false;
    }

}