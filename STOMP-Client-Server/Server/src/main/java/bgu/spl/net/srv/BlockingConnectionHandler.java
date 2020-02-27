package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.stomp.Connections;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final MessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private Connections<T> connections;
    private static AtomicInteger id = new AtomicInteger(0);
    private int myId;

    public BlockingConnectionHandler(
            Socket sock,
            MessageEncoderDecoder<T> reader,
            MessagingProtocol protocol,
            Connections<T> connections) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.connections = connections;
        myId = id.incrementAndGet();
        this.protocol.start(myId, connections);
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage,this);
                }
            }
            System.out.println("handler " + myId + " disconnected");
            connections.disconnect(myId);
            System.out.println("BlockingConnection Exiting Loop");

        } catch (IOException ex) {
            System.out.println("Read: " + ex);
        }
    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        try { //just for automatic closing
            if (msg != null) {
                synchronized (out) {
                    byte[] bytes = encdec.encode(msg);
                    out.write(bytes);
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Send: " + e);
        }
    }

}