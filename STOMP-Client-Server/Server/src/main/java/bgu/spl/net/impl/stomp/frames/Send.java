package bgu.spl.net.impl.stomp.frames;

import bgu.spl.net.impl.stomp.ConnectionsImpl;
import bgu.spl.net.impl.stomp.StompClientFrame;

public class Send extends StompClientFrame {

    public Send(String frame){
        super(frame);
    }

    public boolean process(ConnectionsImpl connections, int connectionId){
        if(loggedIn(connections,connectionId)) {
            String topic = getHeader("destination");
            if(topic == null) {
                String details = getDetails("destination");
                Error error = new Error("malformed frame received", getHeader("receipt"), toString(), details);
                connections.send(connectionId, error);
                return false;
            }
            if (connections.topicExist(topic)) {
                Message message = new Message(topic, getBody());
                connections.send(topic, message);
                return true;
            }
            connections.send(connectionId,new Error("Topic does not exist", getHeader("receipt"), toString()));
            return false;
        }
        connections.send(connectionId,new Error("User is not logged in", getHeader("receipt"), toString()));
        return false;
    }
}