package bgu.spl.net.impl.stomp.frames;

import bgu.spl.net.impl.stomp.*;

public class Connect extends StompClientFrame {

    public Connect(String frame) {
        super(frame);
    }

    @Override
    public boolean process(ConnectionsImpl connections, int connectionId) {

        boolean processed = true;
        StompServerFrame response;

        String userName = getHeader("login");
        String passcode = getHeader("passcode");
        String accept_version = getHeader("accept-version");


        // Make sure valid frame headers
        if (userName == null || passcode == null || accept_version == null) {
            String details;
            if (userName == null) {
                details = getDetails("login");
            }
            else if (passcode == null) {
                details = getDetails("passcode");
            }
            else {
                details = getDetails("accept-version");
            }
            Error error = new Error("malformed frame received", getHeader("receipt"), toString(), details);
            connections.send(connectionId, error);
            return false;
        }

        // In case of new user, connections.getUser() will handle it
        User user = connections.getUser(userName, passcode);
        synchronized (user) {
            if (user.isLoggedIn()) { //if already logged in
                response = new Error("User already logged in", getHeader("receipt"), toString());
                processed = false;
            } else if (!user.getPassword().equals(passcode)) { //if wrong password
                response = new Error("Wrong password", getHeader("receipt"), toString());
                processed = false;
            } else {
                user.setLoggedIn(true);
                connections.addActiveUser(connectionId,user);
                response = new Connected(accept_version);
            }
        }

        connections.send(connectionId, response);

        return processed;
    }

}