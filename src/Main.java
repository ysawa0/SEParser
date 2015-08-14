


import java.util.ArrayList;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class Main {
	private static ArrayList<String> overallResults = new ArrayList<String>();
	
	private static LexicalizedParser lp;
	
	// Main method 
	// input - the input text file. Each sentence should be on its own line and end with a period
	// lp - the Parser Model 
	public static void main(String[] args) {
		
		String input = "input.txt"; // Example input for DEFCON presentation
		input = "supcourt.txt";
		String ex = "defcon_ex.txt";
		String allattacks = "all attacks.txt";
		lp = LexicalizedParser.loadModel("englishPCFG.ser.gz");
		
		String supcourt = "supcourt_splitbyperiod.txt";
//		questionParse(input, 6);
//		softCommandParse(input, 3);
//		hardCommandParse(input, 3);
		int numOfParses = 3;
		
		OutputWriter.writeOverallResults("\n# of parses used: " + numOfParses);
		ArrayList<Sentence> sentList = ParseTreeMaker.makeParseTrees(supcourt, lp, numOfParses);
		questionParse(input, 1, sentList);
		softCommandParse(input, 1, sentList);
		hardCommandParse(input, 1, sentList);
		
		OutputWriter.printAll();
		
		overallResults.forEach(System.out::println);
	}
	
	private static void addResults(int numberResults) {
		
	}
	
	// Analyzes the input for questions and see if any of them are malicious
	public static void questionParse(String filename, int numParses, ArrayList<Sentence> sentList) {
		QParser qp = new QParser(filename,lp, numParses, 0, sentList);
		TParser tp;
		OutputWriter.write("\n---Question parse start--- " + numParses + " parses \n");
		OutputWriter.write("Questions detected: " + qp.qList.size());

		for(Sentence s :qp.detectedList) {
			tp = new TParser(s);
			tp.SQParse();
			printSentences(s);
			PairChecker pairCheck = new PairChecker(s);
			pairCheck.blacklistCheck2();
		}
		
		OutputWriter.writeOverallResults("# questions: " + qp.qList.size());
	}

	// Analyzes the input for Soft Commands and see if any of them are malicious
	public static void softCommandParse(String filename, int numParses, ArrayList<Sentence> sentList) {
		OutputWriter.write("\n---Soft Commands parse start--- " + numParses + " parses \n");

		QParser qp = new QParser(filename,lp, numParses, 1, sentList);
		OutputWriter.write("Soft commands detected: " + qp.softList.size());
       
		for(Sentence s : qp.softList) {
			TParser tp = new TParser(s);
			tp.CommandParse();
			
			printSentences(s);
			PairChecker pairCheck = new PairChecker(s);
			pairCheck.blacklistCheck2();
			
		}
		OutputWriter.writeOverallResults("# soft commands: " + qp.softList.size());

	}
	
	// Analyzes the input for Hard Commands and see if any of them are malicious
	public static void hardCommandParse(String filename, int numParses, ArrayList<Sentence> sentList) {
		OutputWriter.write("\n---Direct Commands parse start--- " + numParses + " parses \n");
		
		QParser qp = new QParser(filename,lp, numParses,2, sentList);
		OutputWriter.write("Direct commands detected: " + qp.hardList.size());
		
		for(Sentence s : qp.hardList) {
			TParser tp = new TParser(s);
			tp.CommandParse();
			
			printSentences(s);
			PairChecker pairCheck = new PairChecker(s);
			pairCheck.blacklistCheck2();
			

		}
		
		OutputWriter.writeOverallResults("# direct commands: " + qp.hardList.size());
	}
	
	public static void printSentences(Sentence s) {
		OutputWriter.write("\nSentence: " + s.sent);
		if (s.sent.equals("Click run.")) {
//			OutputWriter.write(s.kBest.get(1).object().skipRoot());
		}
		else {
//			OutputWriter.write(s.kBest.get(0).object().skipRoot());
		}
	}
		


}
