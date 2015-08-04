

import java.util.ArrayList;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.ScoredObject;

public class Main {

	private static LexicalizedParser lp;
	
	// Main method, accepts 
	public static void main(String[] args) {
		
		String input = "input.txt";
		String ex = "defcon_ex.txt";
		String allattacks = "all attacks.txt";
		lp = LexicalizedParser.loadModel("englishPCFG.ser.gz");
		
		questionParse(allattacks, 6);
		softParse(allattacks, 3);
		hardParse(allattacks, 3);
		//		doQuestionParse(lp, inputTextFile, 6);
	}
	
	// Analyzes the input for questions and see if any of them are malicious
	public static void questionParse(String filename, int numParses) {
		QParser qp = new QParser(filename,lp, numParses,0);
		TParser tp;
		System.out.println();
		System.out.println("---Question parse start--- " + numParses + " parses \n");

		for(Sentence s :qp.detectedList) {
			tp = new TParser(s);
			tp.SQParse();
			printSent2(s);
			PairChecker pairCheck = new PairChecker(s);
			pairCheck.blacklistCheck2();
		}
	}

	// Analyzes the input for Soft Commands and see if any of them are malicious
	public static void softParse(String filename, int numParses) {
		System.out.println();
		System.out.println("---Soft Commands parse start--- " + numParses + " parses \n");

		QParser qp = new QParser(filename,lp, numParses,1);
		System.out.println("Soft commands detected: " + qp.softList.size());
       
		for(Sentence s : qp.softList) {
			TParser tp = new TParser(s);
			tp.CommandParse();
			
			printSent2(s);
			PairChecker pairCheck = new PairChecker(s);
			pairCheck.blacklistCheck2();
			
//			for (VerbNounPair pair : s.softWords) {
//				System.out.println("Verb: " + pair.verb);
//				System.out.println("Noun: " + pair.nouns);
//			}

		}
	}
	
	// Analyzes the input for Hard Commands and see if any of them are malicious
	public static void hardParse(String filename, int numParses) {
		System.out.println();
		System.out.println("---Direct Commands parse start--- " + numParses + " parses \n");
		System.out.println();
		
		QParser qp = new QParser(filename,lp, numParses,2);
		System.out.println("Direct commands detected: " + qp.hardList.size());
		
		for(Sentence s : qp.hardList) {
			TParser tp = new TParser(s);
			tp.CommandParse();
			
			printSent2(s);
			PairChecker pairCheck = new PairChecker(s);
			pairCheck.blacklistCheck2();
			

		}

		
	}
	public static void doDetailedQuestionParse(LexicalizedParser lp, String filename, int numParses) {
		System.out.println();
		System.out.println("---cleanParse method start---" + numParses + " parses");
		System.out.println();
		
		QParser qp = new QParser(filename,lp, numParses,0);
		System.out.println("CORRECTLY DETECTED:");
		for(Sentence s : qp.correctList) {
			printSent(s);
		}
		System.out.println();
		System.out.println("FALSE NEGATIVES:");
		for(Sentence s : qp.fnList) {
			printSent(s);
		}
		System.out.println();
		System.out.println("FALSE POSITIVES:");
		for(Sentence s : qp.fpList) {
			printSent(s);
		}
		qp.print();
	}
	
	
	public static void deepParse(LexicalizedParser lp, String filename, int numParses, int parseType) {
		QParser qp = new QParser(filename,lp, numParses,parseType);
		TParser tp;
		System.out.println();
		System.out.println("---deepParse method start---" + " " + filename);
		System.out.println();
		System.out.println("CORRECTLY DETECTED:");
		for(Sentence s : qp.correctList) {
			printSent(s);
		}
		for(Sentence s :qp.sList) {
			tp = new TParser(s);
			tp.SQParse();
			PairChecker pairCheck = new PairChecker(s);
			pairCheck.blacklistCheck();
		}
	}
	
	public static void printEveryParse(String filename, int numParses) {
		// print every parse for every sentence and that's it
		System.out.println();
		System.out.println("---allParse method start---");
		System.out.println();
		
		QParser qp = new QParser(filename,lp, numParses,0);
		System.out.println("ALL SENTENCES:");
		for(Sentence s : qp.allList) {
			System.out.println(s.sent);
			System.out.println(s.kBest.get(0).object());
			System.out.println();
		}

		
		qp.print();
	}
	public static void allParse(String filename, int numParses) {
		// Parse, Detect and Analyze every question and command
		
		System.out.println();
		System.out.println("---Direct Commands parse start--- " + numParses + " parses");
		System.out.println();

		QParser qp = new QParser(filename,lp, numParses,3);

		System.out.println("ALL Questions and Commands: " + qp.detectedList.size() + " detected " + numParses + " Parses");
		
			for(Sentence s : qp.detectedList) { 
				TParser tp = new TParser(s);
				tp.CommandParse();
			}

		
       	for(Sentence s : qp.detectedList) {
       		
			PairChecker pairCheck = new PairChecker(s);
			pairCheck.blacklistCheck();

			//System.out.println("-----");
		}
	}
	
	public static void getParse(String filename, int numParses) {
		System.out.println();
		System.out.println("---getParse method start---");
		System.out.println();
		
		QParser qp = new QParser(filename,lp, numParses,0);
		System.out.println("ALL SENTENCES:");
		for(Sentence s : qp.allList) {
			for (int i=0; i<numParses; i++) {
				System.out.println(s.kBest.get(i).object());
			}
			System.out.println(s.sent);
			if(s.sent.equals("And up in the top line, the address, type in FTP://update-google.com.")) {
				System.out.println(s.sent);
				for (int i=0; i<numParses; i++) {
					System.out.println(s.kBest.get(i).object());
				}
				System.out.println();
			}
			if(s.sent.equals("And up in the top line, the address, type_VB in \"ftp://update-google.com.\"")) {
				System.out.println(s.sent);
				for (int i=0; i<numParses; i++) {
					System.out.println(s.kBest.get(i).object());
				}
				System.out.println();
			}

		}

		
		qp.print();
	}	
	
	public static void printSent(Sentence s) {
		System.out.println("S: " + s.sent);
		System.out.println(s.tags);
		System.out.println();
		System.out.println(s.kBest.get(s.detectedKBest).object().skipRoot());

		System.out.println();
	}
	
	public static void printSent2(Sentence s) {
		System.out.println("Sentence: " + s.sent);
		if (s.sent.equals("Click run.")) {
			System.out.println(s.kBest.get(1).object().skipRoot());
		}
		else {
			System.out.println(s.kBest.get(0).object().skipRoot());
		}
		System.out.println();
	}
		


}
