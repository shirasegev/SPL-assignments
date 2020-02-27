package bgu.spl.net.api;

import bgu.spl.net.impl.stomp.Connections;
import bgu.spl.net.srv.ConnectionHandler;

public interface MessagingProtocol<T> {
	/**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
	**/
    void start(int connectionId, Connections<T> connections);
    
    void process(T message, ConnectionHandler<T> connectionHandler);
	
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
}