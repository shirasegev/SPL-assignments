package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.OutputGenerator;

import java.util.*;

/**
 *  That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

	/**
	 * @INV gadgets' list contains "simply a String" for each gadget
	 */
	private List<String> gadgets;

	// Private constructor so that the Inventory can't be created outside getInstance()
	private Inventory() {
		gadgets = new ArrayList<>();
	}

	/**
     * Retrieves the single instance of this class.
	 * @return instance of Inventory
     */
	public static Inventory getInstance() {
		return singletonHolder.instance;
	}

	/**
     * Initializes the inventory. This method adds all the items given to the gadget
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
	 * @PRE inventory' array has an gadget object at each cell
	 * @POST gadgets' list (the private field of this class) contains all of the given gadgets
     */
	public void load (String[] inventory) {
		for (String gadget: inventory) {
			gadgets.add(gadget);
		}
	}
	
	/**
     * acquires a gadget and returns 'true' if it exists.
     * <p>
     * @param gadget 		Name of the gadget to check if available
     * @return 	‘false’ if the gadget is missing, and ‘true’ otherwise
	 * @POST The given gadget is removed from gadgets list
     */
	public synchronized boolean getItem(String gadget){
		return gadgets.remove(gadget);
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<String> which is a
	 * list of all the of the gadgeds.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename) {
		OutputGenerator.printInventory(filename, gadgets);
	}

	private static class singletonHolder {
		private static Inventory instance = new Inventory();
	}

}