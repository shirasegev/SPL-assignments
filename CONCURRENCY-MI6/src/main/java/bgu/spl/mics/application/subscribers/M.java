package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.responses.*;

import static bgu.spl.mics.application.MI6Runner.incrementCompleteInit;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {

	private int mNum;

	private int currTime = 0;

	public M(int m) {
		super("M " + m);
		mNum = m;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, tick -> {
			currTime = tick.getTime();
			if (tick.terminate()) {
				terminate();
			}
		});

		subscribeEvent(MissionReceivedEvent.class, event -> {
			Diary.getInstance().incrementTotal();
			// Create and send to Moneypenny, via the message broker, AgentsAvailableEvent
			AgentsAvailableEvent agentAvailableEvent =
					new AgentsAvailableEvent(event.getMissionInfo().getSerialAgentsNumbers());
			Future<AgentAvailableResponse> agentFuture = getSimplePublisher().sendEvent(agentAvailableEvent);
			if (agentFuture != null) {
				AgentAvailableResponse agentAvailableResponse = agentFuture.get();
				if(agentAvailableResponse == null) {
					terminate();
				}
				else {
					if (agentAvailableResponse.isAvailable()) {
						// Create and send to Q, via the message broker, GadgetAvailableEvent
						GadgetAvailableEvent gadgetAvailableEvent = new GadgetAvailableEvent(event.getMissionInfo().getGadget());
						Future<GadgetAvailableResponse> gadgetFuture = getSimplePublisher().sendEvent(gadgetAvailableEvent);
						if (gadgetFuture == null) {
							AgentOperationResponse agentOperationResponse = new AgentOperationResponse(event.getMissionInfo());
							agentOperationResponse.setRelease(true);
							agentAvailableResponse.getOperationResponse().resolve(agentOperationResponse);
						}
						else {
							GadgetAvailableResponse gadgetAvailableResponse = gadgetFuture.get();
							AgentOperationResponse agentOperationResponse = new AgentOperationResponse(event.getMissionInfo());
							if(gadgetAvailableResponse == null) {
								terminate();
							}
							else {
								if (gadgetAvailableResponse.isAvailable() && event.getMissionInfo().getTimeExpired() > currTime) {
									// Sends agents on a mission
									agentOperationResponse.setRelease(false);
								}
								else {
									// If there is no available gadget, or time, release the agents
									agentOperationResponse.setRelease(true);
								}
								agentAvailableResponse.getOperationResponse().resolve(agentOperationResponse);

								AgentOperationResponse operationResponse = agentOperationResponse.getOperationResponse().get();
								if (operationResponse.getOperationStatus()) {
									// Create a new Report and add it to the Diary
									createReport(event, agentAvailableResponse, gadgetAvailableResponse);
								}
							}
						}
					}
				}
			}
		});
		incrementCompleteInit();
	}

	private void createReport(MissionReceivedEvent event, AgentAvailableResponse agentAvailableResponse,
							  GadgetAvailableResponse gadgetAvailableResponse) {

		MissionInfo missionInfo = event.getMissionInfo();

		Report report = new Report();

		report.setAgentsNames(agentAvailableResponse.getAgentNames());
		report.setAgentsSerialNumbers(missionInfo.getSerialAgentsNumbers());
		report.setGadgetName(missionInfo.getGadget());
		report.setM(mNum);
		report.setMissionName(missionInfo.getMissionName());
		report.setMoneypenny(agentAvailableResponse.getMoneypenny());
		report.setQTime(gadgetAvailableResponse.getTime());
		report.setTimeIssued(missionInfo.getTimeIssued());
		report.setTimeCreated(currTime);
		Diary.getInstance().addReport(report);
	}
}