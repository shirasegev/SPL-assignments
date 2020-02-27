package bgu.spl.net.impl.stomp;

import bgu.spl.net.impl.stomp.frames.*;

public abstract class StompClientFrame extends StompFrame {

    // constructor
    public StompClientFrame(String frame) {
        super();
        String[] headerLines = frame.substring(0, frame.indexOf("\n\n")).split("\n");
        command = headerLines[0];
        for (int i = 1; i < headerLines.length; i++) {
            String[] headerParts = headerLines[i].split(":");
            headers.put(headerParts[0], headerParts[1]);
        }
        body = frame.substring(frame.indexOf("\n\n")+2);
        if(body.length() > 0) {
            while (body.charAt(body.length() - 1) == '\n') {
                body = body.substring(0, body.length() - 1);
            }
        }
    }

    public static StompClientFrame createFrame (String frame){

        StompClientFrame output = null;
        String command = frame.substring(0, frame.indexOf("\n"));

        if (command.equals("CONNECT")) {
            output = new Connect(frame);
        }
        else if (command.equals("SUBSCRIBE")){
            output = new Subscribe(frame);
        }
        else if(command.equals("UNSUBSCRIBE")){
            output = new Unsubscribe(frame);
        }
        else if(command.equals("SEND")){
            output = new Send(frame);
        }
        else if(command.equals("DISCONNECT")){
            output = new Disconnect(frame);
        }
        return output;
    }

    public boolean loggedIn(ConnectionsImpl connections, int connectionId){
        return connections.containsActiveClient(connectionId);
    }

    public String getDetails(String missingHeader){
        return ("Did not contain a " + missingHeader + " header, which is REQUIRED for message propagation");
    }

    abstract public boolean process(ConnectionsImpl connections, int id);
}