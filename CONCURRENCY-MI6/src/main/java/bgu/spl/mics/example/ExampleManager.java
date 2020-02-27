package bgu.spl.mics.example;

import bgu.spl.mics.example.subscribers.ExampleBroadcastSubscriber;
import bgu.spl.mics.example.publishers.ExampleMessageSender;
import bgu.spl.mics.example.subscribers.ExampleEventHandlerSubscriber;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ExampleManager {

    public static void main(String[] args) {
        Map<String, Creator> creators = new HashMap<>();
        creators.put("ev-handler", ExampleEventHandlerSubscriber::new);
        creators.put("brod-listener", ExampleBroadcastSubscriber::new);
        creators.put("sender", ExampleMessageSender::new);

        Scanner sc = new Scanner(System.in);
        boolean quit = false;
        try {
            System.out.println("Example manager is started - supported commands are: start,quit");
            System.out.println("Supporting apps: " + creators.keySet());
            while (!quit) {

                String line = sc.nextLine();
                String[] params = line.split("\\s+");

                if (params.length > 0) {

                    switch (params[0]) {
                        case "start":
                            try {
                                if (params.length < 3) {
                                    throw new IllegalArgumentException("Expecting app type and id, supported types: " + creators.keySet());
                                }
                                Creator creator = creators.get(params[1]);
                                if (creator == null) {
                                    throw new IllegalArgumentException("unknown app type, supported types: " + creators.keySet());
                                }

                                new Thread(creator.create(params[2], Arrays.copyOfRange(params, 3, params.length))).start();
                            } catch (IllegalArgumentException ex) {
                                System.out.println("Error: " + ex.getMessage());
                            }

                            break;
                        case "quit":
                            quit = true;
                            break;
                    }
                }
            }
        } catch (Throwable t) {
            System.err.println("Unexpected Error!!!!");
            t.printStackTrace();
        } finally {
            System.out.println("Manager Terminating - UNGRACEFULLY!");
            sc.close();
            System.exit(0);
        }
    }
}
