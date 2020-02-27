package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBrokerTest {

    MessageBroker msgB;
    Subscriber m;
    Future<String> fu;
    testEvent tEvent;
    testBroadcast tBroadcast;
    Message getEvent;

    @BeforeEach
    public void setUp() {
        msgB = MessageBrokerImpl.getInstance();
        tEvent = new testEvent("TestEvent");
        tBroadcast = new testBroadcast("TestBroadcast");
        fu = new Future<>();
    }

    @Test
    public void TestEventProcess() {
        msgB.subscribeEvent(testEvent.class,m);
        msgB.sendEvent(tEvent);
        assertDoesNotThrow(()->getEvent = msgB.awaitMessage(m));
        assertNotNull(getEvent,"awaitMessage() did'nt return message");
        assertSame(getEvent , tEvent , "awaitMessage() did'nt return right message.");
    }

    @Test
    public void TestBroadcastProcess() {
        // Check if the subscriber was really subscribed for the wanted type of broadcast
        msgB.subscribeBroadcast(testBroadcast.class,m);
        msgB.sendBroadcast(tBroadcast);
        assertDoesNotThrow(()->getEvent = msgB.awaitMessage(m));
        assertNotNull(getEvent,"awaitMessage() did'nt return message");
        assertSame(getEvent , tBroadcast , "awaitMessage() did'nt return right message.");
    }

    @Test
    public void complete() {
        msgB.subscribeEvent(testEvent.class,m);
        fu = msgB.sendEvent(tEvent);
        msgB.complete(tEvent,"success");
        assertNotNull(fu,"there was subscriber and yet return null");
        assertEquals("success",fu.get(),"fu did'nt resolved good");
    }

    @Test
    public void register() {
        assertDoesNotThrow(()->msgB.register(null));
        assertDoesNotThrow(()->msgB.register(m));
    }

    @Test
    public void unregister() {
        assertDoesNotThrow(()->msgB.register(m));
        assertDoesNotThrow(()->msgB.unregister(m));
    }
}