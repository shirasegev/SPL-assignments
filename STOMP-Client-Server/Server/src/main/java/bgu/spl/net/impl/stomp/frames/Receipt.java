package bgu.spl.net.impl.stomp.frames;

import bgu.spl.net.impl.stomp.StompServerFrame;

public class Receipt extends StompServerFrame {

    public Receipt(String receiptId) {
        super("RECEIPT", "");
        headers.put("receipt-id", receiptId);
    }
}