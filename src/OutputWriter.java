import java.util.ArrayList;

import edu.stanford.nlp.trees.Tree;

// Class that takes in output from other classes and saved them into an ArrayList
// After all the parsing is done, call printAll to print to system.out
public class OutputWriter {

	private static ArrayList<String> log;
	private static ArrayList<String> overallResultsLog; // holds the overall results, like total questions detected
	// initialize the ArrayList
	static {
		log = new ArrayList<String>();
		overallResultsLog = new ArrayList<String>();
		overallResultsLog.add("\nOverall Results/Totals");
	}
	
	public static void writeOverallResults(String str) {
		overallResultsLog.add(str);
	}
	
	public static void write() {
		log.add("\n");
	}
	
	public static void write(String str) {
		log.add(str);
	}

	public static void write(Tree tree) {
		log.add(tree.toString());
	}
	
	public static void printAll() {
		log.forEach((str) -> System.out.println(str)); 
		overallResultsLog.forEach((str) -> System.out.println(str));
	}
}
