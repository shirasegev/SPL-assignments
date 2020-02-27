package bgu.spl.net.impl.stomp.frames;

import bgu.spl.net.impl.stomp.ConnectionsImpl;
import bgu.spl.net.impl.stomp.StompClientFrame;

public class Disconnect extends StompClientFrame {

    public Disconnect(String frame) {
        super(frame);
    }

    @Override
    public boolean process(ConnectionsImpl connections, int connectionId) {

        String receipt = getHeader("receipt");

        // Make sure valid frame headers
        if (receipt == null) {
            String details = getDetails("receipt");
            Error error = new Error("malformed frame received", receipt, toString(), details);
            connections.send(connectionId, error);
            return false;
        }
        if(loggedIn(connections,connectionId)) {
            return true;
        }
        connections.send(connectionId,new Error("User is not logged in", receipt, toString()));
        return false;
    }
}