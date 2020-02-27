package bgu.spl.net.impl.stomp.frames;
import bgu.spl.net.impl.stomp.StompServerFrame;

public class Connected extends StompServerFrame {

    public Connected(String version) {
        super("CONNECTED", "");
        if (version != null) {
            headers.put("version", version);
        }
    }
}