import java.util.ArrayList;

import edu.stanford.nlp.trees.Tree;

public class OutputWriter {

	private static ArrayList<String> log;
	
	public static void init() {
		log = new ArrayList<String>();
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
}
