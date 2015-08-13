


import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class Main {

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
		
		// Initialize the Output Writing Class
		OutputWriter.init();
		
		// 
//		questionParse(input, 6);
//		softCommandParse(input, 3);
//		hardCommandParse(input, 3);
		
		questionParse(input, 1);
		softCommandParse(input, 1);
		hardCommandParse(input, 1);
		//		doQuestionParse(lp, inputTextFile, 6);
	}
	
	// Analyzes the input for questions and see if any of them are malicious
	public static void questionParse(String filename, int numParses) {
		QParser qp = new QParser(filename,lp, numParses,0);
		TParser tp;
		OutputWriter.write();
		OutputWriter.write("---Question parse start--- " + numParses + " parses \n");

		for(Sentence s :qp.detectedList) {
			tp = new TParser(s);
			tp.SQParse();
			printSent2(s);
			PairChecker pairCheck = new PairChecker(s);
			pairCheck.blacklistCheck2();
		}
		
		for (Sentence s : qp.detectedList) {
			System.err.println(s.isMalicious());
		}
	}

	// Analyzes the input for Soft Commands and see if any of them are malicious
	public static void softCommandParse(String filename, int numParses) {
		OutputWriter.write();
		OutputWriter.write("---Soft Commands parse start--- " + numParses + " parses \n");

		QParser qp = new QParser(filename,lp, numParses,1);
		OutputWriter.write("Soft commands detected: " + qp.softList.size());
       
		for(Sentence s : qp.softList) {
			TParser tp = new TParser(s);
			tp.CommandParse();
			
			printSent2(s);
			PairChecker pairCheck = new PairChecker(s);
			pairCheck.blacklistCheck2();
			
//			for (VerbNounPair pair : s.softWords) {
//				OutputWriter.write("Verb: " + pair.verb);
//				OutputWriter.write("Noun: " + pair.nouns);
//			}

		}
	}
	
	// Analyzes the input for Hard Commands and see if any of them are malicious
	public static void hardCommandParse(String filename, int numParses) {
		OutputWriter.write();
		OutputWriter.write("---Direct Commands parse start--- " + numParses + " parses \n");
		OutputWriter.write();
		
		QParser qp = new QParser(filename,lp, numParses,2);
		OutputWriter.write("Direct commands detected: " + qp.hardList.size());
		
		for(Sentence s : qp.hardList) {
			TParser tp = new TParser(s);
			tp.CommandParse();
			
			printSent2(s);
			PairChecker pairCheck = new PairChecker(s);
			pairCheck.blacklistCheck2();
			

		}

		
	}
	public static void doDetailedQuestionParse(LexicalizedParser lp, String filename, int numParses) {
		OutputWriter.write();
		OutputWriter.write("---cleanParse method start---" + numParses + " parses");
		OutputWriter.write();
		
		QParser qp = new QParser(filename,lp, numParses,0);
		OutputWriter.write("CORRECTLY DETECTED:");
		for(Sentence s : qp.correctList) {
			printSent(s);
		}
		OutputWriter.write();
		OutputWriter.write("FALSE NEGATIVES:");
		for(Sentence s : qp.fnList) {
			printSent(s);
		}
		OutputWriter.write();
		OutputWriter.write("FALSE POSITIVES:");
		for(Sentence s : qp.fpList) {
			printSent(s);
		}
		qp.print();
	}
	
	
	public static void deepParse(LexicalizedParser lp, String filename, int numParses, int parseType) {
		QParser qp = new QParser(filename,lp, numParses,parseType);
		TParser tp;
		OutputWriter.write();
		OutputWriter.write("---deepParse method start---" + " " + filename);
		OutputWriter.write();
		OutputWriter.write("CORRECTLY DETECTED:");
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
		OutputWriter.write();
		OutputWriter.write("---allParse method start---");
		OutputWriter.write();
		
		QParser qp = new QParser(filename,lp, numParses,0);
		OutputWriter.write("ALL SENTENCES:");
		for(Sentence s : qp.allList) {
			OutputWriter.write(s.sent);
			OutputWriter.write(s.kBest.get(0).object());
			OutputWriter.write();
		}

		
		qp.print();
	}
	public static void allParse(String filename, int numParses) {
		// Parse, Detect and Analyze every question and command
		
		OutputWriter.write();
		OutputWriter.write("---Direct Commands parse start--- " + numParses + " parses");
		OutputWriter.write();

		QParser qp = new QParser(filename,lp, numParses,3);

		OutputWriter.write("ALL Questions and Commands: " + qp.detectedList.size() + " detected " + numParses + " Parses");
		
			for(Sentence s : qp.detectedList) { 
				TParser tp = new TParser(s);
				tp.CommandParse();
			}

		
       	for(Sentence s : qp.detectedList) {
       		
			PairChecker pairCheck = new PairChecker(s);
			pairCheck.blacklistCheck();

			//OutputWriter.write("-----");
		}
	}
	
	public static void getParse(String filename, int numParses) {
		OutputWriter.write();
		OutputWriter.write("---getParse method start---");
		OutputWriter.write();
		
		QParser qp = new QParser(filename,lp, numParses,0);
		OutputWriter.write("ALL SENTENCES:");
		for(Sentence s : qp.allList) {
			for (int i=0; i<numParses; i++) {
				OutputWriter.write(s.kBest.get(i).object());
			}
			OutputWriter.write(s.sent);
			if(s.sent.equals("And up in the top line, the address, type in FTP://update-google.com.")) {
				OutputWriter.write(s.sent);
				for (int i=0; i<numParses; i++) {
					OutputWriter.write(s.kBest.get(i).object());
				}
				OutputWriter.write();
			}
			if(s.sent.equals("And up in the top line, the address, type_VB in \"ftp://update-google.com.\"")) {
				OutputWriter.write(s.sent);
				for (int i=0; i<numParses; i++) {
					OutputWriter.write(s.kBest.get(i).object());
				}
				OutputWriter.write();
			}

		}
		qp.print();
	}	
	
	public static void printSent(Sentence s) {
		OutputWriter.write("S: " + s.sent);
		OutputWriter.write(s.tags);
		OutputWriter.write();
		OutputWriter.write(s.kBest.get(s.detectedKBest).object().skipRoot());

		OutputWriter.write();
	}
	
	public static void printSent2(Sentence s) {
		OutputWriter.write("\nSentence: " + s.sent);
		if (s.sent.equals("Click run.")) {
			OutputWriter.write(s.kBest.get(1).object().skipRoot());
		}
		else {
			OutputWriter.write(s.kBest.get(0).object().skipRoot());
		}
		OutputWriter.write();
	}
		


}
