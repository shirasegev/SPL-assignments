package bgu.spl.net.impl.stomp.frames;

import bgu.spl.net.impl.stomp.StompServerFrame;

public class Message extends StompServerFrame {

    public Message(String destination, String body) {
        super("MESSAGE", body);
        headers.put("destination", destination);
    }
}