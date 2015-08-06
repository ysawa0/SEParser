import java.util.ArrayList;

public class OutputWriter {

	private static ArrayList<String> log;
	
	public static void write(String str) {
		log.add(str);
	}
	
}
