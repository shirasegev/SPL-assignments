package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.stomp.frames.Receipt;
import bgu.spl.net.srv.ConnectionHandler;

public class StompMessagingProtocol implements MessagingProtocol<StompFrame> {
	private boolean shouldTerminate = false;
	private ConnectionsImpl connections;
	private int connectionId;

	@Override
	public void start(int connectionId, Connections<StompFrame> connections) {
		this.connections = (ConnectionsImpl)connections;
		this.connectionId = connectionId;
	}

	// execute
	public void process(StompFrame message, ConnectionHandler connectionHandler) {
		if(message.getCommand().equals("CONNECT")) {
			connections.addConnectionHandler(connectionId, connectionHandler);
		}
		boolean processed = ((StompClientFrame)message).process(connections, connectionId);

		// If a message received contains a "receipt" header, we should send back a Receipt frame
		if (processed) {
			String receiptId = message.getHeader("receipt");
			if (receiptId != null) {
				Receipt receipt = new Receipt(receiptId);
				connections.send(connectionId, receipt);
			}
			if(message.getCommand().equals("DISCONNECT")){
				shouldTerminate = true;
			}
		}

		else {
			shouldTerminate = true;
		}
	}

	@Override
	public boolean shouldTerminate() {
		return shouldTerminate;
	}

}