package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.*;

public class StompServer {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("you must supply two arguments: port, server");
            System.exit(1);
        }

        Server server = null;
        if(args[1].equals("tpc")) {
            server = new ThreadPerClientServer<StompFrame>(Integer.parseInt(args[0]),
                    () -> new StompMessagingProtocol(),
                    () -> new StompEncoderDecoder());
        }
        else if(args[1].equals("reactor")){
            server = new Reactor<>(4, Integer.parseInt(args[0]),
                    () -> new StompMessagingProtocol(),
                    () -> new StompEncoderDecoder());
        }
        server.serve();

    }
}