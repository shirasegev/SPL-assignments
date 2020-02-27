package bgu.spl.net.impl.stomp;

public abstract class StompServerFrame extends StompFrame {

    // constructor
    public StompServerFrame(String command, String body) {
        super();
        this.command = command;
        this.body = body;
    }
}