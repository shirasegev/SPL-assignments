package bgu.spl.mics;

public class testEvent implements Event<String> {
    private String senderName;

    public testEvent(String senderName) {
        this.senderName = senderName;
    }
}