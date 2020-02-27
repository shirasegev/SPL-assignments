package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.OutputGenerator;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {

	private List<Report> reports;

	private static AtomicInteger totalNum;

	// Private constructor so that the Diary can't be created outside getInstance()
	private Diary() {
		reports = new LinkedList<>();
		totalNum = new AtomicInteger(0);
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Diary getInstance() {
		return singletonHolder.instance;
	}

	public List<Report> getReports() {
		return reports;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public synchronized void addReport(Report reportToAdd){
		reports.add(reportToAdd);
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename) {
		OutputGenerator.printDiary(filename, reports);
	}

	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return totalNum.get();
	}

	/**
	 * Increments the total number of received missions by 1
	 */
	public void incrementTotal() {
		totalNum.incrementAndGet();
	}

	private static class singletonHolder {
		private static Diary instance = new Diary();
	}
}