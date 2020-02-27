package bgu.spl.mics.application.passiveObjects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	/**
	 * @INV Map.first.substring(0,2) == "00"
	 */
	private Map<String, Agent> agents;

	// Private constructor so that the Squad can't be created outside getInstance()
	private Squad() {
		agents = new HashMap<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 * @return instance of Squad
	 */
	public static Squad getInstance() {
		return singletonHolder.instance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 * @PRE Agents' array has an agent object at each cell
	 * @POST Agents' map (the private field of this class) contains all of the given agents
	 */
	public void load (Agent[] agents) {
		for (Agent agent: agents) {
			this.agents.put(agent.getSerialNumber(), agent);
		}
	}

	/**
	 * Releases agents.
	 * @PRE Serials' list contains legal serial numbers
	 * @POST All agents with the specified serials were released (each agent availability updated to "true")
	 */
	public void releaseAgents(List<String> serials){
		for (String serial: serials) {
			agents.get(serial).release();
		}
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   milliseconds to sleep
	 * @POST All agents with the specified serials were released (each agent availability updated to "true")
	 */
	public void sendAgents(List<String> serials, int time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {}

		releaseAgents(serials);
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){
		for (String serial: serials) {
			if (!agents.containsKey(serial)) {
				return false;
			}
		}

		// Potential deadlock here. So either sort the serials or synchronize on this block
		serials.sort(String.CASE_INSENSITIVE_ORDER);

		for (String serial : serials) {
			agents.get(serial).acquire();
		}
		return true;
	}

    /**
     * gets the agents names
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */
    public List<String> getAgentsNames(List<String> serials){
    	List<String> names = new LinkedList<>();
		for (String serial : serials) {
			names.add(agents.get(serial).getName());
		}
	    return names;
    }

	private static class singletonHolder {
		private static Squad instance = new Squad();
	}
}