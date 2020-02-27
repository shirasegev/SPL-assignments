package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Squad;
import bgu.spl.mics.application.responses.*;

import static bgu.spl.mics.application.MI6Runner.incrementCompleteInit;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

	private int moneypennyNum;
	private int tickLongInMilli;

	public Moneypenny(int moneypenny, int tickLongInMilli) {
		super("Moneypenny " + moneypenny);
		moneypennyNum = moneypenny;
		this.tickLongInMilli = tickLongInMilli;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, tick -> {
			if (tick.terminate()) {
				terminate();
			}
		});

		subscribeEvent(AgentsAvailableEvent.class, event -> {

			// Check availability in Squad
			AgentAvailableResponse agentAvailableResponse = new AgentAvailableResponse(moneypennyNum,
												Squad.getInstance().getAgents(event.getAgentSerialNumbers()));
			if (agentAvailableResponse.isAvailable()) {
				agentAvailableResponse.setAgentNames(Squad.getInstance().getAgentsNames(event.getAgentSerialNumbers()));
			}
			complete(event, agentAvailableResponse);
			// Return names only of agents actually exists
			if (agentAvailableResponse.isAvailable()) {
				AgentOperationResponse agentOperationResponse = agentAvailableResponse.getOperationResponse().get();
				AgentOperationResponse answerOfOperation = new AgentOperationResponse(agentOperationResponse.getMissionInfo());

				if (agentOperationResponse.toRelease()) {
					Squad.getInstance().releaseAgents(agentOperationResponse.getMissionInfo().getSerialAgentsNumbers());
					answerOfOperation.setOperationStatus(false);
				}
				else {
					Squad.getInstance().sendAgents(agentOperationResponse.getMissionInfo().getSerialAgentsNumbers(),
							agentOperationResponse.getMissionInfo().getDuration() * tickLongInMilli);
					answerOfOperation.setOperationStatus(true);
				}
				agentOperationResponse.getOperationResponse().resolve(answerOfOperation);
			}
		});
		incrementCompleteInit();
	}
}