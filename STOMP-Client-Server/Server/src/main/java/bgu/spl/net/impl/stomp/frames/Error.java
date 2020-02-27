package bgu.spl.net.impl.stomp.frames;

import bgu.spl.net.impl.stomp.StompServerFrame;

public class Error extends StompServerFrame {

    public Error(String errorMsg, String receiptId, String body) {
        super("ERROR", body);
        headers.put("message", errorMsg);

        // another option is to pass the client message
        if (receiptId != null) {
            headers.put("receipt-id", receiptId);
        }
    }

    public Error(String errorMsg, String receiptId, String body, String details) {
        super("ERROR", "The message:" + '\n' + "-----" + '\n' + body + '\n' + "-----" + '\n' + details);

        headers.put("message", errorMsg);

        // another option is to pass the client message
        if (receiptId != null) {
            headers.put("receipt-id", receiptId);
        }
    }

}